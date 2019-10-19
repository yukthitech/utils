package com.yukthitech.persistence.conversion;

import com.yukthitech.persistence.annotations.DataType;

/**
 * Abstraction which can provide implicit conversion based on target storage.
 * @author akiran
 */
public interface IImplicitCoverterProvider
{
	/**
	 * Invoked to get implicit converter, if any, for specified data type.
	 * @param dbDataType
	 * @return
	 */
	public IPersistenceConverter getImplicitConverter(DataType dbDataType);
}
