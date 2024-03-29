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
package freemarker.template;

import java.util.List;

import freemarker.template.utility.Constants;

/**
 * Singleton object representing nothing, used by ?if_exists built-in.
 * It is meant to be interpreted in the most sensible way possible in various contexts.
 * This can be returned to avoid exceptions.
 */

final class GeneralPurposeNothing
implements TemplateBooleanModel, TemplateScalarModel, TemplateSequenceModel, TemplateHashModelEx2, TemplateMethodModelEx {

    private static final TemplateModel instance = new GeneralPurposeNothing();

    private GeneralPurposeNothing() {
    }

    static TemplateModel getInstance() {
        return instance;
    }

    public String getAsString() {
        return "";
    }

    public boolean getAsBoolean() {
        return false;
    }

    public boolean isEmpty() {
        return true;
    }

    public int size() {
        return 0;
    }

    public TemplateModel get(int i) throws TemplateModelException {
        throw new TemplateModelException("Can't get item from an empty sequence.");
    }

    public TemplateModel get(String key) {
        return null;
    }

    public Object exec(List args) {
        return null;
    }
    
    public TemplateCollectionModel keys() {
        return Constants.EMPTY_COLLECTION;
    }

    public TemplateCollectionModel values() {
        return Constants.EMPTY_COLLECTION;
    }

    public KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
        return Constants.EMPTY_KEY_VALUE_PAIR_ITERATOR;
    }
}
