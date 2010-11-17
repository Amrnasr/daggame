package com.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
* Class that represents maps in the game. Maps are owned and controlled by the 
* map manager.
* 
* @author NeoM
*
*/
public class Map {
	/**
	 * Map's bitmap
	 */
	private Bitmap bitmap;
	/**
	 * Map's tile map
	 */
	private Vector<Tile> tileMap;
	
	/**
	 * How many tiles there are in a horizontal line
	 */
	private int tilesPerRow;
	
	/**
	 * How many tiles in a vertical line
	 */
	private int tilesPerColumn;
	
	/**
	 * Initializes the map
	 * @param activity Activity that created the map manager
	 * @param bitmapID Reference to the bitmap to load.
	 * @param tileMapID Reference to the tile map to load.
	 */
	public Map(Activity activity, int bitmapID,int tileMapID)
	{	
		//Load the image
		Log.i("Map", "Started constructor");
		bitmap=BitmapFactory.decodeResource(activity.getResources(), bitmapID);

		tilesPerRow = bitmap.getWidth() / Constants.TileWidth;
		tilesPerColumn = bitmap.getHeight() / Constants.TileWidth;		
		
		Log.i("Map", "Tiles: " + tilesPerRow + ", " + tilesPerColumn);
		
		//Store the width and height of the map
		Preferences.Get().mapWidth=bitmap.getWidth();
		Preferences.Get().mapHeight=bitmap.getHeight();
		
		//open the tile map text file
		BufferedReader tileMapReader = new  BufferedReader(new InputStreamReader(activity.getResources().openRawResource(tileMapID)));
		
		//Initialize the matrix
		tileMap = new Vector<Tile>();
		
		//Calculate the maximum capacity of each tile and initialize them
		for( int j = 0; j < tilesPerColumn; j++)
		{
			String[] line = null;
			try {
				line = tileMapReader.readLine().split(" ");
			} catch(IOException ioe) {
			   Log.e("Map","Error reading from " + tileMapID);
			} 

			for(int i = 0; i < tilesPerRow; i++)
			{
				int value=0;
				
				try 
				{
					value = Integer.parseInt(line[i].trim());
				} 
				catch(NumberFormatException nfe) {
				   Log.e("Map","Error converting into an integer a value from " + tileMapID);
				} 

				tileMap.addElement(new Tile(i,j, value, this));
			}
		}
		
		Log.i("Map", "Tiles: " + tilesPerRow + ", " + tilesPerColumn + " total: " + tileMap.size());
	}
	/**
	 * Returns the current bitmap
	 * @return the related bitmap.
	 */
	public Bitmap getBitmap()
	{
		return bitmap;
	}
	/**
	 * Returns the current tile map
	 * @return the related tile map.
	 */
	public Vector<Tile> getTileMap(){
		return tileMap;
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
	
	/**
	 * Gets the tile at the specified tile position 
	 * (ej, 12,34 returns the tile at column 12, row 34)
	 * @param col Where the tile is at
	 * @param row Where the tile is at
	 * @return The tile specified by the row, col
	 */
	public Tile AtTile(int col, int row)
	{
		//Log.i("Tile", "AtTile: " + col + ", " + row);
		
		// Bounds check
		if(row < 0 || col >= tilesPerRow || col < 0 || row >= tilesPerColumn) 
		{
			return null;
		}
		
		int location = row * this.tilesPerRow + col;
		//Log.i("Tile", "AtTile: " + location);
		return this.tileMap.elementAt(location);
	}
	
	/**
	 * Gets the tile closest to the specified world position
	 * (ej, 300, 450 returns the tile at column 300/tileSize, 450/tilesize)
	 * @param x In world coordinates of the tile
	 * @param y In world coordinates of the tile
	 * @return The tile specified by x,y
	 */
	public Tile AtWorld(int x, int y)
	{
		return AtTile(x/Constants.TileWidth, y/Constants.TileWidth);
	}
	
	/**
	 * Finds and returns the closest tile to the coordinates provided. 
	 * Does a BFS, so use sparingly. 
	 * @param initialX The x position from where we start searching
	 * @param initialY The y position from where we start searching
	 * @return The closest empty tile.
	 */
	public Tile GetClosestEmptyTile(int initialX, int initialY)
	{
		Queue<Tile> toSearch = new LinkedList<Tile>();
		toSearch.add(this.AtWorld(initialX,initialY));
		
		Tile objectiveTile = null;
		
		while(!toSearch.isEmpty())
		{
			Tile aux = toSearch.remove();
			
			// If it's a max capacity tile, and empty
			if(aux.GetCurrentCapacity() == Tile.TileMaxCapacity())
			{
				// Found our objective tile
				objectiveTile = aux;
				toSearch.clear();
			}
			else
			{
				// Keep looking, look at all the 8 tiles around
				int x = (int) aux.GetPos().X();
				int y = (int) aux.GetPos().Y();
				
				for(int i = -1; i < 2; i++)
				{
					for(int j = -1; j < 2; j++)
					{
						Tile newSuspect = this.AtTile(x+i, y+j);
						if(newSuspect != null)
						{
							toSearch.add(newSuspect);
						}
					}
				}
			}
		}
		
		if(objectiveTile == null)
		{
			Log.e("Map", "NOT FOUND EMPTY TILE!");
		}
				
		return objectiveTile;
	}
	

}
