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
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.game.MessageHandler;
import com.game.MsgType;
import com.game.Preferences;
import com.game.R;
import com.game.MessageHandler.MsgReceiver;

/**
 * ViewData for the Single Player scene.
 * 
 * @author NeoM
 *
 */

public class SingleViewData extends ViewData 
{
	private View auxView;

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
        
        // Center in screen at 80% width
        LinearLayout centerLinerarLayout = (LinearLayout)xmlLayout.findViewById(R.id.single_layout_inner_scroll);
        this.Set80PercentWidth(activity, centerLinerarLayout);
        
        // Callback for the buttons 
        Button okButton = (Button) xmlLayout.findViewById(R.id.ok_single_but);
        okButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("SingleViewData", "Clicked OK button");
            MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.BUTTON_CLICK, R.id.ok_single_but);
          }
        });
        
        Button backButton = (Button) xmlLayout.findViewById(R.id.back_single_but);
        backButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("SingleViewData", "Clicked Back button");
            MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.BUTTON_CLICK, R.id.back_single_but );
          }
        });
        
        // Callback for the checkboxes
        CheckBox minimapCheckBox = (CheckBox) xmlLayout.findViewById(R.id.minimap_single_check);
        minimapCheckBox.setChecked(Preferences.Get().singleShowMinmap);
        this.auxView = minimapCheckBox;
        minimapCheckBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Log.i("SingleViewData", "Clicked minimap checkbox");
				int checked = 0;
				if(((CheckBox) SingleViewData.this.auxView).isChecked() == true)
				{
					checked = 1;
				}
				else
				{
					checked = 0;
				}
				MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.CHECKBOX_CLICK, R.id.minimap_single_check, checked);	
			}
		});
        
        CheckBox powerupsCheckBox = (CheckBox) xmlLayout.findViewById(R.id.powerups_single_check);
        powerupsCheckBox.setChecked(Preferences.Get().singlePowerups);
        this.auxView = powerupsCheckBox;
        powerupsCheckBox.setOnClickListener(new OnClickListener() 
        {			
			@Override
			public void onClick(View v) {
				Log.i("SingleViewData", "Clicked power-ups checkbox");
				int checked = 0;
				if(((CheckBox) SingleViewData.this.auxView).isChecked() == true)
				{
					checked = 1;
				}
				else
				{
					checked = 0;
				}
				MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.CHECKBOX_CLICK, R.id.powerups_single_check, checked);	
			}
		});
        
        // Callback for the galleries
        Gallery mapsGallery = (Gallery) xmlLayout.findViewById(R.id.maps_single_gal);
        mapsGallery.setAdapter(new MapsImageAdapter(activity));
        mapsGallery.setSelection(Preferences.Get().singleCurrentMap);
        mapsGallery.setOnItemClickListener(new OnItemClickListener() 
        {
            public void onItemClick(AdapterView parent, View v, int position, long id) 
            {
            	Log.i("SingleViewData", "Clicked maps gallery item");
            	MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.GALLERY_ITEM_CLICK, R.id.maps_single_gal, position);
            }
        });
        
        // Callback for the spinners
        Spinner colorSpinner = (Spinner) xmlLayout.findViewById(R.id.color_single_spin);
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(
        		activity, R.array.color_array, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);
        colorSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) {
        		Log.i("SingleViewData", "Selected color spinner item");
        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SPINNER_ITEM_CLICK, R.id.color_single_spin, position);
        	}

        	public void onNothingSelected(AdapterView parent) {
        		Log.i("SingleViewData", "No color spinner item has been selected");
        	}

        });
        colorSpinner.setSelection(Preferences.Get().singlePlayer1Color);
        
        Spinner opponentsSpinner = (Spinner) xmlLayout.findViewById(R.id.op_single_spin);
        ArrayAdapter<CharSequence> opponentsAdapter = ArrayAdapter.createFromResource(
        		activity, R.array.op_single_array, android.R.layout.simple_spinner_item);
        opponentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opponentsSpinner.setAdapter(opponentsAdapter);
        opponentsSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) {
        		Log.i("SingleViewData", "Selected opponents spinner item");
        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SPINNER_ITEM_CLICK, R.id.op_single_spin, position);
        	}

        	public void onNothingSelected(AdapterView parent) {
        		Log.i("SingleViewData", "No opponents spinner item has been selected");
        	}

        });
        opponentsSpinner.setSelection(Preferences.Get().singleNumberOpponents-1);
        
        Spinner controlSpinner = (Spinner) xmlLayout.findViewById(R.id.control_single_spin);
        ArrayAdapter<CharSequence> controlAdapter = ArrayAdapter.createFromResource(
        		activity, R.array.control_mode_array, android.R.layout.simple_spinner_item);
        controlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controlSpinner.setAdapter(controlAdapter);
        controlSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) {
        		Log.i("SingleViewData", "Selected control spinner item");
        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SPINNER_ITEM_CLICK, R.id.control_single_spin, position);
        	}

        	public void onNothingSelected(AdapterView parent) {
        		Log.i("SingleViewData", "No control spinner item has been selected");
        	}

        });
        controlSpinner.setSelection(Preferences.Get().singleControlMode);
        
        return xmlLayout;
	}
}
