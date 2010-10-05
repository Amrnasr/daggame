package com.game;

import java.util.Vector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

/**
* Class that represents maps in the game. Maps are owned and controlled by the 
* map manager.
* 
* @author NeoM
*
*/
public class Map {
	
	private Bitmap mBitmap;
	private Vector<Tile> mTileMap;
	
	
	/**
	 * Initializes the map
	 * @param activity Activity that created the map manager
	 * @param mapRef Reference to the map to load.
	 */
	public Map(Activity activity, int mapRef)
	{	
		//Load the image
		Log.i("Map", "Started constructor");
		mBitmap=BitmapFactory.decodeResource(activity.getResources(), mapRef);

		int tilesPerRow = mBitmap.getWidth() / Constants.TileWidth;
		int tilesPerColumn = mBitmap.getHeight() / Constants.TileWidth;
		
		//Store the width and height of the map
		Preferences.Get().mapWidth=mBitmap.getWidth();
		Preferences.Get().mapHeight=mBitmap.getHeight();
		
		//Initialize the matrix
		mTileMap = new Vector<Tile>();

		//Calculate the maximum capacity of each tile and initialize them
		for( int j = tilesPerColumn-1; j >= 0; j--){
			for(int i = 0; i < tilesPerRow; i++){
				int whitePixels = 0;
				for(int k = 0; k < Constants.TileWidth; k++){
					for(int l = 0; l < Constants.TileWidth; l++){
						if(mBitmap.getPixel(i*Constants.TileWidth+k, j*Constants.TileWidth+l) != Color.BLACK){
							whitePixels++;
						}
					}
				}
				mTileMap.addElement(new Tile(i,tilesPerColumn-j,whitePixels));
				//Log.i("Map",new String().valueOf(whitePixels));
			}
		}
	}
	
	public Bitmap getBitmap()
	{
		return mBitmap;
	}
	
	public Vector<Tile> getTileMap(){
		return mTileMap;
	}
	
	public void Start() {
		// TODO Auto-generated method stub
	}
	
	public void Update() 
	{
		// TODO Auto-generated method stub
	}
	
	public void End(){
		// TODO Auto-generated method stub
	}
	

}
