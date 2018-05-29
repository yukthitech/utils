package com.yukthitech.utils.fmarker.directives;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Makes all words to start with capital letter.
 * @author akiran
 */
public class InitCapDirective implements TemplateDirectiveModel
{
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
	{
		StringWriter writer = new StringWriter();
		body.render(writer);

		String actualOutput = writer.toString();

		Pattern wordPattern = Pattern.compile("\\w+");
		Matcher matcher = wordPattern.matcher(actualOutput);
		String word = null;
		StringBuilder res = new StringBuilder();

		while(matcher.find())
		{
			word = matcher.group();
			word = word.toLowerCase();

			word = ("" + word.charAt(0)).toUpperCase() + word.substring(1);
		}

		env.getOut().append(res.toString());
	}
}
