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
package com.yukthitech.utils.fmarker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.StringTokenizer;

import com.yukthitech.utils.fmarker.annotaion.FreeMarkerDirective;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class DefaultDirectives
{
	@FreeMarkerDirective(value = "trim")
	public static void trim(Environment env, Map<String, String> params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
	{
		StringWriter writer = new StringWriter();
		body.render(writer);

		env.getOut().append(writer.toString().trim());
	}

	@FreeMarkerDirective
	public static void indent(Environment env, Map<String, Object> params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
	{
		StringWriter writer = new StringWriter();
		body.render(writer);

		String actualOutput = writer.toString();
		Object prefix = params.get("prefix");
		prefix = (prefix != null) ? prefix.toString() : "";
		
		boolean retainLineBreaks = "true".equals(MethodProxy.convertArgument(params.get("retainLineBreaks"), String.class));

		StringTokenizer st = new StringTokenizer(actualOutput, "\n");
		StringBuilder builder = new StringBuilder();
		String line = null;
		boolean firstLine = true;

		while(st.hasMoreTokens())
		{
			if(firstLine)
			{
				firstLine = false;
			}
			else
			{
				if(retainLineBreaks)
				{
					builder.append("\n");
				}
			}
			
			line = st.nextToken().trim();
			builder.append(prefix).append(line);
		}

		String output = builder.toString();
		output = output.replace("\\t", "\t");
		output = output.replace("\\n", "\n");

		env.getOut().append(output);
	}
}
