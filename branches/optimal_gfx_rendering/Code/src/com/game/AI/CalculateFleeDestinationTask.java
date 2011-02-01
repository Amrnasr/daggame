package com.game.AI;

import java.util.Vector;

import com.game.Player;
import com.game.Vec2;

/**
 * This task calculates the direction vector for the Cursor to
 * flee in, and stores the vector and the destination in the Blackboard
 * @author Ying
 *
 */
public class CalculateFleeDestinationTask extends LeafTask 
{
	/**
	 * Distance to run in case of fleeing
	 */
	private final int FleeDistance = 80;
	
	/**
	 * Radius to check for danger
	 */
	private final int DangerRadius = 100;

	/**
	 * Creates a new instance of the CalculateFleeDestinationTask class
	 * @param blackboard Reference to the AI Blackboard data
	 */
	public CalculateFleeDestinationTask(Blackboard blackboard) 
	{
		super(blackboard);
	}
	
	/**
	 * Creates a new instance of the CalculateFleeDestinationTask class
	 * @param blackboard Reference to the AI Blackboard data
	 * @param name Name of the class, for debug purposes
	 */
	public CalculateFleeDestinationTask(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	/**
	 * Does basic sanity checks for this Task
	 */
	@Override
	public boolean CheckConditions() 
	{
		LogTask("Cheking conditions");
		return Blackboard.players.size() > 1;
	}

	/**
	 * Calculates flee path and ends the task.
	 */
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
			GetControl().FinishWithSuccess();
		}
		else
		{
			GetControl().FinishWithFailure();
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
