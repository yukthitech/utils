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

import freemarker.log.Logger;
import freemarker.template.Version;
import freemarker.template.utility.SecurityUtilities;

/**
 * Used internally only, might changes without notice!
 */
public final class _JavaVersions {
    
    private _JavaVersions() {
        // Not meant to be instantiated
    }

    private static final boolean IS_AT_LEAST_6;
    static {
        boolean result = false;
        String vStr = SecurityUtilities.getSystemProperty("java.version", null);
        if (vStr != null) {
            try {
                Version v = new Version(vStr);
                result = v.getMajor() == 1 && v.getMinor() >= 6 || v.getMajor() > 1;
            } catch (Exception e) {
                // Ignore
            }
        }
        if (vStr == null) {
            try {
                Class.forName("java.util.ServiceLoader");
                result = true;
            } catch (Exception e) {
                // Ignore
            }
        }
        IS_AT_LEAST_6 = result;
    }
    
    static public final _Java6 JAVA_6;
    static {
        _Java6 java6;
        if (IS_AT_LEAST_6) {
            try {
                java6 = (_Java6) Class.forName("freemarker.core._Java6Impl").getField("INSTANCE").get(null);
            } catch (Exception e) {
                try {
                    Logger.getLogger("freemarker.runtime").error("Failed to access Java 6 functionality", e);
                } catch (Exception e2) {
                    // Suppressed
                }
                java6 = null;
            }
        } else {
            java6 = null;
        }
        JAVA_6 = java6;
    }
    
    private static final boolean IS_AT_LEAST_8;
    static {
        boolean result = false;
        String vStr = SecurityUtilities.getSystemProperty("java.version", null);
        if (vStr != null) {
            try {
                Version v = new Version(vStr);
                result = v.getMajor() == 1 && v.getMinor() >= 8 || v.getMajor() > 1;
            } catch (Exception e) {
                // Ignore
            }
        } else {
            try {
                Class.forName("java.time.Instant");
                result = true;
            } catch (Exception e) {
                // Ignore
            }
        }
        IS_AT_LEAST_8 = result;
    }
    
    /**
     * {@code null} if Java 8 is not available, otherwise the object through with the Java 8 operations are available.
     */
    static public final _Java8 JAVA_8;
    static {
        _Java8 java8;
        if (IS_AT_LEAST_8) {
            try {
                java8 = (_Java8) Class.forName("freemarker.core._Java8Impl").getField("INSTANCE").get(null);
            } catch (Exception e) {
                try {
                    Logger.getLogger("freemarker.runtime").error("Failed to access Java 8 functionality", e);
                } catch (Exception e2) {
                    // Suppressed
                }
                java8 = null;
            }
        } else {
            java8 = null;
        }
        JAVA_8 = java8;
    }
    
}
