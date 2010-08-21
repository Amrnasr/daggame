package com.game.ViewData;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Spinner;

import com.game.MsgType;
import com.game.R;


public class SingleViewData extends ViewData {

	/**
	 * @see ViewData createXMLView(Activity activity) 
	 */
	@Override
	public View createXMLView(Activity activity) 
	{
		Log.i("SingleViewData", "createXMLView");
		
		// Access xml layout
		LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View xmlLayout = (View) li.inflate(R.layout.single, null);
        
        // Callback for the buttons
        Button fingerButton = (Button) xmlLayout.findViewById(R.id.finger_single_but);
        fingerButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("ViewData", "Clicked finger button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.finger_single_but, 0));
          }
        });
        
        Button ballButton = (Button) xmlLayout.findViewById(R.id.ball_single_but);
        ballButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("ViewData", "Clicked ball button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.ball_single_but, 0));
          }
        });
        
        Button joystickButton = (Button) xmlLayout.findViewById(R.id.joystick_single_but);
        joystickButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("ViewData", "Clicked joystick button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.joystick_single_but, 0));
          }
        });
        
        Button okButton = (Button) xmlLayout.findViewById(R.id.ok_single_but);
        okButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("ViewData", "Clicked OK button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.ok_single_but, 0));
          }
        });
        
        // Callback for the checkboxes
        CheckBox minimapCheckBox = (CheckBox) xmlLayout.findViewById(R.id.minimap_single_check);
        minimapCheckBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("ViewData", "Clicked minimap checkbox");
				handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.CHECKBOX_CLICK.ordinal(), R.id.minimap_single_check, 0));	
			}
		});
        
        CheckBox powerupsCheckBox = (CheckBox) xmlLayout.findViewById(R.id.powerups_single_check);
        powerupsCheckBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("ViewData", "Clicked power-ups checkbox");
				handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.CHECKBOX_CLICK.ordinal(), R.id.powerups_single_check, 0));	
			}
		});
        
        // Callback for the galleries
        Gallery mapsGallery = (Gallery) xmlLayout.findViewById(R.id.maps_single_gal);
        mapsGallery.setAdapter(new MapsImageAdapter(activity));//mirar como pasar contexto como parametro

        mapsGallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
            	Log.i("ViewData", "Clicked maps gallery item");
            	handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.GALLERY_ITEM_CLICK.ordinal(), R.id.maps_single_gal, position));
            }
        });
        
        // Callback for the spinners
        Spinner colorSpinner = (Spinner) xmlLayout.findViewById(R.id.color_single_spin);
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(
        		activity, R.array.color_array, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);
        colorSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) {
        		Log.i("ViewData", "Selected color spinner item");
        		handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.SPINNER_ITEM_CLICK.ordinal(), R.id.color_single_spin, position));
        	}

        	public void onNothingSelected(AdapterView parent) {
        		Log.i("ViewData", "No color spinner item has been selected");
        	}

        });
        
        Spinner opponentsSpinner = (Spinner) xmlLayout.findViewById(R.id.op_single_spin);
        ArrayAdapter<CharSequence> opponentsAdapter = ArrayAdapter.createFromResource(
        		activity, R.array.op_array, android.R.layout.simple_spinner_item);
        opponentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opponentsSpinner.setAdapter(opponentsAdapter);
        opponentsSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) {
        		Log.i("ViewData", "Selected opponents spinner item");
        		handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.SPINNER_ITEM_CLICK.ordinal(), R.id.op_single_spin, position));
        	}

        	public void onNothingSelected(AdapterView parent) {
        		Log.i("ViewData", "No opponents spinner item has been selected");
        	}

        });


        
        return xmlLayout;
	}
	
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


		public MapsImageAdapter(Context c) {
			mContext = c;
	        TypedArray a = c.obtainStyledAttributes(R.styleable.mapsGallery);

	        mGalleryItemBackground = a.getResourceId(
	                R.styleable.mapsGallery_android_galleryItemBackground, 0);
	        a.recycle();

	    }
		
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

}
