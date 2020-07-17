package odu.cs.ion;

public class ClassInformation {

	public String starttime;
	public String endtime;
	public String classname;
	public String classnumber;
	
	public ClassInformation()
	{
		
	}
	
	public ClassInformation(String a, String b, String c, String d)
	{
		starttime = a;
		endtime = b;
		classname = c;
		classnumber = d;
	}
	
	public String getStartTime()
	{
		return starttime;
	}
	
	public String getEndTime()
	{
		return endtime;
	}
	
	public String getClassName()
	{
		return classname;
	}
	
	public String getClassNumber()
	{
		return classnumber;
	}
	
}
