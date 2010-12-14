package com.game.AI;

/**
 * Decorator that adds flee checks to the task it is applied to.
 * @author Ying
 *
 */
public class FleeDecorator extends TaskDecorator 
{
	/**
	 * Bound for when to start fleeing. It works in a [0, 1] domain.
	 */
	private final float LOOSING_LOWER_BOUND = 0.4f;

	/**
	 * Creates a new instance of the FleeDecorator class
	 * @param blackboard Reference to the AI Blackboard data
	 * @param task Task we decorate
	 * @param name Name of the class, for debuging
	 */
	public FleeDecorator(Blackboard blackboard, Task task, String name) 
	{
		super(blackboard, task, name);
	}
	
	/**
	 * Creates a new instance of the FleeDecorator class
	 * @param blackboard Reference to the AI Blackboard data
	 * @param task Task we decorate
	 */
	public FleeDecorator(Blackboard blackboard, Task task) 
	{
		super(blackboard, task);
	}

	/**
	 * Executes the normal DoAction of the decorated task.
	 */
	@Override
	public void DoAction() 
	{
		task.DoAction();
	}
	
	/**
	 * Adds a fight prowess check to the conditions of the decorated task.
	 */
	@Override
	public boolean CheckConditions()
	{
		return super.CheckConditions() && bb.player.GetAverageFightRecord() < LOOSING_LOWER_BOUND;
	}
	

}
