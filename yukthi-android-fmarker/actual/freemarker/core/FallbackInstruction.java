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

import java.io.IOException;

import freemarker.template.TemplateException;

final class FallbackInstruction extends TemplateElement {

    @Override
    TemplateElement[] accept(Environment env) throws IOException, TemplateException {
        env.fallback();
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        return canonical ? "<" + getNodeTypeSymbol() + "/>" : getNodeTypeSymbol();
    }
    
    @Override
    String getNodeTypeSymbol() {
        return "#fallback";
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
    
    @Override
    boolean isShownInStackTrace() {
        return true;
    }

}
