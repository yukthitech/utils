/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.utils;

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
