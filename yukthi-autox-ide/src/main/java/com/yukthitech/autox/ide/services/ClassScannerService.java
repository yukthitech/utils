package com.yukthitech.autox.ide.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.stereotype.Service;

/**
 * Service to scan classes.
 */
@Service
public class ClassScannerService
{
	/**
	 * Root packages to be scanned.
	 */
	private List<String> rootPackages = Arrays.asList("com.yukthitech");
	
	/**
	 * Packages prefixes to be excluded.
	 */
	private Set<String> excludedPackages = new HashSet<>();
	
	/**
	 * Internal variable. Reflections built from root packages.
	 */
	private List<Reflections> reflections;

	/**
	 * Gets the root packages to be scanned.
	 *
	 * @return the root packages to be scanned
	 */
	public List<String> getRootPackages()
	{
		return rootPackages;
	}

	/**
	 * Sets the root packages to be scanned.
	 *
	 * @param rootPackages the new root packages to be scanned
	 */
	public void setRootPackages(List<String> rootPackages)
	{
		this.rootPackages = rootPackages;
	}
	
	/**
	 * Gets the packages prefixes to be excluded.
	 *
	 * @return the packages prefixes to be excluded
	 */
	public Set<String> getExcludedPackages()
	{
		return excludedPackages;
	}

	/**
	 * Sets the packages prefixes to be excluded.
	 *
	 * @param excludedPackages the new packages prefixes to be excluded
	 */
	public void setExcludedPackages(Set<String> excludedPackages)
	{
		this.excludedPackages = excludedPackages;
	}

	/**
	 * Gets the classes with specified annotation.
	 *
	 * @param annotationType the annotation type
	 * @return the classes with annotation
	 */
	public Set<Class<?>> getClassesWithAnnotation(Class<? extends Annotation> annotationType)
	{
		Set<Class<?>> result = new HashSet<Class<?>>();
		Set<Class<?>> classes = null;
		
		for(Reflections reflection: reflections)
		{
			classes = reflection.getTypesAnnotatedWith(annotationType);
			
			if(classes == null)
			{
				continue;
			}
			
			for(Class<?> cls : classes)
			{
				if(isExcluded(cls))
				{
					continue;
				}

				result.add(cls);
			}
		}
		
		return result;
	}
	
	/**
	 * Gets the classes of specified type.
	 *
	 * @param type the type
	 * @return the classes of type
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<Class<?>> getClassesOfType(Class<?> type)
	{
		Set<Class<?>> result = new HashSet<>();
		Set<Class<?>> classes = null;
		
		for(Reflections reflection: reflections)
		{
			classes = (Set)reflection.getSubTypesOf(type);
			
			if(classes == null)
			{
				continue;
			}
			
			for(Class<?> cls : classes)
			{
				if(isExcluded(cls))
				{
					continue;
				}

				result.add(cls);
			}
		}
		
		return result;
	}
	
	public Set<Method> getMethodsWithAnnotation(Class<? extends Annotation> annotationType)
	{
		Set<Method> allMethods = new HashSet<>();
		
		for(Reflections reflections : reflections)
		{
			Set<Method> methods = reflections.getMethodsAnnotatedWith(annotationType);
			
			if(methods == null || methods.isEmpty())
			{
				continue;
			}
			
			allMethods.addAll(methods);
		}
		
		return allMethods;
	}
	
	private boolean isExcluded(Class<?> cls)
	{
		for(String excludedPkg : excludedPackages)
		{
			if(cls.getName().startsWith(excludedPkg))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Post loading.
	 */
	@PostConstruct
	public void init()
	{
		if(rootPackages == null || rootPackages.isEmpty())
		{
			throw new NullPointerException("No root-package is not specified");
		}
		
		reflections = new ArrayList<Reflections>(rootPackages.size());
		
		for(String pack: rootPackages)
		{
			reflections.add(new Reflections(pack, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner()));
		}
	}
}
