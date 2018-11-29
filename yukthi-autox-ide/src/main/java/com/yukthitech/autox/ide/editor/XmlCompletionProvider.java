package com.yukthitech.autox.ide.editor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.StringUtils;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.ShorthandCompletion;

import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.doc.ParamInfo;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.doc.ValidationInfo;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.XmlFileLocation;
import com.yukthitech.ccg.xml.XMLConstants;

public class XmlCompletionProvider extends AbstractCompletionProvider
{
	private Project project;
	
	private XmlFileLocation xmlFileLocation;
	
	private FileEditor fileEditor;
	
	public XmlCompletionProvider(Project project, FileEditor fileEditor)
	{
		this.project = project;
		this.fileEditor = fileEditor;
	}
	
	private String getReplacementText(StepInfo step, XmlFileLocation location)
	{
		StringBuilder builder = new StringBuilder();
		String nodeName = null;
		
		if(StringUtils.isEmpty(location.getCurrentToken()))
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
		
		builder.append(">").append("\n").append(location.getIndentation());
		builder.append("</").append(location.getXmlFile().getPrefixForNamespace(XMLConstants.CCG_URI)).append(":").append(nodeName).append(">");
		
		return builder.toString();
	}
	
	private List<Completion> getElementCompletions(XmlFileLocation location)
	{
		Class<?> parentType = location.getParentElement().getElementType();
		
		if(parentType == null)
		{
			return Collections.emptyList();
		}
		
		List<Completion> completions = new ArrayList<>();
		String namespace = location.getNameSpace();
		
		if(IStepContainer.class.isAssignableFrom(parentType) && 
				( namespace == null || XMLConstants.CCG_URI.equals(namespace) )
				)
		{
			Collection<StepInfo> steps = project.getDocInformation().getSteps();
			String curToken = location.getName() != null ? location.getName().toLowerCase().trim() : null;
			
			for(StepInfo step : steps)
			{
				if(curToken != null && !step.getName().toLowerCase().startsWith(curToken) && !step.getNameWithHyphens().toLowerCase().startsWith(curToken))
				{
					continue;
				}
				
				completions.add( new ShorthandCompletion(this, step.getName(), getReplacementText(step, location), step.getName(), step.getDescription()) );
			}

			for(ValidationInfo validation : project.getDocInformation().getValidations())
			{
				if(curToken != null && !validation.getName().toLowerCase().startsWith(curToken) && !validation.getNameWithHyphens().toLowerCase().startsWith(curToken))
				{
					continue;
				}
				
				completions.add( new ShorthandCompletion(this, validation.getName(), getReplacementText(validation, location), validation.getName(), validation.getDescription()) );
			}
		}
		
		return completions;
	}

	private List<Completion> getAttributeCompletions(XmlFileLocation location)
	{
		return Collections.emptyList();
	}

	@Override
	public List<Completion> getCompletions(JTextComponent comp)
	{
		return getCompletionsAt(comp, null);
	}

	@Override
	public List<Completion> getCompletionsAt(JTextComponent comp, Point p)
	{
		xmlFileLocation.getXmlFile().getRootElement().populateTestFileTypes(project, new ArrayList<>());
		
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

	/*
	@Override
	public void clearParameterizedCompletionParams()
	{
	}

	@Override
	public String getAlreadyEnteredText(JTextComponent comp)
	{
		return null;
	}

	private List<Completion> getElementCompletions(XmlFileLocation location)
	{
		Class<?> parentType = location.getParentElement().getElementType();
		
		if(parentType == null)
		{
			return Collections.emptyList();
		}
		
		List<Completion> completions = new ArrayList<>();
		
		if(StepGroup.class.isAssignableFrom(parentType))
		{
			Collection<StepInfo> steps = project.getDocInformation().getSteps();
			String prefix = location.getCurrentToken() != null ? location.getCurrentToken().toLowerCase().trim() : null;
			
			for(StepInfo step : steps)
			{
				if(location.getCurrentToken() != null && !step.getName().toLowerCase().startsWith(prefix))
				{
					continue;
				}
				
				completions.add( new BasicCompletion(this, step.getName(), step.getName(), step.getDescription()) );
			}
		}
		
		return completions;
	}

	private List<Completion> getAttributeCompletions(XmlFileLocation location)
	{
		return Collections.emptyList();
	}

	@Override
	public List<Completion> getCompletions(JTextComponent comp)
	{
		return getCompletionsAt(comp, null);
	}

	@Override
	public List<Completion> getCompletionsAt(JTextComponent comp, Point p)
	{
		XmlFileLocation fileLoc = fileEditor.getXmlFileLocation();
		fileLoc.getXmlFile().getRootElement().populateTestFileTypes(project, new ArrayList<>());
		
		switch(fileLoc.getType())
		{
			case CHILD_ELEMENT:
			{
				return getElementCompletions(fileLoc);
			}
			case ATTRIBUTE:
			{
				return getAttributeCompletions(fileLoc);
			}
		}
		
		return Collections.emptyList();
	}

	@Override
	public ListCellRenderer getListCellRenderer()
	{
		return null;
	}

	@Override
	public ParameterChoicesProvider getParameterChoicesProvider()
	{
		return null;
	}
	
	@Override
	public List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent tc)
	{
		return null;
	}

	@Override
	public char getParameterListEnd()
	{
		return 0;
	}

	@Override
	public String getParameterListSeparator()
	{
		return null;
	}

	@Override
	public char getParameterListStart()
	{
		return 0;
	}

	@Override
	public CompletionProvider getParent()
	{
		return null;
	}

	@Override
	public boolean isAutoActivateOkay(JTextComponent tc)
	{
		return false;
	}

	@Override
	public void setListCellRenderer(ListCellRenderer r)
	{
	}

	@Override
	public void setParameterizedCompletionParams(char listStart, String separator, char listEnd)
	{
	}

	@Override
	public void setParent(CompletionProvider parent)
	{
	}
	*/

}
