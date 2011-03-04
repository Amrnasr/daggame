package com.game.battleofpixels.InputDevice;

import java.util.Random;

import android.util.Log;


import com.game.battleofpixels.Player;
import com.game.battleofpixels.Preferences;
import com.game.battleofpixels.Regulator;
import com.game.battleofpixels.AI.AStarSearchTask;
import com.game.battleofpixels.AI.AStarSplitDecorator;
import com.game.battleofpixels.AI.Blackboard;
import com.game.battleofpixels.AI.CalculateCirclePathTask;
import com.game.battleofpixels.AI.CalculateFleeDestinationTask;
import com.game.battleofpixels.AI.CalculateFleePathTask;
import com.game.battleofpixels.AI.ChanceDecorator;
import com.game.battleofpixels.AI.CheckIfNeedToHuntTask;
import com.game.battleofpixels.AI.DefendDecorator;
import com.game.battleofpixels.AI.DummyTeleportTask;
import com.game.battleofpixels.AI.FindEnemyDensityTask;
import com.game.battleofpixels.AI.GetClosestEnemyCursorTask;
import com.game.battleofpixels.AI.GetClosestOwnedTileTask;
import com.game.battleofpixels.AI.IteratePathDecorator;
import com.game.battleofpixels.AI.MoveToDestinationTask;
import com.game.battleofpixels.AI.ParentTaskController;
import com.game.battleofpixels.AI.RegulatorDecorator;
import com.game.battleofpixels.AI.ResetDecorator;
import com.game.battleofpixels.AI.SearchForPowerUpTask;
import com.game.battleofpixels.AI.SelectRandomMovePos;
import com.game.battleofpixels.AI.Selector;
import com.game.battleofpixels.AI.Sequence;
import com.game.battleofpixels.AI.SetEnemyCursorAsDestinationTask;
import com.game.battleofpixels.AI.SetPathTileAsDestination;
import com.game.battleofpixels.AI.SimplifyPathTask;
import com.game.battleofpixels.AI.StopIfAttackedDecorator;
import com.game.battleofpixels.AI.Task;
import com.game.battleofpixels.AI.WaitTillNearDestinationTask;
import com.game.battleofpixels.Scenes.PlayScene;

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
		
		// Random move
		Task randomMove = new Sequence(blackboard, "Random move");
		randomMove = new ChanceDecorator(blackboard, randomMove, "Random move", 30);
		((ParentTaskController)randomMove.GetControl()).Add( new SelectRandomMovePos(blackboard, "SelectRandomMovePos") );
		((ParentTaskController)randomMove.GetControl()).Add( new MoveToDestinationTask(blackboard, "MoveToDestinationTask") );
		((ParentTaskController)randomMove.GetControl()).Add( new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestinationTask"));

		
		// PowerUp search
		Task powerUpSearch = new Sequence(blackboard, "Get PowerUp sequence");
		powerUpSearch = new ChanceDecorator(blackboard, powerUpSearch, "Get PowerUp sequence", 10);
		((ParentTaskController)powerUpSearch.GetControl()).Add( new SearchForPowerUpTask(blackboard, "SearchForPowerUpTask") );
		((ParentTaskController)powerUpSearch.GetControl()).Add( new MoveToDestinationTask(blackboard, "MoveToDestinationTask") );
		((ParentTaskController)powerUpSearch.GetControl()).Add( new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestinationTask"));
		
				
		// Attack
		Task attack = new Selector(blackboard, "Attack");
		
		/// Hunt Attack
		Task hunt = new Sequence(blackboard, "Hunt sequence");
		hunt = new ChanceDecorator(blackboard, hunt, "Hunt sequence", 30);
		((ParentTaskController)hunt.GetControl()).Add(new CheckIfNeedToHuntTask(blackboard, "CheckIfNeedToHunt"));
		((ParentTaskController)hunt.GetControl()).Add(new GetClosestOwnedTileTask(blackboard, "GetClosestOwnedTile"));
		((ParentTaskController)hunt.GetControl()).Add(new MoveToDestinationTask(blackboard, "MoveToDestination"));
		((ParentTaskController)hunt.GetControl()).Add(new FindEnemyDensityTask(blackboard, "FindEnemyDensity"));
		Task aStarSearch = new AStarSearchTask(blackboard, "AStarSearch");
		aStarSearch = new AStarSplitDecorator(blackboard, aStarSearch, "AStarSearch {SplitDec}");
		aStarSearch = new StopIfAttackedDecorator(blackboard, aStarSearch, "AStarSearch {StopIfAttack}");
		((ParentTaskController)hunt.GetControl()).Add( aStarSearch );
		((ParentTaskController)hunt.GetControl()).Add(new SimplifyPathTask(blackboard, "SimplifyPathTask"));
		Task huntPathSequence = new Sequence(blackboard, "Follow next tile sequence");
		huntPathSequence = new IteratePathDecorator(blackboard, huntPathSequence, "Follow next tile sequence");
		Task huntSetPathTileAsDestination = new SetPathTileAsDestination(blackboard, "SetPathTileAsDestination");
		huntSetPathTileAsDestination = new StopIfAttackedDecorator(blackboard, huntSetPathTileAsDestination, "SetPathTileAsDestination {StopIfAttack}" );
		((ParentTaskController)huntPathSequence.GetControl()).Add(huntSetPathTileAsDestination);
		((ParentTaskController)huntPathSequence.GetControl()).Add(new MoveToDestinationTask(blackboard, "MoveToDestination"));
		((ParentTaskController)huntPathSequence.GetControl()).Add(new WaitTillNearDestinationTask(blackboard, "WaitTillNearDestination"));
		((ParentTaskController)hunt.GetControl()).Add(huntPathSequence);
		
		
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
		((ParentTaskController)attack.GetControl()).Add(hunt);
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
		
		((ParentTaskController)this.planner.GetControl()).Add(randomMove);
		((ParentTaskController)this.planner.GetControl()).Add(powerUpSearch);
		((ParentTaskController)this.planner.GetControl()).Add(defend);
		((ParentTaskController)this.planner.GetControl()).Add(attack);
		
		
		//DEBUG
		//((ParentTaskController)this.planner.GetControl()).Add(new DummyTeleportTask(blackboard, "DummyTeleport"));
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
		this.planner.GetControl().SafeStart();
	}

	//Regulator debugReg = new Regulator(0.2f);
	//Random debugRand = new Random();
	
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
		/*
		if(debugReg.IsReady())
		{
			int x = debugRand.nextInt(Preferences.Get().mapWidth);
			int y = debugRand.nextInt(Preferences.Get().mapHeight);
			parent.GetCursor().SetPosition(x, y);
		}
		*/
		this.planner.DoAction();
	}
}
