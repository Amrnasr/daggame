package com.game.battleofpixels.ViewData;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.game.battleofpixels.R;
import com.game.battleofpixels.MessageHandler;
import com.game.battleofpixels.MsgType;
import com.game.battleofpixels.MessageHandler.MsgReceiver;

/**
 * ViewData for the Menu scene.
 * 
 * @author Ying
 *
 */
public class MenuViewData extends ViewData 
{
	/**
	 * @see ViewData createXMLView(Activity activity) 
	 */
	@Override public View createXMLView(Activity activity) 
	{
		//Log.i("MenuViewData", " ------------ createXMLView start");
		// Access xml layout
		LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View xmlLayout = (View) li.inflate(R.layout.menu, null);
        
        // Create button callbacks. 
        // It's important to create them from the xmlLayout object, not the activity, as
        // the activity has no layout assigned yet.
        Button singleButton = (Button) xmlLayout.findViewById(R.id.single_but);
        singleButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
        	  //Log.i("ViewData", " Clicked Single button");
            MessageHandler.Get().Send(MsgReceiver.LOGIC,MsgType.BUTTON_CLICK, R.id.single_but);
          }
        });
        
        Button multiButton = (Button) xmlLayout.findViewById(R.id.multi_but);
        multiButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
        	  //Log.i("ViewData", " Clicked Multiplayer button");
            MessageHandler.Get().Send(MsgReceiver.LOGIC,MsgType.BUTTON_CLICK, R.id.multi_but);
          }
        });
        
        Button optionsButton = (Button) xmlLayout.findViewById(R.id.options_but);
        optionsButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
        	  //Log.i("ViewData", " Clicked Options button");
            MessageHandler.Get().Send(MsgReceiver.LOGIC,MsgType.BUTTON_CLICK, R.id.options_but);
          }
        });
        
        Button howButton = (Button) xmlLayout.findViewById(R.id.how_but);
        howButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
        	  //Log.i("ViewData", " Clicked How to play button");
            MessageHandler.Get().Send(MsgReceiver.LOGIC,MsgType.BUTTON_CLICK, R.id.how_but);
          }
        });
        
        Button aboutButton = (Button) xmlLayout.findViewById(R.id.about_but);
        aboutButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
        	  //Log.i("ViewData", " Clicked About us button");
            MessageHandler.Get().Send(MsgReceiver.LOGIC,MsgType.BUTTON_CLICK, R.id.about_but);
          }
        });
        
        
		return xmlLayout;
	}

}
