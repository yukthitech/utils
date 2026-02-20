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

[[defaultDirectiveContent]]

## Default Methods
Following additional freemarker methods by default are supported by this library:

[[defaultMethodContent]]
