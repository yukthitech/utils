package com.yukthitech.autox.doc;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.autox.test.ResourceManager;

/**
 * Tool to generate documentation for all plugins, steps and validations.
 * @author akiran
 */
public class DocGenerator
{
	/**
	 * Loads the specified step types into specified doc info.
	 * @param docInformation target doc info
	 * @param stepTypes step types to be loaded
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void loadSteps(DocInformation docInformation, Set<Class<?>> stepTypes)
	{
		Executable executableAnnot = null;

		for(Class<?> stepType : stepTypes)
		{
			if(stepType.isInterface() || Modifier.isAbstract(stepType.getModifiers()))
			{
				continue;
			}
			
			executableAnnot = stepType.getAnnotation(Executable.class);
			
			if(executableAnnot == null)
			{
				continue;
			}
			
			System.out.println("Found step of type: " + stepType.getName());
			docInformation.addStep(new StepInfo( (Class) stepType, executableAnnot));
		}
	}
	
	/**
	 * Loads specified validation types into target doc info.
	 * @param docInformation target doc info.
	 * @param validationTypes Validation types to be loaded
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void loadValidations(DocInformation docInformation, Set<Class<?>> validationTypes)
	{
		Executable executableAnnot = null;

		for(Class<?> validationType : validationTypes)
		{
			if(validationType.isInterface() || Modifier.isAbstract(validationType.getModifiers()))
			{
				continue;
			}
			
			executableAnnot = validationType.getAnnotation(Executable.class);
			
			if(executableAnnot == null)
			{
				continue;
			}
			
			System.out.println("Found validation of type: " + validationType.getName());
			docInformation.addValidation(new ValidationInfo( (Class) validationType, executableAnnot));
		}
	}

	/**
	 * Loads specified plugins into target doc info.
	 * @param docInformation target doc info
	 * @param pluginTypes plugins to be loaded
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void loadPlugins(DocInformation docInformation, Set<Class<?>> pluginTypes)
	{
		Executable executableAnnot = null;

		for(Class<?> pluginType : pluginTypes)
		{
			if(pluginType.isInterface() || Modifier.isAbstract(pluginType.getModifiers()))
			{
				continue;
			}
			
			executableAnnot = pluginType.getAnnotation(Executable.class);
			
			if(executableAnnot == null)
			{
				continue;
			}
			
			System.out.println("Found plugin of type: " + pluginType.getName());
			docInformation.addPlugin(new PluginInfo((Class) pluginType, executableAnnot));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws Exception
	{
		if(args.length != 2)
		{
			System.err.println("Invalid number of arguments specified.");
			System.err.println("Syntax: java " + DocGenerator.class.getName() + " <comma-separated-packages-to-scan> <out-folder>");
		}
		
		String packStr = args[0];
		String outFolder = args[1];
		
		String basePackages[] = packStr.split("\\s*\\,\\s*");
		
		Reflections reflections = null;
		
		DocInformation docInformation = new DocInformation();

		for(String pack : basePackages)
		{
			System.out.println("Scanning package - " + pack);
			reflections = new Reflections(pack, new SubTypesScanner());
			
			loadSteps(docInformation, (Set) reflections.getSubTypesOf(IStep.class) );

			loadValidations(docInformation, (Set) reflections.getSubTypesOf(IValidation.class) );
			
			loadPlugins(docInformation, (Set) reflections.getSubTypesOf(IPlugin.class) );
		}
		
		//convert data into json
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(docInformation);
		
		File dataJsFolder = new File(outFolder, "doc");
		
		dataJsFolder.mkdirs();
		
		File dataJsFile = new File(dataJsFolder, "data.js");
		FileUtils.writeStringToFile(dataJsFile, "var docData = " + json + ";");
		
		ResourceManager.getInstance().copyDocResources(new File(outFolder));
	}
}
