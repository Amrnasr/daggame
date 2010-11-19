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

public class CircleStrategy extends Strategy 
{
	private Vector<Tile> path;
	private static final int pathLen = 10;
	
	public CircleStrategy(PlayScene sceneRef, Player playerRef)
	{
		super(sceneRef, playerRef, 0.1f);
	}
	
	@Override public void Start() 
	{
		// --- Calculate path to follow ---
		// Find closest enemy cursor
		Vec2 myPos = this.playerRef.GetCursor().GetPosition();
		Vector<Player> players = this.sceneRef.GetPlayers();
		Vec2 lineToEnemy = new Vec2(Preferences.Get().mapWidth*2, Preferences.Get().mapHeight*2);
		Player chosenEnemy = null;
		
		for(Iterator<Player> iter = players.iterator(); iter.hasNext();)
		{
			Player enemy = (Player) iter.next();
			Vec2 auxLineToEnemy = myPos.GetVectorTo(enemy.GetCursor().GetPosition());
			if(auxLineToEnemy.Length() < lineToEnemy.Length())
			{
				lineToEnemy = auxLineToEnemy;
				chosenEnemy = enemy;
			}
			
		}
		
		// Safety check
		if(chosenEnemy == null)
		{
			Log.e("CircleStrategy", "We're fucked. Did not find any enemy to circle");
		}
		
		// Get the last empty tile before the cursor density
		Map mapRef = sceneRef.GetMap();
		Tile curTile = mapRef.AtWorld(
				(int)chosenEnemy.GetCursor().GetPosition().X(), 
				(int)chosenEnemy.GetCursor().GetPosition().Y());
		
		lineToEnemy.Normalize();
		lineToEnemy.Scale(Constants.TileWidth);
		
		while( curTile != null && curTile.GetMaxCapacity() > 0 && !curTile.HasEnemyDensity(chosenEnemy.GetID()))
		{
			curTile = mapRef.AtWorld(
					(int)(curTile.GetRealPos().X()+lineToEnemy.X()), 
					(int)(curTile.GetRealPos().Y()+lineToEnemy.Y()));
		}
		
		// If no starting tile, we're done here.
		if(curTile == null)
		{
			done = true;
			return;
		}	
		
		// In the direction of the cursor, find the emptiest tile of the surrounding ones		
		path = new Vector<Tile>();
		path.add(curTile);		
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
				path.add(toAdd);
			}
			else
			{
				break;
			}
		}
	}
	
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

	@Override
	protected void Update() 
	{
		Log.i("CircleStrategy", "Updating. Path:  " + path.size());
		// If on the last tile, done
		if(path.size() > 0)
		{
			this.done = true;
			return;
		}
		
		// The next tile
		Tile curTile = path.firstElement();
		
		// If we are already in this tile, move to the next
		if(curTile.GetRealPos().Equals(this.playerRef.GetCursor().GetPosition()))
		{
			path.remove(curTile);
			
			if(path.size() > 0)
			{
				Tile objective = path.firstElement();
				Vec2 destination = this.playerRef.GetCursor().GetPosition().GetVectorTo(objective.GetRealPos());
				this.playerRef.GetCursor().MoveInDirection(destination);
			}
		}
	}

}
