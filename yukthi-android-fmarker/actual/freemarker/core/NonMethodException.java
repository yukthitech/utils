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

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;

/**
 * Indicates that a {@link TemplateMethodModel} value was expected, but the value had a different type.
 * 
 * @since 2.3.21
 */
public class NonMethodException extends UnexpectedTypeException {

    private static final Class[] EXPECTED_TYPES = new Class[] { TemplateMethodModel.class };
    
    public NonMethodException(Environment env) {
        super(env, "Expecting method value here");
    }

    public NonMethodException(String description, Environment env) {
        super(env, description);
    }

    NonMethodException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonMethodException(
            Expression blamed, TemplateModel model, Environment env)
            throws InvalidReferenceException {
        super(blamed, model, "method", EXPECTED_TYPES, env);
    }

    NonMethodException(
            Expression blamed, TemplateModel model, String tip,
            Environment env)
            throws InvalidReferenceException {
        super(blamed, model, "method", EXPECTED_TYPES, tip, env);
    }

    NonMethodException(
            Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "method", EXPECTED_TYPES, tips, env);
    }    

}
