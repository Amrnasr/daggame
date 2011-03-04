package com.game.ViewData;

import com.game.MessageHandler;
import com.game.MsgType;
import com.game.Preferences;
import com.game.R;
import com.game.MessageHandler.MsgReceiver;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Specific view data class for the "About Us" screen.
 * @author Ying
 *
 */
public class GameOverViewData extends ViewData {

	/**
	 * @see ViewData createXMLView(Activity activity) 
	 */
	@Override
	public View createXMLView(Activity activity) {
		//Log.i("GameOverViewData", "createXMLView");
		
		// Access xml layout
		LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View xmlLayout = (View) li.inflate(R.layout.gameover, null);
        
        // Callback for the buttons
        Button okButton = (Button) xmlLayout.findViewById(R.id.ok_gameover_but);
        okButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) 
          {
        	  //Log.i("ViewData", " Clicked OK button");
            MessageHandler.Get().Send(MsgReceiver.LOGIC,MsgType.BUTTON_CLICK, R.id.ok_gameover_but);
          }
        });
        
        TextView winner = new TextView(activity);
        winner.setText("" + Preferences.Get().playerColor(Preferences.Get().winnerPlayerColorIndex) );
        winner.setGravity(Gravity.CENTER_HORIZONTAL);
        winner.setTextSize(80);
        LinearLayout textHolder = (LinearLayout) xmlLayout.findViewById(R.id.gameover_layout_inner_scroll);
        textHolder.addView(winner);
        
		return xmlLayout;
	}

}
