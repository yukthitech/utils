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

/** Don't use this; used internally by FreeMarker, might changes without notice. */
public class _DelayedGetMessage extends _DelayedConversionToString {

    public _DelayedGetMessage(Throwable exception) {
        super(exception);
    }

    @Override
    protected String doConversion(Object obj) {
        final String message = ((Throwable) obj).getMessage();
        return message == null || message.length() == 0 ? "[No exception message]" : message;
    }
    
}
