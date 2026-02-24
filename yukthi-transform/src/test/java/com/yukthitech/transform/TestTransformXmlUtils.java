package com.yukthitech.transform;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestTransformXmlUtils 
{
    @SuppressWarnings("unchecked")
	@Test
    public void testToMap() throws Exception
    {
        // Single XML covering:
        // - nodes with only attributes,
        // - only child elements,
        // - attributes + child elements,
        // - attributes + text content,
        // - etc.

        String xml = IOUtils.resourceToString("/elemToMap/sample.xml", Charset.defaultCharset());
        String expectedJson = IOUtils.resourceToString("/elemToMap/expected-output.json", Charset.defaultCharset());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xml.getBytes()));
        Element element = document.getDocumentElement();
        Map<String, Object> map = TransformXmlUtils.toMap(element);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> expectedMap = mapper.readValue(
            expectedJson, 
            Map.class);

        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));

        Assert.assertEquals(map, expectedMap);
    }
}
