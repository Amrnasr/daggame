package com.game.battleofpixels.AI;

import java.util.Vector;

import android.util.Log;

import com.game.battleofpixels.Preferences;
import com.game.battleofpixels.Tile;

public class AStarSearchTask extends LeafTask 
{
	private final long maxMilisecondsAllowed  = 3;
	public AStarSearchTask(Blackboard blackboard) {
		super(blackboard);
	}

	public AStarSearchTask(Blackboard blackboard, String name) {
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking Conditions");
		return bb.aStarData != null;
	}

	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		int curIter = 0;
		long startTime = System.currentTimeMillis();
		
		while(bb.aStarData.openSet.size() != 0 && (System.currentTimeMillis() - startTime) < maxMilisecondsAllowed) // curIter < numberIterations
		{
			Tile curTile = GetLowestFTile();
			
			// If we found a tile with density, that will do as well :D
			if(curTile.HasEnemyDensity(bb.player.GetID()))
			{
				//bb.aStarData.destinationTile = curTile; // HAS BUG (propagates to PopulatePath)
			}
			
			if(curTile.Index() == bb.aStarData.destinationTile.Index() )
			{
				PopulatePath();
				bb.aStarData.done = true;
				LogTask("Created path of " + bb.path.size() + " steps");
				control.FinishWithSuccess();
				return;
			}
			
			bb.aStarData.openSet.remove(curTile);
			bb.aStarData.closedSet.add(curTile);
			
			for(int i = 0; i < 8; i++)
			{
				Tile neighTile = Blackboard.map.GetNeighbour(curTile, i);
				if(neighTile != null && !bb.aStarData.closedSet.contains(neighTile) && neighTile.GetMaxCapacity() > 0)
				{
					float tentativeGScore = bb.aStarData.gCosts[curTile.Index()] + 1; // gCost(cur) + dist(cur,neigh)
					boolean tentativeIsBetter = false;
					
					if(!bb.aStarData.openSet.contains(neighTile))
					{
						bb.aStarData.openSet.add(neighTile);
						tentativeIsBetter = true;
					}
					else if(tentativeGScore < bb.aStarData.gCosts[neighTile.Index()])
					{
						tentativeIsBetter = true;
					}
					
					if(tentativeIsBetter)
					{
						bb.aStarData.cameFrom[neighTile.Index()] = curTile.Index();
						
						bb.aStarData.gCosts[neighTile.Index()] = tentativeGScore;
						bb.aStarData.hCosts[neighTile.Index()] = ManhatanDistance(neighTile.Index());
						bb.aStarData.fCosts[neighTile.Index()] = 	bb.aStarData.gCosts[neighTile.Index()] + 
																	bb.aStarData.hCosts[neighTile.Index()];
					}
				}				
			}
			
			// One more iteration
			curIter++;
		}
		
		if(System.currentTimeMillis() - startTime < maxMilisecondsAllowed/2 && bb.aStarData.openSet.size() == 0)
		{
			// If we got here, found no path
			bb.aStarData.done = true;
			control.FinishWithFailure();
		}
		else
		{
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
		bb.aStarData.openSet.add(bb.aStarData.initialTile);
		int index = bb.aStarData.initialTile.Index();
		//Log.i("AStarSearchTask", "DesiredIndex: " + index + " gCost length: " + bb.aStarData.gCosts.length);
		bb.aStarData.gCosts[index] = 0;
		bb.aStarData.hCosts[index] = ManhatanDistance(index);
		bb.aStarData.fCosts[index] = bb.aStarData.gCosts[index] + bb.aStarData.hCosts[index];
		
		bb.path.clear();
	}
	
	/**
	 * Heuristic function for calculating the distance.
	 * Manhatan function calculates the number of tiles horizontal and vertical and returns the sum.
	 * @param index Index of the tile to calculate the distance from to the destination Tile
	 * @return The Manhatan distance between both tiles
	 */
	private int ManhatanDistance(int index)
	{
		int x = (int) (bb.aStarData.destinationTile.GetPos().X() - Blackboard.map.getTileMap().elementAt(index).GetPos().X());
		int y = (int) (bb.aStarData.destinationTile.GetPos().Y() - Blackboard.map.getTileMap().elementAt(index).GetPos().Y());
		return x + y;
	}
	
	/**
	 * Gets, out of all the tiles in the openset, the one with the lowest fCost.
	 * @return The index of the tile.
	 */
	private Tile GetLowestFTile()
	{
		Vector<Tile> openSet = bb.aStarData.openSet;
		float lowestF = Preferences.Get().mapWidth * Preferences.Get().mapWidth; // Really high number
		Tile lowestIndexTile = null;
		for(int i = 0; i < openSet.size(); i++)
		{
			int index = openSet.elementAt(i).Index();
			if(bb.aStarData.fCosts[index] < lowestF)
			{
				lowestF = bb.aStarData.fCosts[index] ;
				lowestIndexTile = openSet.elementAt(i);
			}
		}
		
		if(lowestIndexTile == null)
		{
			Log.e("AStarSearchTask", "Not found lower F values than top value");
		}
		
		return lowestIndexTile;
	}

	/**
	 * Generates the path and stores it in the blackboard
	 */
	private void PopulatePath()
	{
		boolean finished = false;
		Vector<Integer> indexes = new Vector<Integer>();
		int cur = bb.aStarData.cameFrom[bb.aStarData.destinationTile.Index()];
		
		// Gen path
		while(!finished)
		{
			indexes.add(cur);
			
			assert (cur > 0 && cur < bb.aStarData.cameFrom.length) : "Cur == " + cur + " Index out of bounds!"; 
			cur = bb.aStarData.cameFrom[cur];
			
			if(cur == bb.aStarData.initialTile.Index())
			{
				indexes.add(cur);
				finished = true;
			}
			if(cur == -1)
			{
				Log.e("AStarSearchTask"," Populate path broken!");
			}
		}
		
		// Flip it into bb.path
		
		for(int i = indexes.size()-1; i >= 0; i--)
		{
			bb.path.add(Blackboard.map.getTileMap().elementAt(indexes.elementAt(i)));
		}
	}
}
