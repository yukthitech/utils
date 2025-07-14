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
package com.yukthitech.persistence.rdbms;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.conversion.IImplicitCoverterProvider;
import com.yukthitech.persistence.conversion.IPersistenceConverter;
import com.yukthitech.persistence.freemarker.TrimDirective;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class RdbmsConfiguration implements Validateable, IImplicitCoverterProvider
{
	public static final String COMMON_CODE = "#commonCode";
	
	public static final String CREATE_TABLE = "createTableTemplate";
	public static final String CREATE_EXTENDED_TABLE = "createExtendedTableTemplate";
	public static final String CREATE_INDEX = "createIndexTemplate";
	public static final String SAVE_QUERY = "saveTemplate";
	public static final String UPDATE_QUERY = "updateTemplate";
	public static final String SAVE_UPDATE_QUERY = "saveUpdateTemplate";
	public static final String DELETE_QUERY = "deleteTemplate";
	public static final String FINDER_QUERY = "finderTemplate";
	public static final String AGGREGATE_QUERY = "aggregateTemplate";
	public static final String CHILDREN_EXISTENCE_QUERY = "childrenExistenceTemplate";
	public static final String FETCH_CHILDREN_IDS_QUERY = "fetchChildrenIdsTemplate";
	public static final String DROP_QUERY = "dropTableTemplate";

	public static final String AUTO_ID_COVERSION_QUERY = "autoIdConversionQuery";

	public static final String PATTERN_GRP_CONST_ERR_NAME = "name";

	public static final String MANDATORY_QUERIES[] = {
		CREATE_TABLE, CREATE_INDEX, CREATE_EXTENDED_TABLE,
		
		SAVE_QUERY, UPDATE_QUERY, DELETE_QUERY, FINDER_QUERY, AGGREGATE_QUERY, 
		
		CHILDREN_EXISTENCE_QUERY, FETCH_CHILDREN_IDS_QUERY,
		
		DROP_QUERY
	};
	
	private static Configuration configuration = new Configuration(Configuration.getVersion());
	
	static
	{
		configuration.setSharedVariable("trim", new TrimDirective());
	}
	
	/**
	 * Represents step of a query.
	 * @author akiran
	 */
	public static class QueryStep
	{
		/**
		 * Query template.
		 */
		private String template;
		
		/**
		 * Flag indicating if error by this query step can be ignored.
		 */
		private boolean ignoreOnError = false;
		
		private Template freemarkerTemplate;

		public QueryStep()
		{}
		
		public QueryStep(String template)
		{
			this.template = template;
		}
		
		public void setTemplate(String template)
		{
			this.template = template;
		}
		
		public void setIgnoreOnError(boolean ignoreOnError)
		{
			this.ignoreOnError = ignoreOnError;
		}
		
		public String getTemplate()
		{
			return template;
		}
		
		public boolean isIgnoreOnError()
		{
			return ignoreOnError;
		}
		
		private void addCommonCode(String code)
		{
			this.template = code + "\n" + template;
		}

		public String buildQuery(String name, Object... contextEntries)
		{
			if(freemarkerTemplate == null)
			{
				try
				{
					freemarkerTemplate = new Template(name, template, configuration);
				}catch(Exception ex)
				{
					throw new IllegalStateException("An error occurred while loading query template", ex);
				}
			}
			
			try
			{
				Map<String, Object> context = CommonUtils.toMap(contextEntries);
				StringWriter writer = new StringWriter();
				freemarkerTemplate.process(context, writer);
				
				writer.flush();
				return writer.toString();
			}catch(Exception ex)
			{
				throw new IllegalStateException("An exception occurred while building query: " + name, ex);
			}
		}
	}
	
	/**
	 * Represents a query with steps.
	 * @author akiran
	 */
	public static class Query
	{
		private String name;
		
		private List<QueryStep> steps = new ArrayList<>();
		
		public Query()
		{}
		
		public Query(String name, String template)
		{
			this.name = name;
			this.steps.add(new QueryStep(template));
		}

		public void setName(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
		
		public void addStep(QueryStep step)
		{
			this.steps.add(step);
		}
		
		public List<QueryStep> getSteps()
		{
			return steps;
		}
		
		private void addCommonCode(String code)
		{
			for(QueryStep step : steps)
			{
				step.addCommonCode(code);
			}
		}
	}

	private Map<String, Query> queryMap = new HashMap<>();
	
	/**
	 * Indicates whether the target DB supports paging or not
	 */
	private boolean pagingSupported = true;
	
	/**
	 * Flag indicating if unique id column generation is required, which in turn is used for id value fetching.
	 */
	private boolean uniqueIdColumnRequired = false;
	
	/**
	 * Pattern to be used to extract constraint name from constraint exception messages. This pattern must have 
	 * group "name" which will be used to extract constraint name.
	 */
	private List<Pattern> constraintErrorPatterns;
	
	/**
	 * Implicit converters to be used for this db configuration to interact with db. 
	 */
	private Map<DataType, IPersistenceConverter> dbImplicitConverters = new HashMap<>();
	
	/**
	 * Flag to be enabled if lower case table names to be used while querying for
	 * table meta data.
	 */
	private boolean lowerCaseNames = false;
	
	public void addConstraintErrorPattern(String constraintErrorPattern)
	{
		constraintErrorPattern = constraintErrorPattern.trim();
		
		if(!constraintErrorPattern.contains("?<" + PATTERN_GRP_CONST_ERR_NAME + ">"))
		{
			throw new InvalidArgumentException("No group found with name 'name' in specified pattern: {}", constraintErrorPattern);
		}
		
		if(this.constraintErrorPatterns == null)
		{
			this.constraintErrorPatterns = new ArrayList<>();
		}
		
		this.constraintErrorPatterns.add( Pattern.compile(constraintErrorPattern) );
	}
	
	public void addImplicitConverter(DataType dataType, IPersistenceConverter converter)
	{
		this.dbImplicitConverters.put(dataType, converter);
	}
	
	public IPersistenceConverter getImplicitConverter(DataType javaType)
	{
		return this.dbImplicitConverters.get(javaType);
	}
	
	public boolean isUniqueIdColumnRequired()
	{
		return uniqueIdColumnRequired;
	}
	
	public List<Pattern> getConstraintErrorPatterns()
	{
		return constraintErrorPatterns;
	}
	
	public void addTemplate(String name, String template)
	{
		queryMap.put(name, new Query(name, template));
	}
	
	public void addQuery(Query query)
	{
		queryMap.put(query.getName(), query);
	}
	
	/**
	 * Checks if is indicates whether the target DB supports paging or not.
	 *
	 * @return the indicates whether the target DB supports paging or not
	 */
	public boolean isPagingSupported()
	{
		return pagingSupported;
	}

	/**
	 * Sets the indicates whether the target DB supports paging or not.
	 *
	 * @param pagingSupported the new indicates whether the target DB supports paging or not
	 */
	public void setPagingSupported(boolean pagingSupported)
	{
		this.pagingSupported = pagingSupported;
	}
	
	/**
	 * Checks if is flag to be enabled if lower case table names to be used while querying for table meta data.
	 *
	 * @return the flag to be enabled if lower case table names to be used while querying for table meta data
	 */
	public boolean isLowerCaseNames()
	{
		return lowerCaseNames;
	}

	/**
	 * Sets the flag to be enabled if lower case table names to be used while querying for table meta data.
	 *
	 * @param lowerCaseNames the new flag to be enabled if lower case table names to be used while querying for table meta data
	 */
	public void setLowerCaseNames(boolean lowerCaseNames)
	{
		this.lowerCaseNames = lowerCaseNames;
	}

	@Override
	public void validate() throws ValidateException
	{
		for(String query: MANDATORY_QUERIES)
		{
			if(!queryMap.containsKey(query))
			{
				throw new ValidateException("'" + query + "' template can not be null or empty");
			}
		}
		
		Query commonCode = queryMap.remove(COMMON_CODE);
		
		if(commonCode != null)
		{
			String commonCodeTemplate = commonCode.getSteps().get(0).getTemplate();
			
			for(Query query : queryMap.values())
			{
				query.addCommonCode(commonCodeTemplate);
			}
		}
	}
	
	/**
	 * Checks if a query is configured with specified name
	 * @param name
	 * @return
	 */
	public boolean hasQuery(String name)
	{
		return queryMap.containsKey(name);
	}
	
	public String buildQuery(String name, Object... contextEntries)
	{
		Query query = queryMap.get(name);
		return query.steps.get(0).buildQuery(name, contextEntries);
	}
	
	/**
	 * Fetches query with specified name.
	 * @param name
	 * @return
	 */
	public Query getQuery(String name)
	{
		return queryMap.get(name);
	}
}
