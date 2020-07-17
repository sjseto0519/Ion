package odu.cs.ion.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import odu.cs.ion.path.NavNode;
import odu.cs.ion.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class IonDrawable extends Drawable {

	private final Paint mPaint;
	private final Paint mPaint2;
    private final RectF mRect;
    private Bitmap bmp;
    private Matrix matrix;
    private List<NavNode> path = null;
    private Context context;
    private float starticonx = -1.0f;
    private float starticony = -1.0f;
    private Bitmap starticon;
    private float personiconx = -1.0f;
    private float personicony = -1.0f;
    private Bitmap personicon;
    private float endiconx = -1.0f;
    private float endicony = -1.0f;
    private float pointx = -1.0f;
    private float pointy = -1.0f;
    private Bitmap endicon;
    private long time;
    private Paint[] paints;
    private int gridwidth;
    private int gridheight;
    private int gridx = -1;
    private int gridy = -1;
    private int paintnum = 0;
    private Map gridmap;
    private float markerposx = -1.0f;
    private float markerposy = -1.0f;
    private float centerposx = -1.0f;
    private float centerposy = -1.0f;
    private float circlex = -1.0f;
    private float circley = -1.0f;
    public Resources resources;
    public Bitmap bmpmarker;
    public Bitmap pngcircle;
    public ArrayList<IonIcon> ionicons = new ArrayList<IonIcon>();
    
    public IonDrawable(Context cc, Bitmap bmp, Matrix m)
    {
    	context = cc;
        mPaint = new Paint();
        mPaint2 = new Paint();
        mRect = new RectF();
        this.bmp = bmp;
        this.matrix = m;
        gridwidth = (int)Math.floor(bmp.getWidth()/50.0d);
        gridheight = (int)Math.floor(bmp.getHeight()/50.0d);
        
        setPaints();
        gridmap = new HashMap<Integer[], Integer>();
    }
    
    public void setResources(Resources r)
    {
    	resources = r;
    }
    
    public void setPaints()
    {
    	int total = gridwidth*gridheight;
    	if (total > 216) total = 216;
    	paints = new Paint[total];
    	int l = 0;
    	boolean end = false;
    	for (int i = 0; i < 255; i+=50)
    	{
    		for (int j = 0; j < 255; j+= 50)
    		{
    			for (int k = 0; k < 255; k+=50)
    			{
    				paints[l] = new Paint();
    				paints[l].setARGB(55, i, j, k);
    				paints[l].setStyle(Style.FILL);
    				l++;
    				if (l == total) {
    					end = true;
    					break;
    				}
    			}
    			if (end) break;
    		}
    		if (end) break;
    	}
    	
    }
    
    public void movePoint(int counter)
    {
    	pointx = (float)getNode(counter).getPosition().getX();
    	pointy = (float)getNode(counter).getPosition().getY();
    	invalidateSelf();
    }
    
    public void placePerson(int a, int b)
    {
    	pointx = (float)a*50.0f+25.0f;
    	pointy = (float)b*50.0f+25.0f;
    	personiconx = pointx;
    	personicony = pointy;
    	invalidateSelf();
    }
    
    public void circlePixel(int[] a)
    {
    	pointx = (float)a[0];
    	pointy = (float)a[1];
    	circlex = pointx;
    	circley = pointy;
    	invalidateSelf();
    }
    
    public void setCircleBitmap(Bitmap b)
    {
    	pngcircle = b;
    }
    
    public void placePersonByPixel(int a, int b)
    {
    	pointx = (float)a;
    	pointy = (float)b;
    	personiconx = pointx;
    	personicony = pointy;
    	invalidateSelf();
    }
    
    public void placeIcon(Bitmap b, int posx, int posy)
    {
    	IonIcon ii = new IonIcon(b, posx, posy);
    	ionicons.add(ii);
    }
    
    public void centerAtPosition(int[] a)
    {
    	centerposx = a[0];
    	centerposy = a[1];
    	invalidateSelf();
    }
    
    public void createMarker(int a, int b, String nn)
    {
    	if (bmpmarker == null)
    	{
    	bmpmarker = BitmapFactory.decodeResource(resources, odu.cs.ion.R.drawable.mapmarker);
        bmpmarker = Bitmap.createScaledBitmap(bmpmarker, 36, 36, true);
    	}
        markerposx = a;
        markerposy = b;
        invalidateSelf();
    }
    
    public void colorGrid(int a, int b)
    {
    	gridx = a;
    	gridy = b;
    	if (!gridcontains(new Integer[]{gridx,gridy})) {
    		
    		
    		gridmap.put(new Integer[]{gridx,gridy}, new Integer(paintnum));
    	
    	paintnum++;
    	}
    	invalidateSelf();
    }
    
    public boolean gridcontains(Integer[] a)
    {
    	Iterator ii = gridmap.keySet().iterator();
    	while (ii.hasNext())
    	{
    		Integer[] aa = (Integer[])ii.next();
    		if (aa[0] == a[0] && aa[1] == a[1]) {
    			//Toast.makeText(context, "contains", Toast.LENGTH_SHORT).show();
    	        
    			return true;
    		}
    	}
    	return false;
    }
    
    public NavNode getNode(int i)
    {
      return path.get(i);	
    }
    
    public void placeStartIcon(Bitmap bmp3, float a, float b)
    {
    	starticon = bmp3;
    	starticonx = a;
    	starticony = b;
    }
    
    public void placePersonIcon(Bitmap bmp3, float a, float b)
    {
    	personicon = bmp3;
    	//personiconx = a;
    	//personicony = b;
    	//pointx = personiconx;
    	//pointy = personicony;
    }
    
    public void placeEndIcon(Bitmap bmp3, float a, float b)
    {
    	endicon = bmp3;
    	endiconx = a;
    	endicony = b;
    }
    
    public void refresh()
    {
    	invalidateSelf();
    }
    
    public void setPath(List<NavNode> path)
    {
    	this.path = path;
    	invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas)
    {
    	canvas.drawBitmap(bmp, matrix, mPaint);
    	//Toast.makeText(context, ""+bmp.getHeight()+" "+bmp.getWidth(), Toast.LENGTH_SHORT).show();
    	
        // Set the correct values in the Paint
        mPaint.setARGB(255, 0, 255, 0);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Style.FILL);
        mPaint2.setARGB(100, 150, 150, 150);
        mPaint2.setStrokeWidth(1);
        mPaint2.setStyle(Style.STROKE);

        //drawLine(canvas, 50.0f, 0.0f, 50.0f, 0.0f);
        
        //drawLine(canvas, 50.0f, 50.0f, 50.0f, 0.0f);
        
        //drawLine(canvas, 50.0f, 100.0f, 50.0f, 0.0f);
        
        canvas.save();
        
        if (path != null)
        {
        	Iterator<NavNode> itr = path.iterator();
            NavNode element = (NavNode)itr.next();
            float nx = (float)element.getPosition().getX();
            float ny = (float)element.getPosition().getY();
            int i = 0;
            float nx2 = 0.0f;
            float ny2 = 0.0f;
            float nx3 = 0.0f;
            float ny3 = 0.0f;
            while (itr.hasNext()) {
            	i++;
            	
            	if (i > 1)
            	{
            		NavNode element3 = (NavNode)itr.next();
                    nx3 = (float)element3.getPosition().getX();
                    ny3 = (float)element3.getPosition().getY()+25;
                    
                    drawLine(canvas, nx2, ny2, nx3, ny3);
            	}
            	if (!itr.hasNext()) break;
                NavNode element2 = (NavNode)itr.next();
                nx2 = (float)element2.getPosition().getX();
                ny2 = (float)element2.getPosition().getY()+25;
                
                if (i == 1) drawLine(canvas, nx, ny, nx2, ny2);
                else
                {
                	drawLine(canvas, nx3, ny3, nx2, ny2);
                }
            }
        }
        canvas.restore();
        
        if (gridmap.size()>0)
        {
        	Iterator ii = gridmap.keySet().iterator();
        	while (ii.hasNext())
        	{
        		Integer[] key = (Integer[])ii.next();
        		Integer value = (Integer)gridmap.get(key);
        		//Toast.makeText(context, "addtogrid "+key[0]+" "+key[1]+" "+value+" "+gridmap.size(), Toast.LENGTH_SHORT).show();
    	        
        		drawOnGrid(canvas, key, value);
        	}
        }
        
        if (starticonx >= 0.0f)
        {
        	drawStartIcon(canvas);
        }
        
        if (personiconx >= 0.0f)
        {
        	drawPersonIcon(canvas);
        }
        
        if (endiconx >= 0.0f)
        {
        	drawEndIcon(canvas);
        }
        
        if (markerposx >= 0.0f)
        {
        	drawMarker(canvas);
        }
        
        if (circlex >= 0.0f)
        {
        	drawCircle(canvas);
        }
        
   
        
        for (int i = 0; i < ionicons.size(); ++i)
        {
        	IonIcon ii = ionicons.get(i);
        	drawIcon(ii, canvas);
        }
        
        // drawGridLines
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        for (int i = 0; i < height; i+= 50)
        {
        	drawLine2(canvas, 0, i, width-1, i);
        }
        for (int i = 0; i < width; i+= 50)
        {
        	drawLine2(canvas, i, 0, i, height-1);
        }
        
    }
    
    public void drawStartIcon(Canvas canvas)
    {
    	//canvas.restore();
    	canvas.save();
    	matrix.preTranslate(starticonx, starticony);
    	matrix.preTranslate(-11.0f, -20.0f);
    	canvas.drawBitmap(starticon, matrix, mPaint);
    	matrix.postTranslate(11.0f, 20.0f);
    	matrix.postTranslate(starticonx*-1.0f, starticony*-1.0f);
    	canvas.restore();
    	
    }
    
    public void drawOnGrid(Canvas canvas, Integer[] a, Integer b)
    {
    	canvas.save();
    	matrix.preTranslate(a[0]*50.0f, (a[1]*50.0f)+77);
    	canvas.setMatrix(matrix);
    	canvas.drawRect(new Rect(0,0,50,50), paints[b]);
    	matrix.postTranslate((a[0]*50.0f)*-1.0f, ((a[1]*50.0f)+77)*-1.0f);
    	canvas.restore();
    }
    
    public void drawPersonIcon(Canvas canvas)
    {
    	//canvas.restore();
    	canvas.save();
    	matrix.preTranslate(pointx, pointy);
    	matrix.preTranslate(-11.0f, -20.0f);
    	canvas.drawBitmap(personicon, matrix, mPaint);
    	matrix.postTranslate(11.0f, 20.0f);
    	matrix.postTranslate(pointx*-1.0f, pointy*-1.0f);
    	canvas.restore();
    	
    }
    
    public void drawMarker(Canvas canvas)
    {
    	//canvas.restore();
    	canvas.save();
    	matrix.preTranslate(markerposx, markerposy);
    	matrix.preTranslate(-11.0f, -20.0f);
    	canvas.drawBitmap(bmpmarker, matrix, mPaint);
    	matrix.postTranslate(11.0f, 20.0f);
    	matrix.postTranslate(markerposx*-1.0f, markerposy*-1.0f);
    	canvas.restore();
    }
    
    public void drawCircle(Canvas canvas)
    {
    	//canvas.restore();
    	canvas.save();
    	matrix.preTranslate(circlex, circley);
    	matrix.preTranslate(-11.0f, -20.0f);
    	canvas.drawBitmap(pngcircle, matrix, mPaint);
    	matrix.postTranslate(11.0f, 20.0f);
    	matrix.postTranslate(circlex*-1.0f, circley*-1.0f);
    	canvas.restore();
    }
    
    public void drawIcon(IonIcon a, Canvas canvas)
    {
    	//canvas.restore();
    	canvas.save();
    	matrix.preTranslate(a.getPosX(), a.getPosY());
    	matrix.preTranslate(-11.0f, -20.0f);
    	canvas.drawBitmap(a.getBitmap(), matrix, mPaint);
    	matrix.postTranslate(11.0f, 20.0f);
    	matrix.postTranslate(a.getPosX()*-1.0f, a.getPosY()*-1.0f);
    	canvas.restore();
    }
    
    public void drawEndIcon(Canvas canvas)
    {
    	//canvas.restore();
    	canvas.save();
    	matrix.preTranslate(endiconx, endicony);
    	matrix.preTranslate(-11.0f, -20.0f);
    	canvas.drawBitmap(endicon, matrix, mPaint);
    	matrix.postTranslate(11.0f, 20.0f);
    	matrix.postTranslate(endiconx*-1.0f, endicony*-1.0f);
    	canvas.restore();
    	
    }
    
    public void drawLine(Canvas canvas, float startx, float starty, float endx, float endy)
    {
    	//Toast.makeText(context, ""+startx+" "+starty+" "+endx+" "+endy, Toast.LENGTH_SHORT).show();
    	float movex = (endx-startx);
    	float movey = (endy-starty);
        matrix.preTranslate(startx, starty+77.0f);
    	canvas.setMatrix(matrix);
        canvas.drawLine(0.0f, 0.0f, movex, movey, mPaint);
        //canvas.drawCircle(0.0f, 0.0f, 4.0f, mPaint);
        matrix.postTranslate(startx*-1.0f, (starty+77.0f)*-1.0f);
    }
    
    public void drawLine2(Canvas canvas, float startx, float starty, float endx, float endy)
    {
    	//Toast.makeText(context, ""+startx+" "+starty+" "+endx+" "+endy, Toast.LENGTH_SHORT).show();
    	float movex = (endx-startx);
    	float movey = (endy-starty);
        matrix.preTranslate(startx, starty+77.0f);
    	canvas.setMatrix(matrix);
        canvas.drawLine(0.0f, 0.0f, movex, movey, mPaint2);
        matrix.postTranslate(startx*-1.0f, (starty+77.0f)*-1.0f);
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void setAlpha(int arg0)
    {
    }

    @Override
    public void setColorFilter(ColorFilter arg0)
    {
    }
    
}
