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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.fmarker.FreeMarkerDirectiveDoc;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.FreeMarkerMethodDoc;

public class DocGenerator
{
	private static FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	public static void main(String args[]) throws Exception
	{
		Collection<FreeMarkerMethodDoc> methodDocCol = freeMarkerEngine.getRegisterMethodDocuments();
		List<FreeMarkerMethodDoc> methodDocLst = methodDocCol
				.stream()
				.filter(doc -> !doc.getName().startsWith("_"))
				.collect(Collectors.toList());
		
		String methodDoc = freeMarkerEngine.processTemplate("/method-doc.ftl", load("/method-doc.ftl"), CommonUtils.toMap("methods", methodDocLst));
		//System.out.println(methodDoc);
		
		Collection<FreeMarkerDirectiveDoc> directiveDocCol = freeMarkerEngine.getRegisterDirectiveDocuments();
		List<FreeMarkerDirectiveDoc> directiveDocLst = directiveDocCol
				.stream()
				.filter(doc -> !doc.getName().startsWith("_"))
				.collect(Collectors.toList());
		String directiveDoc = freeMarkerEngine.processTemplate("/directive-doc.ftl", load("/directive-doc.ftl"), CommonUtils.toMap("directives", directiveDocLst));
		//System.out.println(directiveDoc);
		
		String readMeDoc = load("/README-template.md");
		readMeDoc = readMeDoc.replace("[[defaultMethodContent]]", methodDoc);
		readMeDoc = readMeDoc.replace("[[defaultDirectiveContent]]", directiveDoc);
		
		FileOutputStream fos = new FileOutputStream("README.md");
		fos.write(readMeDoc.getBytes());
		fos.flush();
		fos.close();
		
		System.out.println("Successfully generated documentation..");
	}
	
	private static String load(String resource) throws Exception
	{
		return load(DocGenerator.class.getResourceAsStream(resource));
	}
	
	private static String load(InputStream is) throws Exception
	{
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
