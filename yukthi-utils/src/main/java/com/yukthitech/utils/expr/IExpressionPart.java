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
package com.yukthitech.utils.expr;

import java.util.Set;

/**
 * Represents abstract of part of the expression.
 * @author akiran
 */
public interface IExpressionPart
{
	/**
	 * Collects variables used in this part into provided "variables".
	 * @param variables Set where variables will be collected.
	 */
	public void collectVariables(Set<String> variables);
	
	/**
	 * Gets the result type of the expression part.
	 * @param variableTypeProvider Provides variable type.
	 * @param registry Expression registry to be used
	 * @return Final result type
	 */
	public Class<?> getType(IVariableTypeProvider variableTypeProvider, ExpressionRegistry registry);
	
	/**
	 * Evaluates expression part and provides final value.
	 * @param variableValueProvider Variable value provider.
	 * @param registry Expression registry to be used
	 * @return Result value
	 */
	public Object evaluate(IVariableValueProvider variableValueProvider, ExpressionRegistry registry);
}
