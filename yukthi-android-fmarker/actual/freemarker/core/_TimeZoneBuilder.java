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

import java.util.TimeZone;

/**
 * For internal use only; don't depend on this, there's no backward compatibility guarantee at all!
 */
public class _TimeZoneBuilder {

    private final String timeZoneId;

    public _TimeZoneBuilder(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public TimeZone build() {
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        if (timeZone.getID().equals("GMT") && !timeZoneId.equals("GMT") && !timeZoneId.equals("UTC")
                && !timeZoneId.equals("GMT+00") && !timeZoneId.equals("GMT+00:00") && !timeZoneId.equals("GMT+0000")) {
            throw new IllegalArgumentException("Unrecognized time zone: " + timeZoneId);
        }
        return timeZone;
    }

}
