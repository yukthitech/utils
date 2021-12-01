package com.yukthitech.autox.ide.model;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.ide.rest.MultiPart;

public class RestInvocationModel
{
	String uri;
	List<Header> headers= new ArrayList<>();
	List<Param> paramList = new ArrayList<>();
	String rawBody;
	List<PathVariable> pathVariables= new ArrayList<>();
	List<MultiPart> multiPartlist= new ArrayList<>();
	public String getUri()
	{
		return uri;
	}
	public void setUri(String uri)
	{
		this.uri = uri;
	}
	public List<Header> getHeaders()
	{
		return headers;
	}
	public void setHeaders(List<Header> headers)
	{
		this.headers = headers;
	}
	
	public List<Param> getParamList()
	{
		return paramList;
	}
	public void setParamList(List<Param> paramList)
	{
		this.paramList = paramList;
	}
	public String getRawBody()
	{
		return rawBody;
	}
	public void setRawBody(String rawBody)
	{
		this.rawBody = rawBody;
	}
	public List<PathVariable> getPathVariables()
	{
		return pathVariables;
	}
	public void setPathVariables(List<PathVariable> pathVariables)
	{
		this.pathVariables = pathVariables;
	}
	public List<MultiPart> getMultiPartlist()
	{
		return multiPartlist;
	}
	public void setMultiPartlist(List<MultiPart> multiPartlist)
	{
		this.multiPartlist = multiPartlist;
	}
	
		
}
