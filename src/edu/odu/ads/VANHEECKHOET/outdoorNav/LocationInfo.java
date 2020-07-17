package edu.odu.ads.VANHEECKHOET.outdoorNav;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import odu.cs.ion.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class LocationInfo extends Activity {
	
	/* Declaration of the variables */
	
	private static final String TAG = "OutdoorNav/LocationInfo"; // Log Tag
	
	LocationItem item;
	
	/* Declaration of the views */

	// Views
	private LinearLayout linear_info;
	private TextView title_text;
	private TextView snippet_text;
	private TextView group_text;
	private ImageView image;
	private TextView description_text;
	private TextView website_text; 
	
	// Listener
	private OnClickListener WebsiteClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//First Step
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationinfo);
		 
		//Creation of a new item with data
		item = new LocationItem();
		LocationItem.FillItem(item, getIntent().getStringExtra("itemInfo"));
		
		//Find views
		linear_info= (LinearLayout) findViewById(R.id.linear_info);
		title_text = (TextView) findViewById(R.id.item_title);
		snippet_text = (TextView) findViewById(R.id.item_snippet);
		group_text = (TextView) findViewById(R.id.item_group);		
		image = (ImageView) findViewById(R.id.item_image);
		description_text = (TextView) findViewById(R.id.item_description);
		website_text = (TextView) findViewById(R.id.item_website);
		
		//Modify views
		title_text.setText(item.getName());
		snippet_text.setText(item.getSnippet());
		group_text.setText("Region : " + item.getGroup());
		description_text.setText("Description : " + item.getDescription());
		website_text.setText("Website : " + item.getWebsite());
		setImage(image, item.getImage());

		// Set the listener for the website textview
		WebsiteClickListener = new OnClickListener () {
            public void onClick(View v) {
        			try 
                    {   
        				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.getWebsite())));
        			} 
                    catch (Exception e) {
                    	Log.d(TAG, "Exception : " + e.toString());
        			} 
             }
        };
        website_text.setOnClickListener(WebsiteClickListener);		
	}
	
	
	/** Modify the image with the given URL **/
	public void setImage(ImageView view, String url) {
		Bitmap bitmap = null;
		
		try {
			URL urlImage = new URL(url);	
			HttpURLConnection connection = (HttpURLConnection) urlImage.openConnection();	
			InputStream inputStream = connection.getInputStream();	
			bitmap = BitmapFactory.decodeStream(inputStream);	
			view.setImageBitmap(bitmap);
		}
		catch (Exception e) {				
			e.printStackTrace();
		}	 
		
		view.setImageBitmap(bitmap);
	}
	
	/** Modify the image with the given URL **/
	public static void setImageFromOut(ImageView view, String url) {
		Bitmap bitmap = null;
		
		try {
			URL urlImage = new URL(url);	
			HttpURLConnection connection = (HttpURLConnection) urlImage.openConnection();	
			InputStream inputStream = connection.getInputStream();	
			bitmap = BitmapFactory.decodeStream(inputStream);	
			view.setImageBitmap(bitmap);
		}
		catch (Exception e) {				
			e.printStackTrace();
		}	 
		
		view.setImageBitmap(bitmap);
	}
	/*
	public static LinearLayout getLinearLayoutItemInfo(LocationItem item) {
		
		LinearLayout linear_info = new LinearLayout(this);
		TextView title_text = new TextView(this);
		TextView snippet_text = new TextView(this);
		TextView group_text = new TextView(this);
		ImageView image = new ImageView(this);
		TextView description_text = new TextView(this);
		TextView website_text = new TextView(this);
		
		title_text.setText(item.getName());
		snippet_text.setText(item.getSnippet());
		group_text.setText("Region : " + item.getGroup());
		description_text.setText("Description : " + item.getDescription());
		website_text.setText("Website : " + item.getWebsite());
		setImage(image, item.getImage());
		
		linear_info.addView(title_text);
		linear_info.addView(snippet_text);
		linear_info.addView(group_text);
		linear_info.addView(image);
		linear_info.addView(description_text);
		linear_info.addView(website_text);
		
		return linear_info;		
	}
	*/
}