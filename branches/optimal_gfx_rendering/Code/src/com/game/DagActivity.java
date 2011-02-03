package com.game;


import com.game.MessageHandler.MsgReceiver;
import com.game.ViewData.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.widget.RelativeLayout;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
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
	/**
	 * Used to display ram and fps on screen in any view.
	 */
	private Profiler profiler;
	
	/**
	 * Handler for messages from other threads (like the render or logic threads)
	 */
	private Handler handler;
	
	/**
	 * Enum with all the scenes in the game.
	 * @author Ying
	 *
	 */
	public enum SceneType 
	{ MENU_SCENE, SINGLE_SCENE, MULTI_SCENE, OPTIONS_SCENE, HOW_SCENE, ABOUT_SCENE, PLAY_SCENE, GAMEOVER_SCENE};
	
	/**
	 * The actual game view as a relative layout
	 */
	public RelativeLayout gameView;
	
	/**
	 * Logic thread of the game
	 */
	public DagLogicThread gameLogic;
	
	/**
	 * Aux value for changing to a new scene/view
	 */
	private SceneType nextScene;
	
	/**
	 * Dialog used for long loading times
	 */
	//private ProgressDialog dialog = null;
	private final static int LOAD_DIALOG = 0;
	
	/**
	 * Identifiers for the options menu buttons.
	 * @author Ying
	 *
	 */
	public enum OptionsMenuID { OP_MENU_MENU, OP_MENU_HOME }
	
	/**
	 * Indicates if the menu is actually open
	 */
	private boolean menuOpen;
	
    /** 
     * Called when the activity is first created. 
     * Creates the initial view of the game
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        Log.i("DagActivity", "======== onCreate");

        // I don't like it, but ORDER IS IMPORTANT! So don't change the order.
        // (Because the view uses the logic handler objects.)
        
        // Load stored game preferences
        LoadPreferences();
        
        // Set initial camera parameters
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
     * Called when the activity starts.
     * Should have all we put onCreate, but
     * @see onStop hackhack
     */
    @Override protected void onStart()
    {
    	super.onStart();
    	
    	Log.i("DagActivity", "======== onStart");
    }
    
    /**
     * Called by the activity when restoring from onStop.
     * Not called ever.
     * @see onStop hackhack
     */
    @Override protected void onRestart()
    {
    	super.onRestart();
    	Log.i("DagActivity", "======== onRestart");
    }

    /**
     * Resumes the game if the activity requires it
     */
    @Override protected void onResume()
    {
    	super.onResume();
    	Log.i("DagActivity", "======== onResume");
    	
    	// Unpause
    	MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.UNPAUSE_GAME);
    }

    /**
     * Pauses the game if the activity requires it
     */
    @Override protected void onPause()
    {
    	super.onPause();
    	Log.i("DagActivity", "======== onPause");
    	
    	// Pause
    	MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.PAUSE_GAME);
    }

    /**
     * Called when the activity stops, it also stops the thread.
     */
    @Override protected void onStop()
    {
    	super.onStop();
    	//Debug.stopMethodTracing();
    	
    	Log.i("DagActivity", "======== onStop");
    	
    	if(gameLogic.isAlive())
    	{
    		gameLogic.stopGame();
    	}
    	finish();
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override protected void onDestroy()
    {
    	super.onDestroy();
    	Log.i("DagActivity", "======== onDestroy");
    }
    
    @Override protected Dialog onCreateDialog(int id)
    {
    	Dialog dialog = null;
    	
    	switch (id) {
		case LOAD_DIALOG:
			// Loading dialog
			dialog = new ProgressDialog(this);
    		((ProgressDialog)dialog).setMessage("Loading...");
    		dialog.setCancelable(false);
			break;

		default:
			Log.e("DagActivity", "Requested dialog that does not exist!!");
			break;
		}
    	
    	return dialog;
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
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			this.finish();
		}
		
		// Load layout from xml and create callbacks for the buttons.
    	View xmlLayout = xmlLayoutData.createXMLView(this);

    	
        
    	// Add xml & profiler views to the relative view
    	gameView.addView(xmlLayout);
    	if(Constants.OnScreenProfiler)
		{
    		// Create profiler
        	profiler = new Profiler();
        	profiler.Attach(gameView, this);
		}
    	
    }
    
    /**
     * Creates logic scene for the game, and gives communication with the logic scene 
     * created via a handler.
     */
    private void createLogicScene(SceneType scene)
    {    	
    	MessageHandler.Get().SetLogicHandler(null);
    	
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
    	
    	MessageHandler.Get().SetLogicHandler(gameLogic.getCurrentScene().getHandler());
    	MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SCENE_CALL_START);
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
	        		
	        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.STOP_SCENE);
	        	}
	        	else if(msg.what == MsgType.SCENE_STOPED_READY_FOR_CHANGE.ordinal())
	        	{
	        		ChangeScene(nextScene);
	        	}
	        	else if(msg.what == MsgType.UPDATE_LOGIC_PROFILER.ordinal())
	        	{
	        		if(Constants.OnScreenProfiler)
	        		{
	        			profiler.LogicUpdate();
	        		}
	        	}
	        	else if(msg.what == MsgType.UPDATE_RENDER_PROFILER.ordinal())
	        	{
	        		if(Constants.OnScreenProfiler)
	        		{	        		
	        			profiler.RenderUpdate();
	        		}
	        	}
	        	else if (msg.what == MsgType.ACTIVITY_SAVE_PREFERENCES.ordinal())
	        	{
	        		SavePreferences();
	        	}
	        	else if (msg.what == MsgType.ACTIVITY_DISMISS_LOAD_DIALOG.ordinal())
	        	{
	        		removeDialog(LOAD_DIALOG);
	        	}
	        }
	    };
	    
	    MessageHandler.Get().SetActivityHandler(this.handler);
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
    		this.showDialog(LOAD_DIALOG);
    		//dialog.show();   
    				
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
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) 
    {
        super.onPrepareOptionsMenu (menu);
        boolean handled = false;
        this.menuOpen = true;
        if(nextScene == SceneType.PLAY_SCENE)
	    {
	        menu.clear();
	        menu.add(0, OptionsMenuID.OP_MENU_MENU.ordinal(), 0, "Quit to MENU");
	        menu.add(0, OptionsMenuID.OP_MENU_HOME.ordinal(), 1, "Quit to HOME");
	        handled = true;
	        
	        MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.PAUSE_GAME);
    	}

        return handled;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle item selection
    	if(item.getItemId() == OptionsMenuID.OP_MENU_MENU.ordinal())
    	{
    		Log.i("Activity", "MENU MENU");
    		CreateAlert(OptionsMenuID.OP_MENU_MENU);
    		this.menuOpen = false;
    		return true;
    	}
    	else if(item.getItemId() == OptionsMenuID.OP_MENU_HOME.ordinal()) 
    	{
    		Log.i("Activity", "MENU HOME");
    		CreateAlert(OptionsMenuID.OP_MENU_HOME);
    		this.menuOpen = false;
    		return true;
    	}
    	else
    	{    		
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    public void onOptionsMenuClosed(Menu menu)
    {
    	if(this.menuOpen)
    	{
    		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.UNPAUSE_GAME);
    	}
    }
    
    public void CreateAlert(OptionsMenuID optionsID)
    {
    	AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
    	final OptionsMenuID optID = optionsID;
    	
    	alt_bld.setMessage(" Are you really sure about that?");
    	alt_bld.setCancelable(false);
    	
    	alt_bld.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
    	{
	    	public void onClick(DialogInterface dialog, int id) {
	    	// Action for 'Yes' Button
	    		if(optID == OptionsMenuID.OP_MENU_HOME)
	    		{
	    			finish();
	    		}
	    		else if (optID == OptionsMenuID.OP_MENU_MENU)
	    		{
	    			MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.ACTIVITY_CHANGE_SCENE, SceneType.MENU_SCENE.ordinal());
	    		}
	    	}
    	});
    	
    	alt_bld.setNegativeButton("No", new DialogInterface.OnClickListener() 
    	{
	    	public void onClick(DialogInterface dialog, int id) {
	    	//  Action for 'NO' Button
	    	MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.UNPAUSE_GAME);
	    	dialog.cancel();
    	}
    	});
    	
    	AlertDialog alert = alt_bld.create();
    	// Title for AlertDialog
    	//alert.setTitle("Title");
    	// Icon for AlertDialog
    	//alert.setIcon(R.drawable.icon);
    	alert.show();
    }
}