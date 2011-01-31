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
import com.game.InputDevice.AIInputDevice;
import com.game.MessageHandler.MsgReceiver;
import com.game.Constants;
import com.game.MsgType;
import com.game.Player;
import com.game.Preferences;
import com.game.R;

/**
 * ViewData for the Multiplayer scene.
 * 
 * @author NeoM
 *
 */

public class MultiViewData extends ViewData {

	private View minimapCheckBoxView;
	private View powerupsCheckBoxView;
	private View color1SpinnerView;
	private View color2SpinnerView;
	
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
        
        // Center in screen at 80% width
        LinearLayout centerLinerarLayout = (LinearLayout)xmlLayout.findViewById(R.id.multi_layout_inner_scroll);
        this.Set80PercentWidth(activity, centerLinerarLayout);
        
        // Callback for the buttons
        Button okButton = (Button) xmlLayout.findViewById(R.id.ok_multi_but);
        okButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("MultiViewData", "Clicked OK button");
            MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.BUTTON_CLICK, R.id.ok_multi_but);
          }
        });
        
        Button backButton = (Button) xmlLayout.findViewById(R.id.back_multi_but);
        backButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("MultiViewData", "Clicked Back button");
            MessageHandler.Get().Send(MsgReceiver.LOGIC,MsgType.BUTTON_CLICK, R.id.back_multi_but);
          }
        });
        
        // Callback for the checkboxes
        CheckBox minimapCheckBox = (CheckBox) xmlLayout.findViewById(R.id.minimap_multi_check);
        minimapCheckBox.setChecked(Preferences.Get().multiShowMinimap);
        this.minimapCheckBoxView = minimapCheckBox;
        minimapCheckBox.setOnClickListener(new OnClickListener() 
        {			
			@Override
			public void onClick(View v) 
			{
				Log.i("MultiViewData", "Clicked minimap checkbox");
				
				int checked = 0;
				if(((CheckBox) MultiViewData.this.minimapCheckBoxView).isChecked() == true)
				{
					checked = 1;
				}
				
				
				MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.CHECKBOX_CLICK, R.id.minimap_multi_check, checked);	
			}
		});
        
        CheckBox powerupsCheckBox = (CheckBox) xmlLayout.findViewById(R.id.powerups_multi_check);
        powerupsCheckBox.setChecked(Preferences.Get().multiPowerups);
        this.powerupsCheckBoxView = powerupsCheckBox;
        powerupsCheckBox.setOnClickListener(new OnClickListener() 
        {			
			@Override
			public void onClick(View v) 
			{
				int checked = 0;
				if(((CheckBox) MultiViewData.this.powerupsCheckBoxView).isChecked() == true)
				{
					checked = 1;
				}
				else
				{
					checked = 0;
				}
				Log.i("MultiViewData", "Clicked power-ups checkbox");
				MessageHandler.Get().Send(MsgReceiver.LOGIC,MsgType.CHECKBOX_CLICK, R.id.powerups_multi_check, checked);	
			}
		});
        
        // Callback for the galleries
        Gallery mapsGallery = (Gallery) xmlLayout.findViewById(R.id.maps_multi_gal);
        mapsGallery.setAdapter(new MapsImageAdapter(activity));
        mapsGallery.setSelection(Preferences.Get().multiCurrentMap);
        mapsGallery.setOnItemClickListener(new OnItemClickListener() 
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	Log.i("MultiViewData", "Clicked maps gallery item");
            	MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.GALLERY_ITEM_CLICK, R.id.maps_multi_gal, position);
            }
        });
        
        // Callback for the spinners
        Spinner color1Spinner = (Spinner) xmlLayout.findViewById(R.id.color1_multi_spin);
        ArrayAdapter<CharSequence> color1Adapter = ArrayAdapter.createFromResource(
        		activity, R.array.color_array, android.R.layout.simple_spinner_item);
        color1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        color1Spinner.setAdapter(color1Adapter);
        color1Spinner.setSelection(Preferences.Get().multiPlayer1Color);
        this.color1SpinnerView = color1Spinner;
        color1Spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) {
        		Log.i("MultiViewData", "Selected color 1 spinner item");
        		long color2SpinnerIndex = ((Spinner) MultiViewData.this.color2SpinnerView).getSelectedItemId();
        		//If the color has already been chosen
        		if(color2SpinnerIndex == position){
        			//Choose a different color
        			for(int i = 0; i < Constants.MaxPlayers; i++){
        				if(i != color2SpinnerIndex){
        					((Spinner) MultiViewData.this.color1SpinnerView).setSelection(i);
        					MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SPINNER_ITEM_CLICK, R.id.color1_multi_spin, i);
        					return;
        				}	
        			}		
        		}
        		
        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SPINNER_ITEM_CLICK, R.id.color1_multi_spin, position);
        	}

        	public void onNothingSelected(AdapterView<?> parent) {
        		Log.i("MultiViewData", "No color 1 spinner item has been selected");
        	}

        });
        
        //TODO: disabling the color used by the other color spinner
        Spinner color2Spinner = (Spinner) xmlLayout.findViewById(R.id.color2_multi_spin);
        ArrayAdapter<CharSequence> color2Adapter = ArrayAdapter.createFromResource(
        		activity, R.array.color_array, android.R.layout.simple_spinner_item);
        color2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        color2Spinner.setAdapter(color2Adapter);
        color2Spinner.setSelection(Preferences.Get().multiPlayer2Color);
        this.color2SpinnerView = color2Spinner;
        color2Spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) {
        		Log.i("MultiViewData", "Selected color 2 spinner item");
        		long color1SpinnerIndex = ((Spinner) MultiViewData.this.color1SpinnerView).getSelectedItemId();
        		//If the color has already been chosen
        		if(color1SpinnerIndex == position){
        			//Choose a different color
        			for(int i = 0; i < Constants.MaxPlayers; i++){
        				if(i != color1SpinnerIndex){
        					((Spinner) MultiViewData.this.color2SpinnerView).setSelection(i);
        					MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SPINNER_ITEM_CLICK, R.id.color2_multi_spin, i);
        					return;
        				}
        			}		
        		}
        		
        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SPINNER_ITEM_CLICK, R.id.color2_multi_spin, position);
        	}

        	public void onNothingSelected(AdapterView<?> parent) {
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
        		MessageHandler.Get().Send(MsgReceiver.LOGIC,MsgType.SPINNER_ITEM_CLICK, R.id.op_multi_spin, position);
        	}

        	public void onNothingSelected(AdapterView<?> parent) {
        		Log.i("MultiViewData", "No opponents spinner item has been selected");
        	}
        });
        opponentsSpinner.setSelection(Preferences.Get().multiNumberOpponents-1);
        /*
        Spinner controlSpinner = (Spinner) xmlLayout.findViewById(R.id.control_multi_spin);
        ArrayAdapter<CharSequence> controlAdapter = ArrayAdapter.createFromResource(
        		activity, R.array.control_multi_mode_array, android.R.layout.simple_spinner_item);
        controlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controlSpinner.setAdapter(controlAdapter);
        controlSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) {
        		Log.i("MultiViewData", "Selected control spinner item");
        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SPINNER_ITEM_CLICK, R.id.control_multi_spin, position);
        	}

        	public void onNothingSelected(AdapterView<?> parent) {
        		Log.i("MultiViewData", "No control spinner item has been selected");
        	}
        });
        controlSpinner.setSelection(Preferences.Get().multiControlMode);
        
        For the xml:
        <TextView
				android:id="@+id/control_multi_layout"
				android:layout_width="fill_parent"
				android:layout_height="wrap_content"
				android:text="Player 1 control mode:"
				>
			</TextView>
			<Spinner
				android:id="@+id/control_multi_spin"
				android:layout_width="fill_parent"
				android:layout_height="wrap_content"
				android:prompt="@string/control_prompt"
				>
			</Spinner>
			<TextView
				android:id="@+id/control2_multi_layout"
				android:layout_width="fill_parent"
				android:layout_height="wrap_content"
				android:text="Player 2 control mode is the track ball."
				>
			</TextView>
		
		Remember to edit in MultiSelectScene: handleSpinnerItemClick
*/

        
        return xmlLayout;
	}

}
