package com.yukthitech.utils;

import java.util.LinkedHashMap;

/**
 * Simple Linked Hash Map based LRU map.
 * 
 * @author akiran
 * @param <K>
 * @param <V>
 */
public class LruMap<K, V> extends LinkedHashMap<K, V>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Capacity of LRU cache (number of elements)
	 */
	private int capacity;
	
	public LruMap(int capacity)
	{
		super(16, 0.75f, true);
		
		if(capacity <= 0)
		{
			throw new IllegalArgumentException("Capacity should non zero positive value.");
		}
		
		this.capacity = capacity;
	}
	
	public LruMap(LruMap<K, V> map)
	{
		super(map);
		this.capacity = map.capacity;
	}
	
	/**
	 * Gets the capacity of LRU cache (number of elements).
	 *
	 * @return the capacity of LRU cache (number of elements)
	 */
	public int getCapacity()
	{
		return capacity;
	}
	
	/* (non-Javadoc)
	 * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
	 */
	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest)
	{
		boolean res = super.size() > capacity;
		return res;
	}
}
