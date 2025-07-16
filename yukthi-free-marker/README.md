# Yukthi FreeMarker Wrapper

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

## Default Methods and Directives

This library provides a set of default methods and directives for common tasks.

### Methods

#### Collection Methods

##### `groupBy`
* **Description:** Groups elements of specified collection based on specified keyExpression
* **Return Type:** `List<Group>`
* **Return Description:** List of groups. Each group has key (value of key based on which current group is created) and elements having same key.
* **Parameters:**
  * `collection` (`Collection<Object>`): Collection of objects which needs grouping
  * `keyExpression` (`String`): Freemarker key expression which will be executed on each of collection element. And obtained key will be used for grouping.

##### `sortBy`
* **Description:** Sorted elements of specified collection based on specified keyExpression. Duplicate elements (with same key) will be kept together (though internal order is not guaranteed).
* **Return Type:** `List<Object>`
* **Return Description:** List of ordered elements based on specified key expression.
* **Parameters:**
  * `collection` (`Collection<Object>`): Collection of objects which needs sorting
  * `keyExpression` (`String`): Freemarker key expression which will be executed on each of collection element. And obtained key will be used for sorting.

##### `mapValues`
* **Description:** Extracts and returns the values collection as list of specified map.
* **Return Type:** `Collection<Object>`
* **Return Description:** the values collection of specified map.
* **Parameters:**
  * `map` (`Map<Object, Object>`): Map whose values has to be extracted

##### `mapKeys`
* **Description:** Extracts and returns the keys collection as list of specified map.
* **Return Type:** `Collection<Object>`
* **Return Description:** the values collection of specified map.
* **Parameters:**
  * `map` (`Map<Object, Object>`): Map whose keys has to be extracted

##### `collectionToString`
* **Description:** Converts collection of objects into string.
* **Return Type:** `String`
* **Return Description:** Converted string
* **Parameters:**
  * `lst` (`Collection<Object>`): Collection to be converted
  * `prefix` (`String`): Prefix to be used at start of coverted string.
  * `delimiter` (`String`): Delimiter to be used between the collection elements.
  * `suffix` (`String`): Suffix to be used at end of converted string.
  * `emptyString` (`String`): String to be used when input list is null or empty.
* **Examples:**
  * `collectionToString(lst, '[', ' | ', ']', '')` => `[a | b | c]`
  * `collectionToString(null, '[', ' | ', ']', '<empty>')` => `<empty>`

##### `mapToString`
* **Description:** Converts map of objects into string.
* **Return Type:** `String`
* **Return Description:** Converted string
* **Parameters:**
  * `map` (`Map<Object, Object>`): Prefix to be used at start of coverted string
  * `template` (`String`): Template representing how key and value should be converted into string (the string can have #key and #value which will act as place holders).
  * `prefix` (`String`): Prefix to be used at start of coverted string.
  * `delimiter` (`String`): Delimiter to be used between elements.
  * `suffix` (`String`): Suffix to be used at end of string.
  * `emptyString` (`String`): String that will be returned if input map is null or empty.
* **Examples:**
  * `mapToString(map, '#key=#value', '[', ' | ', ']', '')` => `[a=1 | b=2 | c=3]`
  * `mapToString(null, '#key=#value', '[', ' | ', ']', '<empty>')` => `<empty>`

#### Common Methods

##### `isEmpty`
* **Description:** Used to check if specified value is empty. For collection, map and string, along with null this will check for empty value.
* **Return Type:** `boolean`
* **Return Description:** True if value is empty.
* **Parameters:**
  * `value` (`Object`): Value to be checked for empty

##### `isNotEmpty`
* **Description:** Used to check if specified value is not empty. For collection, map and string, along with non-null this will check for non-empty value.
* **Return Type:** `boolean`
* **Return Description:** True if value is empty.
* **Parameters:**
  * `value` (`Object`): Value to be checked for empty

##### `nvl`
* **Description:** Used to check if specified value is null and return approp value when null and when non-null.
* **Return Type:** `Object`
* **Return Description:** Specified null-condition-value or non-null-condition-value.
* **Parameters:**
  * `value` (`Object`): Value to be checked for empty
  * `nullValue` (`Object`): Value to be returned when value is null
  * `nonNullValue` (`Object`): Value to be returned when value is non-null

##### `ifTrue`
* **Description:** Used to check if specified value is true and return approp value Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false.
* **Return Type:** `Object`
* **Return Description:** Specified true-condition-value or false-condition-value.
* **Parameters:**
  * `value` (`Object`): Value to be checked for true.
  * `trueValue` (`Object`): Value to be returned when value is true.
  * `falseValue` (`Object`): Value to be returned when value is false or null.

##### `ifFalse`
* **Description:** Used to check if specified value is false and return approp value Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false. If null, the condition will be considered as false (hence returing falseValue)
* **Return Type:** `Object`
* **Return Description:** Specified true-condition-value or false-condition-value.
* **Parameters:**
  * `value` (`Object`): Value to be checked for false. Can be boolean true or string 'true'
  * `falseValue` (`Object`): Value to be returned when value is false or null.
  * `trueValue` (`Object`): Value to be returned when value is true.

##### `toText`
* **Description:** Used to convert specified object into string. toString() will be invoked on input object to convert
* **Return Type:** `String`
* **Return Description:** Converted string. If null, 'null' will be returned.
* **Parameters:**
  * `value` (`Object`): Value to be converted into string.

##### `ifNull`
* **Description:** If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.
* **Return Type:** `Object`
* **Return Description:** ifNull or ifNotNull based on nullCheck.
* **Parameters:**
  * `nullCheck` (`Object`): object to be checked for null
  * `ifNull` (`Object`): object to be returned if null.
  * `ifNotNull` (`Object`): object to be returned if not null

##### `ifNotNull`
* **Description:** If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.
* **Return Type:** `Object`
* **Return Description:** ifNull or ifNotNull based on nullCheck.
* **Parameters:**
  * `nullCheck` (`Object`): object to be checked for null
  * `ifNotNull` (`Object`): object to be returned if not null.
  * `ifNull` (`Object`): object to be returned if null.

##### `nullVal`
* **Description:** If 'nullCheck' is null, 'ifNull' will be returned otherwise 'nullCheck' will be returned.
* **Return Type:** `Object`
* **Return Description:** ifNull or nullCheck based on nullCheck is null or not.
* **Parameters:**
  * `nullCheck` (`Object`): object to be checked for null
  * `ifNull` (`Object`): object to be returned if null

##### `sizeOf`
* **Description:** Used to fetch size of specified value. If string length of string is returned, if collection size of collection is returned, if null zero will be returned. Otherwise 1 will be returned.
* **Return Type:** `int`
* **Return Description:** Size of specified object.
* **Parameters:**
  * `value` (`Object`): Value whose size to be determined

##### `replace`
* **Description:** Replaces specified substring with replacement in main string.
* **Return Type:** `String`
* **Parameters:**
  * `mainString` (`String`): String in which replacement should happen
  * `substring` (`String`): Substring to be replaced
  * `replacement` (`String`): Replacement string

##### `initcap`
* **Description:** Makes first letter of every word into capital letter.
* **Return Type:** `String`
* **Parameters:**
  * `str` (`String`): String to convert

##### `strContains`
* **Description:** Checks if specified substring can be found in main string
* **Return Type:** `boolean`
* **Return Description:** true, if substring can be found.
* **Parameters:**
  * `mainStr` (`String`): Main string in which search has to be performed
  * `substr` (`String`): Substring to be searched
  * `ignoreCase` (`boolean`): Flag to indicate if case has to be ignored during search

#### Date Methods

##### `toMillis`
* **Description:** Converts specified date into millis.
* **Return Type:** `Long`
* **Return Description:** Millis value
* **Parameters:**
  * `date` (`Date`): Date to be converted

##### `dateToStr`
* **Description:** Converts specified date into string in specified format.
* **Return Type:** `String`
* **Return Description:** Fromated date string.
* **Parameters:**
  * `date` (`Date`): Date to be converted
  * `format` (`String`): Date format to which date should be converted
* **Examples:**
  * `dateToStr(date, 'MM/dd/yyy')` => `20/20/2018`

##### `addDays`
* **Description:** Adds specified number of days to specified date
* **Return Type:** `Date`
* **Return Description:** Resultant date after addition of specified days
* **Parameters:**
  * `date` (`Date`): Date to which days should be added
  * `days` (`int`): Days to be added.

##### `addYears`
* **Description:** Adds specified number of days to specified date
* **Return Type:** `Date`
* **Return Description:** Resultant date after addition of specified years
* **Parameters:**
  * `date` (`Date`): Date to which days should be added
  * `years` (`int`): Years to be added.

##### `addHours`
* **Description:** Adds specified number of hours to specified date
* **Return Type:** `Date`
* **Return Description:** Resultant date after addition of specified hours
* **Parameters:**
  * `date` (`Date`): Date to which hours should be added
  * `hours` (`int`): Hours to be added.

##### `addMinutes`
* **Description:** Adds specified number of minutes to specified date
* **Return Type:** `Date`
* **Return Description:** Resultant date after addition of specified minutes
* **Parameters:**
  * `date` (`Date`): Date to which minutes should be added
  * `minutes` (`int`): Minutes to be added.

##### `addSeconds`
* **Description:** Adds specified number of seconds to specified date
* **Return Type:** `Date`
* **Return Description:** Resultant date after addition of specified seconds
* **Parameters:**
  * `date` (`Date`): Date to which seconds should be added
  * `seconds` (`int`): Seconds to be added.

##### `today`
* **Description:** Returns the current date object
* **Return Type:** `Date`
* **Return Description:** Current date

##### `now`
* **Description:** Returns the current date object
* **Return Type:** `Date`
* **Return Description:** Current date and time

### Directives

#### Common Directives

##### `<@trim>`
* **Description:** Trims the content enclosed within this directive.
* **Parameters:**
  * `body` (`String`): Enclosing body content
* **Examples:**
  * **Usage**
    ```
    <@trim>   some content  </@trim>
    ```
  * **Output**
    ```
    some content
    ```

##### `<@indent>`
* **Description:** Helps in indenting the enclosed content. Accepts optional prefix attribute, defaults to empty string. Every line will be trimmed and converted into single line and prefix will be added at the start. And from the output content '	' and '
' will be replaced with tab and new-line characters respectively.
* **Parameters:**
  * `body` (`String`): Enclosing body content
  * `prefix` (`String`): If specified, this value will be added in start of every line
  * `retainLineBreaks` (`boolean`): [boolean] if true, lines will be maintained as separate lines.
* **Example 1**
  * **Usage**
    ```
    <@indent>   first line

    second line  </@indent>
    ```
  * **Output**
    ```
    first linesecond line
    ```
* **Example 2**
  * **Usage**
    ```
    <@indent prefix='--'>   first line

    second line  </@indent>
    ```
  * **Output**
    ```
    --first line--second line
    ```
* **Example 3**
  * **Usage**
    ```
    <@indent prefix='--' retainLineBreaks=true>   first line

    second line  </@indent>
    ```
 * **Output**
   ```
   --first line
   --second line
   ```
