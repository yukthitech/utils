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

import java.util.AbstractSet;

/** Don't use this; used internally by FreeMarker, might changes without notice. */
public abstract class _UnmodifiableSet<E> extends AbstractSet<E> {

    @Override
    public boolean add(E o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        if (contains(o)) {
            throw new UnsupportedOperationException();
        }
        return false;
    }

    @Override
    public void clear() {
        if (!isEmpty()) {
            throw new UnsupportedOperationException();
        }
    }

}
