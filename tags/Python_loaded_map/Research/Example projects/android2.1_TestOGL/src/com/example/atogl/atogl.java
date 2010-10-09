package com.example.atogl;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.RelativeLayout;

public class atogl extends Activity {
	public static final int FIRST = 0;
	public static final int SECOND = 1;
	public static final int THIRD = 2;
	public static final int FOURTH = 3;
	public static final int FIFTH = 4;
	public static final int SIXTH = 5;
	public static final int SEVENTH = 6;
	public static final int EIGHTH = 7;
	public static final int NINTH = 8;
	public static final int TENTH = 9;
	public static final int ELEVENTH = 10;
	public static final int TWELFTH = 11;
	public static final int THIRTEENTH = 12;
	public static final int FOURTEENTH = 13;
	
	GLTutorialBase v = null;
	Profiler profiler = null;
	
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		profiler = new Profiler();
		
		int type = SECOND;
		switch (type) {
		case FIRST:
			v = new GLTutorialOne(this);
			break;
		case SECOND:
			v = new GLTutorialTwo(this);
			break;
		case THIRD:
			v = new GLTutorialThree(this);
			break;
		case FOURTH:
			v = new GLTutorialFour(this);
			break;
		case FIFTH:
			v = new GLTutorialFive(this);
			break;
		case SIXTH:
			v = new GLTutorialSix(this);
			break;
		case SEVENTH:
			v = new GLTutorialSeven(this);
			break;
		case EIGHTH:
			v = new GLTutorialEight(this);
			break;
		case NINTH:
			v = new GLTutorialNine(this);
			break;
		case TENTH:
			v = new GLTutorialTen(this);
			break;
		case ELEVENTH:
			v = new GLTutorialEleven(this);
			break;
		case TWELFTH:
			v = new GLTutorialTwelve(this);
			break;
		case THIRTEENTH:
			v = new GLTutorialThirteen(this);
			break;
		case FOURTEENTH:
			v = new GLTutorialFourteen(this);
			break;
		}
		v.setRenderer(v);
        v.setFocusable(true);
        v.setHandler(handler);
        
        // Get the inflater to access the layout in xml
        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout) li.inflate(R.layout.main, null);   
        
        // Add the GLlayout
        layout.addView(v, 0);
        
        // Add profiler GUI
        profiler.Attach(layout, this);
        
        setContentView(layout);
        
    }
	
	final Handler handler = new Handler() 
	{
        public void handleMessage(Message msg) 
        {
        	profiler.Update();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        v.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        v.onResume();
    }
}

