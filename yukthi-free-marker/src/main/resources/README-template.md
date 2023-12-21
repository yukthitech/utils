# Yukthi Free Marker

This is a wrapper over Apache freemarker (https://freemarker.apache.org/). This library gives ability to configure static java methods as free-marker methods/directives using simple annotations and also give ability to document them.

## Usage
${\color{blue}FreeMarkerEngine}$ is the entry class for this library. This is wrapper over freemarker and provides following functionality:

* **void loadClass(Class<?> clazz)** - Loads the methods marked as freemarker methods/directives into registry. Which in turn can be used in templates processed by this engine instance.
* **String processTemplate(String name, String templateString, Object context)** - Used to process specified template string as a freemaker template, in which registered freemarker methods/directives can be used.
* **boolean evaluateCondition(String name, String condition, Object context)** - Input condition should be a simple freemarker condition expression (which registered freemarker methods/directives can be used).
* **Object fetchValue(String name, String valueExpression, Object context)** - valueExpression should be simple freemarker expression, which gets evaluated and result will be returned (not the result template string but value of expression). Here also registered freemarker methods/directives can be used.

## Custom Free Marker Methods
	
${\color{blue}@FreeMarkerMethod}$ annotation can be used to mark a static method as a free marker method. And optionally ${\color{blue}@FmParam}$ can be used to document parameters.

> **Example:**
> ```java
> {
> 	@FreeMarkerMethod(
> 			description = "Adds specified number of days to specified date",
> 			returnDescription = "Resultant date after addition of specified days")
> 	public static Date addDays(
> 			@FmParam(name = "date", description = "Date to which days should be added") Date date, 
> 			@FmParam(name = "days", description = "Days to be added.") int days)
> 	{
> 		return DateUtils.addDays(date, days);
> 	}
> ```

## Custom Free Marker Directives
	
${\color{blue}@FreeMarkerDirective}$ annotation can be used to mark a static method as a free marker directive. It should be as of same signature as shown in below example (these parameters is same as that custom directive parameters of freemarker).

> **Example:**
> ```java
> 	@FreeMarkerDirective(value = "trim", 
> 			description = "Trims the content enclosed within this directive.",
> 			examples = {
> 				@ExampleDoc(usage = "<@trim>   some content  </@trim>", result = "some content")
> 			})
> 	public static void trim(Environment env, Map<String, String> params, 
		TemplateModel[] loopVars, TemplateDirectiveBody body) throws Exception
> 	{
> 		StringWriter writer = new StringWriter();
> 		body.render(writer);
> 
> 		env.getOut().append(writer.toString().trim());
> 	}
> ```

## Default Directives
Following additional directives by default are supported by this library:

[[defaultDirectiveContent]]

## Custom Free Marker Methods
Following additional freemarker methods by default are supported by this library:

[[defaultMethodContent]]
