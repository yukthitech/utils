# Developer Guide: Yukthi-Transform Library

## Table of Contents
1. [Overview](#overview)
2. [Class Organization](#class-organization)
3. [Transformation Flow](#transformation-flow)
4. [Common vs Format-Specific Logic](#common-vs-format-specific-logic)
5. [Key Design Patterns](#key-design-patterns)
6. [Extension Points](#extension-points)

---

## Overview

The **yukthi-transform** library is a transformation/templating engine that transforms object data from multiple sources (JSON, XML, POJOs) into different forms based on templates. The library designed for JSON/XML transformation.

The architecture follows a clear separation of concerns:
- **Template Parsing**: Format-specific factories parse templates into a common internal representation
- **Transformation Execution**: Common engine processes the internal representation regardless of input/output format
- **Output Generation**: Format-specific generators produce the final output

---

## Class Organization

### Package Structure

```
com.yukthitech.transform/
├── TransformEngine.java          # Core transformation engine (COMMON)
├── TransformState.java            # Transformation state management (COMMON)
├── TransformException.java         # Exception handling (COMMON)
├── Conversions.java                # Expression and resource processing (COMMON)
├── ExpressionUtil.java             # FreeMarker expression utilities (COMMON)
├── TransformFmarkerMethods.java   # Custom FreeMarker methods (COMMON)
│
├── ITransformContext.java          # Context interface (COMMON)
├── MapExprContext.java             # Map-based context implementation (COMMON)
├── PojoExprContext.java            # POJO-based context implementation (COMMON)
│
├── IContentLoader.java             # Resource loading interface (COMMON)
├── ITransformConstants.java        # Constants (COMMON)
│
└── template/
    ├── TransformTemplate.java      # Common template representation (COMMON)
    ├── ITemplateFactory.java       # Template factory interface (COMMON)
    ├── JsonTemplateFactory.java    # JSON template parser (JSON-SPECIFIC)
    ├── XmlTemplateFactory.java     # XML template parser (XML-SPECIFIC)
    ├── XmlTemplateParserHandler.java # XML parsing handler (XML-SPECIFIC)
    ├── XmlDynamicBean.java         # XML dynamic bean (XML-SPECIFIC)
    │
    ├── IGenerator.java             # Output generator interface (COMMON)
    ├── JsonGenerator.java          # JSON output generator (JSON-SPECIFIC)
    ├── XmlGenerator.java           # XML output generator (XML-SPECIFIC)
    │
    └── TransformUtils.java         # Utility methods (COMMON)
```

### Class Categories

#### 1. **Common Classes** (Format-Agnostic)

These classes work with both JSON and XML transformations:

- **`TransformEngine`**: Main entry point that orchestrates the transformation process
- **`TransformState`**: Manages transformation state, path tracking, and generator instance
- **`TransformTemplate`**: Common internal representation of templates (TransformObject, TransformList, Expression)
- **`Conversions`**: Handles expression evaluation, resource loading, and include processing
- **`ExpressionUtil`**: FreeMarker expression processing utilities
- **`ITransformContext`**: Context interface for accessing transformation data
- **`MapExprContext`**: Context implementation for Map-based data
- **`PojoExprContext`**: Context implementation for POJO-based data

#### 2. **JSON-Specific Classes**

- **`JsonTemplateFactory`**: Parses JSON templates into `TransformTemplate`
- **`JsonGenerator`**: Generates JSON output from processed template

#### 3. **XML-Specific Classes**

- **`XmlTemplateFactory`**: Parses XML templates into `TransformTemplate`
- **`XmlTemplateParserHandler`**: Handles XML parsing with reserved namespace (`/transform`)
- **`XmlDynamicBean`**: Dynamic bean representation for XML nodes
- **`XmlGenerator`**: Generates XML output from processed template

---

## Transformation Flow

### High-Level Flow

```
┌─────────────────┐
│ Template String │ (JSON or XML)
│   (Input)       │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Template Factory│ (JsonTemplateFactory or XmlTemplateFactory)
│   (Parse)       │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ TransformTemplate│ (Common Internal Representation)
│   (Structure)   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ TransformEngine │ (Common Processing Logic)
│   (Process)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Generator      │ (JsonGenerator or XmlGenerator)
│  (Generate)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Output String   │ (JSON or XML)
│   (Result)      │
└─────────────────┘
```

### Detailed Flow

#### Phase 1: Template Parsing

**JSON Path:**
```
JSON String
  → JsonTemplateFactory.parseTemplate()
  → parseObject() [recursive]
  → TransformTemplate (with JsonGenerator.class)
```

**XML Path:**
```
XML String
  → XMLBeanParser.parse() [with XmlTemplateParserHandler]
  → XmlDynamicBean
  → XmlTemplateFactory.parseTemplate()
  → parseObject() [recursive]
  → TransformTemplate (with XmlGenerator.class)
```

**Key Difference:**
- JSON: Direct parsing using Jackson ObjectMapper
- XML: Uses XMLBeanParser with custom handler to identify reserved namespace attributes/elements

#### Phase 2: Template Structure

Both formats parse into the same `TransformTemplate` structure:

```java
TransformTemplate
  └── root: TransformObject | TransformList
      ├── TransformObject
      │   ├── condition: String
      │   ├── value: Object
      │   ├── falseValue: Object
      │   ├── forEachLoop: ForEachLoop
      │   ├── switchStatement: Switch
      │   ├── resource: Resource
      │   ├── include: Include
      │   ├── transformExpression: Expression
      │   └── fields: List<TransformObjectField>
      │
      ├── TransformList
      │   ├── condition: String
      │   └── objects: List<Object>
      │
      └── Expression
          ├── type: ExpressionType (FMARKER, XPATH, XPATH_MULTI, TEMPLATE, STRING)
          └── expression: String
```

#### Phase 3: Transformation Execution

**Common Processing (TransformEngine):**

```java
TransformEngine.process()
  → TransformState (creates appropriate Generator)
  → processObject() [recursive]
     ├── TransformList → processList()
     ├── TransformObject → processMap()
     └── Expression → Conversions.processExpression()
```

**Processing Steps:**

1. **Condition Evaluation**: Check if object/list should be included
2. **Switch Processing**: Evaluate switch statements
3. **Value Processing**: Handle @value/@falseValue
4. **Resource Loading**: Process @resource directives
5. **Include Processing**: Process @includeResource/@includeFile
6. **Loop Processing**: Handle @for-each loops
7. **Field Processing**: Process each field recursively
8. **Expression Evaluation**: Evaluate expressions (@fmarker, @xpath, etc.)

#### Phase 4: Output Generation

**JSON Path:**
```
Processed Object Tree
  → JsonGenerator.setField()
  → LinkedHashMap<String, Object>
  → JsonGenerator.formatObject()
  → JSON String
```

**XML Path:**
```
Processed Object Tree
  → XmlGenerator.setField()
  → DOM Element
  → XmlGenerator.formatObject()
  → XML String
```

**Key Difference:**
- JSON: Uses `LinkedHashMap` to build object structure
- XML: Uses DOM `Element` to build XML structure

---

## Common vs Format-Specific Logic

### Common Logic (Shared by Both JSON and XML)

#### 1. **Template Structure**
- Both use `TransformTemplate`, `TransformObject`, `TransformList`, `Expression`
- Same internal representation regardless of input format

#### 2. **Transformation Engine**
- `TransformEngine` processes templates identically for both formats
- Same logic for:
  - Condition evaluation
  - Loop processing
  - Expression evaluation (FreeMarker, XPath)
  - Resource loading
  - Include processing
  - Switch statements
  - Variable setting

#### 3. **Expression Processing**
- `Conversions.processExpression()` handles all expression types:
  - `@fmarker:` - FreeMarker expressions
  - `@xpath:` - XPath (first match)
  - `@xpathMulti:` - XPath (all matches)
  - Template strings with `${}` syntax

#### 4. **Context Handling**
- `ITransformContext` interface works for both formats
- `MapExprContext` and `PojoExprContext` are format-agnostic

#### 5. **Path Tracking**
- `TransformState` tracks paths using generator-specific separators:
  - JSON: `>` separator (e.g., `>field1>field2`)
  - XML: `/` separator (e.g., `/field1/field2`)

### Format-Specific Logic

#### 1. **Template Parsing**

**JSON (`JsonTemplateFactory`):**
- Uses Jackson `ObjectMapper` to parse JSON
- Recognizes special keys starting with `@`:
  - `@condition`, `@value`, `@falseValue`
  - `@for-each(varName):`, `@for-each-condition`
  - `@set(varName):`, `@switch`, `@case`
  - `@resource`, `@includeResource`, `@includeFile`
  - `@replace(name)`, `@transform`
- List conditions: First element as `@condition: expression`

**XML (`XmlTemplateFactory`):**
- Uses `XMLBeanParser` with `XmlTemplateParserHandler`
- Recognizes reserved namespace `/transform` (prefix `t:`):
  - Attributes: `t:condition`, `t:value`, `t:falseValue`
  - Attributes: `t:forEach`, `t:loopVar`, `t:forEachCondition`
  - Attributes: `t:name`, `t:transform`
  - Elements: `<t:set>`, `<t:switch>`, `<t:case>`
  - Elements: `<t:resource>`, `<t:includeResource>`, `<t:includeFile>`
  - Elements: `<t:replace>`
- Parses into `XmlDynamicBean` first, then converts to `TransformTemplate`

#### 2. **Output Generation**

**JSON (`JsonGenerator`):**
- Creates `LinkedHashMap<String, Object>` for objects
- Simple key-value mapping
- `formatObject()` uses Jackson to serialize to JSON string
- Handles lists as `List<Object>`
- `injectReplaceEntry()` merges map entries

**XML (`XmlGenerator`):**
- Creates DOM `Element` objects
- Handles three field types:
  - `ATTRIBUTE`: Sets XML attributes
  - `NODE`: Creates child elements
  - `TEXT_CONTENT`: Sets element text content
- `formatObject()` uses `TransformUtils.toXmlString()` to serialize
- `injectReplaceEntry()` merges attributes and child nodes
- Handles element renaming when name expression differs

#### 3. **Path Separators**

**JSON:**
- Root path: `""`
- Field separator: `>` (e.g., `>field1>field2`)
- Index separator: `[index]` (e.g., `[0]`)

**XML:**
- Root path: `""`
- Field separator: `/` (e.g., `/field1/field2`)
- Index separator: `[index]` (e.g., `[0]`)

#### 4. **Special Handling**

**JSON:**
- Map keys can be expressions (evaluated at runtime)
- Simple value replacement in maps
- List first element can be condition

**XML:**
- Element names can be dynamic via `t:name` attribute
- Attributes vs nodes vs text content distinction
- Reserved namespace handling (`/transform`)
- Element renaming support

---

## Key Design Patterns

### 1. **Strategy Pattern**

**Template Factories:**
- `ITemplateFactory` interface
- `JsonTemplateFactory` and `XmlTemplateFactory` implementations
- Different parsing strategies for different formats

**Generators:**
- `IGenerator` interface
- `JsonGenerator` and `XmlGenerator` implementations
- Different output generation strategies

### 2. **Template Method Pattern**

**TransformEngine:**
- Defines the skeleton of the transformation algorithm
- Delegates format-specific operations to generators via `TransformState`
- Common processing logic with format-specific output generation

### 3. **Adapter Pattern**

**Context Adapters:**
- `ITransformContext` interface
- `MapExprContext` adapts `Map<String, Object>`
- `PojoExprContext` adapts POJO objects
- Allows uniform access to different data sources

### 4. **Factory Pattern**

**Template Creation:**
- `TransformTemplate` created with generator class reference
- `TransformState` instantiates appropriate generator based on template type

---

## Extension Points

### 1. **Custom Context Implementation**

Implement `ITransformContext` to support custom data sources:

```java
public class CustomContext implements ITransformContext {
    // Implement entrySet(), get(), setValue()
}
```

### 2. **Custom Content Loader**

Implement `IContentLoader` to customize resource/file loading:

```java
public class CustomContentLoader implements IContentLoader {
    @Override
    public String loadResource(String resource) {
        // Custom resource loading logic
    }
    
    @Override
    public String loadFile(String file) {
        // Custom file loading logic
    }
}
```

### 3. **Custom FreeMarker Methods**

Add custom methods via `TransformFmarkerMethods` or by loading custom classes:

```java
freeMarkerEngine.loadClass(MyCustomMethods.class);
```

### 4. **Custom Generator** (Future Extension)

To add a new output format, implement `IGenerator`:

```java
public class CustomGenerator implements IGenerator {
    // Implement all interface methods
    // Register in TransformTemplate creation
}
```

---

## Key Classes Deep Dive

### TransformEngine

**Responsibilities:**
- Main orchestration of transformation process
- Recursive processing of template structure
- Delegation to format-specific generators via `TransformState`

**Key Methods:**
- `process()`: Main entry point
- `processObject()`: Recursive processing dispatcher
- `processMap()`: Process TransformObject
- `processList()`: Process TransformList
- `procesRepeatedElement()`: Handle loops
- `processCondition()`: Evaluate conditions
- `processSwitch()`: Handle switch statements

### TransformState

**Responsibilities:**
- Maintains transformation state
- Manages generator instance
- Tracks current path
- Handles attribute mode (for XML set operations)

**Key Methods:**
- `forField()`: Create state for field processing
- `forIndex()`: Create state for list index
- `forClone()`: Create state for loop iteration
- `newObject()`: Delegate to generator
- `setField()`: Delegate to generator
- `formatObject()`: Delegate to generator

### TransformTemplate

**Structure:**
- Root object (TransformObject or TransformList)
- Generator class reference (JsonGenerator.class or XmlGenerator.class)

**Key Inner Classes:**
- `TransformObject`: Represents an object/map/element
- `TransformList`: Represents a list/array
- `TransformObjectField`: Represents a field/attribute/node
- `Expression`: Represents an expression
- `ForEachLoop`: Represents a loop definition
- `Switch`: Represents a switch statement
- `SwitchCase`: Represents a switch case
- `Resource`: Represents a resource to load
- `Include`: Represents an include directive

### Conversions

**Responsibilities:**
- Expression evaluation (FreeMarker, XPath)
- Resource loading and processing
- Include processing
- Transform expression evaluation

**Key Methods:**
- `processExpression()`: Evaluate expressions
- `processMapRes()`: Process resource directives
- `processInclude()`: Process include directives
- `checkForTransform()`: Apply transform expressions

---

## Summary

The yukthi-transform library follows a clean architecture where:

1. **Parsing is format-specific** (JsonTemplateFactory vs XmlTemplateFactory)
2. **Processing is common** (TransformEngine works identically for both)
3. **Generation is format-specific** (JsonGenerator vs XmlGenerator)

This design allows:
- Easy addition of new formats (implement ITemplateFactory and IGenerator)
- Shared transformation logic (conditions, loops, expressions)
- Format-specific optimizations where needed
- Consistent behavior across formats

The common internal representation (`TransformTemplate`) is the key to this design, allowing the transformation engine to be format-agnostic while supporting format-specific parsing and generation.
