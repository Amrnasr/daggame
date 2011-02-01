package com.game.AI;

import java.util.Vector;

import com.game.Preferences;
import com.game.Vec2;
import com.game.PowerUp.PowerUp;

/**
 * Task to search for the first PowerUp that is close enough to the
 * Player Cursor, and set it's position as destination
 * @author Ying
 *
 */
public class SearchForPowerUpTask extends LeafTask 
{
	/**
	 * Maximum distance to search before giving up.
	 */
	private int MaxDistanceToSearch = 200;

	public SearchForPowerUpTask(Blackboard blackboard) 
	{
		super(blackboard);
		
	}

	public SearchForPowerUpTask(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() 
	{
		return bb.player.GetCursor().GetPosition() != null;
	}

	/**
	 * Searches for just the first one that meets the conditions,
	 * not the closest one. 
	 * 
	 * If it finds one, it sets it as the destination and finishes 
	 * with success, if not, it just finishes with failure.
	 */
	@Override
	public void DoAction() 
	{
		boolean found = false;
		Vector<PowerUp> powerUps = Blackboard.powerUpManager.GetPowerUps();
		Vec2 cursorPos = bb.player.GetCursor().GetPosition(); 
		
		for(int i = 0; i < powerUps.size(); i++)
		{
			Vec2 puPos = powerUps.elementAt(i).Pos();
			Vec2 distance = cursorPos.GetVectorTo(puPos);
			if(distance.Length() < MaxDistanceToSearch)
			{
				bb.moveDirection = distance;
				bb.destination.Set(puPos.X(), puPos.Y());
				this.control.FinishWithSuccess();
				found = true;
				break;
			}
		}
		
		if(!found)
		{
			this.control.FinishWithFailure();
		}
	}

	@Override
	public void End() 
	{
		LogTask("Ending");
	}

	@Override
	public void Start() 
	{
		LogTask("Starting");
		MaxDistanceToSearch = Math.max(Preferences.Get().mapHeight /2, MaxDistanceToSearch);
	}

}
