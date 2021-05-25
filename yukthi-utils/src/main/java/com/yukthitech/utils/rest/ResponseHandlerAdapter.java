package com.yukthitech.utils.rest;

import java.io.IOException;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

class ResponseHandlerAdapter<T> implements HttpClientResponseHandler<T>
{
	private IRestResponseHandler<T> actualHandler;
	
	public ResponseHandlerAdapter(IRestResponseHandler<T> actualHandler)
	{
		this.actualHandler = actualHandler;
	}

	@Override
	public T handleResponse(ClassicHttpResponse response) throws HttpException, IOException
	{
		return actualHandler.handleResponse(new HttpResponse(response));
	}
}
