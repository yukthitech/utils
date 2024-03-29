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
 * Class that's a little bit more efficient than using an {@code ArrayList<LocalContext>}. 
 * 
 * @since 2.3.24
 */
final class LocalContextStack {

    private LocalContext[] buffer = new LocalContext[8];
    private int size;

    void push(LocalContext localContext) {
        final int newSize = ++size;
        LocalContext[] buffer = this.buffer;
        if (buffer.length < newSize) {
            final LocalContext[] newBuffer = new LocalContext[newSize * 2];
            for (int i = 0; i < buffer.length; i++) {
                newBuffer[i] = buffer[i];
            }
            buffer = newBuffer;
            this.buffer = newBuffer;
        }
        buffer[newSize - 1] = localContext;
    }

    void pop() {
        buffer[--size] = null;
    }

    public LocalContext get(int index) {
        return buffer[index];
    }
    
    public int size() {
        return size;
    }

}
