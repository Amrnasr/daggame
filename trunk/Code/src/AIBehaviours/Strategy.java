package AIBehaviours;

import com.game.Player;
import com.game.Regulator;
import com.game.Scenes.PlayScene;

/**
 * Base class to encapsulate individual AI strategies.
 * @author Ying
 *
 */
public abstract class Strategy 
{
	/**
	 * Reference to the PlayScene
	 */
	protected PlayScene sceneRef;
	
	/**
	 * Reference to the player
	 */
	protected Player playerRef;
	
	/**
	 * Keeps a lid on the update speed.
	 */
	protected Regulator nextUpdate;
	
	/**
	 * Creates a new instance of the Strategy class
	 * @param sceneRef Reference to the PlayScene
	 * @param playerRef Reference to the current Player
	 */
	public Strategy(PlayScene sceneRef, Player playerRef, float updateSpeed)
	{
		this.sceneRef = sceneRef;
		this.playerRef = playerRef;
		this.nextUpdate = new Regulator(updateSpeed);
	}
	
	/**
	 * Abstract interface for the update function.
	 * Updates the strategy
	 */
	protected abstract void Update();	
	
	/**
	 * Updates the Strategy when the regulator is ready
	 */
	public void TimedUpdate()
	{
		if(nextUpdate.IsReady())
		{
			this.Update();
		}
	}
}
