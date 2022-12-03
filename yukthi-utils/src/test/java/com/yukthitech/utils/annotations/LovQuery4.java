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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author akiran
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@SearchResult(count = 10, 
	mappings = {
		@Mapping(field = "field", property = "property5")
	}, 
	returnMapping = @Mapping(field = "retField", property = "retProperty5")
)
public @interface LovQuery4
{
	@OverrideProperty(targetAnnotationType = SearchResult.class, property = "count")
	public int count();

	@OverrideProperty(targetAnnotationType = SearchResult.class, property = "returnMapping.field")
	public String returnField();

	@OverrideProperty(targetAnnotationType = SearchResult.class, property = "mappings[0].field")
	public String mappingField();
}
