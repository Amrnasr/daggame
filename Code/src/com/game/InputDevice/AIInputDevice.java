package com.game.InputDevice;

import AIBehaviours.ChaseClosestCursorStrategy;
import AIBehaviours.FleeStrategy;
import AIBehaviours.Strategy;
import android.util.Log;

import com.game.Regulator;
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
		currentStrategy = null;
	}

	/**
	 * Start logic.
	 */
	@Override
	public void Start() 
	{
		currentStrategy = new ChaseClosestCursorStrategy(sceneRef, parent);
	}

	/**
	 * Update the AIInputDevice
	 */
	@Override public void Update() 
	{
		if(parent.GetTotalDensity() <= 0)
		{
			// This AI has lost, nothing else to do here.
			return;
		}
		
		if(redecideRegulator.IsReady())
		{
			ChooseBestStrategy();
		}
		
		currentStrategy.TimedUpdate();
	}
	
	/**
	 * Checks if the current strategy is going well, 
	 * if not, choose another one
	 */
	private void ChooseBestStrategy()
	{
		float averageFightRecord = parent.GetAverageFightRecord();
		String decided = new String("doing the same");
		if(averageFightRecord >= 1)
		{
			// Wining streak
			if(currentStrategy.getClass() != ChaseClosestCursorStrategy.class)
			{
				currentStrategy = new ChaseClosestCursorStrategy(sceneRef, parent);
				decided = new String("chasing");
			}
		}
		else
		{
			// Loosing here!
			if(currentStrategy.getClass() != FleeStrategy.class)
			{
				currentStrategy = new FleeStrategy(sceneRef, parent);
				decided = new String("fleeing");
			}
		}
		
		Log.i("Player " + parent.GetID(), "Is "+ decided+"! AvFiRe: " + averageFightRecord);
	}

}
