package com.example.hw4;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;

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

	ExecutorService threadpool;
	ProgressDialog pdMain;
	ImageView ivMain;
	URL url;
	String s[];
	Bitmap bm;
	int mode=0;
	int cur_image, size;
	float nextXClick,prevXClick,maxX,minX,maxY,minY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		
		s= getResources().getStringArray(R.array.photo_urls);
		
		ivMain = (ImageView) findViewById(R.id.ivMain);
		ivMain.setScaleType(ScaleType.FIT_CENTER);
		
		if (getIntent().getExtras() != null) {
			mode = getIntent().getExtras().getInt("MODE");
			cur_image = 0;
			size = s.length;
			new DoWork().execute();
		}

		if(mode==1){
			ivMain.setOnTouchListener(new OnTouchListener() {
	
				public boolean onTouch(View v, MotionEvent event) {
					Log.d("DEBUG","Touched");
					//TODO: view.setImageBitmap(imageloader.next());
					return false;
				}
			});
		}
		else{
			//TODO: init a slideshow with 2 second interval.
			// on2secondInterval -> view.setImageBitmap(imageloader.next());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}

	class DoWork extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			try {
				InputStream in = new java.net.URL(s[13]).openStream();
				bm = BitmapFactory.decodeStream(in);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			ivMain.setImageBitmap(bm);
			pdMain.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pdMain = new ProgressDialog(PhotoActivity.this);
			pdMain.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pdMain.setCancelable(false);
			pdMain.setTitle("Loading Image");
			pdMain.show();

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}

}
