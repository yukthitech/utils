<#list directives as directive>
### ${'$'}{\color{blue}@${directive.name}}$
**Description**: ${directive.description}<br>

<#if isNotEmpty(directive.parameters)>
**Parameters**
|Name|Default Value|Description|
|:---|:-----------|:-----------|
<#list directive.parameters as param>
|${param.name}|${param.defaultValue!''}|${param.description!''}|
</#list>
</#if>

<#if isNotEmpty(directive.examples)>
<#list directive.examples as example>
> **Example:** ${example.title}<br>
> **Usage:** ```${example.usage}```<br>
> **Result:** ```${example.result}```

</#list>
</#if>

</#list>
