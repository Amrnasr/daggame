package com.game.AI;

public class CheckIfNeedToHunt extends LeafTask 
{
	/**
	 * Minimum time, in milliseconds, that we wait with no density change,
	 * before deciding to hunt.
	 */
	public static final int minTimeToHunt = 500;
	
	public CheckIfNeedToHunt(Blackboard blackboard)
	{
		super(blackboard);
	}
	
	public CheckIfNeedToHunt(Blackboard blackboard, String name)
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
