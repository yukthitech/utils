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
package com.yukthitech.utils.annotations;

/**
 * @author akiran
 *
 */
public interface TestInterface
{
	@SearchResult(count = 10, 
			mappings = {
					@Mapping(field = "field1", property = "property1")
			}, 
			returnMapping = @Mapping(field = "retField1", property = "retProperty1")
	)
	public void directAnnotation();
	
	@LovQuery1
	public void recursiveAnnotaion();
	
	@LovQuery2(count = 30)
	public void simplePropOverride();

	@LovQuery3(returnField = "retField4")
	public void nestedPropPropOverride();

	@LovQuery4(count = 50, mappingField = "field5", returnField = "retField5")
	public void arrayPropOverride();
	
	@LovQuery5(field = "field6")
	public void multiOverride();
	
	@SuppressRecursiveSearch(SearchResult.class)
	@LovQuery1
	public void suppressMethod1();
	
	@SuppressRecursiveSearch(SearchResult.class)
	@LovQuery1
	@SearchResult(count = 20, 
		mappings = { @Mapping(field = "field2", property = "property2") }, 
		returnMapping = @Mapping(field = "retField2", property = "retProperty2")
	)
	public void suppressMethod2();
}
