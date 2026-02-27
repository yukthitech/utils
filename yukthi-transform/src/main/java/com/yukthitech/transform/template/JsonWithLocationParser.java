package com.yukthitech.transform.template;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * Parser for JSON with location information.
 */
public class JsonWithLocationParser
{

	/**
	 * Represents a path element in a JSON document.
	 */
	private static class PathElement
	{
		String fieldName;
		Integer index;

		static PathElement field(String name)
		{
			PathElement e = new PathElement();
			e.fieldName = name;
			return e;
		}

		static PathElement index(int i)
		{
			PathElement e = new PathElement();
			e.index = i;
			return e;
		}
	}

	public static interface JsonElementWithLocation
	{
		Location getLocation();

		default Object getValue()
		{
			return this;
		}
	}

	/**
	 * Represents a value node in a JSON document.
	 */
	public static class ValueWithLocation implements JsonElementWithLocation
	{
		Object value;
		Location location;

		public ValueWithLocation(Object value, Location location)
		{
			this.value = value;
			this.location = location;
		}

		@Override
		public Location getLocation()
		{
			return location;
		}

		@Override
		public Object getValue()
		{
			return value;
		}

		@Override
		public String toString()
		{
			return value.toString();
		}
	}

	/**
	 * Represents a map with location information.
	 */
	public static class MapWithLocation extends LinkedHashMap<String, JsonElementWithLocation> implements JsonElementWithLocation
	{
		private static final long serialVersionUID = 1L;
		Location location;

		public MapWithLocation(Location location)
		{
			this.location = location;
		}

		@Override
		public Location getLocation()
		{
			return location;
		}

		public String getString(String key)
		{
			JsonElementWithLocation value = (JsonElementWithLocation) get(key);

			if(value == null)
			{
				return null;
			}

			if(!(value instanceof ValueWithLocation))
			{
				throw new TemplateParseException(value.getLocation(), "Encountered non-string value when string is expected. Key: %s", key);
			}

			ValueWithLocation valueWithLocation = (ValueWithLocation) value;

			if(!(valueWithLocation.getValue() instanceof String))
			{
				throw new TemplateParseException(value.getLocation(), "Encountered non-string value when string is expected. Key: %s", key);
			}

			return (String) valueWithLocation.getValue();
		}
	}

	/**
	 * Represents a list with location information.
	 */
	public static class ListWithLocation extends ArrayList<JsonElementWithLocation> implements JsonElementWithLocation
	{
		private static final long serialVersionUID = 1L;
		Location location;

		public ListWithLocation(Location location)
		{
			this.location = location;
		}

		@Override
		public Location getLocation()
		{
			return location;
		}
	}

	/**
	 * Name of the template.
	 */
	private String templateName;

	/**
	 * The JSON factory.
	 */
	private final JsonFactory factory = new JsonFactory();

	/**
	 * The path stack.
	 */
	private final Deque<PathElement> pathStack = new ArrayDeque<>();

	public JsonWithLocationParser(String templateName)
	{
		this.templateName = templateName;
	}

	/**
	 * Parses the JSON string and returns the root node.
	 * 
	 * @param json the JSON string to parse
	 * 
	 * @return the root node
	 * 
	 * @throws IOException if an error occurs while parsing the JSON string
	 */
	public Object parse(String json) throws IOException
	{
		JsonParser parser = factory.createParser(json);
		parser.nextToken(); // move to first token
		return readValue(parser);
	}

	/**
	 * Builds the JSON path from the path stack.
	 * 
	 * @return the JSON path
	 */
	private String buildJsonPath()
	{
		StringBuilder sb = new StringBuilder("$");

		for(PathElement e : pathStack)
		{
			if(e.fieldName != null)
			{
				sb.append(".").append(e.fieldName);
			}
			else
			{
				sb.append("[").append(e.index).append("]");
			}
		}

		return sb.toString();
	}

	/**
	 * Reads a value from the JSON parser.
	 * 
	 * @param parser the JSON parser
	 * 
	 * @return the value
	 * 
	 * @throws IOException if an error occurs while reading the value
	 */
	private JsonElementWithLocation readValue(JsonParser parser) throws IOException
	{
		JsonToken token = parser.currentToken();

		JsonLocation location = parser.currentLocation();
		Location locationObj = new Location(templateName, location.getLineNr(), location.getColumnNr(), buildJsonPath());

		switch (token)
		{

			case START_OBJECT:
				return readObject(parser);

			case START_ARRAY:
				return readArray(parser);

			case VALUE_STRING:
				return new ValueWithLocation(parser.getValueAsString(), locationObj);
			case VALUE_NUMBER_INT:
				long value = parser.getValueAsLong();

				if(value > Integer.MAX_VALUE || value < Integer.MIN_VALUE)
				{
					return new ValueWithLocation(value, locationObj);
				}

				return new ValueWithLocation((int) value, locationObj);
			case VALUE_NUMBER_FLOAT:
				return new ValueWithLocation(parser.getValueAsDouble(), locationObj);
			case VALUE_TRUE:
				return new ValueWithLocation(Boolean.TRUE, locationObj);
			case VALUE_FALSE:
				return new ValueWithLocation(Boolean.FALSE, locationObj);
			case VALUE_NULL:
				return new ValueWithLocation(null, locationObj);

			default:
				throw new IllegalStateException("Unexpected token: " + token);
		}
	}

	/**
	 * Reads an object from the JSON parser.
	 * 
	 * @param parser the JSON parser
	 * 
	 * @return the object
	 * 
	 * @throws IOException if an error occurs while reading the object
	 */
	private MapWithLocation readObject(JsonParser parser) throws IOException
	{

		JsonLocation location = parser.currentLocation();
		Location locationObj = new Location(templateName, location.getLineNr(), location.getColumnNr(), buildJsonPath());
		MapWithLocation map = new MapWithLocation(locationObj);

		while(parser.nextToken() != JsonToken.END_OBJECT)
		{

			String fieldName = parser.currentName();
			pathStack.addLast(PathElement.field(fieldName));

			parser.nextToken(); // move to value

			JsonElementWithLocation value = readValue(parser);

			map.put(fieldName, value);

			pathStack.removeLast();
		}

		return map;
	}

	/**
	 * Reads an array from the JSON parser.
	 * 
	 * @param parser the JSON parser
	 * 
	 * @return the array
	 * 
	 * @throws IOException if an error occurs while reading the array
	 */
	private ListWithLocation readArray(JsonParser parser) throws IOException
	{

		JsonLocation location = parser.currentLocation();
		Location locationObj = new Location(templateName, location.getLineNr(), location.getColumnNr(), buildJsonPath());
		ListWithLocation list = new ListWithLocation(locationObj);
		int index = 0;

		while(parser.nextToken() != JsonToken.END_ARRAY)
		{
			pathStack.addLast(PathElement.index(index));
			list.add(readValue(parser));
			pathStack.removeLast();

			index++;
		}

		return list;
	}
}
