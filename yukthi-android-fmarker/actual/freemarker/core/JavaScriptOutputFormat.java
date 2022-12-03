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
 * Represents the JavaScript output format (MIME type "application/javascript", name "JavaScript"). This format doesn't
 * support escaping.
 * 
 * @since 2.3.24
 */
public class JavaScriptOutputFormat extends OutputFormat {

    /**
     * The only instance (singleton) of this {@link OutputFormat}.
     */
    public static final JavaScriptOutputFormat INSTANCE = new JavaScriptOutputFormat();
    
    private JavaScriptOutputFormat() {
        // Only to decrease visibility
    }
    
    @Override
    public String getName() {
        return "JavaScript";
    }

    @Override
    public String getMimeType() {
        return "application/javascript";
    }

    @Override
    public boolean isOutputFormatMixingAllowed() {
        return false;
    }

}
