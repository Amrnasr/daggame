package edu.union;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class GLActivity extends Activity {
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
	
    protected boolean isFullscreenOpaque() {
        // Our main window is set to translucent, but we know that we will
        // fill it with opaque data. Tell the system that so it can perform
        // some important optimizations.
        return true;
    }
	
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		int type = getIntent().getExtras().getInteger(AndroidGL.GL_DRAW);
		
		View v = null;
		switch (type) {
		case FIRST:
			v = new GLTutorialOne(this);
			break;
		case SECOND:
			getWindow().setFormat(PixelFormat.TRANSLUCENT);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			
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
		}
		setContentView(v);
	}
}
