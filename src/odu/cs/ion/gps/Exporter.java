/*
   Copyright 2010 Libor Tvrdik

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package odu.cs.ion.gps;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Formatter;

import android.content.Context;
import android.location.Location;
import odu.cs.ion.R;

/** @author Libor Tvrdik (libor.tvrdik &lt;at&gt; gmail.com) */
public class Exporter {

	public static final SimpleDateFormat XSD_DATETIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); //$NON-NLS-1$
	public static final String ACCURACY_SYMBOL = "±"; //$NON-NLS-1$

	private final Context context;

	public Exporter(Context applicationContext) {
		this.context = applicationContext;
	}

	private Context getContext() {
		return context;
	}

	/** Coordinate format to a readable format (degrees - DDD MM.MMM) */
	public String formatGPS(Location location) {

		final StringBuilder output = new StringBuilder();
		final Formatter formatter = new Formatter(output);

		try {
			formatGPS(location, formatter, output);
		} catch (IOException ex) {
			// IOException on StringBuilder is impossible
			throw new IllegalStateException(ex);
		}

		return output.toString();
	}

	/** Coordinate format to a readable format (degrees - DDD MM.MMM) */
	@SuppressWarnings("nls")
	public Appendable formatGPS(Location location, Formatter formatter, Appendable output) throws IOException {

		output.append(location.getLatitude() > 0 ? " N" : " S");

		final double lat = Math.abs(location.getLatitude());
		final int latDegree = (int) lat;
		final double latMinute = (lat - latDegree) * 60;
		// Second format parameter, fixed zero padding mistake for floating point number
		formatter.format("%02d° %s%.4f ", latDegree, (latMinute < 10 ? "0" : ""), latMinute);

		output.append(location.getLongitude() > 0 ? " E" : " W");

		final double lon = Math.abs(location.getLongitude());
		final int lonDegree = (int) lon;
		final double lonMinute = (lon - lonDegree) * 60;
		// Second format parameter, fixed zero padding mistake for floating point number
		formatter.format("%03d° %s%.4f ", lonDegree, (lonMinute < 10 ? "0" : ""), lonMinute);

		return output;
	}

	/** Coordinate format to a readable format (degrees - DDD MM.MMM) � accuracy. */
	public String formatGPSWithAccuracy(Location location) {

		final StringBuilder output = new StringBuilder();
		final Formatter formatter = new Formatter(output);

		try {
			formatGPSWithAccuracy(location, formatter, output);
		} catch (IOException ex) {
			// IOException on StringBuilder is impossible
			throw new IllegalStateException(ex);
		}

		return output.toString();
	}

	/** Coordinate format to a readable format (degrees - DDD MM.MMM) � accuracy. */
	public Appendable formatGPSWithAccuracy(Location location, Formatter formatter, Appendable output)
			throws IOException {

		formatGPS(location, formatter, output);
		formatAccuracy(location, formatter, output);

		return output;
	}

	/** Format � accuracy in meter and feet. */
	public String formatAccuracy(Location location) {

		final StringBuilder output = new StringBuilder();
		final Formatter formatter = new Formatter(output);

		try {
			formatAccuracy(location, formatter, output);
		} catch (IOException ex) {
			// IOException on StringBuilder is impossible
			throw new IllegalStateException(ex);
		}

		return output.toString();
	}

	/** Format � accuracy in meter and feet. */
	public Appendable formatAccuracy(Location location, Formatter formatter, Appendable output) throws IOException {

		output.append(ACCURACY_SYMBOL);
		formatLength(location.getAccuracy(), formatter);

		return output;
	}

	/** Formats length for output in meter and feet. */
	public void formatLength(double length, Formatter formatter) {
		formatter.format(" %,.1f m (%,.1f %s)", length, length * 3.28132739, getContext().getString(R.string.Feet)); //$NON-NLS-1$
	}

	/** Formats height to view the users. 
	 *  @param altitude in meter */
	public String formatAltitude(double altitude) {

		final StringBuilder alt = new StringBuilder();
		final Formatter formatter = new Formatter(alt);

		alt.append(getContext().getString(R.string.Altitude));
		formatLength(altitude, formatter);

		return alt.toString();
	}

}
