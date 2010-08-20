package com.game.ViewData;


import com.game.MsgType;
import com.game.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Specific view data class for the "Options" screen.
 * @author Ying
 *
 */
public class OptionsViewData extends ViewData 
{

	/**
	 * @see ViewData createXMLView(Activity activity) 
	 */
	@Override
	public View createXMLView(Activity activity) 
	{
		Log.i("OptionsViewData", "createXMLView");
		
		// Access xml layout
		LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View xmlLayout = (View) li.inflate(R.layout.options, null);
        
        // Callback for the buttons & views
        
        Button okButton = (Button) xmlLayout.findViewById(R.id.ok_options_but);
        okButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("ViewData", " Clicked OK button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.ok_options_but, 0));
          }
        });
        
        Spinner numberUnitsSpin = (Spinner) xmlLayout.findViewById(R.id.num_units_spin);
        ArrayAdapter numberUnitsAdapter = ArrayAdapter.createFromResource(	activity, R.array.count_names, android.R.layout.simple_spinner_item);
        numberUnitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberUnitsSpin.setAdapter(numberUnitsAdapter);
        
        Spinner moveSpeedSpin = (Spinner) xmlLayout.findViewById(R.id.move_speed_spin);
        ArrayAdapter moveSpeedAdapter = ArrayAdapter.createFromResource(	activity, R.array.speed_names, android.R.layout.simple_spinner_item);
        moveSpeedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moveSpeedSpin.setAdapter(moveSpeedAdapter);
        
        Spinner eatSpeedSpin = (Spinner) xmlLayout.findViewById(R.id.eat_speed_spin);
        ArrayAdapter eatSpeedAdapter = ArrayAdapter.createFromResource(	activity, R.array.speed_names, android.R.layout.simple_spinner_item);
        eatSpeedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eatSpeedSpin.setAdapter(eatSpeedAdapter);
        
		return xmlLayout;
	}

}
