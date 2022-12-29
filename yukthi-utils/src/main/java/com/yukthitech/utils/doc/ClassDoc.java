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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents class documentation.
 * @author akranthikiran
 */
public class ClassDoc extends BaseDoc<ClassDoc>
{
	/**
	 * Field documentations.
	 */
	private List<FieldDoc> fields;
	
	/**
	 * Constructor documentations.
	 */
	private List<MethodDoc> constructors;
	
	/**
	 * Method documentations.
	 */
	private List<MethodDoc> methods;

	public List<FieldDoc> getFields()
	{
		return fields;
	}

	public ClassDoc setFields(List<FieldDoc> fields)
	{
		this.fields = fields;
		return this;
	}
	
	/** 
	 * Adds value to {@link #fields field}
	 *
	 * @param fld fld to be added
	 */
	 public ClassDoc addField(FieldDoc fld)
	 {
		 if(fields == null)
		 {
			 fields = new ArrayList<FieldDoc>();
		 }
		 
		 fields.add(fld);
		 return this;
	 }
	 

	public List<MethodDoc> getConstructors()
	{
		return constructors;
	}

	public ClassDoc setConstructors(List<MethodDoc> constructors)
	{
		this.constructors = constructors;
		return this;
	}
	
	/** 
	 * Adds value to {@link #constructors constructor}
	 *
	 * @param constructor constructor to be added
	 */
	 public ClassDoc addConstructor(MethodDoc constructor)
	 {
		 if(constructors == null)
		 {
			 constructors = new ArrayList<MethodDoc>();
		 }
		 
		 constructors.add(constructor);
		 return this;
	 }
	 

	public List<MethodDoc> getMethods()
	{
		return methods;
	}

	public ClassDoc setMethods(List<MethodDoc> methods)
	{
		this.methods = methods;
		return this;
	}
	
	/** 
	 * Adds value to {@link #methods method}
	 *
	 * @param method method to be added
	 */
	 public ClassDoc addMethod(MethodDoc method)
	 {
		 if(methods == null)
		 {
			 methods = new ArrayList<MethodDoc>();
		 }
		 
		 methods.add(method);
		 return this;
	 }
	 
}
