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

import com.game.PowerUp.PowerUp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
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
	private Vector<Vec3> combatPosVector;
	
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

		tilesPerRow = bitmap.getWidth() / Preferences.Get().tileWidth;
		tilesPerColumn = bitmap.getHeight() / Preferences.Get().tileWidth;		
		
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

			//Log.i("Map", "Line size: " + line.length);
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
				catch(IndexOutOfBoundsException iob)
				{
					//Log.e("Map", "Exception on i:" + i + " line.lenght: " + line.length + " tilesPerRow: " + tilesPerRow);					
				}

				tileMap.addElement(new Tile(i,j, value, this));
			}
		}
		
		// Create the data for map drawing
		GenerateDrawMesh();
		
		this.combatPosVector = new Vector<Vec3>();
		//DrawDebugLines(bitmap);
		
		Log.i("Map", "Tiles: " + tilesPerRow + ", " + tilesPerColumn + " total: " + tileMap.size());
	}
	
	private void DrawDebugLines(Bitmap bitmap2)
	{
		Bitmap bitmap = bitmap2.copy(Bitmap.Config.ARGB_4444, true);
		for(int i = 0; i< bitmap.getWidth(); i += 100)
		{
			for(int j = 0; j < bitmap.getHeight(); j++)
			{
				bitmap.setPixel(i, j, Color.MAGENTA);
			}
		}
		
		for(int i = 0; i < bitmap.getHeight(); i += 100)
		{
			for(int j = 0; j < bitmap.getWidth(); j++)
			{
				bitmap.setPixel(j, i, Color.MAGENTA);
			}
		}
		bitmap2 = bitmap;
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
	/**
	 * Returns the current combat position vector
	 * @return the related vector.
	 */
	public Vector<Vec3> getCombatPosVector(){
		return combatPosVector;
	}
	
	public void Start() {
	}
	
	public void Update(Vector<Player> players) 
	{
		if(players == null) { return; }
		
		synchronized (combatPosVector){
			//clean the fighting tiles vector
			for(int i = 0; i < combatPosVector.size(); i++){
				if(combatPosVector.elementAt(i).Z() >= Constants.CombatEffectImgNum){
					combatPosVector.remove(i);
				}	
			}
		}
		Random r = new Random();
		
		// Set the tile colors
		for(int i = 0; i < players.size(); i++)
		{
			Player player = players.elementAt(i);
			Vector<Tile> tiles = player.GetTiles();
			
			for(int j = 0; j < tiles.size(); j++)
			{
				Tile tile = tiles.elementAt(j);
				if(tile.HasBeenColorUpdated())
				{
					float density = 1 - (tile.GetCurrentDensity() / tile.GetMaxCapacity());
					SetColor(tile.GetRealPos(), density, 0, 0, density);
					
					boolean exists = false;
					
					for(int k = 0; k < combatPosVector.size(); k++){
						if(combatPosVector.elementAt(k).X() == tile.GetRealPos().X() && combatPosVector.elementAt(k).Y() == tile.GetRealPos().Y()){
							exists = true;
						}	
					}
					if (!exists && r.nextFloat() < Constants.CombatEffectChance)
						combatPosVector.addElement(new Vec3(tile.GetRealPos().X(),tile.GetRealPos().Y(),0));
				}
				else
				{
					int colorIndex = player.GetColorIndex();
					float density = (tile.GetDensityFrom(player.GetID()) * 0.25f / tile.GetMaxCapacity());
					this.UpdateTileToColor(colorIndex, density, tile.GetRealPos());
				}
				
				tile.FlagAsColorUpdated();
			}
		}
	}
	
	private void UpdateTileToColor(int colorIndex, float density, Vec2 realPos)
	{
		float r = 0, g = 0, b = 0, a = 0;
		if(density > 0f)
		{
			switch(colorIndex)
			{
				case 0: //Brown
					r += density + 0.1f;
					g += density + 0.43f;
					b += density + 0.62f;
					
					a += density;
					break;
				case 1: //Green
					g += density;
					
					a += density;
					break;
				case 2: //Blue
					b += density;
					
					a += density;
					break;	
				case 3: //Cyan
					g += density;
					b += density;
					
					a += density;
					break;
				case 4: //Purple
					r += density;
					b += density;
					
					a += density;
					break;
				case 5: //Yellow
					r += density;
					g += density;
					
					a += density;
					break;
			}	
			
			//Add the base intensity if necessary
			r = (r > 0f) ? (0.75f - r) : 0f; 
			g = (g > 0f) ? (0.75f - g) : 0f;
			b = (b > 0f) ? (0.75f - b) : 0f;
			a = (r+g+b > 0f) ? 0.75f + a : 0f;
			
			//Log.i("Map", "r: " + r + " g: " + g + " b: " + b + " a: " + a );
			SetColor(realPos, r, g, b, a);
		}
	}
	
	public void End(){
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
		return AtTile(x/Preferences.Get().tileWidth, y/Preferences.Get().tileWidth);
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
			
			if(aux == null)
			{
				// Probably doing some debugging
				aux = AtTile(0, 0);
				toSearch.clear();
				Log.e("Map", "Using dummy start position!");
				break;
			}
			
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
			
			// Probably doing some debugging
			objectiveTile = AtTile(0, 0);
			Log.e("Map", "Using dummy start position!");
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
	
	/**
	 * Creates the index, vertex and color meshes for the map.
	 */
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
                    indexBuffer.put(i++, d);
                    indexBuffer.put(i++, c);
                }
            }
        }
        
        // Creating the vertex buffer
        {
        	vertexBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 3)
    			.order(ByteOrder.nativeOrder()).asFloatBuffer();
        	int i = 0;
            for(int y = 0; y < tilesPerColumn; y++)
            {
            	for(int x = 0; x < tilesPerRow; x++)
            	{
            		vertexBuffer.put(i++, x*Preferences.Get().tileWidth);
            		vertexBuffer.put(i++, y*Preferences.Get().tileWidth);
            		vertexBuffer.put(i++, 0);
            	}
            }
        }
        
        // Creating the color buffer
        {
        	colorBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 4)
	    		.order(ByteOrder.nativeOrder()).asFloatBuffer();
        	int i = 0;
        	for(int y = 0; y < tilesPerColumn; y++)
            {
        		for(int x = 0; x < tilesPerRow; x++)
            	{
            		colorBuffer.put(i++, 1f);
            		colorBuffer.put(i++, 1f);
            		colorBuffer.put(i++, 1f);
            		colorBuffer.put(i++, 1f);
            	}
            }
        }
	}
	
	/**
	 * Sets the color of a tile of the render map
	 * @param realPos Position of the tile to change color
	 * @param r Red
	 * @param g Green
	 * @param b Blue
	 * @param a Alpha
	 */
	public synchronized void SetColor(Vec2 realPos, float r, float g, float b, float a)
	{
		// Get index
		final int index = (int) ((realPos.Y()/Preferences.Get().tileWidth)*this.tilesPerRow + realPos.X()/Preferences.Get().tileWidth);
		final int colorIndex = index * 4;
		
		this.colorBuffer.put(colorIndex, r);
		this.colorBuffer.put(colorIndex + 1, g);
		this.colorBuffer.put(colorIndex + 2, b);
		this.colorBuffer.put(colorIndex + 3, a);
	}
}
