# Yukthi Utils — Documentation Index

Central AI-friendly documentation for modules under the [utils](../) repository. Use these docs as Cursor references when integrating or extending Yukthi libraries in other projects.

---

## Modules

| Module | Purpose | Docs |
|--------|---------|------|
| **yukthi-orm** | Annotation-driven Java ORM (`yukthi-data`): repositories, finders, transactions, native FreeMarker SQL | [Overview](./yukthi-orm/README.md) |
| **yukthi-xml-mapper** | Runtime XML ↔ Java bean mapper (reserve nodes, adders, bean refs, DynamicBean) | [Overview](./yukthi-xml-mapper/README.md) |
| **yukthi-free-marker** | FreeMarker wrapper with annotated methods/directives and default helpers | [Overview](./yukthi-free-marker/README.md) |
| **yukthi-utils** | Shared Java utilities (collections, beans, CLI, expressions, crypto, pooling) and REST client | [Overview](./yukthi-utils/README.md) · [RestClient](./yukthi-utils/RestClient.md) |
| **yukthi-transform** | JSON/XML transformation engine (templates, FreeMarker, XPath/JsonPath, conditions, loops) | [AI template guide](./yukthi-transform/ai-template-generation-guide.md) · [JSON](./yukthi-transform/json-transformation-guide.md) · [XML](./yukthi-transform/xml-transformation-guide.md) · [FM methods](./yukthi-transform/transform-fmarker-methods.md) · [Developer guide](./yukthi-transform/developer-guide.md) |

---

## Quick module summaries

### [yukthi-orm](./yukthi-orm/README.md)

Lightweight ORM built around `RepositoryFactory` + `ICrudRepository`. Map POJOs with JPA-style and Yukthi annotations; define finder/update/delete/native methods on repository interfaces. Depend on `com.yukthitech:yukthi-data`.

### [yukthi-xml-mapper](./yukthi-xml-mapper/README.md)

Reflection-based XML bean mapping via `XMLBeanParser` / `XmlBeanWriter`. Supports runtime `beanType`, collection adders, Spring-like bean refs, and schema-less `DynamicBean` trees. Used widely for Yukthi XML configuration.

### [yukthi-free-marker](./yukthi-free-marker/README.md)

`FreeMarkerEngine` for templates, conditions, and typed value expressions. Register public static helpers with `@FreeMarkerMethod` / `@FreeMarkerDirective`. Ships date, collection, string, regex, file, and random helpers.

### [yukthi-utils](./yukthi-utils/README.md)

General-purpose helpers (`CommonUtils`, `PropertyAccessor`, exceptions, CLI, expressions, pools) plus **[RestClient](./yukthi-utils/RestClient.md)** over Apache HttpClient 5 for typed JSON/form/multipart HTTP calls.

### [yukthi-transform](./yukthi-transform/ai-template-generation-guide.md)

Template-driven transformation of Maps/POJOs/XML into JSON or XML using FreeMarker expressions, path queries, conditions, loops, includes, and switch. Start with the AI template guide or the JSON/XML syntax guides.

---

## How to use with Cursor

1. Open or `@`-reference this index: `docs/README.md`
2. Drill into the module folder you need (e.g. `@docs/yukthi-utils/RestClient.md`)
3. Prefer these docs over inventing ad-hoc APIs when working in Yukthi-based projects

```
docs/
├── README.md                 ← this index
├── yukthi-orm/
├── yukthi-xml-mapper/
├── yukthi-free-marker/
├── yukthi-utils/
│   ├── README.md
│   └── RestClient.md
└── yukthi-transform/
    ├── ai-template-generation-guide.md
    ├── json-transformation-guide.md
    ├── xml-transformation-guide.md
    ├── transform-fmarker-methods.md
    ├── developer-guide.md
    └── images/
```
