package com.yukthitech.utils.rest;

import java.io.IOException;

/**
 * Used to specify custom response handling.
 * @author akranthikiran
 * @param <T> converted result type
 */
public interface IRestResponseHandler<T>
{
	T handleResponse(HttpResponse response) throws IOException;
}
