package com.yukthitech.autox.doc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.fmarker.FreeMarkerMethodDoc;

/**
 * Tool to generate documentation for all plugins, steps and validations.
 * @author akiran
 */
public class DocGenerator
{
	/**
	 * Loads the examples from all the autox example resources.
	 */
	private static ExampleCollectionFile loadExamples()
	{
		try
		{
			ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();   
			Resource [] mappingLocations = patternResolver.getResources("classpath*:/autox-examples/*.xml");
			
			if(mappingLocations == null || mappingLocations.length == 0)
			{
				return null;
			}
			
			InputStream is = null;
			ExampleCollectionFile finalFile = new ExampleCollectionFile(), curFile = null;
			
			for(Resource res : mappingLocations)
			{
				is = res.getInputStream();
				
				curFile = new ExampleCollectionFile();
				XMLBeanParser.parse(is, curFile);
				is.close();
				
				finalFile.merge(curFile);
			}
			
			return finalFile;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while loading example resources", ex);
		}
	}
	
	/**
	 * Loads the specified step types into specified doc info.
	 * @param docInformation target doc info
	 * @param stepTypes step types to be loaded
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void loadSteps(DocInformation docInformation, Set<Class<?>> stepTypes, ExampleCollectionFile exampleCollections)
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
			
			//ignore validation steps
			if(IValidation.class.isAssignableFrom(stepType))
			{
				continue;
			}
			
			//System.out.println("Found step of type: " + stepType.getName());
			docInformation.addStep(new StepInfo( (Class) stepType, executableAnnot, exampleCollections.getExamples(stepType.getName()) ));
		}
	}
	
	/**
	 * Loads specified validation types into target doc info.
	 * @param docInformation target doc info.
	 * @param validationTypes Validation types to be loaded
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void loadValidations(DocInformation docInformation, Set<Class<?>> validationTypes, ExampleCollectionFile exampleCollections)
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
			
			//System.out.println("Found validation of type: " + validationType.getName());
			docInformation.addValidation(
					new ValidationInfo( 
							(Class) validationType, 
							executableAnnot,
							exampleCollections.getExamples(validationType.getName())
							)
					);
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
	public static DocInformation buildDocInformation(String basePackages[]) throws Exception
	{
		DocInformation docInformation = new DocInformation();
		Reflections reflections = null;
		
		ExampleCollectionFile exampleCollections = loadExamples();
		
		for(String pack : basePackages)
		{
			reflections = new Reflections(pack, new SubTypesScanner());
			
			loadSteps(docInformation, (Set) reflections.getSubTypesOf(IStep.class), exampleCollections );

			loadValidations(docInformation, (Set) reflections.getSubTypesOf(IValidation.class), exampleCollections );
			
			loadPlugins(docInformation, (Set) reflections.getSubTypesOf(IPlugin.class) );
		}
		
		FreeMarkerMethodManager.reload(null, new HashSet<>(Arrays.asList(basePackages)));
		Collection<FreeMarkerMethodDoc> methodDocs = FreeMarkerMethodManager.getRegisterMethodDocuments();

		docInformation.setFreeMarkerMethods(new HashSet<>(methodDocs));
		
		//convert data into json
		return docInformation;
	}
	
	/**
	 * Loads basic documents.
	 * @return basic documents.
	 */
	private static List<BasicDocs.Document> loadBasicDocs() throws IOException
	{
		BasicDocs docs = new BasicDocs();
		XMLBeanParser.parse(DocGenerator.class.getResourceAsStream("/docs/basic-docs.xml"), docs);
		
		for(BasicDocs.Document doc : docs.getDocuments())
		{
			String content = IOUtils.toString(DocGenerator.class.getResourceAsStream(doc.getFile()));
			doc.setContent(content);
		}
		
		return docs.getDocuments();
	}

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
		
		DocInformation docInformation = buildDocInformation(basePackages);
		docInformation.setBasicDocuments(loadBasicDocs());

		//convert data into json
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(docInformation);
		
		File apiFolder = new File(outFolder, "api");
		File dataJsFolder = new File(apiFolder, "js");
		
		dataJsFolder.mkdirs();
		
		File dataJsFile = new File(dataJsFolder, "data.js");
		FileUtils.write(dataJsFile, "var docData = " + json + ";", (String) null);
		
		System.out.println("Files are generate to folder: " + outFolder);
	}
}
