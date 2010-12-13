package com.game.InputDevice;

import java.util.Random;

import AIBehaviours.ChaseClosestCursorStrategy;
import AIBehaviours.CircleStrategy;
import AIBehaviours.FleeStrategy;
import AIBehaviours.Strategy;
import android.util.Log;

import com.game.Player;
import com.game.Regulator;
import com.game.AI.Blackboard;
import com.game.AI.CalculateFleeDestinationTask;
import com.game.AI.GetClosestEnemyCursorTask;
import com.game.AI.MoveToDestinationTask;
import com.game.AI.Selector;
import com.game.AI.Sequence;
import com.game.AI.SetEnemyCursorAsDestinationTask;
import com.game.AI.Task;
import com.game.AI.WaitTillNearDestinationTask;
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
	private Regulator redecideRegulator;
	
	/**
	 * Reference to the PlayScene, to access data.
	 */
	private PlayScene sceneRef;
	
	/**
	 * Current strategy the AIInputDevice is following. 
	 */
	//private Strategy currentStrategy;
	
	/**
	 * Used for balancing the probability for the two different kinds of attack.
	 */
	private int winningAlpha;
	
	private Selector planner;
	
	private Blackboard blackboard;
	/**
	 * Creates a new instance of the AIInputDevice
	 * @param playScene Reference to the playScene
	 */
	public AIInputDevice(PlayScene playScene)
	{
		super(playScene);
		sceneRef = playScene;
		redecideRegulator = new Regulator(2f);
		//currentStrategy = null;
		winningAlpha = 0;
		
		// Set AI the blackboard data.
		blackboard = new Blackboard();			
		
		CreateBehaviourTree();
	}
	
	private void CreateBehaviourTree()
	{
		this.planner = new Selector(blackboard, "Planner");
		
		Sequence chase = new Sequence(blackboard, "Chase sequence");
		chase.Add(new GetClosestEnemyCursorTask(blackboard, "GetClosestEnemyCursor"));
		chase.Add(new SetEnemyCursorAsDestinationTask(blackboard, "SetEnemyCursorAsDestination"));
		chase.Add(new MoveToDestinationTask(blackboard, "MoveToDestination"));
		chase.Add(new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestination"));
		
		Sequence flee = new Sequence(blackboard, "Flee sequence");
		flee.Add(new CalculateFleeDestinationTask(blackboard, "CalculateFleeDestination"));
		flee.Add(new MoveToDestinationTask(blackboard, "MoveToDestination"));
		flee.Add(new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestination"));
		
		this.planner.Add(chase);
		this.planner.Add(flee);
	}
	
	public void SetParent(Player parent)
	{
		super.SetParent(parent);
		blackboard.player = parent;	
	}

	/**
	 * Start logic.
	 */
	@Override
	public void Start() 
	{
		//currentStrategy = new ChaseClosestCursorStrategy(sceneRef, parent);
		this.planner.SafeStart();
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
		
		this.planner.DoAction();
		
		/*
		
		if(redecideRegulator.IsReady())
		{
			ChooseBestStrategy();
		}
		
		UpdateStrategy();
		*/
	}
	
	/*
	private void UpdateStrategy()
	{
		if(currentStrategy != null)
		{
			if(currentStrategy.Done())
			{
				currentStrategy = null;
			}
			else
			{
				currentStrategy.SafeUpdate();
			}
		}
	}
	*/
	/**
	 * Checks if the current strategy is going well, 
	 * if not, choose another one
	 */
	/*
	private void ChooseBestStrategy()
	{
		float averageFightRecord = parent.GetAverageFightRecord();
		String decided = new String("doing the same");
		
		Strategy newStrategy = null;
		
		if(averageFightRecord >= 1)
		{

			// Wining streak
			Random rand = new Random();		
			int choose = rand.nextInt() % 100;
			if(choose < (100 - winningAlpha))//if(choose > (100 ))// TODO: Testing... if(choose < (100 - winningAlpha))
			{
				newStrategy = new ChaseClosestCursorStrategy(sceneRef, parent);
				decided = new String("chasing");
			}
			else
			{
				newStrategy = new CircleStrategy(sceneRef, parent);
				decided = new String("circling");
			}
			
			winningAlpha+=3;
			if(winningAlpha > 99)
			{
				winningAlpha = 0;
			}
		}
		else
		{
			// Loosing here!

			newStrategy = new FleeStrategy(sceneRef, parent);
			decided = new String("fleeing");
		}
		
		if(currentStrategy == null)
		{
			//Log.i("Player " + parent.GetID(), "Is changing to "+ decided+"!");
			currentStrategy = newStrategy;
			currentStrategy.Start();
		}
		else
		{
			if(currentStrategy.getClass() != newStrategy.getClass())
			{
				//Log.i("Player " + parent.GetID(), "Is changing to "+ decided+"!");
				currentStrategy = newStrategy;
				currentStrategy.Start();
			}
		}
		
		//Log.i("Player " + parent.GetID(), "Is "+ decided+"! AvFiRe: " + averageFightRecord);
	}
	*/

}
