package com.game.battleofpixels.AI;

import java.util.Random;

import com.game.battleofpixels.Preferences;
import com.game.battleofpixels.Vec2;

public class SelectRandomMovePos extends LeafTask {

	public SelectRandomMovePos(Blackboard blackboard) {
		super(blackboard);
	}

	public SelectRandomMovePos(Blackboard blackboard, String name) {
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() {
		return true;
	}

	@Override
	public void DoAction() 
	{
		Random rand = new Random();
		Vec2 dest = new Vec2(rand.nextInt(Preferences.Get().mapWidth), rand.nextInt(Preferences.Get().mapHeight));
		Vec2 cursorPos = bb.player.GetCursor().GetPosition();
		
		bb.moveDirection = new Vec2(dest.X()-cursorPos.X(), dest.Y()-cursorPos.Y());
		bb.destination.Set(dest.X(), dest.Y());
		
		control.FinishWithSuccess();
	}

	@Override
	public void End() {

	}

	@Override
	public void Start() 
	{

	}

}
