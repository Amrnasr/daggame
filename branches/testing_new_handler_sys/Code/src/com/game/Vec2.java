package com.game;

/**
 * Two dimensional vector class.
 * @author Ying
 *
 */
public class Vec2 
{
	private double x;
	private double y;
	
	/**
	 * Empty constructor, initializes to 0
	 */
	public Vec2()
	{
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * Creates an instance of the Vec2 class and initializes x and y
	 * @param x
	 * @param y
	 */
	public Vec2(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Calculates if this Vec2 is equal to the one provided
	 * @param v is the Vec2 to compare against
	 * @return True if it's equal, false if it isn't
	 */
	public boolean Equals(Vec2 v)
	{
		if(this.x == v.x && this.y == v.y) return true;
		else return false;
	}
	
	/**
	 * Calculates the vector length
	 * @return the length of the vector
	 */
	public double Length()
	{
		return Math.sqrt((this.x*this.x + this.y*this.y));
	}
	
	/**
	 * Normalizes the vector
	 */
	public void Normalize()
	{
		double len = Length();
		this.x /= len;
		this.y /= len;
	}
	
	/**
	 * Returns a vector from this to the point provided
	 * @param point to calculate the vector to
	 * @return The vector.
	 */
	public Vec2 GetVectorTo(Vec2 point)
	{
		Vec2 aux = new Vec2();
		
		aux.SetX(point.x - this.x);
		aux.SetY(point.y - this.y);
		
		return aux;
	}
	
	/**
	 * Sets the x,y
	 * @param x
	 * @param y
	 */
	public void Set(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets the x
	 * @param x
	 */
	public void SetX(double x)
	{
		this.x = x;
	}
	
	/**
	 * Sets the y
	 * @param y
	 */
	public void SetY(double y)
	{
		this.y = y;
	}
	
	/**
	 * Adds the offset to the current position
	 * @param x to add to the x component
	 * @param y to add to the y component
	 */
	public void Offset(double x, double y)
	{
		this.x += x;
		this.y += y;
	}
	
	/**
	 * Gets the x value
	 * @return x
	 */
	public double X() { return this.x; }
	
	/**
	 * Gets the y value
	 * @return y
	 */
	public double Y() { return this.y; }

}
