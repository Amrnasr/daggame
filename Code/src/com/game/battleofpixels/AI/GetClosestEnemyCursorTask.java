package com.game.battleofpixels.AI;

import java.util.Vector;

import com.game.battleofpixels.Cursor;
import com.game.battleofpixels.Player;
import com.game.battleofpixels.Preferences;
import com.game.battleofpixels.Vec2;

/**
 * Finds the closest enemy Cursor and stores it in the Blackboard.
 * @author Ying
 *
 */
public class GetClosestEnemyCursorTask extends LeafTask 
{
	/**
	 * Creates a new instance of the GetClosestEnemyCursorTask task
	 * @param blackboard Reference to the AI Blackboard data
	 */
	public GetClosestEnemyCursorTask(Blackboard blackboard)
	{
		super(blackboard);
	}
	
	/**
	 * Creates a new instance of the GetClosestEnemyCursorTask task
	 * @param blackboard Reference to the AI Blackboard data
	 * @param name Name of the class for debugging
	 */
	public GetClosestEnemyCursorTask(Blackboard blackboard, String name)
	{
		super(blackboard, name);
	}

	/**
	 * Checks for preconditions
	 */
	@Override
	public boolean CheckConditions() 
	{		
		LogTask("Checking conditions");
		return Blackboard.players.size() > 1 && bb.player.GetCursor().GetPosition() != null;
	}

	/**
	 * Finds the closest enemy cursor and stores it in the Blackboard
	 */
	@Override
	public void DoAction() 
	{
		LogTask("Doing action");

		Vec2 cursorPos = bb.player.GetCursor().GetPosition(); 
		Vec2 enemyPos = null;	
		Vec2 vecToEnemy = new Vec2();
		
		float minLenght = Preferences.Get().mapWidth*2; // Really large number
		Vector<Player> players = Blackboard.players;
		
		for(int i= 0; i < players.size(); i++)
		{
			if(players.elementAt(i).GetID() != bb.player.GetID())
			{
				if(players.elementAt(i).GetTotalDensity() > 0)
				{
					Cursor enemyCur = players.elementAt(i).GetCursor();
					enemyPos = enemyCur.GetPosition();
					vecToEnemy.Set(enemyPos.X()-cursorPos.X(), enemyPos.Y()-cursorPos.Y());
					if(vecToEnemy.Length() < minLenght)
					{
						minLenght = (float) vecToEnemy.Length();
						bb.closestEnemyCursor = enemyCur;
						GetControl().FinishWithSuccess();
					}
				}
			}			
		}
	}

	/**
	 * Ends the task
	 */
	@Override
	public void End() 
	{
		LogTask("Ending");
	}

	/**
	 * Starts the task
	 */
	@Override
	public void Start() 
	{
		LogTask("Starting");
	}
}
