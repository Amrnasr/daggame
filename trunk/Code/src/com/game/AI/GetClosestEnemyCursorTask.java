package com.game.AI;

import java.util.Vector;

import com.game.Cursor;
import com.game.Player;
import com.game.Preferences;
import com.game.Vec2;

public class GetClosestEnemyCursorTask extends Task 
{
	public GetClosestEnemyCursorTask(Blackboard blackboard)
	{
		super(blackboard);
	}
	
	public GetClosestEnemyCursorTask(Blackboard blackboard, String name)
	{
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() 
	{		
		LogTask("Checking conditions");
		return Blackboard.players.size() > 1 && bb.player.GetCursor().GetPosition() != null;
	}

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
						this.FinishWithSuccess();
					}
				}
			}			
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
	}

}
