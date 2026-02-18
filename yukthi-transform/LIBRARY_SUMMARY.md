# Yukthi-Transform Library Summary

## Overview
**yukthi-transform** is a Java transformation/templating engine that transforms object data from multiple sources (JSON, XML, POJOs) into different forms based on templates. Originally designed for JSON transformation, it now supports XML transformation as well.

## Core Architecture

### Main Components
1. **TransformEngine** (`com.yukthitech.transform.TransformEngine`)
   - Main entry point for transformations
   - Processes templates with context data
   - Methods: `process()`, `processAsString()`
   - Uses FreeMarker engine for expression evaluation

2. **Template Factories**
   - **JsonTemplateFactory** - Parses JSON templates
   - **XmlTemplateFactory** - Parses XML templates (uses `/transform` namespace)
   - Both implement `ITemplateFactory` interface

3. **Generators**
   - **JsonGenerator** - Generates JSON output
   - **XmlGenerator** - Generates XML output
   - Both implement `IGenerator` interface

4. **Context Types**
   - **MapExprContext** - For Map-based context
   - **PojoExprContext** - For POJO-based context
   - **ITransformContext** - Interface for custom contexts

## Key Features

### 1. Replacement Expressions
Full key/value replacement with result types (String, boolean, int, date, map, list):
- `@fmarker:` - FreeMarker expression evaluation
- `@xpath:` - XPath expression (returns first match)
- `@xpathMulti:` - XPath expression (returns all matches)

### 2. Freemarker Expressions
Standard FreeMarker syntax in string values:
- `${variable}` - Variable interpolation
- `<#if condition>...</#if>` - Conditionals
- Supports all FreeMarker features

### 3. Conditions
Conditional inclusion/exclusion of elements:

**JSON Templates:**
- `@condition` - Key in map for conditional inclusion
- `@value` - Value when condition is true
- `@falseValue` - Value when condition is false
- `@condition:` - First element in list for conditional list inclusion

**XML Templates:**
- `t:condition` - Attribute for conditional inclusion
- `t:value` - Attribute for value when condition is true
- `t:falseValue` - Attribute for value when condition is false

### 4. Loops
Dynamic repetition of elements:

**JSON Templates:**
- `@for-each(varName):` - Key in map/list for looping
- `@for-each-condition` - Condition to filter loop iterations

**XML Templates:**
- `t:for-each` - Attribute specifying collection to loop
- `t:loop-var` - Attribute specifying loop variable name
- `t:for-each-condition` - Attribute for filtering iterations

### 5. Variables
Set variables for reuse:
- **JSON:** `@set(varName):` - Key in map
- **XML:** `<t:set name="varName">` - Element node

### 6. Resource Loading
Load external content:
- `@resource` - Load resource from classpath
- `@expressions` - Enable/disable expression processing in resource (default: true)
- `@resParams` - Parameters accessible in resource as `resParams`
- `@transform` - Transform loaded content using expression (access as `thisValue`)

### 7. Includes
Include other templates:
- `@includeResource` - Include template from classpath
- `@includeFile` - Include template from file system
- `@replace` - Replace current entry with included template entries
- `@params` - Pass parameters to included template (accessible as `params`)

### 8. Transformations
Transform values using expressions:
- `@transform` - Transform current value (accessible as `thisValue`)
- Commonly used: `@fmarker: toJson(thisValue)` to convert object to JSON string

## Template Syntax Examples

### JSON Template Example
```json
{
  "book": {
    "@condition": "book.availableCount >= 1",
    "title": "${book.title}",
    "cost": "@fmarker: book.cost"
  },
  "books": [
    {
      "@for-each(book)": "books",
      "@for-each-condition": "book.copyCount > 0",
      "name": "${book.title}",
      "author": "${book.author}"
    }
  ],
  "bookNames": "@xpathMulti: /books//title"
}
```

### XML Template Example
```xml
<output xmlns:t="/transform">
  <book t:condition="toBoolean(book.availableCount >= 1)" 
        title="${book.title}" 
        cost="@fmarker: book.cost"/>
  
  <books t:for-each="books" t:loop-var="book" 
         t:for-each-condition="toInt(book.copyCount) gt 0">
    <name>${book.title}</name>
    <author>${book.author}</author>
  </books>
  
  <bookNames t:value="@xpathMulti: /books//title"/>
</output>
```

## Usage Pattern

### Basic Usage
```java
// Create template factory
JsonTemplateFactory factory = new JsonTemplateFactory();
// or
XmlTemplateFactory factory = new XmlTemplateFactory();

// Parse template
String templateJson = "{ ... }";
TransformTemplate template = factory.parseTemplate(templateJson);

// Create transform engine
TransformEngine engine = new TransformEngine();

// Prepare context (Map, POJO, or XML)
Map<String, Object> context = new HashMap<>();
context.put("book", bookData);

// Process transformation
Object result = engine.process(template, context);
// or
String resultString = engine.processAsString(template, context);
```

## Test Files Reference

### Test Data Files
- `json-trans-test-data.xml` - JSON transformation test cases
- `xml-trans-test-data.xml` - XML transformation test cases
- `pojo-json-trans-test-data.xml` - POJO-based JSON transformation tests
- `json-trans-negative-test-data.xml` - Negative test cases (error scenarios)

### Test Classes
- `TestJsonTransformation` - JSON transformation tests
- `TestXmlTransformation` - XML transformation tests

## Dependencies
- **yukthi-free-marker** - FreeMarker expression engine
- **yukthi-xml-mapper** - XML parsing and mapping
- **commons-jxpath** - XPath support
- **jackson-databind** - JSON processing

## Key Directives Summary

### JSON Directives
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

### XML Directives (t: namespace)
| Directive | Usage | Description |
|-----------|-------|-------------|
| `t:condition` | Attribute | Conditional inclusion |
| `t:value` | Attribute | Value when condition true |
| `t:falseValue` | Attribute | Value when condition false |
| `t:for-each` | Attribute | Loop collection |
| `t:loop-var` | Attribute | Loop variable name |
| `t:for-each-condition` | Attribute | Loop filter condition |
| `t:name` | Attribute | Dynamic element name |
| `t:set` | Element | Variable definition |
| `t:resource` | Element | Load resource |
| `t:includeResource` | Element | Include resource template |
| `t:includeFile` | Element | Include file template |
| `t:replace` | Element | Replace with included entries |
| `t:transform` | Attribute | Transform value |
| `t:params` | Element | Parameters for includes |
| `t:expressions` | Attribute | Enable/disable expressions in resource |

## Notes
- XML templates use namespace `xmlns:t="/transform"` for transform directives
- Context can be Map, POJO, or XML (converted to appropriate context type)
- Templates support recursive includes
- Resource loading supports custom content loaders via `IContentLoader`
- Expression evaluation uses FreeMarker with custom methods from `TransformFmarkerMethods`
- XPath expressions use JXPath for evaluation
