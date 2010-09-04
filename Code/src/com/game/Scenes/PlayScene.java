package com.game.Scenes;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.game.Constants;
import com.game.Map;
import com.game.MsgType;
import com.game.R;

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
	public enum GameState
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
	private GameState gameState;
	
	private Handler sendRenderer;
	
	private Map map;
	
	private boolean mShowTileMap;
	
	/**
	 * Initializes and sets the handler callback.
	 */
	public PlayScene()
	{
		super();
		
		gameState = GameState.UNINITIALIZED;
		
		mShowTileMap = true;
		
		map = null;
		
		this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {
	        	if(msg.what == MsgType.TOUCH_EVENT.ordinal())
	        	{
	        		MotionEvent event = (MotionEvent)msg.obj;
	        		
	        		// TODO: Do something relevant with the event
	        		Log.i("PlayScene Handler: ", "Motion event: " + event.getX() + ", " + event.getY());
	        	}
	        	else if(msg.what == MsgType.STOP_SCENE.ordinal())
	        	{
	        		runScene = false;
	        	}
	        	else if (msg.what == MsgType.RENDERER_LOGIC_HANDLER_LINK.ordinal())
	        	{	        		        	
	        		sendRenderer = (Handler)msg.obj;
	        		Log.i("PlayScene", "Received renderer handle!");
	        		
	        		// Only restraining condition for the game to start right now, 
	        		// if there are others additional logic will be needed.
	        		gameState = GameState.PLAYING;
	        		
	        		/*if(mShowTileMap){
	        			sendRenderer.sendMessage(sendRenderer.obtainMessage(MsgType.NEW_TILEMAP.ordinal(),map.getBitmap().getWidth()/Constants.TileWidth,map.getBitmap().getHeight()/Constants.TileWidth, map.getTileMap()));	
	        		}
	        		else{
	        			sendRenderer.sendMessage(sendRenderer.obtainMessage(MsgType.NEW_BITMAP.ordinal(), map.getBitmap().getWidth(), map.getBitmap().getHeight(), map.getBitmap()));
	        		}*/
	        	}
	        	
	        }
	    };
	}

	@Override
	public void End() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Start() 
	{
		if(refActivity == null )
		{
			Log.e("PlayScene","Reference pointer to activity broken!");
			
			
		}
		
		//map = new Map(refActivity,R.drawable.map_size480_1);
	}

	/**
	 * Updates the game each update step until the thread is stopped
	 * TODO: Set fps limit (must be easily deactivated for debugging speed)
	 */
	@Override
	public void Update() 
	{
		// Logic not dependent on game state
		actHandlerRef.sendEmptyMessage(MsgType.UPDATE_PROFILER.ordinal());
		
		// Logic only to run in playing (un-paused) mode
		if(gameState == GameState.PLAYING)
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
		
	}

}
