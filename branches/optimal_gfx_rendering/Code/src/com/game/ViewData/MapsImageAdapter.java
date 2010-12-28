package com.game.ViewData;

import com.game.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Adapter for the Maps gallery.
 * 
 * @author NeoM
 *
 */

public class MapsImageAdapter extends BaseAdapter {
	int galleryItemBackground;
	private Context context;
	
	private static Integer[] imageIDs = {
        R.drawable.samplemap,
        R.drawable.map_size800_1
    };
	
	private static Integer[] tilemapIDs = {
        R.raw.samplemaptilemap,
        R.raw.map_size800_1tilemap
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
		
		i.setImageResource(imageIDs[position]);
        i.setLayoutParams(new Gallery.LayoutParams(150, 100));
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        i.setBackgroundResource(galleryItemBackground);

		
		return i;
	}
	
	public static int getImageID(int position){
		return imageIDs[position];
	}
	
	public static int getTilemapID(int position){
		return tilemapIDs[position];
	}

}

