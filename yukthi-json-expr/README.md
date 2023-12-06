# Json Expression Language (JEL)

JEL is a transformation/templating engine which can transform object data (from multiple sources) into different form based on template. Templates are also json files with support for basic templating units like - conditions, loops, variable, easy access etc.

![Alt text](doc/images/jel.png?raw=true "JEL Engine")

## Replacement Expressions
	
All values in JEL templates can have free-marker expressions in json format and will be processed. But there will be situations where entire value should be replaced with just not string but other data type or complex object. In those places below mentioned replace expressions will come handy.

Using one the prefixes mentioned below, the full key/value can be replaced with the resultant value. The result can be any of the JSON supported type - String, boolean, int, date, map or list. For keys, if expression results in non-string, the result will be converted to string and then used as a key.

* ${\color{blue}@fmarker}$ - Used to inject dynamic values using free marker expression.
* ${\color{blue}@xpath}$ -  Used to fetch value from the current context using xpath. In this case, when xpath matches multiple values, only the first value will be picked.
* ${\color{blue}@xpathMulti}$ - Used to fetch multiple values from the current context using xpath.

> **Map Examples** - Shows how replacement-expressions can be used in map values
>  * { "key2": " ${\color{blue}@fmarker}$: empMap.employeeNames[0]" }
>  * { "bookCost": " ${\color{blue}@xpath}$: /books[name='Sphere']/cost" }
>  * { "bookNames": " ${\color{blue}@xpathMulti}$: /books//name" }

> **List Examples** - Shows how replacement-expressions can be used in list values
>  * { "listWithNoCond": [" ${\color{blue}@fmarker}$: book.cost", 20] }

## Freemarker Expressions
String values (both in keys and values) when dynamic-value-replacer prefixes are not used, they will be considered as free-marker templates. And freemarker expressions within the strings can be used in standard way.

> **Example in Key**
> { "book-${book.name}": {  "desc": "This is a science fiction." } }

> **Example in value**
> { "name": "${name}" }

## Conditions
Different parts of a json can be declared to be included conditionally. That is, the target section will be included only when specified condition is true. This can be done using ${\color{blue}@condition}$. But usage of ${\color{blue}@condition}$ different from the element to element.

> **Maps Example** - To include or exclude Map objects, Map objects should have an entry with key as ${\color{blue}@condition}$
> ```json
> {
> 	"book": {
> 		"@condition": "book.availableCount >= 1",
> 		"title": "${book.title}"
> 	}
> }
> ```
> In above example, "book" (whose value is map) will be included in the output json, only if the condition specified by key ${\color{blue}@condition}$ in this map results in true.

> **Lists Example** - To include or exclude List objects, the first element of list should be string and should be prefix " ${\color{blue}@condition:}$ " followed by condition. With this the current list will be included in output json only when this condition evaluates to true.
>```json
>{
>	"books": ["@condition: library.enabled", 1000, {"a": "b"}],
>}
>```
>In above example, "books" will be included in final json only if corresponding conditions are true.

### Simple Values
Cases when simple values has to be included/excluded based on conditions, Instead of simple value a map will be included with 2 or 3 keys as described below.
* ${\color{blue}@condition}$: tells when to include or exclude. Or when to use main ${\color{blue}@value}$ or when to use ${\color{blue}@falseValue}$.
* ${\color{blue}@value}$: defines the final value (by replacing the current map) when condition evaluates to **true**.
  * **Note:** When ${\color{blue}@value}$ is defined along with condition, other keys in this map will be ignored.
* ${\color{blue}@falseValue}$: defines the final value (by replacing the current map) when condition evaluates to **false**.
  * **Note:** If ${\color{blue}@falseValue}$ is NOT defined and condition evaluates to false, the current map will be completely excluded.

> **Example for inclusion/exclusion key-value of a Map**
> ```json
> {
> 	"enabled": {
> 		"@condition": "book.available == 1",
> 		"@value": "Enabled"
> 	},
> 	"otherKey": "otherValue"
> }
> ```
> In above example, if condition evaluates to true, the result would be:
> ```json
> {
> 	"enabled": "Enabled",
> 	"otherKey": "otherValue"
> }
> ```
> if condition evaluates to false, "enable" atttribute would be excluded and the result would be:
> ```json
> {
> 	"otherKey": "otherValue"
> }

> **Example for inclusion/exclusion with false-value**
> ```json
> {
> 	"enabled": {
> 		"@condition</span>": "boook.available == 1",
> 		"@value": "Enabled",
> 		"@falseValue": "Disabled",
> 		"extraKey": "some value"
> 	},
> 	"otherKey": "otherValue"
> }
> ```
> In above example, if condition evaluates to true, the result would be:
> ```json
> {
> 	"enabled": "Enabled",
> 	"otherKey": "otherValue"
> }
> ```
> If condition evaluates to false, the result would be:
> ```json
> {
> 	"enabled": "Disabled",
> 	"otherKey": "otherValue"
> }
> ```
> **Note in both cases, "extraKey" is completely ignored.**

> **Example for inclusion/exclusion of an element of a list**
> ```json
> [
> 	{
> 		"@condition": "book.available == 1",
> 		"@value": "@fmarker: book.price"
> 	},
> 	100
> ]
> ```
> In above example, if condition evaluates to true, assuming "book.price" value is 1000 the result would be:
> ```json
> [1000,100]
> ```
> If condition evaluates to false, the result would be:
> ```json
> [100]
> ```

## Loops
When elements has to be repeated dynamically based on data from context, then loops play major role.
* **List values**: Loops for List elements: A map element of a list can be repeated using ${\color{blue}@for-each}$ attribute in the map. Which defines the loop variable and list of elements to loop through. The value of this key will be treated as free-marker value expression by default.
* **Map Entries**: Just like list elements, we can use loops in map-entries also. And in general, in such cases, the key of entry which is getting repeated will also be an expression. And will have access to the current loop variable.

> **List Example**
> ```json
> {
> 	"books": [
> 		{
> 			"@for-each(book)": "books",
> 			"title": "${book.title}",
> 			"desc": "This is book with summary - ${book.summary}"
> 		}
> 	]
> }
> ```
> In above example, a loop variable "book" is used and within current object it can be accessed as a context variable. And it loops through the context value returned by "books".

> **Map Entries Example**
> ```json
> {
> 	"bookMap": {
> 		"@fmarker: book.id": {
> 			"@for-each(book)": "books",
> 			"available": 1
> 		}
> 	}
> }
> ```
> In above example, the element will generate key-value pair for every element returned by "books" expression. And the key of generated entry, uses container name itself. So if "books" returns 3 elements, then in final json "bookMap" will have 3 entries with book-id as key and "available=1" as the map entry.

> **Using static list for loop**
> ```json
> {
> 	"books": {
> 		"@fmarker: title": {
> 			"@for-each(title)": [ "Davinci Code", "Sphere", "Prey" ],
> 			"available": 1
> 		}
> 	}
> }
> ```
> In this case a simple json list can be specified as shown above for looping.

> **Inclusion/exclusion within loop**
> ```json
> {
> 	"books": {
> 		"@fmarker: title": {
> 			"@for-each(title)": [ "Davinci Code", "Sphere", "Prey" ],
> 			"@for-each-condition": "isEnabled(title)",
> 			"available": 1
> 		}
> 	}
> }
> ```
> Within the loop the object inclusion and exclusion can be done using ${\color{blue}@for-each-condition}$ which will be evaluated for each iteration.

## Transformation
Following special conversions are supported when standard json representation is not enough ${\color{blue}@transform}$ can be used to transform the data into other format using any replacement expressions. In general this is used in conjunction with ${\color{blue}@value}$ and ${\color{blue}@falseValue}$ (in conditions).

In these expressions current-value being transformed can be accessed as ${\color{blue}@thisValue}$.

> **Example to convert result object into json string**
> ```json
> {
> 	"result": {
> 		"@transform": "@expr: toJson(thisValue)",
> 		"@value": {
> 			"someVal": 1,
> 			"books": "@fmarker: books"
> 		}
> 	}
> }
> ```
> The result will be something like below:
> ```json
> {
> 	"result": "{\"someVal\":1,\"books\":[\"Davinci Code\",\"Sphere\",\"Prey\"]}"
> }
> ```

## Resource Loading
When complex content, like multi-line or double-quotes etc, needs to be added in json, the content can be placed in an external resource file and can be loaded	using ${\color{blue}@resource}$.

${\color{blue}@transform}$ can be applied to transform loaded content into other format.

> **Loading and transformation example**
> ```json
> {
> 	"type": "Fiction",
> 	"metaInfo":  {
> 		"@resource": "/fiction-meta.xml",
> 		"@transform": "@fmarker: normalizeXml(thisValue)"
> 	}
> }
> ```

## Variables
In cases, where a complex expressions has to be used repeatedly or to minimize complexity of an expression, single expression may needs to be divided, a dynamic variable will come handy.

Using ${\color{blue}@set}$ new variables can be declared which in turn can be accessed directly as standard context attributes.

> **Set Example**
> ```json
> {
> 	"@set(bookMap)": {
> 		"titles": "@xpathMulti: /books//title"
> 	},
> 	"key2": "@fmarker: bookMap.titles[0]"
> }
> ```
> In the above example, a variable “prodMap” is created with value as a map (defined on right side). And then this variable is used in next line as a normal context attribute.
 
