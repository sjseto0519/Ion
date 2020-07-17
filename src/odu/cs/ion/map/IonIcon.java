package odu.cs.ion.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import android.graphics.Bitmap;


public class IonIcon
{
	
public Bitmap b;
public int posx;
public int posy;
	
IonIcon(Bitmap b, int posx, int posy)
{
	this.b = b;
	this.posx = posx;
	this.posy = posy;
}

public Bitmap getBitmap()
{
	return b;
}

public int[] getPosition()
{
	return new int[] {posx, posy};
}

public int getPosX()
{
	return posx;
}

public int getPosY()
{
	return posy;
}
	
}