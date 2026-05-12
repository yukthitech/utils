package com.yukthitech.transform.template;

import java.io.Serializable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

/**
 * Context passed during template compilation. Holds the {@link FreeMarkerEngine} used to build
 * FreeMarker templates and parse expressions; additional fields can be added later.
 */
public final class TemplateCompileContext implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final FreeMarkerEngine freeMarkerEngine;

	private final transient Set<TransformTemplate> templatesInProgress = Collections.newSetFromMap(new IdentityHashMap<>());

	private final transient Set<TransformObject> objectsInProgress = Collections.newSetFromMap(new IdentityHashMap<>());

	public TemplateCompileContext(FreeMarkerEngine freeMarkerEngine)
	{
		if(freeMarkerEngine == null)
		{
			throw new NullPointerException("freeMarkerEngine cannot be null.");
		}
		this.freeMarkerEngine = freeMarkerEngine;
	}

	public FreeMarkerEngine getFreeMarkerEngine()
	{
		return freeMarkerEngine;
	}

	public boolean beginTemplate(TransformTemplate template)
	{
		if(templatesInProgress.contains(template))
		{
			return false;
		}
		templatesInProgress.add(template);
		return true;
	}

	public void endTemplate(TransformTemplate template)
	{
		templatesInProgress.remove(template);
	}

	public boolean beginTransformObject(TransformObject object)
	{
		if(objectsInProgress.contains(object))
		{
			return false;
		}
		objectsInProgress.add(object);
		return true;
	}

	public void endTransformObject(TransformObject object)
	{
		objectsInProgress.remove(object);
	}
}
