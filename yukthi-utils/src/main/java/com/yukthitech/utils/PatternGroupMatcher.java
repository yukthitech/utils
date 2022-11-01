package com.yukthitech.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Matcher which works like standard regex matcher but with multiple patterns.
 * For every find, all patterns will be checked, whichever is found first that
 * will be used for further processing.
 * 
 * @author akranthikiran
 */
public class PatternGroupMatcher
{
	/**
	 * Encapsulation of pattern, matcher and its corresponding consumer.
	 * 
	 * @author akranthikiran
	 */
	public static class PatternAndConsumer
	{
		/**
		 * Pattern to find.
		 */
		private Pattern pattern;
		
		/**
		 * Corresponding matcher.
		 */
		private Matcher matcher;
		
		/**
		 * Optional consumer to be invoked when 
		 */
		private Consumer<PatternGroupMatcher> consumer;

		public PatternAndConsumer(Pattern pattern, Consumer<PatternGroupMatcher> consumer)
		{
			this.pattern = pattern;
			this.consumer = consumer;
		}
	}
	
	private List<PatternAndConsumer> matchers;
	private int matchFoundAt = -1;
	private int nextStart = 0;
	private int appendPos = 0;
	private String actualString;
	
	/**
	 * Builder which is tracking the pattern replacement.
	 * This is not used in this class, it is place holder for ease of access.
	 */
	private StringBuffer stringBuffer;

	public PatternGroupMatcher(String str, Pattern... patterns)
	{
		List<PatternAndConsumer> lst = Arrays.asList(patterns)
				.stream()
				.map(ptrn -> new PatternAndConsumer(ptrn, null))
				.collect(Collectors.toList());
		
		this.setPatternConsumers(str, lst);
		this.actualString = str;
	}
	
	public PatternGroupMatcher(String str, PatternAndConsumer... patterns)
	{
		this.setPatternConsumers(str, Arrays.asList(patterns));
		this.actualString = str;
	}
	
	private void setPatternConsumers(String str, List<PatternAndConsumer> patterns)
	{
		for(PatternAndConsumer pattern : patterns)
		{
			pattern.matcher = pattern.pattern.matcher(str);
		}

		matchers = new ArrayList<>(patterns);
	}

	public StringBuffer getStringBuffer()
	{
		return stringBuffer;
	}

	public void setStringBuffer(StringBuffer stringBuffer)
	{
		this.stringBuffer = stringBuffer;
	}

	public boolean find()
	{
		int minIdx = Integer.MAX_VALUE;
		int idx = 0, i = -1;
		int foundIdx = -1;
		PatternAndConsumer matchedPattern = null;

		for(PatternAndConsumer pattern : this.matchers)
		{
			i++;
			
			if(!pattern.matcher.find(nextStart))
			{
				continue;
			}

			idx = pattern.matcher.start();

			if(idx < minIdx)
			{
				minIdx = idx;
				foundIdx = i;
				matchedPattern = pattern;
			}
		}

		if(foundIdx < 0)
		{
			matchFoundAt = -1;
			return false;
		}

		this.nextStart = matchedPattern.matcher.end();
		this.matchFoundAt = foundIdx;
		
		//call the corresponding consumer
		if(matchedPattern.consumer != null)
		{
			matchedPattern.consumer.accept(this);
		}
		
		return true;
	}

	public int getGroupCount()
	{
		if(matchFoundAt < 0)
			throw new IllegalStateException("No match found.");

		return matchers.get(matchFoundAt).matcher.groupCount();
	}

	public int getMatchIndex()
	{
		return matchFoundAt;
	}

	public String group()
	{
		if(matchFoundAt < 0)
			throw new IllegalStateException("No match found.");

		return matchers.get(matchFoundAt).matcher.group();
	}

	public String group(int grp)
	{
		if(matchFoundAt < 0)
			throw new IllegalStateException("No match found.");

		return matchers.get(matchFoundAt).matcher.group(grp);
	}

	public PatternGroupMatcher appendReplacement(StringBuffer sb, String replacement)
	{
		if(matchFoundAt < 0)
			throw new IllegalStateException("No match found.");

		Matcher matcher = matchers.get(matchFoundAt).matcher;
		int st = matcher.start();

		if(appendPos > st)
			throw new IllegalStateException("No match found after previous replacement.");

		int ed = matcher.end();

		if(appendPos == st)
		{
			sb.append(replacement);
		}
		else
		{
			String appStr = actualString.substring(appendPos, st);
			sb.append(appStr).append(replacement);
		}
		
		this.appendPos = ed;
		return this;
	}

	public StringBuffer appendTail(StringBuffer sb)
	{
		int len = actualString.length();

		if(appendPos >= len)
			return sb;

		String appStr = actualString.substring(appendPos, len);

		sb.append(appStr);
		this.appendPos = len;
		return sb;
	}

}
