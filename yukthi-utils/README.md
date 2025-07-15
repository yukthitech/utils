### `com.yukthitech.utils`

This package contains core utility classes.

#### Classes

*   **`ArrayIterator`**: An iterator wrapper over an array. The array can be of a primitive or complex type.
*   **`BitHelper`**: A helper class to access and manipulate bits.
*   **`CaseInsensitiveComparator`**: A comparator for case-insensitive string comparison.
*   **`CommonUtils`**: Provides common utility methods related to objects and classes.
*   **`ConvertUtils`**: A utility class to convert data from one type to another.
*   **`CryptoUtils`**: Provides utilities for encryption and decryption.
*   **`DataConverter`**: An interface for converting data from one format to another.
*   **`DateUtil`**: Contains utility methods for date manipulation.
*   **`Encryptor`**: A simple utility to encrypt a given string with a key.
*   **`ExecutionUtils`**: Provides utilities related to execution.
*   **`ExitException`**: An exception used to indicate the exit of an application.
*   **`FileUtils`**: Contains file-related utility methods.
*   **`IFormatter`**: An interface for formatting an input object into a string.
*   **`LruMap`**: A simple LinkedHashMap-based LRU map.
*   **`MessageFormatter`**: A utility to format strings in a log4j 2 style.
*   **`ObjectLockManager`**: A utility class that helps in locking objects until they are released.
*   **`ObjectWrapper`**: A simple wrapper over a value.
*   **`OnDemand`**: Represents objects that get populated only on demand.
*   **`PatternGroupMatcher`**: A matcher that works like a standard regex matcher but with multiple patterns.
*   **`PatternScanner`**: A scanner to extract data from an input using patterns.
*   **`ReflectionUtils`**: Provides reflection-related utility methods.
*   **`RuntimeInterruptedException`**: A runtime exception to wrap `InterruptedException`.
*   **`StringUtils`**: Provides string-related utility methods.
*   **`TimedValue`**: A simple wrapper to hold a value with a timeout.
*   **`ZipUtils`**: Provides ZIP-related utility methods.

### `com.yukthitech.utils.annotations`

This package contains custom annotations used throughout the library.

#### Annotations

*   **`@IgnorePropertyDestination`**: Indicates that the target field property should be ignored during a property copy.
*   **`@Named`**: Used to provide a name or override the default name for a target element.
*   **`@OverrideProperties`**: Used when multiple overrides need to be specified on a single element.
*   **`@OverrideProperty`**: Used to override a property of the main annotation.
*   **`@PropertyAdder`**: Used to define the adder method of a property.
*   **`@PropertyMapping`**: Defines a property mapping to be performed.
*   **`@PropertyMappings`**: Used to group property mappings.
*   **`@RecursiveAnnotationFactory`**: A factory for creating recursive annotations.
*   **`@SuppressRecursiveSearch`**: Used to suppress a recursive annotation search.

### `com.yukthitech.utils.beans`

This package provides utilities for bean manipulation, such as property copying and inspection.

#### Classes

*   **`BeanInfo`**: Caches bean properties and mappings for efficient property copying.
*   **`BeanInfoFactory`**: A factory for creating `BeanInfo` instances.
*   **`BeanProperty`**: Represents a property of a bean.
*   **`BeanPropertyInfo`**: Contains information about a bean's properties.
*   **`BeanPropertyInfoFactory`**: A factory and cache for `BeanPropertyInfo` instances.
*   **`MappingInfo`**: Contains information about custom mappings between beans.
*   **`NestedProperty`**: Represents a nested property of a bean (e.g., `user.address.city`).
*   **`PropertyInfo`**: Contains information about a single property.
*   **`PropertyMapper`**: Maps properties from a source bean to a destination bean, including nested properties.

### `com.yukthitech.utils.cli`

This package contains classes for parsing command-line arguments.

#### Classes

*   **`CliArgument`**: An annotation used to mark a field as a command-line argument.
*   **`CommandLineOptions`**: Represents and processes command-line options.
*   **`MissingArgumentException`**: An exception thrown when a mandatory argument is missing.
*   **`OptionsFactory`**: A factory for creating `CommandLineOptions` instances.

### `com.yukthitech.utils.doc`

This package contains classes for generating documentation from source code annotations.

#### Classes

*   **`BaseDoc`**: A base class for documentation information classes.
*   **`ClassDoc`**: Represents the documentation for a class.
*   **`Doc`**: An annotation used to provide documentation on different elements.
*   **`DocInfoGenerator`**: Generates documentation information from a specified class.
*   **`FieldDoc`**: Represents the documentation for a field.
*   **`MethodDoc`**: Represents the documentation for a method.
*   **`ParamDoc`**: Represents the documentation for a method parameter.

### `com.yukthitech.utils.event`

This package provides a simple event listener management system.

#### Classes

*   **`EventListenerManager`**: A manager for handling event listeners and firing events.

## Common Exceptions

This library provides a set of common exceptions that are similar to the standard Java exceptions (e.g., `IllegalArgumentException`, `IllegalStateException`). These exceptions extend `UtilsException` (for runtime exceptions) or `UtilsCheckedException` (for checked exceptions) and provide a convenient way to include formatted messages with variable arguments.

### Usage

The exceptions in the `com.yukthitech.utils.exceptions` package accept a formatted string and a variable number of arguments. The `{}` placeholders in the message are replaced with the corresponding argument values. If the last argument is a `Throwable` instance, it is treated as the root cause of the exception.

**Example:**

```java
if (value < 0) {
    throw new InvalidArgumentException("Invalid value specified: {}", value);
}
```

This will produce an exception with the message: `Invalid value specified: -10` (if `value` is -10).

**Example with a root cause:**

```java
try {
    // Some operation that throws an exception
} catch (Exception e) {
    throw new InvalidStateException(e, "An error occurred while processing user data for user: {}", userId);
}
```

This will create an `InvalidStateException` with the specified message and the original exception as the root cause.

### Exception Classes

*   **`InvalidArgumentException`**: Thrown to indicate that a method has been passed an illegal or inappropriate argument.
*   **`InvalidConfigurationException`**: Thrown to indicate that the application has an invalid configuration.
*   **`InvalidStateException`**: Thrown to signal that a method has been invoked at an illegal or inappropriate time.
*   **`NullValueException`**: Thrown when a `null` value is encountered where an object is required.
*   **`UnsupportedOperationException`**: Thrown to indicate that the requested operation is not supported.
*   **`UtilsCheckedException`**: The base class for checked exceptions in this library.
*   **`UtilsException`**: The base class for runtime exceptions in this library.

## Rest Client

`com.yukthitech.utils.rest` package provides a simple and intuitive REST client (`RestClient`) to interact with RESTful APIs. It simplifies common HTTP operations like GET, POST, PUT, and DELETE, and supports various request and response handling mechanisms.

### Purpose

The `RestClient` is designed to provide a straightforward way to:
*   Invoke REST APIs with different HTTP methods.
*   Handle request bodies in various formats (JSON, form data, multipart).
*   Manage path variables and URL parameters.
*   Process responses, including JSON parsing into custom objects or lists, and handling raw string or binary content.
*   Provide clear success/failure status based on HTTP status codes.

### Key Classes

*   **`RestClient`**: The main class for sending HTTP requests.
*   **`RestRequest`**: Base class for all request types (e.g., `GetRestRequest`, `PostRestRequest`).
*   **`RestResult`**: Encapsulates the response from a REST call, including status code, success status, and parsed value.
*   **`IRestResponseHandler`**: Interface for custom response processing.

### Usage Examples

#### 1. Basic GET Request

To fetch data from a REST endpoint:

```java
// Initialize RestClient with the base URL
RestClient restClient = new RestClient("https://jsonplaceholder.typicode.com");

// Create a GET request for a specific resource
GetRestRequest getRequest = new GetRestRequest("/posts/1");

// Invoke the request and parse the JSON response into a custom POJO (e.g., Post.class)
RestResult<Post> result = restClient.invokeJsonRequest(getRequest, Post.class);

// Check for success and access the data
if (result.isSuccess()) {
    Post post = result.getValue();
    System.out.println("Fetched Post Title: " + post.getTitle());
} else {
    System.err.println("Failed to fetch post. Status: " + result.getStatusCode());
}
```

#### 2. POST Request with JSON Body

To create a new resource:

```java
// Assuming 'Post' is a simple POJO with getters/setters
Post newPost = new Post();
newPost.setUserId(1);
newPost.setTitle("My New Post");
newPost.setBody("This is the content of my new post.");

// Create a POST request
PostRestRequest postRequest = new PostRestRequest("/posts");

// Set the request body as JSON from the POJO
postRequest.setJsonBody(newPost);

// Invoke the request
RestResult<Post> result = restClient.invokeJsonRequest(postRequest, Post.class);

if (result.isSuccess()) {
    Post createdPost = result.getValue();
    System.out.println("Created Post ID: " + createdPost.getId());
} else {
    System.err.println("Failed to create post. Status: " + result.getStatusCode());
}
```

#### 3. PUT Request with Path Variables and JSON Body

To update an existing resource:

```java
// Create a PUT request with a path variable
PutRestRequest putRequest = new PutRestRequest("/posts/{id}");
putRequest.addPathVariable("id", "1"); // Assuming we are updating post with ID 1

// Prepare update data (e.g., using a Map or another POJO)
Map<String, Object> updateData = new HashMap<>();
updateData.put("title", "Updated Title");
updateData.put("body", "This post has been updated.");

// Set the request body as JSON
putRequest.setJsonBody(updateData);

RestResult<Post> result = restClient.invokeJsonRequest(putRequest, Post.class);

if (result.isSuccess()) {
    Post updatedPost = result.getValue();
    System.out.println("Updated Post Title: " + updatedPost.getTitle());
} else {
    System.err.println("Failed to update post. Status: " + result.getStatusCode());
}
```

#### 4. DELETE Request

To delete a resource:

```java
// Create a DELETE request with a path variable
DeleteRestRequest deleteRequest = new DeleteRestRequest("/posts/{id}");
deleteRequest.addPathVariable("id", "1"); // Deleting post with ID 1

// Invoke the request (response might be empty or a simple string)
RestResult<String> result = restClient.invokeRequest(deleteRequest);

if (result.isSuccess()) {
    System.out.println("Post deleted successfully.");
} else {
    System.err.println("Failed to delete post. Status: " + result.getStatusCode());
}
```

#### 5. GET Request with URL Parameters and List Response

To fetch a list of resources filtered by parameters:

```java
// Create a GET request with a URL parameter
GetRestRequest getRequest = new GetRestRequest("/posts");
getRequest.addParam("userId", "1"); // Get all posts by user ID 1

// Invoke the request and parse the JSON response into a List of custom POJOs
RestResult<List<Post>> result = restClient.invokeJsonRequestForList(getRequest, List.class, Post.class);

if (result.isSuccess()) {
    List<Post> posts = result.getValue();
    System.out.println("Found " + posts.size() + " posts for user 1.");
    posts.forEach(post -> System.out.println("- " + post.getTitle()));
} else {
    System.err.println("Failed to fetch posts. Status: " + result.getStatusCode());
}
```

#### 6. POST Request with Form Data

To send data as `application/x-www-form-urlencoded`:

```java
// Create a POST request
PostRestRequest request = new PostRestRequest("https://httpbin.org/post");

// Add form fields
request.addFormField("param1", "value1");
request.addFormField("param2", "value2");

// Add a JSON field as part of the form data
request.addJsonFormField("user", CommonUtils.toMap("name", "testUser", "id", 123));

// Invoke the request (httpbin.org echoes back the request details)
RestResult<String> result = restClient.invokeRequest(request);

if (result.isSuccess()) {
    System.out.println("Form Post Response: " + result.getValue());
} else {
    System.err.println("Failed to send form data. Status: " + result.getStatusCode());
}
```

#### 7. POST Request with Attachments (File Upload)

To upload files using `multipart/form-data`:

```java
// Create dummy files for attachment
File file1 = new File("path/to/your/file1.txt"); // Replace with actual file paths
File file2 = new File("path/to/your/file2.txt");

// Create a POST request
PostRestRequest request = new PostRestRequest("https://httpbin.org/post");

// Add attachments
request.addAttachment("document1", file1); // Auto-detects content type
request.addAttachment("image", file2, "image/png"); // Specify content type

RestResult<String> result = restClient.invokeRequest(request);

if (result.isSuccess()) {
    System.out.println("Attachment Post Response: " + result.getValue());
} else {
    System.err.println("Failed to upload attachments. Status: " + result.getStatusCode());
}
```

#### 8. Custom Response Handling

When you need to process the raw response content (e.g., binary data like images):

```java
// Create a GET request for an image
GetRestRequest getRequest = new GetRestRequest("https://httpbin.org/image/png");

// Invoke with a custom response handler to get raw bytes
RestResult<byte[]> byteResult = restClient.invokeRequest(getRequest, (response) -> {
    int statusCode = response.getCode();
    byte[] data = null;
    
    if(statusCode >= 200 && statusCode <= 299)
    {
        HttpEntity entity = response.getEntity();
        
        try
        {
            data = entity != null? EntityUtils.toByteArray(entity): null;
        }catch(Exception ex)
        {
            data = null;
        }
     }
     
     return new RestResult<byte[]>(data, statusCode, response);
});

if (byteResult.isSuccess()) {
    System.out.println("Downloaded image with " + byteResult.getValue().length + " bytes.");
    // You can now save these bytes to a file or process them further
} else {
    System.err.println("Failed to download image. Status: " + byteResult.getStatusCode());
}
```

This `RestClient` provides a flexible and powerful way to interact with various RESTful services.
