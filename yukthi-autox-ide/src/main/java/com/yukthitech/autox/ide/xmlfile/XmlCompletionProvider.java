package com.yukthitech.autox.ide.xmlfile;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.JTextComponent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.ShorthandCompletion;

import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.doc.ElementInfo;
import com.yukthitech.autox.doc.ExpressionParserDoc;
import com.yukthitech.autox.doc.ParamInfo;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.doc.UiLocatorDoc;
import com.yukthitech.autox.doc.ValidationInfo;
import com.yukthitech.autox.expr.ExpressionFactory;
import com.yukthitech.autox.expr.ParserContentType;
import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.monitor.ienv.ContextAttributeDetails;
import com.yukthitech.ccg.xml.XMLConstants;
import com.yukthitech.ccg.xml.XMLUtil;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.fmarker.FreeMarkerMethodDoc;

public class XmlCompletionProvider extends AbstractCompletionProvider
{
	private static Logger logger = LogManager.getLogger(XmlCompletionProvider.class);
	
	private static Pattern ALPHA_NUMERIC_ONLY = Pattern.compile("\\w*");
	
	private static Pattern EXPR_PREFIX_PATTERN = Pattern.compile("^\\s*(\\w+)\\s*\\:");
	
	private static Pattern FM_BLOCK_START = Pattern.compile("\\<[\\#\\@](\\w+)\\s+");
	
	private static Pattern LAST_TOKEN = Pattern.compile("([\\w\\.]+)$");
	
	private Project project;
	
	private XmlFileLocation xmlFileLocation;
	
	private FileEditor fileEditor;
	
	private IdeContext ideContext;
	
	public XmlCompletionProvider(Project project, FileEditor fileEditor, IdeContext ideContext)
	{
		this.project = project;
		this.fileEditor = fileEditor;
		this.ideContext = ideContext;
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
		
		if(CollectionUtils.isEmpty(step.getChildElements()))
		{
			builder.append("/>");
			return builder.toString();
		}
		
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
					paramType = CommonUtils.getClass(param.getType());
				}catch(Exception ex)
				{
					logger.warn("Failed to determine type of attribute '{}' of element: {}. Type String: {}", param.getName(), elem.getName(), param.getType());
					continue;
				}
				
				if(param.getSourceType() == SourceType.NONE && !XMLUtil.isSupportedAttributeClass(paramType))
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
					String attrCompletion = location.isFullElementGeneration() ? name + "=\"\"" : name;
					
					completions.add( new ShorthandCompletion(this, param.getName(), attrCompletion, param.getName(), param.getDescription()) );
				}
				else
				{
					String attrCompletion = location.isFullElementGeneration() ? param.getName() + "=\"\"" : param.getName();
					completions.add( new ShorthandCompletion(this, param.getName(), attrCompletion, param.getName(), param.getDescription()) );
				}
			}
		}
		
		return completions;
	}

	private List<Completion> getAttributeValueCompletions(XmlFileLocation location)
	{
		Element elem = location.getParentElement();
		StepInfo step = project.getDocInformation().getStep(elem.getName());
		
		if(step == null)
		{
			step = project.getDocInformation().getValidation(elem.getName());
		}
		
		ParamInfo paramInfo = step != null ? step.getParam(location.getName()) : null;
		SourceType sourceType = paramInfo != null ? paramInfo.getSourceType() : null;
		
		List<Completion> completions = new ArrayList<>();
		
		String curVal = location.getText();
		curVal = StringUtils.isBlank(curVal) ? null : curVal.trim().toLowerCase();

		//fetch the content type based on expression type
		ParserContentType contentType = ParserContentType.NONE;

		if(sourceType == SourceType.EXPRESSION || sourceType == SourceType.EXPRESSION_PATH)
		{
			if(curVal == null || ALPHA_NUMERIC_ONLY.matcher(curVal).matches())
			{
				String name = null;
				
				for(ExpressionParserDoc parser : project.getDocInformation().getParsers())
				{
					if(curVal != null && !parser.getName().startsWith(curVal))
					{
						continue;
					}
					
					name = curVal != null ? parser.getName().substring(curVal.length()) : parser.getName();
					
					completions.add( new ShorthandCompletion(this, parser.getName(), name + ":", parser.getName(), parser.getDescription()) );
				}
				
				return completions;
			}

			if(curVal != null)
			{
				List<String> expressionTokens = ExpressionFactory.parseExpressionTokens(curVal);
				Matcher matcher = EXPR_PREFIX_PATTERN.matcher(expressionTokens.get(expressionTokens.size() - 1));
				
				if(matcher.matches())
				{
					String exprType = matcher.group(1);
					ExpressionParserDoc parser = project.getDocInformation().getParser(exprType);
					
					if(parser != null)
					{
						contentType = parser.getContentType();
					}
				}
			}
		}
		
		if(sourceType == SourceType.UI_LOCATOR)
		{
			if(curVal == null || ALPHA_NUMERIC_ONLY.matcher(curVal).matches())
			{
				String name = null;
				
				for(UiLocatorDoc locDoc : project.getDocInformation().getUiLocators())
				{
					if(curVal != null && !locDoc.getName().startsWith(curVal))
					{
						continue;
					}
					
					name = curVal != null ? locDoc.getName().substring(curVal.length()) : locDoc.getName();
					
					completions.add( new ShorthandCompletion(this, locDoc.getName(), name + ":", locDoc.getName(), locDoc.getDescription()) );
				}
				
				return completions;
			}
		}
		
		boolean isExprPart = false;

		if(curVal != null)
		{
			//check if current position is part of ${} expression
			int exprStartIdx = curVal.lastIndexOf("${");
			int exprEndIdx = (exprStartIdx >= 0) ? curVal.substring(exprStartIdx).indexOf("}") : -1;
			
			isExprPart = (exprStartIdx >=0 && exprEndIdx < 0);
			
			if(!isExprPart)
			{
				Matcher matcher = FM_BLOCK_START.matcher(curVal);
				
				if(matcher.find())
				{
					exprEndIdx = curVal.substring(matcher.start()).indexOf(">");
					isExprPart = (exprEndIdx < 0);
				}
				
				if(isExprPart)
				{
					//skip free marker block start
					curVal = curVal.substring(matcher.end(1));
				}
			}
			
			//if current position is part of expression, based on last token find
			// the type of auto completion required
			if(isExprPart)
			{
				Matcher matcher = LAST_TOKEN.matcher(curVal);
				contentType = ParserContentType.FM_EXPRESSION;
				
				if(matcher.find())
				{
					curVal = matcher.group(1);
					
					if(curVal.startsWith("attr."))
					{
						curVal = curVal.substring("attr.".length());
						contentType = ParserContentType.ATTRIBUTE;
					}
				}
			}
		}
		
		if(contentType == ParserContentType.FM_EXPRESSION)
		{
			String complText = null;
			
			for(FreeMarkerMethodDoc method : project.getDocInformation().getFreeMarkerMethods())
			{
				if(curVal != null && !method.getName().startsWith(curVal))
				{
					continue;
				}
				
				complText = curVal != null ? method.getName().substring(curVal.length()) : method.getName();
				completions.add( new ShorthandCompletion(this, method.getName(), complText + "()", method.getName() + "()", method.getDescription()) );

				//auto complete with params
				if(method.hasParameters())
				{
					complText = curVal != null ? method.getName().substring(curVal.length()) : method.getName();
					completions.add( new ShorthandCompletion(this, method.getName(), complText + method.getParameterString(), method.getName() + method.getParameterString(), method.getDescription()) );
				}
			}
		}
		else if(contentType == ParserContentType.ATTRIBUTE && ideContext.getActiveEnvironment() != null)
		{
			List<ContextAttributeDetails> contextAttrs = ideContext.getActiveEnvironment().getContextAttributes();
			String complText = null, name = null;
			
			for(ContextAttributeDetails attr : contextAttrs)
			{
				if(curVal != null && !attr.getName().startsWith(curVal))
				{
					continue;
				}
				
				name = attr.getName();
				complText = curVal != null ? name.substring(curVal.length()) : name;
				completions.add( new ShorthandCompletion(this, name, complText, name, name) );
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
			case ATTRIBUTE_VALUE:
			{
				return getAttributeValueCompletions(xmlFileLocation);
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
