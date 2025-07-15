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

### `com.yukthitech.utils.expr`

This package provides a simple expression language evaluator.

#### Classes

*   **`Expression`**: Represents a parsed expression.
*   **`ExpressionEvaluator`**: Parses an expression string into an `Expression` object.
*   **`ExpressionRegistry`**: A registry for maintaining operators and functions.
*   **`FunctionExpr`**: Represents a function invocation in an expression.
*   **`FunctionInfo`**: An annotation used to mark a method as an expression function.
*   **`IExpressionPart`**: An interface representing a part of an expression.
*   **`IFunction`**: An interface representing a function that can be used in expressions.
*   **`IOperator`**: An interface representing an evaluatable operator.
*   **`IVariableTypeProvider`**: An interface for providing the type of a variable.
*   **`IVariableValueProvider`**: An interface for providing the value of a variable.
*   **`InvalidTypeException`**: Thrown when an invalid parameter type is encountered in an expression.
*   **`Literal`**: Represents a literal value in an expression.
*   **`OperatorExpr`**: Represents an operator-based expression part with operands.
*   **`ParseException`**: Thrown when an error occurs while parsing an expression.
*   **`ParseState`**: Used to maintain the state of expression parsing.
*   **`RegistryFactory`**: A factory to add default operators and functions to a registry.
*   **`SimpleJavaFunction`**: A simple implementation of the `IFunction` interface.
*   **`SimpleOperator`**: A simple abstract implementation of an operator.
*   **`Token`**: Represents a token in an expression being parsed.
*   **`TokenType`**: An enum representing the type of an expression token.
*   **`Variable`**: Represents a variable in an expression.

### `com.yukthitech.utils.pool`

This package provides a simple object pooling mechanism.

#### Classes

*   **`ConsolidatedJobManager`**: A manager for scheduling and rescheduling jobs based on a name.
*   **`ObjectPool`**: Maintains a pool of objects.

### `com.yukthitech.utils.rest`

This package provides a simple REST client.

#### Classes

*   **`DeleteRestRequest`**: Represents a REST DELETE request.
*   **`FileInfo`**: Contains information about a file to be uploaded.
*   **`GetRestRequest`**: Represents a REST GET request.
*   **`HttpClientFactory`**: A factory for creating `CloseableHttpClient` instances.
*   **`HttpResponse`**: A wrapper over the `ClassicHttpResponse` object.
*   **`IRestClientListener`**: A listener for the REST client to support callbacks.
*   **`IRestResponseHandler`**: Used to specify custom response handling.
*   **`PatchRestRequest`**: Represents a REST PATCH request.
*   **`PostRestRequest`**: Represents a REST POST request.
*   **`PutRestRequest`**: Represents a REST PUT request.
*   **`ResponseHandlerAdapter`**: An adapter class for `IRestResponseHandler`.
*   **`RestClient`**: A simple REST client.
*   **`RestInvocationException`**: Thrown when an error occurs during a REST invocation.
*   **`RestRequest`**: The base class for all REST requests.
*   **`RestRequestWithBody`**: The base class for REST requests that have a body.
*   **`RestResult`**: Represents the result of a REST request.

### `com.yukthitech.utils.test`

This package contains utility classes for testing.

#### Classes

*   **`BeanConstructor`**: A utility class for constructing beans for testing.
*   **`ITestGroups`**: An interface containing test group constants.
*   **`TestUtil`**: A utility class for testing.

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