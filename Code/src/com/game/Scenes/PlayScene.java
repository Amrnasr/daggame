package com.game.Scenes;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.game.Camera;
import com.game.Cursor;
import com.game.Map;
import com.game.MessageHandler;
import com.game.MessageHandler.MsgReceiver;
import com.game.MsgType;
import com.game.Player;
import com.game.Preferences;
import com.game.R;
import com.game.Regulator;
import com.game.RenderInitData;
import com.game.InputDevice.AIInputDevice;
import com.game.InputDevice.BallInputDevice;
import com.game.InputDevice.InputDevice;
import com.game.InputDevice.TouchInputDevice;

/**
 * A specific scene for the "Play" screen.
 * @author Ying
 *
 */
public class PlayScene extends Scene 
{
	/**
	 * Specifies the state of the plays scene
	 * @author Ying
	 *
	 */
	public enum LogicState
	{
		/**
		 * When the state is created but not yet ready
		 */
		UNINITIALIZED,
		
		/**
		 * State is ready to run, have fun kids!
		 */
		PLAYING,
		
		/**
		 * Game paused
		 */
		PAUSED
	}
	
	/**
	 * Current state of the game
	 */
	private LogicState gameState;
	
	/**
	 * Game map reference
	 */
	private Map map;
	
	/**
	 * Flag that indicates whether to show the tilemap
	 */
	private boolean mShowTileMap;
	
	/**
	 * List of all the players in the game
	 */
	private Vector<Player> players;
	
	/**
	 * Sends a message when a trackball message arrives via the handler
	 */
	public Handler trackballEvent;
	
	/**
	 * Sends a message when a touch message arrives via the handler
	 */
	public Handler touchEvent;
	
	/**
	 * Keeps the gameplay fps stable.
	 */
	private Regulator gameplayRegulator;
	
	/**
	 * Keeps the update of the zoom funcion much lower than gameplay.
	 */
	private Regulator cameraZoomRegulator;
	
	private Regulator messageManagerRegulator;
	
	/**
	 * Initializes and sets the handler callback.
	 */
	public PlayScene()
	{
		super();
		
		this.gameState = LogicState.UNINITIALIZED;
		this.players = new Vector<Player>();
		this.trackballEvent = null;
		this.touchEvent = null;
		this.gameplayRegulator = new Regulator(60);
		this.cameraZoomRegulator = new Regulator(1);
		this.messageManagerRegulator = new Regulator(1);
		
		CreatePlayers();
		
		map = null;

		this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {
	        	if(msg.what == MsgType.TOUCH_EVENT.ordinal())
	        	{       		
	        		if( touchEvent != null )
	        		{
	        			// If there is some input event registered to the touch events
	        			// send him the message. Otherwise we ignore it.
	        			touchEvent.sendMessage(touchEvent.obtainMessage(MsgType.TOUCH_EVENT.ordinal(), msg.obj));
	        		}
	        	}
	        	else if(msg.what == MsgType.REPLY_WCS_TRANSFORM_REQUEST.ordinal())
				{
	        		if( touchEvent != null )
	        		{
	        			touchEvent.sendMessage(touchEvent.obtainMessage(MsgType.REPLY_WCS_TRANSFORM_REQUEST.ordinal(), msg.obj));
	        		}
				}	        	
	        	else if(msg.what == MsgType.TRACKBALL_EVENT.ordinal())
				{	        		
	        		if( trackballEvent != null )
	        		{
	        			// If there is some input event registered to the trackball events
	        			// send him the message. Otherwise we ignore it.
	        			trackballEvent.sendMessage(trackballEvent.obtainMessage(MsgType.TRACKBALL_EVENT.ordinal(), msg.obj));
	        		}
				}
	        	else if (msg.what == MsgType.SCENE_CALL_START.ordinal())
	        	{
	        		Start();
	        	}
	        	else if(msg.what == MsgType.STOP_SCENE.ordinal())
	        	{
	        		runScene = false;
	        	}    	
	        	else if(msg.what == MsgType.RENDERER_INITIALIZATION_DONE.ordinal())
	        	{
	        		// The renderer is done, so start has been done as well.
	        		gameState = LogicState.PLAYING;
	        	}
	        }
	    };	  
	    
	    Log.i("PlayScene", "PlayScene constructed");
	}

	@Override
	public void End() 
	{
		// TODO Auto-generated method stub
	}

	/**
	 * Initializes the play scene data.
	 * Loaded async to allow the loading dialog to function.
	 */
	@Override
	public void Start() 
	{
		final RenderInitData renderInitData = new RenderInitData();
		
		// A thread for background loading of the map
		Thread t = new Thread() 
		{
            public void run() 
            {
            	map = new Map(refActivity,R.drawable.samplemap, R.raw.samplemaptilemap);
        		renderInitData.SetMap(map);
        		
        		Bitmap cursorBitmap=BitmapFactory.decodeResource(refActivity.getResources(), R.drawable.cursor);
        		renderInitData.SetCursorBitmap(cursorBitmap);
            	
            	// Set the initial pos for all the cursors
        		for(int i= 0; i < players.size(); i++)
        		{
        			players.elementAt(i).SetCursorInitialPos();
        			players.elementAt(i).SetInitialTile(map);
        		}
        		
        		// Send all the cursors
        		Vector<Cursor> cursors = new Vector<Cursor>();
        		for(int i = 0; i < players.size(); i++)
        		{
        			cursors.add(players.elementAt(i).GetCursor());
        		}
        		renderInitData.SetCursors(cursors);
        		
        		// Done initializing logic, get the word out to the renderer.
        		MessageHandler.Get().Send(MsgReceiver.RENDERER, MsgType.INITIALIZE_RENDERER, renderInitData); 
        		Log.i("PlayScene", "Start function finished");
            }
        };
        t.start();		
	}

	/**
	 * Updates the game each update step until the thread is stopped
	 */
	@Override
	public void Update() 
	{
		// Logic not dependent on game state
		MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.UPDATE_PROFILER);
		
		// Update the message handler.
		if(messageManagerRegulator.IsReady())
		{
			MessageHandler.Get().Update();
		}
		
		// Logic only to run in playing (un-paused) mode
		if(SceneReady())
		{			
			Gameplay();
		}
	}
	
	/**
	 * Logic to be called only when the gameState is "Playing"
	 * Normal game logic goes here, as it will automatically stop updating
	 * when the game state changes.
	 */
	private void Gameplay()
	{
		if(!gameplayRegulator.IsReady())
		{
			return;
		}		
		
		PrepareUpdate();
		
		for(int i = 0; i < this.players.size(); i++)
		{
			this.players.elementAt(i).Update();			
		}
		
		if(Camera.Get() != null)
		{
			if(cameraZoomRegulator.IsReady())
			{
				Camera.Get().ZoomOnPlayers(players);
			}
			
			Camera.Get().Update();
		}
	}
	
	/**
	 * Prepares everything that needs to be prepared before the update step
	 */
	private void PrepareUpdate()
	{
		for(int i = 0; i < this.players.size(); i++)
		{
			this.players.elementAt(i).Prepare();			
		}
	}
	
	/**
	 * Creates the list of players and their InputDevices
	 */
	private void CreatePlayers()
	{
		if(Preferences.Get().multiplayerGame)
		{
			// Multiplayer game
			
			// Player 1 gets to choose input device
			Player player1;
			InputDevice player1ID = null;
			
			switch (Preferences.Get().multiControlMode) 
			{
				case 0:
					// Touch mode
					player1ID = new TouchInputDevice(this);
					break;
				
				case 1:
					// Trackball
					player1ID = new BallInputDevice(this);
					break;
	
				default:
					Log.e("PlayScene", "Input device requested for player not implemented yet!");				
					break;
			}
			player1 = new Player(0, player1ID, true);
			this.players.add(player1);
			
			// Player 2 gets stuck with trackball.
			Player player2 = new Player(1, new BallInputDevice(this), true);
			this.players.add(player2);
			
			// Add all the opponents
			Player newPlayer = null;
			for(int i = 0; i < Preferences.Get().multiNumberOpponents; i++ )
			{
				newPlayer = new Player(i+2, new AIInputDevice(this), false);
				this.players.add(newPlayer);
			}
			
		}
		else
		{
			// Single player game
			Player newPlayer;
			
			// Add player 1, input device checked from Preferences
			InputDevice inputDevice = null;
			
			switch (Preferences.Get().singleControlMode) 
			{
				case 0:
					// Touch mode
					inputDevice = new TouchInputDevice(this);
					break;
				
				case 1:
					// Trackball
					inputDevice = new BallInputDevice(this);
					break;
	
				default:
					Log.e("PlayScene", "Input device requested for player not implemented yet!");				
					break;
			}
			newPlayer = new Player(0, inputDevice, true);
			this.players.add(newPlayer);
			
			// Add all the opponents
			for(int i = 0; i < Preferences.Get().singleNumberOpponents; i++ )
			{
				newPlayer = new Player(i+1, new AIInputDevice(this), false);
				this.players.add(newPlayer);
			}			
		}
	}
	
	/**
	 * Checks if the scene is ready to execute gameplay
	 * @return True if it is, false if it isn't
	 */
	private boolean SceneReady()
	{
		return (gameState == LogicState.PLAYING); 
	}

}
