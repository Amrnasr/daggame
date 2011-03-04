package com.game.battleofpixels.AI;

import java.util.Vector;

import com.game.battleofpixels.Player;
import com.game.battleofpixels.Tile;

/**
 * Finds a tile with density, any tile and any density, as long as it's from another player.
 * @author Ying
 *
 */
public class FindEnemyDensityTask extends LeafTask 
{

	public FindEnemyDensityTask(Blackboard blackboard) 
	{
		super(blackboard);
	}

	public FindEnemyDensityTask(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return bb.aStarData != null;
	}

	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		Vector<Player> players = Blackboard.players;
		Tile destination = null;
		
		for(int i = 0; i < players.size(); i++)
		{
			Player player = players.elementAt(i);
			if(player.GetID() != bb.player.GetID())
			{
				Vector<Tile> tiles = player.GetTiles();
				if(tiles.size() != 0)
				{
					destination = tiles.firstElement();
					break;
				}
			}
		}
		
		if(destination == null)
		{
			control.FinishWithFailure();
		}
		else
		{
			bb.aStarData.destinationTile = destination;
			control.FinishWithSuccess();
		}
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
