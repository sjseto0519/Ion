package odu.cs.ion;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.util.Log;



public class IonXml {

	public String url;
	public String xml;
	public Document xmldoc;
	
	public IonXml(String url)
	{
		this.url = url;
	}
	
	public String getXml()
	{
		return xml;
	}
	
	public ClassInformation getClassInformation()
	{
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/iops/classes/class/starttime";
		String expression2 = "/iops/classes/class/endtime";
		String expression3 = "/iops/classes/class/classname";
		String expression4 = "/iops/classes/class/classnumber";
		String widgetNode = null, widgetNode2 = null, widgetNode3 = null, widgetNode4 = null;
		try {
		widgetNode = (String) xpath.evaluate(expression, xmldoc, XPathConstants.STRING);
		widgetNode2 = (String) xpath.evaluate(expression2, xmldoc, XPathConstants.STRING);
		widgetNode3 = (String) xpath.evaluate(expression3, xmldoc, XPathConstants.STRING);
		widgetNode4 = (String) xpath.evaluate(expression4, xmldoc, XPathConstants.STRING);
		
		}
		catch (Exception e) {
			Log.e("classinfoexception",Log.getStackTraceString(e));
			e.printStackTrace();}	
		return new ClassInformation(widgetNode, widgetNode2, widgetNode3, widgetNode4);
	}
	
	public int[] getPosition()
	{
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/iops/positionx";
		String expression2 = "/iops/positiony";
		double widgetNode = 0.0d, widgetNode2 = 0.0d;
		try {
		widgetNode = (Double) xpath.evaluate(expression, xmldoc, XPathConstants.NUMBER);
		widgetNode2 = (Double) xpath.evaluate(expression2, xmldoc, XPathConstants.NUMBER);
		
		}
		catch (Exception e) {e.printStackTrace();}
		int posx = (int)widgetNode;
		int posy = (int)widgetNode2;
		return new int[] {posx, posy};
		
	}
	
	public String getRoomNumber()
	{
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/iops/roomnumber";
		String widgetNode = null;
		try {
		  widgetNode = (String) xpath.evaluate(expression, xmldoc, XPathConstants.STRING);	
		}
		catch (Exception e) {e.printStackTrace();}
		return widgetNode;
		
	}
	
	public String getPlaceName()
	{
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/iops/placename";
		String widgetNode = null;
		try {
		  widgetNode = (String) xpath.evaluate(expression, xmldoc, XPathConstants.STRING);	
		}
		catch (Exception e) {e.printStackTrace();}
		return widgetNode;
		
	}
	
	public String getFloor()
	{
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/iops/floor";
		String widgetNode = null;
		try {
		  widgetNode = (String) xpath.evaluate(expression, xmldoc, XPathConstants.STRING);	
		}
		catch (Exception e) {e.printStackTrace();}
		return widgetNode;
		
	}
	
	public void parseUrl()
	{
		readUrl();
		xml = xml.replaceAll("&","&amp;");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		try {
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		xmldoc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		}
		catch (Exception e) {
			Log.e("exception",Log.getStackTraceString(e));
			e.printStackTrace();}
	}
	
	public void readUrl()
	{
		
		HttpURLConnection connection = null;
	      OutputStreamWriter wr = null;
	      BufferedReader rd  = null;
	      StringBuilder sb = null;
	      String line = null;
	    
	      URL serverAddress = null;
	    
	      try {
	          serverAddress = new URL(url);
	          //set up out communications stuff
	          connection = null;
	        
	          //Set up the initial connection
	          connection = (HttpURLConnection)serverAddress.openConnection();
	          connection.setRequestMethod("GET");
	          connection.setDoOutput(true);
	          connection.setReadTimeout(10000);
	                    
	          connection.connect();
	        
	          //get the output stream writer and write the output to the server
	          //not needed in this example
	          //wr = new OutputStreamWriter(connection.getOutputStream());
	          //wr.write("");
	          //wr.flush();
	        
	          //read the result from the server
	          rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	          sb = new StringBuilder();
	        
	          while ((line = rd.readLine()) != null)
	          {
	              sb.append(line + '\n');
	          }
	        
	          xml = sb.toString();
	                    
	      } catch (MalformedURLException e) {
	          e.printStackTrace();
	      } catch (ProtocolException e) {
	          e.printStackTrace();
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	      finally
	      {
	          //close the connection, set all objects to null
	          connection.disconnect();
	          rd = null;
	          sb = null;
	          wr = null;
	          connection = null;
	      }
		
	}
	
}
