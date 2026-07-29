# Yukthi XML Mapper — AI Reference

> **Audience:** Cursor / AI agents mapping XML ↔ Java beans in other projects.  
> **Artifact:** `com.yukthitech:yukthi-xml-mapper`  
> **License:** Apache 2.0 · **Java:** 8+ (via parent)

---

## 1. What this module is

**Yukthi XML Mapper** is a **runtime, reflection-based** XML ↔ Java bean mapper. Unlike JAXB, it does **not** require generated classes or schema-bound bindings. The XML document maps to a **root bean**; attributes and child elements map to setters/adders on that bean and nested beans.

### Why use it instead of JAXB

| Feature | Behavior |
|---------|----------|
| Runtime types | `beanType` can inject concrete classes at parse time |
| Attributes ↔ elements | Interchangeable for simple properties |
| Adder methods | Prefers `addX(...)` over `setX(...)` for collections |
| Bean references | Spring-like `beanId` / `beanRef` |
| Reserve namespace | Pluggable special nodes/attributes |
| DynamicBean | Schema-less XML → Map / JSON bridge |

Primary entry points:

- `com.yukthitech.ccg.xml.XMLBeanParser` — XML → Java
- `com.yukthitech.ccg.xml.writer.XmlBeanWriter` — Java → XML
- `com.yukthitech.ccg.xml.DefaultParserHandler` — default mapping rules
- `com.yukthitech.ccg.xml.DynamicBean` — untyped tree

---

## 2. Maven dependency

```xml
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-xml-mapper</artifactId>
    <version>1.3.16-SNAPSHOT</version>
</dependency>
```

Depends on: `yukthi-utils`, `commons-io`, `jackson-databind`.

---

## 3. XML → Java (`XMLBeanParser`)

```java
// Populate an existing root bean
XMLBeanParser.parse(InputStream xml, Object rootBean);

// With custom handler
XMLBeanParser.parse(InputStream xml, Object rootBean, IParserHandler handler);

// Create root via handler (or DynamicBean)
Object root = XMLBeanParser.parse(InputStream xml);
Object root = XMLBeanParser.parse(InputStream xml, IParserHandler handler);

// Optional XSD Schema on overloads
XMLBeanParser.parse(InputStream xml, Object rootBean, IParserHandler handler, Schema schema);
```

Default handler: `DefaultParserHandler`. Namespace-aware SAX parsing.

### Minimal example

```java
ListBean bean = new ListBean();
XMLBeanParser.parse(getClass().getResourceAsStream("/list-data.xml"), bean);
```

```xml
<listData xmlns:ccg="/fw/ccg/XMLBeanParser">
  <strList>
    <ccg:element>Value1</ccg:element>
    <ccg:element>Value2</ccg:element>
  </strList>
  <objectList>
    <ccg:element ccg:beanType="com.example.TestDataBean" intVal="5"/>
  </objectList>
</listData>
```

---

## 4. Mapping rules (parse)

1. Element / attribute name → property via **setter** or **adder** (`add*` preferred when both exist).
2. Hyphenated names → camelCase (`bean-map` → `beanMap`).
3. Text / simple attrs convert to primitives, wrappers, `String`, `Date`, `Class`, enums, `BigDecimal` / `BigInteger`, etc.
4. Nested beans: instantiate from setter/adder parameter type, recurse.
5. Polymorphic / `Object` properties: set `ccg:beanType="fully.qualified.ClassName"`.
6. Expressions in text/attrs: `${property}` against root bean; `#constant` for constants; escape with `$$`. Disable via `exprPattern` reserved node.
7. Meta-text (text + child elements together) is **not** supported unless the bean implements `IHybridTextBean`.

---

## 5. Reserved namespaces

| URI | Role |
|-----|------|
| `http://xmlbeanparser.yukthitech.com/reserved` | Preferred reserved namespace |
| `/fw/ccg/XMLBeanParser` | Legacy reserved namespace (still widely used in tests) |
| `http://xmlbeanparser.yukthitech.com/wrap` (and legacy wrap URI) | Wrap nodes — structural only, ignored for mapping |

Typical declaration:

```xml
<root xmlns:ccg="/fw/ccg/XMLBeanParser">
  <!-- or -->
<root xmlns:res="http://xmlbeanparser.yukthitech.com/reserved">
```

### Reserved attributes

| Attribute | Meaning |
|-----------|---------|
| `beanType` | Runtime class for this node |
| `beanId` | Register bean under id |
| `beanRef` | Inject previously registered bean |
| `dateFormat` | `SimpleDateFormat` pattern (default often `MM/dd/yyyy`) |
| `paramTypes` / `params` | Constructor arguments |
| `beanExpr` | Dot-path / expression object |
| `trimLines` | Trim each line of text |

Also: attribute values like `ref:beanId` resolve to registered beans.

### Built-in reserved nodes

| Node | Purpose |
|------|---------|
| `element` | Add item to parent `Collection` |
| `entry` | Put into parent `Map` (`key` attribute) |
| `bean` | Define named bean (`id`, optional `type`) |
| `json` | Parse JSON text into property / registry |
| `includeXml` | Nested parse (`resource` or `file`) |
| `customNodeHandler` | Register another `IReserveNodeHandler` |
| `factory` | Map type → `BeanFactory` |
| `constant` | Named constant |
| `exprPattern` | Enable/disable `${}` expressions |

---

## 6. Bean id / ref example

```xml
<listData xmlns:res="http://xmlbeanparser.yukthitech.com/reserved">
  <res:bean id="strVal">Some string value</res:bean>
  <res:bean id="intLst">int[]: 1, 2, 3, 4</res:bean>
  <objectList>
    <res:element res:beanType="com.example.TestBeanForRef">
      <value1 res:beanRef="strVal"/>
      <value2 res:beanRef="intLst"/>
    </res:element>
  </objectList>
</listData>
```

---

## 7. DynamicBean (schema-less)

```java
// Typed root with dynamic children
DynamicTestBean bean = new DynamicTestBean();
XMLBeanParser.parse(in, bean, new DynamicBeanParserHandler());

// Fully dynamic root
DynamicBean root = (DynamicBean) XMLBeanParser.parse(in, null, new DynamicBeanParserHandler());
Map<?, ?> map = root.toSimpleMap();
```

With type conversation enabled, attribute prefixes work:

```
int: 100 | boolean: true | int[]: 1, 2, 3 | date: ... | json: {...}
```

(`TypeConversionUtils` / `DynamicBeanParserHandler.setTypeConversationEnabled(true)`)

---

## 8. Java → XML (`XmlBeanWriter`)

```java
String xml = XmlBeanWriter.writeToString("root-name", bean);
String xml = XmlBeanWriter.writeToString("root-name", bean, config);

XmlBeanWriter.writeTo("root-name", bean, file);
XmlBeanWriter.writeTo("root-name", bean, outputStream, config);

XmlBeanWriter.clearCache(); // clear property-reflection cache
```

### Writer annotations

| Annotation | Effect |
|------------|--------|
| `@XmlAttribute` | Force property as XML attribute (`name` optional) |
| `@XmlElement` | Force as element (`name`, `cdata`) |
| `@XmlIgnore` | Skip on write |
| `@CollectionElement("item")` | Custom collection item element name (instead of `ccg:element`) |

### `XmlWriterConfig`

| Flag | Default | Effect |
|------|---------|--------|
| `indentXml` | false | Pretty-print |
| `escapeExpressions` | true | Emit disable for `${}` pattern |
| `readCompatible` | true | Prefer round-trip-safe properties |
| `excludeNameSpace` | false | Omit ccg/wrap xmlns |
| `excludeXmlDeclaration` | false | Omit `<?xml?>` |

### Round-trip pattern

```java
XmlWriterConfig config = new XmlWriterConfig()
    .setIndentXml(true)
    .setExcludeXmlDeclaration(true)
    .setReadCompatible(true);

String xml = XmlBeanWriter.writeToString("action-plan", plan, config);
ActionPlan again = new ActionPlan();
XMLBeanParser.parse(new ByteArrayInputStream(xml.getBytes()), again);
```

---

## 9. Extension points

| Interface | When to implement |
|-----------|-------------------|
| `IParserHandler` | Full custom parse behavior |
| `IReserveNodeHandler` + `@NodeName` | Custom reserved nodes |
| `BeanFactory` | Custom construction for a type |
| `IWriteableBean` | Custom write via `writeTo(XmlWriterContext)` |
| `IDynamicAttributeAcceptor` | Accept unknown attributes |
| `IDynamicNodeAcceptor` | Accept unknown child nodes |
| `IHybridTextBean` | Mixed text + children |
| `IParentAware` | Receive parent bean via `setParent` |
| `Validateable` | `validate()` at end of node |

Skip a subtree by returning `BeanNode.SKIP_NODE_ELEMENT` from the handler.

---

## 10. Package map

```
com.yukthitech.ccg.xml
  XMLBeanParser, DefaultParserHandler, DynamicBean, DynamicBeanParserHandler
  BeanNode, XMLAttributeMap, XMLUtil, …
com.yukthitech.ccg.xml.annotations
  XmlAttribute, XmlElement, XmlIgnore, CollectionElement
com.yukthitech.ccg.xml.reserved
  IReserveNodeHandler, built-in handlers (element, entry, bean, json, …)
com.yukthitech.ccg.xml.writer
  XmlBeanWriter, XmlWriterConfig, BeanToDocPopulator, IWriteableBean
com.yukthitech.ccg.xml.util
  TypeConversionUtils, Validateable, …
```

---

## 11. Guidance for AI agents

1. For known config POJOs → `XMLBeanParser.parse(stream, rootBean)`.
2. For schema-less / Map configs → `DynamicBeanParserHandler` + `toSimpleMap()`.
3. For persistence of beans → `XmlBeanWriter` with `readCompatible=true` if you need to parse back.
4. Prefer reserved namespace `http://xmlbeanparser.yukthitech.com/reserved`; legacy `/fw/ccg/XMLBeanParser` is fine when matching existing XML.
5. Use `beanType` for polymorphism; use `beanId`/`beanRef` for shared instances.
6. Prefer **adders** on beans that hold lists: `addItem(Item)` rather than only `setItems(List)`.
7. Do not mix free text and child elements on the same node unless implementing `IHybridTextBean`.
8. Used heavily by other Yukthi modules (ORM test config, Papilio, etc.) — prefer this mapper when integrating with those stacks.
