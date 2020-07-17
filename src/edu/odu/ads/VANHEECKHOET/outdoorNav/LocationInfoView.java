package edu.odu.ads.VANHEECKHOET.outdoorNav;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import odu.cs.ion.DualScreenApiSampleActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LocationInfoView extends LinearLayout {

	private final DualScreenApiSampleActivity dualScreenApiSample;
	private TextView title_text;
	private TextView snippet_text;
	private TextView group_text;
	private ImageView image;
	private TextView description_text;
	private TextView website_text; 
	
	// Listener
	private OnClickListener WebsiteClickListener;
	
	public LocationInfoView(Context context) {
		super(context);

		this.dualScreenApiSample = (DualScreenApiSampleActivity) context;
		TextView title_text = new TextView(context);
		TextView snippet_text = new TextView(context);
		TextView group_text = new TextView(context);
		ImageView image = new ImageView(context);
		TextView description_text = new TextView(context);
		TextView website_text = new TextView(context);
		
		this.addView(title_text);
		this.addView(snippet_text);
		this.addView(group_text);
		this.addView(image);
		this.addView(description_text);
		this.addView(website_text);
	}
	
	public void fillContent (LocationItem item)
	{
		title_text.setText(item.getName());
		snippet_text.setText(item.getSnippet());
		group_text.setText("Region : " + item.getGroup());
		description_text.setText("Description : " + item.getDescription());
		website_text.setText(item.getWebsite());
		setImage(image, item.getImage());
		
		// Set the listener for the website textview
		WebsiteClickListener = new OnClickListener () {
            public void onClick(View v) {
        			try 
                    {   
        				String str = new String();
        				dualScreenApiSample.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String) website_text.getText())));
        			} 
                    catch (Exception e) {
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
			image.setImageBitmap(bitmap);
		}
		catch (Exception e) {				
			e.printStackTrace();
		}	 
		
		view.setImageBitmap(bitmap);
	}
}
