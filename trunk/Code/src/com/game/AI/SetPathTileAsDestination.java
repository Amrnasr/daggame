package com.game.AI;

import com.game.Tile;

public class SetPathTileAsDestination extends LeafTask {

	public SetPathTileAsDestination(Blackboard blackboard) 
	{
		super(blackboard);
	}

	public SetPathTileAsDestination(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return bb.path != null && bb.path.size() > 0;
	}

	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		Tile objective = bb.path.firstElement();
		bb.destination = objective.GetRealPos().GetIntValue();
		bb.moveDirection = bb.player.GetCursor().GetPosition().GetVectorTo(objective.GetRealPos().GetIntValue());
		bb.path.remove(bb.path.firstElement());
		this.control.FinishWithSuccess();
	}

	@Override
	public void End() {
		LogTask("Ending");

	}

	@Override
	public void Start() 
	{
		LogTask("Starting");
	}
}
