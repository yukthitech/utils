/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package freemarker.ext.beans;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import freemarker.core.BugException;
import freemarker.core._JavaVersions;
import freemarker.ext.beans.BeansWrapper.MethodAppearanceDecision;
import freemarker.ext.beans.BeansWrapper.MethodAppearanceDecisionInput;
import freemarker.ext.util.ModelCache;
import freemarker.log.Logger;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.SecurityUtilities;

/**
 * Returns information about a {@link Class} that's useful for FreeMarker. Encapsulates a cache for this. Thread-safe,
 * doesn't even require "proper publishing" starting from 2.3.24 or Java 5. Immutable, with the exception of the
 * internal caches.
 * 
 * <p>
 * Note that instances of this are cached on the level of FreeMarker's defining class loader. Hence, it must not do
 * operations that depend on the Thread Context Class Loader, such as resolving class names.
 */
class ClassIntrospector {

    // Attention: This class must be thread-safe (not just after proper publishing). This is important as some of
    // these are shared by many object wrappers, and concurrency related glitches due to user errors must remain
    // local to the object wrappers, not corrupting the shared ClassIntrospector.

    private static final Logger LOG = Logger.getLogger("freemarker.beans");

    private static final String JREBEL_SDK_CLASS_NAME = "org.zeroturnaround.javarebel.ClassEventListener";
    private static final String JREBEL_INTEGRATION_ERROR_MSG
            = "Error initializing JRebel integration. JRebel integration disabled.";

    /**
     * When this property is true, some things are stricter. This is mostly to catch suspicious things in development
     * that can otherwise be valid situations.
     */
    static final boolean DEVELOPMENT_MODE = "true".equals(SecurityUtilities.getSystemProperty("freemarker.development",
            "false"));

    private static final ClassChangeNotifier CLASS_CHANGE_NOTIFIER;
    static {
        boolean jRebelAvailable;
        try {
            Class.forName(JREBEL_SDK_CLASS_NAME);
            jRebelAvailable = true;
        } catch (Throwable e) {
            jRebelAvailable = false;
            try {
                if (!(e instanceof ClassNotFoundException)) {
                    LOG.error(JREBEL_INTEGRATION_ERROR_MSG, e);
                }
            } catch (Throwable loggingE) {
                // ignore
            }
        }

        ClassChangeNotifier classChangeNotifier;
        if (jRebelAvailable) {
            try {
                classChangeNotifier = (ClassChangeNotifier)
                        Class.forName("freemarker.ext.beans.JRebelClassChangeNotifier").newInstance();
            } catch (Throwable e) {
                classChangeNotifier = null;
                try {
                    LOG.error(JREBEL_INTEGRATION_ERROR_MSG, e);
                } catch (Throwable loggingE) {
                    // ignore
                }
            }
        } else {
            classChangeNotifier = null;
        }

        CLASS_CHANGE_NOTIFIER = classChangeNotifier;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Introspection info Map keys:

    /** Key in the class info Map to the Map that maps method to argument type arrays */
    private static final Object ARG_TYPES_BY_METHOD_KEY = new Object();
    /** Key in the class info Map to the object that represents the constructors (one or multiple due to overloading) */
    static final Object CONSTRUCTORS_KEY = new Object();
    /** Key in the class info Map to the get(String|Object) Method */
    static final Object GENERIC_GET_KEY = new Object();

    // -----------------------------------------------------------------------------------------------------------------
    // Introspection configuration properties:

    // Note: These all must be *declared* final (or else synchronization is needed everywhere where they are accessed).

    final int exposureLevel;
    final boolean exposeFields;
    final MethodAppearanceFineTuner methodAppearanceFineTuner;
    final MethodSorter methodSorter;
    final boolean treatDefaultMethodsAsBeanMembers;
    final boolean bugfixed;

    /** See {@link #getHasSharedInstanceRestrictons()} */
    final private boolean hasSharedInstanceRestrictons;

    /** See {@link #isShared()} */
    final private boolean shared;

    // -----------------------------------------------------------------------------------------------------------------
    // State fields:

    private final Object sharedLock;
    private final Map<Class<?>, Map<Object, Object>> cache
            = new ConcurrentHashMap<Class<?>, Map<Object, Object>>(0, 0.75f, 16);
    private final Set<String> cacheClassNames = new HashSet<String>(0);
    private final Set<Class<?>> classIntrospectionsInProgress = new HashSet<Class<?>>(0);

    private final List<WeakReference<Object/*ClassBasedModelFactory|ModelCache>*/>> modelFactories
            = new LinkedList<WeakReference<Object>>();
    private final ReferenceQueue<Object> modelFactoriesRefQueue = new ReferenceQueue<Object>();

    private int clearingCounter;

    // -----------------------------------------------------------------------------------------------------------------
    // Instantiation:

    /**
     * Creates a new instance, that is hence surely not shared (singleton) instance.
     * 
     * @param pa
     *            Stores what the values of the JavaBean properties of the returned instance will be. Not {@code null}.
     */
    ClassIntrospector(ClassIntrospectorBuilder pa, Object sharedLock) {
        this(pa, sharedLock, false, false);
    }

    /**
     * @param hasSharedInstanceRestrictons
     *            {@code true} exactly if we are creating a new instance with {@link ClassIntrospectorBuilder}. Then
     *            it's {@code true} even if it won't put the instance into the cache.
     */
    ClassIntrospector(ClassIntrospectorBuilder builder, Object sharedLock,
            boolean hasSharedInstanceRestrictons, boolean shared) {
        NullArgumentException.check("sharedLock", sharedLock);

        this.exposureLevel = builder.getExposureLevel();
        this.exposeFields = builder.getExposeFields();
        this.methodAppearanceFineTuner = builder.getMethodAppearanceFineTuner();
        this.methodSorter = builder.getMethodSorter();
        this.treatDefaultMethodsAsBeanMembers = builder.getTreatDefaultMethodsAsBeanMembers();
        this.bugfixed = builder.isBugfixed();

        this.sharedLock = sharedLock;

        this.hasSharedInstanceRestrictons = hasSharedInstanceRestrictons;
        this.shared = shared;

        if (CLASS_CHANGE_NOTIFIER != null) {
            CLASS_CHANGE_NOTIFIER.subscribe(this);
        }
    }

    /**
     * Returns a {@link ClassIntrospectorBuilder}-s that could be used to create an identical {@link #ClassIntrospector}
     * . The returned {@link ClassIntrospectorBuilder} can be modified without interfering with anything.
     */
    ClassIntrospectorBuilder createBuilder() {
        return new ClassIntrospectorBuilder(this);
    }

    // ------------------------------------------------------------------------------------------------------------------
    // Introspection:

    /**
     * Gets the class introspection data from {@link #cache}, automatically creating the cache entry if it's missing.
     * 
     * @return A {@link Map} where each key is a property/method/field name (or a special {@link Object} key like
     *         {@link #CONSTRUCTORS_KEY}), each value is a {@link FastPropertyDescriptor} or {@link Method} or
     *         {@link OverloadedMethods} or {@link Field} (but better check the source code...).
     */
    Map<Object, Object> get(Class<?> clazz) {
        {
            Map<Object, Object> introspData = cache.get(clazz);
            if (introspData != null) return introspData;
        }

        String className;
        synchronized (sharedLock) {
            Map<Object, Object> introspData = cache.get(clazz);
            if (introspData != null) return introspData;

            className = clazz.getName();
            if (cacheClassNames.contains(className)) {
                onSameNameClassesDetected(className);
            }

            while (introspData == null && classIntrospectionsInProgress.contains(clazz)) {
                // Another thread is already introspecting this class;
                // waiting for its result.
                try {
                    sharedLock.wait();
                    introspData = cache.get(clazz);
                } catch (InterruptedException e) {
                    throw new RuntimeException(
                            "Class inrospection data lookup aborded: " + e);
                }
            }
            if (introspData != null) return introspData;

            // This will be the thread that introspects this class.
            classIntrospectionsInProgress.add(clazz);
        }
        try {
            Map<Object, Object> introspData = createClassIntrospectionData(clazz);
            synchronized (sharedLock) {
                cache.put(clazz, introspData);
                cacheClassNames.add(className);
            }
            return introspData;
        } finally {
            synchronized (sharedLock) {
                classIntrospectionsInProgress.remove(clazz);
                sharedLock.notifyAll();
            }
        }
    }

    /**
     * Creates a {@link Map} with the content as described for the return value of {@link #get(Class)}.
     */
    private Map<Object, Object> createClassIntrospectionData(Class<?> clazz) {
        final Map<Object, Object> introspData = new HashMap<Object, Object>();

        if (exposeFields) {
            addFieldsToClassIntrospectionData(introspData, clazz);
        }

        final Map<MethodSignature, List<Method>> accessibleMethods = discoverAccessibleMethods(clazz);

        addGenericGetToClassIntrospectionData(introspData, accessibleMethods);

        if (exposureLevel != BeansWrapper.EXPOSE_NOTHING) {
            try {
                addBeanInfoToClassIntrospectionData(introspData, clazz, accessibleMethods);
            } catch (IntrospectionException e) {
                LOG.warn("Couldn't properly perform introspection for class " + clazz, e);
                introspData.clear(); // FIXME NBC: Don't drop everything here.
            }
        }

        addConstructorsToClassIntrospectionData(introspData, clazz);

        if (introspData.size() > 1) {
            return introspData;
        } else if (introspData.size() == 0) {
            return Collections.emptyMap();
        } else { // map.size() == 1
            Entry<Object, Object> e = introspData.entrySet().iterator().next();
            return Collections.singletonMap(e.getKey(), e.getValue());
        }
    }

    private void addFieldsToClassIntrospectionData(Map<Object, Object> introspData, Class<?> clazz)
            throws SecurityException {
        Field[] fields = clazz.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if ((field.getModifiers() & Modifier.STATIC) == 0) {
                introspData.put(field.getName(), field);
            }
        }
    }

    private void addBeanInfoToClassIntrospectionData(
            Map<Object, Object> introspData, Class<?> clazz, Map<MethodSignature, List<Method>> accessibleMethods)
            throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        List<PropertyDescriptor> pdas = getPropertyDescriptors(beanInfo, clazz);
        int pdasLength = pdas.size();
        // Reverse order shouldn't mater, but we keep it to not risk backward incompatibility.
        for (int i = pdasLength - 1; i >= 0; --i) {
            addPropertyDescriptorToClassIntrospectionData(
                    introspData, pdas.get(i), clazz,
                    accessibleMethods);
        }

        if (exposureLevel < BeansWrapper.EXPOSE_PROPERTIES_ONLY) {
            final MethodAppearanceDecision decision = new MethodAppearanceDecision();
            MethodAppearanceDecisionInput decisionInput = null;
            List<MethodDescriptor> mds = getMethodDescriptors(beanInfo, clazz);
            sortMethodDescriptors(mds);
            int mdsSize = mds.size();
            IdentityHashMap<Method, Void> argTypesUsedByIndexerPropReaders = null;
            for (int i = mdsSize - 1; i >= 0; --i) {
                final Method method = getMatchingAccessibleMethod(mds.get(i).getMethod(), accessibleMethods);
                if (method != null && isAllowedToExpose(method)) {
                    decision.setDefaults(method);
                    if (methodAppearanceFineTuner != null) {
                        if (decisionInput == null) {
                            decisionInput = new MethodAppearanceDecisionInput();
                        }
                        decisionInput.setContainingClass(clazz);
                        decisionInput.setMethod(method);

                        methodAppearanceFineTuner.process(decisionInput, decision);
                    }

                    PropertyDescriptor propDesc = decision.getExposeAsProperty();
                    if (propDesc != null &&
                            (decision.getReplaceExistingProperty()
                                    || !(introspData.get(propDesc.getName()) instanceof FastPropertyDescriptor))) {
                        addPropertyDescriptorToClassIntrospectionData(
                                introspData, propDesc, clazz, accessibleMethods);
                    }

                    String methodKey = decision.getExposeMethodAs();
                    if (methodKey != null) {
                        Object previous = introspData.get(methodKey);
                        if (previous instanceof Method) {
                            // Overloaded method - replace Method with a OverloadedMethods
                            OverloadedMethods overloadedMethods = new OverloadedMethods(bugfixed);
                            overloadedMethods.addMethod((Method) previous);
                            overloadedMethods.addMethod(method);
                            introspData.put(methodKey, overloadedMethods);
                            // Remove parameter type information (unless an indexed property reader needs it):
                            if (argTypesUsedByIndexerPropReaders == null
                                    || !argTypesUsedByIndexerPropReaders.containsKey(previous)) {
                                getArgTypesByMethod(introspData).remove(previous);
                            }
                        } else if (previous instanceof OverloadedMethods) {
                            // Already overloaded method - add new overload
                            ((OverloadedMethods) previous).addMethod(method);
                        } else if (decision.getMethodShadowsProperty()
                                || !(previous instanceof FastPropertyDescriptor)) {
                            // Simple method (this far)
                            introspData.put(methodKey, method);
                            Class<?>[] replaced = getArgTypesByMethod(introspData).put(method,
                                    method.getParameterTypes());
                            if (replaced != null) {
                                if (argTypesUsedByIndexerPropReaders == null) {
                                    argTypesUsedByIndexerPropReaders = new IdentityHashMap<Method, Void>();
                                }
                                argTypesUsedByIndexerPropReaders.put(method, null);                                
                            }
                        }
                    }
                }
            } // for each in mds
        } // end if (exposureLevel < EXPOSE_PROPERTIES_ONLY)
    }

    /**
     * Very similar to {@link BeanInfo#getPropertyDescriptors()}, but can deal with Java 8 default methods too.
     */
    private List<PropertyDescriptor> getPropertyDescriptors(BeanInfo beanInfo, Class<?> clazz) {
        PropertyDescriptor[] introspectorPDsArray = beanInfo.getPropertyDescriptors();
        List<PropertyDescriptor> introspectorPDs = introspectorPDsArray != null ? Arrays.asList(introspectorPDsArray)
                : Collections.<PropertyDescriptor>emptyList();
        
        if (!treatDefaultMethodsAsBeanMembers || _JavaVersions.JAVA_8 == null) {
            // java.beans.Introspector was good enough then.
            return introspectorPDs;
        }
        
        // introspectorPDs contains each property exactly once. But as now we will search them manually too, it can
        // happen that we find the same property for multiple times. Worse, because of indexed properties, it's possible
        // that we have to merge entries (like one has the normal reader method, the other has the indexed reader
        // method), instead of just replacing them in a Map. That's why we have introduced PropertyReaderMethodPair,
        // which holds the methods belonging to the same property name. IndexedPropertyDescriptor is not good for that,
        // as it can't store two methods whose types are incompatible, and we have to wait until all the merging was
        // done to see if the incompatibility goes away.
        
        // This could be Map<String, PropertyReaderMethodPair>, but since we rarely need to do merging, we try to avoid
        // creating those and use the source objects as much as possible. Also note that we initialize this lazily.
        LinkedHashMap<String, Object /*PropertyReaderMethodPair|Method|PropertyDescriptor*/> mergedPRMPs = null;

        // Collect Java 8 default methods that look like property readers into mergedPRMPs: 
        // (Note that java.beans.Introspector discovers non-accessible public methods, and to emulate that behavior
        // here, we don't utilize the accessibleMethods Map, which we might already have at this point.)
        for (Method method : clazz.getMethods()) {
            if (_JavaVersions.JAVA_8.isDefaultMethod(method) && method.getReturnType() != void.class
                    && !method.isBridge()) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 0
                        || paramTypes.length == 1 && paramTypes[0] == int.class /* indexed property reader */) {
                    String propName = _MethodUtil.getBeanPropertyNameFromReaderMethodName(
                            method.getName(), method.getReturnType());
                    if (propName != null) {
                        if (mergedPRMPs == null) {
                            // Lazy initialization
                            mergedPRMPs = new LinkedHashMap<String, Object>();
                        }
                        if (paramTypes.length == 0) {
                            mergeInPropertyReaderMethod(mergedPRMPs, propName, method);
                        } else { // It's an indexed property reader method
                            mergeInPropertyReaderMethodPair(mergedPRMPs, propName,
                                    new PropertyReaderMethodPair(null, method));
                        }
                    }
                }
            }
        } // for clazz.getMethods()
        
        if (mergedPRMPs == null) {
            // We had no interfering Java 8 default methods, so we can chose the fast route.
            return introspectorPDs;
        }
        
        for (PropertyDescriptor introspectorPD : introspectorPDs) {
            mergeInPropertyDescriptor(mergedPRMPs, introspectorPD);
        }
        
        // Now we convert the PRMPs to PDs, handling case where the normal and the indexed read methods contradict.
        List<PropertyDescriptor> mergedPDs = new ArrayList<PropertyDescriptor>(mergedPRMPs.size());
        for (Entry<String, Object> entry : mergedPRMPs.entrySet()) {
            String propName = entry.getKey();
            Object propDescObj = entry.getValue();
            if (propDescObj instanceof PropertyDescriptor) {
                mergedPDs.add((PropertyDescriptor) propDescObj);
            } else {
                Method readMethod;
                Method indexedReadMethod;
                if (propDescObj instanceof Method) {
                    readMethod = (Method) propDescObj;
                    indexedReadMethod = null;
                } else if (propDescObj instanceof PropertyReaderMethodPair) {
                    PropertyReaderMethodPair prmp = (PropertyReaderMethodPair) propDescObj;
                    readMethod = prmp.readMethod;
                    indexedReadMethod = prmp.indexedReadMethod;
                    if (readMethod != null && indexedReadMethod != null
                            && indexedReadMethod.getReturnType() != readMethod.getReturnType().getComponentType()) {
                        // Here we copy the java.beans.Introspector behavior: If the array item class is not exactly the
                        // the same as the indexed read method return type, we say that the property is not indexed.
                        indexedReadMethod = null;
                    }
                } else {
                    throw new BugException();
                }
                try {
                    mergedPDs.add(
                            indexedReadMethod != null
                                    ? new IndexedPropertyDescriptor(propName,
                                            readMethod, null, indexedReadMethod, null)
                                    : new PropertyDescriptor(propName, readMethod, null));
                } catch (IntrospectionException e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Failed creating property descriptor for " + clazz.getName() + " property " + propName,
                                e);
                    }
                }
            }
        }
        return mergedPDs;
    }

    private static class PropertyReaderMethodPair {
        private final Method readMethod;
        private final Method indexedReadMethod;
        
        PropertyReaderMethodPair(Method readerMethod, Method indexedReaderMethod) {
            this.readMethod = readerMethod;
            this.indexedReadMethod = indexedReaderMethod;
        }
        
        PropertyReaderMethodPair(PropertyDescriptor pd) {
            this(
                    pd.getReadMethod(),
                    pd instanceof IndexedPropertyDescriptor
                            ? ((IndexedPropertyDescriptor) pd).getIndexedReadMethod() : null);
        }
    
        static PropertyReaderMethodPair from(Object obj) {
            if (obj instanceof PropertyReaderMethodPair) {
                return (PropertyReaderMethodPair) obj;
            } else if (obj instanceof PropertyDescriptor) {
                return new PropertyReaderMethodPair((PropertyDescriptor) obj);
            } else if (obj instanceof Method) {
                return new PropertyReaderMethodPair((Method) obj, null);
            } else {
                throw new BugException("Unexpected obj type: " + obj.getClass().getName());
            }
        }
        
        static PropertyReaderMethodPair merge(PropertyReaderMethodPair oldMethods, PropertyReaderMethodPair newMethods) {
            return new PropertyReaderMethodPair(
                    newMethods.readMethod != null ? newMethods.readMethod : oldMethods.readMethod,
                    newMethods.indexedReadMethod != null ? newMethods.indexedReadMethod
                            : oldMethods.indexedReadMethod);
        }
    
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((indexedReadMethod == null) ? 0 : indexedReadMethod.hashCode());
            result = prime * result + ((readMethod == null) ? 0 : readMethod.hashCode());
            return result;
        }
    
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PropertyReaderMethodPair other = (PropertyReaderMethodPair) obj;
            return other.readMethod == readMethod && other.indexedReadMethod == indexedReadMethod;
        }
        
    }

    private void mergeInPropertyDescriptor(LinkedHashMap<String, Object> mergedPRMPs, PropertyDescriptor pd) {
        String propName = pd.getName();
        Object replaced = mergedPRMPs.put(propName, pd);
        if (replaced != null) {
            PropertyReaderMethodPair newPRMP = new PropertyReaderMethodPair(pd);
            putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRMP);
        }
    }

    private void mergeInPropertyReaderMethodPair(LinkedHashMap<String, Object> mergedPRMPs,
            String propName, PropertyReaderMethodPair newPRM) {
        Object replaced = mergedPRMPs.put(propName, newPRM);
        if (replaced != null) {
            putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRM);
        }
    }
    
    private void mergeInPropertyReaderMethod(LinkedHashMap<String, Object> mergedPRMPs,
            String propName, Method readerMethod) {
        Object replaced = mergedPRMPs.put(propName, readerMethod);
        if (replaced != null) {
            putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName,
                    replaced, new PropertyReaderMethodPair(readerMethod, null));
        }
    }

    private void putIfMergedPropertyReaderMethodPairDiffers(LinkedHashMap<String, Object> mergedPRMPs,
            String propName, Object replaced, PropertyReaderMethodPair newPRMP) {
        PropertyReaderMethodPair replacedPRMP = PropertyReaderMethodPair.from(replaced);
        PropertyReaderMethodPair mergedPRMP = PropertyReaderMethodPair.merge(replacedPRMP, newPRMP);
        if (!mergedPRMP.equals(newPRMP)) {
            mergedPRMPs.put(propName, mergedPRMP);
        }
    }
    
    /**
     * Very similar to {@link BeanInfo#getMethodDescriptors()}, but can deal with Java 8 default methods too.
     */
    private List<MethodDescriptor> getMethodDescriptors(BeanInfo beanInfo, Class<?> clazz) {
        MethodDescriptor[] introspectorMDArray = beanInfo.getMethodDescriptors();
        List<MethodDescriptor> introspectionMDs = introspectorMDArray != null && introspectorMDArray.length != 0
                ? Arrays.asList(introspectorMDArray) : Collections.<MethodDescriptor>emptyList();

        if (!treatDefaultMethodsAsBeanMembers || _JavaVersions.JAVA_8 == null) {
            // java.beans.Introspector was good enough then.
            return introspectionMDs;
        }

        Map<String, List<Method>> defaultMethodsToAddByName = null;
        for (Method method : clazz.getMethods()) {
            if (_JavaVersions.JAVA_8.isDefaultMethod(method) && !method.isBridge()) {
                if (defaultMethodsToAddByName == null) {
                    defaultMethodsToAddByName = new HashMap<String, List<Method>>();
                }
                List<Method> overloads = defaultMethodsToAddByName.get(method.getName());
                if (overloads == null) {
                    overloads = new ArrayList<Method>(0);
                    defaultMethodsToAddByName.put(method.getName(), overloads);
                }
                overloads.add(method);
            }
        }
        
        if (defaultMethodsToAddByName == null) {
            // We had no interfering default methods:
            return introspectionMDs;
        }

        // Recreate introspectionMDs so that its size can grow: 
        ArrayList<MethodDescriptor> newIntrospectionMDs
                = new ArrayList<MethodDescriptor>(introspectionMDs.size() + 16);
        for (MethodDescriptor introspectorMD : introspectionMDs) {
            Method introspectorM = introspectorMD.getMethod();
            // Prevent cases where the same method is added with different return types both from the list of default
            // methods and from the list of Introspector-discovered methods, as that would lead to overloaded method
            // selection ambiguity later. This is known to happen when the default method in an interface has reified
            // return type, and then the interface is implemented by a class where the compiler generates an override
            // for the bridge method only. (Other tricky cases might exist.)
            if (!containsMethodWithSameParameterTypes(
                    defaultMethodsToAddByName.get(introspectorM.getName()), introspectorM)) {
                newIntrospectionMDs.add(introspectorMD);
            }
        }
        introspectionMDs = newIntrospectionMDs;
        
        // Add default methods:
        for (Entry<String, List<Method>> entry : defaultMethodsToAddByName.entrySet()) {
            for (Method method : entry.getValue()) {
                introspectionMDs.add(new MethodDescriptor(method));
            }
        }
        
        return introspectionMDs;
    }

    private boolean containsMethodWithSameParameterTypes(List<Method> overloads, Method m) {
        if (overloads == null) {
            return false;
        }
        
        Class<?>[] paramTypes = m.getParameterTypes();
        for (Method overload : overloads) {
            if (Arrays.equals(overload.getParameterTypes(), paramTypes)) {
                return true;
            }
        }
        return false;
    }

    private void addPropertyDescriptorToClassIntrospectionData(Map<Object, Object> introspData,
            PropertyDescriptor pd, Class<?> clazz, Map<MethodSignature, List<Method>> accessibleMethods) {
        Method readMethod = getMatchingAccessibleMethod(pd.getReadMethod(), accessibleMethods);
        if (readMethod != null && !isAllowedToExpose(readMethod)) {
            readMethod = null;
        }
        
        Method indexedReadMethod;
        if (pd instanceof IndexedPropertyDescriptor) {
            indexedReadMethod = getMatchingAccessibleMethod(
                    ((IndexedPropertyDescriptor) pd).getIndexedReadMethod(), accessibleMethods);
            if (indexedReadMethod != null && !isAllowedToExpose(indexedReadMethod)) {
                indexedReadMethod = null;
            }
            if (indexedReadMethod != null) {
                getArgTypesByMethod(introspData).put(
                        indexedReadMethod, indexedReadMethod.getParameterTypes());
            }
        } else {
            indexedReadMethod = null;
        }
        
        if (readMethod != null || indexedReadMethod != null) {
            introspData.put(pd.getName(), new FastPropertyDescriptor(readMethod, indexedReadMethod));
        }
    }

    private void addGenericGetToClassIntrospectionData(Map<Object, Object> introspData,
            Map<MethodSignature, List<Method>> accessibleMethods) {
        Method genericGet = getFirstAccessibleMethod(
                MethodSignature.GET_STRING_SIGNATURE, accessibleMethods);
        if (genericGet == null) {
            genericGet = getFirstAccessibleMethod(
                    MethodSignature.GET_OBJECT_SIGNATURE, accessibleMethods);
        }
        if (genericGet != null) {
            introspData.put(GENERIC_GET_KEY, genericGet);
        }
    }

    private void addConstructorsToClassIntrospectionData(final Map<Object, Object> introspData,
            Class<?> clazz) {
        try {
            Constructor<?>[] ctors = clazz.getConstructors();
            if (ctors.length == 1) {
                Constructor<?> ctor = ctors[0];
                introspData.put(CONSTRUCTORS_KEY, new SimpleMethod(ctor, ctor.getParameterTypes()));
            } else if (ctors.length > 1) {
                OverloadedMethods overloadedCtors = new OverloadedMethods(bugfixed);
                for (int i = 0; i < ctors.length; i++) {
                    overloadedCtors.addConstructor(ctors[i]);
                }
                introspData.put(CONSTRUCTORS_KEY, overloadedCtors);
            }
        } catch (SecurityException e) {
            LOG.warn("Can't discover constructors for class " + clazz.getName(), e);
        }
    }

    /**
     * Retrieves mapping of {@link MethodSignature}-s to a {@link List} of accessible methods for a class. In case the
     * class is not public, retrieves methods with same signature as its public methods from public superclasses and
     * interfaces. Basically upcasts every method to the nearest accessible method.
     */
    private static Map<MethodSignature, List<Method>> discoverAccessibleMethods(Class<?> clazz) {
        Map<MethodSignature, List<Method>> accessibles = new HashMap<MethodSignature, List<Method>>();
        discoverAccessibleMethods(clazz, accessibles);
        return accessibles;
    }

    private static void discoverAccessibleMethods(Class<?> clazz, Map<MethodSignature, List<Method>> accessibles) {
        if (Modifier.isPublic(clazz.getModifiers())) {
            try {
                Method[] methods = clazz.getMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    MethodSignature sig = new MethodSignature(method);
                    // Contrary to intuition, a class can actually have several
                    // different methods with same signature *but* different
                    // return types. These can't be constructed using Java the
                    // language, as this is illegal on source code level, but
                    // the compiler can emit synthetic methods as part of
                    // generic type reification that will have same signature
                    // yet different return type than an existing explicitly
                    // declared method. Consider:
                    // public interface I<T> { T m(); }
                    // public class C implements I<Integer> { Integer m() { return 42; } }
                    // C.class will have both "Object m()" and "Integer m()" methods.
                    List<Method> methodList = accessibles.get(sig);
                    if (methodList == null) {
                     // TODO Collection.singletonList is more efficient, though read only.
                        methodList = new LinkedList<Method>();
                        accessibles.put(sig, methodList);
                    }
                    methodList.add(method);
                }
                return;
            } catch (SecurityException e) {
                LOG.warn("Could not discover accessible methods of class " +
                        clazz.getName() +
                        ", attemping superclasses/interfaces.", e);
                // Fall through and attempt to discover superclass/interface methods
            }
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            discoverAccessibleMethods(interfaces[i], accessibles);
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            discoverAccessibleMethods(superclass, accessibles);
        }
    }

    private static Method getMatchingAccessibleMethod(Method m, Map<MethodSignature, List<Method>> accessibles) {
        if (m == null) {
            return null;
        }
        MethodSignature sig = new MethodSignature(m);
        List<Method> ams = accessibles.get(sig);
        if (ams == null) {
            return null;
        }
        for (Method am : ams) {
            if (am.getReturnType() == m.getReturnType()) {
                return am;
            }
        }
        return null;
    }

    private static Method getFirstAccessibleMethod(MethodSignature sig, Map<MethodSignature, List<Method>> accessibles) {
        List<Method> ams = accessibles.get(sig);
        if (ams == null || ams.isEmpty()) {
            return null;
        }
        return ams.get(0);
    }

    /**
     * As of this writing, this is only used for testing if method order really doesn't mater.
     */
    private void sortMethodDescriptors(List<MethodDescriptor> methodDescriptors) {
        if (methodSorter != null) {
            methodSorter.sortMethodDescriptors(methodDescriptors);
        }
    }

    boolean isAllowedToExpose(Method method) {
        return exposureLevel < BeansWrapper.EXPOSE_SAFE || !UnsafeMethods.isUnsafeMethod(method);
    }

    private static Map<Method, Class<?>[]> getArgTypesByMethod(Map<Object, Object> classInfo) {
        @SuppressWarnings("unchecked")
        Map<Method, Class<?>[]> argTypes = (Map<Method, Class<?>[]>) classInfo.get(ARG_TYPES_BY_METHOD_KEY);
        if (argTypes == null) {
            argTypes = new HashMap<Method, Class<?>[]>();
            classInfo.put(ARG_TYPES_BY_METHOD_KEY, argTypes);
        }
        return argTypes;
    }

    private static final class MethodSignature {
        private static final MethodSignature GET_STRING_SIGNATURE =
                new MethodSignature("get", new Class[] { String.class });
        private static final MethodSignature GET_OBJECT_SIGNATURE =
                new MethodSignature("get", new Class[] { Object.class });

        private final String name;
        private final Class<?>[] args;

        private MethodSignature(String name, Class<?>[] args) {
            this.name = name;
            this.args = args;
        }

        MethodSignature(Method method) {
            this(method.getName(), method.getParameterTypes());
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MethodSignature) {
                MethodSignature ms = (MethodSignature) o;
                return ms.name.equals(name) && Arrays.equals(args, ms.args);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode() ^ args.length; // TODO That's a poor quality hash... isn't this a problem?
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Cache management:

    /**
     * Corresponds to {@link BeansWrapper#clearClassIntrospecitonCache()}.
     * 
     * @since 2.3.20
     */
    void clearCache() {
        if (getHasSharedInstanceRestrictons()) {
            throw new IllegalStateException(
                    "It's not allowed to clear the whole cache in a read-only " + this.getClass().getName() +
                            "instance. Use removeFromClassIntrospectionCache(String prefix) instead.");
        }
        forcedClearCache();
    }

    private void forcedClearCache() {
        synchronized (sharedLock) {
            cache.clear();
            cacheClassNames.clear();
            clearingCounter++;

            for (WeakReference<Object> regedMfREf : modelFactories) {
                Object regedMf = regedMfREf.get();
                if (regedMf != null) {
                    if (regedMf instanceof ClassBasedModelFactory) {
                        ((ClassBasedModelFactory) regedMf).clearCache();
                    } else if (regedMf instanceof ModelCache) {
                        ((ModelCache) regedMf).clearCache();
                    } else {
                        throw new BugException();
                    }
                }
            }

            removeClearedModelFactoryReferences();
        }
    }

    /**
     * Corresponds to {@link BeansWrapper#removeFromClassIntrospectionCache(Class)}.
     * 
     * @since 2.3.20
     */
    void remove(Class<?> clazz) {
        synchronized (sharedLock) {
            cache.remove(clazz);
            cacheClassNames.remove(clazz.getName());
            clearingCounter++;

            for (WeakReference<Object> regedMfREf : modelFactories) {
                Object regedMf = regedMfREf.get();
                if (regedMf != null) {
                    if (regedMf instanceof ClassBasedModelFactory) {
                        ((ClassBasedModelFactory) regedMf).removeFromCache(clazz);
                    } else if (regedMf instanceof ModelCache) {
                        ((ModelCache) regedMf).clearCache(); // doesn't support selective clearing ATM
                    } else {
                        throw new BugException();
                    }
                }
            }

            removeClearedModelFactoryReferences();
        }
    }

    /**
     * Returns the number of events so far that could make class introspection data returned earlier outdated.
     */
    int getClearingCounter() {
        synchronized (sharedLock) {
            return clearingCounter;
        }
    }

    private void onSameNameClassesDetected(String className) {
        // TODO: This behavior should be pluggable, as in environments where
        // some classes are often reloaded or multiple versions of the
        // same class is normal (OSGi), this will drop the cache contents
        // too often.
        if (LOG.isInfoEnabled()) {
            LOG.info(
                    "Detected multiple classes with the same name, \"" + className +
                            "\". Assuming it was a class-reloading. Clearing class introspection " +
                            "caches to release old data.");
        }
        forcedClearCache();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Managing dependent objects:

    void registerModelFactory(ClassBasedModelFactory mf) {
        registerModelFactory((Object) mf);
    }

    void registerModelFactory(ModelCache mf) {
        registerModelFactory((Object) mf);
    }

    private void registerModelFactory(Object mf) {
        // Note that this `synchronized (sharedLock)` is also need for the BeansWrapper constructor to work safely.
        synchronized (sharedLock) {
            modelFactories.add(new WeakReference<Object>(mf, modelFactoriesRefQueue));
            removeClearedModelFactoryReferences();
        }
    }

    void unregisterModelFactory(ClassBasedModelFactory mf) {
        unregisterModelFactory((Object) mf);
    }

    void unregisterModelFactory(ModelCache mf) {
        unregisterModelFactory((Object) mf);
    }

    void unregisterModelFactory(Object mf) {
        synchronized (sharedLock) {
            for (Iterator<WeakReference<Object>> it = modelFactories.iterator(); it.hasNext(); ) {
                Object regedMf = it.next().get();
                if (regedMf == mf) {
                    it.remove();
                }
            }

        }
    }

    private void removeClearedModelFactoryReferences() {
        Reference<?> cleardRef;
        while ((cleardRef = modelFactoriesRefQueue.poll()) != null) {
            synchronized (sharedLock) {
                findClearedRef: for (Iterator<WeakReference<Object>> it = modelFactories.iterator(); it.hasNext(); ) {
                    if (it.next() == cleardRef) {
                        it.remove();
                        break findClearedRef;
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Extracting from introspection info:

    static Class<?>[] getArgTypes(Map<Object, Object> classInfo, Method method) {
        @SuppressWarnings("unchecked")
        Map<Method, Class<?>[]> argTypesByMethod = (Map<Method, Class<?>[]>) classInfo.get(ARG_TYPES_BY_METHOD_KEY);
        return argTypesByMethod.get(method);
    }

    /**
     * Returns the number of introspected methods/properties that should be available via the TemplateHashModel
     * interface.
     */
    int keyCount(Class<?> clazz) {
        Map<Object, Object> map = get(clazz);
        int count = map.size();
        if (map.containsKey(CONSTRUCTORS_KEY)) count--;
        if (map.containsKey(GENERIC_GET_KEY)) count--;
        if (map.containsKey(ARG_TYPES_BY_METHOD_KEY)) count--;
        return count;
    }

    /**
     * Returns the Set of names of introspected methods/properties that should be available via the TemplateHashModel
     * interface.
     */
    Set<Object> keySet(Class<?> clazz) {
        Set<Object> set = new HashSet<Object>(get(clazz).keySet());
        set.remove(CONSTRUCTORS_KEY);
        set.remove(GENERIC_GET_KEY);
        set.remove(ARG_TYPES_BY_METHOD_KEY);
        return set;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Properties

    int getExposureLevel() {
        return exposureLevel;
    }

    boolean getExposeFields() {
        return exposeFields;
    }
    
    boolean getTreatDefaultMethodsAsBeanMembers() {
        return treatDefaultMethodsAsBeanMembers;
    }

    MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
        return methodAppearanceFineTuner;
    }

    MethodSorter getMethodSorter() {
        return methodSorter;
    }

    /**
     * Returns {@code true} if this instance was created with {@link ClassIntrospectorBuilder}, even if it wasn't
     * actually put into the cache (as we reserve the right to do so in later versions).
     */
    boolean getHasSharedInstanceRestrictons() {
        return hasSharedInstanceRestrictons;
    }

    /**
     * Tells if this instance is (potentially) shared among {@link BeansWrapper} instances.
     * 
     * @see #getHasSharedInstanceRestrictons()
     */
    boolean isShared() {
        return shared;
    }

    /**
     * Almost always, you want to use {@link BeansWrapper#getSharedIntrospectionLock()}, not this! The only exception is
     * when you get this to set the field returned by {@link BeansWrapper#getSharedIntrospectionLock()}.
     */
    Object getSharedLock() {
        return sharedLock;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Monitoring:

    /** For unit testing only */
    Object[] getRegisteredModelFactoriesSnapshot() {
        synchronized (sharedLock) {
            return modelFactories.toArray();
        }
    }

}
