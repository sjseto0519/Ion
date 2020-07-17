package odu.cs.ion.database;

import java.util.HashMap;

public class GridData {
	
	HashMap<String, Float[]> gridmap;
	HashMap<String, Integer[]> gridprob;
	public String position;
	
	GridData(String pos)
	{
		position = pos;
		gridmap = new HashMap<String,Float[]>();
		gridprob = new HashMap<String,Integer[]>();
	}
	
	public GridData()
	{
		gridmap = new HashMap<String,Float[]>();
		gridprob = new HashMap<String,Integer[]>();
	}
	
	public HashMap<String, Float[]> getGridMap()
	{
		return gridmap;
	}
	
	public HashMap<String, Integer[]> getGridProb()
	{
		return gridprob;
	}
	
	public String getPosition()
	{
		return position;
	}
	
	public void setProb(String a, boolean b)
	{
		if (!gridprob.containsKey(a))
		{
			if (b == false)
			{
			Integer[] aa = new Integer[] {0,1};
			gridprob.put(a, aa);
			}
			else
			{
				Integer[] aa = new Integer[] {1,1};
				gridprob.put(a, aa);	
			}
		}
		else
		{
		  Integer[] aa = gridprob.get(a);
		  if (b == false)
		  {
			  aa[1] = aa[1]+1;
		  }
		  else
		  {
			  aa[0] = aa[0]+1;
			  aa[1] = aa[1]+1;
		  }
		}
	}
	
	public void add(String a, Float[] b)
	{
		if (!gridmap.containsKey(a))
		{
			gridmap.put(a, b);
		}
		else
		{
			Float[] bb = (Float[])gridmap.get(a);
			Float[] cc = merge(b, bb);
			gridmap.remove(a);
			gridmap.put(a, cc);
		}
	}
	
	public Float[] merge(Float[] a, Float[] b)
	{
		int length = a.length;
		Float[] c = new Float[length];
		for (int i = 0; i < length; ++i)
		{
			c[i] = (a[i]+b[i])/2;
		}
		return c;
		
	}

}
