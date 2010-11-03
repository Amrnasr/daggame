package com.game.InputDevice;

import AIBehaviours.ChaseClosestCursorStrategy;
import AIBehaviours.Strategy;

import com.game.Preferences;
import com.game.Regulator;
import com.game.Vec2;
import com.game.Scenes.PlayScene;
/**
 * Input device from a AI. Outputs the expected results, but takes the stimulus from
 * internal logic, rather than user input.
 * 
 * @author Ying
 *
 */
public class AIInputDevice extends InputDevice 
{
	/**
	 * Regulator for keeping track of when we have to check if we change strategy.
	 */
	Regulator redecideRegulator;
	
	/**
	 * Reference to the PlayScene, to access data.
	 */
	PlayScene sceneRef;
	
	/**
	 * Current strategy the AIInputDevice is following. 
	 */
	Strategy currentStrategy;
	
	/**
	 * Creates a new instance of the AIInputDevice
	 * @param playScene Reference to the playScene
	 */
	public AIInputDevice(PlayScene playScene)
	{
		super(playScene);
		sceneRef = playScene;
		redecideRegulator = new Regulator(2f);
		currentStrategy = new ChaseClosestCursorStrategy(sceneRef, parent);
	}

	/**
	 * Start logic.
	 */
	@Override
	public void Start() 
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Update the AIInputDevice
	 */
	@Override
	public void Update() 
	{
		if(redecideRegulator.IsReady())
		{
			NaiveChaseClosestCursor();
		}
	}
	
	/**
	 * Checks if the current strategy is goign well, 
	 * if not, choose another one
	 */
	private void ChooseBestStrategy()
	{
		
	}
	
	/**
	 * The AI just tries to find the closest enemy and follows it's cursor
	 */
	private void NaiveChaseClosestCursor()
	{
		Vec2 cursorPos = parent.GetCursor().GetPosition();
		Vec2 enemyPos = null;
		Vec2 destination = new Vec2();
		
		Vec2 vecToEnemy = new Vec2();
		float minLenght = Preferences.Get().mapWidth*2; // Really large number
		for(int i= 0; i < sceneRef.GetPlayers().size(); i++)
		{
			if(sceneRef.GetPlayers().elementAt(i).GetID() != this.parent.GetID())
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
		
		this.parent.GetCursor().MoveInDirection(destination);
	}

}
