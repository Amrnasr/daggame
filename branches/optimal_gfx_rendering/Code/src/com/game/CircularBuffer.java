package com.game;

/**
 * Circular static buffer implementation.
 * Does not grow.
 * It overrides the oldest element when it runs out of space
 * @author Ying
 *
 */
public class CircularBuffer 
{
	/**
	 * Data array
	 */
	private float data[];
	
	/**
	 * Tail position
	 */
	private int tail;
	
	/**
	 * Creates a new instance of CircularBuffer with the specified number of elements
	 * @param number Number of elements the buffer is going to have
	 * @param initialVal Value to initialize all elements of the buffer
	 */
	public CircularBuffer(int number, float initialVal) 
	{
		data = new float[number];
		
		for(int i = 0; i < number; i++)
		{
			data[i] = initialVal;
		}
		
		tail = 0;
	}
	
	/**
	 * Stores a new value in the buffer
	 * @param value Value to store
	 */
	public void Store(float value) 
	{
		data[tail++] = value;
		if (tail == data.length) 
		{
			tail = 0;
		}
	}
	
	/**
	 * Gets the average of all the values in the buffer.
	 * @return The average of all values.
	 */
	public float GetAverage()
	{
		float sum = 0;
		
		for(int i= 0; i < data.length; i++)
		{
			sum += data[i];
		}
		
		return sum / data.length;
	}
	
	@Override
	public String toString()
	{
		String res = "Av: " + GetAverage() + " Data: ";
		for(int i = 0; i < data.length; i++)
		{
			res += data[i] + ", ";
		}
		return res;
	}
}
