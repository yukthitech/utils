package com.yukthi.persistence.freemarker;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthi.utils.exceptions.InvalidStateException;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Freemarker directive to collect parameter values that needs to be passed as dynamic values for 
 * prepared statement. 
 * 
 * In final query, this directive will be replaced with question mark (?) and during execution collected param values needs
 * to be passed to prepared statement parameters using {@link PreparedStatement#setObject(int, Object)}
 * 
 * @author akiran
 */
public class ParamCollectorDirective implements TemplateDirectiveModel
{
	/**
	 * List of parameter values to be passed
	 */
	private List<Object> paramValues = new ArrayList<Object>(20);
	
	private Object context;
	
	/* (non-Javadoc)
	 * @see freemarker.template.TemplateDirectiveModel#execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[], freemarker.template.TemplateDirectiveBody)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
	{
		String name = params.get("name").toString();
		
		try
		{
			paramValues.add(PropertyUtils.getProperty(context, name));
		}catch(Exception e)
		{
			throw new InvalidStateException(e, "An error occurred while fetching value for property - {}", name);
		}

		env.getOut().append("?");
	}
	
	/**
	 * Resets this directive and sets specified context and free marker context (for fetching propery values)
	 * @param context
	 */
	public void reset(Object context)
	{
		this.context = context;
		paramValues.clear();
	}
	
	/**
	 * Returns collected parameter values
	 * @return
	 */
	public List<Object> getParamValues()
	{
		return paramValues;
	}
}
