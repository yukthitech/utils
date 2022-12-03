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
package com.yukthitech.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author akiran
 */
public class TMessageFormatter
{
	@Test
	public void testWithoutIndexes()
	{
		Assert.assertEquals(MessageFormatter.format("one {} three {} five", "two", "4"), "one two three 4 five");
		Assert.assertEquals(MessageFormatter.format("one {} three {} five {}", "two", "4"), "one two three 4 five " + MessageFormatter.UNDEFINED);
	}

	@Test
	public void testWithIndexes()
	{
		Assert.assertEquals(MessageFormatter.format("one {0} three {1} five", "two", "4"), "one two three 4 five");
		Assert.assertEquals(MessageFormatter.format("one {0} three {1} five {2}", "two", "4"), "one two three 4 five " + MessageFormatter.UNDEFINED);
		Assert.assertEquals(MessageFormatter.format("one {0} three {1} five {1}", "two", "4"), "one two three 4 five 4");
	}

	@Test
	public void testWithMixed()
	{
		Assert.assertEquals(MessageFormatter.format("one {0} three {} five", "two", "4"), "one two three 4 five");
		Assert.assertEquals(MessageFormatter.format("one {} three {1} five {}", "two", "4"), "one two three 4 five " + MessageFormatter.UNDEFINED);
		Assert.assertEquals(MessageFormatter.format("one {1} three {1} five {1}", "two", "4"), "one 4 three 4 five 4");
	}
}
