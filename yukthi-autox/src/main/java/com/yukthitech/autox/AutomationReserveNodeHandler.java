package com.yukthitech.autox;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.autox.test.StepGroup;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.reserved.IReserveNodeHandler;
import com.yukthitech.ccg.xml.reserved.NodeName;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
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
	 * Fetches the step group represented by specified bean node and adds the steps/validations
	 * from that group to specified parent.
	 * @param parent parent to which steps/validations to be added
	 * @param beanNode node representing step group
	 */
	private void addStepGroup(Object parent, BeanNode beanNode)
	{
		if(!(parent instanceof IStepContainer))
		{
			return;
		}
		
		String name = beanNode.getAttributeMap().get("name", null);
		
		if(name == null)
		{
			throw new InvalidStateException("Mandatory attribute 'name' is not specified in step-group-ref node");
		}
		
		StepGroup stepGroup = context.getStepGroup(name);
		
		if(stepGroup == null)
		{
			throw new InvalidArgumentException("No step-group found with specified name - {}", name);
		}
		
		if((parent instanceof IStepContainer) && stepGroup.getSteps() != null)
		{
			IStepContainer stepContainer = (IStepContainer) parent;
			
			for(IStep step : stepGroup.getSteps())
			{
				stepContainer.addStep(step.clone());
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
		
		if("stepGroupRef".equals(beanNode.getName()))
		{
			addStepGroup(parent, beanNode);
			return null;
		}

		if(parent instanceof IStepContainer)
		{
			IStep step = newStep(beanNode.getName());

			if(step != null)
			{
				return step;
			}
			
			throw new InvalidStateException("No step/validator found with name: {}.\nAvailable Steps/Validators: {}", beanNode.getName(), nameToStepType.keySet());
		}

		throw new InvalidStateException("Unsupported custom node encountered. If it is step/validator ensure it is defined under right parent. Node name: {}", beanNode.getName());
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.reserved.IReserveNodeHandler#handleCustomNodeEnd(com.yukthitech.ccg.xml.IParserHandler, com.yukthitech.ccg.xml.BeanNode, com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public void handleCustomNodeEnd(IParserHandler handler, BeanNode beanNode, XMLAttributeMap attrMap)
	{
		Object parent = beanNode.getParent();
		Object bean = beanNode.getActualBean();

		if((bean instanceof IStep) && (parent instanceof IStepContainer))
		{
			AutomationUtils.validateRequiredParams(bean);
			
			((IStepContainer) parent).addStep((IStep) bean);
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
}
