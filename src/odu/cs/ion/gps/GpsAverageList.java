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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import odu.cs.ion.indoor.IndoorLocate;

import android.location.Location;

/** Group all the measurements. It allows the calculation of the weighted average of the measured values and record times of measurement.
 *  @author Libor Tvrdik (libor.tvrdik &lt;at&gt; gmail.com) */
public class GpsAverageList {

	private final List<Location> locations;

	private double averageLat;
	private double averageLon;
	private double averageAlt;
	private float averageAccuracy;
	private double weightedLatSum;
	private double weightedLonSum;
	private double altSum;
	private double invertedAccuracySum;
	private float distanceFromAverageCoordsSum;

	/** Creates a new list. */
	public GpsAverageList() {
		this.locations = new ArrayList<Location>();
		clean();
	}

	/** @return size of list (count of items)
	 *  @see #add(Location) */
	public synchronized int size() {
		return locations.size();
	}

	/** Delete all items from list. Can be reused for next measurements. */
	public synchronized void clean() {
		locations.clear();
		averageLat = 0;
		averageLon = 0;
		averageAlt = 0;
		averageAccuracy = 0;
		weightedLatSum = 0;
		weightedLonSum = 0;
		altSum = 0;
		invertedAccuracySum = 0;
		distanceFromAverageCoordsSum = 0;
	}
	
	public synchronized void removeOne()
	{
		Location aa = getLocation(0);
		locations.remove(0);
		final double invertedAccuracy = 1 / (aa.getAccuracy() == 0 ? 1 : aa.getAccuracy());
		weightedLatSum -= aa.getLatitude()*invertedAccuracy;
		weightedLonSum -= aa.getLongitude()*invertedAccuracy;
		
		invertedAccuracySum -= invertedAccuracy;
		altSum -= aa.getAltitude();
	}

	/** Adds a new value into list. */
	public synchronized void add(Location location) {

		locations.add(location);

		final double invertedAccuracy = 1 / (location.getAccuracy() == 0 ? 1 : location.getAccuracy());
		weightedLatSum += location.getLatitude() * invertedAccuracy;
		weightedLonSum += location.getLongitude() * invertedAccuracy;
		invertedAccuracySum += invertedAccuracy;
		altSum += location.getAltitude();

		// calculating average coordinates (weighted by accuracy) and altitude
		averageLat = weightedLatSum / invertedAccuracySum;
		averageLon = weightedLonSum / invertedAccuracySum;
		averageAlt = altSum / size();

		// calculating accuracy improved by averaging
		double distance = IndoorLocate.distance(location.getLatitude(), location.getLongitude(), averageLat, averageLon);
		if (distance == 0) {
			distance = (location.getAccuracy() == 0 ? 1 : location.getAccuracy());
		}
		distanceFromAverageCoordsSum += distance;
		averageAccuracy = distanceFromAverageCoordsSum / size();
	}

	/** @return weighted mean of all location in list, or location with all property set on 0 if list is empty. */
	public synchronized Location getLocation() {

		final Location location = new Location("average"); //$NON-NLS-1$
		location.setLatitude(getLatitude());
		location.setLongitude(getLongitude());
		location.setAccuracy(getAccuracy());
		location.setAltitude(getAltitude());
		location.setTime(System.currentTimeMillis());
		return location;
	}

	/** @return weighted mean of all location in list, or location with all property set on 0 if list is empty. */
	public synchronized Location getLocation(int index) {
		return locations.get(index);
	}

	/** @return weighted mean of all latitudes in list, or 0 if list is empty. */
	public synchronized double getLatitude() {
		return averageLat;
	}

	/** @return weighted mean of all longitudes in list, or 0 if list is empty. */
	public synchronized double getLongitude() {
		return averageLon;
	}

	/** @return arithmetic mean of all errors in list, or 0 if list is empty. */
	public synchronized float getAccuracy() {
		return averageAccuracy;
	}

	/** @return weighted mean of all altitudes in list, or 0 if list is empty. */
	public synchronized double getAltitude() {
		return averageAlt;
	}

	/** @param index in list, 0 for first item, size()-1 for last item
	 *  @return stored latitude from list. */
	public synchronized double getLatitude(int index) {
		return locations.get(index).getLatitude();
	}

	/** @param index in list, 0 for first item, size()-1 for last item
	 *  @return stored longitude from list. */
	public synchronized double getLongitude(int index) {
		return locations.get(index).getLongitude();
	}

	/** @param index in list, 0 for first item, size()-1 for last item
	 *  @return stored error from list. */
	public synchronized float getAccuracy(int index) {
		return locations.get(index).getAccuracy();
	}

	/** @param index in list, 0 for first item, size()-1 for last item
	 *  @return stored altitude from list. */
	public synchronized double getAltitude(int index) {
		return locations.get(index).getAltitude();
	}

	/** @param index in list, 0 for first item, size()-1 for last item
	 *  @return date of store index in list. */
	public synchronized Date getTime(int index) {
		return new Date(locations.get(index).getTime());
	}

	@Override
	public synchronized String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append(getClass().getSimpleName()).append(": ("); //$NON-NLS-1$
		sb.append("avg lat=").append(getLatitude()); //$NON-NLS-1$
		sb.append(" lon=").append(getLongitude()); //$NON-NLS-1$
		sb.append(" alt=").append(getAltitude()); //$NON-NLS-1$
		sb.append(" acc=").append(getAccuracy()).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i = 0; i < size(); i++) {
			sb.append("[").append(getLongitude(i)).append(","); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(getLatitude(i)).append(","); //$NON-NLS-1$
			sb.append(getAltitude(i)).append(","); //$NON-NLS-1$
			sb.append(getAccuracy(i)).append("]"); //$NON-NLS-1$
		}
		sb.append(")"); //$NON-NLS-1$

		return sb.toString();
	}

}
