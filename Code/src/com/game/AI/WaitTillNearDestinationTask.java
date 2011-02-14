package com.game.AI;


import android.graphics.Rect;
import android.util.Log;

/**
 * This task waits until the Cursor is near the destination.
 * It is used to force the planner to wait until the current strategy
 * has been completely carried out before starting on the next one.
 * 
 * @author Ying
 *
 */
public class WaitTillNearDestinationTask extends LeafTask 
{
	/**
	 * Error allowed to the end distance point. 
	 */
	private static final int PADDING_DISTANCE = 5;
	
	/**
	 * Time to wait till evaluating again
	 */
	private static final float TIMEOUT = 3000;
	
	private long initialTime;
	
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
		LogTask("Checking conditions " + (bb.moveDirection != null));
		return bb.moveDirection != null;
	}

	/**
	 * Checks if we have arrived, finishes if we have.
	 */
	@Override
	public void DoAction() 
	{
		LogTask("Doing action : " + bb.path.size());
		
		if( rect.contains(
				(int)(bb.player.GetCursor().GetPosition().X()),
				(int)(bb.player.GetCursor().GetPosition().Y())) )
		{
			control.FinishWithSuccess();
		}
		
		// Check for a timeout of the wait
		else if(System.currentTimeMillis() - this.initialTime > TIMEOUT)
		{
			Log.i("WaitTillNearDestination", "" + System.currentTimeMillis() + " - " + this.initialTime + " ( " + (System.currentTimeMillis() - this.initialTime) + " ) "+" >? " + TIMEOUT + " = " + (System.currentTimeMillis() - this.initialTime > TIMEOUT));
			control.FinishWithFailure();
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
		this.initialTime = System.currentTimeMillis();
	}
}
