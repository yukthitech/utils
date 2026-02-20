# Transform Free Marker Methods

Below is the list of free marker directives and methods available by default as part of transformation templates.

## Default Directives

### Common Directives
#### <span style="color:blue">indent</span>
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


#### <span style="color:blue">trim</span>
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

### Date Methods
#### <span style="color:blue">addDays()</span>
**Description**: Adds specified number of days to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified days

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which days should be added|
|days|int||Days to be added.|


#### <span style="color:blue">addHours()</span>
**Description**: Adds specified number of hours to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified hours

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which hours should be added|
|hours|int||Hours to be added.|


#### <span style="color:blue">addMinutes()</span>
**Description**: Adds specified number of minutes to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified minutes

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which minutes should be added|
|minutes|int||Minutes to be added.|


#### <span style="color:blue">addSeconds()</span>
**Description**: Adds specified number of seconds to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified seconds

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which seconds should be added|
|seconds|int||Seconds to be added.|


#### <span style="color:blue">addYears()</span>
**Description**: Adds specified number of days to specified date<br>
**Returns**: **[java.util.Date]** Resultant date after addition of specified years

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to which days should be added|
|years|int||Years to be added.|


#### <span style="color:blue">dateToStr()</span>
**Description**: Converts specified date into string in specified format.<br>
**Returns**: **[java.lang.String]** Fromated date string.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to be converted|
|format|java.lang.String||Date format to which date should be converted|

> **Example:** ```dateToStr(date, 'MM/dd/yyy')```<br>
> **Result:** ```20/20/2018```


#### <span style="color:blue">now()</span>
**Description**: Returns the current date object<br>
**Returns**: **[java.util.Date]** Current date and time



#### <span style="color:blue">parseDate()</span>
**Description**: Parses specified date string into date object using specified format.<br>
**Returns**: **[java.util.Date]** Parsed date object.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|dateStr|java.lang.String||Date string to be parsed|
|format|java.lang.String||Date format to use|


#### <span style="color:blue">toMillis()</span>
**Description**: Converts specified date into millis.<br>
**Returns**: **[java.lang.Long]** Millis value

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|date|java.util.Date||Date to be converted|


#### <span style="color:blue">today()</span>
**Description**: Returns the current date object<br>
**Returns**: **[java.util.Date]** Current date




### Transform Methods
#### <span style="color:blue">nullValue()</span>
**Description**: Simply returns null. Helpful in defining null values in xml<br>
**Returns**: **[java.lang.Object]** null



#### <span style="color:blue">safeEval()</span>
**Description**: Evaluates specified expression in safe manner. In case of exception (because of missing path) default value will be returned.<br>
**Returns**: **[java.lang.Object]** Result of expression evaluation or default value if expression evaluation fails.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|expression|java.lang.String||Expression to be evaluated|
|defaultValue|java.lang.Object|null|Default value to be returned if expression evaluation fails|


#### <span style="color:blue">toBoolean()</span>
**Description**: Convert specified object into boolean value.<br>
**Returns**: **[java.lang.Boolean]** Converted value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be converted.|


#### <span style="color:blue">toDouble()</span>
**Description**: Convert specified object into double value.<br>
**Returns**: **[java.lang.Double]** Converted value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be converted.|


#### <span style="color:blue">toFloat()</span>
**Description**: Convert specified object into float value.<br>
**Returns**: **[java.lang.Float]** Converted value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be converted.|


#### <span style="color:blue">toInt()</span>
**Description**: Convert specified object into int value.<br>
**Returns**: **[java.lang.Integer]** Converted value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be converted.|


#### <span style="color:blue">toJson()</span>
**Description**: Used to convert specified object into json string.<br>
**Returns**: **[java.lang.String]** Converted json string.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be converted into json string.|


#### <span style="color:blue">toList()</span>
**Description**: Wraps specified value with a list, if it is single object<br>
**Returns**: **[java.util.List]** Converted value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be converted.|


#### <span style="color:blue">toLong()</span>
**Description**: Convert specified object into long value.<br>
**Returns**: **[java.lang.Long]** Converted value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be converted.|



### Collection Methods
#### <span style="color:blue">collectionToString()</span>
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


#### <span style="color:blue">contains()</span>
**Description**: Checks if the specified collection contains the specified value.<br>
**Returns**: **[boolean]** true if the specified collection contains the specified value, false otherwise.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|collection|java.util.Collection||Collection to be checked|
|value|java.lang.Object||Value to be checked|


#### <span style="color:blue">groupBy()</span>
**Description**: Groups elements of specified collection based on specified keyExpression<br>
**Returns**: **[java.util.List]** List of groups. Each group has key (value of key based on which current group is created) and elements having same key.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|collection|java.util.Collection||Collection of objects which needs grouping|
|keyExpression|java.lang.String||Freemarker key expression which will be executed on each of collection element. And obtained key will be used for grouping.|


#### <span style="color:blue">mapKeys()</span>
**Description**: Extracts and returns the keys collection as list of specified map.<br>
**Returns**: **[java.util.Collection]** the values collection of specified map.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|map|java.util.Map||Map whose keys has to be extracted|


#### <span style="color:blue">mapToString()</span>
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


#### <span style="color:blue">mapValues()</span>
**Description**: Extracts and returns the values collection as list of specified map.<br>
**Returns**: **[java.util.Collection]** the values collection of specified map.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|map|java.util.Map||Map whose values has to be extracted|


#### <span style="color:blue">sortBy()</span>
**Description**: Sorted elements of specified collection based on specified keyExpression. Duplicate elements (with same key) will be kept together (though internal order is not guaranteed).<br>
**Returns**: **[java.util.List]** List of ordered elements based on specified key expression.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|collection|java.util.Collection||Collection of objects which needs sorting|
|keyExpression|java.lang.String||Freemarker key expression which will be executed on each of collection element. And obtained key will be used for sorting.|



### Common Methods
#### <span style="color:blue">ifFalse()</span>
**Description**: Used to check if specified value is false and return approp value Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false. If null, the condition will be considered as false (hence returing falseValue)<br>
**Returns**: **[java.lang.Object]** Specified true-condition-value or false-condition-value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for false. Can be boolean true or string 'true'|
|falseValue|java.lang.Object|true|Value to be returned when value is false or null.|
|trueValue|java.lang.Object|false|Value to be returned when value is true.|


#### <span style="color:blue">ifNotNull()</span>
**Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.<br>
**Returns**: **[java.lang.Object]** ifNull or ifNotNull based on nullCheck.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|nullCheck|java.lang.Object||object to be checked for null|
|ifNotNull|java.lang.Object|true (boolean)|object to be returned if not null.|
|ifNull|java.lang.Object|false (boolean)|object to be returned if null.|


#### <span style="color:blue">ifNull()</span>
**Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.<br>
**Returns**: **[java.lang.Object]** ifNull or ifNotNull based on nullCheck.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|nullCheck|java.lang.Object||object to be checked for null|
|ifNull|java.lang.Object|true (boolean)|object to be returned if null.|
|ifNotNull|java.lang.Object|false (boolean)|object to be returned if not null|


#### <span style="color:blue">ifTrue()</span>
**Description**: Used to check if specified value is true and return approp value Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false.<br>
**Returns**: **[java.lang.Object]** Specified true-condition-value or false-condition-value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for true.|
|trueValue|java.lang.Object|true|Value to be returned when value is true.|
|falseValue|java.lang.Object|false|Value to be returned when value is false or null.|


#### <span style="color:blue">initcap()</span>
**Description**: Makes first letter of every word into capital letter.<br>
**Returns**: **[java.lang.String]** 

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|str|java.lang.String||String to convert|


#### <span style="color:blue">isEmpty()</span>
**Description**: Used to check if specified value is empty. For collection, map and string, along with null this will check for empty value.<br>
**Returns**: **[boolean]** True if value is empty.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for empty|


#### <span style="color:blue">isEqualIgnoreCase()</span>
**Description**: Checks if specified values are equal ignoring case.<br>
**Returns**: **[boolean]** True if values are equal ignoring case.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value1|java.lang.String||First value to be compared|
|value2|java.lang.String||Second value to be compared|


#### <span style="color:blue">isEqualString()</span>
**Description**: Checks if specified values are equal post string conversion.<br>
**Returns**: **[boolean]** True if values are equal.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value1|java.lang.Object||First value to be compared|
|value2|java.lang.Object||Second value to be compared|


#### <span style="color:blue">isNotEmpty()</span>
**Description**: Used to check if specified value is not empty. For collection, map and string, along with non-null this will check for non-empty value.<br>
**Returns**: **[boolean]** True if value is empty.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for empty|


#### <span style="color:blue">isNotNull()</span>
**Description**: Used to check if specified value is not null.<br>
**Returns**: **[boolean]** True if value is not null.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for not null|


#### <span style="color:blue">isNull()</span>
**Description**: Used to check if specified value is null.<br>
**Returns**: **[boolean]** True if value is null.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for null|


#### <span style="color:blue">lower()</span>
**Description**: Converts specified string to lower case.<br>
**Returns**: **[java.lang.String]** Lower case string.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|str|java.lang.String||String to be converted to lower case|


#### <span style="color:blue">nullVal()</span>
**Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'nullCheck' will be returned.<br>
**Returns**: **[java.lang.Object]** ifNull or nullCheck based on nullCheck is null or not.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|nullCheck|java.lang.Object||object to be checked for null|
|ifNull|java.lang.Object||object to be returned if null|


#### <span style="color:blue">nvl()</span>
**Description**: Used to check if specified value is null and return approp value when null and when non-null.<br>
**Returns**: **[java.lang.Object]** Specified null-condition-value or non-null-condition-value.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be checked for empty|
|nullValue|java.lang.Object||Value to be returned when value is null|
|nonNullValue|java.lang.Object||Value to be returned when value is non-null|


#### <span style="color:blue">replace()</span>
**Description**: Replaces specified substring with replacement in main string.<br>
**Returns**: **[java.lang.String]** 

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|mainString|java.lang.String||String in which replacement should happen|
|substring|java.lang.String||Substring to be replaced|
|replacement|java.lang.String||Replacement string|


#### <span style="color:blue">sizeOf()</span>
**Description**: Used to fetch size of specified value. If string length of string is returned, if collection size of collection is returned, if null zero will be returned. Otherwise 1 will be returned.<br>
**Returns**: **[int]** Size of specified object.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value whose size to be determined|


#### <span style="color:blue">strContains()</span>
**Description**: Checks if specified substring can be found in main string<br>
**Returns**: **[boolean]** true, if substring can be found.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|mainString|java.lang.String||Main string in which search has to be performed|
|substr|java.lang.String||Substring to be searched|
|ignoreCase|boolean|false|Flag to indicate if case has to be ignored during search|


#### <span style="color:blue">toText()</span>
**Description**: Used to convert specified object into string. toString() will be invoked on input object to convert<br>
**Returns**: **[java.lang.String]** Converted string. If null, 'null' will be returned.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|value|java.lang.Object||Value to be converted into string.|


#### <span style="color:blue">upper()</span>
**Description**: Converts specified string to upper case.<br>
**Returns**: **[java.lang.String]** Upper case string.

**Parameters**
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
|str|java.lang.String||String to be converted to upper case|




