# Yukthi-Transform Library

**yukthi-transform** is a Java transformation/templating engine that transforms object data from multiple sources (JSON, XML, POJOs) into different forms based on templates. The library supports both JSON and XML transformation templates.

![Alt text](doc/images/jel.png?raw=true "Transformation Engine")

## Features

- **Dual Format Support**: Transform data using JSON or XML templates
- **Multiple Data Sources**: Works with Maps, POJOs, and XML context data
- **Powerful Expressions**: FreeMarker expressions, XPath queries
- **Advanced Features**: Conditions, loops, variables, resource loading, includes, switch statements
- **Type Agnostic**: Transform to any JSON-supported type (String, boolean, int, date, map, list)

## Documentation

- **[JSON Transformation Guide](doc/json-transformation-guide.md)** - Complete guide for JSON template syntax and usage
- **[XML Transformation Guide](doc/xml-transformation-guide.md)** - Complete guide for XML template syntax and usage
- **[Transform Free Marker Methods](doc/transform-fmarker-methods.md)** - List of FreeMarker methods available for transformation templates
- **[Developer Guide](doc/developer-guide.md)** - Architecture, class organization, and extension points

## Maven Dependency

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-transform</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Required Dependencies

The library requires the following dependencies (automatically included):

- `yukthi-free-marker` - FreeMarker expression engine
- `yukthi-xml-mapper` - XML parsing and mapping
- `commons-jxpath` - XPath support
- `jackson-databind` - JSON processing

## Quick Start

### JSON Transformation

```java
import com.yukthitech.transform.TransformEngine;
import com.yukthitech.transform.template.JsonTemplateFactory;
import com.yukthitech.transform.template.TransformTemplate;
import java.util.HashMap;
import java.util.Map;

// Create template factory
JsonTemplateFactory factory = new JsonTemplateFactory();

// Parse JSON template
String templateJson = "{\n" +
    "  \"book\": {\n" +
    "    \"@condition\": \"book.availableCount >= 1\",\n" +
    "    \"title\": \"${book.title}\",\n" +
    "    \"cost\": \"@fmarker: book.cost\"\n" +
    "  }\n" +
    "}";

TransformTemplate template = factory.parseTemplate(templateJson);

// Create transform engine
TransformEngine engine = new TransformEngine();

// Prepare context data
Map<String, Object> context = new HashMap<>();
Map<String, Object> book = new HashMap<>();
book.put("title", "The Great Gatsby");
book.put("cost", 15.99);
book.put("availableCount", 5);
context.put("book", book);

// Process transformation
String result = engine.processAsString(template, context);
System.out.println(result);
// Output: {"book":{"title":"The Great Gatsby","cost":15.99}}
```

### XML Transformation

```java
import com.yukthitech.transform.TransformEngine;
import com.yukthitech.transform.template.XmlTemplateFactory;
import com.yukthitech.transform.template.TransformTemplate;
import java.util.HashMap;
import java.util.Map;

// Create template factory
XmlTemplateFactory factory = new XmlTemplateFactory();

// Parse XML template
String templateXml = "<output xmlns:t=\"/transform\">\n" +
    "  <book t:condition=\"toBoolean(book.availableCount >= 1)\">\n" +
    "    <title>${book.title}</title>\n" +
    "    <cost>@fmarker: book.cost</cost>\n" +
    "  </book>\n" +
    "</output>";

TransformTemplate template = factory.parseTemplate(templateXml);

// Create transform engine
TransformEngine engine = new TransformEngine();

// Prepare context data (same as JSON example)
Map<String, Object> context = new HashMap<>();
Map<String, Object> book = new HashMap<>();
book.put("title", "The Great Gatsby");
book.put("cost", 15.99);
book.put("availableCount", 5);
context.put("book", book);

// Process transformation
String result = engine.processAsString(template, context);
System.out.println(result);
```

## Usage Examples

### Basic Usage with Map Context

```java
// Create template factory
JsonTemplateFactory factory = new JsonTemplateFactory();

// Parse template
String templateJson = "{ \"name\": \"${user.name}\", \"age\": \"@fmarker: user.age\" }";
TransformTemplate template = factory.parseTemplate(templateJson);

// Create transform engine
TransformEngine engine = new TransformEngine();

// Prepare context
Map<String, Object> context = new HashMap<>();
Map<String, Object> user = new HashMap<>();
user.put("name", "John Doe");
user.put("age", 30);
context.put("user", user);

// Process transformation
Object result = engine.process(template, context);
// or get as string
String resultString = engine.processAsString(template, context);
```

### Using POJO Context

```java
// Create template factory
JsonTemplateFactory factory = new JsonTemplateFactory();

// Parse template
String templateJson = "{ \"name\": \"${user.name}\", \"email\": \"${user.email}\" }";
TransformTemplate template = factory.parseTemplate(templateJson);

// Create transform engine
TransformEngine engine = new TransformEngine();

// Use POJO as context
User user = new User("John Doe", "john@example.com");

// Process transformation (POJO is automatically wrapped in PojoExprContext)
String result = engine.processAsString(template, user);
```

### Custom FreeMarker Engine

```java
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

// Create custom FreeMarker engine
FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
freeMarkerEngine.loadClass(MyCustomMethods.class); // Load custom methods

// Create transform engine with custom FreeMarker engine
TransformEngine engine = new TransformEngine(freeMarkerEngine);

// Use engine as normal
```

### Custom Content Loader

```java
import com.yukthitech.transform.IContentLoader;
import java.io.IOException;

// Create custom content loader
IContentLoader customLoader = new IContentLoader() {
    @Override
    public String loadResource(String resource) throws IOException {
        // Custom resource loading logic
        return loadFromCustomLocation(resource);
    }
    
    @Override
    public String loadFile(String file) throws IOException {
        // Custom file loading logic
        return loadFromCustomLocation(file);
    }
};

// Create template factory with custom loader
JsonTemplateFactory factory = new JsonTemplateFactory(customLoader);
```

### Template Caching

Templates can be cached and reused:

```java
JsonTemplateFactory factory = new JsonTemplateFactory();

// Parse template once
String templateJson = "{ ... }";
TransformTemplate template = factory.parseTemplate(templateJson);

// Reuse template for multiple transformations
TransformEngine engine = new TransformEngine();

for (Map<String, Object> context : contexts) {
    String result = engine.processAsString(template, context);
    // Process result...
}

// Clear cache if needed
factory.reset();
```

## Template Syntax Overview

### JSON Templates

JSON templates use special keys starting with `@` for directives:

- `@condition` - Conditional inclusion
- `@value` / `@falseValue` - Value replacement
- `@for-each(varName):` - Loop definition
- `@for-each-condition` - Loop filter
- `@set(varName):` - Variable definition
- `@switch` / `@case` - Switch statements
- `@resource` - Load external resource
- `@includeResource` / `@includeFile` - Include other templates
- `@replace(name)` - Merge included entries
- `@transform` - Transform value
- `@fmarker:` - FreeMarker expression
- `@xpath:` / `@xpathMulti:` - XPath expressions

**See [JSON Transformation Guide](doc/json-transformation-guide.md) for complete syntax documentation.**

### XML Templates

XML templates use the `/transform` namespace (prefix `t:`) for directives:

- `t:condition` - Conditional inclusion
- `t:value` / `t:falseValue` - Value replacement
- `t:for-each` / `t:loop-var` - Loop definition
- `t:for-each-condition` - Loop filter
- `<t:set>` - Variable definition
- `<t:switch>` / `<t:case>` - Switch statements
- `<t:resource>` - Load external resource
- `<t:includeResource>` / `<t:includeFile>` - Include other templates
- `<t:replace>` - Merge included entries
- `t:transform` - Transform value
- `t:name` - Dynamic element name
- `@fmarker:`, `@xpath:`, `@xpathMulti:` - Expressions in attributes/content

**See [XML Transformation Guide](doc/xml-transformation-guide.md) for complete syntax documentation.**

## Processing Logic

The transformation engine processes objects in a specific order. Understanding this order is crucial for writing effective templates.

### Object Processing Overview

An object in JSON/XML (which can be map, list, or primitive values like string, boolean, int) will be processed by the following steps:
- In case of list, processed as list (see List Processing below)
- Map is processed as map object (see Map Processing below)
- All primitives are processed as expressions/strings

### Map/Element Processing

A map (in JSON) / element (in XML) object is evaluated in the following order:

1. **Condition Evaluation**: If `@condition`/`t:condition` is specified, it is evaluated first.
   - If condition evaluates to false:
     - If `@falseValue`/`t:false-value` is present, that value is returned
     - Otherwise, null is returned
   - If condition is not present or evaluates to true, the next steps are executed

2. **Switch Statement**: If `@switch`/`t:switch` is present, the current object will be evaluated as a switch statement. Rest of the map-entries/nodes, if any, will be ignored.

3. **Value Expression**: If `@value`/`t:value` is specified, this expression value will be the effective value of this map. Rest of the entries are ignored.

4. **Resource Loading**: If `@resource`/`t:resource` or `@includeResource`/`t:include-resource` is specified, the current map will be processed as a resource. Rest of the entries are ignored.

5. **Field Processing**: Each field/attribute of the map/element is processed in the following order (in case of XML, attributes are evaluated first):
   - If sub-element/map has `@for-each`/`t:for-each` loop, the element (along with its dynamic key) will be processed and repeated as per the loop parameters.
     - In maps, `@for-each` loops can exist only for submaps. Main map should not have loop element directly (as multiple roots cannot be created)
     - In a loop element, `@condition`/`t:condition` will be evaluated first. If it evaluates to false, the for-loop entry will be completely excluded
     - For each iteration, if `@for-each-condition`/`t:for-each-condition` is specified, it will be evaluated. If false, that iteration will be skipped
     - Rest of the entries will be processed as standard map entries
   - If the current entry key represents a set-key (`@set(varName)`/`t:set`), then the value will be processed as an object and a new context variable will be set
   - If the key/name represents a replace-entry (`@replace`/`t:replace`), then the result value entries will be added to parent map (instead of being a submap)
   - In case of XML, if the element has `t:name` attribute, the current entry key will be replaced with the dynamic value represented by `t:name`
   - Otherwise, the value will be processed as a standard object and added to the result map

### List Processing

A list (in JSON) is evaluated in the following order:

1. **Condition Evaluation**: If condition is specified on the list, it is evaluated first. If it evaluates to false, null is returned

2. **Element Processing**: For each element in the list, the following steps are executed:
   - If the array element is a map/object and has a `@for-each` loop, the loop is evaluated (just like map loop) and resultant elements will be added to the result list
   - If the loop results in zero elements, the entire list will be excluded (returns null)
   - Other elements are processed as standard objects
   - If any processed element results in null, it is excluded from the result list

### For-Each Loop Processing

When a for-each loop is encountered:

1. The condition on the loop element (if present) is evaluated first. If false, an empty list is returned

2. The list expression is evaluated to get the collection to iterate over

3. For each item in the collection:
   - The loop variable is set in the context
   - If `@for-each-condition` is specified, it is evaluated. If false, the iteration is skipped
   - The template object is processed with the current iteration value
   - If a name expression is present (for map entries), the result is added as a NameValueEntry
   - Otherwise, the processed object is added directly to the result list

4. If the loop results in zero elements, null is returned (so the parent key can be removed)

## Context Types

The library supports multiple context types:

1. **Map Context** - `Map<String, Object>`
   ```java
   Map<String, Object> context = new HashMap<>();
   context.put("key", value);
   ```

2. **POJO Context** - Any Java object
   ```java
   MyPojo pojo = new MyPojo();
   // POJO properties are accessible via property names
   ```

3. **Custom Context** - Implement `ITransformContext`
   ```java
   public class CustomContext implements ITransformContext {
       // Implement interface methods
   }
   ```

## Expression Types

### FreeMarker Expressions

Standard FreeMarker syntax in string values:
- `${variable}` - Variable interpolation
- `<#if condition>...</#if>` - Conditionals
- All FreeMarker features supported

**Available FreeMarker methods:** [Yukthi Free Marker](https://github.com/yukthitech/utils/tree/master/yukthi-free-marker)

### XPath Expressions

Access data using XPath:
- `@xpath: /path/to/element` - Returns first match
- `@xpathMulti: /path/to/elements` - Returns all matches

### Replacement Expressions

Replace entire values (not just strings):
- `@fmarker: expression` - FreeMarker expression result
- `@xpath: /path` - XPath first match
- `@xpathMulti: /path` - XPath all matches

## Advanced Features

### Loops

Iterate over collections:

**JSON:**
```json
{
  "books": [
    {
      "@for-each(book)": "books",
      "title": "${book.title}",
      "@for-each-condition": "book.available"
    }
  ]
}
```

**XML:**
```xml
<books t:for-each="books" t:loop-var="book" 
       t:for-each-condition="book.available">
  <title>${book.title}</title>
</books>
```

### Switch Statements

Multiple conditional branches:

**JSON:**
```json
{
  "grade": {
    "@switch": [
      { "@case": "score gte 90", "@value": "A" },
      { "@case": "score gte 80", "@value": "B" },
      { "@value": "F" }
    ]
  }
}
```

**XML:**
```xml
<grade>
  <t:switch>
    <t:case t:condition="score gte 90" t:value="A"/>
    <t:case t:condition="score gte 80" t:value="B"/>
    <t:case t:value="F"/>
  </t:switch>
</grade>
```

### Resource Loading

Load external content:

**JSON:**
```json
{
  "content": {
    "@resource": "/template.txt",
    "@resParams": { "key": "value" },
    "@expressions": true
  }
}
```

**XML:**
```xml
<content>
  <t:resource path="/template.txt" expressions="true">
    <t:params>
      <key>value</key>
    </t:params>
  </t:resource>
</content>
```

### Template Includes

Include other templates:

**JSON:**
```json
{
  "header": { "@includeResource": "/header.json" },
  "@replace(body)": { "@includeResource": "/body.json" }
}
```

**XML:**
```xml
<output>
  <header>
    <t:includeResource path="/header.xml"/>
  </header>
  <t:replace>
    <t:includeResource path="/body.xml"/>
  </t:replace>
</output>
```

## Error Handling

The library throws `TransformException` for transformation errors:

```java
try {
    String result = engine.processAsString(template, context);
} catch (TransformException e) {
    System.err.println("Transformation error at: " + e.getMessage());
    // Error includes path information for debugging
}
```

## Best Practices

1. **Cache Templates**: Parse templates once and reuse them
2. **Use Variables**: Store complex expressions in variables for reuse
3. **Split Large Templates**: Use includes to organize complex transformations
4. **Handle Nulls**: Use conditions to handle optional data gracefully
5. **Type Safety**: Use type conversion functions in XML (`toBoolean()`, `toInt()`, etc.)
6. **Error Messages**: Check path information in error messages for debugging

## Additional Resources

- **[JSON Transformation Guide](doc/json-transformation-guide.md)** - Detailed JSON template syntax
- **[XML Transformation Guide](doc/xml-transformation-guide.md)** - Detailed XML template syntax
- **[Transform Free Marker Methods](doc/transform-fmarker-methods.md)** - List of FreeMarker methods available for transformation templates
- **[Developer Guide](doc/developer-guide.md)** - Architecture and extension points
- **[Library Summary](LIBRARY_SUMMARY.md)** - Quick reference guide

## License

Licensed under the Apache License, Version 2.0
