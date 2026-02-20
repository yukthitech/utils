# XML Transformation Guide

This guide explains how to use XML templates for data transformation with the yukthi-transform library.

## Table of Contents
1. [Namespace Declaration](#namespace-declaration)
2. [Replacement Expressions](#replacement-expressions)
3. [FreeMarker Expressions](#freemarker-expressions)
4. [Conditions](#conditions)
5. [Loops](#loops)
6. [Variables](#variables)
7. [Transformation](#transformation)
8. [Resource Loading](#resource-loading)
9. [Including Other Resources/Files](#including-other-resourcesfiles)
10. [Switch Statements](#switch-statements)
11. [Dynamic Element Names](#dynamic-element-names)
12. [Attributes vs Elements](#attributes-vs-elements)

---

## Namespace Declaration

XML templates must declare the transform namespace using `xmlns:t="/transform"`:

```xml
<output xmlns:t="/transform">
  <!-- template content -->
</output>
```

All transform directives use the `t:` prefix.

---

## Replacement Expressions

Replacement expressions allow you to replace entire values with the result of an expression.

### Syntax

- `@fmarker:` - FreeMarker expression evaluation
- `@xpath:` - XPath expression (returns first match)
- `@xpathMulti:` - XPath expression (returns all matches)

### Examples

**Using t:value Attribute:**
```xml
<output xmlns:t="/transform">
  <bookName t:value="@xpathMulti: /libraries/Justbooks/books//title"/>
  <copyCount t:value="@xpath: /libraries/Justbooks/books[1]/copyCount"/>
</output>
```

**In Element Content:**
```xml
<output xmlns:t="/transform">
  <price>@fmarker: book.cost</price>
</output>
```

---

## FreeMarker Expressions

String values in attributes and element content are processed as FreeMarker templates.

### Examples

**In Attributes:**
```xml
<output xmlns:t="/transform">
  <book title="${book.title}" status="${book.status}"/>
</output>
```

**In Element Content:**
```xml
<output xmlns:t="/transform">
  <name>${book.title}</name>
  <description>Type: ${book.type}, author: ${book.author}</description>
</output>
```

---

## Conditions

Conditions allow you to include or exclude elements conditionally.

### Element Conditions

Use `t:condition` attribute:

```xml
<output xmlns:t="/transform">
  <Justbooks t:condition="toBoolean(libraries.Justbooks.open)">
    <desc>Library is open</desc>
  </Justbooks>
  <Imagine t:condition="toBoolean(libraries.Imagine.open)">
    <desc>Library is open</desc>
  </Imagine>
</output>
```

### Simple Value Conditions

Use `t:condition` with `t:value`:

```xml
<output xmlns:t="/transform">
  <mapWithString t:condition="toBoolean(libraries.Justbooks.open)" 
                 t:value="Enabled" 
                 extraParam="1"/>
  <mapWithNonString t:condition="toBoolean(libraries.Justbooks.open)" 
                    t:value="100" 
                    extraParam="1"/>
</output>
```

### False Value

Use `t:falseValue` for alternative value when condition is false:

```xml
<output xmlns:t="/transform">
  <enabled t:condition="toBoolean(book.available)" 
           t:value="Enabled" 
           t:falseValue="Disabled"/>
</output>
```

**Note:** When `t:value` is defined, other attributes (except transform attributes) are ignored.

---

## Loops

Loops allow you to repeat elements dynamically.

### Basic Loop

Use `t:for-each` and `t:loop-var`:

```xml
<output xmlns:t="/transform">
  <Justbooks t:for-each="libraries.Justbooks.books" 
             t:loop-var="book" 
             name="${book.title}">
    <desc>Type: ${book.type}, author: ${book.author}</desc>
  </Justbooks>
</output>
```

### Loop with Condition

Filter iterations using `t:for-each-condition`:

```xml
<output xmlns:t="/transform">
  <Imagine t:for-each="libraries.Imagine.books" 
           t:loop-var="book" 
           name="${book.title}"
           t:for-each-condition="toInt(book.copyCount) gt 0">
    <desc>Type: ${book.type}, author: ${book.author}</desc>
  </Imagine>
</output>
```

### Global Condition on Loop Elements

Loop elements can have a global `t:condition` attribute. If the condition evaluates to `false`, the entire element (including the loop) is removed from the output. This is useful when you want to conditionally include entire loop sections.

```xml
<output xmlns:t="/transform">
  <books>
    <book t:condition="toBoolean(libraries.Justbooks.open)"
          t:for-each="libraries.Justbooks.books" 
          t:loop-var="book">
      <title>${book.title}</title>
    </book>
    <book t:condition="toBoolean(libraries.Imagine.open)"
          t:for-each="libraries.Imagine.books" 
          t:loop-var="book">
      <title>${book.title}</title>
    </book>
    <book t:condition="toBoolean(libraries.Other.open)"
          t:for-each="libraries.Other.books" 
          t:loop-var="book">
      <title>${book.title}</title>
    </book>
  </books>
</output>
```

In this example, if `libraries.Other.open` is `false`, the entire third loop element will be excluded from the output, and no books from the "Other" library will appear in the result.

**Note:** The global `t:condition` is evaluated before the loop executes. If the condition is `false`, the loop element is completely removed, regardless of the loop's content or `t:for-each-condition` settings.

### Multiple Elements in Loop

When looping, multiple child elements are repeated:

```xml
<output xmlns:t="/transform">
  <book t:for-each="books" t:loop-var="book">
    <title>${book.title}</title>
    <author>${book.author}</author>
    <type>${book.type}</type>
  </book>
</output>
```

---

## Variables

Variables allow you to store complex expressions for reuse.

### Syntax

Use `<t:set name="varName">` element:

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
  <map>
    <key1>val1</key1>
    <t:set name="libJb">
      <bookNames t:value="@xpathMulti: /libraries/Justbooks/books//title"/>
    </t:set>
    <key2>@fmarker: libraries.Imagine.books[1].title</key2>
  </map>
  <names t:value="@fmarker: libJb.bookNames"/>
</output>
```

**Note:** Variables are accessible as standard context attributes after they are set.

---

## Transformation

Use `t:transform` attribute to transform values. The current value is available as `thisValue`.

### Example

```xml
<output xmlns:t="/transform">
  <result t:transform="@fmarker: toJson(thisValue)">
    <someVal>1</someVal>
    <libNames t:value="@xpathMulti: /libraries/Justbooks//name"/>
  </result>
</output>
```

---

## Resource Loading

Load external content from resource files using `<t:resource>`.

### Basic Usage

```xml
<output xmlns:t="/transform">
  <result>
    <t:resource path="/res-file.txt"/>
  </result>
</output>
```

### With Parameters

```xml
<output xmlns:t="/transform">
  <result>
    <t:resource path="/res-file.txt">
      <t:params>
        <key1>val1</key1>
      </t:params>
    </t:resource>
  </result>
</output>
```

Parameters are accessible in resource content as `resParams.key1`.

### Disable Expression Processing

By default, expressions in loaded resources are processed. To disable:

```xml
<output xmlns:t="/transform">
  <result>
    <t:resource path="/res-file.txt" expressions="false">
      <t:params>
        <key1>val1</key1>
      </t:params>
    </t:resource>
  </result>
</output>
```

### With Transformation

```xml
<output xmlns:t="/transform">
  <type>Fiction</type>
  <metaInfo>
    <t:resource path="/fiction-meta.xml" transform="@fmarker: normalizeXml(thisValue)"/>
  </metaInfo>
</output>
```

---

## Including Other Resources/Files

Split complex templates into multiple files and include them.

### Include Resource

```xml
<output xmlns:t="/transform">
  <key1>@fmarker: data.ckey1</key1>
  <extra>
    <t:includeResource path="/include-res.xml"/>
  </extra>
  <key2>@fmarker: data.ckey2</key2>
</output>
```

### Include File

```xml
<output xmlns:t="/transform">
  <key1>@fmarker: data.ckey1</key1>
  <extra>
    <t:includeFile path="./src/test/resources/include-file.xml"/>
  </extra>
  <key2>@fmarker: data.ckey2</key2>
</output>
```

### Replace Entry

Use `<t:replace>` to merge included entries into the parent element:

```xml
<output xmlns:t="/transform">
  <key1>@fmarker: data.ckey1</key1>
  <t:replace>
    <t:includeResource path="/include-res.xml"/>
  </t:replace>
  <key2>@fmarker: data.ckey2</key2>
</output>
```

This merges attributes and child elements from the included template into the parent element.

### With Parameters

Pass parameters to included templates:

```xml
<output xmlns:t="/transform">
  <mainMap>
    <t:includeResource path="/include-recursive.xml">
      <t:params>
        <value>@fmarker: 3</value>
      </t:params>
    </t:includeResource>
  </mainMap>
  <ckey1>@fmarker: ckey1</ckey1>
</output>
```

Parameters are accessible in the included template using the `params` key.

### With Condition

Include conditionally:

```xml
<output xmlns:t="/transform">
  <key1>@fmarker: data.ckey1</key1>
  <t:replace t:condition="data.ckey1 == 'cval1'">
    <t:includeResource path="/include-res2.xml">
      <t:params>
        <paramKey1>paramValue</paramKey1>
      </t:params>
    </t:includeResource>
  </t:replace>
  <key2>@fmarker: data.ckey2</key2>
</output>
```

---

## Switch Statements

Switch statements allow you to evaluate multiple conditions and return the matching value.

### Basic Syntax

```xml
<output xmlns:t="/transform">
  <number>
    <t:switch>
      <t:case>
        <t:condition>toInt(data.a) lt 0</t:condition>
        <t:value>Negative Number</t:value>
      </t:case>
      <t:case>
        <t:condition>toInt(data.a) gt 100</t:condition>
        <t:value>Invalid Number</t:value>
      </t:case>
      <t:case>
        <t:value>@fmarker: toInt(data.a) * 100</t:value>
      </t:case>
    </t:switch>
  </number>
</output>
```

### Compact Syntax

You can also use attributes for conditions and values:

```xml
<output xmlns:t="/transform">
  <number>
    <t:switch>
      <t:case t:condition="toInt(data.a) lt 0" t:value="Negative Number"/>
      <t:case t:condition="toInt(data.a) gt 100" t:value="Invalid Number"/>
      <t:case t:value="@fmarker: toInt(data.a) * 100"/>
    </t:switch>
  </number>
</output>
```

### Rules

1. Cases are evaluated in order
2. The first matching case is used
3. The last case without `t:condition` is the default case
4. Default case must be the last case
5. Every case must have `t:value` specified

### Examples

**String Values:**
```xml
<output xmlns:t="/transform">
  <message>
    <t:switch>
      <t:case>
        <t:condition>data.status == 'active'</t:condition>
        <t:value>User is active</t:value>
      </t:case>
      <t:case>
        <t:condition>data.status == 'inactive'</t:condition>
        <t:value>User is inactive</t:value>
      </t:case>
      <t:case>
        <t:value>Unknown status: ${data.status}</t:value>
      </t:case>
    </t:switch>
  </message>
</output>
```

**Complex Expressions:**
```xml
<output xmlns:t="/transform">
  <grade>
    <t:switch>
      <t:case t:condition="toInt(data.score) gte 90" t:value="A"/>
      <t:case t:condition="toInt(data.score) gte 80" t:value="B"/>
      <t:case t:condition="toInt(data.score) gte 70" t:value="C"/>
      <t:case t:value="F"/>
    </t:switch>
  </grade>
</output>
```

**Nested Objects:**
```xml
<output xmlns:t="/transform">
  <category>
    <t:switch>
      <t:case>
        <t:condition>toInt(data.user.age) lt 18</t:condition>
        <t:value>Minor</t:value>
      </t:case>
      <t:case>
        <t:condition>toInt(data.user.age) gte 18 && toBoolean(data.user.premium)</t:condition>
        <t:value>Premium Adult</t:value>
      </t:case>
      <t:case>
        <t:condition>toInt(data.user.age) gte 18</t:condition>
        <t:value>Adult</t:value>
      </t:case>
      <t:case>
        <t:value>Unknown</t:value>
      </t:case>
    </t:switch>
  </category>
</output>
```

**Element Values:**
```xml
<output xmlns:t="/transform">
  <permissions>
    <t:switch>
      <t:case t:condition="data.type == 'admin'">
        <t:value>
          <read>true</read>
          <write>true</write>
          <delete>true</delete>
        </t:value>
      </t:case>
      <t:case t:condition="data.type == 'user'">
        <t:value>
          <read>true</read>
          <write>true</write>
          <delete>false</delete>
        </t:value>
      </t:case>
      <t:case>
        <t:value>
          <read>true</read>
          <write>false</write>
          <delete>false</delete>
        </t:value>
      </t:case>
    </t:switch>
  </permissions>
</output>
```

**Null Values:**
If a case returns `null`, the element is excluded from the result:

```xml
<output xmlns:t="/transform">
  <result>
    <t:switch>
      <t:case t:condition="toBoolean(data.flag)" t:value="@fmarker: nullValue()"/>
      <t:case t:value="default"/>
    </t:switch>
  </result>
</output>
```

---

## Dynamic Element Names

Use `t:name` attribute to set element names dynamically:

```xml
<output xmlns:t="/transform">
  <Justbooks t:name="Justbooks-${libraries.Justbooks.open}" status="${libraries.Justbooks.open}"/>
  <Imagine t:name="@fmarker: mapValues(libraries)[1].name">
    <status>${libraries.Imagine.open}</status>
  </Imagine>
</output>
```

**Result:**
```xml
<output>
  <Justbooks-true status="true"/>
  <Imagine>
    <status>false</status>
  </Imagine>
</output>
```

### In Loops

```xml
<output xmlns:t="/transform">
  <Justbooks>
    <book t:name="${book.title}" t:for-each="libraries.Justbooks.books" t:loop-var="book">
      <copyCount>@fmarker: book.copyCount</copyCount>
    </book>
  </Justbooks>
</output>
```

---

## Attributes vs Elements

XML templates distinguish between attributes, child elements, and text content.

### Attributes

Regular XML attributes are preserved as attributes:

```xml
<output xmlns:t="/transform">
  <book title="${book.title}" author="${book.author}"/>
</output>
```

### Child Elements

Child elements become child nodes:

```xml
<output xmlns:t="/transform">
  <book>
    <title>${book.title}</title>
    <author>${book.author}</author>
  </book>
</output>
```

### Text Content

Text content becomes element text:

```xml
<output xmlns:t="/transform">
  <price>@fmarker: book.cost</price>
</output>
```

### Multiple Elements with Same Name

When multiple elements have the same name, they are grouped:

```xml
<output xmlns:t="/transform">
  <allDocCounts t:value="@fmarker: sizeOf(libraries.Justbooks.books)"/>
  <allDocCounts t:value="@fmarker: sizeOf(libraries.Imagine.books)"/>
</output>
```

**Result:**
```xml
<output>
  <allDocCounts>2</allDocCounts>
  <allDocCounts>3</allDocCounts>
</output>
```

---

## Complete Example

Here's a comprehensive example combining multiple features:

```xml
<output xmlns:t="/transform">
  <bookName t:value="@xpathMulti: /libraries/Justbooks/books//title"/>
  <copyCount t:value="@xpath: /libraries/Justbooks/books[1]/copyCount"/>
  
  <allDocCounts t:value="@fmarker: sizeOf(libraries.Justbooks.books)"/>
  <allDocCounts t:value="@fmarker: sizeOf(libraries.Imagine.books)"/>
  
  <Justbooks t:for-each="libraries.Justbooks.books" 
             t:loop-var="book" 
             name="${book.title}">
    <desc>Type: ${book.type}, author: ${book.author}</desc>
  </Justbooks>
  
  <Imagine t:for-each="libraries.Imagine.books" 
           t:loop-var="book" 
           name="${book.title}"
           t:for-each-condition="toInt(book.copyCount) gt 0">
    <desc>Type: ${book.type}, author: ${book.author}</desc>
  </Imagine>
  
  <status>
    <t:switch>
      <t:case t:condition="library.enabled" t:value="Active"/>
      <t:case t:value="Inactive"/>
    </t:switch>
  </status>
</output>
```

---

## Best Practices

1. **Always declare namespace**: Use `xmlns:t="/transform"` on the root element
2. **Use t:value for simple replacements**: When replacing entire element content
3. **Use attributes for simple values**: When the value is a simple string/number
4. **Use elements for complex structures**: When the value is an object or array
5. **Use t:name for dynamic element names**: When element names depend on data
6. **Use includes** to split large templates into manageable pieces
7. **Use conditions** to handle optional data gracefully
8. **Use loops** for dynamic collections
9. **Use switch statements** for multiple conditional branches
10. **Use resource loading** for large static content or multi-line text

---

## Important Notes

1. **Type Conversion**: XML attributes are strings. Use type conversion functions:
   - `toBoolean()` - Convert to boolean
   - `toInt()` - Convert to integer
   - `toDouble()` - Convert to double

2. **Expression Operators**: Use XML-safe operators:
   - `lt` instead of `<`
   - `gt` instead of `>`
   - `gte` instead of `>=`
   - `lte` instead of `<=`
   - `&&` for AND
   - `||` for OR

3. **Namespace Handling**: Transform namespace attributes (`t:*`) are removed from output

4. **Element Renaming**: When `t:name` differs from actual element name, the element is renamed

5. **Empty Elements**: Empty elements are preserved in output (e.g., `<emptyList/>`)
