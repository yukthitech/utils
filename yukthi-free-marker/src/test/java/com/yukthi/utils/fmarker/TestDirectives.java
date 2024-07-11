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
package com.yukthi.utils.fmarker;

import java.util.Collections;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.utils.fmarker.FreeMarkerEngine;

public class TestDirectives
{
	private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	@Test
	public void testTrim()
	{
		String content = "Content: <@trim>  This is first line\n"
				+ "  This is second line   </@trim>.";
		
		String result = freeMarkerEngine.processTemplate("content", content, Collections.emptyMap());
		System.out.println(result);
		
		Assert.assertEquals(result, "Content: This is first line\n"
				+ "  This is second line.");
	}

	@Test
	public void testIndent_withoutPrefix()
	{
		String content = "Content: <@indent>  This is first line\n"
				+ "  This is second line   </@indent>.";
		
		String result = freeMarkerEngine.processTemplate("content", content, Collections.emptyMap());
		System.out.println(result);
		
		Assert.assertEquals(result, "Content: This is first lineThis is second line.");
	}

	@Test
	public void testIndent_withPrefix()
	{
		String content = "Content: <@indent prefix='\t'>  This is first line\n"
				+ "  This is second line   </@indent>.";
		
		String result = freeMarkerEngine.processTemplate("content", content, Collections.emptyMap());
		System.out.println(result);
		
		Assert.assertEquals(result, "Content: \tThis is first line\tThis is second line.");
	}

	@Test
	public void testIndent_retainLineBreaks()
	{
		String content = "Content: <@indent prefix='\t' retainLineBreaks=true>  This is first line\n"
				+ "  This is second line   </@indent>.";
		
		String result = freeMarkerEngine.processTemplate("content", content, Collections.emptyMap());
		System.out.println(result);
		
		Assert.assertEquals(result, "Content: \tThis is first line\n\tThis is second line.");
	}
}
