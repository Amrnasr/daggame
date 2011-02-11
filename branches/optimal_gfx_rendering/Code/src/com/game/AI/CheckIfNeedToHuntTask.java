package com.game.AI;

public class CheckIfNeedToHuntTask extends LeafTask 
{
	/**
	 * Minimum time, in milliseconds, that we wait with no density change,
	 * before deciding to hunt.
	 */
	public static final int minTimeToHunt = 1000;
	
	public CheckIfNeedToHuntTask(Blackboard blackboard)
	{
		super(blackboard);
	}
	
	public CheckIfNeedToHuntTask(Blackboard blackboard, String name)
	{
		super(blackboard,name);
	}

	@Override
	public boolean CheckConditions() 
	{
		LogTask("Cheking conditions");
		return true;
	}

	@Override
	public void DoAction() 
	{
		LogTask("Doing Action");
		int timeElapsedSinceDensityUpdate = (int) (System.currentTimeMillis() - bb.player.LastTimeDensityChanged());
		if( timeElapsedSinceDensityUpdate > minTimeToHunt)
		{
			bb.aStarData = new AStarData();
			control.FinishWithSuccess();
		}
		else
		{
			control.FinishWithFailure();
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
