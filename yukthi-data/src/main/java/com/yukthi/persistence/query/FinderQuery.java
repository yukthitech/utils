package com.yukthi.persistence.query;

import com.yukthi.persistence.EntityDetails;

public class FinderQuery extends AbstractConditionalQuery
{
	public FinderQuery(EntityDetails entityDetails)
	{
		super(entityDetails);
	}
}
