package com.game.AI;

import android.util.Log;

/**
 * Decorator that ends with failure the task if the density suffers changes
 * because that means it's been attacked.
 * @author Ying
 *
 */
public class StopIfAttackedDecorator extends TaskDecorator 
{
	public static final int minTimeToHunt = 1000; // TODO (punt 1000)

	public StopIfAttackedDecorator(Blackboard blackboard, Task task) 
	{
		super(blackboard, task);
	}

	public StopIfAttackedDecorator(Blackboard blackboard, Task task, String name) 
	{
		super(blackboard, task, name);
	}

	@Override
	public void DoAction() 
	{
		long timeElapsedSinceDensityUpdate = (long) (System.currentTimeMillis() - bb.player.LastTimeDensityChanged());
		LogTask( "" + System.currentTimeMillis()  + " - " + bb.player.LastTimeDensityChanged() + " (" +
				timeElapsedSinceDensityUpdate + " ) <? " + minTimeToHunt + " = " + (timeElapsedSinceDensityUpdate < minTimeToHunt));
		if( timeElapsedSinceDensityUpdate < minTimeToHunt)
		{
			this.task.GetControl().FinishWithFailure();
		}
		else
		{
			this.task.DoAction();
		}
	}

}
