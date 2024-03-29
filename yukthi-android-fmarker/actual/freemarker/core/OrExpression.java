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
package freemarker.core;

import freemarker.template.TemplateException;

final class OrExpression extends BooleanExpression {

    private final Expression lho;
    private final Expression rho;

    OrExpression(Expression lho, Expression rho) {
        this.lho = lho;
        this.rho = rho;
    }

    @Override
    boolean evalToBoolean(Environment env) throws TemplateException {
        return lho.evalToBoolean(env) || rho.evalToBoolean(env);
    }

    @Override
    public String getCanonicalForm() {
        return lho.getCanonicalForm() + " || " + rho.getCanonicalForm();
    }
    
    @Override
    String getNodeTypeSymbol() {
        return "||";
    }

    @Override
    boolean isLiteral() {
        return constantValue != null || (lho.isLiteral() && rho.isLiteral());
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(
            String replacedIdentifier, Expression replacement, ReplacemenetState replacementState) {
    	return new OrExpression(
    	        lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState),
    	        rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
        case 0: return lho;
        case 1: return rho;
        default: throw new IndexOutOfBoundsException();
        }
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        return ParameterRole.forBinaryOperatorOperand(idx);
    }
    
}
