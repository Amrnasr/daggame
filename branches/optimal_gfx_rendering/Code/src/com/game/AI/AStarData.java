package com.game.AI;

import java.util.Collections;
import java.util.Vector;

import com.game.Tile;

/**
 * Data class for the A* algorithm 
 * @author Ying
 *
 */
public class AStarData 
{
	/**
	 * Cost to get from the initial node to each node in the graph
	 */
	public float [] gCosts;
	
	/**
	 * Heuristic cost from this node to the destination node
	 */
	public float [] hCosts;
	
	/**
	 * Final cost. hCost + gCost
	 */
	public float [] fCosts;
	
	/**
	 * Set of tiles to visit
	 */
	public Vector<Tile> openSet;
	
	/**
	 * Set of tiles visited
	 */
	public Vector<Tile> closedSet;
	
	/**
	 * Whether or not the algorithm is done
	 */
	public boolean done;
	
	/**
	 * Initial tile to start the search at
	 */
	public Tile initialTile;
	
	/**
	 * Destination tile
	 */
	public Tile destinationTile;
	
	public int [] cameFrom;
	
	public AStarData() 
	{
		int mapSize = Blackboard.map.getTileMap().size();
		
		gCosts = new float[mapSize];
		fCosts = new float[mapSize];
		hCosts = new float[mapSize];
		cameFrom = new int [mapSize];
		
		for(int i = 0; i < mapSize; i++)
		{
			gCosts[i] = 0.0f;
			fCosts[i] = 0.0f;
			hCosts[i] = 0.0f;
			cameFrom[i] = -1;
		}
		
		openSet = new Vector<Tile>();
		closedSet = new Vector<Tile>();
		
		done = false;
	}
}

/**
 * public class Node implements Comparable<Node>
	{
		private int index;
		private float fCost;
		
		public Node(int pos, float fCost)
		{
			this.index = pos;
			this.fCost = fCost;
		}
		
		public int compareTo(Node o) 
		{
	        return (int) (100*(this.fCost - o.fCost)) ;
	    }
		
		public int Index() { return this.index;}
		
		public float FCost() { return this.fCost;}
		
		public void SetFCost(float newCost) { this.fCost = newCost; }
	}*/
