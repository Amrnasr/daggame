package com.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
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
	
	/// Drawing data
	private CharBuffer indexBuffer;
	private int indexSize;
	private FloatBuffer vertexBuffer;
	private FloatBuffer colorBuffer;
	
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
		
		// Create the data for map drawing
		GenerateDrawMesh();
		
		Log.i("Map", "Tiles: " + tilesPerRow + ", " + tilesPerColumn + " total: " + tileMap.size());
	}
	
	public CharBuffer GetIndexBuffer() { return this.indexBuffer; }
	public int GetIndexSize() { return this.indexSize; }
	public Buffer GetVertexBuffer() { return this.vertexBuffer; }
	public Buffer GetColorBuffer() { return this.colorBuffer; }
	
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
	
	
	/**
	 * Gets the neighboring tile to a tile in a specific direction:
	 * 
	 * |7|0|1|
	 * |6|8|2|
	 * |5|4|3|
	 * 
	 * Where 8 is the current tile.
	 * 
	 * @param curTile Tile at the center. We search around this tile for neighbors
	 * @param dir Direction to search on. Accepted values ]0,8[
	 * @return The neighboring tile to curTile in the specified direction.
	 */
	public Tile GetNeighbour(Tile curTile, int dir)
	{
		Tile neighbour = null;
		
		switch (dir) 
		{
		case 0:
			neighbour = this.AtTile((int)(curTile.GetPos().X()), (int)(curTile.GetPos().Y() - 1));
			break;
		case 1:
			neighbour = this.AtTile((int)(curTile.GetPos().X() + 1), (int)(curTile.GetPos().Y() - 1));
			break;
		case 2:
			neighbour = this.AtTile((int)(curTile.GetPos().X() + 1), (int)(curTile.GetPos().Y()));
			break;
		case 3:
			neighbour = this.AtTile((int)(curTile.GetPos().X() + 1), (int)(curTile.GetPos().Y() + 1));
			break;
		case 4:
			neighbour = this.AtTile((int)(curTile.GetPos().X()), (int)(curTile.GetPos().Y() + 1));
			break;
		case 5:
			neighbour = this.AtTile((int)(curTile.GetPos().X() - 1), (int)(curTile.GetPos().Y() + 1));
			break;
		case 6:
			neighbour = this.AtTile((int)(curTile.GetPos().X() - 1), (int)(curTile.GetPos().Y()));
			break;
		case 7:
			neighbour = this.AtTile((int)(curTile.GetPos().X() - 1), (int)(curTile.GetPos().Y() - 1));
			break;
		case 8:
			neighbour = this.AtTile((int)(curTile.GetPos().X()), (int)(curTile.GetPos().Y()));
			break;

		default:
			Log.e("CircleStrategy", "Requested direction invalid motherfcuker: dir: " + dir);
			break;
		}
		
		return neighbour;
	}
	
	private void GenerateDrawMesh()
	{
		int size = tilesPerColumn * tilesPerRow;
        final int FLOAT_SIZE = 4;
        final int CHAR_SIZE = 2;
        
        // Creating the index array for a triangle mesh
        int quadW = tilesPerRow - 1;
        int quadH = tilesPerColumn - 1;
        int quadCount = quadW * quadH;
        int indexCount = quadCount * 6;
        indexSize = indexCount;
        indexBuffer = ByteBuffer.allocateDirect(CHAR_SIZE * indexCount)
            .order(ByteOrder.nativeOrder()).asCharBuffer();

        /*
         * Initialize triangle list mesh.
         *
         *     [0]-----[  1] ...
         *      |    /   |
         *      |   /    |
         *      |  /     |
         *     [w]-----[w+1] ...
         *      |       |
         *
         */

        {
            int i = 0;
            for (int y = 0; y < quadH; y++) {
                for (int x = 0; x < quadW; x++) {
                    char a = (char) (y * tilesPerRow + x);
                    char b = (char) (y * tilesPerRow + x + 1);
                    char c = (char) ((y + 1) * tilesPerRow + x);
                    char d = (char) ((y + 1) * tilesPerRow + x + 1);

                    indexBuffer.put(i++, a);
                    indexBuffer.put(i++, b);
                    indexBuffer.put(i++, c);

                    indexBuffer.put(i++, b);
                    indexBuffer.put(i++, c);
                    indexBuffer.put(i++, d);
                }
            }
        }
        
        // Creating the vertex buffer
        {
        	vertexBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 3)
    			.order(ByteOrder.nativeOrder()).asFloatBuffer();
        	int i = 0;
            for(int x = 0; x < tilesPerRow; x++)
            {
            	for(int y = 0; y < tilesPerColumn; y++)
            	{
            		vertexBuffer.put(i++, x*Constants.TileWidth);
            		vertexBuffer.put(i++, y*Constants.TileWidth);
            		vertexBuffer.put(i++, 0);
            	}
            }
        }
        
        // Creating the color buffer
        {
        	Random rand = new Random();
        	
        	colorBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 4)
	    		.order(ByteOrder.nativeOrder()).asFloatBuffer();
        	int i = 0;
        	for(int x = 0; x < tilesPerRow; x++)
            {
            	for(int y = 0; y < tilesPerColumn; y++)
            	{
            		colorBuffer.put(i++, rand.nextFloat());
            		colorBuffer.put(i++, 0f);
            		colorBuffer.put(i++, rand.nextFloat());
            		colorBuffer.put(i++, 1f);
            	}
            }
        }
	    
	}

}
