package com.game.PowerUp;

import java.util.Vector;

import com.game.Player;
import com.game.Vec2;
/**
 * PowerUp that slows all the enemies by a fixed amount.
 * @author Ying
 *
 */
public class SlowPowerUp extends PowerUp 
{
	private static final float penalizer = -0.3f;
	
	public SlowPowerUp(Vec2 startingPoint, int type)
	{
		super(startingPoint,type, 3);
		
	}

	@Override
	public void ApplyEffect() 
	{
		int playerID = this.parent.GetID();
		Vector<Player> players = this.sceneRef.GetPlayers();
		
		for(int i = 0; i < players.size(); i++)
		{
			Player player = players.elementAt(i);
			if( player.GetID() != playerID)
			{
				player.EditDensitySpeed(penalizer);
				player.GetCursor().AddToSpeed(penalizer);
			}
		}

	}

	@Override
	public void RemoveEffect() 
	{
		int playerID = this.parent.GetID();
		Vector<Player> players = this.sceneRef.GetPlayers();
		
		for(int i = 0; i < players.size(); i++)
		{
			Player player = players.elementAt(i);
			if( player.GetID() != playerID)
			{
				player.EditDensitySpeed(-penalizer);
				player.GetCursor().AddToSpeed(-penalizer);
			}
		}
	}

	@Override
	public void Update() {	}

}
