package odu.cs.ion.gps;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import android.util.FloatMath;

public class GPSUtilities {

	public static float getDistance(float lat1, float long1, float lat2, float long2)
	{
		float pk = (float) (180/3.14169);

	    float a1 = lat1 / pk;
	    float a2 = long1 / pk;
	    float b1 = lat2 / pk;
	    float b2 = long2 / pk;

	    float t1 = FloatMath.cos(a1)*FloatMath.cos(a2)*FloatMath.cos(b1)*FloatMath.cos(b2);
	    float t2 = FloatMath.cos(a1)*FloatMath.sin(a2)*FloatMath.cos(b1)*FloatMath.sin(b2);
	    float t3 = FloatMath.sin(a1)*FloatMath.sin(b1);
	    double tt = Math.acos(t1 + t2 + t3);
	   
	    return (float) (6366000*tt);
	}
	
}