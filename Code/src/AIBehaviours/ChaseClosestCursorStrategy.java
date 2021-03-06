package AIBehaviours;

import com.game.Player;
import com.game.Preferences;
import com.game.Vec2;
import com.game.Scenes.PlayScene;

/**
 * Strategy that chases the closest cursor
 * @deprecated
 * @see Task
 * @author Ying
 *
 */
public class ChaseClosestCursorStrategy extends Strategy 
{
	/**
	 * Creates a new instance of the ChaseClosestCursorStrategy
	 * @param sceneRef Reference to the PlayScene
	 * @param playerRef Reference to the Player
	 */
	public ChaseClosestCursorStrategy(PlayScene sceneRef, Player playerRef)
	{
		super(sceneRef,playerRef,0.5f);
	}
	
	@Override public void Start() 
	{}
	
	/**
	 * Updates the strategy
	 */
	@Override protected void Update() 
	{
		Vec2 cursorPos = playerRef.GetCursor().GetPosition();
		Vec2 enemyPos = null;
		Vec2 destination = new Vec2();		
		Vec2 vecToEnemy = new Vec2();
		
		float minLenght = Preferences.Get().mapWidth*2; // Really large number
		for(int i= 0; i < sceneRef.GetPlayers().size(); i++)
		{
			if(sceneRef.GetPlayers().elementAt(i).GetID() != this.playerRef.GetID())
			{
				if(sceneRef.GetPlayers().elementAt(i).GetTotalDensity() > 0)
				{
					enemyPos = sceneRef.GetPlayers().elementAt(i).GetCursor().GetPosition();
					vecToEnemy.Set(enemyPos.X()-cursorPos.X(), enemyPos.Y()-cursorPos.Y());
					if(vecToEnemy.Length() < minLenght)
					{
						minLenght = (float) vecToEnemy.Length();
						destination.Set(vecToEnemy.X(), vecToEnemy.Y());
					}
				}
			}			
		}
		
		this.playerRef.GetCursor().MoveInDirection(destination);
	}

}
