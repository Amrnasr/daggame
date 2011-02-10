package com.game.AI;

import java.util.Iterator;
import java.util.Vector;

import com.game.Camera;
import com.game.Constants;
import com.game.Cursor;
import com.game.Map;
import com.game.Preferences;
import com.game.Tile;
import com.game.Vec2;

/**
 * Task to calculate a path along the border of the nearest enemy
 * army.
 * @author Ying
 *
 */
public class CalculateCirclePathTask extends LeafTask 
{
	/**
	 * Max length of the path we want
	 */
	private static final int pathLen = 10;
	
	/**
	 * Minimum distance at which we attempt to circle
	 */
	private static final float minDistance = Preferences.Get().mapHeight/10;

	/**
	 * Creates a new instance of the CalculateCirclePathTask class 
	 * @param blackboard Reference to the AI Blackboard data
	 */
	public CalculateCirclePathTask(Blackboard blackboard) 
	{
		super(blackboard);
	}

	/**
	 * Creates a new instance of the CalculateCirclePathTask class 
	 * @param blackboard Reference to the AI Blackboard data
	 * @param name Name of the class, used for debugging
	 */
	public CalculateCirclePathTask(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	/**
	 * Confirms the data we need exists and that we are not
	 * too far away to circle.
	 */
	@Override
	public boolean CheckConditions()
	{
		LogTask("Checking conditions");
		if(bb.path == null && bb.closestEnemyCursor == null)
		{
			return false;
		}
		
		float distanceToEnemy = (float) bb.player.GetCursor().GetPosition().GetVectorTo(bb.closestEnemyCursor.GetPosition()).Length();
		if(distanceToEnemy > minDistance)
		{
			return false;
		}
		
		return true;
	}

	/**
	 * Calculates a path of tiles around the enemy army
	 * and stores it in the Blackboard
	 */
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
		lineToEnemy.Scale(Preferences.Get().tileWidth);
		
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
						(int)(curTile.GetRealPos().X()-(Math.signum(lineToEnemy.X()) * Preferences.Get().tileWidth) ) , 
						(int)(curTile.GetRealPos().Y()-(Math.signum(lineToEnemy.Y()) * Preferences.Get().tileWidth) ));
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
				Tile next = Blackboard.map.GetNeighbour(curTile, j);
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
