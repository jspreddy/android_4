package com.example.hw4;

import java.io.InputStream;
import java.net.MalformedURLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PhotoActivity extends Activity {

	ProgressDialog pdMain;
	ImageView ivMain;
	String ImageUrls[];
	Bitmap bm;
	int CurrentImage;
	int mode=0;
	int FORWARD=1, BACKWARD=-1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		CurrentImage=0;
		
		ImageUrls= getResources().getStringArray(R.array.photo_urls);
		
		
		ivMain = (ImageView) findViewById(R.id.ivMain);
		ivMain.setScaleType(ScaleType.FIT_CENTER);
		
		if (getIntent().getExtras() != null) {
			mode = getIntent().getExtras().getInt("MODE");
		}
		else{mode=1;}
		
		new GetImage(0).execute();
		
		if(mode==1){
			ivMain.setOnTouchListener(new OnTouchListener() {
	
				public boolean onTouch(View v, MotionEvent event) {
					//Log.d("DEBUG","touch_x: "+event.getX());
					//Log.d("DEBUG", "width: "+v.getWidth());
					float width = v.getWidth();
					float x=event.getX();
					float percentage = (x/width) * 100;
					Log.d("DEBUG","percentage: "+percentage);
					if(percentage > 80.0f){
						new GetImage(FORWARD).execute();
					}
					else if(percentage < 20.0f){
						new GetImage(BACKWARD).execute();
					}
						
					return false;
				}
			});
		}
		else{
			//TODO: init a slideshow with 2 second interval.
		}
		Log.d("DEBUG", "maxMem: "+ (Runtime.getRuntime().maxMemory()/1024)/1024 );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}

	class GetImage extends AsyncTask<Void, Integer, Void> {

		int dir;
		GetImage(int dir){
			if(dir == BACKWARD){
				this.dir = -1;
			}
			else if(dir == FORWARD){
				this.dir = 1;
			}
			else{
				this.dir=0;
			}
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				CurrentImage+=this.dir;
				if(CurrentImage >= ImageUrls.length){
					CurrentImage=0;
				}
				else if(CurrentImage<0){
					CurrentImage=ImageUrls.length-1;
				}
				InputStream in = new java.net.URL(ImageUrls[CurrentImage]).openStream();
				bm = BitmapFactory.decodeStream(in);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			ivMain.setImageBitmap(bm);
			pdMain.dismiss();
			//Log.d("DEBUG","bitmapSize: "+bm.getByteCount()/(1024));
		}

		@Override
		protected void onPreExecute() {
			pdMain = new ProgressDialog(PhotoActivity.this);
			pdMain.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pdMain.setCancelable(false);
			pdMain.setMessage("Loading Image");
			pdMain.show();

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
		}

	}

}
