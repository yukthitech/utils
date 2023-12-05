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
>  * { "key2": " ${\color{blue}@fmarker}$: prodMap.productNames[0]" }
>  * { "docCount": " ${\color{blue}@xpath}$: /products[name='aggr']/docSize" }
>  * { "productNames": " ${\color{blue}@xpathMulti}$: /products//name" }

> **List Examples** - Shows how replacement-expressions can be used in list values
>  * { "listWithNoCond": [" ${\color{blue}@fmarker}$: devStackEnabled", 20] }

## Freemarker Expressions
String values (both in keys and values) when dynamic-value-replacer prefixes are not used, they will be considered as free-marker templates. And freemarker expressions within the strings can be used in standard way.

> **Example in Key**
> { "devStack-${devStackEnabled}": {  "desc": "This is a dev stack" } }

> **Example in value**
> { "name": "${cont}" }

## Conditions
Different parts of a json can be declared to be included conditionally. That is, the target section will be included only when specified condition is true. This can be done using ${\color{blue}@condition}$. But usage of ${\color{blue}@condition}$ different from the element to element.

> **Maps Example** - To include or exclude Map objects, Map objects should have an entry with key as ${\color{blue}@condition}$
> ```json
> {
> 	"devStack": {
> 		"@condition": "devStackEnabled == 1",
> 		"desc": "This is a dev stack"
> 	}
> }
> ```
> In above example, "devStack" (whose value is map) will be included in the output json, only if the condition specified by key ${\color{blue}@condition}$ in this map results in true.

> **Lists Example** - To include or exclude List objects, the first element of list should be string and should be prefix " ${\color{blue}@condition:}$ " followed by condition. With this the current list will be included in output json only when this condition evaluates to true.
>```json
>{
>	"listWithPositiveCond": ["@condition: devStackEnabled == 1", 1000, {"a": "b"}],
> 	"listWithNegativeCond": ["@condition: prodStackEnabled == 1", 1000, {"a": "b"}]
>}
>```
>In above example, listWithPositiveCond / listWithNegativeCond will be included in final json only if corresponding conditions are true.

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
> 		"@condition": "devStackEnabled == 1",
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
> 		"@condition</span>": "devStackEnabled == 1",
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
> 		"@condition": "devStackEnabled == 1",
> 		"@value": "@fmarker: devStackValue"
> 	},
> 	100
> ]
> ```
> In above example, if condition evaluates to true, assuming "devStackValue" value is 1000 the result would be:
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
> 	"containers": [
> 		{
> 			"@for-each(cont)": "containers",
> 			"name": "${cont}",
> 			"desc": "This is container ${cont}"
> 		}
> 	]
> }
> ```
> In above example, a loop variable "cont" is used and within current object it can be accessed as a context variable. And it loops through the context value returned by "containers".

> **Map Entries Example**
> ```json:
> {
> 	"fullContainers": {
> 		"@fmarker: cont": {
> 			"@for-each(cont)": "containers",
> 			"days": "100"
> 		}
> 	}
> }
> ```
> In above example, the element will generate key-value pair for every element returned by "cotainers" expression. And the key of generated entry, uses container name itself. So if "containers" returns 3 elements, then in final json "fullContainers" will have 3 entries with container name as key and "days=100" as the map entry.

> **Using static list for loop**
> ```json
> {
> 	"containers": {
> 		"@fmarker: cont": {
> 			"@for-each(cont)": [ "bank", "investment", "card" ],
> 			"days": "100"
> 		}
> 	}
> }
> ```
> In this case a simple json list can be specified as shown below for looping.

> **Inclusion/exclusion within loop**
> ```json
> {
> 	"containers": {
> 		"@fmarker: cont": {
> 			"@for-each(cont)": [ "bank", "investment", "card" ],
> 			"@for-each-condition": "isEnabled(cont)",
> 			"days": "100"
> 		}
> 	}
> }
> ```
> Within the loop the object inclusion and exclusion can be done using ${\color{blue}@for-each-condition}$ which will be evaluated for each iteration.


  
