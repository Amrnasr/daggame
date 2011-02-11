package com.game.AI;

import java.util.Vector;

import com.game.Preferences;
import com.game.Tile;
import com.game.Vec2;

public class GetClosestOwnedTileTask extends LeafTask {

	public GetClosestOwnedTileTask(Blackboard blackboard) 
	{
		super(blackboard);
	}

	public GetClosestOwnedTileTask(Blackboard blackboard, String name) 
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
		
		Vector< Tile > tiles =  bb.player.GetTiles();
		
		// Long vector
		Vec2 vecToClosestTile = new Vec2(Preferences.Get().mapWidth,Preferences.Get().mapWidth);
		Vec2 cursorPos = new Vec2(bb.player.GetCursor().GetPosition().X(),bb.player.GetCursor().GetPosition().Y());
		Tile closestTile = null;
		
		for(int i = 0; i < tiles.size(); i++)
		{
			Tile tile = tiles.elementAt(i);
			Vec2 dist = new Vec2(tile.GetRealPos().X()-cursorPos.X(),tile.GetRealPos().Y()-cursorPos.Y());
			
			if(dist.Length() < vecToClosestTile.Length())
			{
				closestTile = tile;
				vecToClosestTile = dist;
			}
		}
		
		if(closestTile == null)
		{
			control.FinishWithFailure();
		}
		else
		{
			bb.aStarData.initialTile = closestTile;
			bb.destination = closestTile.GetRealPos();
			bb.moveDirection = new Vec2(vecToClosestTile.X() + cursorPos.X(),vecToClosestTile.Y() + cursorPos.Y());
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
