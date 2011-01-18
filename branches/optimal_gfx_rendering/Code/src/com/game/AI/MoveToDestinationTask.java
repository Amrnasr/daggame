package com.game.AI;


/**
 * This task requests the Cursor to move to a given destination.
 * It takes the destination from the Blackboard.
 * @author Ying
 *
 */
public class MoveToDestinationTask extends LeafTask 
{
	/**
	 * Creates a new instance of the MoveToDestinationTask class
	 * @param blackboard Reference of the AI Blackboard data
	 */
	public MoveToDestinationTask(Blackboard blackboard)
	{
		super(blackboard);
	}
	
	/**
	 * Creates a new instance of the MoveToDestinationTask class
	 * @param blackboard Reference of the AI Blackboard data
	 * @param name Name of the class for debugging
	 */
	public MoveToDestinationTask(Blackboard blackboard, String name)
	{
		super(blackboard, name);
	}

	/**
	 * Sanity check of needed data for the operations
	 */
	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return bb.moveDirection != null;
	}

	/**
	 * Requests the cursor to move
	 */
	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		bb.player.GetCursor().MoveInDirection(bb.moveDirection);
		bb.player.GetCursor().StartRotating();
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
