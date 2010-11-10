package com.game.PowerUp;

import java.util.Random;
import java.util.Vector;

import com.game.MessageHandler;
import com.game.MsgType;
import com.game.Preferences;
import com.game.Regulator;
import com.game.Tile;
import com.game.Vec2;
import com.game.MessageHandler.MsgReceiver;
import com.game.Scenes.PlayScene;

/**
 * Creates the PowerUps as needed and keeps track of them while on the map.
 * Every few seconds it creates a PowerUp, to a maximum specified. 
 * It updates them and keeps them around until a Player claims them. Then
 * it severs all connection with them and gives them to the player. 
 *  
 * @author Ying
 *
 */
public class PowerUpManager 
{
	/**
	 * List of all the PowerUp it has
	 */
	private Vector<PowerUp> powerUps;
	
	/**
	 * Maximum number of PowerUp we want
	 * TODO: Read from file!
	 */
	private int recomendedNumberPowerUps;
	
	/**
	 * Regulator for the PowerUp update speed
	 */
	private Regulator addNewPowerUpRegulator;
	
	/**
	 * Reference to the parent PlayScene
	 */
	private PlayScene playRef;
	
	/**
	 * Creates a new instance of a PowerUpManager.
	 * @param playRef Reference to the parent PlayScene
	 */
	public PowerUpManager(PlayScene playRef)
	{
		this.powerUps = new Vector<PowerUp>();
		this.recomendedNumberPowerUps = 3;
		this.addNewPowerUpRegulator = new Regulator(2);
		this.playRef = playRef; 
	}
	
	/**
	 * Updates the PowerUpManager, adding new PowerUps as needed
	 */
	public void Update()
	{
		// Only execute if the PlayScene is ready
		if(!this.playRef.SceneReady())
		{
			return;
		}
		
		if(HasToAddNewPowerUp())
		{
			AddNewPowerUp();
		}
	}
	
	/**
	 * Creates and adds a new PowerUp, linking it to a Tile and asking the 
	 * DagRenderer to display it.
	 */
	private void AddNewPowerUp() 
	{
		// Select random PowerUp
		PowerUp newPowerUp = ChooseRandomPowerUp();

		// Add to list
		this.powerUps.add(newPowerUp);
		
		// Adds to the nearest tile and updates the PowerUp position to match the tile.
		Tile objectiveTile = this.playRef.GetMap().GetClosestEmptyTile(
				(int)newPowerUp.Pos().X(), 
				(int)newPowerUp.Pos().Y());
		newPowerUp.SetPos(objectiveTile.GetRealPos());
		objectiveTile.AddPowerUp(newPowerUp);
		
		// Send message to renderer
		MessageHandler.Get().Send(MsgReceiver.RENDERER, MsgType.DISPLAY_NEW_POWERUP, newPowerUp);
	}
	
	/**
	 * Removes a PowerUp from it's care.
	 * @param powerUp PowerUp to remove
	 */
	public void RemovePoweUp(PowerUp powerUp)
	{
		this.powerUps.remove(powerUp);
	}

	/**
	 * Creates a new random PowerUp in a initial random position.
	 * The position might be changed later on if it's not a viable 
	 * (with capacity and empty of density) one.
	 * 
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

	/**
	 * Checks if it has to add a new PowerUp. 
	 * @return True if it has, false if it doesn't.
	 */
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

	/**
	 * Gets the vector of PowerUps
	 * @return The vector of PowerUp.
	 */
	public Vector<PowerUp> GetPowerUps() { return this.powerUps; }

}
