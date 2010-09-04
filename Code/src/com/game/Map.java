package com.game;

import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
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
	private Vector<Vector<Tile>> mTileMap;
	
	
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
		//mBitmap=BitmapFactory.decodeFile("res/drawable/map_size480_1.png");
		Log.i("Map", "a");
		int tilesPerRow = mBitmap.getWidth() / Constants.TileWidth;
		int tilesPerColumn = mBitmap.getHeight() / Constants.TileWidth;
		Log.i("Map", "b");
		//Initialize the matrix
		mTileMap = new Vector<Vector<Tile>>();
		for(int i = 0; i < tilesPerRow; i++){
			mTileMap.addElement(new Vector<Tile>());
		}
		Log.i("Map", "c");
		
		//Calculate the maximum capacity of each tile and initialize them
		Iterator<Vector<Tile>> it = mTileMap.listIterator();
		Vector<Tile> tileVector=null;
		for(int i = 0; i < tilesPerRow; i++){
			tileVector= it.next();
			for( int j = 0; j < tilesPerColumn; j++){
				int whitePixels = 0;
				for(int k = 0; k < Constants.TileWidth; k++){
					for(int l = 0; l < Constants.TileWidth; l++){
						if(mBitmap.getPixel(i*Constants.TileWidth+k, j*Constants.TileWidth+l) == Color.WHITE){
							whitePixels++;
						}
					}
				}
				tileVector.addElement(new Tile(i,j,whitePixels));
			}
		}
		Log.i("Map", "d");
	}
	
	public Bitmap getBitmap()
	{
		return mBitmap;
	}
	
	public Vector<Vector<Tile>> getTileMap(){
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
