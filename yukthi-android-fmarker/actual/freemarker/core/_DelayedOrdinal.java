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

/** 1 to "1st", 2 to "2nd", etc. */
public class _DelayedOrdinal extends _DelayedConversionToString {

    public _DelayedOrdinal(Object object) {
        super(object);
    }

    @Override
    protected String doConversion(Object obj) {
        if (obj instanceof Number) {
            long n = ((Number) obj).longValue();
            if (n % 10 == 1 && n % 100 != 11) {
                return n + "st";
            } else if (n % 10 == 2 && n % 100 != 12) {
                return n + "nd";
            } else if (n % 10 == 3 && n % 100 != 13) {
                return n + "rd";
            } else {
                return n + "th";
            }
        } else {
            return "" + obj;
        }
    }
    
}
