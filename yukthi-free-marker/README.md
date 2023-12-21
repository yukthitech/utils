# Yukthi Free Marker

This is a wrapper over Apache freemarker (https://freemarker.apache.org/). This library gives ability to configure static java methods as free-marker methods/directives using simple annotations and also give ability to document them.

## Usage
${\color{blue}FreeMarkerEngine}$ is the entry class for this library. This is wrapper over freemarker and provides following functionality:

* **void loadClass(Class<?> clazz)** - Loads the methods marked as freemarker methods/directives into registry. Which in turn can be used in templates processed by this engine instance.
* **String processTemplate(String name, String templateString, Object context)** - Used to process specified template string as a freemaker template, in which registered freemarker methods/directives can be used.
* **boolean evaluateCondition(String name, String condition, Object context)** - Input condition should be a simple freemarker condition expression (which registered freemarker methods/directives can be used).
* **Object fetchValue(String name, String valueExpression, Object context)** - valueExpression should be simple freemarker expression, which gets evaluated and result will be returned (not the result template string but value of expression). Here also registered freemarker methods/directives can be used.

## Custom Free Marker Methods
	
${\color{blue}@FreeMarkerMethod}$ annotation can be used to mark a static method as a free marker method. And optionally ${\color{blue}@FmParam}$ can be used to document parameters.

> **Example:**
> ```java
> {
> 	@FreeMarkerMethod(
> 			description = "Adds specified number of days to specified date",
> 			returnDescription = "Resultant date after addition of specified days")
> 	public static Date addDays(
> 			@FmParam(name = "date", description = "Date to which days should be added") Date date, 
> 			@FmParam(name = "days", description = "Days to be added.") int days)
> 	{
> 		return DateUtils.addDays(date, days);
> 	}
> ```

## Custom Free Marker Directives
	
${\color{blue}@FreeMarkerDirective}$ annotation can be used to mark a static method as a free marker directive. It should be as of same signature as shown in below example (these parameters is same as that custom directive parameters of freemarker).

> **Example:**
> ```java
> 	@FreeMarkerDirective(value = "trim", 
> 			description = "Trims the content enclosed within this directive.",
> 			examples = {
> 				@ExampleDoc(usage = "<@trim>   some content  </@trim>", result = "some content")
> 			})
> 	public static void trim(Environment env, Map<String, String> params, 
		TemplateModel[] loopVars, TemplateDirectiveBody body) throws Exception
> 	{
> 		StringWriter writer = new StringWriter();
> 		body.render(writer);
> 
> 		env.getOut().append(writer.toString().trim());
> 	}
> ```

## Default Directives
Following additional directives by default are supported by this library:

### ${\color{blue}@indent}$
**Description**: Helps in indenting the enclosed content. Accepts optional prefix attribute, defaults to empty string. Every line will be trimmed and converted into single line and prefix will be added at the start. And from the output content '\t' and '\n' will be replaced with tab and new-line characters respectively.<br>

**Parameters**
|Name|Default Value|Description|
|:---|:-----------|:-----------|
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


### ${\color{blue}@trim}$
**Description**: Trims the content enclosed within this directive.<br>


> **Example:** <br>
> **Usage:** 
> ```
> <@trim>   some content  </@trim>
> ```
> **Result:** 
> ```
> some content
> ```




## Custom Free Marker Methods
Following additional freemarker methods by default are supported by this library:

### ${\color{blue}addDays()}$
**Description**: Adds specified number of days to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified days

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which days should be added|
|days|int||Days to be added.|


### ${\color{blue}addHours()}$
**Description**: Adds specified number of hours to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified hours

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which hours should be added|
|hours|int||Hours to be added.|


### ${\color{blue}addMinutes()}$
**Description**: Adds specified number of minutes to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified minutes

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which minutes should be added|
|minutes|int||Minutes to be added.|


### ${\color{blue}addSeconds()}$
**Description**: Adds specified number of seconds to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified seconds

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which seconds should be added|
|seconds|int||Seconds to be added.|


### ${\color{blue}addYears()}$
**Description**: Adds specified number of days to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified years

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which days should be added|
|years|int||Years to be added.|


### ${\color{blue}collectionToString()}$
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


### ${\color{blue}dateToStr()}$
**Description**: Converts specified date into string in specified format.<br>
**Returns**: **[java.lang.String]** Fromated date string.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to be converted|
|format|java.lang.String||Date format to which date should be converted|

> **Example:** ```dateToStr(date, 'MM/dd/yyy')```<br>
> **Result:** ```20/20/2018```


### ${\color{blue}groupBy()}$
**Description**: Groups elements of specified collection based on specified keyExpression<br>
**Returns**: **[java.util.List]** List of groups. Each group has key (value of key based on which current group is created) and elements having same key.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|collection|java.util.Collection||Collection of objects which needs grouping|
|keyExpression|java.lang.String||Freemarker key expression which will be executed on each of collection element. And obtained key will be used for grouping.|


### ${\color{blue}ifFalse()}$
**Description**: Used to check if specified value is false and return approp value Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false. If null, the condition will be considered as false (hence returing falseValue)<br>
**Returns**: **[java.lang.Object]** Specified true-condition-value or false-condition-value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for false. Can be boolean true or string 'true'|
|falseValue|java.lang.Object|true|Value to be returned when value is false or null.|
|trueValue|java.lang.Object|false|Value to be returned when value is true.|


### ${\color{blue}ifNotNull()}$
**Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.<br>
**Returns**: **[java.lang.Object]** ifNull or ifNotNull based on nullCheck.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|nullCheck|java.lang.Object||object to be checked for null|
|ifNotNull|java.lang.Object|true (boolean)|object to be returned if not null.|
|ifNull|java.lang.Object|false (boolean)|object to be returned if null.|


### ${\color{blue}ifNull()}$
**Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.<br>
**Returns**: **[java.lang.Object]** ifNull or ifNotNull based on nullCheck.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|nullCheck|java.lang.Object||object to be checked for null|
|ifNull|java.lang.Object|true (boolean)|object to be returned if null.|
|ifNotNull|java.lang.Object|false (boolean)|object to be returned if not null|


### ${\color{blue}ifTrue()}$
**Description**: Used to check if specified value is true and return approp value Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false.<br>
**Returns**: **[java.lang.Object]** Specified true-condition-value or false-condition-value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for true.|
|trueValue|java.lang.Object|true|Value to be returned when value is true.|
|falseValue|java.lang.Object|false|Value to be returned when value is false or null.|


### ${\color{blue}initcap()}$
**Description**: Makes first letter of every word into capital letter.<br>
**Returns**: **[java.lang.String]** 

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|str|java.lang.String||String to convert|


### ${\color{blue}isEmpty()}$
**Description**: Used to check if specified value is empty. For collection, map and string, along with null this will check for empty value.<br>
**Returns**: **[boolean]** True if value is empty.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for empty|


### ${\color{blue}isNotEmpty()}$
**Description**: Used to check if specified value is not empty. For collection, map and string, along with non-null this will check for non-empty value.<br>
**Returns**: **[boolean]** True if value is empty.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for empty|


### ${\color{blue}mapKeys()}$
**Description**: Extracts and returns the keys collection as list of specified map.<br>
**Returns**: **[java.util.Collection]** the values collection of specified map.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|map|java.util.Map||Map whose keys has to be extracted|


### ${\color{blue}mapToString()}$
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


### ${\color{blue}mapValues()}$
**Description**: Extracts and returns the values collection as list of specified map.<br>
**Returns**: **[java.util.Collection]** the values collection of specified map.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|map|java.util.Map||Map whose values has to be extracted|


### ${\color{blue}now()}$
**Description**: Returns the current date object<br>
**Returns**: **[java.util.Date]** Current date and time



### ${\color{blue}nullVal()}$
**Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'nullCheck' will be returned.<br>
**Returns**: **[java.lang.Object]** ifNull or nullCheck based on nullCheck is null or not.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|nullCheck|java.lang.Object||object to be checked for null|
|ifNull|java.lang.Object||object to be returned if null|


### ${\color{blue}nvl()}$
**Description**: Used to check if specified value is null and return approp value when null and when non-null.<br>
**Returns**: **[java.lang.Object]** Specified null-condition-value or non-null-condition-value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for empty|
|nullValue|java.lang.Object||Value to be returned when value is null|
|nonNullValue|java.lang.Object||Value to be returned when value is non-null|


### ${\color{blue}replace()}$
**Description**: Replaces specified substring with replacement in main string.<br>
**Returns**: **[java.lang.String]** 

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|mainString|java.lang.String||String in which replacement should happen|
|substring|java.lang.String||Substring to be replaced|
|replacement|java.lang.String||Replacement string|


### ${\color{blue}sizeOf()}$
**Description**: Used to fetch size of specified value. If string length of string is returned, if collection size of collection is returned, if null zero will be returned. Otherwise 1 will be returned.<br>
**Returns**: **[int]** Size of specified object.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value whose size to be determined|


### ${\color{blue}sortBy()}$
**Description**: Sorted elements of specified collection based on specified keyExpression. Duplicate elements (with same key) will be kept together (though internal order is not guaranteed).<br>
**Returns**: **[java.util.List]** List of ordered elements based on specified key expression.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|collection|java.util.Collection||Collection of objects which needs sorting|
|keyExpression|java.lang.String||Freemarker key expression which will be executed on each of collection element. And obtained key will be used for sorting.|


### ${\color{blue}strContains()}$
**Description**: Checks if specified substring can be found in main string<br>
**Returns**: **[boolean]** true, if substring can be found.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|mainString|java.lang.String||Main string in which search has to be performed|
|substr|java.lang.String||Substring to be searched|
|ignoreCase|boolean|false|Flag to indicate if case has to be ignored during search|


### ${\color{blue}toMillis()}$
**Description**: Converts specified date into millis.<br>
**Returns**: **[java.lang.Long]** Millis value

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to be converted|


### ${\color{blue}toText()}$
**Description**: Used to convert specified object into string. toString() will be invoked on input object to convert<br>
**Returns**: **[java.lang.String]** Converted string. If null, 'null' will be returned.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be converted into string.|


### ${\color{blue}today()}$
**Description**: Returns the current date object<br>
**Returns**: **[java.util.Date]** Current date




