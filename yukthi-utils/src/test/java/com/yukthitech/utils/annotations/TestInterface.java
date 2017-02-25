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

package com.yukthitech.utils.annotations;

/**
 * @author akiran
 *
 */
public interface TestInterface
{
	@SearchResult(count = 10, 
			mappings = {
					@Mapping(field = "field1", property = "property1")
			}, 
			returnMapping = @Mapping(field = "retField1", property = "retProperty1")
	)
	public void directAnnotation();
	
	@LovQuery1
	public void recursiveAnnotaion();
	
	@LovQuery2(count = 30)
	public void simplePropOverride();

	@LovQuery3(returnField = "retField4")
	public void nestedPropPropOverride();

	@LovQuery4(count = 50, mappingField = "field5", returnField = "retField5")
	public void arrayPropOverride();
	
	@LovQuery5(field = "field6")
	public void multiOverride();
	
	@SuppressRecursiveSearch(SearchResult.class)
	@LovQuery1
	public void suppressMethod1();
	
	@SuppressRecursiveSearch(SearchResult.class)
	@LovQuery1
	@SearchResult(count = 20, 
		mappings = { @Mapping(field = "field2", property = "property2") }, 
		returnMapping = @Mapping(field = "retField2", property = "retProperty2")
	)
	public void suppressMethod2();
}
