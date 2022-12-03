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
package com.yukthitech.indexer.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the field booster for the search query.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FieldBooster
{
	/**
	 * Field name to be used as booster field.
	 * @return
	 */
	public String field();
	
	/**
	 * Defaults to zero (uses linear boosting). If value is greater than zero than log booster
	 * will be used with specified factor value.
	 * 
	 * Linear boosting: actual-score * field-value
	 * 		Score increases linearly with field value
	 * Log boosting: actual-score * log(log-factor + field-value)
	 * 		Score increases highly when field values are low and rate of increase decreases with high values.
	 * @return
	 */
	public int logFactor() default 0;
}
