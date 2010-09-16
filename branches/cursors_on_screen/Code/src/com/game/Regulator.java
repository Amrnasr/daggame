package com.game;

public class Regulator 
{
	private long lastUpdateTime;
	private long updateSpeed;
	private static final int MILSECS = 1000;
	
	public Regulator(int framesPerSecond)
	{
		this.updateSpeed = MILSECS / framesPerSecond;
		this.lastUpdateTime = System.currentTimeMillis();
	}
	
	public boolean IsReady()
	{
		if(System.currentTimeMillis() - this.lastUpdateTime > updateSpeed)
		{
			this.lastUpdateTime = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
}
