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
	int mGalleryItemBackground;
	private Context mContext;
	
	private Integer[] mImageIds = {
            R.drawable.sample_1,
            R.drawable.sample_2,
            R.drawable.sample_3,
            R.drawable.sample_4,
            R.drawable.sample_5,
            R.drawable.sample_6,
            R.drawable.sample_7
    };

	/**
	 * Initializes the adapter
	 */
	public MapsImageAdapter(Context c) {
		mContext = c;
        TypedArray a = c.obtainStyledAttributes(R.styleable.mapsGallery);

        mGalleryItemBackground = a.getResourceId(
                R.styleable.mapsGallery_android_galleryItemBackground, 0);
        a.recycle();
	}
	
	/**
	 * Returns how many images the gallery has
	 */
	@Override
	public int getCount() {
		return mImageIds.length;
	}
	
	@Override
	public Object getItem(int position) {
		return position;
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
		ImageView i = new ImageView(mContext);
		
		i.setImageResource(mImageIds[position]);
        i.setLayoutParams(new Gallery.LayoutParams(150, 100));
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        i.setBackgroundResource(mGalleryItemBackground);

		
		return i;
	}

}

