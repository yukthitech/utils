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
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

abstract class BuiltInForSequence extends BuiltIn {
    @Override
    TemplateModel _eval(Environment env)
            throws TemplateException {
        TemplateModel model = target.eval(env);
        if (!(model instanceof TemplateSequenceModel)) {
            throw new NonSequenceException(target, model, env);
        }
        return calculateResult((TemplateSequenceModel) model);
    }
    abstract TemplateModel calculateResult(TemplateSequenceModel tsm)
    throws TemplateModelException;
}