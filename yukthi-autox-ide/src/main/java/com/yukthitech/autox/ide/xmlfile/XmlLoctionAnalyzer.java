package com.yukthitech.autox.ide.xmlfile;

import java.util.regex.Pattern;

import com.yukthitech.autox.ide.IdeUtils;

public class XmlLoctionAnalyzer
{
	private static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s*");
	
	private static final Pattern PATTERN_ATTR_VALUE_START = Pattern.compile("\\s*\\=\\s*\\\"");
	
	private static final Pattern PATTERN_FULL_ATTR_TOKEN = Pattern.compile("(.*?)\\s*\\=\\s*\\\"(.*?)\\\"");
	
	private static final Pattern PATTERN_NAME = Pattern.compile("[\\w\\-\\:]+");
	
	public static XmlFileLocation getLocation(String xmlContent, int pos) throws Exception
	{
		char chArr[] = xmlContent.toCharArray();
		String lastToken = getLastToken(xmlContent, chArr, pos);
		
		if(lastToken != null && lastToken.startsWith("<"))
		{
			return getElementLocation(xmlContent, chArr, pos, lastToken, null);
		}
		
		int elemStart = xmlContent.lastIndexOf("<", pos);
		int nextElemStart = xmlContent.indexOf("<", pos);
		int closePos = xmlContent.indexOf(">", elemStart);
		
		//if closing tag is coming after next open tag
		if(nextElemStart > 0 && closePos > nextElemStart)
		{
			closePos = -1;
		}
		
		//if the current position is before current element close
		if(closePos == -1 || closePos >= pos)
		{
			return getAttributeLocation(xmlContent, chArr, pos, elemStart, closePos, lastToken);
		}
		
		String nodeText = xmlContent.substring(closePos + 1, pos);
		return getElementLocation(xmlContent, chArr, pos, lastToken, nodeText);
	}
	
	private static String getIndentation(char chArr[], int pos)
	{
		int newLinePos = -1;
		
		for(int i = pos - 1; i >= 0; i--)
		{
			if(chArr[i] == '\n')
			{
				newLinePos = i;
				break;
			}
		}
		
		if(newLinePos < 0)
		{
			return "";
		}
		
		StringBuilder indent = new StringBuilder();
		
		for(int i = newLinePos + 1; i < pos; i++)
		{
			if(!Character.isWhitespace(chArr[i]))
			{
				break;
			}
			
			if(chArr[i] == ' ' || chArr[i] == '\t')
			{
				indent.append(chArr[i]);
			}
		}
		
		return indent.toString();
	}
	
	private static XmlFileLocation getElementLocation(String xmlContent, char chArr[], int pos, String lastToken, String nodeText)
	{
		int validPos = lastToken == null ? pos : (pos - lastToken.length());
		
		String contentWithoutToken = xmlContent.substring(0, validPos);
		XmlFile xmlFile = null;
		
		try
		{
			xmlFile = XmlFile.parse(contentWithoutToken + "\n\n", validPos);
		}catch(Exception ex)
		{
			return null;
		}
		
		if(lastToken != null && lastToken.startsWith("<"))
		{
			lastToken = lastToken.substring(1);
		}
		
		//if text is present in current element before current position
		if(nodeText != null && nodeText.trim().length() > 0)
		{
			return XmlFileLocation.newTextElementLocation(xmlFile, xmlFile.getLastElement(validPos), nodeText, lastToken);
		}
		
		return XmlFileLocation.newElementLocation(xmlFile, xmlFile.getLastElement(validPos), lastToken, getIndentation(chArr, pos));
	}
	
	private static String getLastToken(String xmlContent, char chArr[], int pos)
	{
		int startPos = 0;
		
		for(int i = pos - 1; i > 0; i--)
		{
			if(Character.isWhitespace( chArr[i] ) || chArr[i] == '>')
			{
				startPos = i + 1;
				break;
			}
		}
		
		if(startPos >= pos)
		{
			return null;
		}
		
		return xmlContent.substring(startPos, pos);
	}
	
	private static XmlFileLocation getAttributeLocation(String xmlContent, char chArr[], int pos, int elemStart, int elemEnd, String lastToken) throws Exception
	{
		//int parentLineCount = IdeUtils.getLineCount(chArr, elemStart);
		XmlFile xmlFile = XmlFile.parse(xmlContent.substring(0, elemStart - 1) + "\n\n", elemStart);
		Element parentElement = xmlFile.getLastElement( elemStart );
		
		String elementContent = xmlContent.substring(elemStart + 1, pos);
		Element curElement = new Element();
		
		curElement.setParentElement(parentElement);
		parentElement.addNode(curElement);
		
		PatternScanner scanner = new PatternScanner(elementContent);
		
		scanner.skip(PATTERN_WHITESPACE);
		
		String elemName = scanner.next(PATTERN_NAME);
		int idx = 0;
		
		if( (idx = elemName.indexOf(":")) > 0)
		{
			curElement.setName(elemName.substring(idx + 1));
			curElement.setPrefix(elemName.substring(0, idx));
			curElement.setNamespace(curElement.getNamespaceWithPrefix(curElement.getPrefix()));
		}
		else
		{
			curElement.setName(elemName);
		}
		
		scanner.skip(PATTERN_WHITESPACE);
		
		PatternScanner.ScannerMatch matchResult = null;
		String attrName = null;
		String prefix = null;
		
		while(true)
		{
			scanner.skip(PATTERN_WHITESPACE);
			
			if(!scanner.hasNext(PATTERN_FULL_ATTR_TOKEN))
			{
				break;
			}
			
			scanner.next(PATTERN_FULL_ATTR_TOKEN);
			
			matchResult = scanner.getLastMatch();
			attrName = matchResult.group(1);
			
			if( (idx = attrName.indexOf(":")) > 0)
			{
				prefix = attrName.substring(0, idx);
				attrName = attrName.substring(idx + 1);
			}
			else
			{
				prefix = null;
			}

			//get name location details
			LocationRange nameLoc = new LocationRange();
			IdeUtils.getLocationRange(chArr, matchResult.start(1), matchResult.end(1), nameLoc);
			
			//get value location details
			LocationRange valueLoc = new LocationRange();
			IdeUtils.getLocationRange(chArr, matchResult.start(2), matchResult.end(2), valueLoc);
			
			curElement.addAttribute(new Attribute(prefix, curElement.getNamespaceWithPrefix(prefix), attrName, matchResult.group(2), nameLoc, valueLoc));
		}
		
		scanner.skip(PATTERN_WHITESPACE);

		//the position is for next attribute insertion
		if(!scanner.hasNext("\\S+"))
		{
			return XmlFileLocation.newAttributeLocation(xmlFile, curElement, null);
		}
		
		//invalid scenario
		if(!scanner.hasNext(PATTERN_NAME))
		{
			return null;
		}
		
		attrName = scanner.next(PATTERN_NAME);
		
		//position for new attr with partially typed.
		if(!scanner.hasNext(PATTERN_ATTR_VALUE_START))
		{
			if(elementContent.endsWith(attrName))
			{
				return XmlFileLocation.newAttributeLocation(xmlFile, curElement, attrName);
			}
			
			//if there is a space or something after attr name
			return null;
		}
		
		PatternScanner.ScannerMatch matchRes = scanner.skip(PATTERN_ATTR_VALUE_START).getLastMatch();
		
		//position of attribute value
		int attrValStartPos = matchRes.end();
		String attrText = (attrValStartPos < (elementContent.length() - 1)) ? elementContent.substring(attrValStartPos) : null;
		
		return XmlFileLocation.newAttributeValueLocation(xmlFile, curElement, attrName, attrText, lastToken);
	}
}
