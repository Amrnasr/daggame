package com.game.InputDevice;

import com.game.Preferences;
import com.game.Regulator;
import com.game.Vec2;
import com.game.Scenes.PlayScene;

public class AIInputDevice extends InputDevice 
{
	Regulator redecideRegulator;
	PlayScene sceneRef;
	
	public AIInputDevice(PlayScene playScene)
	{
		super(playScene);
		sceneRef = playScene;
		redecideRegulator = new Regulator(0.5f);
	}

	@Override
	public void Start() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void Update() 
	{
		if(redecideRegulator.IsReady())
		{
			NaiveChaseClosestCursor();
		}
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
