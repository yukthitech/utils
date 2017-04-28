package com.yukthi.utils.fmarker.directives;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.StringTokenizer;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Helps in indenting the strings.
 * @author akiran
 */
public class IndentDirective implements TemplateDirectiveModel
{
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
	{
		StringWriter writer = new StringWriter();
		body.render(writer);

		String actualOutput = writer.toString();
		Object prefix = params.get("prefix");

		if(prefix == null || !(prefix instanceof String))
		{
			prefix = "";
		}

		StringTokenizer st = new StringTokenizer(actualOutput, "\n");
		StringBuilder builder = new StringBuilder();
		String line = null;

		while(st.hasMoreTokens())
		{
			line = st.nextToken().trim();
			builder.append(prefix).append(line);
		}

		String output = builder.toString();
		output = output.replace("\\t", "\t");
		output = output.replace("\\n", "\n");

		env.getOut().append(output);
	}
}
