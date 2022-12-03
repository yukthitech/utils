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
import freemarker.template.TemplateModel;

final class ParentheticalExpression extends Expression {

    private final Expression nested;

    ParentheticalExpression(Expression nested) {
        this.nested = nested;
    }

    @Override
    boolean evalToBoolean(Environment env) throws TemplateException {
        return nested.evalToBoolean(env);
    }

    @Override
    public String getCanonicalForm() {
        return "(" + nested.getCanonicalForm() + ")";
    }
    
    @Override
    String getNodeTypeSymbol() {
        return "(...)";
    }
    
    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        return nested.eval(env);
    }
    
    @Override
    public boolean isLiteral() {
        return nested.isLiteral();
    }
    
    Expression getNestedExpression() {
        return nested;
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(
            String replacedIdentifier, Expression replacement, ReplacemenetState replacementState) {
        return new ParentheticalExpression(
                nested.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }
    
    @Override
    int getParameterCount() {
        return 1;
    }

    @Override
    Object getParameterValue(int idx) {
        if (idx != 0) throw new IndexOutOfBoundsException();
        return nested;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) throw new IndexOutOfBoundsException();
        return ParameterRole.ENCLOSED_OPERAND;
    }
    
}
