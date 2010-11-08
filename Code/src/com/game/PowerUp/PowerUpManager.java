package com.game.PowerUp;

import java.util.Vector;

import com.game.Regulator;

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
		// Add to list
		// Send message to renderer
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
