package com.yukthitech.autox.ide.format;

public enum IndentAction
{
	PRE_INCR_INDENT
	{
		public String alterIndet(boolean pre, String currentIndent)
		{
			return pre ? currentIndent + "\t" : currentIndent;
		}
	},
	
	POST_INCR_INDENT
	{
		public String alterIndet(boolean pre, String currentIndent)
		{
			return pre ? currentIndent : currentIndent + "\t";
		}
	},
	
	PRE_DECR_INDENT
	{
		public String alterIndet(boolean pre, String currentIndent)
		{
			if(!pre || currentIndent.length() <= 0)
			{
				return currentIndent;
			}
			
			return currentIndent.substring(1);
		}
	},
	
	POST_DECR_INDENT
	{
		public String alterIndet(boolean pre, String currentIndent)
		{
			if(pre || currentIndent.length() <= 0)
			{
				return currentIndent;
			}
			
			return currentIndent.substring(1);
		}
	},
	
	;
	
	public abstract String alterIndet(boolean pre, String currentIndent);
}
