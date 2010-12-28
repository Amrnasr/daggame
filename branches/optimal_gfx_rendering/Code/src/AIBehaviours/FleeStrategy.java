package AIBehaviours;

import com.game.Player;
import com.game.Vec2;
import com.game.Scenes.PlayScene;

/**
 * Trivial flee strategy
 * @deprecated
 * @see Task
 * @author Ying
 *
 */
public class FleeStrategy extends Strategy 
{
	/**
	 * Distance to run in case of fleeing
	 */
	private final int FleeDistance = 80;
	
	/**
	 * Radius to check for danger
	 */
	private final int DangerRadius = 100;
	
	/**
	 * Creates a new FleeStrategy
	 * @param sceneRef Reference to the current PlayScene
	 * @param playerRef Reference to the parent Player
	 */
	public FleeStrategy(PlayScene sceneRef, Player playerRef)
	{
		super(sceneRef, playerRef, 1f);
	}
	
	@Override public void Start() 
	{}

	/**
	 * Updates the strategy
	 */
	@Override protected void Update() 
	{
		Vec2 destination = new Vec2();		
		Vec2 vecToEnemy = new Vec2();
		Vec2 cursorPos = playerRef.GetCursor().GetPosition();
	
		// Get a vector away from all cursors 
		for(int i= 0; i < sceneRef.GetPlayers().size(); i++)
		{
			if(sceneRef.GetPlayers().elementAt(i).GetID() != this.playerRef.GetID())
			{
				Vec2 enemyPos = sceneRef.GetPlayers().elementAt(i).GetCursor().GetPosition();
				vecToEnemy.Set(cursorPos.X()-enemyPos.X(), cursorPos.Y()-enemyPos.Y());
				
				if(vecToEnemy.Length() < this.DangerRadius)
				{
					destination.Add(vecToEnemy);
				}
			}			
		}
		
		if(destination.Length() > 0)
		{
			// We've got some direction to run to
			// Set a specific distance to flee along that vector
			destination.Normalize();
			destination.Scale(FleeDistance);
			
			this.playerRef.GetCursor().MoveInDirection(destination);
		}
	}

}
