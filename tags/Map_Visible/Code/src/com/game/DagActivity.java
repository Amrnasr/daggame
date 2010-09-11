package com.game;


import com.game.ViewData.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.RelativeLayout;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * Activity class of the game. Creates and maintains the needed views.
 * It also is in charge of swapping out a scene-view pair for another, for example
 * the MenuScene and MenuView for a HowScene and HowView (How is short for "How to play")
 * 
 * It communicates with any threads via handlers. Specifically, it receives events from the logic thread
 * and process them.
 * 
 * It has two main members, gameView and gameLogic.
 * 
 * @author Ying
 */

public class DagActivity extends Activity 
{
	/// Used to display ram and fps on screen in any view.
	private Profiler profiler;
	
	/// Handler for messages from other threads (like the render or logic threads)
	private Handler handler;
	
	/// Enum with all the scenes in the game.
	public enum SceneType 
	{ MENU_SCENE, SINGLE_SCENE, MULTI_SCENE, OPTIONS_SCENE, HOW_SCENE, ABOUT_SCENE, PLAY_SCENE, GAMEOVER_SCENE};
	
	
	public RelativeLayout gameView;
	public DagLogicThread gameLogic;
	
	/// Aux value for changing to a new scene/view
	private SceneType nextScene;
	
	// Dialog used for long loading times
	private ProgressDialog dialog = null;
	
    /** 
     * Called when the activity is first created. 
     * Creates the initial view of the game
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        // I don't like it, but ORDER IS IMPORTANT! So don't change the order.
        // (Because the view uses the logic handler objects.)
        
        // Load stored game preferences
        LoadPreferences();
        
        // Set initial camera params
        InitializeCamera();
        
        // Create handler
        createHandler();
        
        // Create logic and view
        gameLogic = new DagLogicThread();        
        createLogicScene(SceneType.MENU_SCENE);
        createView(SceneType.MENU_SCENE);

        // Start logic and set view
        gameLogic.start();
    	setContentView(gameView);
    }
    
    /**
     * Given a game scene to load the view for, it creates it, and attaches it and 
     * the profiler to a relative view, to have profiler services automatically.
     * @param view
     */
    private void createView(SceneType view) 
    {
    	// Create a relative layout
    	gameView = new RelativeLayout(this); 	
    	gameView.setBackgroundResource(R.drawable.background4);
    	
    	// Create xml view and callbacks
    	
    	ViewData xmlLayoutData = null;
		try 
		{
			xmlLayoutData = ViewDataFactory.GetView(view);
			xmlLayoutData.setHandlerReference(gameLogic.getCurrentScene().getHandler());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			this.finish();
		}
		
		// Load layout from xml and create callbacks for the buttons.
    	View xmlLayout = xmlLayoutData.createXMLView(this);
    	
    	// Create profiler
    	profiler = new Profiler();
        
    	// Add xml & profiler views to the relative view
    	gameView.addView(xmlLayout);
    	profiler.Attach(gameView, this);
    }
    
    /**
     * Creates logic scene for the game, and gives communication with the logic scene 
     * created via a handler.
     */
    private void createLogicScene(SceneType scene)
    {    	
   	
    	try 
    	{
			gameLogic.setScene(scene, this);
		} 
    	catch (Exception e) 
    	{
    		Log.e("DagActivity", "Could not complete gamelogic.SetScene");
			e.printStackTrace();
			this.finish();
		}
    	
    	gameLogic.getCurrentScene().setActivityHandlerRef(this.handler);
    	
    	Handler auxH = gameLogic.getCurrentScene().getHandler();
		auxH.sendEmptyMessage(MsgType.SCENE_CALL_START.ordinal());
    }
    
    /**
     * Creates a Handler object and defines how the activity reacts to messages.
     * @see MsgType.java for message details.
     * 
     * Messages the activity can receive:
     *  - UPDATE_PROFILER: It's sent by the update loop of the gameLogic to tell us
     *  to call the Update() function of the profiler. This must be so because said
     *  function modifies one or more Views, which can only be modified by the thread 
     *  that created them (the Thread the activity runs on), but the Activity does 
     *  not have a Run() function so the continuous calling must be done from the 
     *  gameLogic.
     *  
     *  - ACTIVITY_CHANGE_SCENE: Is sent by the gameLogic when we want to change to
     *  another scene. As the scene we need to change runs in another thread, we can't 
     *  just barge in and stop it. We send the STOP_SCENE message to the scene, and the 
     *  scene will replay with a...
     *  
     *  - SCENE_STOPED_READY_FOR_CHANGE: ... when it's ready to change. We then create a
     *  new logic scene and a new view, and everyone is happy to go.
     *  
     *  - ACTIVITY_SAVE_PREFERENCES: Sent by the gameLogic when any scene warns us that
     *  changes in the default preferences have been made and we want to save them
     *  
     *  - ACTIVITY_DISMISS_LOAD_DIALOG: Sent by the PlayScene so we get rid of the 
     *  loading dialog shown when loading of the PlayScene is taking place
     */
    private void createHandler()
    {
    	this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {
	        	// Can't use a switch because of casting reasons. So if/else fun for everyone!
	        	if(msg.what == MsgType.ACTIVITY_CHANGE_SCENE.ordinal())
	        	{
	        		nextScene = SceneType.values()[msg.arg1];
	        		
	        		Handler auxH = gameLogic.getCurrentScene().getHandler();
	        		auxH.sendEmptyMessage(MsgType.STOP_SCENE.ordinal());
	        	}
	        	else if(msg.what == MsgType.SCENE_STOPED_READY_FOR_CHANGE.ordinal())
	        	{
	        		ChangeScene(nextScene);
	        	}
	        	else if(msg.what == MsgType.UPDATE_PROFILER.ordinal())
	        	{
	        		profiler.Update();
	        	}
	        	else if (msg.what == MsgType.ACTIVITY_SAVE_PREFERENCES.ordinal())
	        	{
	        		SavePreferences();
	        	}
	        	else if (msg.what == MsgType.ACTIVITY_DISMISS_LOAD_DIALOG.ordinal())
	        	{
	        		dialog.dismiss();
	        		dialog = null;
	        	}
	        	
	        }
	    };
    }
    
    /**
     * Loads game preferences from main memory
     */
    private void LoadPreferences()
    {
    	Preferences.Get().Load(this);
    }
    
    /**
     * Saves the game preferences to main memory
     */
    private void SavePreferences()
    {
    	Preferences.Get().Save(this);
    }
    
    /**
     * Changes the scene and view to the one provided
     * @param nextScene is the scene we want to change to.
     */
    private void ChangeScene(SceneType nextScene)
    {
    	// Special case, PlayScene being loaded, so we ad a "Loading" dialog.
    	// It will be deactivated by the PlayScene when it's done loading
    	if(nextScene == SceneType.PLAY_SCENE)
    	{
    		dialog = new ProgressDialog(this);
    		dialog.setCancelable(false);
    		dialog.show();
    	}
    	
    	// Do not change the order!!
    	createLogicScene(nextScene);
        createView(nextScene);

    	setContentView(gameView);
    }
    
    /**
     * Sets the camera initial parameters, like the screen size.
     */
    private void InitializeCamera()
    {
    	Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
        
        Camera.Get().SetScreenSize(display.getWidth(), display.getHeight());
    }
    
    /**
     * Called when the activity stops, it also stops the thread.
     * 
     * TODO: This is just a temporary measure. All the basic onWhatever 
     * methods must be overridden.
     */
    @Override protected void onStop()
    {
    	super.onStop();
    	if(gameLogic.isAlive())
    	{
    		gameLogic.stopGame();
    	}
    }
    
   
}