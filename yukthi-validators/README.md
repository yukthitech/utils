# Yukthi Validators

This project provides a set of custom validation annotations that extend the functionality of Jakarta/Hibernate validators. These annotations cover a range of validation scenarios, including cross-field validations.

## Annotations

Here is a list of the custom validation annotations provided by this library:

### @EnableSelfValidation
- **Purpose:** Enables a POJO class to self-validate.
- **Attributes:**
    - `message`: The error message to be used if the validation fails. Defaults to "Custom Error".

### @FutureOrToday
- **Purpose:** Validates that a `Date` field is either today or a future date.
- **Attributes:**
    - `message`: The error message to be used if the validation fails. Defaults to "Date value should be either future date or today".
- **Example:**
    ```java
    public class MyBean {
        @FutureOrToday
        private Date eventDate;
    }
    ```

### @GreaterThan
- **Purpose:** Compares the annotated field with another field to ensure its value is greater. This can be used on `Number` or `Date` fields.
- **Attributes:**
    - `field`: The name of the field to compare with.
    - `message`: The error message to be used if the validation fails. Defaults to "Value should be greater than the {field}'s value".
- **Example:**
    ```java
    public class MyBean {
        @GreaterThan(field = "startDate")
        private Date endDate;
        private Date startDate;
    }
    ```

### @GreaterThanEquals
- **Purpose:** Compares the annotated field with another field to ensure its value is greater than or equal. This can be used on `Number` or `Date` fields.
- **Attributes:
    - `field`: The name of the field to compare with.
    - `message`: The error message to be used if the validation fails. Defaults to "Value should be greater or equal to {field}'s value".
- **Example:**
    ```java
    public class MyBean {
        @GreaterThanEquals(field = "minAge")
        private int age;
        private int minAge;
    }
    ```

### @LessThan
- **Purpose:** Compares the annotated field with another field to ensure its value is less than. This can be used on `Number` or `Date` fields.
- **Attributes:**
    - `field`: The name of the field to compare with.
    - `message`: The error message to be used if the validation fails. Defaults to "Value should be lesser than {field}'s value".
- **Example:**
    ```java
    public class MyBean {
        @LessThan(field = "maxAmount")
        private double amount;
        private double maxAmount;
    }
    ```

### @LessThanEquals
- **Purpose:** Compares the annotated field with another field to ensure its value is less than or equal. This can be used on `Number` or `Date` fields.
- **Attributes:**
    - `field`: The name of the field to compare with.
    - `message`: The error message to be used if the validation fails. Defaults to "Value should be lesser or equal to {field}'s value".
- **Example:**
    ```java
    public class MyBean {
        @LessThanEquals(field = "endDate")
        private Date startDate;
        private Date endDate;
    }
    ```

### @MandatoryOption
- **Purpose:** Ensures that at least one field among the specified fields has a value.
- **Attributes:**
    - `fields`: An array of field names to check.
    - `message`: The error message to be used if the validation fails. Defaults to "Value should be provided for at least one of the mandatory field".
- **Example:**
    ```java
    public class MyBean {
        @MandatoryOption(fields = {"email", "phone"})
        private String contact;
        private String email;
        private String phone;
    }
    ```

### @MatchWith
- **Purpose:** Ensures that the annotated field's value matches the value of another field.
- **Attributes:**
    - `field`: The name of the field to match with.
    - `message`: The error message to be used if the validation fails. Defaults to "Value should match with {field}'s value".
- **Example:**
    ```java
    public class MyBean {
        private String password;
        @MatchWith(field = "password")
        private String confirmPassword;
    }
    ```

### @MaxLen
- **Purpose:** Validates that the length of a `String` field is less than or equal to the specified value.
- **Attributes:**
    - `value`: The maximum allowed length.
    - `message`: The error message to be used if the validation fails. Defaults to "Value length can be maximum of {value}.".
- **Example:**
    ```java
    public class MyBean {
        @MaxLen(10)
        private String username;
    }
    ```

### @MinLen
- **Purpose:** Validates that the length of a `String` field is greater than or equal to the specified value.
- **Attributes:**
    - `value`: The minimum allowed length.
    - `message`: The error message to be used if the validation fails. Defaults to "Value length should be minimum of {value}.".
- **Example:**
    ```java
    public class MyBean {
        @MinLen(5)
        private String password;
    }
    ```

### @Mispattern
- **Purpose:** Ensures that the annotated field's value does not match the specified regular expression.
- **Attributes:
    - `regexp`: The regular expression to not match.
    - `message`: The error message to be used if the validation fails. Defaults to "Value is matching with undesired pattern".
- **Example:**
    ```java
    public class MyBean {
        @Mispattern(regexp = ".*\\s.*", message = "Username must not contain whitespace")
        private String username;
    }
    ```

### @NotEmpty
- **Purpose:** Ensures that a `String` or `Collection` field is not empty.
- **Attributes:
    - `message`: The error message to be used if the validation fails. Defaults to "Value can not be empty".
- **Example:**
    ```java
    public class MyBean {
        @NotEmpty
        private String name;
        @NotEmpty
        private List<String> roles;
    }
    ```

### @PastOrToday
- **Purpose:** Validates that a `Date` field is either a past date or today.
- **Attributes:
    - `message`: The error message to be used if the validation fails. Defaults to "Date value should be either past date or today".
- **Example:**
    ```java
    public class MyBean {
        @PastOrToday
        private Date dateOfBirth;
    }
    ```

### @Pattern
- **Purpose:** Ensures that the annotated field's value matches the specified regular expression.
- **Attributes:
    - `regexp`: The regular expression to match.
    - `message`: The error message to be used if the validation fails. Defaults to "Value not matching with required pattern".
- **Example:
    ```java
    public class MyBean {
        @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        private String email;
    }
    ```

### @Required
- **Purpose:** Ensures that a field has a value. For `String` fields, it checks for non-empty and non-null. For `Number` fields, it checks for a non-zero value.
- **Attributes:
    - `message`: The error message to be used if the validation fails. Defaults to "Value is mandatory".
- **Example:**
    ```java
    public class MyBean {
        @Required
        private String name;
        @Required
        private int age;
    }
    ```