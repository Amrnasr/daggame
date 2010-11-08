package com.game.PowerUp;

import com.game.Vec2;

public class SpeedPowerUp extends PowerUp 
{
	float speedIncrement;

	public SpeedPowerUp(Vec2 startingPos)
	{
		super(startingPos);
		this.speedIncrement = 0.3f;
	}
	
	@Override
	public void ApplyEffect() 
	{		
		parent.EditDensitySpeed(speedIncrement);
	}

	@Override
	public void RemoveEffect() 
	{
		parent.EditDensitySpeed(-speedIncrement);

	}

	@Override
	public void Update() 
	{	}

}
