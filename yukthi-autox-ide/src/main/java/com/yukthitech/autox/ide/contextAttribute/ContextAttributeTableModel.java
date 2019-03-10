package com.yukthitech.autox.ide.contextAttribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.monitor.ienv.ContextAttributeDetails;

public class ContextAttributeTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(ContextAttributeTableModel.class);

	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static
	{
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	private static String columnNames[] = new String[] { "Key", "Value" };
	
	private List<String[]> attributes = new ArrayList<>();

	public ContextAttributeTableModel()
	{
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
	
	public void reload(ExecutionEnvironment environment)
	{
		this.attributes.clear();
		
		if(environment == null)
		{
			super.fireTableDataChanged();
			return;
		}
		
		Collection<ContextAttributeDetails> attrLst = environment.getContextAttributes();
		
		if(attrLst != null)
		{
			for(ContextAttributeDetails attr : attrLst)
			{
				attributes.add(new String[] {attr.getName(), toString(attr.getValue())});
			}
		}
		
		super.fireTableDataChanged();
	}
	
	private String toString(Object val)
	{
		if(val == null)
		{
			return "";
		}
		
		if(val instanceof String)
		{
			return (String) val;
		}
		
		try
		{
			return objectMapper.writeValueAsString(val);
		} catch(JsonProcessingException e)
		{
			logger.warn("Failed to convert object into json [Object: {}, Error: {}]", val, "" + e);
			return val.toString();
		}
	}
	
	public void addContextAttribute(ContextAttributeDetails attr)
	{
		int rowIdx = 0, rowToDel = -1;
		
		for(String[] row : this.attributes)
		{
			if(attr.getName().equals(row[0]))
			{
				rowToDel = rowIdx;
				break;
			}
			
			rowIdx++;
		}
		
		if(rowToDel >= 0)
		{
			this.attributes.remove(rowToDel);
			super.fireTableRowsDeleted(rowToDel, rowToDel);
		}
		
		this.attributes.add(new String[] {attr.getName(), toString(attr.getValue())} );
		
		int lastIdx = this.attributes.size() - 1;
		super.fireTableRowsInserted(lastIdx, lastIdx);
	}
	
	@Override
	public int getRowCount()
	{
		return attributes.size();
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column)
	{
		return columnNames[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		String attr[] = attributes.get(rowIndex);
		return attr[columnIndex];
	}
}
