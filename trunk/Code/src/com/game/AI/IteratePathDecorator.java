package com.game.AI;

/**
 * Decorates a task making it reset until the path is empty.
 * @author Ying
 *
 */
public class IteratePathDecorator extends TaskDecorator 
{
	/**
	 * Creates a new instance of the IteratePathDecorator class
	 * @param blackboard Reference to the AI Blackboard data
	 * @param task Task to decorate
	 */
	public IteratePathDecorator(Blackboard blackboard, Task task) 
	{
		super(blackboard, task);
	}

	/**
	 * Creates a new instance of the IteratePathDecorator class
	 * @param blackboard Reference to the AI Blackboard data
	 * @param task Task to decorate
	 * @param name Name of the class, for debug purposes
	 */
	public IteratePathDecorator(Blackboard blackboard, Task task, String name) 
	{
		super(blackboard, task, name);
	}

	/**
	 * Does the decorated tasks action.
	 * If the task finishes and there is still tiles in the 
	 * Blackboard path, we give it another go.
	 */
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
