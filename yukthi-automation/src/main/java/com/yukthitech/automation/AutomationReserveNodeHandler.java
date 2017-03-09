package com.yukthitech.automation;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.yukthitech.automation.common.AutomationUtils;
import com.yukthitech.automation.config.ApplicationConfiguration;
import com.yukthitech.automation.config.IPlugin;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.reserved.IReserveNodeHandler;
import com.yukthitech.ccg.xml.reserved.NodeName;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Factory to create new steps and validations.
 * 
 * @author akiran
 */
@NodeName(namePattern = ".*")
public class AutomationReserveNodeHandler implements IReserveNodeHandler
{
	/**
	 * Mapping from step name to type.
	 */
	private Map<String, Class<? extends IStep>> nameToStepType = new HashMap<>();

	/**
	 * Mapping from name to validation type.
	 */
	private Map<String, Class<? extends IValidation>> nameToValidationType = new HashMap<>();
	
	/**
	 * Current application configuration.
	 */
	private ApplicationConfiguration applicationConfiguration;
	
	/**
	 * Current context.
	 */
	private AutomationContext context;
	
	/**
	 * Instantiates a new executable factory.
	 *
	 * @param appConfiguraion
	 *            the app configuraion
	 */
	public AutomationReserveNodeHandler(AutomationContext context, ApplicationConfiguration appConfiguraion)
	{
		this.context = context;
		this.applicationConfiguration = appConfiguraion;
		
		Set<String> basePackages = appConfiguraion.getBasePackages();

		if(basePackages == null)
		{
			basePackages = new HashSet<>();
		}

		basePackages.add("com.yukthitech");

		loadStepTypes(basePackages);
		loadValidationTypes(basePackages);
	}

	/**
	 * Loads all step types found in specified base packages.
	 * 
	 * @param basePackages
	 *            Base packages to be scanned
	 */
	private void loadStepTypes(Set<String> basePackages)
	{
		Reflections reflections = null;
		Set<Class<? extends IStep>> stepTypes = null;
		Executable executable = null;

		for(String pack : basePackages)
		{
			reflections = new Reflections(pack, new TypeAnnotationsScanner(), new SubTypesScanner());

			stepTypes = reflections.getSubTypesOf(IStep.class);

			for(Class<? extends IStep> stepType : stepTypes)
			{
				if(stepType.isInterface() || Modifier.isAbstract(stepType.getModifiers()))
				{
					continue;
				}

				executable = stepType.getAnnotation(Executable.class);

				if(executable == null)
				{
					continue;
				}

				nameToStepType.put(executable.name(), stepType);
			}
		}
	}

	/**
	 * Loads all validation types found in specified base packages.
	 * 
	 * @param basePackages
	 *            Base packages to be scanned
	 */
	private void loadValidationTypes(Set<String> basePackages)
	{
		Reflections reflections = null;
		Set<Class<? extends IValidation>> validationTypes = null;
		Executable executable = null;

		for(String pack : basePackages)
		{
			reflections = new Reflections(pack, new TypeAnnotationsScanner(), new SubTypesScanner());

			validationTypes = reflections.getSubTypesOf(IValidation.class);

			for(Class<? extends IValidation> validationType : validationTypes)
			{
				if(validationType.isInterface() || Modifier.isAbstract(validationType.getModifiers()))
				{
					continue;
				}

				executable = validationType.getAnnotation(Executable.class);

				if(executable == null)
				{
					continue;
				}

				nameToValidationType.put(executable.name(), validationType);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.reserved.IReserveNodeHandler#createCustomNodeBean(com.yukthitech.ccg.xml.IParserHandler, com.yukthitech.ccg.xml.BeanNode, com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public Object createCustomNodeBean(IParserHandler handler, BeanNode beanNode, XMLAttributeMap attrMap)
	{
		Object parent = beanNode.getParent();

		if(parent instanceof IStepContainer)
		{
			IStep step = newStep(beanNode.getName());

			if(step != null)
			{
				return step;
			}
		}

		if(parent instanceof IValidationContainer)
		{
			Object validator = newValidation(beanNode.getName());
			
			if(validator != null)
			{
				return validator;
			}
		}

		throw new InvalidStateException("No step/validator found with name: {}", beanNode.getName());
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.reserved.IReserveNodeHandler#handleCustomNodeEnd(com.yukthitech.ccg.xml.IParserHandler, com.yukthitech.ccg.xml.BeanNode, com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public void handleCustomNodeEnd(IParserHandler handler, BeanNode beanNode, XMLAttributeMap attrMap)
	{
		if("beanRef".equals(beanNode.getName()))
		{
			
		}
		
		Object parent = beanNode.getParent();
		Object bean = beanNode.getActualBean();

		if((bean instanceof IStep) && (parent instanceof IStepContainer))
		{
			AutomationUtils.validateRequiredParams(bean);
			
			((IStepContainer) parent).addStep((IStep) bean);
		}

		if((bean instanceof IValidation) && (parent instanceof IValidationContainer))
		{
			AutomationUtils.validateRequiredParams(bean);
			
			((IValidationContainer) parent).addValidation((IValidation) bean);
		}
	}
	
	/**
	 * Creates a new step with specified name. A step can be named using @
	 * {@link Executable}
	 * 
	 * @param stepTypeName
	 *            Step name
	 * @return Matching step instance
	 */
	@SuppressWarnings("rawtypes")
	public IStep newStep(String stepTypeName)
	{
		Class<? extends IStep> stepType = nameToStepType.get(stepTypeName);

		if(stepType == null)
		{
			return null;
		}
		
		Executable executable = stepType.getAnnotation(Executable.class);
		IPlugin<?> plugin = null;
		
		for(Class<? extends IPlugin> pluginType : executable.requiredPluginTypes())
		{
			plugin = applicationConfiguration.getPlugin(pluginType);
			
			if(plugin == null)
			{
				throw new InvalidStateException("Plugin-type {} is not specified by application-configuration which is required by step: {}", pluginType.getName(), stepTypeName);
			}
			
			context.addRequirePlugin(plugin);
		}

		try
		{
			return stepType.newInstance();
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating step of type - {}", stepType.getName());
		}
	}

	/**
	 * Creates a new validation with specified name. A step can be named using @
	 * {@link Executable}
	 * 
	 * @param validationTypeName
	 *            Validation name
	 * @return Matching validation instance
	 */
	@SuppressWarnings("rawtypes")
	public IValidation newValidation(String validationTypeName)
	{
		Class<? extends IValidation> validationType = nameToValidationType.get(validationTypeName);

		if(validationType == null)
		{
			return null;
		}

		Executable executable = validationType.getAnnotation(Executable.class);
		IPlugin<?> plugin = null;
		
		for(Class<? extends IPlugin> pluginType : executable.requiredPluginTypes())
		{
			plugin = applicationConfiguration.getPlugin(pluginType);
			
			if(plugin == null)
			{
				throw new InvalidStateException("Plugin-type {} is not specified by application-configuration which is required by validator: {}", validationType.getName(), validationTypeName);
			}
			
			context.addRequirePlugin(plugin);
		}

		try
		{
			return validationType.newInstance();
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating validation of type - {}", validationType.getName());
		}
	}
}
