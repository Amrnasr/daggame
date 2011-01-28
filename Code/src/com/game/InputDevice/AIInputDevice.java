package com.game.InputDevice;

import com.game.Player;

import com.game.AI.Blackboard;
import com.game.AI.CalculateCirclePathTask;
import com.game.AI.CalculateFleeDestinationTask;
import com.game.AI.CalculateFleePathTask;
import com.game.AI.ChanceDecorator;
import com.game.AI.DefendDecorator;
import com.game.AI.GetClosestEnemyCursorTask;
import com.game.AI.IteratePathDecorator;
import com.game.AI.ParentTaskController;
import com.game.AI.MoveToDestinationTask;
import com.game.AI.RegulatorDecorator;
import com.game.AI.ResetDecorator;
import com.game.AI.SearchForPowerUpTask;
import com.game.AI.Selector;
import com.game.AI.Sequence;
import com.game.AI.SetEnemyCursorAsDestinationTask;
import com.game.AI.SetPathTileAsDestination;
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
		// Planner
		this.planner = new Selector(blackboard, "Planner");
		this.planner = new ResetDecorator(blackboard, this.planner, "Planner");
		this.planner = new RegulatorDecorator(blackboard, this.planner, "Planner", 0.1f);
		
		// PowerUp search
		Task powerUpSearch = new Sequence(blackboard, "Get PowerUp sequence");
		powerUpSearch = new ChanceDecorator(blackboard, powerUpSearch, "Get PowerUp sequence", 20);
		((ParentTaskController)powerUpSearch.GetControl()).Add( new SearchForPowerUpTask(blackboard, "SearchForPowerUpTask") );
		((ParentTaskController)powerUpSearch.GetControl()).Add( new MoveToDestinationTask(blackboard, "MoveToDestinationTask") );
		((ParentTaskController)powerUpSearch.GetControl()).Add( new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestinationTask"));
		
		// Attack
		Task attack = new Selector(blackboard, "Attack");
		
		/// Circle Chase Attack
		Task circleChase = new Sequence(blackboard, "Circle chase sequence");
		circleChase = new ChanceDecorator(blackboard, circleChase, "Circle chase sequence", 60);
		((ParentTaskController)circleChase.GetControl()).Add(new GetClosestEnemyCursorTask(blackboard, "GetClosestEnemyCursor"));
		((ParentTaskController)circleChase.GetControl()).Add(new CalculateCirclePathTask(blackboard, "CalculateCirclePathTask"));
		Task circleChasePathSequence = new Sequence(blackboard, "Follow next tile sequence");
		circleChasePathSequence = new IteratePathDecorator(blackboard, circleChasePathSequence, "Follow next tile sequence");
		((ParentTaskController)circleChasePathSequence.GetControl()).Add(new SetPathTileAsDestination(blackboard, "SetPathTileAsDestination"));
		((ParentTaskController)circleChasePathSequence.GetControl()).Add(new MoveToDestinationTask(blackboard, "MoveToDestination"));
		((ParentTaskController)circleChasePathSequence.GetControl()).Add(new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestination"));
		((ParentTaskController)circleChase.GetControl()).Add(circleChasePathSequence);
		
		/// Straight Chase Attack
		Task straightChase = new Sequence(blackboard, "Chase sequence");
		((ParentTaskController)straightChase.GetControl()).Add(new GetClosestEnemyCursorTask(blackboard, "GetClosestEnemyCursor"));
		((ParentTaskController)straightChase.GetControl()).Add(new SetEnemyCursorAsDestinationTask(blackboard, "SetEnemyCursorAsDestination"));
		((ParentTaskController)straightChase.GetControl()).Add(new MoveToDestinationTask(blackboard, "MoveToDestination"));
		((ParentTaskController)straightChase.GetControl()).Add(new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestination"));
		
		// Add to attack
		((ParentTaskController)attack.GetControl()).Add(circleChase);
		((ParentTaskController)attack.GetControl()).Add(straightChase);
		
		// Defend
		Task defend = new Selector(blackboard, "Defend");
		defend = new DefendDecorator(blackboard, defend, "Defend");
		
		/// Circle Flee Defend
		Task circleFlee = new Sequence(blackboard, "Circle flee sequence");
		((ParentTaskController)circleFlee.GetControl()).Add(new CalculateFleePathTask(blackboard, "CalculateFleePath"));
		Task circleFleePathSequence = new Sequence(blackboard, "Flee chase sequence");
		circleFleePathSequence = new IteratePathDecorator(blackboard, circleFleePathSequence, "Flee chase sequence");
		((ParentTaskController)circleFleePathSequence.GetControl()).Add(new SetPathTileAsDestination(blackboard, "SetPathTileAsDestination"));
		((ParentTaskController)circleFleePathSequence.GetControl()).Add(new MoveToDestinationTask(blackboard, "MoveToDestination"));
		((ParentTaskController)circleFleePathSequence.GetControl()).Add(new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestination"));
		((ParentTaskController)circleFlee.GetControl()).Add(circleFleePathSequence);
		
		/// Straight Flee Defend
		Task straightFlee = new Sequence(blackboard, "Straight flee sequence");		
		((ParentTaskController)straightFlee.GetControl()).Add(new CalculateFleeDestinationTask(blackboard, "CalculateFleeDestination"));
		((ParentTaskController)straightFlee.GetControl()).Add(new MoveToDestinationTask(blackboard, "MoveToDestination"));
		((ParentTaskController)straightFlee.GetControl()).Add(new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestination"));
		
		// Add to defend
		((ParentTaskController)defend.GetControl()).Add(circleFlee);
		((ParentTaskController)defend.GetControl()).Add(straightFlee);
		
		// Add to planner
		((ParentTaskController)this.planner.GetControl()).Add(powerUpSearch);
		((ParentTaskController)this.planner.GetControl()).Add(defend);
		((ParentTaskController)this.planner.GetControl()).Add(attack);
		
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
