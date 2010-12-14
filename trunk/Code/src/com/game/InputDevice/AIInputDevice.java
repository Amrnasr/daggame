package com.game.InputDevice;

import com.game.Player;

import com.game.AI.Blackboard;
import com.game.AI.CalculateFleeDestinationTask;
import com.game.AI.FleeDecorator;
import com.game.AI.GetClosestEnemyCursorTask;
import com.game.AI.ParentTaskController;
import com.game.AI.MoveToDestinationTask;
import com.game.AI.RegulatorDecorator;
import com.game.AI.ResetDecorator;
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
	 * Root task of the behavior tree for the AI
	 */
	private Task planner;
	
	/**
	 * Information blackboard for the AI
	 */
	private Blackboard blackboard;
	
	/**
	 * Creates a new instance of the AIInputDevice
	 * @param playScene Reference to the playScene
	 */
	public AIInputDevice(PlayScene playScene)
	{
		super(playScene);
		
		// Set AI the blackboard data.
		blackboard = new Blackboard();	
		CreateBehaviourTree();
	}
	
	/**
	 * Creates the behavior tree and populates the node hierarchy
	 */
	private void CreateBehaviourTree()
	{
		this.planner = new Selector(blackboard, "Planner");
		this.planner = new ResetDecorator(blackboard, this.planner, "Planner");
		this.planner = new RegulatorDecorator(blackboard, this.planner, "Planner", 0.1f);
		
		Task chase = new Sequence(blackboard, "Chase sequence");
		((ParentTaskController)chase.GetControl()).Add(new GetClosestEnemyCursorTask(blackboard, "GetClosestEnemyCursor"));
		((ParentTaskController)chase.GetControl()).Add(new SetEnemyCursorAsDestinationTask(blackboard, "SetEnemyCursorAsDestination"));
		((ParentTaskController)chase.GetControl()).Add(new MoveToDestinationTask(blackboard, "MoveToDestination"));
		((ParentTaskController)chase.GetControl()).Add(new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestination"));
		
		Task flee = new Sequence(blackboard, "Flee sequence");
		flee = new FleeDecorator(blackboard, flee, "Flee sequence");
		((ParentTaskController)flee.GetControl()).Add(new CalculateFleeDestinationTask(blackboard, "CalculateFleeDestination"));
		((ParentTaskController)flee.GetControl()).Add(new MoveToDestinationTask(blackboard, "MoveToDestination"));
		((ParentTaskController)flee.GetControl()).Add(new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestination"));
		
		((ParentTaskController)this.planner.GetControl()).Add(flee);
		((ParentTaskController)this.planner.GetControl()).Add(chase);
		
	}
	
	/**
	 * Sets the parent of the InputDevice.
	 */
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
		this.planner.GetControl().SafeStart();
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
	}
}
