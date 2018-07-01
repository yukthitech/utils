package freemarker.ext.beans;

import java.lang.reflect.Method;

public class MethodDescriptor
{
	private Method method;
	
	public MethodDescriptor(Method method)
	{
		this.method = method;
	}
	
	public Method getMethod()
	{
		return method;
	}
}
