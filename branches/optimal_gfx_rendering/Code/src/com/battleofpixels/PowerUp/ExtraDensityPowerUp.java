package com.game.battleofpixels.PowerUp;

import com.game.battleofpixels.Vec2;

/**
 * PowerUp that gives the player extra density.
 * @author Ying
 *
 */
public class ExtraDensityPowerUp extends PowerUp 
{
	private final static int extraDensity = 200;

	public ExtraDensityPowerUp(Vec2 startingPos, int type) 
	{
		super(startingPos, type, 0.1f);
	}

	@Override
	public void ApplyEffect() 
	{
		this.sceneRef.GetMap().AtWorld((int)this.Pos().X(), (int)this.Pos().Y())
			.AddDensity(this.parent, extraDensity);
	}

	@Override
	public void RemoveEffect() {}

	@Override
	public void Update() {}

}
