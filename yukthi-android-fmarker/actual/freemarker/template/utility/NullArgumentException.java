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
package freemarker.template.utility;

/**
 * Indicates that an argument that must be non-{@code null} was {@code null}. 
 * 
 * @since 2.3.20
 */
public class NullArgumentException extends IllegalArgumentException {

    public NullArgumentException() {
        super("The argument can't be null");
    }
    
    public NullArgumentException(String argumentName) {
        super("The \"" + argumentName + "\" argument can't be null");
    }

    public NullArgumentException(String argumentName, String details) {
        super("The \"" + argumentName + "\" argument can't be null. " + details);
    }
    
    /**
     * Convenience method to protect against a {@code null} argument.
     */
    public static void check(String argumentName, Object argumentValue) {
        if (argumentValue == null) {
            throw new NullArgumentException(argumentName);
        }
    }

    /**
     * @since 2.3.22
     */
    public static void check(Object argumentValue) {
        if (argumentValue == null) {
            throw new NullArgumentException();
        }
    }
    
}
