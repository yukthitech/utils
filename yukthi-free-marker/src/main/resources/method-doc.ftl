<#list methods as method>
#### ${method.name}()
**Description**: ${method.description}
**Returns**: **[${method.returnType}]** ${method.returnDescription}

<#if isNotEmpty(method.parameters)>
|Parameters|
|----------|
|Name|Type|Default Value|Description|
|:---|:---|:-----------|:-----------|
<#list method.parameters as param>
|${param.name}|${param.type!''}|${param.defaultValue!''}|${param.description!''}|
</#list>
</#if>

<#if isNotEmpty(method.examples)>
<#list method.examples as example>
> **Example:** ${example.usage}
> **Result:** ${example.result}
</#list>
</#if>

</#list>
