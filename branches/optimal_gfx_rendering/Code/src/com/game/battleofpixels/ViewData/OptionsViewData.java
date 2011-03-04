package com.game.battleofpixels.ViewData;


import com.game.battleofpixels.R;
import com.game.battleofpixels.MessageHandler;
import com.game.battleofpixels.MsgType;
import com.game.battleofpixels.Preferences;
import com.game.battleofpixels.MessageHandler.MsgReceiver;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Specific view data class for the "Options" screen.
 * @author Ying
 *
 */
public class OptionsViewData extends ViewData 
{
	// Used to access data inside the OnClickListener classes
	private View auxView = null;

	/**
	 * @see ViewData createXMLView(Activity activity) 
	 */
	@Override
	public View createXMLView(Activity activity) 
	{
		//Log.i("OptionsViewData", "createXMLView");
		
		// Access xml layout
		LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View xmlLayout = (View) li.inflate(R.layout.options, null);

        // Center in screen at 80% width
        LinearLayout centerLinerarLayout = (LinearLayout)xmlLayout.findViewById(R.id.options_layout_inner_scroll);
        this.Set80PercentWidth(activity, centerLinerarLayout);

        // Callback for the button      
        Button okButton = (Button) xmlLayout.findViewById(R.id.ok_options_but);
        okButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
        	  //Log.i("ViewData", " Clicked OK button");
            MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.BUTTON_CLICK, R.id.ok_options_but);
          }
        });
        
        // Callback for the spinners
        Spinner numberUnitsSpin = (Spinner) xmlLayout.findViewById(R.id.num_units_spin);
        ArrayAdapter numberUnitsAdapter = ArrayAdapter.createFromResource(	activity, R.array.count_names, android.R.layout.simple_spinner_item);
        numberUnitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberUnitsSpin.setAdapter(numberUnitsAdapter);
        numberUnitsSpin.setOnItemSelectedListener(new OnItemSelectedListener()
        {
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) 
        	{
        		//Log.i("OptionsViewData", "Selected number units spinner item");
        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SPINNER_ITEM_CLICK, R.id.num_units_spin, position);
        	}

        	public void onNothingSelected(AdapterView<?> parent) {
        		//Log.i("OptionsViewData", "No number units spinner item has been selected");
        	}

        });
        numberUnitsSpin.setSelection(Preferences.Get().optionsUnitCuantity);
        
        /*
        Spinner moveSpeedSpin = (Spinner) xmlLayout.findViewById(R.id.move_speed_spin);
        ArrayAdapter moveSpeedAdapter = ArrayAdapter.createFromResource(	activity, R.array.speed_names, android.R.layout.simple_spinner_item);
        moveSpeedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moveSpeedSpin.setAdapter(moveSpeedAdapter);
        moveSpeedSpin.setOnItemSelectedListener(new OnItemSelectedListener()
        {
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) 
        	{
        		Log.i("OptionsViewData", "Selected move speed spinner item");
        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SPINNER_ITEM_CLICK, R.id.move_speed_spin, position);
        	}

        	public void onNothingSelected(AdapterView<?> parent) {
        		Log.i("OptionsViewData", "No eat speed spinner item has been selected");
        	}

        });
        moveSpeedSpin.setSelection(Preferences.Get().optionsUnitMoveSpeed);
        
        <TextView
				android:id="@+id/widget42"
				android:layout_width="fill_parent"
				android:layout_height="wrap_content"
				android:layout_marginTop="10px"
				android:text="Unit movement speed:"
				>
			</TextView>
			<Spinner
				android:id="@+id/move_speed_spin"
				android:layout_width="fill_parent"
				android:layout_height="wrap_content"
				>
			</Spinner>
			
        */
        
        Spinner eatSpeedSpin = (Spinner) xmlLayout.findViewById(R.id.eat_speed_spin);
        ArrayAdapter eatSpeedAdapter = ArrayAdapter.createFromResource(	activity, R.array.speed_names, android.R.layout.simple_spinner_item);
        eatSpeedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eatSpeedSpin.setAdapter(eatSpeedAdapter);
        eatSpeedSpin.setOnItemSelectedListener(new OnItemSelectedListener()
        {
        	public void onItemSelected(AdapterView<?> parent,
        			View view, int position, long id) 
        	{
        		//Log.i("OptionsViewData", "Selected eat speed spinner item");
        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.SPINNER_ITEM_CLICK, R.id.eat_speed_spin, position);
        	}

        	public void onNothingSelected(AdapterView<?> parent) {
        		//Log.i("OptionsViewData", "No eat speed spinner item has been selected");
        	}

        });
        eatSpeedSpin.setSelection(Preferences.Get().optionsUnitEatSpeed);
        
        // Callback for the checkboxes
        /*
        CheckBox soundCheckBox = (CheckBox) xmlLayout.findViewById(R.id.sound_mute);
        soundCheckBox.setChecked(Preferences.Get().optionsSoundMute);
        
        this.auxView = soundCheckBox;
        soundCheckBox.setOnClickListener(new OnClickListener() 
        {			
			@Override
			public void onClick(View v) {
				Log.i("OptionsViewData", "Clicked mute");				
				
				int checked = 0;
				if(((CheckBox) OptionsViewData.this.auxView).isChecked() == true)
				{
					checked = 1;
				}
				else
				{
					checked = 0;
				}
				
				MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.CHECKBOX_CLICK, R.id.sound_mute, checked);	
			}
		});
		
		<CheckBox
				android:id="@+id/sound_mute"
				android:layout_width="fill_parent"
				android:layout_height="wrap_content"
				android:text="Sound"
				>
			</CheckBox>
		*/
        
		return xmlLayout;
	}

}
