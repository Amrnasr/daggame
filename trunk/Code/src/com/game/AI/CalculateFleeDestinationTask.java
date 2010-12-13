package com.game.AI;

import java.util.Vector;

import com.game.Player;
import com.game.Vec2;

public class CalculateFleeDestinationTask extends Task 
{
	/**
	 * Distance to run in case of fleeing
	 */
	private final int FleeDistance = 80;
	
	/**
	 * Radius to check for danger
	 */
	private final int DangerRadius = 100;

	public CalculateFleeDestinationTask(Blackboard blackboard) 
	{
		super(blackboard);
		
	}
	
	public CalculateFleeDestinationTask(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
		
	}

	@Override
	public boolean CheckConditions() 
	{
		// TODO Auto-generated method stub
		LogTask("Cheking conditions");
		return Blackboard.players.size() > 1;
	}

	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		Vec2 movementVector = new Vec2();		
		Vec2 vecToEnemy = new Vec2();
		Vec2 cursorPos = bb.player.GetCursor().GetPosition();
		Vector<Player> players = Blackboard.players;
		
		// Get a vector away from all cursors 
		for(int i= 0; i < players.size(); i++)
		{
			if(players.elementAt(i).GetID() != bb.player.GetID())
			{
				Vec2 enemyPos = players.elementAt(i).GetCursor().GetPosition();
				vecToEnemy.Set(cursorPos.X()-enemyPos.X(), cursorPos.Y()-enemyPos.Y());
				
				if(vecToEnemy.Length() < this.DangerRadius)
				{
					movementVector.Add(vecToEnemy);
				}
			}			
		}
		
		if(movementVector.Length() > 0)
		{
			// We've got some direction to run to
			// Set a specific distance to flee along that vector
			movementVector.Normalize();
			movementVector.Scale(FleeDistance);
			
			
			bb.moveDirection = movementVector;
			bb.destination = new Vec2( cursorPos.X() + movementVector.X(),cursorPos.Y() + movementVector.Y());
			FinishWithSuccess();
		}
		else
		{
			FinishWithFailure();
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
