package com.yukthitech.autox.ide.engine;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import com.yukthitech.autox.ide.model.IdeState;

public class IdeEngineClassLoader extends ClassLoader
{
	private class DynamicClassLoader extends URLClassLoader
	{
		private ClassLoader appClassLoader = DynamicClassLoader.class.getClassLoader();
		
		public DynamicClassLoader()
		{
			super(getSystemClassPath(), null);
		}
		
		@Override
		public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
		{
			if(!isMonitoredType(name))
			{
				return appClassLoader.loadClass(name);
			}
			
			return super.loadClass(name, resolve);
		}
	}
	
	/**
	 * Dynamic class loader which helps in loading the classes on demand.
	 */
	private DynamicClassLoader dynamicClassLoader = new DynamicClassLoader();
	
	private Set<String> monitoredPacks = new HashSet<>();
	
	public IdeEngineClassLoader(IdeState ideState)
	{
		monitoredPacks.add("com.yukthitech.autox.ide");
		
		if(ideState.getMonitoredPackages() != null)
		{
			monitoredPacks.addAll(ideState.getMonitoredPackages());
		}
	}
	
	private boolean isMonitoredType(String name)
	{
		for(String pack : monitoredPacks)
		{
			if(name.startsWith(pack))
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		if(!isMonitoredType(name))
		{
			return super.loadClass(name, resolve);
		}

		System.out.println("Load class is invoked: " + name);

		try
		{
			Class<?> cls = dynamicClassLoader.loadClass(name, resolve);
			
			if(cls != null)
			{
				return cls;
			}
		}catch(ClassNotFoundException ex)
		{
			//ignore
		}
		
		return super.loadClass(name, resolve);
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		return this.loadClass(name, false);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		System.out.println("Find class is invoked: " + name);
		return super.findClass(name);
	}

	/**
	 * Gets the urls of classpath of system classloader.
	 * @return
	 */
	private static URL[] getSystemClassPath()
	{
		ClassLoader cl = ClassLoader.getSystemClassLoader();
        return ((URLClassLoader)cl).getURLs();
	}
}
