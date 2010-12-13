package com.game.AI;

import com.game.Regulator;

import android.graphics.Rect;
import android.util.Log;

public class WaitTillNearDestinationTask extends Task 
{
	//private final float TIMEOUT_SECS = 8;
	private final int PADDING_DISTANCE = 5;
	Rect rect;
	//Regulator timeout;

	public WaitTillNearDestinationTask(Blackboard blackboard) 
	{
		super(blackboard);
	}
	
	public WaitTillNearDestinationTask(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return bb.moveDirection != null;
	}

	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		
		if( rect.contains(
				(int)(bb.player.GetCursor().GetPosition().X()),
				(int)(bb.player.GetCursor().GetPosition().Y())) )
		{
			FinishWithSuccess();
		}
		/*
		else if(timeout.IsReady())
		{
			FinishWithFailure();
		}
		*/
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
		this.rect = new Rect(
				(int)(bb.destination.X() - PADDING_DISTANCE),
				(int)(bb.destination.Y() - PADDING_DISTANCE),
				(int)(bb.destination.X() + PADDING_DISTANCE),
				(int)(bb.destination.Y() + PADDING_DISTANCE));
		//this.timeout = new Regulator(1.0f/TIMEOUT_SECS);
	}

}
