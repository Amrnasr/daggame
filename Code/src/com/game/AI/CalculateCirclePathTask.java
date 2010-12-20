package com.game.AI;

import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

import com.game.Constants;
import com.game.Cursor;
import com.game.Map;
import com.game.Tile;
import com.game.Vec2;

public class CalculateCirclePathTask extends LeafTask 
{
	/**
	 * Max length of the path we want
	 */
	private static final int pathLen = 10;

	public CalculateCirclePathTask(Blackboard blackboard) 
	{
		super(blackboard);
	}

	public CalculateCirclePathTask(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions()
	{
		LogTask("Checking conditions");
		return bb.path != null && bb.closestEnemyCursor != null;
	}

	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		
		
		Cursor chosenEnemy = bb.closestEnemyCursor;
		Vec2 lineToEnemy = bb.player.GetCursor().GetPosition().GetVectorTo(chosenEnemy.GetPosition());
		
		//Log.i("CircleStrategy", "Start: Selected enemy " + chosenEnemy.GetID());
		
		// Get the last empty tile before the cursor density
		Map mapRef = Blackboard.map;
		Tile curTile = mapRef.AtWorld(
				(int)chosenEnemy.GetPosition().X(), 
				(int)chosenEnemy.GetPosition().Y());
		
		lineToEnemy.Normalize();
		lineToEnemy.Scale(Constants.TileWidth);
		
		// If no starting tile, we're done here.
		if(curTile == null)
		{
			LogTask("Not found initial tile, bailing out");
			this.control.FinishWithFailure();
			return;
		}
		
		//curTile.GetPos().Print("CircleStrategy", "Start: Enemy cursor closest tile is ");
		//lineToEnemy.Print("CircleStrategy", "Start: Line to enemy is ");
		
		while( curTile != null && curTile.GetMaxCapacity() > 0 && !curTile.HasEnemyDensity(chosenEnemy.GetPlayer().GetID()))
		{
			//curTile.GetPos().Print("CircleStrategy", "Start: Checking tile ");
			
			int x = (int)Math.round((curTile.GetRealPos().X()-lineToEnemy.X() ));
			int y = (int)Math.round((curTile.GetRealPos().Y()-lineToEnemy.Y()) );
			
			//curTile.GetRealPos().Print("CircleStrategy", "Start: (" + x+", " +y +") Checking tile ");
			Tile nextTile = mapRef.AtWorld( x , y);
			
			// To avoid infinite loops caused by float precision, if we're stuck on the same tile twice
			// Just jump to the closest in that direction.
			if(curTile == nextTile)
			{
				curTile = mapRef.AtWorld( 
						(int)(curTile.GetRealPos().X()-(Math.signum(lineToEnemy.X()) * Constants.TileWidth) ) , 
						(int)(curTile.GetRealPos().Y()-(Math.signum(lineToEnemy.Y()) * Constants.TileWidth) ));
			}
			else
			{
				curTile = nextTile;
			}
					
		}
		//Log.i("CircleStrategy", "Start: Finished looking for initial tile");
		
		
		// If no starting tile, we're done here.
		if(curTile == null)
		{
			LogTask("Not found initial tile, bailing out");
			this.control.FinishWithFailure();
			return;
		}	
		
		//curTile.GetPos().Print("CircleStrategy", "Start: Initial selected tile is ");
		
		// In the direction of the cursor, find the emptiest tile of the surrounding ones		
		Vector<Tile> path= new Vector<Tile>();
		path.add(curTile);		
		//Log.i("CircleStrategy", "Start: Looking for tile path");
		for(int i = 0; i < pathLen; i++)
		{		
			Tile toAdd = null;
			curTile = mapRef.AtWorld(
					(int)(path.lastElement().GetRealPos().X()+lineToEnemy.X()), 
					(int)(path.lastElement().GetRealPos().Y()+lineToEnemy.Y()));
			
			for(int j = 0; j < 9; j++)
			{
				Tile next = GetNeighbour(curTile, j, Blackboard.map);
				if(next != null)
				{
					if(toAdd == null)
					{
						if(next.GetCurrentCapacity() > 0)
						{
							if(NotInPath(next,path))
							{
								toAdd = next;
							}
						}
					}
					else
					{
						if(next.GetCurrentCapacity() > toAdd.GetCurrentCapacity())
						{
							if(NotInPath(next,path))
							{
								toAdd = next;
							}
						}
					}
				}
			}
			
			if(toAdd != null)
			{
				//toAdd.GetPos().Print("CircleStrategy", "Start: -- Adding tile ");
				path.add(toAdd);
			}
			else
			{
				//Log.i("CircleStrategy", "Start: No more tiles to add. Path ended");
				break;
			}
		}
		
		bb.path = path;
		this.control.FinishWithSuccess();
		
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
	
	/**
	 * Gets the neighboring tile to a tile in a specific direction:
	 * 
	 * |7|0|1|
	 * |6|8|2|
	 * |5|4|3|
	 * 
	 * Where 8 is the current tile.
	 * 
	 * @param curTile Tile at the center. We search around this tile for neighbors
	 * @param dir Direction to search on. Accepted values ]0,8[
	 * @return The neighboring tile to curTile in the specified direction.
	 */
	private Tile GetNeighbour(Tile curTile, int dir, Map map)
	{
		Tile neighbour = null;
		
		switch (dir) 
		{
		case 0:
			neighbour = map.AtTile((int)(curTile.GetPos().X()), (int)(curTile.GetPos().Y() - 1));
			break;
		case 1:
			neighbour = map.AtTile((int)(curTile.GetPos().X() + 1), (int)(curTile.GetPos().Y() - 1));
			break;
		case 2:
			neighbour = map.AtTile((int)(curTile.GetPos().X() + 1), (int)(curTile.GetPos().Y()));
			break;
		case 3:
			neighbour = map.AtTile((int)(curTile.GetPos().X() + 1), (int)(curTile.GetPos().Y() + 1));
			break;
		case 4:
			neighbour = map.AtTile((int)(curTile.GetPos().X()), (int)(curTile.GetPos().Y() + 1));
			break;
		case 5:
			neighbour = map.AtTile((int)(curTile.GetPos().X() - 1), (int)(curTile.GetPos().Y() + 1));
			break;
		case 6:
			neighbour = map.AtTile((int)(curTile.GetPos().X() - 1), (int)(curTile.GetPos().Y()));
			break;
		case 7:
			neighbour = map.AtTile((int)(curTile.GetPos().X() - 1), (int)(curTile.GetPos().Y() - 1));
			break;
		case 8:
			neighbour = map.AtTile((int)(curTile.GetPos().X()), (int)(curTile.GetPos().Y()));
			break;

		default:
			Log.e("CircleStrategy", "Requested direction invalid motherfcuker: dir: " + dir);
			break;
		}
		
		return neighbour;
	}

}
