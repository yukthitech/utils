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

/**
 * Represents a &lt;break&gt; instruction to break out of a loop.
 */
final class BreakInstruction extends TemplateElement {

    @Override
    TemplateElement[] accept(Environment env) {
        throw BreakOrContinueException.BREAK_INSTANCE;
    }

    @Override
    protected String dump(boolean canonical) {
        return canonical ? "<" + getNodeTypeSymbol() + "/>" : getNodeTypeSymbol();
    }
    
    @Override
    String getNodeTypeSymbol() {
        return "#break";
    }

    @Override
    int getParameterCount() {
        return 0;
    }

    @Override
    Object getParameterValue(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
    
}


