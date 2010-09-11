package edu.union.android;

import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import edu.union.graphics.IntMesh;
import edu.union.graphics.MD2Loader;
import edu.union.graphics.Model;

public class ModelViewer extends Activity {
	InputStream is;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		is = getResources().openRawResource(R.drawable.tris);
		
		MD2Loader ld = new MD2Loader();
		ld.setFactory(IntMesh.factory());
		
		try {
			Model model = ld.load(is, 0.1f, "skin.jpg");
			if (model.getFrameCount() > 1)
				setContentView(new ModelViewInterpolated(model, this));
			else
				setContentView(new ModelView(model, this));
		} 
		catch (java.io.IOException ex) {
			setContentView(R.layout.main);
		}
	}
}