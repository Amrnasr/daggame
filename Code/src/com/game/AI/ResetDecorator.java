package com.game.AI;

public class ResetDecorator extends Selector 
{
	private Selector task;
	
	public ResetDecorator(Blackboard blackboard, Selector task) 
	{
		super(blackboard);
		this.task = task;
	}

	public ResetDecorator(Blackboard blackboard, Selector task, String name) {
		super(blackboard, name);
		this.task = task;
	}
	
	public void DoAction()
	{
		super.DoAction();
		if(Finished())
		{
			Reset();
		}
	}

}
