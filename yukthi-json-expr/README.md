# Json Expression Language (JEL)

JEL is a transformation/templating engine which can transform object data (from multiple sources) into different form based on template. Templates are also json files with support for basic templating units like - conditions, loops, variable, easy access etc.

![Alt text](doc/images/jel.png?raw=true "JEL Engine")

## Replacement Expressions
	
All values in JEL templates can have free-marker expressions in json format and will be processed. But there will be situations where entire value should be replaced with just not string but other data type or complex object. In those places below mentioned replace expressions will come handy.

Using one the prefixes mentioned below, the full key/value can be replaced with the resultant value. The result can be any of the JSON supported type - String, boolean, int, date, map or list. For keys, if expression results in non-string, the result will be converted to string and then used as a key.

```html
	<ul>
		<li>
			<span style="color: blue; font-weight: bold;">@fmarker</span> - Used to inject dynamic values using free marker expression.
		</li>
		<li>
			<span style="color: blue; font-weight: bold;">@xpath</span> - Used to fetch value from the current context using xpath. In this case, when xpath matches multiple values, only the first value will be picked.
		</li>
		<li>
			<span style="color: blue; font-weight: bold;">@xpathMulti</span> - Used to fetch multiple values from the current context using xpath.
		</li>
	</ul>
```
	
	<div class="example-box">
		<div class="example-heading">
			Map Examples - Shows how replacement-expressions can be used in map values
		</div>
		<div class="example-body">
			<p>
				{ "key2": "<span class="simple-blue">@fmarker</span>: prodMap.productNames[0]" }
			</p>
			<p>
				{ "docCount": "<span class="simple-blue">@xpath</span>: /products[name='aggr']/docSize" }
			</p>
			<p>
				{ "productNames": "<span class="simple-blue">@xpathMulti</span>: /products//name" }
			</p>
		</div>
	</div>

	<div class="example-box">
		<div class="example-heading">
			List Examples - Shows how replacement-expressions can be used in list values
		</div>
		<div class="example-body">
			<p>
				{ "listWithNoCond": ["@fmarker: devStackEnabled", 20] }
			</p>
		</div>
	</div>


Expressions in Keys and Values
String values (both in keys and values) when dynamic-value-replacer prefixes are not used, they will be considered as free-marker templates. And freemarker expressions within the strings can be used in standard way.

Examples:

Example in key:

{ "devStack-${devStackEnabled}": {  "desc": "This is a dev stack" } }

Example in value:

{ "name": "${cont}" }
	
</body>
