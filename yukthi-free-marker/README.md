# Yukthi FreeMarker

This library provides a simplified wrapper around the FreeMarker template engine. It allows you to register Java static methods as FreeMarker methods and directives using annotations, making them directly accessible within your templates.

## How to Use `FreeMarkerEngine`

The `FreeMarkerEngine` class is the entry point for using this library. It provides several methods for template evaluation:

### 1. `processTemplate(String name, String templateString, Object context)`

This method processes a FreeMarker template string with the given context and returns the processed output.

**Example:**

```java
FreeMarkerEngine engine = new FreeMarkerEngine();
Map<String, Object> context = new HashMap<>();
context.put("user", "John Doe");

String template = "Hello, ${user}!";
String result = engine.processTemplate("greetingTemplate", template, context);

System.out.println(result); // Output: Hello, John Doe!
```

### 2. `evaluateCondition(String name, String condition, Object context)`

This method evaluates a FreeMarker boolean expression and returns the result.

**Example:**

```java
FreeMarkerEngine engine = new FreeMarkerEngine();
Map<String, Object> context = new HashMap<>();
context.put("age", 25);

String condition = "age > 18";
boolean isAdult = engine.evaluateCondition("ageCheck", condition, context);

System.out.println(isAdult); // Output: true
```

### 3. `fetchValue(String name, String valueExpression, Object context)`

This method evaluates a FreeMarker expression and returns the resulting value.

**Example:**

```java
FreeMarkerEngine engine = new FreeMarkerEngine();
Map<String, Object> context = new HashMap<>();
context.put("price", 100);
context.put("tax", 20);

String expression = "price + tax";
Object total = engine.fetchValue("totalCalculation", expression, context);

System.out.println(total); // Output: 120
```

## Registering Custom Methods and Directives

You can register your own custom methods and directives using annotations. This allows you to extend the functionality of FreeMarker with your own Java code.

### Methods vs. Directives

*   **Methods:** FreeMarker methods are functions that return a value. They are called using the `${...}` syntax.
*   **Directives:** FreeMarker directives are used to control the template's output. They are called using the `<@...>` syntax.

### Annotations

The following annotations are available for registering custom methods and directives:

*   `@FreeMarkerMethod`: Marks a static method as a FreeMarker method.
*   `@FreeMarkerDirective`: Marks a static method as a FreeMarker directive.
*   `@FmParam`: Provides metadata for a method parameter.
*   `@ExampleDoc`: Provides example usage for a method or directive.

#### `@FreeMarkerMethod`

| Attribute         | Description                                     |
| ----------------- | ----------------------------------------------- |
| `value`           | The name of the method in FreeMarker templates. |
| `description`     | A description of the method.                    |
| `returnDescription`| A description of the return value.              |
| `examples`        | An array of `@ExampleDoc` annotations.          |

**Example:**

```java
@FreeMarkerMethod(
  description = "Converts specified date into string in specified format.",
  returnDescription = "Fromated date string.",
  examples = {
    @ExampleDoc(usage = "dateToStr(date, 'MM/dd/yyy')", result = "20/20/2018")
})
public static String dateToStr(
    @FmParam(name = "date", description = "Date to be converted") Date date, 
    @FmParam(name = "format", description = "Date format to which date should be converted") String format) {
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
  return simpleDateFormat.format(date);
}
```

#### `@FreeMarkerDirective`

| Attribute     | Description                                       |
| ------------- | ------------------------------------------------- |
| `value`       | The name of the directive in FreeMarker templates. |
| `description` | A description of the directive.                   |
| `examples`    | An array of `@ExampleDoc` annotations.            |

**Example:**

```java
@FreeMarkerDirective(value = "trim", 
    description = "Trims the content enclosed within this directive.",
    examples = {
      @ExampleDoc(usage = "<@trim>   some content  </@trim>", result = "some content")
})
public static String trim(
    @FmParam(name = "body", body = true, description = "Enclosing body content") String body ) 
    throws TemplateException, IOException {
  return body.trim();
}
```

#### `@FmParam`

| Attribute      | Description                                                                        |
| -------------- | ---------------------------------------------------------------------------------- |
| `name`         | The name of the parameter.                                                         |
| `description`  | A description of the parameter.                                                    |
| `defaultValue` | The default value of the parameter.                                                |
| `body`         | If `true`, the parameter will receive the body of the directive.                   |
| `allParams`    | If `true`, the parameter (which must be a `Map`) will receive all directive parameters. |

#### `@ExampleDoc`

| Attribute | Description                      |
| --------- | -------------------------------- |
| `title`   | The title of the example.        |
| `usage`   | An example of how to use the method or directive. |
| `result`  | The expected result of the example. |


# Default Methods and Directives

This library provides a set of default methods and directives for common tasks.

## Default Directives
Following additional directives by default are supported by this library:

### Common Directives
#### ${\color{blue}@indent}$
**Description**: Helps in indenting the enclosed content. Accepts optional prefix attribute, defaults to empty string. Every line will be trimmed and converted into single line and prefix will be added at the start. And from the output content '\t' and '\n' will be replaced with tab and new-line characters respectively.<br>

**Parameters**
|Name|Default Value|Description|
|:---|:-----------|:-----------|
|body||Enclosing body content|
|prefix|<empty string>|If specified, this value will be added in start of every line|
|retainLineBreaks|false|[boolean] if true, lines will be maintained as separate lines.|

> **Example:** Without parameters<br>
> **Usage:** 
> ```
> <@indent>   first line
> second line  </@indent>
> ```
> **Result:** 
> ```
> first linesecond line
> ```

> **Example:** With Prefix<br>
> **Usage:** 
> ```
> <@indent prefix='--'>   first line
> second line    </@indent>
> ```
> **Result:** 
> ```
> --first line--second line
> ```

> **Example:** With Prefix and retainLineBreaks<br>
> **Usage:** 
> ```
> <@indent prefix='--' retainLineBreaks=true>   first line
> second line    </@indent>
> ```
> **Result:** 
> ```
> --first line
> --second line
> ```


#### ${\color{blue}@trim}$
**Description**: Trims the content enclosed within this directive.<br>

**Parameters**
|Name|Default Value|Description|
|:---|:-----------|:-----------|
|body||Enclosing body content|

> **Example:** <br>
> **Usage:** 
> ```
> <@trim>   some content  </@trim>
> ```
> **Result:** 
> ```
> some content
> ```





## Default Methods
Following additional freemarker methods by default are supported by this library:

### Date Methods
#### ${\color{blue}addDays()}$
**Description**: Adds specified number of days to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified days

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which days should be added|
|days|int||Days to be added.|


#### ${\color{blue}addHours()}$
**Description**: Adds specified number of hours to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified hours

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which hours should be added|
|hours|int||Hours to be added.|


#### ${\color{blue}addMinutes()}$
**Description**: Adds specified number of minutes to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified minutes

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which minutes should be added|
|minutes|int||Minutes to be added.|


#### ${\color{blue}addSeconds()}$
**Description**: Adds specified number of seconds to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified seconds

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which seconds should be added|
|seconds|int||Seconds to be added.|


#### ${\color{blue}addYears()}$
**Description**: Adds specified number of days to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified years

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which days should be added|
|years|int||Years to be added.|


#### ${\color{blue}dateToStr()}$
**Description**: Converts specified date into string in specified format.<br>
**Returns**: **[java.lang.String]** Fromated date string.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to be converted|
|format|java.lang.String||Date format to which date should be converted|

> **Example:** ```dateToStr(date, 'MM/dd/yyy')```<br>
> **Result:** ```20/20/2018```


#### ${\color{blue}now()}$
**Description**: Returns the current date object<br>
**Returns**: **[java.util.Date]** Current date and time



#### ${\color{blue}parseDate()}$
**Description**: Parses specified date string into date object using specified format.<br>
**Returns**: **[java.util.Date]** Parsed date object.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|dateStr|java.lang.String||Date string to be parsed|
|format|java.lang.String||Date format to use|


#### ${\color{blue}toMillis()}$
**Description**: Converts specified date into millis.<br>
**Returns**: **[java.lang.Long]** Millis value

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to be converted|


#### ${\color{blue}today()}$
**Description**: Returns the current date object<br>
**Returns**: **[java.util.Date]** Current date




### Collection Methods
#### ${\color{blue}collectionToString()}$
**Description**: Converts collection of objects into string.<br>
**Returns**: **[java.lang.String]** Converted string

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|lst|java.util.Collection||Collection to be converted|
|prefix|java.lang.String|empty string|Prefix to be used at start of coverted string.|
|delimiter|java.lang.String|comma (,)|Delimiter to be used between the collection elements.|
|suffix|java.lang.String|empty string|Suffix to be used at end of converted string.|
|emptyString|java.lang.String|empty string|String to be used when input list is null or empty.|

> **Example:** ```collectionToString(lst, '[', ' | ', ']', '')```<br>
> **Result:** ```[a | b | c]```

> **Example:** ```collectionToString(null, '[', ' | ', ']', '<empty>')```<br>
> **Result:** ```<empty>```


#### ${\color{blue}contains()}$
**Description**: Checks if the specified collection contains the specified value.<br>
**Returns**: **[boolean]** true if the specified collection contains the specified value, false otherwise.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|collection|java.util.Collection||Collection to be checked|
|value|java.lang.Object||Value to be checked|


#### ${\color{blue}groupBy()}$
**Description**: Groups elements of specified collection based on specified keyExpression<br>
**Returns**: **[java.util.List]** List of groups. Each group has key (value of key based on which current group is created) and elements having same key.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|collection|java.util.Collection||Collection of objects which needs grouping|
|keyExpression|java.lang.String||Freemarker key expression which will be executed on each of collection element. And obtained key will be used for grouping.|


#### ${\color{blue}mapKeys()}$
**Description**: Extracts and returns the keys collection as list of specified map.<br>
**Returns**: **[java.util.Collection]** the values collection of specified map.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|map|java.util.Map||Map whose keys has to be extracted|


#### ${\color{blue}mapToString()}$
**Description**: Converts map of objects into string.<br>
**Returns**: **[java.lang.String]** Converted string

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|map|java.util.Map||Prefix to be used at start of coverted string|
|template|java.lang.String|#key=#value|Template representing how key and value should be converted into string (the string can have #key and #value which will act as place holders).|
|prefix|java.lang.String|empty string|Prefix to be used at start of coverted string.|
|delimiter|java.lang.String|comma (,)|Delimiter to be used between elements.|
|suffix|java.lang.String|empty string|Suffix to be used at end of string.|
|emptyString|java.lang.String|empty string|String that will be returned if input map is null or empty.|

> **Example:** ```mapToString(map, '#key=#value', '[', ' | ', ']', '')```<br>
> **Result:** ```[a=1 | b=2 | c=3]```

> **Example:** ```mapToString(null, '#key=#value', '[', ' | ', ']', '<empty>')```<br>
> **Result:** ```<empty>```


#### ${\color{blue}mapValues()}$
**Description**: Extracts and returns the values collection as list of specified map.<br>
**Returns**: **[java.util.Collection]** the values collection of specified map.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|map|java.util.Map||Map whose values has to be extracted|


#### ${\color{blue}sortBy()}$
**Description**: Sorted elements of specified collection based on specified keyExpression. Duplicate elements (with same key) will be kept together (though internal order is not guaranteed).<br>
**Returns**: **[java.util.List]** List of ordered elements based on specified key expression.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|collection|java.util.Collection||Collection of objects which needs sorting|
|keyExpression|java.lang.String||Freemarker key expression which will be executed on each of collection element. And obtained key will be used for sorting.|



### Common Methods
#### ${\color{blue}ifFalse()}$
**Description**: Used to check if specified value is false and return approp value Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false. If null, the condition will be considered as false (hence returing falseValue)<br>
**Returns**: **[java.lang.Object]** Specified true-condition-value or false-condition-value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for false. Can be boolean true or string 'true'|
|falseValue|java.lang.Object|true|Value to be returned when value is false or null.|
|trueValue|java.lang.Object|false|Value to be returned when value is true.|


#### ${\color{blue}ifNotNull()}$
**Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.<br>
**Returns**: **[java.lang.Object]** ifNull or ifNotNull based on nullCheck.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|nullCheck|java.lang.Object||object to be checked for null|
|ifNotNull|java.lang.Object|true (boolean)|object to be returned if not null.|
|ifNull|java.lang.Object|false (boolean)|object to be returned if null.|


#### ${\color{blue}ifNull()}$
**Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.<br>
**Returns**: **[java.lang.Object]** ifNull or ifNotNull based on nullCheck.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|nullCheck|java.lang.Object||object to be checked for null|
|ifNull|java.lang.Object|true (boolean)|object to be returned if null.|
|ifNotNull|java.lang.Object|false (boolean)|object to be returned if not null|


#### ${\color{blue}ifTrue()}$
**Description**: Used to check if specified value is true and return approp value Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false.<br>
**Returns**: **[java.lang.Object]** Specified true-condition-value or false-condition-value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for true.|
|trueValue|java.lang.Object|true|Value to be returned when value is true.|
|falseValue|java.lang.Object|false|Value to be returned when value is false or null.|


#### ${\color{blue}initcap()}$
**Description**: Makes first letter of every word into capital letter.<br>
**Returns**: **[java.lang.String]** 

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|str|java.lang.String||String to convert|


#### ${\color{blue}isEmpty()}$
**Description**: Used to check if specified value is empty. For collection, map and string, along with null this will check for empty value.<br>
**Returns**: **[boolean]** True if value is empty.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for empty|


#### ${\color{blue}isEqualIgnoreCase()}$
**Description**: Checks if specified values are equal ignoring case.<br>
**Returns**: **[boolean]** True if values are equal ignoring case.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value1|java.lang.String||First value to be compared|
|value2|java.lang.String||Second value to be compared|


#### ${\color{blue}isEqualString()}$
**Description**: Checks if specified values are equal post string conversion.<br>
**Returns**: **[boolean]** True if values are equal.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value1|java.lang.Object||First value to be compared|
|value2|java.lang.Object||Second value to be compared|


#### ${\color{blue}isNotEmpty()}$
**Description**: Used to check if specified value is not empty. For collection, map and string, along with non-null this will check for non-empty value.<br>
**Returns**: **[boolean]** True if value is empty.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for empty|


#### ${\color{blue}isNotNull()}$
**Description**: Used to check if specified value is not null.<br>
**Returns**: **[boolean]** True if value is not null.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for not null|


#### ${\color{blue}isNull()}$
**Description**: Used to check if specified value is null.<br>
**Returns**: **[boolean]** True if value is null.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for null|


#### ${\color{blue}lower()}$
**Description**: Converts specified string to lower case.<br>
**Returns**: **[java.lang.String]** Lower case string.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|str|java.lang.String||String to be converted to lower case|


#### ${\color{blue}nullVal()}$
**Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'nullCheck' will be returned.<br>
**Returns**: **[java.lang.Object]** ifNull or nullCheck based on nullCheck is null or not.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|nullCheck|java.lang.Object||object to be checked for null|
|ifNull|java.lang.Object||object to be returned if null|


#### ${\color{blue}nvl()}$
**Description**: Used to check if specified value is null and return approp value when null and when non-null.<br>
**Returns**: **[java.lang.Object]** Specified null-condition-value or non-null-condition-value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for empty|
|nullValue|java.lang.Object||Value to be returned when value is null|
|nonNullValue|java.lang.Object||Value to be returned when value is non-null|


#### ${\color{blue}replace()}$
**Description**: Replaces specified substring with replacement in main string.<br>
**Returns**: **[java.lang.String]** 

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|mainString|java.lang.String||String in which replacement should happen|
|substring|java.lang.String||Substring to be replaced|
|replacement|java.lang.String||Replacement string|


#### ${\color{blue}sizeOf()}$
**Description**: Used to fetch size of specified value. If string length of string is returned, if collection size of collection is returned, if null zero will be returned. Otherwise 1 will be returned.<br>
**Returns**: **[int]** Size of specified object.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value whose size to be determined|


#### ${\color{blue}strContains()}$
**Description**: Checks if specified substring can be found in main string<br>
**Returns**: **[boolean]** true, if substring can be found.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|mainString|java.lang.String||Main string in which search has to be performed|
|substr|java.lang.String||Substring to be searched|
|ignoreCase|boolean|false|Flag to indicate if case has to be ignored during search|


#### ${\color{blue}toText()}$
**Description**: Used to convert specified object into string. toString() will be invoked on input object to convert<br>
**Returns**: **[java.lang.String]** Converted string. If null, 'null' will be returned.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be converted into string.|


#### ${\color{blue}upper()}$
**Description**: Converts specified string to upper case.<br>
**Returns**: **[java.lang.String]** Upper case string.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|str|java.lang.String||String to be converted to upper case|




