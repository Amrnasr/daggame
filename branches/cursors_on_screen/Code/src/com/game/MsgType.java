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
	 * Sent each update loop from the current scene. 
	 * Tells the activity to update the profiler
	 */
	UPDATE_PROFILER,
	
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
	 * Sent by the PlayScene to the renderer to give the renderer the new tilemap (Debug mode)
	 */
	NEW_TILEMAP,
	
	/**
	 * Sent by the PlayScene to the renderer to give the renderer the new Bitmap (Release mode)
	 */
	NEW_BITMAP,
	
	/**
	 * Sent to the activity to request the current contentView to be replaced by the load one.
	 */
	ACTIVITY_REQUEST_LOAD_SCREEN,
	
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
	 * Updates the camera position
	 */
	RENDERER_UPDATE_CAM,
	
	/**
	 * Sent to logic to warn the renderer is ready
	 */
	RENDERER_CONSTRUCTOR_DONE,
	
	/**
	 * Sent to the renderer to change a cursor color
	 * arg1:   Which cursor to update
	 * object: float[] with the rgba values
	 */
	UPDATE_CURSOR_COLOR,
	
	/**
	 * Sent from the Logic to the renderer with the ref vector with the cursor list
	 * object: vector of cursors
	 */
	GET_CURSOR_VECTOR,
	
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
	REPLY_WCS_TRANSFORM_REQUEST
	
}
