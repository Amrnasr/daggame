package com.game.AI;

import java.util.Random;

public class ChanceDecorator extends TaskDecorator 
{
	/**
	 * Chance of this Task being chosen. 
	 * Range: ]0,100[
	 */
	private float chance;
	Random rand;

	public ChanceDecorator(Blackboard blackboard, Task task, float chance) {
		super(blackboard, task);
		Init(chance);
	}

	public ChanceDecorator(Blackboard blackboard, Task task, String name, float chance) {
		super(blackboard, task, name);
		Init(chance);
		
	}
	
	private void Init(float chance)
	{
		this.chance = chance;
		rand = new Random();
	}

	@Override
	public void DoAction() 
	{
		task.DoAction();
	}
	
	@Override
	public boolean CheckConditions()
	{
		float value = rand.nextFloat() % 100;
		return task.CheckConditions() && value < this.chance;
	}

}
