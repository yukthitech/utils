package com.yukthitech.persistence.rdbms;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.persistence.freemarker.ParamCollectorDirective;
import com.yukthitech.persistence.freemarker.TrimDirective;
import com.yukthitech.utils.CommonUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class RdbmsConfiguration implements Validateable
{
	public static final String COMMON_CODE = "#commonCode";
	
	public static final String CHECK_SEQUENCE_QUERY = "checkSequenceTemplate";
	public static final String CREATE_SEQUENCE_QUERY = "createSequenceTemplate";
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

	public static final String MANDATORY_QUERIES[] = {
		CREATE_TABLE, CREATE_INDEX, CREATE_EXTENDED_TABLE,
		
		SAVE_QUERY, UPDATE_QUERY, DELETE_QUERY, FINDER_QUERY, AGGREGATE_QUERY, 
		
		CHILDREN_EXISTENCE_QUERY, FETCH_CHILDREN_IDS_QUERY,
		
		DROP_QUERY
	};

	private Map<String, String> queryMap = new HashMap<>();
	private Map<String, Template> templateMap = new HashMap<>();
	
	@SuppressWarnings("deprecation")
	private Configuration configuration = new Configuration();
	
	private ParamCollectorDirective paramCollectorDirective = new ParamCollectorDirective();
	
	/**
	 * Indicates whether the target DB supports paging or not
	 */
	private boolean pagingSupported = true;
	
	public RdbmsConfiguration()
	{
		configuration.setSharedVariable("trim", new TrimDirective());
		configuration.setSharedVariable("param", paramCollectorDirective);
	}
	
	public void addTemplate(String name, String template)
	{
		queryMap.put(name, template);
	}
	
	private boolean isEmptyQuery(String name)
	{
		String query = queryMap.get(name);
		return (query == null || query.trim().length() == 0);
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

	@Override
	public void validate() throws ValidateException
	{
		for(String query: MANDATORY_QUERIES)
		{
			if(isEmptyQuery(query))
			{
				throw new ValidateException("'" + query + "' template can not be null or empty");
			}
		}
		
		String commonCode = queryMap.remove(COMMON_CODE);
		
		if(commonCode != null)
		{
			String names[] = queryMap.keySet().toArray(new String[0]);
			
			for(String name : names)
			{
				queryMap.put(name, commonCode + "\n" + queryMap.get(name));
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
		return buildQuery(name, null, CommonUtils.toMap(contextEntries));
	}
	
	public String buildQuery(String name, List<Object> paramValues, Object... contextEntries)
	{
		return buildQuery(name, paramValues, CommonUtils.toMap(contextEntries));
	}
	
	public String buildQuery(String name, List<Object> paramValues, Map<String, Object> context)
	{
		Template template = templateMap.get(name);
		
		if(template == null)
		{
			try
			{
				template = new Template(name, queryMap.get(name), configuration);
				templateMap.put(name, template);
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while loading delete query template", ex);
			}
		}
		
		try
		{
			paramCollectorDirective.reset(context);
			
			StringWriter writer = new StringWriter();
			template.process(context, writer);
			
			if(paramValues != null)
			{
				paramValues.addAll(paramCollectorDirective.getParamValues());
			}
			
			writer.flush();
			return writer.toString();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An exception occurred while building query: " + name, ex);
		}
	}
}
