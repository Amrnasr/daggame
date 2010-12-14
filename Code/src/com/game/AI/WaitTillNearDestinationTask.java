package com.game.AI;


import android.graphics.Rect;

/**
 * This task waits until the Cursor is near the destination.
 * It is used to force the planner to wait until the current strategy
 * has been completely carried out before starting on the next one.
 * 
 * TODO: Add timeout
 * 
 * @author Ying
 *
 */
public class WaitTillNearDestinationTask extends LeafTask 
{
	/**
	 * Error allowed to the end distance point. 
	 */
	private final int PADDING_DISTANCE = 5;
	
	/**
	 * Rectangle defining the end area
	 */
	Rect rect;

	/**
	 * Creates a new instance of the WaitTillNearDestinationTask class
	 * @param blackboard Reference to the AI Blackboard data
	 */
	public WaitTillNearDestinationTask(Blackboard blackboard) 
	{
		super(blackboard);
	}
	
	/**
	 * Creates a new instance of the WaitTillNearDestinationTask class
	 * @param blackboard Reference to the AI Blackboard data
	 * @param name Name of the class, used for debugging
	 */
	public WaitTillNearDestinationTask(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	/**
	 * Sanity check of the needed data
	 */
	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return bb.moveDirection != null;
	}

	/**
	 * Checks if we have arrived, finishes if we have.
	 */
	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		
		if( rect.contains(
				(int)(bb.player.GetCursor().GetPosition().X()),
				(int)(bb.player.GetCursor().GetPosition().Y())) )
		{
			control.FinishWithSuccess();
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
		this.rect = new Rect(
				(int)(bb.destination.X() - PADDING_DISTANCE),
				(int)(bb.destination.Y() - PADDING_DISTANCE),
				(int)(bb.destination.X() + PADDING_DISTANCE),
				(int)(bb.destination.Y() + PADDING_DISTANCE));
	}
}
