package com.yukthitech.test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.context.AutomationContext;

public class TAutomationFlows
{
	@BeforeClass
	public void setup() throws Exception
	{
		AutomationLauncher.systemExitEnabled = false;
	}
	
	private List<String> loadFlows(String resource) throws Exception
	{
		String content = IOUtils.resourceToString(resource, Charset.defaultCharset());
		String lines[] = content.split("\\n");
		List<String> list = new ArrayList<String>();
		
		for(String line : lines)
		{
			line = line.trim();
			
			if(line.length() <= 0)
			{
				continue;
			}
			
			list.add(line);
		}
		
		return list;
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	public void testSuccessCases() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-tsf", "./src/test/resources/test-suite-flows",
				"-rf", "./output/flows", 
				"-prop", "./src/test/resources/app.properties", 
				//"-ts", "jobj-test-suites"
				//"-tc", "dataProviderOnFetchIndependentCtx"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		List<String> flowPoints = (List<String>) AutomationContext.getInstance().getGlobalAttribute("flowCapture");
		System.out.println(flowPoints);
		
		Assert.assertEquals(flowPoints, loadFlows("/data/test-flow-order.txt"));
	}
}
