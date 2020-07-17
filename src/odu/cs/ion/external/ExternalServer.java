package odu.cs.ion.external;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ExternalServer {
	
	public String sql;
	public String placessql;

	public ExternalServer()
	{
		
		URL u;
	      InputStream is = null;
	      DataInputStream dis;
	      String s;

	      try {

	         //------------------------------------------------------------//
	         // Step 2:  Create the URL.                                   //
	         //------------------------------------------------------------//
	         // Note: Put your real URL here, or better yet, read it as a  //
	         // command-line arg, or read it from a file.                  //
	         //------------------------------------------------------------//

	         u = new URL("http://scott-seto.com/appdoublescreen/Sql/exportdata.php");

	         //----------------------------------------------//
	         // Step 3:  Open an input stream from the url.  //
	         //----------------------------------------------//

	         is = u.openStream();         // throws an IOException

	         //-------------------------------------------------------------//
	         // Step 4:                                                     //
	         //-------------------------------------------------------------//
	         // Convert the InputStream to a buffered DataInputStream.      //
	         // Buffering the stream makes the reading faster; the          //
	         // readLine() method of the DataInputStream makes the reading  //
	         // easier.                                                     //
	         //-------------------------------------------------------------//

	         dis = new DataInputStream(new BufferedInputStream(is));

	         //------------------------------------------------------------//
	         // Step 5:                                                    //
	         //------------------------------------------------------------//
	         // Now just read each record of the input stream, and print   //
	         // it out.  Note that it's assumed that this problem is run   //
	         // from a command-line, not from an application or applet.    //
	         //------------------------------------------------------------//

	         while ((s = dis.readLine()) != null) {
	            sql += s;
	         }

	      } catch (MalformedURLException mue) {

	         System.out.println("Ouch - a MalformedURLException happened.");
	         mue.printStackTrace();
	         System.exit(1);

	      } catch (IOException ioe) {

	         System.out.println("Oops- an IOException happened.");
	         ioe.printStackTrace();
	         System.exit(1);

	      } finally {

	         //---------------------------------//
	         // Step 6:  Close the InputStream  //
	         //---------------------------------//

	         try {
	            is.close();
	         } catch (IOException ioe) {
	            // just going to ignore this one
	         }

	      } // end of 'finally' clause
		
	      
	      try {

		         //------------------------------------------------------------//
		         // Step 2:  Create the URL.                                   //
		         //------------------------------------------------------------//
		         // Note: Put your real URL here, or better yet, read it as a  //
		         // command-line arg, or read it from a file.                  //
		         //------------------------------------------------------------//

		         u = new URL("http://scott-seto.com/appdoublescreen/Sql/exportdataplaces.php");

		         //----------------------------------------------//
		         // Step 3:  Open an input stream from the url.  //
		         //----------------------------------------------//

		         is = u.openStream();         // throws an IOException

		         //-------------------------------------------------------------//
		         // Step 4:                                                     //
		         //-------------------------------------------------------------//
		         // Convert the InputStream to a buffered DataInputStream.      //
		         // Buffering the stream makes the reading faster; the          //
		         // readLine() method of the DataInputStream makes the reading  //
		         // easier.                                                     //
		         //-------------------------------------------------------------//

		         dis = new DataInputStream(new BufferedInputStream(is));

		         //------------------------------------------------------------//
		         // Step 5:                                                    //
		         //------------------------------------------------------------//
		         // Now just read each record of the input stream, and print   //
		         // it out.  Note that it's assumed that this problem is run   //
		         // from a command-line, not from an application or applet.    //
		         //------------------------------------------------------------//

		         while ((s = dis.readLine()) != null) {
		            placessql += s;
		         }

		      } catch (MalformedURLException mue) {

		         System.out.println("Ouch - a MalformedURLException happened.");
		         mue.printStackTrace();
		         System.exit(1);

		      } catch (IOException ioe) {

		         System.out.println("Oops- an IOException happened.");
		         ioe.printStackTrace();
		         System.exit(1);

		      } finally {

		         //---------------------------------//
		         // Step 6:  Close the InputStream  //
		         //---------------------------------//

		         try {
		            is.close();
		         } catch (IOException ioe) {
		            // just going to ignore this one
		         }

		      } // end of 'finally' clause
	      
	}
	
	
	
	public ExternalServer(WifiManager wifiManager)
	{
		
		URL u;
	      InputStream is = null;
	      DataInputStream dis;
	      String s;
	      URLConnection yc = null;
	      BufferedReader in = null;
	      
	      for (;;) {

	      try {

	         //------------------------------------------------------------//
	         // Step 2:  Create the URL.                                   //
	         //------------------------------------------------------------//
	         // Note: Put your real URL here, or better yet, read it as a  //
	         // command-line arg, or read it from a file.                  //
	         //------------------------------------------------------------//

	         u = new URL("http://scott-seto.com/appdoublescreen/Sql/exportdata.php");

	         yc = u.openConnection();
	         in = new BufferedReader(
	                                 new InputStreamReader(
	                                 yc.getInputStream()));
	         String inputLine;

	         while ((inputLine = in.readLine()) != null) 
	             sql += inputLine;

	      } catch (Exception mue) {

         mue.printStackTrace();

         wifiManager.setWifiEnabled(false);
	        wifiManager.setWifiEnabled(true);
	        continue;

	      }  finally {

	         //---------------------------------//
	         // Step 6:  Close the InputStream  //
	         //---------------------------------//

	         try {
	            in.close();
	         } catch (IOException ioe) {
	            // just going to ignore this one
	         }

	      } // end of 'finally' clause
		
	      
	      break;
	      }
	      
	      try {

		         //------------------------------------------------------------//
		         // Step 2:  Create the URL.                                   //
		         //------------------------------------------------------------//
		         // Note: Put your real URL here, or better yet, read it as a  //
		         // command-line arg, or read it from a file.                  //
		         //------------------------------------------------------------//

		         u = new URL("http://scott-seto.com/appdoublescreen/Sql/exportdataplaces.php");

		         //----------------------------------------------//
		         // Step 3:  Open an input stream from the url.  //
		         //----------------------------------------------//

		         is = u.openStream();         // throws an IOException

		         //-------------------------------------------------------------//
		         // Step 4:                                                     //
		         //-------------------------------------------------------------//
		         // Convert the InputStream to a buffered DataInputStream.      //
		         // Buffering the stream makes the reading faster; the          //
		         // readLine() method of the DataInputStream makes the reading  //
		         // easier.                                                     //
		         //-------------------------------------------------------------//

		         dis = new DataInputStream(new BufferedInputStream(is));

		         //------------------------------------------------------------//
		         // Step 5:                                                    //
		         //------------------------------------------------------------//
		         // Now just read each record of the input stream, and print   //
		         // it out.  Note that it's assumed that this problem is run   //
		         // from a command-line, not from an application or applet.    //
		         //------------------------------------------------------------//

		         while ((s = dis.readLine()) != null) {
		            placessql += s;
		         }

		      } catch (MalformedURLException mue) {

		         System.out.println("Ouch - a MalformedURLException happened.");
		         mue.printStackTrace();
		         System.exit(1);

		      } catch (IOException ioe) {

		         System.out.println("Oops- an IOException happened.");
		         ioe.printStackTrace();
		         System.exit(1);

		      } finally {

		         //---------------------------------//
		         // Step 6:  Close the InputStream  //
		         //---------------------------------//

		         try {
		            is.close();
		         } catch (IOException ioe) {
		            // just going to ignore this one
		         }

		      } // end of 'finally' clause
	      
	}
	
	
	
	
	

	
	public String getSql()
	{
		return sql;
	}
	
	public String getPlacesSql()
	{
		return placessql;
	}
	
}