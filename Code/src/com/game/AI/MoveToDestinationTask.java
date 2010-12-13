package com.game.AI;



public class MoveToDestinationTask extends Task 
{
	public MoveToDestinationTask(Blackboard blackboard)
	{
		super(blackboard);
	}
	
	public MoveToDestinationTask(Blackboard blackboard, String name)
	{
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return bb.moveDirection != null;
	}

	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		bb.player.GetCursor().MoveInDirection(bb.moveDirection);
		this.FinishWithSuccess();
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
