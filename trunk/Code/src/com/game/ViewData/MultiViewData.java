package com.game.ViewData;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Gallery;
import android.widget.Spinner;

import com.game.MsgType;
import com.game.R;

/**
 * ViewData for the Multiplayer scene.
 * 
 * @author NeoM
 *
 */

public class MultiViewData extends ViewData {

	/**
	 * @see ViewData createXMLView(Activity activity) 
	 */
	@Override
	public View createXMLView(Activity activity) 
	{
		Log.i("MultiViewData", "createXMLView");
		
		// Access xml layout
		LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View xmlLayout = (View) li.inflate(R.layout.multi, null);
        
        // Callback for the buttons
        Button fingerButton = (Button) xmlLayout.findViewById(R.id.finger_multi_but);
        fingerButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("MultiViewData", "Clicked finger button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.finger_multi_but, 0));
          }
        });
        
        Button ballButton = (Button) xmlLayout.findViewById(R.id.ball_multi_but);
        ballButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("MultiViewData", "Clicked ball button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.ball_multi_but, 0));
          }
        });
        
        Button joystickButton = (Button) xmlLayout.findViewById(R.id.joystick_multi_but);
        joystickButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("MultiViewData", "Clicked joystick button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.joystick_multi_but, 0));
          }
        });
        
        Button okButton = (Button) xmlLayout.findViewById(R.id.ok_multi_but);
        okButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("MultiViewData", "Clicked OK button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.ok_multi_but, 0));
          }
        });
        
        // Callback for the checkboxes
        CheckBox minimapCheckBox = (CheckBox) xmlLayout.findViewById(R.id.minimap_multi_check);
        minimapCheckBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("MultiViewData", "Clicked minimap checkbox");
				handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.CHECKBOX_CLICK.ordinal(), R.id.minimap_multi_check, 0));	
			}
		});
        
        CheckBox powerupsCheckBox = (CheckBox) xmlLayout.findViewById(R.id.powerups_multi_check);
        powerupsCheckBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("MultiViewData", "Clicked power-ups checkbox");
				handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.CHECKBOX_CLICK.ordinal(), R.id.powerups_multi_check, 0));	
			}
		});
        
        // Callback for the galleries
        Gallery mapsGallery = (Gallery) xmlLayout.findViewById(R.id.maps_multi_gal);
        mapsGallery.setAdapter(new MapsImageAdapter(activity));//mirar como pasar contexto como parametro

        mapsGallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
            	Log.i("MultiViewData", "Clicked maps gallery item");
            	handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.GALLERY_ITEM_CLICK.ordinal(), R.id.maps_multi_gal, position));
            }
        });
        
        // Callback for the spinners
        Spinner color1Spinner = (Spinner) xmlLayout.findViewById(R.id.color1_multi_spin);
        ArrayAdapter<CharSequence> color1Adapter = ArrayAdapter.createFromResource(
        		activity, R.array.color_array, android.R.layout.simple_spinner_item);
        color1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        color1Spinner.setAdapter(color1Adapter);
        color1Spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) {
        		Log.i("MultiViewData", "Selected color 1 spinner item");
        		handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.SPINNER_ITEM_CLICK.ordinal(), R.id.color1_multi_spin, position));
        	}

        	public void onNothingSelected(AdapterView parent) {
        		Log.i("MultiViewData", "No color 1 spinner item has been selected");
        	}

        });
        
        Spinner color2Spinner = (Spinner) xmlLayout.findViewById(R.id.color2_multi_spin);
        ArrayAdapter<CharSequence> color2Adapter = ArrayAdapter.createFromResource(
        		activity, R.array.color_array, android.R.layout.simple_spinner_item);
        color2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        color2Spinner.setAdapter(color2Adapter);
        color2Spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) {
        		Log.i("MultiViewData", "Selected color 2 spinner item");
        		handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.SPINNER_ITEM_CLICK.ordinal(), R.id.color2_multi_spin, position));
        	}

        	public void onNothingSelected(AdapterView parent) {
        		Log.i("MultiViewData", "No color 2 spinner item has been selected");
        	}

        });
        
        Spinner opponentsSpinner = (Spinner) xmlLayout.findViewById(R.id.op_multi_spin);
        ArrayAdapter<CharSequence> opponentsAdapter = ArrayAdapter.createFromResource(
        		activity, R.array.op_multi_array, android.R.layout.simple_spinner_item);
        opponentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opponentsSpinner.setAdapter(opponentsAdapter);
        opponentsSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) {
        		Log.i("MultiViewData", "Selected opponents spinner item");
        		handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.SPINNER_ITEM_CLICK.ordinal(), R.id.op_multi_spin, position));
        	}

        	public void onNothingSelected(AdapterView parent) {
        		Log.i("MultiViewData", "No opponents spinner item has been selected");
        	}

        });


        
        return xmlLayout;
	}

}
