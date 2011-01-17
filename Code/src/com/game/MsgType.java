package com.game;

/**
 * List of all the messages the threads can send to one another.
 * There are 3 threads in play:
 * 
 * - Main: Where the activity runs and the views are, created when the application 
 * starts. Does not have a "run" loop. Usually receives messages from logic and sends
 * input messages to logic.
 * - Logic: Created by the main thread in the gameLogic variable. Has a scene manager
 * with scenes and controls the game logic. The current active scene usually receives 
 * messages.
 * - Render: When the GLView is used, we will use a renderer, which runs in it's own thread
 * Doesn't use any messages right now (but it will, basically receive messages from logic)
 * 
 * We use the android.os.Handler and android.os.Message classes for message sending. 
 * 
 * For each message I'm going to specify the arguments. For more info look at the Handler and
 * Message classes.
 * 
 * @author Ying
 *
 */
public enum MsgType 
{
	/**
	 * Sent when a button in a view is clicked. 
	 * arg1: the R.id of the button clicked
	 */
	BUTTON_CLICK,
	
	/**
	 * Sent when a checkbox in a view is clicked. 
	 * arg1: the R.id of the checkbox clicked
	 */	
	CHECKBOX_CLICK,
	
	/**
	 * Sent when a gallery item in a view is clicked. 
	 * arg1: the R.id of the gallery clicked
	 * arg2: the position of the gallery item clicked
	 */	
	GALLERY_ITEM_CLICK,
	
	/**
	 * Sent when a spinner item in a view is clicked. 
	 * arg1: the R.id of the spinner clicked
	 * arg2: the position of the spinner item clicked
	 */	
	SPINNER_ITEM_CLICK,
	
	/**
	 * Sent when a touch happens on screen. Not implemented yet (It's a PLAY_SCENE thing)
	 */
	TOUCH_EVENT,
	
	/**
	 * Sent to the activity when the gameLogic decides the game has to change scenes.
	 * arg1: The SceneType to which we wish to change to.
	 */
	ACTIVITY_CHANGE_SCENE,
	
	/**
	 * Sent each update loop from the current logic scene. 
	 * Tells the activity to update the profiler
	 */
	UPDATE_LOGIC_PROFILER,
	
	/**
	 * Sent each update loop from the renderer. 
	 * Tells the activity to update the profiler
	 */
	UPDATE_RENDER_PROFILER,
	
	/**
	 * Sent by the Activity to request a Scene to stop running. 
	 */
	STOP_SCENE,
	
	/**
	 * Sent by a scene to the Activity as a reply to STOP_SCENE, when the 
	 * scene is stopped and ready to change.
	 */
	SCENE_STOPED_READY_FOR_CHANGE,
	
	/**
	 * Tells the activity to save the Preferences object to main
	 * storage.
	 */
	ACTIVITY_SAVE_PREFERENCES,
	
	/**
	 * Sent when the track ball receives input.
	 */
	TRACKBALL_EVENT,
	
	/**
	 * Tells a scene to call the Start() function.
	 * This is my attempt on having the PlayScene load async.
	 */
	SCENE_CALL_START,
	
	/**
	 * Tells the activity loading has finished
	 */
	ACTIVITY_DISMISS_LOAD_DIALOG,
	
	/**
	 * Sent to logic to warn the renderer is ready
	 */
	RENDERER_INITIALIZATION_DONE,
	
	/**
	 * Sent when the touch input device needs to calculate a SCS to WCS transformation on
	 * a point. 
	 * 
	 * obj: Vec2 to transform.
	 */
	REQUEST_WCS_TRANSFORM,
	
	/**
	 * Reply to the WCS transform request.
	 * 
	 * obj: Vec2 with the transform
	 */
	REPLY_WCS_TRANSFORM_REQUEST,
	
	/**
	 * Sent to the DagRenderer to initialize the renderer once the logic has loaded stuff
	 * 
	 * obj: Render initialization data (As a RenderInitData object)
	 */
	INITIALIZE_RENDERER,
	
	/**
	 * Sent from the activity when onPause is called
	 */
	PAUSE_GAME,
	
	/**
	 * Sent from the activity when onResume is called
	 */
	UNPAUSE_GAME,
	
	/**
	 * Sent when the PowerUp manager wants to set a new PowerUp on screen^
	 * obj: The PowerUp to display
	 */
	DISPLAY_NEW_POWERUP,
	
	/**
	 * Sent when a PowerUp is not to be displayed anymore on screen
	 * obj: The PowerUp to stop displaying
	 */
	STOP_DISPLAYING_POWERUP,
	
	/**
	 * Sent when the Camera has changed the min/max z. 
	 */
	RENDERER_REQUEST_SURFACE_UPDATED,
}
