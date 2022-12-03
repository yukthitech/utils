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
