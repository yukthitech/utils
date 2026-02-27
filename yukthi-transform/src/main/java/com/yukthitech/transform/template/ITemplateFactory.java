package com.yukthitech.transform.template;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

public interface ITemplateFactory
{
	public default TransformTemplate parseTemplateFromResource(String resourcePath)
	{
		String content = null;
		
		try
		{
			content = IOUtils.resourceToString(resourcePath, Charset.defaultCharset());
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to load resource: {}", resourcePath, ex);
		}

		return parseTemplate(resourcePath, content);
	}

	public default TransformTemplate parseTemplateFromFile(File file)
	{
		String content = null;
		
		try
		{
			content = FileUtils.readFileToString(file, Charset.defaultCharset());
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to load file: {}", file.getAbsolutePath(), ex);
		}

		return parseTemplate(file.getPath(), content);
	}

	/**
	 * Parses the template content and returns a TransformTemplate object.
	 * @param name Name of the template.
	 * @param templateContent Content of the template.
	 * @return TransformTemplate object.
	 */
	public TransformTemplate parseTemplate(String name, String templateContent);
}
