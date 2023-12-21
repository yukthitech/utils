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
package com.yukthitech.utils.fmarker.doc;

import java.io.InputStream;
import java.util.Collection;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.FreeMarkerMethodDoc;

public class DocGenerator
{
	private static FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	public static void main(String args[]) throws Exception
	{
		Collection<FreeMarkerMethodDoc> methodDocLst = freeMarkerEngine.getRegisterMethodDocuments();
		String methodTemplate = load("/method-doc.ftl");
		
		String methodDoc = freeMarkerEngine.processTemplate("Method Tempalte", methodTemplate, CommonUtils.toMap("methods", methodDocLst));
		System.out.println(methodDoc);
	}
	
	private static String load(String resource) throws Exception
	{
		InputStream is = DocGenerator.class.getResourceAsStream("/method-doc.ftl");
		StringBuilder builder = new StringBuilder();
		byte buff[]= new byte[2048];
		int read = 0;
		
		while((read = is.read(buff)) > 0)
		{
			builder.append(new String(buff, 0, read));
		}
		
		is.close();
		return builder.toString();
	}
}
