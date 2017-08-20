## Yukthi XML Mapper

**Yukthi XML Mapper** is a simple xml bean mapper with support for lot of customization. Unlike JAXB, this mapper is based on runtime bean types.

Following is the list of the non-standard features supported in this library:

1. **Reserve Nodes**: With the help of xml namespace, special nodes and attributes are supported which helps in pluggin custom way of mapping xml to beans.

2. **Adder Methods**: Other than standard setter methods, adder methods are supported to support List and map properties.

3. **Runtime Types**: Dynamic types can be specified for setter/getter using reserve node "beanType" which helps in injecting runtime beans.

4. **Interchangeable attributes & sub-elements**: Unlike JAXB attributes and sub-elements can used interchangeably for configuring values for properties for target bean.

5. **Bean References**: Like "Spring" one bean can be referenced in other beans using bean identifiers. And bean identifiers for bean can be defined using reserve attribute "beanId". And referece of the bean is done using "beanRef" reserve node.

