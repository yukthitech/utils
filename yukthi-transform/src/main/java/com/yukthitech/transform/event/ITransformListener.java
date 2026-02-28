package com.yukthitech.transform.event;

/**
 * Listener for transform events.
 * @author akiran
 */
public interface ITransformListener
{
    /**
     * Invoked when a transform event occurs.
     * @param event the transform event
     */
    public void onTransform(TransformEvent event);
}
