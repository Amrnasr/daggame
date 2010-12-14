package AIBehaviours;

import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

import com.game.Constants;
import com.game.Map;
import com.game.Player;
import com.game.Preferences;
import com.game.Tile;
import com.game.Vec2;
import com.game.Scenes.PlayScene;

/**
 * @deprecated
 * @see Task
 * Strategy for attacking players.
 * 
 * The player selects a path from his cursor to the enemy's cursor.
 * The path goes in a straight line until it reaches enemy density, in which case
 * it makes a roundabout around said density.
 *  
 * @author Ying
 *
 */
public class CircleStrategy extends Strategy 
{
	/**
	 * Path to follow when surrounding
	 */
	private Vector<Tile> path;
	
	/**
	 * Max length of the path we want
	 */
	private static final int pathLen = 10;
	
	/**
	 * Creates an instance of the CircleStrategy class
	 * @param sceneRef Reference to the PlayScene
	 * @param playerRef Reference to the parent Player
	 */
	public CircleStrategy(PlayScene sceneRef, Player playerRef)
	{
		super(sceneRef, playerRef, 1f);
	}
	
	/**
	 * Initializes variables and calculates the path to follow.
	 */
	@Override public void Start() 
	{
		// --- Calculate path to follow ---
		//Log.i("CircleStrategy", "START");
		// Find closest enemy cursor
		Vec2 myPos = this.playerRef.GetCursor().GetPosition();
		Vector<Player> players = this.sceneRef.GetPlayers();
		Vec2 lineToEnemy = new Vec2(Preferences.Get().mapWidth*2, Preferences.Get().mapHeight*2);
		Player chosenEnemy = null;
		//Log.i("CircleStrategy", "Start: Done initialization");
		
		for(Iterator<Player> iter = players.iterator(); iter.hasNext();)
		{
			Player enemy = (Player) iter.next();
			//Log.i("CircleStrategy", "Start: Loooking at enemy " + enemy.GetID());
			if(enemy.GetID() != this.playerRef.GetID())
			{
				Vec2 auxLineToEnemy = myPos.GetVectorTo(enemy.GetCursor().GetPosition());
				if(auxLineToEnemy.Length() < lineToEnemy.Length())
				{
					lineToEnemy = auxLineToEnemy;
					chosenEnemy = enemy;
				}
			}
			
		}
		
		// Safety check
		if(chosenEnemy == null)
		{
			Log.e("CircleStrategy", "We're fucked. Did not find any enemy to circle");
		}
		
		//Log.i("CircleStrategy", "Start: Selected enemy " + chosenEnemy.GetID());
		
		// Get the last empty tile before the cursor density
		Map mapRef = sceneRef.GetMap();
		Tile curTile = mapRef.AtWorld(
				(int)chosenEnemy.GetCursor().GetPosition().X(), 
				(int)chosenEnemy.GetCursor().GetPosition().Y());
		
		lineToEnemy.Normalize();
		lineToEnemy.Scale(Constants.TileWidth);
		
		// If no starting tile, we're done here.
		if(curTile == null)
		{
			done = true;
			Log.i("CircleStrategy", "Start: Not found initial tile, bailing out");
			return;
		}
		
		//curTile.GetPos().Print("CircleStrategy", "Start: Enemy cursor closest tile is ");
		//lineToEnemy.Print("CircleStrategy", "Start: Line to enemy is ");
		
		while( curTile != null && curTile.GetMaxCapacity() > 0 && !curTile.HasEnemyDensity(chosenEnemy.GetID()))
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
			done = true;
			Log.i("CircleStrategy", "Start: Not found initial tile, bailing out");
			return;
		}	
		
		//curTile.GetPos().Print("CircleStrategy", "Start: Initial selected tile is ");
		
		// In the direction of the cursor, find the emptiest tile of the surrounding ones		
		path = new Vector<Tile>();
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
				Tile next = GetNeighbour(curTile, j);
				if(next != null)
				{
					if(toAdd == null)
					{
						if(next.GetCurrentCapacity() > 0)
						{
							if(NotInPath(next))
							{
								toAdd = next;
							}
						}
					}
					else
					{
						if(next.GetCurrentCapacity() > toAdd.GetCurrentCapacity())
						{
							if(NotInPath(next))
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
		
		//Log.i("CircleStrategy", "Start: Starting at the first tile now");
		// Start us off to the first tile.
		Tile objective = path.firstElement();
		Vec2 destination = this.playerRef.GetCursor().GetPosition().GetVectorTo(objective.GetRealPos());
		this.playerRef.GetCursor().MoveInDirection(destination);
		//Log.i("CircleStrategy", "/START");
	}
	
	/**
	 * Checks if a said tile is not in the path so far
	 * @param tile Tile to check against.
	 * @return True if it's not on the path, false if it is
	 */
	private boolean NotInPath(Tile tile)
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
	private Tile GetNeighbour(Tile curTile, int dir)
	{
		Tile neighbour = null;
		
		switch (dir) 
		{
		case 0:
			neighbour = sceneRef.GetMap().AtTile((int)(curTile.GetPos().X()), (int)(curTile.GetPos().Y() - 1));
			break;
		case 1:
			neighbour = sceneRef.GetMap().AtTile((int)(curTile.GetPos().X() + 1), (int)(curTile.GetPos().Y() - 1));
			break;
		case 2:
			neighbour = sceneRef.GetMap().AtTile((int)(curTile.GetPos().X() + 1), (int)(curTile.GetPos().Y()));
			break;
		case 3:
			neighbour = sceneRef.GetMap().AtTile((int)(curTile.GetPos().X() + 1), (int)(curTile.GetPos().Y() + 1));
			break;
		case 4:
			neighbour = sceneRef.GetMap().AtTile((int)(curTile.GetPos().X()), (int)(curTile.GetPos().Y() + 1));
			break;
		case 5:
			neighbour = sceneRef.GetMap().AtTile((int)(curTile.GetPos().X() - 1), (int)(curTile.GetPos().Y() + 1));
			break;
		case 6:
			neighbour = sceneRef.GetMap().AtTile((int)(curTile.GetPos().X() - 1), (int)(curTile.GetPos().Y()));
			break;
		case 7:
			neighbour = sceneRef.GetMap().AtTile((int)(curTile.GetPos().X() - 1), (int)(curTile.GetPos().Y() - 1));
			break;
		case 8:
			neighbour = sceneRef.GetMap().AtTile((int)(curTile.GetPos().X()), (int)(curTile.GetPos().Y()));
			break;

		default:
			Log.e("CircleStrategy", "Requested direction invalid motherfcuker: dir: " + dir);
			break;
		}
		
		return neighbour;
	}

	/**
	 * Makes the Player Cursor follow the path of tiles previouslly calculated.
	 */
	@Override
	protected void Update() 
	{
		//Log.i("CircleStrategy", "Updating. Path:  " + path.size());
		PrintPath();
		// If on the last tile, done
		if(path.size() <= 0)
		{
			this.done = true;
			return;
		}
		
		// The next tile
		Tile curTile = path.firstElement();
		
		this.playerRef.GetCursor().GetPosition().Print("CircleStrategy", "- Tile: ");
		Log.i("CircleStrategy", "At the tile? " + (curTile.GetRealPos().RoundEqual(this.playerRef.GetCursor().GetPosition())));
		
		// If we are already in this tile, move to the next
		if(curTile.GetRealPos().RoundEqual(this.playerRef.GetCursor().GetPosition()))
		{
			path.remove(curTile);
			
			if(path.size() > 0)
			{
				Tile objective = path.firstElement();
				Vec2 destination = this.playerRef.GetCursor().GetPosition().GetVectorTo(objective.GetRealPos().GetIntValue());
				this.playerRef.GetCursor().MoveInDirection(destination);
			}
		}
	}
	
	/**
	 * For debug use, prints the path to log.
	 */
	protected void PrintPath()
	{
		Log.i("CircleStrategy", "Path: ---- " + path.size());
		for (Iterator<Tile> iterator = path.iterator(); iterator.hasNext();) 
		{
			Tile type = (Tile) iterator.next();
			type.GetRealPos().Print("CircleStrategy", "- Tile: ");
			
		}
	}

}
