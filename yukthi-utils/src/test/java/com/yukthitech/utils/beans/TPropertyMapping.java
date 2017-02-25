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

package com.yukthitech.utils.beans;


import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author akiran
 *
 */
public class TPropertyMapping
{
	
	@Test
	public void testCopyToNested()
	{
		ModelBean model = new ModelBean(20L, "test", 100L, new Date(), new Date(), new FileModel(1000L, "file1000"));
		EntityBean entityBean = new EntityBean();
		
		PropertyMapper.copyProperties(entityBean, model);
		
		Assert.assertEquals(entityBean.getId(), (Long)20L);
		Assert.assertEquals(entityBean.getEntityName(), "test");
		Assert.assertEquals(entityBean.getSubprop().getId(), 100L);
		Assert.assertEquals(entityBean.getCreatedOn(), model.getCreatedOn());
		
		Assert.assertEquals(entityBean.getFile().getName(), "file1000");
		Assert.assertEquals(entityBean.getFile().getId(), (Long)1000L);
	}

	@Test
	public void testCopyFromNested()
	{
		EntityBean entityBean = new EntityBean(30L, "test", new EntitySubbean(200L), new Date(), new FileEntity(2000L, "file2000"));
		ModelBean modelBean = new ModelBean();
		
		PropertyMapper.copyProperties(modelBean, entityBean);
		
		Assert.assertEquals(modelBean.getId(), (Long)30L);
		Assert.assertEquals(modelBean.getName(), "test");
		Assert.assertEquals(modelBean.getSubpropId(), 200L);
		Assert.assertEquals(modelBean.getCreatedOn(), entityBean.getCreatedOn());
		Assert.assertNull(modelBean.getUpdatedOn());
		
		Assert.assertEquals(modelBean.getFile().getId(), 2000L);
		Assert.assertEquals(modelBean.getFile().getName(), "file2000");
	}
}
