package com.game.AI;

import com.game.Vec2;

public class SetEnemyCursorAsDestinationTask extends Task 
{
	public SetEnemyCursorAsDestinationTask(Blackboard blackboard)
	{
		super(blackboard);
	}
	
	public SetEnemyCursorAsDestinationTask(Blackboard blackboard, String name)
	{
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return bb.closestEnemyCursor != null && bb.moveDirection != null;
	}

	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		Vec2 enemyPos = bb.closestEnemyCursor.GetPosition();
		Vec2 cursorPos = bb.player.GetCursor().GetPosition();
		bb.moveDirection = new Vec2(enemyPos.X()-cursorPos.X(), enemyPos.Y()-cursorPos.Y());
		bb.destination.Set(enemyPos.X(), enemyPos.Y());
		
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
