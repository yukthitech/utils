package com.yukthi.indexer.es;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.yukthi.indexer.UpdateField;
import com.yukthi.indexer.UpdateOperation;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.exceptions.InvalidConfigurationException;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Index object update query builder.
 * @author akiran
 */
public class UpdateQueryBuilder
{
	/**
	 * Builds the update query for specified index from specified update data.
	 * @param typeIndexDetails
	 * @param updateData
	 * @return update query as map
	 */
	public static Object buildQuery(TypeIndexDetails typeIndexDetails, Object updateData)
	{
		Field fields[] = updateData.getClass().getDeclaredFields();
		UpdateField updateField = null;
		Object value = null;
		String name = null;
		
		TypeIndexDetails.FieldIndexDetails fieldDetails = null;
		
		StringBuilder script = new StringBuilder("def jsonSlurper = new groovy.json.JsonSlurper()\n");
		script.append("def sourceObj = jsonSlurper.parseText(ctx._source.").append(EsDataIndex.OBJECT_FIELD).append(")\n");
		
		Map<String, Object> params = new HashMap<>();
		
		UpdateOperation op = null;
		
		//loop through query data fields
		for(Field field : fields)
		{
			updateField = field.getAnnotation(UpdateField.class);
			
			//if field is not marked with annotation, ignore
			if(updateField == null)
			{
				continue;
			}
			
			//Ensure proper index field match is configured
			name = updateField.name();
			name = StringUtils.isEmpty(name) ? field.getName() : name;
			
			fieldDetails = typeIndexDetails.getField(name);
			
			if(fieldDetails == null)
			{
				throw new InvalidConfigurationException("Invalid index field name '{}' for index-type '{}', is specified on update-data field - {}.{}", 
						name, typeIndexDetails.getType().getName(), updateData.getClass().getName(), field.getName());
			}

			//fetch the value and ignore fields with null values
			try
			{
				value = PropertyUtils.getProperty(updateData, field.getName());
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while fetching field value - {}.{}", updateData.getClass().getName(), field.getName());
			}
			
			if(value == null)
			{
				continue;
			}
			
			//fetch update operator and make it compatible with target data type
			op = updateField.op();
			
			if(fieldDetails.getEsDataType() == EsDataType.STRING)
			{
				op = (op == UpdateOperation.APPEND) ? UpdateOperation.APPEND : UpdateOperation.REPLACE;
			}
			else if(fieldDetails.getEsDataType() == EsDataType.BOOLEAN)
			{
				op = UpdateOperation.REPLACE;
			}
			else if(fieldDetails.getEsDataType() == EsDataType.DATE)
			{
				op = UpdateOperation.REPLACE;
			}
			
			//add to params and script
			params.put("new_" + name, value);
			script.append("ctx._source." + name + op.getOperator() + "new_" + name).append("\n");
			script.append("sourceObj." + name + op.getOperator() + "new_" + name).append("\n");
		}
		
		script.append("ctx._source.").append(EsDataIndex.OBJECT_FIELD).append("=JsonOutput.toJson(sourceObj)\n");
		
		return CommonUtils.toMap("script", script.toString(), "params", params, "lang", "groovy");
	}
}
