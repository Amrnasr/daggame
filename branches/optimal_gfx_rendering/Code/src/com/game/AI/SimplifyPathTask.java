package com.game.AI;

import java.util.Vector;

import com.game.Tile;

/**
 * Removes the tiles of the path that are between 2 in a row
 * @author Ying
 *
 */
public class SimplifyPathTask extends LeafTask {

	public SimplifyPathTask(Blackboard blackboard) 
	{
		super(blackboard);
	}

	public SimplifyPathTask(Blackboard blackboard, String name) 
	{
		super(blackboard, name);
	}

	@Override
	public boolean CheckConditions() 
	{
		LogTask("Checking conditions");
		return bb.path != null;
	}
	


	@Override
	public void DoAction() 
	{
		LogTask("Doing action");
		
		// Less than 4 tiles it's kind of useless to simplify.
		if(bb.path.size() < 4)
		{
			this.control.FinishWithSuccess();
			return;
		}
		
		// Add the first tile 
		Vector<Tile> simplePath = new Vector<Tile>();
		simplePath.add(bb.path.firstElement());
		
		// Copy to a new vector only those that are not in the middle of a line
		for(int i = 1; i < bb.path.size()-1; i++)
		{
			Tile preTile = bb.path.elementAt(i-1);
			Tile curTile = bb.path.elementAt(i);
			Tile nexTile = bb.path.elementAt(i+1);
			if(!InLine(preTile, curTile, nexTile))
			{
				simplePath.add(curTile);
			}
			else
			{
				LogTask("Removed a tile! Tile: " + i + " (" + curTile.GetPos().X() + ", " + curTile.GetPos().Y() + ")" ); 
			}
		}
		// Add the last tile
		simplePath.add(bb.path.lastElement());
		
		// Set the simplified path as the path to follow
		bb.path = simplePath;
		this.control.FinishWithSuccess();
	}
	
	/**
	 * Gets whether the three tiles are in a straight contiguous line
	 * @param a
	 * @param b
	 * @param c
	 * @return True if they are, false if they aren't
	 */
	private boolean InLine(Tile a, Tile b, Tile c)
	{
		boolean inLine = false;
		
		if(		((int)(a.GetPos().X() - b.GetPos().X()) == (int)(b.GetPos().X() - c.GetPos().X())) &&
				((int)(a.GetPos().Y() - b.GetPos().Y()) == (int)(b.GetPos().Y() - c.GetPos().Y())))
		{
			inLine = true;
		}
		
		return inLine;
	}

	@Override
	public void End() 
	{
		LogTask("Ending");
	}

	@Override
	public void Start() 
	{
		LogTask("Starting");
	}

}
