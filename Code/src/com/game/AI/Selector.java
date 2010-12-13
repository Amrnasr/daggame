package com.game.AI;

public class Selector extends GroupTask 
{

	public Selector(Blackboard blackboard)
	{
		super(blackboard);		
	}	
	
	public Selector(Blackboard blackboard, String name)
	{
		super(blackboard, name);		
	}
	
	public Task ChooseNewTask()
	{
		Task task = null;
		boolean found = false;
		int curPos = this.subtasks.indexOf(this.curTask);
		
		while(!found)
		{
			if(curPos == (this.subtasks.size() - 1))
			{
				found = true;
				task = null;
				break;
			}
			
			task = this.subtasks.elementAt(curPos);
			if(task.CheckConditions())
			{
				found = true;
			}
			else
			{
				curPos++;
			}
		}
		
		return task;
	}

	@Override
	public void ChildFailed() 
	{
		this.curTask = ChooseNewTask();
		if(this.curTask == null)
		{
			this.FinishWithFailure();
		}		
	}

	@Override
	public void ChildSucceeded() 
	{
		this.FinishWithSuccess();		
	}
}
