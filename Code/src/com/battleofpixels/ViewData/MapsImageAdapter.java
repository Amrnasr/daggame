package com.game.battleofpixels.ViewData;

import com.game.battleofpixels.R;
import com.game.battleofpixels.Camera;
import com.game.battleofpixels.Constants;
import com.game.battleofpixels.Preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.util.Log;

/**
 * Adapter for the Maps gallery.
 * 
 * @author NeoM
 *
 */

public class MapsImageAdapter extends BaseAdapter {
	int galleryItemBackground;
	private Context context;
	
	private static Integer[] galleryIDs = {
        R.drawable.map1gallery,
        R.drawable.map2gallery,
        R.drawable.map3gallery,
        R.drawable.map4gallery,
        R.drawable.map5gallery,
        R.drawable.map6gallery,
        R.drawable.map7gallery,
        R.drawable.map8gallery,
        R.drawable.map9gallery
    };
	
	private static Integer[] imageIDs = {
        R.drawable.map1,
        R.drawable.map2,
        R.drawable.map3,
        R.drawable.map4,
        R.drawable.map5,
        R.drawable.map6,
        R.drawable.map7,
        R.drawable.map8,
        R.drawable.map9
    };
	
	private static Integer[] tilemapIDs = {
        R.raw.map1tilemap,
        R.raw.map2tilemap,
        R.raw.map3tilemap,
        R.raw.map4tilemap,
        R.raw.map5tilemap,
        R.raw.map6tilemap,
        R.raw.map7tilemap,
        R.raw.map8tilemap,
        R.raw.map9tilemap
	};
	
	private static Integer[] tilemapIDsLowDpi = {
        R.raw.map1tilemaplowdpi,
        R.raw.map2tilemaplowdpi,
        R.raw.map3tilemaplowdpi,
        R.raw.map4tilemaplowdpi,
        R.raw.map5tilemaplowdpi,
        R.raw.map6tilemaplowdpi,
        R.raw.map7tilemaplowdpi,
        R.raw.map8tilemaplowdpi,
        R.raw.map9tilemaplowdpi
	};

	/**
	 * Initializes the adapter
	 */
	public MapsImageAdapter(Context c) {
		context = c;
        TypedArray a = c.obtainStyledAttributes(R.styleable.mapsGallery);

        galleryItemBackground = a.getResourceId(
                R.styleable.mapsGallery_android_galleryItemBackground, 0);
        a.recycle();
        
        //Get screen density 
        Display display = ((WindowManager) c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();  
        DisplayMetrics dMetrics = new DisplayMetrics();
        
    	display.getMetrics(dMetrics);

    	Preferences.Get().tileWidth = (dMetrics.densityDpi == DisplayMetrics.DENSITY_LOW) ? Constants.TileWidthLowDpi : Constants.TileWidth;
	}
	
	/**
	 * Returns how many images the gallery has
	 */
	@Override
	public int getCount() {
		return imageIDs.length;
	}
	
	@Override
	public Integer getItem(int position) {
		return imageIDs[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * Creates, initializes and returns the view of the adapter 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView i = new ImageView(context);
		
		//Log.i("MapsImageAdapter","gallery position: " + position);
		i.setImageResource(galleryIDs[position]);
        i.setLayoutParams(new Gallery.LayoutParams(150, 100));
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        i.setBackgroundResource(galleryItemBackground);

		
		return i;
	}
	
	public static int getImageID(int position){
		return imageIDs[position];
	}
	
	public static int getTilemapID(int position){
		if (Preferences.Get().tileWidth == Constants.TileWidthLowDpi){
			return tilemapIDsLowDpi[position];
		}
		return tilemapIDs[position];
	}

}

