package com.game.battleofpixels.PowerUp;

import com.game.battleofpixels.Vec2;

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
	public SpeedPowerUp(Vec2 startingPos, int type)
	{
		super(startingPos,type, 3);
		this.speedIncrement = 0.3f;
	}
	
	/**
	 * Increases the Player speed.
	 */
	@Override
	public void ApplyEffect() 
	{		
		parent.EditDensitySpeed(speedIncrement);
		parent.GetCursor().AddToSpeed(speedIncrement);
	}

	/**
	 * Restores the Player speed to the original amount.
	 */
	@Override
	public void RemoveEffect() 
	{
		parent.EditDensitySpeed(-speedIncrement);
		parent.GetCursor().AddToSpeed(-speedIncrement);
	}

	/**
	 * Does naught.
	 */
	@Override
	public void Update() 
	{	}

}
