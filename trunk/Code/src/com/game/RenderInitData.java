package com.game;

import java.util.Vector;

import android.graphics.Bitmap;

public class RenderInitData 
{
	private Bitmap mapImage;
	private Vector<Tile> tileMap;
	private Vector<Cursor> cursors;
	
	public RenderInitData()
	{
		this.mapImage = null;
		this.tileMap = null;
		this.cursors = null;
	}
	
	public void SetCursors(Vector<Cursor> cursors) { this.cursors = cursors; }
	public void SetTileMap(Vector<Tile> tileMap) { this.tileMap = tileMap; }
	public void SetMapImage(Bitmap mapImage) { this.mapImage = mapImage; }
	
	public Vector<Cursor> GetCursors() { return this.cursors; }
	public Vector<Tile> GetTileMap() { return this.tileMap; }
	public Bitmap GetMapImage() { return this.mapImage; }
}
