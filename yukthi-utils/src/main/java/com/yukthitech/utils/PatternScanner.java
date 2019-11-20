package com.yukthitech.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scanner to extract data using patterns from input.
 * @author akiran
 */
public class PatternScanner
{
	/**
	 * Matcher wrapper providing only access functionality.
	 * @author akiran
	 */
	public class ScannerMatch
	{
		/**
		 * underlying matcher to be used.
		 */
		private Matcher matcher;
		
		/**
		 * Position at which pattern was found.
		 */
		private int patternPosition;
		
		/**
		 * Instantiates a new scanner matcher.
		 *
		 * @param matcher the matcher
		 */
		public ScannerMatch(Matcher matcher, int patternPos)
		{
			this.matcher = matcher;
			this.patternPosition = patternPos;
		}

		public int start()
		{
			return patternPosition + matcher.start();
		}

		public int start(int group)
		{
			return patternPosition + matcher.start(group);
		}

		public int start(String name)
		{
			return patternPosition + matcher.start(name);
		}

		public int end()
		{
			return patternPosition + matcher.end();
		}

		public int end(int group)
		{
			return patternPosition + matcher.end(group);
		}

		public int end(String name)
		{
			return patternPosition + matcher.end(name);
		}

		public String group()
		{
			return matcher.group();
		}

		public String group(int group)
		{
			return matcher.group(group);
		}

		public String group(String name)
		{
			return matcher.group(name);
		}

		public int groupCount()
		{
			return matcher.groupCount();
		}
	}
	
	/**
	 * Source from which data has to be extracted.
	 */
	private String source;
	
	/**
	 * Current position of the scanner.
	 */
	private int position;
	
	/**
	 * Last match found during scan.
	 */
	private ScannerMatch lastMatch;
	
	/**
	 * Instantiates a new pattern scanner.
	 *
	 * @param source the source
	 */
	public PatternScanner(String source)
	{
		this.source = source;
	}
	
	/**
	 * Gets the source from which data has to be extracted.
	 *
	 * @return the source from which data has to be extracted
	 */
	public String getSource()
	{
		return source;
	}
	
	/**
	 * Gets the current position of the scanner.
	 *
	 * @return the current position of the scanner
	 */
	public int getPosition()
	{
		return position;
	}
	
	/**
	 * Gets the last match found during scan.
	 *
	 * @return the last match found during scan
	 */
	public ScannerMatch getLastMatch()
	{
		return lastMatch;
	}
	
	/**
	 * Checks whether current position is followed by specified pattern.
	 * @param pattern pattern to check.
	 * @return true if pattern is found.
	 */
	public boolean hasNext(String pattern)
	{
		return hasNext(Pattern.compile(pattern));
	}
	
	/**
	 * Checks whether current position is followed by specified pattern.
	 * @param pattern pattern to check.
	 * @return true if pattern is found.
	 */
	public boolean hasNext(Pattern pattern)
	{
		Matcher matcher = pattern.matcher(source.substring(position));
		
		if(!matcher.find())
		{
			return false;
		}
		
		return (matcher.start() == 0);
	}
	
	/**
	 * Fetches the pattern value from current position.
	 * @param pattern pattern to fetch
	 * @return value of matched pattern.
	 */
	public String next(String pattern)
	{
		return next(Pattern.compile(pattern));
	}
	
	/**
	 * Fetches the pattern value from current position.
	 * @param pattern pattern to fetch
	 * @return value of matched pattern.
	 */
	public String next(Pattern pattern)
	{
		Matcher matcher = pattern.matcher(source.substring(position));
		
		if(!matcher.find() || matcher.start() != 0)
		{
			return null;
		}
		
		this.lastMatch = new ScannerMatch(matcher, position);
		position = this.lastMatch.end();
		
		return lastMatch.group();
	}
	
	/**
	 * Skips the input content till specified pattern is seen. The position will be set
	 * such that the next token matches wih specified pattern.
	 * @param pattern pattern to find
	 * @return Content that is skipped.
	 */
	public String skipTill(Pattern pattern)
	{
		Matcher matcher = pattern.matcher(source.substring(position));
		
		if(!matcher.find())
		{
			return null;
		}
		
		int curPosition = this.position;
		
		this.lastMatch = new ScannerMatch(matcher, position);
		position = this.lastMatch.start();
		
		if(curPosition == position)
		{
			return "";
		}
		
		return (String) this.source.subSequence(curPosition, position);
	}

	/**
	 * Fetches the next pattern and matcher without changing current scanner position.
	 * @param pattern pattern to be peeked.
	 * @return matching string
	 */
	public String peek(String pattern)
	{
		return peek(Pattern.compile(pattern));
	}

	/**
	 * Fetches the next pattern and matcher without changing current scanner position.
	 * @param pattern pattern to be peeked.
	 * @return matching string
	 */
	public String peek(Pattern pattern)
	{
		int curPos = position;
		String res = next(pattern);
		position = curPos;
		
		return res;
	}

	/**
	 * Skips the pattern after current position.
	 * @param pattern
	 */
	public PatternScanner skip(String pattern)
	{
		next(pattern);
		return this;
	}
	
	/**
	 * Skips the pattern after current position.
	 * @param pattern
	 */
	public PatternScanner skip(Pattern pattern)
	{
		next(pattern);
		return this;
	}
}
