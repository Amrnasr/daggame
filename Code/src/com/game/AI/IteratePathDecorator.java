package com.game.AI;

public class IteratePathDecorator extends TaskDecorator 
{
	public IteratePathDecorator(Blackboard blackboard, Task task) 
	{
		super(blackboard, task);
	}

	public IteratePathDecorator(Blackboard blackboard, Task task, String name) 
	{
		super(blackboard, task, name);
	}

	@Override
	public void DoAction() 
	{
		this.task.DoAction();
		if(this.task.GetControl().Finished() && !bb.path.isEmpty())
		{
			this.task.GetControl().Reset();
		}
	}

}
