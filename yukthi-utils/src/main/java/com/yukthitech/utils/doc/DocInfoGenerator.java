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
package com.yukthitech.utils.doc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper class to generate doc info using reflections.
 * @author akranthikiran
 */
public class DocInfoGenerator
{
	private static String toString(Type type)
	{
		if(type == null)
		{
			return null;
		}
		
		if(type instanceof Class)
		{
			return ((Class<?>) type).getSimpleName();
		}
		
		if(!(type instanceof ParameterizedType))
		{
			return type.getTypeName();
		}

		ParameterizedType parameterizedType = (ParameterizedType) type;
		String baseName = ((Class<?>)parameterizedType.getRawType()).getSimpleName();
		
		StringBuilder builder = new StringBuilder(baseName).append("<");
		int idx = 0;
		
		for(Type param : parameterizedType.getActualTypeArguments())
		{
			if(idx > 0)
			{
				builder.append(", ");
			}
			
			builder.append(toString(param));
			idx++;
		}
		
		return builder.append(">").toString();
	}
	
	private static void populateDoc(BaseDoc<?> docInfo, Doc doc)
	{
		if(doc == null)
		{
			return;
		}
		
		docInfo.setName(StringUtils.isNotBlank(doc.name()) ? doc.name() : docInfo.getName())
			.setDescription(doc.value())
			.setGroup(StringUtils.isNotBlank(doc.group()) ? doc.group() : null)
			.setExamples(doc.examples().length > 0 ? Arrays.asList(doc.examples()) : null);
	}
	
	public static ClassDoc generateClassDoc(Class<?> type)
	{
		Doc doc = type.getAnnotation(Doc.class);
		
		if(doc == null)
		{
			return null;
		}
		
		ClassDoc clsDoc = new ClassDoc().setName(type.getName());
		populateDoc(clsDoc, doc);
		
		return collectClassElements(type, clsDoc);
	}
	
	private static ClassDoc collectClassElements(Class<?> type, ClassDoc doc)
	{
		Class<?> superCls = type.getSuperclass();
		
		if(!Object.class.equals(superCls))
		{
			collectClassElements(superCls, doc);
		}
		
		Field fields[] = type.getDeclaredFields();
		Arrays.asList(fields).stream().map(fld -> generateFieldDoc(fld)).filter(fldDoc -> fldDoc != null).forEach(fldDoc -> doc.addField(fldDoc));
		
		Constructor<?> constructors[] = type.getDeclaredConstructors();
		Arrays.asList(constructors).stream().map(cons -> generateConstructorDoc(cons)).filter(consDoc -> consDoc != null).forEach(consDoc -> doc.addConstructor(consDoc));
		
		Method methods[] = type.getDeclaredMethods();
		Arrays.asList(methods).stream().map(met -> generateMethodDoc(met)).filter(metDoc -> metDoc != null).forEach(metDoc -> doc.addMethod(metDoc));
		
		return doc;
	}

	public static MethodDoc generateMethodDoc(Method met)
	{
		Doc doc = met.getAnnotation(Doc.class);
		
		if(doc == null)
		{
			return null;
		}
		
		MethodDoc metDoc = new MethodDoc()
				.setName(met.getName())
				.setReturnDescription(doc.returnDesc())
				.setReturnType(toString(met.getGenericReturnType()));
		
		addParamDocs(metDoc, met.getParameters(), met.getGenericParameterTypes());
		populateDoc(metDoc, doc);
		
		return metDoc;
	}
	
	private static void addParamDocs(MethodDoc doc, Parameter params[], Type paramTypes[])
	{
		if(params.length == 0)
		{
			return;
		}
		
		int index = 0;
		
		for(Parameter param : params)
		{
			ParamDoc pdoc = new ParamDoc()
					.setName(param.getName())
					.setType(toString(paramTypes[index]));
			
			populateDoc(pdoc, param.getAnnotation(Doc.class));
			doc.addParameter(pdoc);
			index++;
		}
	}
	
	public static MethodDoc generateConstructorDoc(Constructor<?> constructor)
	{
		Doc doc = constructor.getAnnotation(Doc.class);
		
		if(doc == null)
		{
			return null;
		}
		
		MethodDoc metDoc = new MethodDoc()
				.setName(constructor.getDeclaringClass().getSimpleName());
		
		addParamDocs(metDoc, constructor.getParameters(), constructor.getGenericParameterTypes());
		populateDoc(metDoc, doc);
		
		return metDoc;
	}

	public static FieldDoc generateFieldDoc(Field fld)
	{
		Doc doc = fld.getAnnotation(Doc.class);
		
		if(doc == null)
		{
			return null;
		}
		
		FieldDoc fldDoc = new FieldDoc()
				.setName(fld.getName())
				.setType(toString(fld.getGenericType()));
		
		populateDoc(fldDoc, doc);
		return fldDoc;
	}
}
