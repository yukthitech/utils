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
package com.yukthitech.utils.cli;

import java.lang.reflect.Field;

import org.apache.commons.cli.Option;

/**
 * Using {@link CliArgument} annotation command line arguments can be mapped directly to bean fields.
 * The factory helps in processing this annotations from specified types and returns the meta information in the form
 * of {@link CommandLineOptions}. This in turn can be used to map command line arguments into beans.
 * 
 * @author akiran
 */
public class OptionsFactory
{
	/**
	 * Extracts the information from specified types and returns the meta data in 
	 * the form of {@link CommandLineOptions} which in turn can be used to parse command
	 * line arguments.
	 * @param types Types to be used to extract information
	 * @return Extracted information
	 */
	public static CommandLineOptions buildCommandLineOptions(Class<?>... types)
	{
		CommandLineOptions commandLineOptions = new CommandLineOptions();
		
		for(Class<?> type : types)
		{
			collectCliArgOptions(type, commandLineOptions);
		}
		
		return commandLineOptions;
	}
	
	/**
	 * Collects the options and their meta data from specified type.
	 * @param type Type from which information needs to be collected
	 * @param options Options to which information should be added
	 */
	private static void collectCliArgOptions(Class<?> type, CommandLineOptions options)
	{
		if(options.isBeanTypeLoaded(type))
		{
			return;
		}
		
		Field fields[] = type.getDeclaredFields();
		
		CliArgument argAnnot = null;
		boolean valueRequired = false;
		Option option = null;
		
		for(Field field : fields)
		{
			argAnnot = field.getAnnotation(CliArgument.class);
			
			if(argAnnot == null)
			{
				continue;
			}
			
			valueRequired = !boolean.class.equals(field.getType()) && !Boolean.class.equals(field.getType());
			
			String description = argAnnot.description();
			
			if(argAnnot.required())
			{
				description = "[Mandatory] " + description;
			}
			
			option = new Option(argAnnot.name(), argAnnot.longName(), valueRequired, description);
			option.setArgName(field.getName());
			
			options.addOption( type, field, option, argAnnot.required() );
		}
	}
}
