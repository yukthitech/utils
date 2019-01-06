package com.yukthitech.autox.ide.xmlfile;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.ShorthandCompletion;

import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.doc.ElementInfo;
import com.yukthitech.autox.doc.ParamInfo;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.doc.ValidationInfo;
import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.ccg.xml.XMLConstants;
import com.yukthitech.ccg.xml.XMLUtil;

public class XmlCompletionProvider extends AbstractCompletionProvider
{
	private static Logger logger = LogManager.getLogger(XmlCompletionProvider.class);
	
	private Project project;
	
	private XmlFileLocation xmlFileLocation;
	
	private FileEditor fileEditor;
	
	public XmlCompletionProvider(Project project, FileEditor fileEditor)
	{
		this.project = project;
		this.fileEditor = fileEditor;
	}
	
	private String getElementReplacementText(StepInfo step, XmlFileLocation location)
	{
		StringBuilder builder = new StringBuilder();
		String nodeName = null;
		
		if(location.getCurrentToken() == null)
		{
			nodeName = step.getNameWithHyphens();
			builder.append("<").append(location.getXmlFile().getPrefixForNamespace(XMLConstants.CCG_URI)).append(":").append(nodeName).append(" ");
		}
		else
		{
			nodeName = step.getNameWithHyphens().startsWith(location.getName()) ? step.getNameWithHyphens() : step.getName();
			builder.append(location.getXmlFile().getPrefixForNamespace(XMLConstants.CCG_URI)).append(":").append(nodeName).append(" ");
			
			if(builder.toString().startsWith(location.getCurrentToken()))
			{
				builder.delete(0, location.getCurrentToken().length());
			}
		}
		
		if(!location.isFullElementGeneration())
		{
			if(builder.charAt(builder.length() - 1) == ' ')
			{
				builder.deleteCharAt(builder.length() - 1);
			}
			
			return builder.toString();
		}
		
		if(step.getParams() != null)
		{
			for(ParamInfo param : step.getParams())
			{
				if(param.isMandatory())
				{
					builder.append(param.getName()).append("=\"\" ");
				}
			}
		}
		
		builder.deleteCharAt(builder.length() - 1);
		
		builder.append(">").append("\n").append(location.getIndentation());
		builder.append("</").append(location.getXmlFile().getPrefixForNamespace(XMLConstants.CCG_URI)).append(":").append(nodeName).append(">");
		
		return builder.toString();
	}
	
	private String getElementReplacementText(ElementInfo step, XmlFileLocation location)
	{
		StringBuilder builder = new StringBuilder();
		String nodeName = null;
		
		if(location.getCurrentToken() == null)
		{
			nodeName = step.getNameWithHyphens();
			builder.append("<").append(nodeName).append(" ");
		}
		else
		{
			nodeName = step.getName().startsWith(location.getName()) ? step.getName() : step.getNameWithHyphens();
			builder.append(nodeName).append(" ");
			
			if(builder.toString().startsWith(location.getCurrentToken()))
			{
				builder.delete(0, location.getCurrentToken().length());
			}
		}
		
		if(step.getParams() != null)
		{
			for(ParamInfo param : step.getParams())
			{
				if(param.isMandatory())
				{
					builder.append(param.getName()).append("=\"\" ");
				}
			}
		}
		
		if(step.getKeyName() != null)
		{
			builder.append(step.getKeyName()).append("=\"\"");
			builder.append(">").append("</").append(nodeName).append(">");
		}
		else
		{
			builder.deleteCharAt(builder.length() - 1);
			
			builder.append(">").append("\n").append(location.getIndentation());
			builder.append("</").append(nodeName).append(">");
		}
		
		return builder.toString();
	}

	private String getElementReplacementText(ParamInfo step, XmlFileLocation location)
	{
		StringBuilder builder = new StringBuilder();
		String nodeName = null;
		
		if(location.getCurrentToken() == null)
		{
			nodeName = step.getNameWithHyphens();
			builder.append("<").append(nodeName);
		}
		else
		{
			nodeName = step.getName().startsWith(location.getName()) ? step.getName() : step.getNameWithHyphens();
			builder.append(nodeName);
			
			if(builder.toString().startsWith(location.getCurrentToken()))
			{
				builder.delete(0, location.getCurrentToken().length());
			}
		}
		
		builder.append(">").append("</").append(nodeName).append(">");
		
		return builder.toString();
	}

	private List<Completion> getElementCompletions(XmlFileLocation location)
	{
		Element parentElement = location.getParentElement();
		Class<?> parentType = parentElement.getElementType();
		
		if(parentType == null)
		{
			return Collections.emptyList();
		}
		
		List<Completion> completions = new ArrayList<>();
		String namespace = location.getNameSpace();
		String curToken = location.getName() != null ? location.getName().toLowerCase().trim() : null;
		
		if(IStepContainer.class.isAssignableFrom(parentType) && 
				( namespace == null || XMLConstants.CCG_URI.equals(namespace) )
				)
		{
			Collection<StepInfo> steps = project.getDocInformation().getSteps();
			
			for(StepInfo step : steps)
			{
				if(curToken != null && !step.getName().toLowerCase().startsWith(curToken) && !step.getNameWithHyphens().toLowerCase().startsWith(curToken))
				{
					continue;
				}
				
				completions.add( new ShorthandCompletion(this, step.getNameWithHyphens(), getElementReplacementText(step, location), step.getName(), step.getDescription()) );
			}

			for(ValidationInfo validation : project.getDocInformation().getValidations())
			{
				if(curToken != null && !validation.getName().toLowerCase().startsWith(curToken) && !validation.getNameWithHyphens().toLowerCase().startsWith(curToken))
				{
					continue;
				}
				
				completions.add( new ShorthandCompletion(this, validation.getNameWithHyphens(), getElementReplacementText(validation, location), validation.getName(), validation.getDescription()) );
			}
		}
		
		StepInfo stepInfo = parentElement.getStepInfo();
		
		if(stepInfo != null)
		{
			Set<String> childNames = parentElement.getChildNames();
			Collection<ParamInfo> params = stepInfo.getParams();
			
			for(ParamInfo param : params)
			{
				if(childNames.contains(param.getName()))
				{
					continue;
				}
				
				if(curToken != null && !param.getName().toLowerCase().startsWith(curToken) && !param.getNameWithHyphens().toLowerCase().startsWith(curToken))
				{
					continue;
				}

				completions.add( new ShorthandCompletion(this, param.getNameWithHyphens(), getElementReplacementText(param, location), param.getName(), param.getDescription()) );
			}
			
			Collection<ElementInfo> childElems = stepInfo.getChildElements();
			
			for(ElementInfo elem : childElems)
			{
				if(!elem.isMultiple() && childNames.contains(elem.getName()))
				{
					continue;
				}
				
				if(curToken != null && !elem.getName().toLowerCase().startsWith(curToken) && !elem.getNameWithHyphens().toLowerCase().startsWith(curToken))
				{
					continue;
				}

				completions.add( new ShorthandCompletion(this, elem.getNameWithHyphens(), getElementReplacementText(elem, location), elem.getName(), elem.getDescription()) );
			}
			
			return completions;
		}
		
		/*
		BeanPropertyInfo beanInfo = project.getBeanPropertyInfoFactory().getBeanPropertyInfo(parentType);
		
		for(BeanProperty prop : beanInfo.getProperties())
		{
			if(prop.getAddMethod() != null)
			{
				String adder = prop.getAdderName();
				completions.add( new ShorthandCompletion(this, adder, getElementReplacementText(validation, location), adder, validation.getDescription()) );
			}
		}
		*/
		
		return completions;
	}

	private List<Completion> getAttributeCompletions(XmlFileLocation location)
	{
		Element elem = location.getParentElement();
		StepInfo step = project.getDocInformation().getStep(elem.getName());
		
		if(step == null)
		{
			step = project.getDocInformation().getValidation(elem.getName());
		}
		
		List<Completion> completions = new ArrayList<>();
		Collection<ParamInfo> params = step != null ? step.getParams() : null;
		String prefix = location.getCurrentToken();
		
		if(params != null)
		{
			Class<?> paramType = null;
			
			for(ParamInfo param : params)
			{
				if(elem.getAttribute(param.getName()) != null)
				{
					continue;
				}
				
				try
				{
					paramType = Class.forName(param.getType());
				}catch(Exception ex)
				{
					logger.warn("Failed to determine type of attribute '{}' of element: {}. Type String: {}", param.getName(), elem.getName(), param.getType());
					continue;
				}
				
				if(!XMLUtil.isSupportedAttributeClass(paramType))
				{
					continue;
				}
				
				if(StringUtils.isNotBlank(prefix))
				{
					if(!param.getName().startsWith(prefix))
					{
						continue;
					}
					
					String name = param.getName().substring(prefix.length());
					completions.add( new ShorthandCompletion(this, param.getName(), name + "=\"\"", param.getName(), param.getDescription()) );
				}
				else
				{
					completions.add( new ShorthandCompletion(this, param.getName(), param.getName() + "=\"\"", param.getName(), param.getDescription()) );
				}
			}
		}
		
		return completions;
	}

	@Override
	public List<Completion> getCompletions(JTextComponent comp)
	{
		return getCompletionsAt(comp, null);
	}

	@Override
	public List<Completion> getCompletionsAt(JTextComponent comp, Point p)
	{
		xmlFileLocation.getXmlFile().getRootElement().populateTestFileTypes(project, new FileParseCollector());
		
		switch(xmlFileLocation.getType())
		{
			case CHILD_ELEMENT:
			{
				return getElementCompletions(xmlFileLocation);
			}
			case ATTRIBUTE:
			{
				return getAttributeCompletions(xmlFileLocation);
			}
		}
		
		return Collections.emptyList();
	}
	
	@Override
	public List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent tc)
	{
		return null;
	}
	
	@Override
	public String getAlreadyEnteredText(JTextComponent comp)
	{
		this.xmlFileLocation = fileEditor.getXmlFileLocation();

		if(this.xmlFileLocation == null)
		{
			return null;
		}
		
		return "";
	}
}
