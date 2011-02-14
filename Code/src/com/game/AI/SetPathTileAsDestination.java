package com.game.AI;

import com.game.Tile;

/**
 * Takes from the Blackboard the first Tile of the path
 * and sets it as the destination.
 * 
 * @author Ying
 *
 */
public class SetPathTileAsDestination extends LeafTask 
{
	/**
	 * Creates a new instance of the SetPathTileAsDestination class
	 * @param blackboard Reference to the AI Blackboard data
	 */
	public SetPathTileAsDestination(Blackboard blackboard) 
	{
		super(blackboard);
	}

	/**
	 * Creates a new instance of the SetPathTileAsDestination class
	 * @param blackboard Reference to the AI Blackboard data
	 * @param name Name of the class, for debug purposes
	 */
	public SetPathTileAsDestination(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	/**
	 * Makes sure all the data we need exists
	 */
	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return bb.path != null && bb.path.size() > 0;
	}

	/**
	 * Sets the first Tile of the path as the destination
	 * in the Blackboard. Removes the first Tile.
	 */
	@Override
	public void DoAction() 
	{
		LogTask("Doing action: Tiles in path: " + bb.path.size());
		Tile objective = bb.path.firstElement();
		bb.destination = objective.GetRealPos().GetIntValue();
		bb.moveDirection = bb.player.GetCursor().GetPosition().GetVectorTo(objective.GetRealPos().GetIntValue());
		bb.path.remove(bb.path.firstElement());
		this.control.FinishWithSuccess();
	}

	/**
	 * Ends the task
	 */
	@Override
	public void End() 
	{
		LogTask("Ending");
	}

	/**
	 * Starts the task
	 */
	@Override
	public void Start() 
	{
		LogTask("Starting");
	}
}
