/*
 * Copyright (c) 2006,2007 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc.
 * Use is subject to license terms.
 */
package com.yukthitech.autox.jexpr;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Used when an error occurs while parsing json expressions.
 * @author akiran
 */
public class JsonExpressionException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new json expression exception.
	 *
	 * @param path the path
	 * @param mssgTemplate the mssg template
	 * @param args the args
	 */
	public JsonExpressionException(String path, String mssgTemplate, Object... args)
	{
		super(String.format(mssgTemplate, args) + "\n Path: " + path, getRootCause(args));
	}
}
