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
	 * Instanciates the correct ViewData for the scene indicated.
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
		// TODO: Add more xmlviews
		default:
			throw new Exception("View data to create is not defined in the Factory yet");
		}
		
		return viewData;
	}
}
