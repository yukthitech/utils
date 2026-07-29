# Yukthi Utils — AI Reference

> **Audience:** Cursor / AI agents reusing common Yukthi utilities.  
> **Artifact:** `com.yukthitech:yukthi-utils`  
> **Version:** `1.3.12-SNAPSHOT` · **License:** Apache 2.0

For the REST client, see the detailed guide: **[RestClient.md](./RestClient.md)**.

---

## 1. What this module is

Reusable Java utility library used across Yukthi projects: collections/strings/beans, crypto, CLI parsing, expression evaluation, object pooling, annotations, and an Apache HttpClient 5–based REST client.

```xml
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-utils</artifactId>
    <version>1.3.12-SNAPSHOT</version>
</dependency>
```

**Note:** Some features depend on **provided** scope libs (`httpclient5`, `jackson-databind`, `commons-cli`, BouncyCastle, `commons-io`). Add those to your project when using REST / CLI / crypto / zip helpers.

---

## 2. Package overview

| Package | Role |
|---------|------|
| `com.yukthitech.utils` | Core helpers |
| `com.yukthitech.utils.rest` | REST client ([RestClient.md](./RestClient.md)) |
| `com.yukthitech.utils.exceptions` | `{}`-formatted exceptions |
| `com.yukthitech.utils.cli` | Annotate beans → parse CLI args |
| `com.yukthitech.utils.expr` | Expression parser / evaluator |
| `com.yukthitech.utils.annotations` | Meta / recursive annotations |
| `com.yukthitech.utils.doc` | `@Doc` reflection docs |
| `com.yukthitech.utils.event` | Proxy-based listener manager |
| `com.yukthitech.utils.pool` | Object pool + consolidated jobs |
| `com.yukthitech.utils.test` | Test helpers / TestNG group names |

---

## 3. Core utilities (`com.yukthitech.utils`)

| Class | Description |
|-------|-------------|
| **CommonUtils** | Misc helpers: `toMap` / `toSet`, expression substitution in strings, collection/map equality, wrapper/primitive type checks, field access, root-cause messages. |
| **PropertyAccessor** | Nested bean/map/list property access with paths, indexes, and conditions. Used heavily for dynamic property paths. |
| **ReflectionUtils** | Reflection helpers for fields, methods, and types. |
| **ConvertUtils** / **DataConverter** | Strict Apache BeanUtils-style conversion that throws on invalid values instead of silent defaults. |
| **DateUtil** | Date/time arithmetic and default formatters (e.g. `dd/MM/yyyy`). |
| **StringUtils** | Random alphanumeric strings, hashing helpers, whitespace utilities. |
| **MessageFormatter** | Formats messages with `{}` placeholders (also used by the exception hierarchy). |
| **IFormatter** | Callback for formatting values (used with `CommonUtils.replaceExpressions`). |
| **CryptoUtils** | Simple string encrypt/decrypt utilities. |
| **Encryptor** | RSA/keystore-based encrypt/decrypt (BouncyCastle; needs a keystore). |
| **ZipUtils** | Zip/unzip files and gzip byte arrays (also used by REST zip body). |
| **FileUtils** | Extra file helpers beyond commons-io. |
| **ArrayIterator** | `Iterator` / `Iterable` over any array (primitive or object). |
| **BitHelper** | Set / unset / test flag bits in an `int`. |
| **CaseInsensitiveComparator** | Case-insensitive `Comparator<String>`. |
| **LruMap** | Least-recently-used map with bounded size. |
| **ObjectWrapper** | Mutable single-value holder (handy for lambda out-params). |
| **OnDemand** | Lazily populated value; run alternate logic if absent/present. |
| **TimedValue** | Cached supplier result that refreshes after a TTL. |
| **ObjectLockManager** | Fine-grained locking keyed by arbitrary objects. |
| **ExecutionUtils** | Helpers for executing callables/runnables with consistent error handling. |
| **PatternScanner** | Scan input and extract segments matching regex patterns. |
| **PatternGroupMatcher** | Multi-pattern matcher: picks the earliest match among several patterns. |
| **ExitException** | Signal application exit with a code/message. |
| **RuntimeInterruptedException** | Unchecked wrapper for `InterruptedException`. |

---

## 4. Exceptions (`com.yukthitech.utils.exceptions`)

All support `{}` placeholders; the last argument may be a `Throwable` cause.

| Class | When to use |
|-------|-------------|
| **UtilsException** | Base runtime exception |
| **UtilsCheckedException** | Base checked exception |
| **InvalidArgumentException** | Bad / illegal argument |
| **InvalidStateException** | Illegal timing or object state |
| **InvalidConfigurationException** | Bad configuration |
| **NullValueException** | Unexpected null |
| **UnsupportedOperationException** | Unsupported operation (library variant) |

```java
throw new InvalidArgumentException("Expected id > 0 but got: {}", id);
throw new UtilsException("Failed to load {}", path, cause);
```

---

## 5. CLI (`com.yukthitech.utils.cli`)

| Class | Description |
|-------|-------------|
| **@CliArgument** | Maps a bean field to a CLI option (`name`, `longName`, `description`, `required`). |
| **OptionsFactory** | Scans annotated types → `CommandLineOptions`. |
| **CommandLineOptions** | Parses `argv` into annotated beans (commons-cli). |
| **MissingArgumentException** | Required CLI arg missing. |

```java
public class AppArgs {
    @CliArgument(name = "u", longName = "url", required = true, description = "Base URL")
    private String url;
}

CommandLineOptions opts = OptionsFactory.buildOptions(AppArgs.class);
AppArgs args = opts.parseBean(AppArgs.class, argv);
```

---

## 6. Expressions (`com.yukthitech.utils.expr`)

| Class | Description |
|-------|-------------|
| **ExpressionEvaluator** | Parses expression strings (shunting-yard) into `Expression`. |
| **Expression** | Evaluable expression tree. |
| **ExpressionRegistry** | Registers operators and functions. |
| **RegistryFactory** / **DefaultFunctions** | Default op/function set (IF, NOT, math/string helpers, …). |
| **IFunction** / **IOperator** | Pluggable function/operator contracts. |
| **SimpleJavaFunction** / **SimpleOperator** | Wrappers around Java methods/ops. |
| **IVariableValueProvider** / **IVariableTypeProvider** | Supply variable values/types at eval time. |

Use when you need a small expression language **outside** FreeMarker (config formulas, rules, etc.).

---

## 7. Annotations / doc / events / pool / test

### Annotations (`…utils.annotations`)

| Class | Description |
|-------|-------------|
| **@Named** | Provide/override a name on types/fields/methods/params. |
| **@OverrideProperty** / **@OverrideProperties** | Override properties of another annotation (meta-annotation). |
| **RecursiveAnnotationFactory** | Resolves annotations recursively with overrides via proxies. |
| **@SuppressRecursiveSearch** | Stop recursive search for listed annotation types. |

### Doc (`…utils.doc`)

| Class | Description |
|-------|-------------|
| **@Doc** | Runtime documentation on types/fields/methods/params. |
| **DocInfoGenerator** | Reflects `@Doc` into `ClassDoc` / `MethodDoc` / `FieldDoc` / `ParamDoc`. |

### Event (`…utils.event`)

| Class | Description |
|-------|-------------|
| **EventListenerManager** | Register listeners; invoke via dynamic proxy; optional filter and async executor. |

### Pool (`…utils.pool`)

| Class | Description |
|-------|-------------|
| **ObjectPool** | Bounded pool of typed objects with lock/condition wait. |
| **ConsolidatedJobManager** | Deduplicate named delayed jobs so repeated schedules collapse to one run. |

### Test (`…utils.test`)

| Class | Description |
|-------|-------------|
| **TestUtil** | Random beans, resource files, property/equality checks. |
| **@BeanConstructor** | Marks a constructor and named args for test bean construction. |
| **ITestGroups** | Constants `UNIT_TESTS` / `FUNCTIONAL_TESTS` for TestNG groups. |

---

## 8. REST client

Full documentation: **[RestClient.md](./RestClient.md)**.

Quick start:

```java
RestClient client = new RestClient("https://api.example.com");
GetRestRequest req = new GetRestRequest("/users/{id}");
req.addPathVariable("id", "42");
RestResult<User> result = client.invokeJsonRequest(req, User.class);
if (result.isSuccess()) {
    User user = result.getValue();
}
client.close();
```

---

## 9. Guidance for AI agents

1. Prefer **CommonUtils** / **PropertyAccessor** / **ConvertUtils** before inventing ad-hoc helpers.
2. Throw **UtilsException** hierarchy with `{}` placeholders for consistent messages.
3. For HTTP, use **`com.yukthitech.utils.rest`** — do not wrap HttpClient from scratch in Yukthi-based apps.
4. Remember **provided** dependencies: add HttpClient 5 + Jackson when using RestClient.
5. For FreeMarker templating, use sibling module `yukthi-free-marker`, not this jar.
6. For XML bean mapping, use sibling module `yukthi-xml-mapper`.
