package com.game.battleofpixels.AI;

import java.util.Random;

import com.game.battleofpixels.Preferences;
import com.game.battleofpixels.Regulator;

/**
 * Dummy task class for debugging purposes 
 * @author Ying
 *
 */
public class DummyTeleportTask extends LeafTask 
{
	Regulator debugReg = new Regulator(0.2f);
	Random debugRand = new Random();

	public DummyTeleportTask(Blackboard blackboard) 
	{
		super(blackboard);
	}

	public DummyTeleportTask(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() 
	{
		return true;
	}

	@Override
	public void DoAction() 
	{
		if(debugReg.IsReady())
		{
			int x = debugRand.nextInt(Preferences.Get().mapWidth);
			int y = debugRand.nextInt(Preferences.Get().mapHeight);
			bb.player.GetCursor().SetPosition(x, y);
		}
		this.control.FinishWithSuccess();
	}

	@Override
	public void End() 
	{
	}

	@Override
	public void Start() 
	{
	}

}
