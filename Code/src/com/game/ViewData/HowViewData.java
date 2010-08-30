package com.game.ViewData;

import com.game.MsgType;
import com.game.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Specific view data class for the "How to play" screen.
 * @author Ying
 *
 */
public class HowViewData extends ViewData 
{

	/**
	 * @see ViewData createXMLView(Activity activity) 
	 */
	@Override
	public View createXMLView(Activity activity) 
	{
		Log.i("HowViewData", "createXMLView");
		
		// Access xml layout
		LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View xmlLayout = (View) li.inflate(R.layout.how, null);
        
        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
        int width = display.getWidth(); 
        int height = display.getHeight();
        
        // Centers the text and makes it fit inside the column width
        // NOTES: 
        // - It crashes if LinearLayout.LayoutParams is used, instead of FrameLayout.LayoutParams
        // no idea why it thinks the about_layout_inner_scroll is a FrameLayout.
        // - Setting a LayoutParams object seems to override the default params. ALL of them, not
        // just w & h, so gravity must be re-specified here, even tough it's ok in the xml
        // - Had to change the AndroidManifest/Uses SDK /Target SDK & Min SDK cause it was emulating
        // HVGA in all AVD  devices. This seems to be the default behavior if no Target & Min SDK
        // are configured.
        // - Confirmed it works ok in QVGA, HVGA and WVGA
        LinearLayout centerLinerarLayout = (LinearLayout)xmlLayout.findViewById(R.id.about_layout_inner_scroll);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)(width/1.3), FrameLayout.LayoutParams.FILL_PARENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        
        centerLinerarLayout.setLayoutParams(lp);
        
        // Callback for the buttons
        Button okButton = (Button) xmlLayout.findViewById(R.id.ok_how_but);
        okButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("ViewData", " Clicked OK button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.ok_how_but, 0));
          }
        });
        
		return xmlLayout;
	}

}
