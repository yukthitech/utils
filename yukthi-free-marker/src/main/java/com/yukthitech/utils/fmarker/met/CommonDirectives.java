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
package com.yukthitech.utils.fmarker.met;

import java.io.IOException;
import java.util.StringTokenizer;

import com.yukthitech.utils.annotations.Named;
import com.yukthitech.utils.fmarker.annotaion.ExampleDoc;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerDirective;

import freemarker.template.TemplateException;

@Named("Common Directives")
public class CommonDirectives
{
	@FreeMarkerDirective(value = "trim", 
			description = "Trims the content enclosed within this directive.",
			examples = {
				@ExampleDoc(usage = "<@trim>   some content  </@trim>", result = "some content")
			})
	public static String trim(
			@FmParam(name = "body", body = true, description = "Enclosing body content") String body
		) throws TemplateException, IOException
	{
		return body.trim();
	}

	@FreeMarkerDirective( 
			description = "Helps in indenting the enclosed content. Accepts optional prefix attribute, defaults to empty string. Every line will be trimmed and converted into single line and "
					+ "prefix will be added at the start. And from the output content '\\t' and '\\n' will be replaced with tab and new-line characters respectively.",
			examples = {
				@ExampleDoc(title="Without parameters", usage = "<@indent>   first line\n\n   second line  </@indent>", result = "first linesecond line"),
				@ExampleDoc(title="With Prefix", usage = "<@indent prefix='--'>   first line\n\n   second line    </@indent>", result = "--first line--second line"),
				@ExampleDoc(title="With Prefix and retainLineBreaks", 
					usage = "<@indent prefix='--' retainLineBreaks=true>   first line\n\n   second line    </@indent>", 
					result = "--first line\n--second line")
			})
	public static String indent(
			@FmParam(name = "body", body = true, description = "Enclosing body content") String body,
			@FmParam(name = "prefix", description = "If specified, this value will be added in start of every line", defaultValue = "<empty string>") String prefix,
			@FmParam(name = "retainLineBreaks", description = "[boolean] if true, lines will be maintained as separate lines.", defaultValue = "false") boolean retainLineBreaks
			) throws TemplateException, IOException
	{
		StringTokenizer st = new StringTokenizer(body, "\n");
		StringBuilder builder = new StringBuilder();
		String line = null;
		boolean firstLine = true;
		
		prefix = (prefix == null) ? "" : prefix;

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

		return output;
	}
}
