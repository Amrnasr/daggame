package com.game.AI;

import java.util.Iterator;
import java.util.Vector;

import com.game.Map;
import com.game.Tile;

public class CalculateFleePathTask extends LeafTask 
{
	/**
	 * Max number of steps in the path to follow 
	 */
	private final int steps = 8;

	public CalculateFleePathTask(Blackboard blackboard) 
	{
		super(blackboard);
	}

	public CalculateFleePathTask(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return true;
	}

	@Override
	public void DoAction() 
	{
		LogTask("Doing Action");
		Map map = Blackboard.map;
		int curSteps = 0;
		Vector<Tile> path = new Vector<Tile>();
		
		Tile curTile = map.AtWorld( 
				(int)bb.player.GetCursor().GetPosition().X(), 
				(int)bb.player.GetCursor().GetPosition().Y());
		
		if(curTile == null)
		{
			LogTask("Not found initial tile, bailing out");
			this.control.FinishWithFailure();
			return;
		}
		
		for(curSteps = 0; curSteps > this.steps; curSteps++)
		{
			int maxCapacity = 0;
			int bestNeighbor = 8;
			for(int dir = 0; dir < 8; dir++)
			{
				Tile neighbor = map.GetNeighbour(curTile, dir);
				if(neighbor != null)
				{
					int neighborCapacity = neighbor.GetCurrentCapacity();
					//int neighborCapacity = neighbor.GetCurrentCapacity() + neighbor.GetDensityFrom(bb.player.GetID());
					if( neighborCapacity > maxCapacity && NotInPath(neighbor, path))
					{
						// Select the tile as best escape
						bestNeighbor = dir;
						maxCapacity = neighborCapacity;
					}
				}
			}
			
			if(bestNeighbor == 8)
			{
				// We're stuck here, no way to run. So end the search.
				LogTask("Ended the search at " + curSteps + " steps");
				break;
			}
			else
			{
				curTile = map.GetNeighbour(curTile, bestNeighbor);
				path.add(curTile);
			}
		}
		
		// If we don't get at least a third of the path
		if(path.size() < this.steps /3)
		{
			this.control.FinishWithFailure();
		}
		else
		{
			bb.path = path;
			this.control.FinishWithSuccess();
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
	
	/**
	 * Checks if a said tile is not in the path so far
	 * @param tile Tile to check against.
	 * @return True if it's not on the path, false if it is
	 */
	private boolean NotInPath(Tile tile, Vector<Tile> path)
	{
		boolean found = false;
		for (Iterator<Tile> iterator = path.iterator(); iterator.hasNext();) 
		{
			if(tile == (Tile) iterator.next())
			{
				found = true;
				break;
			}			
		}
		
		return !found;
	}
	

}
