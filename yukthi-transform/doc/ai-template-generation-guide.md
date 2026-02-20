# AI Template Generation Guide for Yukthi-Transform

This comprehensive guide is designed to help AI agents (like Cursor) generate transformation templates for the yukthi-transform library. It combines all essential information about template syntax, available methods, and best practices.

## Table of Contents

1. [Library Overview](#library-overview)
2. [Quick Start](#quick-start)
3. [Template Syntax Reference](#template-syntax-reference)
4. [FreeMarker Methods Reference](#freemarker-methods-reference)
5. [Common Patterns](#common-patterns)
6. [Best Practices](#best-practices)
7. [Error Prevention](#error-prevention)

---

## Library Overview

**yukthi-transform** is a Java transformation/templating engine that transforms object data from multiple sources (JSON, XML, POJOs) into different forms based on templates.

### Key Features

- **Dual Format Support**: Transform data using JSON or XML templates
- **Multiple Data Sources**: Works with Maps, POJOs, and XML context data
- **Powerful Expressions**: FreeMarker expressions, XPath queries
- **Advanced Features**: Conditions, loops, variables, resource loading, includes, switch statements
- **Type Agnostic**: Transform to any JSON-supported type (String, boolean, int, date, map, list)

### Context Types

The library supports multiple context types:
1. **Map Context** - `Map<String, Object>`
2. **POJO Context** - Any Java object (properties accessible via property names)
3. **Custom Context** - Implement `ITransformContext`

---

## Quick Start

### Basic Usage Pattern

```java
// Create template factory
JsonTemplateFactory factory = new JsonTemplateFactory();
// or
XmlTemplateFactory factory = new XmlTemplateFactory();

// Parse template
TransformTemplate template = factory.parseTemplate(templateString);

// Create transform engine
TransformEngine engine = new TransformEngine();

// Prepare context
Map<String, Object> context = new HashMap<>();
context.put("key", value);

// Process transformation
String result = engine.processAsString(template, context);
```

### Simple JSON Template Example

```json
{
  "book": {
    "@condition": "book.availableCount >= 1",
    "title": "${book.title}",
    "cost": "@fmarker: book.cost"
  }
}
```

### Simple XML Template Example

```xml
<output xmlns:t="/transform">
  <book t:condition="toBoolean(book.availableCount >= 1)" 
        title="${book.title}" 
        cost="@fmarker: book.cost"/>
</output>
```

---

## Template Syntax Reference

### Expression Types

#### 1. Replacement Expressions

Replace entire values (not just strings) with expression results. Can return any JSON-supported type.

**Syntax:**
- `@fmarker:` - FreeMarker expression evaluation
- `@xpath:` - XPath expression (returns first match)
- `@xpathMulti:` - XPath expression (returns all matches)

**JSON Example:**
```json
{
  "bookCost": "@fmarker: book.cost",
  "firstTitle": "@xpath: /books[1]/title",
  "allTitles": "@xpathMulti: /books//title"
}
```

**XML Example:**
```xml
<output xmlns:t="/transform">
  <bookCost t:value="@fmarker: book.cost"/>
  <firstTitle t:value="@xpath: /books[1]/title"/>
  <allTitles t:value="@xpathMulti: /books//title"/>
</output>
```

#### 2. FreeMarker Expressions

Standard FreeMarker syntax in string values:
- `${variable}` - Variable interpolation
- `<#if condition>...</#if>` - Conditionals
- All FreeMarker features supported

**JSON Example:**
```json
{
  "name": "${user.name}",
  "message": "Hello ${user.firstName} ${user.lastName}"
}
```

**XML Example:**
```xml
<output xmlns:t="/transform">
  <name>${user.name}</name>
  <message>Hello ${user.firstName} ${user.lastName}</message>
</output>
```

### Conditions

#### JSON Templates

**Map Objects:**
```json
{
  "book": {
    "@condition": "book.availableCount >= 1",
    "title": "${book.title}"
  }
}
```

**Lists:**
```json
{
  "books": ["@condition: library.enabled", "item1", "item2"]
}
```

**Simple Values:**
```json
{
  "enabled": {
    "@condition": "book.available == 1",
    "@value": "Enabled",
    "@falseValue": "Disabled"
  }
}
```

#### XML Templates

**Element Conditions:**
```xml
<output xmlns:t="/transform">
  <book t:condition="toBoolean(book.availableCount >= 1)">
    <title>${book.title}</title>
  </book>
</output>
```

**Simple Value Conditions:**
```xml
<output xmlns:t="/transform">
  <enabled t:condition="toBoolean(book.available)" 
           t:value="Enabled" 
           t:falseValue="Disabled"/>
</output>
```

**Important Notes:**
- XML attributes are strings - use type conversion functions: `toBoolean()`, `toInt()`, `toDouble()`
- Use XML-safe operators: `lt`, `gt`, `gte`, `lte` instead of `<`, `>`, `>=`, `<=`

### Loops

#### JSON Templates

**Basic Loop:**
```json
{
  "books": [
    {
      "@for-each(book)": "books",
      "title": "${book.title}",
      "author": "${book.author}"
    }
  ]
}
```

**Loop with Condition:**
```json
{
  "books": [
    {
      "@for-each(book)": "books",
      "@for-each-condition": "book.copyCount > 0",
      "title": "${book.title}"
    }
  ]
}
```

**Global Condition on Loop:**
```json
{
  "books": [
    {
      "@condition": "libraries.Justbooks.open",
      "@for-each(book)": "libraries.Justbooks.books",
      "title": "${book.title}"
    }
  ]
}
```

#### XML Templates

**Basic Loop:**
```xml
<output xmlns:t="/transform">
  <book t:for-each="books" t:loop-var="book">
    <title>${book.title}</title>
    <author>${book.author}</author>
  </book>
</output>
```

**Loop with Condition:**
```xml
<output xmlns:t="/transform">
  <book t:for-each="books" 
        t:loop-var="book"
        t:for-each-condition="toInt(book.copyCount) gt 0">
    <title>${book.title}</title>
  </book>
</output>
```

**Global Condition on Loop:**
```xml
<output xmlns:t="/transform">
  <book t:condition="toBoolean(libraries.Justbooks.open)"
        t:for-each="libraries.Justbooks.books" 
        t:loop-var="book">
    <title>${book.title}</title>
  </book>
</output>
```

### Variables

#### JSON Templates

```json
{
  "@set(bookMap)": {
    "titles": "@xpathMulti: /books//title"
  },
  "firstTitle": "@fmarker: bookMap.titles[0]"
}
```

#### XML Templates

**With Text Content:**
```xml
<output xmlns:t="/transform">
  <t:set name="secondLib">@xpath: /libraries/Imagine/name</t:set>
  <library>@fmarker: secondLib</library>
</output>
```

**With Element Content:**
```xml
<output xmlns:t="/transform">
  <t:set name="libJb">
    <bookNames t:value="@xpathMulti: /libraries/Justbooks/books//title"/>
  </t:set>
  <names t:value="@fmarker: libJb.bookNames"/>
</output>
```

### Switch Statements

#### JSON Templates

```json
{
  "grade": {
    "@switch": [
      {
        "@case": "score gte 90",
        "@value": "A"
      },
      {
        "@case": "score gte 80",
        "@value": "B"
      },
      {
        "@value": "F"
      }
    ]
  }
}
```

**Rules:**
- Cases are evaluated in order
- First matching case is used
- Last case without `@case` is the default case
- Default case must be the last case
- Every case must have `@value` specified

#### XML Templates

**Compact Syntax:**
```xml
<output xmlns:t="/transform">
  <grade>
    <t:switch>
      <t:case t:condition="toInt(data.score) gte 90" t:value="A"/>
      <t:case t:condition="toInt(data.score) gte 80" t:value="B"/>
      <t:case t:value="F"/>
    </t:switch>
  </grade>
</output>
```

**Full Syntax:**
```xml
<output xmlns:t="/transform">
  <grade>
    <t:switch>
      <t:case>
        <t:condition>toInt(data.score) gte 90</t:condition>
        <t:value>A</t:value>
      </t:case>
      <t:case t:value="F"/>
    </t:switch>
  </grade>
</output>
```

### Resource Loading

#### JSON Templates

```json
{
  "result": {
    "@resource": "/res-file.txt",
    "@expressions": true,
    "@resParams": {
      "key1": "val1"
    },
    "@transform": "@fmarker: normalizeXml(thisValue)"
  }
}
```

#### XML Templates

```xml
<output xmlns:t="/transform">
  <result>
    <t:resource path="/res-file.txt" expressions="true">
      <t:params>
        <key1>val1</key1>
      </t:params>
    </t:resource>
  </result>
</output>
```

**Notes:**
- Parameters accessible as `resParams.key1` in resource content
- `@expressions` / `expressions` defaults to `true`
- `@transform` / `transform` receives current value as `thisValue`

### Including Other Templates

#### JSON Templates

**Basic Include:**
```json
{
  "key1": "@fmarker: data.ckey1",
  "extra": {
    "@includeResource": "/include-res.json"
  }
}
```

**Replace Entry (Merge):**
```json
{
  "key1": "@fmarker: data.ckey1",
  "@replace(extra)": {
    "@includeResource": "/include-res.json"
  }
}
```

**With Parameters:**
```json
{
  "mainMap": {
    "@includeResource": "/include-recursive.json",
    "@params": {
      "value": "@fmarker: 3"
    }
  }
}
```

#### XML Templates

**Basic Include:**
```xml
<output xmlns:t="/transform">
  <key1>@fmarker: data.ckey1</key1>
  <extra>
    <t:includeResource path="/include-res.xml"/>
  </extra>
</output>
```

**Replace Entry (Merge):**
```xml
<output xmlns:t="/transform">
  <key1>@fmarker: data.ckey1</key1>
  <t:replace>
    <t:includeResource path="/include-res.xml"/>
  </t:replace>
</output>
```

**With Parameters:**
```xml
<output xmlns:t="/transform">
  <mainMap>
    <t:includeResource path="/include-recursive.xml">
      <t:params>
        <value>@fmarker: 3</value>
      </t:params>
    </t:includeResource>
  </mainMap>
</output>
```

**Notes:**
- Parameters accessible as `params` in included template
- `@replace(name)` merges included entries into parent (name is just a key differentiator)

### Transformation

Use `@transform` / `t:transform` to transform values. Current value available as `thisValue`.

#### JSON Templates

```json
{
  "result": {
    "@transform": "@fmarker: toJson(thisValue)",
    "@value": {
      "someVal": 1,
      "books": "@fmarker: books"
    }
  }
}
```

#### XML Templates

```xml
<output xmlns:t="/transform">
  <result t:transform="@fmarker: toJson(thisValue)">
    <someVal>1</someVal>
    <libNames t:value="@xpathMulti: /libraries/Justbooks//name"/>
  </result>
</output>
```

### Dynamic Element Names (XML Only)

```xml
<output xmlns:t="/transform">
  <Justbooks t:name="Justbooks-${libraries.Justbooks.open}" 
             status="${libraries.Justbooks.open}"/>
  <Imagine t:name="@fmarker: mapValues(libraries)[1].name">
    <status>${libraries.Imagine.open}</status>
  </Imagine>
</output>
```

### Directives Summary

#### JSON Directives

| Directive | Usage | Description |
|-----------|-------|-------------|
| `@fmarker:` | Value replacement | FreeMarker expression |
| `@xpath:` | Value replacement | XPath (first match) |
| `@xpathMulti:` | Value replacement | XPath (all matches) |
| `@condition` | Map key | Conditional inclusion |
| `@value` | Map key | Value when condition true |
| `@falseValue` | Map key | Value when condition false |
| `@condition:` | List first element | Conditional list inclusion |
| `@for-each(var):` | Map/list key | Loop definition |
| `@for-each-condition` | Map key | Loop filter condition |
| `@set(var):` | Map key | Variable definition |
| `@resource` | Map key | Load resource |
| `@includeResource` | Map key | Include resource template |
| `@includeFile` | Map key | Include file template |
| `@replace(name)` | Map key | Replace with included entries |
| `@transform` | Map key | Transform value |
| `@params` | Map key | Parameters for includes |
| `@expressions` | Map key | Enable/disable expressions in resource |
| `@resParams` | Map key | Parameters for resource |
| `@switch` | Map key | Switch statement |
| `@case` | Map key in switch | Switch case condition |

#### XML Directives (t: namespace)

| Directive | Usage | Description |
|-----------|-------|-------------|
| `t:condition` | Attribute | Conditional inclusion |
| `t:value` | Attribute | Value when condition true |
| `t:falseValue` | Attribute | Value when condition false |
| `t:for-each` | Attribute | Loop collection |
| `t:loop-var` | Attribute | Loop variable name |
| `t:for-each-condition` | Attribute | Loop filter condition |
| `t:name` | Attribute | Dynamic element name |
| `t:transform` | Attribute | Transform value |
| `<t:set>` | Element | Variable definition |
| `<t:resource>` | Element | Load resource |
| `<t:includeResource>` | Element | Include resource template |
| `<t:includeFile>` | Element | Include file template |
| `<t:replace>` | Element | Replace with included entries |
| `<t:switch>` | Element | Switch statement |
| `<t:case>` | Element | Switch case |
| `t:expressions` | Attribute | Enable/disable expressions in resource |
| `<t:params>` | Element | Parameters for includes/resource |

**Important:** XML templates must declare namespace: `xmlns:t="/transform"`

---

## FreeMarker Methods Reference

All methods below are available in transformation templates for both JSON and XML formats.

### Default Directives

#### `indent`
Helps in indenting the enclosed content. Accepts optional prefix attribute, defaults to empty string. Every line will be trimmed and converted into single line and prefix will be added at the start. And from the output content '\t' and '\n' will be replaced with tab and new-line characters respectively.

**Parameters:**
- `body` - Enclosing body content
- `prefix` - (default: empty string) If specified, this value will be added in start of every line
- `retainLineBreaks` - (default: false) [boolean] if true, lines will be maintained as separate lines.

**Example:**
```
<@indent prefix='--' retainLineBreaks=true>   first line
second line    </@indent>
```
**Result:**
```
--first line
--second line
```

#### `trim`
Trims the content enclosed within this directive.

**Parameters:**
- `body` - Enclosing body content

**Example:**
```
<@trim>   some content  </@trim>
```
**Result:**
```
some content
```

### Date Methods

#### `addDays(date, days)`
Adds specified number of days to specified date.

**Returns:** `java.util.Date` - Resultant date after addition of specified days

**Parameters:**
- `date` - `java.util.Date` - Date to which days should be added
- `days` - `int` - Days to be added

#### `addHours(date, hours)`
Adds specified number of hours to specified date.

**Returns:** `java.util.Date` - Resultant date after addition of specified hours

**Parameters:**
- `date` - `java.util.Date` - Date to which hours should be added
- `hours` - `int` - Hours to be added

#### `addMinutes(date, minutes)`
Adds specified number of minutes to specified date.

**Returns:** `java.util.Date` - Resultant date after addition of specified minutes

**Parameters:**
- `date` - `java.util.Date` - Date to which minutes should be added
- `minutes` - `int` - Minutes to be added

#### `addSeconds(date, seconds)`
Adds specified number of seconds to specified date.

**Returns:** `java.util.Date` - Resultant date after addition of specified seconds

**Parameters:**
- `date` - `java.util.Date` - Date to which seconds should be added
- `seconds` - `int` - Seconds to be added

#### `addYears(date, years)`
Adds specified number of years to specified date.

**Returns:** `java.util.Date` - Resultant date after addition of specified years

**Parameters:**
- `date` - `java.util.Date` - Date to which years should be added
- `years` - `int` - Years to be added

#### `dateToStr(date, format)`
Converts specified date into string in specified format.

**Returns:** `java.lang.String` - Formatted date string

**Parameters:**
- `date` - `java.util.Date` - Date to be converted
- `format` - `java.lang.String` - Date format to which date should be converted

**Example:** `dateToStr(date, 'MM/dd/yyyy')` → `20/20/2018`

#### `now()`
Returns the current date object.

**Returns:** `java.util.Date` - Current date and time

#### `parseDate(dateStr, format)`
Parses specified date string into date object using specified format.

**Returns:** `java.util.Date` - Parsed date object

**Parameters:**
- `dateStr` - `java.lang.String` - Date string to be parsed
- `format` - `java.lang.String` - Date format to use

#### `toMillis(date)`
Converts specified date into millis.

**Returns:** `java.lang.Long` - Millis value

**Parameters:**
- `date` - `java.util.Date` - Date to be converted

#### `today()`
Returns the current date object.

**Returns:** `java.util.Date` - Current date

### Transform Methods

#### `nullValue()`
Simply returns null. Helpful in defining null values in xml.

**Returns:** `java.lang.Object` - null

#### `safeEval(expression, defaultValue)`
Evaluates specified expression in safe manner. In case of exception (because of missing path) default value will be returned.

**Returns:** `java.lang.Object` - Result of expression evaluation or default value if expression evaluation fails

**Parameters:**
- `expression` - `java.lang.String` - Expression to be evaluated
- `defaultValue` - `java.lang.Object` (default: null) - Default value to be returned if expression evaluation fails

#### `toBoolean(value)`
Convert specified object into boolean value.

**Returns:** `java.lang.Boolean` - Converted value

**Parameters:**
- `value` - `java.lang.Object` - Value to be converted

#### `toDouble(value)`
Convert specified object into double value.

**Returns:** `java.lang.Double` - Converted value

**Parameters:**
- `value` - `java.lang.Object` - Value to be converted

#### `toFloat(value)`
Convert specified object into float value.

**Returns:** `java.lang.Float` - Converted value

**Parameters:**
- `value` - `java.lang.Object` - Value to be converted

#### `toInt(value)`
Convert specified object into int value.

**Returns:** `java.lang.Integer` - Converted value

**Parameters:**
- `value` - `java.lang.Object` - Value to be converted

#### `toJson(value)`
Used to convert specified object into json string.

**Returns:** `java.lang.String` - Converted json string

**Parameters:**
- `value` - `java.lang.Object` - Value to be converted into json string

#### `toList(value)`
Wraps specified value with a list, if it is single object.

**Returns:** `java.util.List` - Converted value

**Parameters:**
- `value` - `java.lang.Object` - Value to be converted

#### `toLong(value)`
Convert specified object into long value.

**Returns:** `java.lang.Long` - Converted value

**Parameters:**
- `value` - `java.lang.Object` - Value to be converted

### Collection Methods

#### `collectionToString(lst, prefix, delimiter, suffix, emptyString)`
Converts collection of objects into string.

**Returns:** `java.lang.String` - Converted string

**Parameters:**
- `lst` - `java.util.Collection` - Collection to be converted
- `prefix` - `java.lang.String` (default: empty string) - Prefix to be used at start of converted string
- `delimiter` - `java.lang.String` (default: comma (,)) - Delimiter to be used between the collection elements
- `suffix` - `java.lang.String` (default: empty string) - Suffix to be used at end of converted string
- `emptyString` - `java.lang.String` (default: empty string) - String to be used when input list is null or empty

**Example:** `collectionToString(lst, '[', ' | ', ']', '')` → `[a | b | c]`

**Example:** `collectionToString(null, '[', ' | ', ']', '<empty>')` → `<empty>`

#### `contains(collection, value)`
Checks if the specified collection contains the specified value.

**Returns:** `boolean` - true if the specified collection contains the specified value, false otherwise

**Parameters:**
- `collection` - `java.util.Collection` - Collection to be checked
- `value` - `java.lang.Object` - Value to be checked

#### `groupBy(collection, keyExpression)`
Groups elements of specified collection based on specified keyExpression.

**Returns:** `java.util.List` - List of groups. Each group has key (value of key based on which current group is created) and elements having same key.

**Parameters:**
- `collection` - `java.util.Collection` - Collection of objects which needs grouping
- `keyExpression` - `java.lang.String` - Freemarker key expression which will be executed on each of collection element. And obtained key will be used for grouping.

#### `mapKeys(map)`
Extracts and returns the keys collection as list of specified map.

**Returns:** `java.util.Collection` - the keys collection of specified map

**Parameters:**
- `map` - `java.util.Map` - Map whose keys has to be extracted

#### `mapToString(map, template, prefix, delimiter, suffix, emptyString)`
Converts map of objects into string.

**Returns:** `java.lang.String` - Converted string

**Parameters:**
- `map` - `java.util.Map` - Map to be converted
- `template` - `java.lang.String` (default: `#key=#value`) - Template representing how key and value should be converted into string (the string can have #key and #value which will act as place holders)
- `prefix` - `java.lang.String` (default: empty string) - Prefix to be used at start of converted string
- `delimiter` - `java.lang.String` (default: comma (,)) - Delimiter to be used between elements
- `suffix` - `java.lang.String` (default: empty string) - Suffix to be used at end of string
- `emptyString` - `java.lang.String` (default: empty string) - String that will be returned if input map is null or empty

**Example:** `mapToString(map, '#key=#value', '[', ' | ', ']', '')` → `[a=1 | b=2 | c=3]`

**Example:** `mapToString(null, '#key=#value', '[', ' | ', ']', '<empty>')` → `<empty>`

#### `mapValues(map)`
Extracts and returns the values collection as list of specified map.

**Returns:** `java.util.Collection` - the values collection of specified map

**Parameters:**
- `map` - `java.util.Map` - Map whose values has to be extracted

#### `sortBy(collection, keyExpression)`
Sorted elements of specified collection based on specified keyExpression. Duplicate elements (with same key) will be kept together (though internal order is not guaranteed).

**Returns:** `java.util.List` - List of ordered elements based on specified key expression

**Parameters:**
- `collection` - `java.util.Collection` - Collection of objects which needs sorting
- `keyExpression` - `java.lang.String` - Freemarker key expression which will be executed on each of collection element. And obtained key will be used for sorting

### Common Methods

#### `ifFalse(value, falseValue, trueValue)`
Used to check if specified value is false and return appropriate value. Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false. If null, the condition will be considered as false (hence returning falseValue).

**Returns:** `java.lang.Object` - Specified true-condition-value or false-condition-value

**Parameters:**
- `value` - `java.lang.Object` - Value to be checked for false. Can be boolean true or string 'true'
- `falseValue` - `java.lang.Object` (default: true) - Value to be returned when value is false or null
- `trueValue` - `java.lang.Object` (default: false) - Value to be returned when value is true

#### `ifNotNull(nullCheck, ifNotNull, ifNull)`
If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.

**Returns:** `java.lang.Object` - ifNull or ifNotNull based on nullCheck

**Parameters:**
- `nullCheck` - `java.lang.Object` - object to be checked for null
- `ifNotNull` - `java.lang.Object` (default: true (boolean)) - object to be returned if not null
- `ifNull` - `java.lang.Object` (default: false (boolean)) - object to be returned if null

#### `ifNull(nullCheck, ifNull, ifNotNull)`
If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.

**Returns:** `java.lang.Object` - ifNull or ifNotNull based on nullCheck

**Parameters:**
- `nullCheck` - `java.lang.Object` - object to be checked for null
- `ifNull` - `java.lang.Object` (default: true (boolean)) - object to be returned if null
- `ifNotNull` - `java.lang.Object` (default: false (boolean)) - object to be returned if not null

#### `ifTrue(value, trueValue, falseValue)`
Used to check if specified value is true and return appropriate value. Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false.

**Returns:** `java.lang.Object` - Specified true-condition-value or false-condition-value

**Parameters:**
- `value` - `java.lang.Object` - Value to be checked for true
- `trueValue` - `java.lang.Object` (default: true) - Value to be returned when value is true
- `falseValue` - `java.lang.Object` (default: false) - Value to be returned when value is false or null

#### `initcap(str)`
Makes first letter of every word into capital letter.

**Returns:** `java.lang.String`

**Parameters:**
- `str` - `java.lang.String` - String to convert

#### `isEmpty(value)`
Used to check if specified value is empty. For collection, map and string, along with null this will check for empty value.

**Returns:** `boolean` - True if value is empty

**Parameters:**
- `value` - `java.lang.Object` - Value to be checked for empty

#### `isEqualIgnoreCase(value1, value2)`
Checks if specified values are equal ignoring case.

**Returns:** `boolean` - True if values are equal ignoring case

**Parameters:**
- `value1` - `java.lang.String` - First value to be compared
- `value2` - `java.lang.String` - Second value to be compared

#### `isEqualString(value1, value2)`
Checks if specified values are equal post string conversion.

**Returns:** `boolean` - True if values are equal

**Parameters:**
- `value1` - `java.lang.Object` - First value to be compared
- `value2` - `java.lang.Object` - Second value to be compared

#### `isNotEmpty(value)`
Used to check if specified value is not empty. For collection, map and string, along with non-null this will check for non-empty value.

**Returns:** `boolean` - True if value is not empty

**Parameters:**
- `value` - `java.lang.Object` - Value to be checked for empty

#### `isNotNull(value)`
Used to check if specified value is not null.

**Returns:** `boolean` - True if value is not null

**Parameters:**
- `value` - `java.lang.Object` - Value to be checked for not null

#### `isNull(value)`
Used to check if specified value is null.

**Returns:** `boolean` - True if value is null

**Parameters:**
- `value` - `java.lang.Object` - Value to be checked for null

#### `lower(str)`
Converts specified string to lower case.

**Returns:** `java.lang.String` - Lower case string

**Parameters:**
- `str` - `java.lang.String` - String to be converted to lower case

#### `nullVal(nullCheck, ifNull)`
If 'nullCheck' is null, 'ifNull' will be returned otherwise 'nullCheck' will be returned.

**Returns:** `java.lang.Object` - ifNull or nullCheck based on nullCheck is null or not

**Parameters:**
- `nullCheck` - `java.lang.Object` - object to be checked for null
- `ifNull` - `java.lang.Object` - object to be returned if null

#### `nvl(value, nullValue, nonNullValue)`
Used to check if specified value is null and return appropriate value when null and when non-null.

**Returns:** `java.lang.Object` - Specified null-condition-value or non-null-condition-value

**Parameters:**
- `value` - `java.lang.Object` - Value to be checked for empty
- `nullValue` - `java.lang.Object` - Value to be returned when value is null
- `nonNullValue` - `java.lang.Object` - Value to be returned when value is non-null

#### `replace(mainString, substring, replacement)`
Replaces specified substring with replacement in main string.

**Returns:** `java.lang.String`

**Parameters:**
- `mainString` - `java.lang.String` - String in which replacement should happen
- `substring` - `java.lang.String` - Substring to be replaced
- `replacement` - `java.lang.String` - Replacement string

#### `sizeOf(value)`
Used to fetch size of specified value. If string length of string is returned, if collection size of collection is returned, if null zero will be returned. Otherwise 1 will be returned.

**Returns:** `int` - Size of specified object

**Parameters:**
- `value` - `java.lang.Object` - Value whose size to be determined

#### `strContains(mainString, substr, ignoreCase)`
Checks if specified substring can be found in main string.

**Returns:** `boolean` - true, if substring can be found

**Parameters:**
- `mainString` - `java.lang.String` - Main string in which search has to be performed
- `substr` - `java.lang.String` - Substring to be searched
- `ignoreCase` - `boolean` (default: false) - Flag to indicate if case has to be ignored during search

#### `toText(value)`
Used to convert specified object into string. toString() will be invoked on input object to convert.

**Returns:** `java.lang.String` - Converted string. If null, 'null' will be returned

**Parameters:**
- `value` - `java.lang.Object` - Value to be converted into string

#### `upper(str)`
Converts specified string to upper case.

**Returns:** `java.lang.String` - Upper case string

**Parameters:**
- `str` - `java.lang.String` - String to be converted to upper case

---

## Common Patterns

### Pattern 1: Conditional Object Inclusion

**JSON:**
```json
{
  "user": {
    "@condition": "user.active",
    "name": "${user.name}",
    "email": "${user.email}"
  }
}
```

**XML:**
```xml
<output xmlns:t="/transform">
  <user t:condition="toBoolean(user.active)">
    <name>${user.name}</name>
    <email>${user.email}</email>
  </user>
</output>
```

### Pattern 2: List Transformation with Filter

**JSON:**
```json
{
  "activeUsers": [
    {
      "@for-each(user)": "users",
      "@for-each-condition": "user.active == true",
      "name": "${user.name}",
      "id": "@fmarker: user.id"
    }
  ]
}
```

**XML:**
```xml
<output xmlns:t="/transform">
  <activeUsers>
    <user t:for-each="users" 
          t:loop-var="user"
          t:for-each-condition="toBoolean(user.active)">
      <name>${user.name}</name>
      <id>@fmarker: user.id</id>
    </user>
  </activeUsers>
</output>
```

### Pattern 3: Null-Safe Value Access

**JSON:**
```json
{
  "description": {
    "@condition": "isNotNull(book.description)",
    "@value": "${book.description}",
    "@falseValue": "No description available"
  }
}
```

**XML:**
```xml
<output xmlns:t="/transform">
  <description t:condition="isNotNull(book.description)"
               t:value="${book.description}"
               t:falseValue="No description available"/>
</output>
```

### Pattern 4: Date Formatting

**JSON:**
```json
{
  "formattedDate": "@fmarker: dateToStr(book.publishDate, 'yyyy-MM-dd')",
  "currentDate": "@fmarker: dateToStr(now(), 'yyyy-MM-dd HH:mm:ss')"
}
```

**XML:**
```xml
<output xmlns:t="/transform">
  <formattedDate t:value="@fmarker: dateToStr(book.publishDate, 'yyyy-MM-dd')"/>
  <currentDate t:value="@fmarker: dateToStr(now(), 'yyyy-MM-dd HH:mm:ss')"/>
</output>
```

### Pattern 5: Collection Operations

**JSON:**
```json
{
  "bookCount": "@fmarker: sizeOf(books)",
  "bookTitles": "@fmarker: collectionToString(books.*title, '[', ', ', ']', 'No books')",
  "hasBooks": "@fmarker: isNotEmpty(books)"
}
```

**XML:**
```xml
<output xmlns:t="/transform">
  <bookCount t:value="@fmarker: sizeOf(books)"/>
  <bookTitles t:value="@fmarker: collectionToString(books.*title, '[', ', ', ']', 'No books')"/>
  <hasBooks t:value="@fmarker: isNotEmpty(books)"/>
</output>
```

### Pattern 6: Switch-Based Status Mapping

**JSON:**
```json
{
  "status": {
    "@switch": [
      {
        "@case": "user.status == 'active'",
        "@value": "Enabled"
      },
      {
        "@case": "user.status == 'pending'",
        "@value": "Pending"
      },
      {
        "@value": "Unknown"
      }
    ]
  }
}
```

**XML:**
```xml
<output xmlns:t="/transform">
  <status>
    <t:switch>
      <t:case t:condition="user.status == 'active'" t:value="Enabled"/>
      <t:case t:condition="user.status == 'pending'" t:value="Pending"/>
      <t:case t:value="Unknown"/>
    </t:switch>
  </status>
</output>
```

### Pattern 7: Variable Reuse

**JSON:**
```json
{
  "@set(fullName)": "@fmarker: user.firstName + ' ' + user.lastName",
  "greeting": "Hello ${fullName}",
  "signature": "Best regards, ${fullName}"
}
```

**XML:**
```xml
<output xmlns:t="/transform">
  <t:set name="fullName">@fmarker: user.firstName + ' ' + user.lastName</t:set>
  <greeting>Hello ${fullName}</greeting>
  <signature>Best regards, ${fullName}</signature>
</output>
```

### Pattern 8: Nested Object Transformation

**JSON:**
```json
{
  "library": {
    "@condition": "isNotNull(library)",
    "name": "${library.name}",
    "books": [
      {
        "@for-each(book)": "library.books",
        "title": "${book.title}",
        "available": "@fmarker: book.copyCount > 0"
      }
    ]
  }
}
```

**XML:**
```xml
<output xmlns:t="/transform">
  <library t:condition="isNotNull(library)">
    <name>${library.name}</name>
    <books>
      <book t:for-each="library.books" t:loop-var="book">
        <title>${book.title}</title>
        <available>@fmarker: book.copyCount > 0</available>
      </book>
    </books>
  </library>
</output>
```

---

## Best Practices

### 1. Template Organization
- **Use variables** for complex expressions that are used multiple times
- **Use includes** to split large templates into manageable pieces
- **Cache templates** - Parse templates once and reuse them

### 2. Error Handling
- **Use conditions** to handle optional data gracefully
- **Use `safeEval()`** for expressions that might fail
- **Use `nullVal()` or `nvl()`** for null-safe value access
- **Check for null/empty** before accessing nested properties

### 3. Type Safety (XML)
- **Always use type conversion** in XML conditions: `toBoolean()`, `toInt()`, `toDouble()`
- **Use XML-safe operators**: `lt`, `gt`, `gte`, `lte` instead of `<`, `>`, `>=`, `<=`
- **Use `&&` and `||`** for logical operators

### 4. Performance
- **Avoid deep nesting** - Keep template structure flat when possible
- **Use loops efficiently** - Apply conditions at loop level when filtering
- **Minimize expression evaluation** - Store results in variables when reused

### 5. Readability
- **Use meaningful variable names** - Make templates self-documenting
- **Group related fields** - Organize template structure logically
- **Add comments in FreeMarker** - Use `<#-- comment -->` syntax

### 6. Data Transformation
- **Use `toJson()`** to convert objects to JSON strings when needed
- **Use `toList()`** to normalize single objects to lists
- **Use collection methods** for string formatting: `collectionToString()`, `mapToString()`

---

## Error Prevention

### Common Mistakes to Avoid

1. **Missing XML Namespace**
   - ❌ Wrong: `<output><book>...</book></output>`
   - ✅ Correct: `<output xmlns:t="/transform"><book>...</book></output>`

2. **Missing Type Conversion in XML**
   - ❌ Wrong: `t:condition="book.count > 0"`
   - ✅ Correct: `t:condition="toInt(book.count) gt 0"`

3. **Using Wrong Operators in XML**
   - ❌ Wrong: `t:condition="score >= 90"`
   - ✅ Correct: `t:condition="toInt(score) gte 90"`

4. **Switch Default Case Not Last**
   - ❌ Wrong: Default case in middle of switch
   - ✅ Correct: Default case must be the last case

5. **Missing @value in Switch Case**
   - ❌ Wrong: Case without `@value` or `t:value`
   - ✅ Correct: Every case must have value specified

6. **Incorrect Loop Variable Reference**
   - ❌ Wrong: Using wrong variable name in loop body
   - ✅ Correct: Use the variable name specified in `@for-each(var)` or `t:loop-var`

7. **Missing Condition Evaluation**
   - ❌ Wrong: `@condition: "value"` (string literal)
   - ✅ Correct: `@condition: "value == 'expected'"` (expression)

### Validation Checklist

When generating templates, ensure:

- [ ] XML templates have `xmlns:t="/transform"` namespace declaration
- [ ] All XML conditions use type conversion functions (`toBoolean()`, `toInt()`, etc.)
- [ ] XML uses safe operators (`lt`, `gt`, `gte`, `lte`, `&&`, `||`)
- [ ] Switch statements have default case as last case
- [ ] All switch cases have `@value` or `t:value` specified
- [ ] Loop variable names match between definition and usage
- [ ] Conditions are valid FreeMarker expressions
- [ ] Replacement expressions use correct prefixes (`@fmarker:`, `@xpath:`, `@xpathMulti:`)
- [ ] Variables are defined before use
- [ ] Resource paths are correct (start with `/` for classpath resources)

---

## Quick Reference

### Expression Prefixes
- `@fmarker:` - FreeMarker expression (any type)
- `@xpath:` - XPath expression (first match)
- `@xpathMulti:` - XPath expression (all matches)
- `${...}` - FreeMarker string interpolation

### JSON Special Keys
- `@condition` - Conditional inclusion
- `@value` / `@falseValue` - Conditional values
- `@for-each(var):` - Loop definition
- `@for-each-condition` - Loop filter
- `@set(var):` - Variable definition
- `@switch` / `@case` - Switch statement
- `@resource` - Load resource
- `@includeResource` / `@includeFile` - Include template
- `@replace(name)` - Merge included entries
- `@transform` - Transform value

### XML Special Attributes (t: namespace)
- `t:condition` - Conditional inclusion
- `t:value` / `t:falseValue` - Conditional values
- `t:for-each` - Loop collection
- `t:loop-var` - Loop variable name
- `t:for-each-condition` - Loop filter
- `t:name` - Dynamic element name
- `t:transform` - Transform value

### XML Special Elements (t: namespace)
- `<t:set>` - Variable definition
- `<t:switch>` / `<t:case>` - Switch statement
- `<t:resource>` - Load resource
- `<t:includeResource>` / `<t:includeFile>` - Include template
- `<t:replace>` - Merge included entries
- `<t:params>` - Parameters for includes/resource

### Type Conversion Functions (Important for XML)
- `toBoolean(value)` - Convert to boolean
- `toInt(value)` - Convert to integer
- `toLong(value)` - Convert to long
- `toFloat(value)` - Convert to float
- `toDouble(value)` - Convert to double
- `toJson(value)` - Convert to JSON string
- `toList(value)` - Wrap in list

### Null/Empty Checking Functions
- `isNull(value)` - Check if null
- `isNotNull(value)` - Check if not null
- `isEmpty(value)` - Check if empty
- `isNotEmpty(value)` - Check if not empty
- `nullVal(value, ifNull)` - Return value or default if null
- `nvl(value, nullValue, nonNullValue)` - Null-safe value selection

### Collection Functions
- `sizeOf(collection)` - Get size
- `contains(collection, value)` - Check membership
- `collectionToString(...)` - Convert to string
- `mapKeys(map)` - Get keys
- `mapValues(map)` - Get values
- `mapToString(...)` - Convert map to string
- `groupBy(collection, keyExpr)` - Group elements
- `sortBy(collection, keyExpr)` - Sort elements

### Date Functions
- `now()` - Current date/time
- `today()` - Current date
- `dateToStr(date, format)` - Format date
- `parseDate(dateStr, format)` - Parse date
- `toMillis(date)` - Convert to milliseconds
- `addDays(date, days)` - Add days
- `addHours(date, hours)` - Add hours
- `addMinutes(date, minutes)` - Add minutes
- `addSeconds(date, seconds)` - Add seconds
- `addYears(date, years)` - Add years

---

## Additional Resources

- **[JSON Transformation Guide](json-transformation-guide.md)** - Detailed JSON template syntax
- **[XML Transformation Guide](xml-transformation-guide.md)** - Detailed XML template syntax
- **[Transform Free Marker Methods](transform-fmarker-methods.md)** - Complete method reference
- **[Developer Guide](developer-guide.md)** - Architecture and extension points
- **[Library Summary](../LIBRARY_SUMMARY.md)** - Quick reference guide

---

*This guide is designed to help AI agents understand and generate transformation templates for the yukthi-transform library. For human developers, refer to the individual guide documents for more detailed explanations and examples.*
