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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Unlike {@link DefaultIteratorAdapter}, this doesn't adapt to some {@link TemplateModel}, but to {@link
 * TemplateModelIterator}.
 */
class IteratorToTemplateModelIteratorAdapter implements TemplateModelIterator {

    private final Iterator<?> it;
    private final ObjectWrapper wrapper;

    IteratorToTemplateModelIteratorAdapter(Iterator<?> it, ObjectWrapper wrapper) {
        this.it = it;
        this.wrapper = wrapper;
    }

    public TemplateModel next() throws TemplateModelException {
        try {
            return wrapper.wrap(it.next());
        } catch (NoSuchElementException e) {
            throw new TemplateModelException("The collection has no more items.", e);
        }
    }

    public boolean hasNext() throws TemplateModelException {
        return it.hasNext();
    }

}