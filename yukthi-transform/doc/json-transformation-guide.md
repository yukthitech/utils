# JSON Transformation Guide

This guide explains how to use JSON templates for data transformation with the yukthi-transform library.

## Table of Contents
1. [Replacement Expressions](#replacement-expressions)
2. [FreeMarker Expressions](#freemarker-expressions)
3. [Conditions](#conditions)
4. [Loops](#loops)
5. [Variables](#variables)
6. [Transformation](#transformation)
7. [Resource Loading](#resource-loading)
8. [Including Other Resources/Files](#including-other-resourcesfiles)
9. [Switch Statements](#switch-statements)

---

## Replacement Expressions

Replacement expressions allow you to replace entire values (not just strings) with the result of an expression. The result can be any JSON-supported type: String, boolean, int, date, map, or list.

### Syntax

- `@fmarker:` - FreeMarker expression evaluation
- `@xpath:` - XPath expression (returns first match)
- `@xpathMulti:` - XPath expression (returns all matches)

### Examples

**In Map Values:**
```json
{
  "key2": "@fmarker: empMap.employeeNames[0]",
  "bookCost": "@xpath: /books[name='Sphere']/cost",
  "bookNames": "@xpathMulti: /books//title"
}
```

**In List Values:**
```json
{
  "listWithNoCond": ["@fmarker: book.cost", 20]
}
```

---

## FreeMarker Expressions

String values (both in keys and values) are processed as FreeMarker templates when replacement expressions are not used.

### Examples

**In Key:**
```json
{
  "book-${book.name}": {
    "desc": "This is a science fiction."
  }
}
```

**In Value:**
```json
{
  "name": "${name}"
}
```

**Note:** Available FreeMarker methods can be found in [Yukthi Free Marker](https://github.com/yukthitech/utils/tree/master/yukthi-free-marker)

---

## Conditions

Conditions allow you to include or exclude parts of JSON conditionally.

### Map Objects

To conditionally include a map object, add an `@condition` key:

```json
{
  "book": {
    "@condition": "book.availableCount >= 1",
    "title": "${book.title}"
  }
}
```

The "book" map will only be included if the condition evaluates to `true`.

### Lists

To conditionally include a list, use `@condition:` as the first element:

```json
{
  "books": ["@condition: library.enabled", 1000, {"a": "b"}]
}
```

The list will only be included if the condition evaluates to `true`.

### Simple Values

For simple values, use a map with `@condition`, `@value`, and optionally `@falseValue`:

**Basic Example:**
```json
{
  "enabled": {
    "@condition": "book.available == 1",
    "@value": "Enabled"
  }
}
```

**With False Value:**
```json
{
  "enabled": {
    "@condition": "book.available == 1",
    "@value": "Enabled",
    "@falseValue": "Disabled"
  }
}
```

**In Lists:**
```json
[
  {
    "@condition": "book.available == 1",
    "@value": "@fmarker: book.price"
  },
  100
]
```

**Note:** When `@value` is defined, other keys in the map are ignored.

---

## Loops

Loops allow you to repeat elements dynamically based on data from the context.

### List Loops

Use `@for-each(varName):` to loop through a collection:

```json
{
  "books": [
    {
      "@for-each(book)": "books",
      "title": "${book.title}",
      "desc": "This is book with summary - ${book.summary}"
    }
  ]
}
```

### Map Entry Loops

Loop through map entries with dynamic keys:

```json
{
  "bookMap": {
    "@fmarker: book.id": {
      "@for-each(book)": "books",
      "available": 1
    }
  }
}
```

### Static List Loops

You can also loop through a static list:

```json
{
  "books": {
    "@fmarker: title": {
      "@for-each(title)": ["Davinci Code", "Sphere", "Prey"],
      "available": 1
    }
  }
}
```

### Loop Conditions

Filter loop iterations using `@for-each-condition`:

```json
{
  "books": {
    "@fmarker: title": {
      "@for-each(title)": ["Davinci Code", "Sphere", "Prey"],
      "@for-each-condition": "isEnabled(title)",
      "available": 1
    }
  }
}
```

### Complete Example

```json
{
  "Justbooks": [
    {
      "@for-each(book)": "libraries.Justbooks.books",
      "@for-each-condition": "book.copyCount gt 0",
      "name": "${book.title}",
      "desc": "Type: ${book.type}, author: ${book.author}"
    }
  ]
}
```

---

## Variables

Variables allow you to store complex expressions for reuse or to simplify complex expressions.

### Syntax

Use `@set(varName):` to define a variable:

```json
{
  "@set(bookMap)": {
    "titles": "@xpathMulti: /books//title"
  },
  "key2": "@fmarker: bookMap.titles[0]"
}
```

### Example with Complex Expression

```json
{
  "list": [
    100,
    {
      "@set(secondLib)": "@xpath: /libraries/Imagine/name"
    },
    200
  ],
  "map": {
    "key1": "val1",
    "@set(libJb)": {
      "bookNames": "@xpathMulti: /libraries/Justbooks/books//title"
    },
    "key2": "@fmarker: libraries.Imagine.books[1].title"
  },
  "names": "@fmarker: libJb.bookNames",
  "library": "@fmarker: secondLib"
}
```

**Note:** Variables are accessible as standard context attributes after they are set.

---

## Transformation

Use `@transform` to transform values using expressions. The current value being transformed is available as `thisValue`.

### Example: Convert Object to JSON String

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

**Result:**
```json
{
  "result": "{\"someVal\":1,\"books\":[\"Davinci Code\",\"Sphere\",\"Prey\"]}"
}
```

---

## Resource Loading

Load external content from resource files using `@resource`.

### Basic Usage

```json
{
  "result": {
    "@resource": "/res-file.txt"
  }
}
```

### With Parameters

```json
{
  "result": {
    "@resource": "/res-file.txt",
    "@resParams": {
      "key1": "val1"
    }
  }
}
```

Parameters are accessible in resource content as `resParams.key1`.

### Disable Expression Processing

By default, expressions in loaded resources are processed. To disable:

```json
{
  "result": {
    "@resource": "/res-file.txt",
    "@expressions": false,
    "@resParams": {
      "key1": "val1"
    }
  }
}
```

### With Transformation

```json
{
  "type": "Fiction",
  "metaInfo": {
    "@resource": "/fiction-meta.xml",
    "@transform": "@fmarker: normalizeXml(thisValue)"
  }
}
```

---

## Including Other Resources/Files

Split complex templates into multiple files and include them.

### Include Resource

```json
{
  "key1": "@fmarker: ckey1",
  "extra": {
    "@includeResource": "/include-res.json"
  },
  "key2": "@fmarker: ckey2"
}
```

### Include File

```json
{
  "key1": "@fmarker: ckey1",
  "extra": {
    "@includeFile": "./src/test/resources/include-file.json"
  },
  "key2": "@fmarker: ckey2"
}
```

### Replace Entry

Use `@replace(name)` to merge included entries into the parent map:

```json
{
  "key1": "@fmarker: ckey1",
  "@replace(extra)": {
    "@includeResource": "/include-res.json"
  },
  "key2": "@fmarker: ckey2"
}
```

**Note:** The `name` parameter in `@replace(name)` is only used as a key differentiator to allow multiple `@replace` entries in a single map.

### With Parameters

Pass parameters to included templates:

```json
{
  "mainMap": {
    "@includeResource": "/include-recursive.json",
    "@params": {
      "value": "@fmarker: 3"
    }
  },
  "ckey1": "@fmarker: ckey1"
}
```

Parameters are accessible in the included template using the `params` key.

**Note:** Parameters map is static (JEL expressions are not supported in keys), only string-based expressions are supported in values.

### With Condition

Include conditionally:

```json
{
  "key1": "@fmarker: ckey1",
  "@replace(rightCond)": {
    "@condition": "ckey1 == 'cval1'",
    "@includeResource": "/include-res2.json",
    "@params": {
      "paramKey1": "paramValue"
    }
  },
  "key2": "@fmarker: ckey2"
}
```

---

## Switch Statements

Switch statements allow you to evaluate multiple conditions and return the matching value.

### Basic Syntax

```json
{
  "number": {
    "@switch": [
      {
        "@case": "a lt 0",
        "@value": "Negative Number"
      },
      {
        "@case": "a gt 100",
        "@value": "Invalid Number"
      },
      {
        "@value": "@fmarker: a * 100"
      }
    ]
  }
}
```

### Rules

1. Cases are evaluated in order
2. The first matching case is used
3. The last case without `@case` is the default case
4. Default case must be the last case

### Examples

**String Values:**
```json
{
  "message": {
    "@switch": [
      {
        "@case": "status == 'active'",
        "@value": "User is active"
      },
      {
        "@case": "status == 'inactive'",
        "@value": "User is inactive"
      },
      {
        "@value": "Unknown status: ${status}"
      }
    ]
  }
}
```

**Complex Expressions:**
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
        "@case": "score gte 70",
        "@value": "C"
      },
      {
        "@value": "F"
      }
    ]
  }
}
```

**Nested Objects:**
```json
{
  "category": {
    "@switch": [
      {
        "@case": "user.age lt 18",
        "@value": "Minor"
      },
      {
        "@case": "user.age gte 18 && user.premium",
        "@value": "Premium Adult"
      },
      {
        "@case": "user.age gte 18",
        "@value": "Adult"
      },
      {
        "@value": "Unknown"
      }
    ]
  }
}
```

**Map Values:**
```json
{
  "permissions": {
    "@switch": [
      {
        "@case": "type == 'admin'",
        "@value": {
          "read": true,
          "write": true,
          "delete": true
        }
      },
      {
        "@case": "type == 'user'",
        "@value": {
          "read": true,
          "write": true,
          "delete": false
        }
      },
      {
        "@value": {
          "read": true,
          "write": false,
          "delete": false
        }
      }
    ]
  }
}
```

**Null Values:**
If a case returns `null`, the key is excluded from the result:

```json
{
  "result": {
    "@switch": [
      {
        "@case": "flag",
        "@value": null
      },
      {
        "@value": "default"
      }
    ]
  }
}
```

---

## Complete Example

Here's a comprehensive example combining multiple features:

```json
{
  "bookNames": "@xpathMulti: /libraries/Justbooks/books//title",
  "copyCount": "@xpath: /libraries/Justbooks/books[1]/copyCount",
  "allDocCounts": [
    "@fmarker: sizeOf(libraries.Justbooks.books)",
    "@fmarker: sizeOf(libraries.Imagine.books)"
  ],
  "Justbooks": [
    {
      "@for-each(book)": "libraries.Justbooks.books",
      "name": "${book.title}",
      "desc": "Type: ${book.type}, author: ${book.author}"
    }
  ],
  "Imagine": [
    {
      "@for-each(book)": "libraries.Imagine.books",
      "@for-each-condition": "book.copyCount gt 0",
      "name": "${book.title}",
      "desc": "Type: ${book.type}, author: ${book.author}"
    }
  ],
  "status": {
    "@switch": [
      {
        "@case": "library.enabled",
        "@value": "Active"
      },
      {
        "@value": "Inactive"
      }
    ]
  }
}
```

---

## Best Practices

1. **Use variables** for complex expressions that are used multiple times
2. **Use includes** to split large templates into manageable pieces
3. **Use conditions** to handle optional data gracefully
4. **Use loops** for dynamic collections
5. **Use switch statements** for multiple conditional branches
6. **Use resource loading** for large static content or multi-line text
7. **Use transformation** to convert data formats when needed

---

## Error Handling

Common errors and their causes:

- **Invalid expression type**: Check that expression prefix is one of: `@fmarker:`, `@xpath:`, `@xpathMulti:`
- **Invalid condition**: Ensure condition expression is valid FreeMarker syntax
- **Missing @value in switch case**: Every switch case must have `@value` specified
- **Default case not last**: The default case (without `@case`) must be the last case in a switch statement
