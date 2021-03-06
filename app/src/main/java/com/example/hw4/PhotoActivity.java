/*********************************
 * Assignment #4
 * FileName: PhotoActivity.java
 *********************************
 * Team Members:
 * Richa Kandlikar
 * Sai Phaninder Reddy Jonnala
 * *******************************
 */
package com.example.hw4;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
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
	Timer timer;
	LruCache<String, Bitmap> mMemoryCache;
	
	private Runnable Timer_Tick = new Runnable() {
	    public void run() {
	    	//This method runs in the same thread as the UI.               
	    	//Do something to the UI thread here
	    	ivMain.setImageBitmap(bm);
	    	Log.d("DEBUG", CurrentImage+": Image Set");
			new GetImage(FORWARD).execute();
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		CurrentImage=0;
		timer=new Timer();
		ImageUrls= getResources().getStringArray(R.array.photo_urls);
		
		
		ivMain = (ImageView) findViewById(R.id.ivMain);
		ivMain.setScaleType(ScaleType.FIT_CENTER);
		
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}

		};
		
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
	public void onStop(){
		super.onStop();
		timer.cancel();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}
	
	private void TimerMethod()
	{
	    //This method is called directly by the timer
	    //and runs in the same thread as the timer.

	    //We call the method that will work with the UI
	    //through the runOnUiThread method.
	    this.runOnUiThread(Timer_Tick);
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
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

				if (getBitmapFromMemCache(ImageUrls[CurrentImage]) != null){
					bm = getBitmapFromMemCache(ImageUrls[CurrentImage]);
					Log.d("DEBUG", CurrentImage+": Get from CACHE");
				}
				else {
					Log.d("DEBUG", CurrentImage+": start download");
					InputStream in = new java.net.URL(ImageUrls[CurrentImage])
							.openStream();
					bm = BitmapFactory.decodeStream(in);
					addBitmapToMemoryCache(ImageUrls[CurrentImage], bm);
					Log.d("DEBUG", CurrentImage+": done");
				}

				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(mode==1){
				ivMain.setImageBitmap(bm);
				Log.d("DEBUG", CurrentImage+": Image Set");
				pdMain.dismiss();
			}
			else{
				if(this.dir==0){
					ivMain.setImageBitmap(bm);
					Log.d("DEBUG", CurrentImage+": Initial Image Set as soon as downloaded");
					new GetImage(FORWARD).execute();
				}
				else{
					try{
						timer.schedule(new TimerTask(){
							@Override
							public void run() {
								TimerMethod();
							}
						}, 2000);
						Log.d("DEBUG", CurrentImage+": Timer start");
					}
					catch(Exception e){}
				}
			}
			//Log.d("DEBUG","bitmapSize: "+bm.getByteCount()/(1024));
		}

		@Override
		protected void onPreExecute() {
			if(mode==1){
				pdMain = new ProgressDialog(PhotoActivity.this);
				pdMain.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pdMain.setCancelable(false);
				pdMain.setMessage("Loading Image");
				pdMain.show();
			}
			else{
				
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
		}

	}

}
