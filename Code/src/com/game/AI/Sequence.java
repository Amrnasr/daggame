package com.game.AI;

public class Sequence extends GroupTask 
{
	public Sequence(Blackboard blackboard)
	{
		super(blackboard);
	}
	
	public Sequence(Blackboard blackboard, String name)
	{
		super(blackboard, name);
	}

	@Override
	public void ChildFailed() 
	{
		this.FinishWithFailure();
	}

	@Override
	public void ChildSucceeded() 
	{
		int curPos = this.subtasks.indexOf(this.curTask);
		if( curPos == (this.subtasks.size() - 1))
		{
			this.FinishWithSuccess();
		}
		else
		{
			this.curTask = this.subtasks.elementAt(curPos + 1);
			if(!this.curTask.CheckConditions())
			{
				this.FinishWithFailure();
			}
		}
	}
}
