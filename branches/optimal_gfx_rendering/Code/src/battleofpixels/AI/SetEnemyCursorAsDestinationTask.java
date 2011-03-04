package com.game.battleofpixels.AI;

import com.game.battleofpixels.Vec2;

/**
 * This tasks takes the closest enemy Cursor from the Blackboard and
 * sets the destination and move-to vectors properly. 
 * @author Ying
 *
 */
public class SetEnemyCursorAsDestinationTask extends LeafTask 
{
	/**
	 * Creates a new instance of the SetEnemyCursorAsDestinationTask class
	 * @param blackboard Reference to the AI Blackboard data
	 */
	public SetEnemyCursorAsDestinationTask(Blackboard blackboard)
	{
		super(blackboard);
	}
	
	/**
	 * Creates a new instance of the SetEnemyCursorAsDestinationTask class
	 * @param blackboard Reference to the AI Blackboard data
	 * @param name Name of the class, used for debugging
	 */
	public SetEnemyCursorAsDestinationTask(Blackboard blackboard, String name)
	{
		super(blackboard, name);
	}

	/**
	 * Checks that the needed data exists.
	 */
	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return bb.closestEnemyCursor != null && bb.moveDirection != null;
	}

	/**
	 * Calculates the Blackboard's moveDirection and destination vectors.
	 */
	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		Vec2 enemyPos = bb.closestEnemyCursor.GetPosition();
		Vec2 cursorPos = bb.player.GetCursor().GetPosition();
		bb.moveDirection = new Vec2(enemyPos.X()-cursorPos.X(), enemyPos.Y()-cursorPos.Y());
		bb.destination.Set(enemyPos.X(), enemyPos.Y());
		
		control.FinishWithSuccess();
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
