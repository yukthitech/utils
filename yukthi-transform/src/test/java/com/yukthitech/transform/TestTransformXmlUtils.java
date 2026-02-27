package com.yukthitech.transform;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

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

        Map<String, Object> map = TransformXmlUtils.toMap(xml);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> expectedMap = mapper.readValue(
            expectedJson, 
            Map.class);

        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));

        Assert.assertEquals(map, expectedMap);
    }
}
