package com.yukthitech.autox.test.ui.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.config.AutomationConfiguration;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.UnsupportedOperationException;

/**
 * Common utils used by automation.
 * 
 * @author akiran
 */
public class UiAutomationUtils
{
	/**
	 * Five seconds.
	 */
	public static final int FIVE_SECONDS = 5;

	private static Logger logger = LogManager.getLogger(UiAutomationUtils.class);

	/**
	 * Min wait duration.
	 */
	private static final long DURATION_HUNDRED_MILLIS = 100;

	/**
	 * Millis per second.
	 */
	private static final long MILLIS_PER_SEC = 1000;

	/**
	 * Pattern expected to be used by locator strings.
	 */
	private static Pattern LOCATOR_PATTERN = Pattern.compile("(\\w+)\\s*\\:\\s*(.*)");

	/**
	 * The open bracket.
	 **/
	private static String OPENBRACKET = "[";

	/**
	 * The close bracket.
	 **/
	private static String CLOSEBRACKET = "]";

	/**
	 * Fetches input form field type of specified element.
	 * 
	 * @param element
	 *            Element for which form field type has to be determined.
	 * @return Matching form field type
	 */
	public static FormFieldType getFormFieldType(WebElement element)
	{
		String tagName = element.getTagName().toLowerCase();

		if("textarea".equals(tagName))
		{
			return FormFieldType.MULTI_LINE_TEXT;
		}

		if("select".equals(tagName))
		{
			return FormFieldType.DROP_DOWN;
		}

		if("input".equals(tagName))
		{
			String type = "" + element.getAttribute("type");
			type = type.toLowerCase();

			switch (type)
			{
				case "number":
					return FormFieldType.INT;
				case "password":
					return FormFieldType.PASSWORD;
				case "radio":
					return FormFieldType.RADIO_BUTTON;
				case "checkbox":
					return FormFieldType.CHECK_BOX;
				case "date":
					return FormFieldType.DATE;
				default:
					return FormFieldType.TEXT;
			}
		}

		return null;
	}

	/**
	 * Populates specified field with specified value.
	 * 
	 * @param context
	 *            Automation context
	 * @param parent
	 *            Parent under which target element can be found
	 * @param locator
	 *            Locator of the target field. If locator pattern is not used, this will be assumed as name.
	 * @param value
	 *            Value to be populated
	 * @return True, if population was successful.
	 */
	public static boolean populateField(AutomationContext context, WebElement parent, String locator, Object value)
	{
		logger.trace("For field {} under parent {} setting value - {}", locator, parent, value);
		
		if(!LOCATOR_PATTERN.matcher(locator).matches())
		{
			locator = LocatorType.NAME.getKey() + ":" + locator;
		}

		List<WebElement> elements = findElements(context, parent, locator);

		// if no elements found with specified name
		if(elements == null || elements.isEmpty())
		{
			return false;
		}

		WebElement element = elements.get(0);
		String tagName = element.getTagName().toLowerCase();

		FormFieldType type = getFormFieldType(element);

		if(type != null)
		{
			if(type.isMultiFieldAccessor())
			{
				type.getFieldAccessor().setValue(elements, value);
			}
			else
			{
				if(elements.size() > 1)
				{
					logger.warn("Multiple elements found for locator '{}'. Choosing the first element for population", locator);
				}
				
				type.getFieldAccessor().setValue(element, value);
			}
		}
		else
		{
			throw new UnsupportedOperationException("Encountered unsupported input tag '{}' for data population", tagName);
		}

		// find the parent element enclosing input elements
		return true;
	}

	/**
	 * Fetches the element with specified locator.
	 * 
	 * @param context
	 *            Context to be used
	 * @param parent
	 *            Parent under which element need to be searched
	 * @param locator
	 *            Locator to be used for searching
	 * @return Matching element
	 */
	public static WebElement findElement(AutomationContext context, WebElement parent, String locator)
	{
		List<WebElement> elements = findElements(context, parent, locator);

		if(elements == null || elements.size() == 0)
		{
			return null;
		}

		return elements.get(0);
	}

	/**
	 * Fetches the elements with specified locator.
	 * 
	 * @param context
	 *            Context to be used
	 * @param parent
	 *            Parent under which elements need to be searched
	 * @param locator
	 *            Locator to be used for searching
	 * @return Matching elements
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<WebElement> findElements(AutomationContext context, WebElement parent, String locator)
	{
		logger.trace("Trying to find element with location '{}' under parent - {}", locator, parent);

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();

		Matcher matcher = LOCATOR_PATTERN.matcher(locator);
		LocatorType locatorType = LocatorType.JS;
		String query = null;

		// if the locator string matches required pattern
		if(matcher.matches())
		{
			locatorType = LocatorType.getLocatorType(matcher.group(1));
			query = matcher.group(2);

			// if invalid locator is specified
			if(locatorType == null)
			{
				throw new InvalidArgumentException("Invalid key '{}' encountered in locator - {}", matcher.group(1), locator);
			}
		}
		else
		{
			query = locator;
		}

		By locatorBy = null;

		// find the locator based on prefix
		switch (locatorType)
		{
			case ID:
				locatorBy = By.id(query);
				break;
			case CSS:
				locatorBy = By.cssSelector(query);
				break;
			case CLASS:
				locatorBy = By.className(query);
				break;
			case NAME:
				locatorBy = By.name(query);
				break;
			case TAG:
				locatorBy = By.tagName(query);
				break;
			case XPATH:
				locatorBy = By.xpath(query);
				break;
			default:
				locatorBy = null;
		}

		logger.trace("For locator '{}' using locator-by - {}", locator, locatorBy);

		List<WebElement> result = null;

		// if locator type is not defined (which would be the case for JS
		// locator type)
		if(locatorBy == null)
		{
			Object res = ((JavascriptExecutor) driver).executeScript("return $(" + query + ").get()");

			if(res instanceof Collection)
			{
				result = new ArrayList<WebElement>((Collection) res);
			}
			else
			{
				result = Arrays.asList((WebElement) res);
			}
		}
		// if parent is defined
		else if(parent != null)
		{
			result = parent.findElements(locatorBy);
		}
		else
		{
			result = driver.findElements(locatorBy);
		}

		if(logger.isTraceEnabled())
		{
			logger.trace("For locator '{}' found elements as - {}", locator, toString(context, result));
		}

		return result;
	}

	/**
	 * Waits for specified amount of time.
	 * 
	 * @param millis
	 *            Milli seconds to wait.
	 */
	public static void waitFor(long millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Checks for checkFunction to be true, if not waits for 1 sec and again
	 * tries to check. This process will be repeated for "iterationCount" number
	 * of times. If result is still false, exception "ex" will be thrown.
	 * 
	 * @param checkFunction
	 *            Function to check
	 * @param waitTime
	 *            Time in seconds to wait for
	 * @param waitMessage
	 *            Wait message to be logged during waiting.
	 * @param ex
	 *            Exception to be thrown if all tries fail.
	 */
	public static void validateWithWait(Supplier<Boolean> checkFunction, int waitTime, String waitMessage, RuntimeException ex)
	{
		long iterationCount = (waitTime * MILLIS_PER_SEC) / DURATION_HUNDRED_MILLIS;

		for(int i = 0; i < iterationCount; i++)
		{
			if(checkFunction.get())
			{
				return;
			}

			logger.trace(waitMessage);
			waitFor(DURATION_HUNDRED_MILLIS);
		}

		throw ex;
	}

	/**
	 * Generates html node string from specified elements.
	 * 
	 * @param context
	 *            Automation context
	 * @param elements
	 *            Elements to be converted
	 * @return Converted string.
	 */
	public static String toString(AutomationContext context, Collection<WebElement> elements)
	{
		StringBuilder builder = new StringBuilder(OPENBRACKET);
		boolean first = true;
		
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		JavascriptExecutor jsExecutor = (JavascriptExecutor) seleniumConfiguration.getWebDriver();

		for(WebElement element : elements)
		{
			if(!first)
			{
				builder.append(",\n");
			}

			builder.append(element).append(OPENBRACKET);

			if(element == null)
			{
				builder.append("null");
				continue;
			}

			builder.append(jsExecutor.executeScript(AutomationConfiguration.getInstance().getScript("elementToString"), element));
			builder.append(CLOSEBRACKET);
			first = false;
		}

		builder.append(CLOSEBRACKET);
		return builder.toString();
	}
}
