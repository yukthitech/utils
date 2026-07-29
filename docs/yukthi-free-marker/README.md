# Yukthi FreeMarker — AI Reference

> **Audience:** Cursor / AI agents evaluating FreeMarker templates/expressions in Java.  
> **Artifact:** `com.yukthitech:yukthi-free-marker`  
> **License:** Apache 2.0  
> **FreeMarker:** 2.3.31 (via parent BOM)

---

## 1. What this module is

Thin wrapper around Apache FreeMarker that:

- Processes **templates**, **boolean conditions**, and **value expressions** via `FreeMarkerEngine`
- Registers **public static** Java methods as FreeMarker **methods** (`${fn(...)}`) or **directives** (`<@dir>…</@dir>`) using annotations
- Ships a large set of default helpers (dates, collections, strings, regex, files, random, null checks)
- Can generate README method/directive docs via `DocGenerator`

**Main class:** `com.yukthitech.utils.fmarker.FreeMarkerEngine`

**Annotation package (note the typo):** `com.yukthitech.utils.fmarker.annotaion`  
(not `annotation`)

---

## 2. Maven dependency

```xml
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-free-marker</artifactId>
    <version>1.3.16-SNAPSHOT</version>
</dependency>
```

Depends on: FreeMarker, `yukthi-utils`, `commons-io`.

---

## 3. Core usage

```java
FreeMarkerEngine engine = new FreeMarkerEngine(); // loads default methods + directives

Map<String, Object> context = new HashMap<>();
context.put("user", "John Doe");

// Template → String
String out = engine.processTemplate("greeting", "Hello, ${user}!", context);

// Condition → boolean
boolean ok = engine.evaluateCondition("ageCheck", "age > 18", Map.of("age", 25));

// Expression → Object (typed result)
Object total = engine.fetchValue("sum", "price + tax", Map.of("price", 100, "tax", 20));
```

### Constructors

| Constructor | Behavior |
|-------------|----------|
| `FreeMarkerEngine()` | Default methods **and** directives loaded |
| `FreeMarkerEngine(excludeDefaultMethods, excludeDefaultDirectives)` | Bare or partial engine |
| `reset()` | Rebuild FreeMarker `Configuration` and reload defaults per flags |

### Prefer which API?

| Goal | API |
|------|-----|
| Render text / HTML / SQL snippets | `processTemplate` |
| Gate / if-condition | `evaluateCondition` |
| Compute a typed value (list, map, number) | `fetchValue` |
| Hot path (avoid re-parse) | `buildTemplate` / `buildConditionTemplate` / `buildValueTemplate` then process |

Underlying config: `engine.getConfiguration()` (number format `"#"`, template exception logging off).

---

## 4. Registering custom methods / directives

Only **public static** methods are registered. Method names must be unique across methods and directives.

```java
public class MyHelpers {
    @FreeMarkerMethod("sum")
    public static int add(int a, int b) {
        return a + b;
    }

    @FreeMarkerDirective(value = "trim", description = "Trim body")
    public static String trim(
            @FmParam(name = "body", body = true, description = "Directive body") String body) {
        return body.trim();
    }
}

engine.loadClass(MyHelpers.class);
// or: engine.registerMethod("sum", method); engine.registerDirective("trim", method);
```

```freemarker
${sum(2, 3)}
<@trim>  hello  </@trim>
```

### Annotations

| Annotation | Key attributes |
|------------|----------------|
| `@FreeMarkerMethod` | `value` (FM name), `description`, `returnDescription`, `examples` |
| `@FreeMarkerDirective` | `value`, `description`, `examples` |
| `@FmParam` | `name`, `description`, `defaultValue` (docs), `body`, `allParams` |
| `@ExampleDoc` | `usage`, `result`, `title` |
| `@Named` (`yukthi-utils`) | Groups methods in DocGenerator |

**Rules:** methods must not return `void`; missing args get Java defaults (`0`, `false`, `null`); varargs supported; FreeMarker models are deep-unwrapped then converted via `ConvertUtils`.

During processing, `FreeMarkerEngine.getCurrentInstance()` is set (ThreadLocal) so nested helpers like `groupBy` / `sortBy` can evaluate per-element expressions.

Failures throw `TemplateProcessingException` (extends `UtilsException`).

---

## 5. Default directives

| Name | Params | Behavior |
|------|--------|----------|
| `trim` | body | `body.trim()` |
| `indent` | body, `prefix` (default `""`), `retainLineBreaks` (default `false`) | Trim lines; optional prefix; join with/without newlines |

```freemarker
<@indent prefix='\t' retainLineBreaks=true>
  line1
  line2
</@indent>
```

---

## 6. Default methods (catalog)

### Common

| Method | Purpose |
|--------|---------|
| `isEmpty` / `isNotEmpty` | null, blank string, empty Collection/Map |
| `isNull` / `isNotNull` | |
| `nvl(value, nullValue, nonNullValue)` | |
| `ifTrue` / `ifFalse` | Treats string `"true"` (ignore case) as true |
| `toText` | `toString()`, null → `"null"` |
| `ifNull` / `ifNotNull` / `nullVal` | Ternary-style null helpers |
| `sizeOf` | string length / collection size / null→0 / else 1 |
| `replace` | `String.replace` |
| `initcap` | capitalize words |
| `isEqual` | `Objects.equals`; Numbers compared by `longValue` across types |
| `toBoolean` / `toInt` / `toLong` / `toFloat` / `toDouble` | |
| `compare` | `Comparable.compareTo` |
| `nullValue()` | Returns null (useful in XML configs) |
| `__fmarker_collect` | **Internal** — used by `fetchValue`; do not call in templates |

### Dates (`DateMethods`)

`toMillis`, `parseDate`, `dateToStr`, `addDays`, `addYears`, `addHours`, `addMinutes`, `addSeconds`, `today` (date-only), `now` (datetime)

```freemarker
${dateToStr(today(), 'dd/MM/yyyy')}
${dateToStr(addDays(today(), -1), 'dd/MM/yyyy')}
```

### Collections (`CollectionMethods`)

| Method | Notes |
|--------|-------|
| `groupBy(col, keyExpression)` | Per-element FM expression → `List` of groups (`key`, `elements`) |
| `sortBy(col, keyExpression)` | Sorted by key expression |
| `mapValues` / `mapKeys` | |
| `collectionToString` / `mapToString` | prefix/delimiter/suffix; map template `#key`/`#value` |
| `contains` / `notContains` | Collection values or Map **keys** |
| `listOf` / `setOf` / `newList` / `emptyMap` / `newMap` / `newSortedMap` | Factories |
| `addToCol` / `removeFromCol` / `addToMap` / `removeFromMap` | Mutating |
| `lstToSet` / `strToList` | |
| `isSubmap` / `intersectionCount` | |
| `push` / `pop` | Stack-like on lists |

```java
engine.fetchValue("g", "groupBy(beans, 'name')", context);
engine.fetchValue("s", "sortBy(beans, 'name')", context);
engine.fetchValue("l", "listOf('1', '2', '3')", context);
```

### Strings (`StringMethods`)

`strTrim`, `strContains(main, substr, ignoreCase)`, `lower`, `upper`, `isEqualString`, `isEqualIgnoreCase`, `indexOf`, `lastIndexOf`, `substr(string, start, end)` (end &lt; 0 → open end), `intToStr` / `strToInt` (radix), `split`

### Regex (`RegexFreeMarkerMethods`)

`regexParse` / `regexParseMatch` / `regexParseAll` (named groups → Map / List&lt;Map&gt;), `regexMatches`, `regexReplaceAll`

### Random (`RandomFreeMarkerMethods`)

`random`, `randomInt(min,max)`, `randomDouble`, `randomFloat`, `randomString(prefix)`, `randomAlpha(prefix,length)`, `randomAlphaNumeric(prefix,length)`

### Files (`FileFreeMarkerMethods`)

`fullPath`, `fileExists`, `isFile`, `isDirectory`, `fileNameFromUrl`, `fileNameFromPath`, `suffixOfFile`, `extensionOfFile`

---

## 7. Doc generation

`com.yukthitech.utils.fmarker.doc.DocGenerator` fills README placeholders:

- `[[defaultMethodContent]]`
- `[[defaultDirectiveContent]]`

using templates `method-doc.ftl` / `directive-doc.ftl`. Groups by `@Named` on declaring class. Skips names starting with `_`.

---

## 8. Package map

```
com.yukthitech.utils.fmarker
  FreeMarkerEngine, FreeMarkerTemplate, TemplateProcessingException
  MethodProxy, DirectiveProxy, MethodLoader
com.yukthitech.utils.fmarker.annotaion   ← note spelling
  FreeMarkerMethod, FreeMarkerDirective, FmParam, ExampleDoc
com.yukthitech.utils.fmarker.met
  CommonMethods, CommonDirectives, DateMethods, CollectionMethods,
  StringMethods, RegexFreeMarkerMethods, RandomFreeMarkerMethods, FileFreeMarkerMethods
com.yukthitech.utils.fmarker.doc
  DocGenerator, FreeMarkerMethodDoc, …
```

---

## 9. Guidance for AI agents

1. Instantiate `FreeMarkerEngine` (defaults on) unless you need a bare engine.
2. Use **`fetchValue`** when you need a Java object result; **`processTemplate`** for string output.
3. Custom helpers: public + static + `@FreeMarkerMethod` / `@FreeMarkerDirective`, then `loadClass`.
4. Import annotations from `…fmarker.annotaion` (typo in package name is intentional in the codebase).
5. Nested collection expressions require the engine’s ThreadLocal — only call them during an active `processTemplate` / `fetchValue`.
6. Prefer source under `met/` as ground truth if README method lists look incomplete.
7. Used by ORM native queries and other Yukthi tooling — keep expression syntax FreeMarker-compatible.
