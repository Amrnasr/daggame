package com.game.ViewData;

import com.game.DagActivity.SceneType;

/**
 * Factory used to create the correct kind of ViewData.
 * 
 * @author Ying
 *
 */
public class ViewDataFactory 
{
	/**
	 * Instantiates the correct ViewData for the scene indicated.
	 * 
	 * @param scene is which scene we are creating the data for 
	 * @return a initialized ViewData object
	 * @throws Exception if the scene is not supported yet
	 */
	public static ViewData GetView(SceneType scene) throws Exception
	{
		ViewData viewData = null;
		
		switch (scene) {
		case MENU_SCENE:
	        viewData = new MenuViewData();
			break;
		case SINGLE_SCENE:
			viewData = new SingleViewData();
			break;
		case HOW_SCENE:
	        viewData = new HowViewData();
			break;
		case ABOUT_SCENE:
	        viewData = new AboutViewData();
			break;
		case GAMEOVER_SCENE:
	        viewData = new GameOverViewData();
			break;
		case OPTIONS_SCENE:
	        viewData = new OptionsViewData();
			break;
		case PLAY_SCENE:
	        viewData = new PlayViewData();
			break;
		// TODO: Add more xmlviews
		default:
			throw new Exception("View data to create is not defined in the Factory yet");
		}
		
		return viewData;
	}
}
