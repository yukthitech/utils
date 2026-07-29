# RestClient — AI Reference

> **Module:** `com.yukthitech:yukthi-utils`  
> **Package:** `com.yukthitech.utils.rest`  
> **Primary class:** `RestClient`  
> **Stack:** Apache HttpClient **5** + Jackson `ObjectMapper`

This document is the detailed guide for calling HTTP/REST APIs from Java using Yukthi utils. For the rest of the utils catalog, see [README.md](./README.md).

---

## 1. Overview

`RestClient` is a thin, fluent wrapper over Apache HttpClient 5 that:

1. Prepends a **base URL** to relative request paths
2. Builds requests from typed `RestRequest` subclasses (GET/POST/PUT/PATCH/DELETE)
3. Executes them and returns a `RestResult<T>` (status, headers, body/value)
4. Optionally parses JSON responses into POJOs / collections / parametric types via Jackson

```
RestClient
  └─ HttpClientFactory → CloseableHttpClient (pooled; optional proxy / timeouts / retry)
       └─ RestRequest / RestRequestWithBody → HttpUriRequestBase
            └─ handler → RestResult<T>
                 └─ optional IRestClientListener (prerequest / postrequest)
```

**Auth:** no dedicated OAuth/Basic helpers — set headers yourself (`Authorization: Bearer …`).  
**SSL:** `HttpClientFactory` uses trust-all certificates + disabled hostname verification (convenient for tests/dev; be aware in production).

**Provided dependencies you must add to the consuming project:**

- `org.apache.httpcomponents.client5:httpclient5`
- `com.fasterxml.jackson.core:jackson-databind`

---

## 2. Creating a client

```java
// Basic
RestClient client = new RestClient("https://api.example.com/v1");

// With proxy host:port
RestClient client = new RestClient("https://api.example.com", "proxy.local:8080");

// With timeouts + retry
RestClientConfig config = new RestClientConfig()
    .setConnectionTimeOutMillis(5_000)
    .setResponseTimeOutMillis(30_000)
    .setConnectionRequestTimeOutMillis(5_000)
    .setMaxRetryAttempts(3)
    .setRetryDurationSec(2);

RestClient client = new RestClient("https://api.example.com", null, config);
```

| Constructor | Notes |
|-------------|-------|
| `RestClient(String baseUrl)` | Trailing `/` on base URL is stripped |
| `RestClient(String baseUrl, String proxyHostPort)` | Proxy as `host:port` |
| `RestClient(String baseUrl, String proxyHostPort, RestClientConfig config)` | Full config |

Also: system property `http.proxy` (`host:port`) is honored by `HttpClientFactory`.

Always call `client.close()` when finished (closes the underlying `CloseableHttpClient`).

### Client accessors

| Method | Purpose |
|--------|---------|
| `getBaseUrl()` | Configured base URL |
| `getObjectMapper()` / `setObjectMapper(...)` | JSON parse/serialize. **Default is a shared static mapper** — mutating it affects all clients that still use the default |
| `getRestClientListener()` / `setRestClientListener(...)` | Pre/post hooks |
| `close()` | Release HTTP client |

---

## 3. Request types

| Class | HTTP method | Notes |
|-------|-------------|-------|
| `GetRestRequest` | GET | Also `addBeanParameters(bean)` → query params from bean properties |
| `PostRestRequest` | POST | Body / form / multipart |
| `PutRestRequest` | PUT | Body / form / multipart |
| `PatchRestRequest` | PATCH | Body / form / multipart |
| `DeleteRestRequest` | DELETE | Can also carry body/form/multipart |

All except the conceptual “GET-only” usage extend `RestRequestWithBody` (including GET), so body APIs exist on GET but are rarely used.

### Common request API (`RestRequest`)

```java
GetRestRequest req = new GetRestRequest("/users/{id}");
req.addPathVariable("id", "42");          // replaces {id}
req.addParam("include", "roles");         // ?include=roles
req.addJsonParam("filter", filterObj);    // object → JSON string query param
req.addHeader("Authorization", "Bearer " + token);
req.setContentType("application/json");
req.setSecured(true);                     // suppress request body in logs
req.setRestTimeoutConfig(config);         // per-request timeout (body path)
```

| Capability | API | Notes |
|------------|-----|-------|
| Path variables | `addPathVariable(name, value)` | Missing `{name}` → `NullPointerException` |
| Query params | `addParam`, `addJsonParam` | |
| Headers | `addHeader` | Same header name can be added multiple times |
| Relative vs absolute URI | path `/…` or full `https://…` | Absolute URIs skip base URL |
| Secured logging | `setSecured(true)` | Hides request details in logs |

---

## 4. Request body modes (`RestRequestWithBody`)

**Mutually exclusive** — mixing throws `IllegalStateException`:

1. **Raw / JSON body** — `setBody(String)`, `setJsonBody(Object)`
2. **Form urlencoded** — `addFormField`, `addJsonFormField`
3. **Multipart** — attachments / text / JSON / binary parts

```java
// JSON body
PostRestRequest post = new PostRestRequest("/posts");
post.setJsonBody(new Post("title", "body"));  // Content-Type: application/json

// Form
PostRestRequest form = new PostRestRequest("/submit");
form.addFormField("name", "Ada");
form.addJsonFormField("meta", Map.of("id", 1));

// Multipart
PostRestRequest mp = new PostRestRequest("/upload");
mp.addTextPart("notes", "hello");
mp.addJsonPart("meta", Map.of("id", 1));
mp.addAttachment("file", file, "text/plain");           // field, File, contentType
mp.addAttachment("file", "report.txt", file, "text/plain");
mp.addBinaryPart("bin", "data.bin", bytes, "application/octet-stream");

// Optional gzip of body
post.setZipBodyEnabled(true);  // Content-Encoding: gzip
```

| Feature | API |
|---------|-----|
| Charset | `setContentCharset(...)` |
| Own ObjectMapper for serialization | `setObjectMapper(...)` on the request |
| Zip body | `setZipBodyEnabled(true)` |

---

## 5. Invoking requests (`RestClient` methods)

### JSON → typed value

```java
RestResult<User> result = client.invokeJsonRequest(req, User.class);

RestResult<List<User>> list =
    client.invokeJsonRequestForList(req, List.class, User.class);

// Wrapper<T> style parametric type
RestResult<ApiResponse> wrapped =
    client.invokeJsonRequest(req, ApiResponse.class, User.class);

// Arbitrary Jackson JavaType
JavaType mapType = TypeFactory.defaultInstance()
    .constructMapType(Map.class, String.class, Object.class);
RestResult<Map<String, Object>> mapResult =
    client.invokeJsonRequest(req, mapType);
```

### Raw string body

```java
RestResult<String> result = client.invokeRequest(req);
```

### Custom response handler (binary, streaming, etc.)

```java
RestResult<byte[]> bytes = client.invokeRequest(req, response -> {
    int code = response.getCode();
    byte[] data = null;
    if (code >= 200 && code <= 299) {
        data = response.getBodyAsByteArray();
    }
    return new RestResult<>(data, code, response);
});
```

`IRestResponseHandler<T>` receives the library `HttpResponse` wrapper (status, headers, body as string/bytes/stream).

### Failure semantics

| Situation | Behavior |
|-----------|----------|
| HTTP non-2xx | Returned in `RestResult`; `isSuccess()` is **false**. Does **not** throw. |
| JSON parse failure | `value` = `null`, `parseError` set; status still returned. Does **not** throw. |
| Transport / I/O / protocol error | Throws **`RestInvocationException`** |

`RestResult.isSuccess()` → HTTP status in **200–299**.

---

## 6. `RestResult<T>`

| Method | Meaning |
|--------|---------|
| `getValue()` | Parsed body (POJO / string / bytes / …) |
| `getStatusCode()` | HTTP status |
| `isSuccess()` | `200 <= status <= 299` |
| `getStatusMessage()` | Reason phrase |
| `getHeaders()` / `getHeaderValue` / `getHeaderValues` | Response headers |
| `getParseError()` | JSON parse failure message, if any |
| `getHttpResponse()` | Transient `HttpResponse` wrapper |

Always check `isSuccess()` (and optionally `getParseError()`) before using `getValue()`.

---

## 7. Listeners

```java
client.setRestClientListener(new IRestClientListener() {
    @Override
    public void prerequest(RestRequest<?> request) {
        // mutate headers, logging, metrics
    }

    @Override
    public void postrequest(RestRequest<?> request, RestResult<?> result) {
        // metrics, error logging
    }
});
```

Order: `prerequest` → HTTP execute → (JSON parse if applicable) → `postrequest` → return to caller.

---

## 8. `RestClientConfig` (timeouts & retry)

Fluent setters; used at **client construction** (and optionally per-request via `setRestTimeoutConfig`).

| Setting | Meaning |
|---------|---------|
| `connectionTimeOutMillis` | Time to establish connection |
| `responseTimeOutMillis` | Time waiting for data |
| `connectionRequestTimeOutMillis` | Time to obtain connection from pool |
| `maxRetryAttempts` | Retry count |
| `retryDurationSec` | Delay between retries (default **2**) |
| `retryExceptions` | Exception types that trigger retry |
| `retryFilter` | `BiFunction<HttpRequest, Exception, Boolean>` custom filter |

Retry treats methods as idempotent (`handleAsIdempotent` → true). Prefer retries carefully for non-idempotent POSTs.

---

## 9. Related classes

| Class | Role |
|-------|------|
| `HttpClientFactory` | Singleton factory; connection pool (max 500 total, 5/route); trust-all SSL; shared connection manager; `reset()` closes pool |
| `HttpResponse` | Isolates callers from Apache types |
| `ResponseHandlerAdapter` | Bridges Apache handler → `IRestResponseHandler` |
| `FileInfo` | Name + `File` + content type for multipart |
| `RestInvocationException` | Unchecked transport failure |

---

## 10. End-to-end usage examples

### 10.1 GET → POJO

```java
RestClient client = new RestClient("https://jsonplaceholder.typicode.com");

GetRestRequest get = new GetRestRequest("/posts/{id}");
get.addPathVariable("id", "1");

RestResult<Post> result = client.invokeJsonRequest(get, Post.class);
if (result.isSuccess()) {
    Post post = result.getValue();
}
client.close();
```

### 10.2 GET with query params → list

```java
GetRestRequest get = new GetRestRequest("/posts");
get.addParam("userId", "1");

RestResult<List<Post>> result =
    client.invokeJsonRequestForList(get, List.class, Post.class);
```

### 10.3 GET with bean query params

```java
GetRestRequest get = new GetRestRequest("/search");
get.addBeanParameters(searchBean); // properties → query string
```

### 10.4 POST JSON (create)

```java
PostRestRequest post = new PostRestRequest("/posts");
post.setJsonBody(new Post("title", "body", 1));

RestResult<Post> result = client.invokeJsonRequest(post, Post.class);
// often status 201
```

### 10.5 PUT with path variable

```java
PutRestRequest put = new PutRestRequest("/posts/{id}");
put.addPathVariable("id", "1");
put.setJsonBody(Map.of("title", "New Title", "body", "New Body"));

RestResult<Post> result = client.invokeJsonRequest(put, Post.class);
```

### 10.6 PATCH

```java
PatchRestRequest patch = new PatchRestRequest("/posts/{id}");
patch.addPathVariable("id", "1");
patch.setJsonBody(Map.of("title", "Patched"));
RestResult<Post> result = client.invokeJsonRequest(patch, Post.class);
```

### 10.7 DELETE

```java
DeleteRestRequest del = new DeleteRestRequest("/posts/{id}");
del.addPathVariable("id", "1");

RestResult<String> result = client.invokeRequest(del);
// e.g. 204 No Content → value may be null; check isSuccess()
```

### 10.8 Form POST

```java
PostRestRequest request = new PostRestRequest("/post");
request.addFormField("param1", "value1");
request.addJsonFormField("user", Map.of("name", "testUser", "id", 123));

RestResult<String> result = client.invokeRequest(request);
```

### 10.9 Multipart upload

```java
PostRestRequest request = new PostRestRequest("/upload");
request.addAttachment("file1", file1, "text/plain");
request.addAttachment("file2", file2, "text/plain");
request.addTextPart("description", "batch upload");

RestResult<String> result = client.invokeRequest(request);
```

### 10.10 Auth header

```java
GetRestRequest get = new GetRestRequest("/me");
get.addHeader("Authorization", "Bearer " + accessToken);
RestResult<User> me = client.invokeJsonRequest(get, User.class);
```

### 10.11 Handling failure status codes

```java
GetRestRequest get = new GetRestRequest("/status/404");
RestResult<String> result = client.invokeRequest(get);

if (!result.isSuccess()) {
    int code = result.getStatusCode(); // 404
    // handle error body if present: result.getValue()
}
```

### 10.12 Binary download via custom handler

```java
GetRestRequest get = new GetRestRequest("/image/png");

RestResult<byte[]> byteResult = client.invokeRequest(get, response -> {
    int statusCode = response.getCode();
    byte[] data = null;
    if (statusCode >= 200 && statusCode <= 299) {
        data = response.getBodyAsByteArray();
    }
    return new RestResult<>(data, statusCode, response);
});
```

### 10.13 CRUD sketch (create → read → update → delete)

```java
RestClient local = new RestClient("http://localhost:9999/entity");

// CREATE
PostRestRequest create = new PostRestRequest("/posts");
create.setJsonBody(post);
RestResult<Post> created = local.invokeJsonRequest(create, Post.class);

// READ
GetRestRequest read = new GetRestRequest("/posts/{id}");
read.addPathVariable("id", Integer.toString(created.getValue().getId()));
RestResult<Post> loaded = local.invokeJsonRequest(read, Post.class);

// UPDATE
PutRestRequest update = new PutRestRequest("/posts/{id}");
update.addPathVariable("id", Integer.toString(loaded.getValue().getId()));
update.setJsonBody(Map.of("title", "New Title", "body", "New Body"));
local.invokeJsonRequest(update, Post.class);

// DELETE
DeleteRestRequest delete = new DeleteRestRequest("/posts/{id}");
delete.addPathVariable("id", Integer.toString(loaded.getValue().getId()));
local.invokeRequest(delete);

local.close();
```

---

## 11. Guidance for AI agents

1. Always construct requests with the right subclass (`GetRestRequest`, `PostRestRequest`, …).
2. Prefer `invokeJsonRequest` / `invokeJsonRequestForList` for JSON APIs; use `invokeRequest` for non-JSON or empty bodies.
3. Check **`result.isSuccess()`** — non-2xx does not throw.
4. Check **`result.getParseError()`** when JSON was expected but `getValue()` is null.
5. Catch **`RestInvocationException`** for network/protocol failures.
6. Do **not** mix JSON body + form fields + multipart on one request.
7. Attachments require content type: `addAttachment(field, file, contentType)` (content type may be `null` → default binary).
8. If customizing Jackson, call **`setObjectMapper`** on the client (or request) instead of mutating the shared default from `getObjectMapper()` unless intentional.
9. Call **`close()`** when the client lifetime ends.
10. For timeouts/retries, pass `RestClientConfig` into the constructor; do not assume infinite waits.
11. Tests live under `yukthi-utils` (`TestRestClient` + `MockServer`); use them as behavioral reference.
