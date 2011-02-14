package com.game.AI;

/**
 * Decorator to split the AStar search in various iterations.
 * @author Ying
 *
 */
public class AStarSplitDecorator extends TaskDecorator 
{
	public AStarSplitDecorator(Blackboard blackboard, Task task) 
	{
		super(blackboard, task);
	}

	public AStarSplitDecorator(Blackboard blackboard, Task task, String name) 
	{
		super(blackboard, task, name);
	}

	@Override
	public void DoAction() 
	{
		this.task.DoAction();
		if(this.task.GetControl().Finished() && this.task.GetControl().Succeeded() && !bb.aStarData.done)
		{
			this.task.GetControl().Reset();
		}
	}

}
