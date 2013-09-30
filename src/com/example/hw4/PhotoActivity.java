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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class PhotoActivity extends Activity {

	ExecutorService threadpool;
	ProgressDialog p;
	ImageView image;
	URL url;
	String s[] = getResources().getStringArray(R.array.photo_urls);
	Bitmap bm;
	int mode;
	int cur_image, size;
	float nextXClick,prevXClick,maxX,minX,maxY,minY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		image = (ImageView) findViewById(R.id.imageView1);
		if (getIntent().getExtras() != null) {
			mode = getIntent().getExtras().getInt("MODE");
			cur_image = 0;
			size = s.length;
			new DoWork().execute();
		}

		image.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (mode == 1) {
					//maxY = v.getHeight();
					//maxX = v.getWidth();
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						if (event.getX() > nextXClick && event.getX() < maxX
								&& event.getY() > minY && event.getY() < maxY)
							cur_image = (cur_image + 1) % size;
						else if(event.getX() > prevXClick && event.getX() < minX
								&& event.getY() > minY && event.getY() < maxY)
							cur_image = (cur_image - 1) % size;

					}
				}

				return false;
			}
		});
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
				InputStream in = new java.net.URL(s[cur_image]).openStream();
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
			image.setImageBitmap(bm);
			p.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			p = new ProgressDialog(PhotoActivity.this);
			p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			p.setCancelable(false);
			p.setTitle("Loading Image");
			p.show();

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}

}
