package com.game.PowerUp;

import java.util.Random;
import java.util.Vector;

import com.game.MessageHandler;
import com.game.MsgType;
import com.game.Preferences;
import com.game.Regulator;
import com.game.Vec2;
import com.game.MessageHandler.MsgReceiver;

public class PowerUpManager 
{
	private Vector<PowerUp> powerUps;
	private int recomendedNumberPowerUps;
	private Regulator addNewPowerUpRegulator;
	
	public PowerUpManager()
	{
		powerUps = new Vector<PowerUp>();
		recomendedNumberPowerUps = 3;
		addNewPowerUpRegulator = new Regulator(2);
	}
	
	public void Update()
	{
		if(HasToAddNewPowerUp())
		{
			AddNewPowerUp();
		}
	}
	
	private void AddNewPowerUp() 
	{
		// Select random PowerUp
		PowerUp newPowerUp = ChooseRandomPowerUp();

		// Add to list
		this.powerUps.add(newPowerUp);
		
		// Send message to renderer
		MessageHandler.Get().Send(MsgReceiver.RENDERER, MsgType.DISPLAY_NEW_POWERUP, newPowerUp);
	}

	/**
	 * Creates a new random PowerUp
	 * TODO: Right now it only creates one
	 * @return A random PowerUp
	 */
	private PowerUp ChooseRandomPowerUp() 
	{
		Random posGen = new Random();
		PowerUp newPUP = null;
		
		int wMargin = Preferences.Get().mapWidth/10;
		int wArea = Preferences.Get().mapWidth*8/10;
		
		int hMargin = Preferences.Get().mapHeight /10;
		int hArea = Preferences.Get().mapHeight *8/10;
		
		int x = posGen.nextInt(wArea) + wMargin;
		int y = posGen.nextInt(hArea) + hMargin;
		
		// Choose random:
		newPUP = new SpeedPowerUp(new Vec2(x,y));
		return newPUP;
	}

	private boolean HasToAddNewPowerUp() 
	{
		if(addNewPowerUpRegulator.IsReady())
		{
			if(powerUps.size() < recomendedNumberPowerUps)
			{
				return true;
			}
		}
		return false;
	}

	public Vector<PowerUp> GetPowerUps() { return this.powerUps; }

}
