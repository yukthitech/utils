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

import freemarker.template.TemplateModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.CollectionUtils;

/**
 * Indicates that a {@link TemplateSequenceModel} value was expected, but the value had a different type.
 * 
 * @since 2.3.21
 */
public class NonSequenceException extends UnexpectedTypeException {

    private static final Class[] EXPECTED_TYPES = new Class[] { TemplateSequenceModel.class };
    
    public NonSequenceException(Environment env) {
        super(env, "Expecting sequence value here");
    }

    public NonSequenceException(String description, Environment env) {
        super(env, description);
    }

    NonSequenceException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonSequenceException(
            Expression blamed, TemplateModel model, Environment env)
            throws InvalidReferenceException {
        this(blamed, model, CollectionUtils.EMPTY_OBJECT_ARRAY, env);
    }

    NonSequenceException(
            Expression blamed, TemplateModel model, String tip,
            Environment env)
            throws InvalidReferenceException {
        this(blamed, model, new Object[] { tip }, env);
    }

    NonSequenceException(
            Expression blamed, TemplateModel model, Object[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "sequence", EXPECTED_TYPES, tips, env);
    }    
    
}
