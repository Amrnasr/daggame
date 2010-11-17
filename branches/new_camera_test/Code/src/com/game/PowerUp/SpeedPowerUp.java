package com.game.PowerUp;

import com.game.Vec2;

/**
 * PowerUp that increases the cursor's speed.
 * @author Ying
 *
 */
public class SpeedPowerUp extends PowerUp 
{
	/**
	 * How much it increases the speed.
	 */
	float speedIncrement;

	/**
	 * Creates a new PowerUp with a specific position.
	 * @param startingPos Position where to place the PowerUp.
	 */
	public SpeedPowerUp(Vec2 startingPos)
	{
		super(startingPos);
		this.speedIncrement = 0.3f;
	}
	
	/**
	 * Increases the Player speed.
	 */
	@Override
	public void ApplyEffect() 
	{		
		parent.EditDensitySpeed(speedIncrement);
	}

	/**
	 * Restores the Player speed to the original amount.
	 */
	@Override
	public void RemoveEffect() 
	{
		parent.EditDensitySpeed(-speedIncrement);

	}

	/**
	 * Does naught.
	 */
	@Override
	public void Update() 
	{	}

}
