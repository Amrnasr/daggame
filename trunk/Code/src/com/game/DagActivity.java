package com.game;


import com.game.ViewData.*;

import android.app.Activity;
import android.widget.RelativeLayout;
import android.os.Bundle;
import android.view.View;

/**
 * Activity class of the game. Creates and maintains the needed views.
 * @author Ying
 *
 */
public class DagActivity extends Activity 
{
	private Profiler profiler;
	public enum SceneType 
	{ MENU_SCENE, SINGLE_SCENE, MULTI_SCENE, OPTIONS_SCENE, HOW_SCENE, ABOUT_SCENE, PLAY_SCENE, GAMEOVER_SCENE};
	
	public RelativeLayout gameView;
	public DagLogicThread gameLogic;
	
    /** 
     * Called when the activity is first created. 
     * Creates the initial view of the game
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        createAndSetView(SceneType.MENU_SCENE);
        
        createAndStartThread();
    }
    
    /**
     * Given a game scene to load the view for, it creates it, and attaches it and 
     * the profiler to a relative view, to have profiler services automatically.
     * @param view
     */
    private void createAndSetView(SceneType view) 
    {
    	// Create a relative layout
    	gameView = new RelativeLayout(this);
    	
    	
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
    	
    	// Create profiler
    	profiler = new Profiler();
        
    	// Add xml & profiler views to the relative view
    	gameView.addView(xmlLayout);
    	profiler.Attach(gameView, this);
    	
    	// Set relative view as view
    	setContentView(gameView);
    }
    
    /**
     * Creates and starts the logic thread for the game.
     */
    private void createAndStartThread()
    {
    	gameLogic = new DagLogicThread();
    	gameLogic.start();
    }
    
    /**
     * Called when the activity stops, it also stops the thread.
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