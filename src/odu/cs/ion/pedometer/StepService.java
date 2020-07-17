/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package odu.cs.ion.pedometer;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import odu.cs.ion.R;
import odu.cs.ion.indoor.IndoorLocate;



/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link StepServiceController}
 * and {@link StepServiceBinding} classes show how to interact with the
 * service.
 *
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */
public class StepService extends Service {
	private static final String TAG = "name.bagi.levente.pedometer.StepService";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;
    private Utils mUtils;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private StepDetector mStepDetector;
    // private StepBuzzer mStepBuzzer; // used for debugging
    private StepDisplayer mStepDisplayer;
    private PaceNotifier mPaceNotifier;
    private DistanceNotifier mDistanceNotifier;
    private SpeedNotifier mSpeedNotifier;
    private SpeakingTimer mSpeakingTimer;
    
    private PowerManager.WakeLock wakeLock;
    private NotificationManager mNM;

    private float CompassValue;
    private int mSteps;
    private int mPace;
    private float mDistance;
    private float mSpeed;
    private float mCalories;
    private int compassAccuracy = 0;
    
    // accelerometer variables
    
	int stepdetected = 0;
	int numberofsteps = 0;  // number of real steps
	float currmaxaccel;
	
	//Current data (first state)
	float AccCurValues[] = new float[3];
	double OriCurValues[] = new double[3]; // 

	// Accelerometer data
	float currentAcceleration = 0;	
	float maxAcceleration = 0;
	double calibration = SensorManager.STANDARD_GRAVITY;
	double lastMeasureTime = 0;				// time of the last accelerometer measure
	double currentMeasureTime = 0;			// time of the current accelerometer measure
	int firstMeasureDone = 0;				// 0 if the first accelerometer measure is not done, 1 if it is done

	double aboveThresholdStart = 0;			// Time of the first measure of an acceleration above the threshold
	double aboveThresholdMaxLength = 0; 	// Maximum duration of an acceleration above the threshold
	
	
	
	/** 
	 * References values
	 * 
	 * These values can be changed in order to optimize the application
	 * 
	 */
	
	int stepInterval = 50; 			// Interval between two measures (ms)
	double stepDistance = 0.3;		// distance of a step (in meters)
	double refAcceleration = 0.1;	// acceleration threshold
	double durationOfAStep = 1;	// Duration of a step (ms)
	
	
    
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }
    
    @Override
    public void onCreate() {
        Log.i(TAG, "[SERVICE] onCreate");
        super.onCreate();
        
        
        //Toast.makeText(this, "oncreate", Toast.LENGTH_SHORT).show();
        
        
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        
        // Load settings
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        mState = getSharedPreferences("state", 0);

        mUtils = Utils.getInstance();
        mUtils.setService(this);
        mUtils.initTTS();

        acquireWakeLock();
        
        // Start detecting
        mStepDetector = new StepDetector();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerDetector();

        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

        mStepDisplayer = new StepDisplayer(mPedometerSettings, mUtils);
        mStepDisplayer.setSteps(mSteps = mState.getInt("steps", 0));
        mStepDisplayer.addListener(mStepListener);
        mStepDetector.addStepListener(mStepDisplayer);

        mPaceNotifier     = new PaceNotifier(mPedometerSettings, mUtils);
        mPaceNotifier.setPace(mPace = mState.getInt("pace", 0));
        mPaceNotifier.addListener(mPaceListener);
        mStepDetector.addStepListener(mPaceNotifier);

        mDistanceNotifier = new DistanceNotifier(mDistanceListener, mPedometerSettings, mUtils);
        mDistanceNotifier.setDistance(mDistance = mState.getFloat("distance", 0));
        mStepDetector.addStepListener(mDistanceNotifier);
        
        mSpeedNotifier    = new SpeedNotifier(mSpeedListener,    mPedometerSettings, mUtils);
        mSpeedNotifier.setSpeed(mSpeed = mState.getFloat("speed", 0));
        mPaceNotifier.addListener(mSpeedNotifier);
        

        
        mSpeakingTimer = new SpeakingTimer(mPedometerSettings, mUtils);
        mSpeakingTimer.addListener(mStepDisplayer);
        mSpeakingTimer.addListener(mPaceNotifier);
        mSpeakingTimer.addListener(mDistanceNotifier);
        mSpeakingTimer.addListener(mSpeedNotifier);
        mStepDetector.addStepListener(mSpeakingTimer);
        
        // Create Compass
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensorList.size() > 0)
        {
        mSensorManager.registerListener(sensorListener, sensorList.get(0), 
        		SensorManager.SENSOR_DELAY_NORMAL);
        
        }
        sensorList = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        Log.e("sensorlistsize",""+sensorList.size());
        if (sensorList.size() > 0)
        {
        mSensorManager.registerListener(sensorListener, sensorList.get(0), 
        		SensorManager.SENSOR_DELAY_NORMAL);
        
        }
        
        // Used when debugging:
        // mStepBuzzer = new StepBuzzer(this);
        // mStepDetector.addStepListener(mStepBuzzer);

        // Start voice
        reloadSettings();

        // Tell the user we started.
        //Toast.makeText(this, getText(R.string.started), Toast.LENGTH_SHORT).show();
    }
    
    /**
     * return the change in an an array of two doubles
     * change[0] is the movement to EAST
     * change[1] is the movement to NORTH
     */
    double[] PosCurValues = new double[3];
    public double[] getChangeInLocation()
    {
    	double change[] = new double[2];
    	
		change[0] = PosCurValues[0]; // x -> EAST
    	change[0] = PosCurValues[1]; // y -> NORTH
    	return change;
    }
    
    /**
     * return the max duration of the last acceleration above the threshold
     */

    public double getaboveThresholdMaxLength()
    {    	
    	
		return aboveThresholdMaxLength;
    }
    
    /**
     * return the number of steps since the launch of the application
     */

    public double getNumberOfSteps()
    {    	
    	
		return numberofsteps;
    }
    
    /**
     * return the maxAcceleartion during the last measure
     */

    public double getMaxAcceleration()
    {    	
    	
		return maxAcceleration;
    }
    
    private final SensorEventListener sensorListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
			compassAccuracy = accuracy;
			if (mCallback != null) {
			mCallback.compassAccuracyChanged(compassAccuracy);
			}
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			
			if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				CompassValue = event.values[0];
				
				OriCurValues[0] = event.values[0] * java.lang.Math.PI / 180; 
	    		OriCurValues[1] = event.values[1] * java.lang.Math.PI / 180;
	    		OriCurValues[2] = event.values[2] * java.lang.Math.PI / 180;
	    		
				if (mCallback != null) {
				mCallback.compassChanged(CompassValue);
				}
			}
			else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				//Log.e("sensorchanged","accelsensor");
	
				
				
				
 				// Records the current time of the measure
				currentMeasureTime = System.currentTimeMillis();
				
				//Log.d(TAG, "event.values = " + event.values[0] + " | " + event.values[1] + " | " + event.values[2]);
				
				// Computes the value of the current acceleration
				double x = event.values[0];
		        double y = event.values[1];
		        double z = event.values[2];
		        
		        //Log.e("ca",""+x+" "+y+" "+z);
		        
		        double a = Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)));        		        
		        currentAcceleration = Math.abs((float)(a-calibration));
		        
		        //outputCurAcc.setText("curAcc : " + currentAcceleration);
		        
		        // Update the max acceleration
		        if (currentAcceleration > maxAcceleration)
		        {
		        	if (aboveThresholdStart != 0)
		        	{
		        		aboveThresholdMaxLength = System.currentTimeMillis() - aboveThresholdStart;
		        	}
		        	else
		        	{
		        		aboveThresholdStart = System.currentTimeMillis();
		        	}
		        	maxAcceleration = currentAcceleration;
		        }
		        else
		        {
		        	aboveThresholdStart = 0;
		        }
		        
		        // Checks if there is already at least one measure done
    		    if (firstMeasureDone != 0)
    		    {
    		    	   	
    		    	// Check if the time between two measures is over
    		    	//outputLastTime.setText("lastTime : " + lastMeasureTime);
    		    	//outputCurTime.setText("CurTime : " + currentMeasureTime);
    		    	//outputInterval.setText("Interval : " + (currentMeasureTime - lastMeasureTime));
    		    	
    		    	// Check if the time between two measures is over (stepInterval is not a good name, it is the interval between two measurements)
		        	if ( (currentMeasureTime - lastMeasureTime) > stepInterval )
		        	{
		        		/*
		        		// Check if a step is done
		        		if (maxAcceleration > refAcceleration)
		        		{
		        		*/
		        			// Check if the duration of the max acceleration is above the threshold
		        			if (aboveThresholdMaxLength > durationOfAStep)
		        			{
		        				// Check if the stepService as also detected a step
		        				//if (stepdetected == 1)
		        				//{
        		        			// Update the change of the location agter a step
		        				//if (OriCurValues != null) {
		        				if (valueWithSensibility(maxAcceleration, 0.8) > 2.0f)
		        				{
    		        				PosCurValues[0] = stepDistance * Math.sin( OriCurValues[0]); // x -> East
        		        			PosCurValues[1] = stepDistance * Math.cos( OriCurValues[0]); // y -> NORTH
        		        			 currmaxaccel = maxAcceleration;
        		        			//Log.e("step",""+PosCurValues[0]+" "+PosCurValues[1]+" "+valueWithSensibility(maxAcceleration, 0.8));
        		        			if (PosCurValues != null) {
        		                        float[] ff = new float[] {(float)PosCurValues[0], (float)PosCurValues[1], valueWithSensibility(maxAcceleration, 0.8)};
        		                        if (mCallback != null) mCallback.accelChanged(ff);
        		                        }
		        				}
		        				//}
        		        			// Increment the number of steps
        		        			//numberofsteps++;
        		        			
        		        			// Update the current position
        		        			//PosCurValues[0] = PosCurValues[0] + stepDistance * Math.sin( OriCurValues[0]); // x -> East
        		        			//PosCurValues[1] = PosCurValues[1] + stepDistance * Math.cos( OriCurValues[0]); // y -> NORTH
        		        			
        		        			// Reset variables 		        		        			//
        		        			aboveThresholdMaxLength = 0;
        		        			aboveThresholdStart = 0;   
        		        			//stepdetected = 0;
		        				//}
		        			}
		        		/*	
		        		}
		        		*/
		        		//Log.d(TAG, "max acceleration : " + maxAcceleration);
		        		maxAcceleration = 0;	// reinitialize the max acceleration for the next step;
		        		lastMeasureTime = System.currentTimeMillis(); // reinitialize the lastmeasureTime for the next step interval
		        	}
		        	
    		    }
    		    else
    		    {
    		    	lastMeasureTime = System.currentTimeMillis(); 	// records the first measure
    		    	firstMeasureDone = 1;						  	// first measure is done
    		    }
    		    
    		  	//outputX.setText("xAcc:"+Float.toString(valueWithSensibility(event.values[0], 0.8)));
        		//outputY.setText("yAcc:"+Float.toString(valueWithSensibility(event.values[1], 0.8)));
        		//outputZ.setText("zAcc:"+Float.toString(valueWithSensibility(event.values[2], 0.8)));
        		
        		//outputMaxAcc.setText("MaxACC:"+Float.toString(valueWithSensibility(maxAcceleration, 0.8)));
        		
        		//outputXCur.setText("x : " + PosCurValues[0]);
        		//outputYCur.setText("y : " + PosCurValues[1]);
        		//outputZCur.setText("distance : " + Math.round(Math.sqrt(Math.pow(PosCurValues[0], 2) + Math.pow(PosCurValues[1], 2))));	            		
        		//outputZCur.setText("number of real steps : " + numberofsteps);
				
				
				
				
			}
		}    	
    };
    
    public float valueWithSensibility(float f, double sens) {
 	   if (java.lang.Math.abs(f) > sens) {
 		   return Float.valueOf(Double.toString(f));
 	   }	   
 	   return 0;
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "[SERVICE] onStart");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy");
        mUtils.shutdownTTS();

        // Unregister our receiver.
        unregisterReceiver(mReceiver);
        unregisterDetector();
        
        mStateEditor = mState.edit();
        mStateEditor.putInt("steps", mSteps);
        mStateEditor.putInt("pace", mPace);
        mStateEditor.putFloat("distance", mDistance);
        mStateEditor.putFloat("speed", mSpeed);
        mStateEditor.putFloat("calories", mCalories);
        mStateEditor.commit();
        
        mNM.cancel(R.string.app_name);

        wakeLock.release();
        
        super.onDestroy();
        
        // Stop detecting
        mSensorManager.unregisterListener(mStepDetector);

        // Tell the user we stopped.
        //Toast.makeText(this, getText(R.string.stopped), Toast.LENGTH_SHORT).show();
    }

    private void registerDetector() {
        mSensor = mSensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER /*| 
            Sensor.TYPE_MAGNETIC_FIELD | 
            Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
            mSensor,
            SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterDetector() {
        mSensorManager.unregisterListener(mStepDetector);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "[SERVICE] onBind");
        return mBinder;
    }

    /**
     * Receives messages from activity.
     */
    private final IBinder mBinder = new StepBinder();

    public interface ICallback {
        public void stepsChanged(int value);
        public void paceChanged(int value);
        public void distanceChanged(float value);
        public void speedChanged(float value);
        public void caloriesChanged(float value);
        public void compassChanged(float value);
        public void accelChanged(float[] value);
        public void compassAccuracyChanged(int value);
    }
    
    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
        //mStepDisplayer.passValue();
        //mPaceListener.passValue();
    }
    
    private int mDesiredPace;
    private float mDesiredSpeed;
    
    /**
     * Called by activity to pass the desired pace value, 
     * whenever it is modified by the user.
     * @param desiredPace
     */
    public void setDesiredPace(int desiredPace) {
        mDesiredPace = desiredPace;
        if (mPaceNotifier != null) {
            mPaceNotifier.setDesiredPace(mDesiredPace);
        }
    }
    /**
     * Called by activity to pass the desired speed value, 
     * whenever it is modified by the user.
     * @param desiredSpeed
     */
    public void setDesiredSpeed(float desiredSpeed) {
        mDesiredSpeed = desiredSpeed;
        if (mSpeedNotifier != null) {
            mSpeedNotifier.setDesiredSpeed(mDesiredSpeed);
        }
    }
    
    public void reloadSettings() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        
        if (mStepDetector != null) { 
            mStepDetector.setSensitivity(
                    Float.valueOf(mSettings.getString("sensitivity", "10"))
            );
        }
        
        if (mStepDisplayer    != null) mStepDisplayer.reloadSettings();
        if (mPaceNotifier     != null) mPaceNotifier.reloadSettings();
        if (mDistanceNotifier != null) mDistanceNotifier.reloadSettings();
        if (mSpeedNotifier    != null) mSpeedNotifier.reloadSettings();
        if (mSpeakingTimer    != null) mSpeakingTimer.reloadSettings();
    }
    
    public void resetValues() {
        mStepDisplayer.setSteps(0);
        mPaceNotifier.setPace(0);
        mDistanceNotifier.setDistance(0);
        mSpeedNotifier.setSpeed(0);

    }
    
    /**
     * Forwards pace values from PaceNotifier to the activity. 
     */
    private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
        public void stepsChanged(int value) {
            mSteps = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.stepsChanged(mSteps);
                Log.e("stepdetector","");
                
            	
            }
        }
    };
    /**
     * Forwards pace values from PaceNotifier to the activity. 
     */
    private PaceNotifier.Listener mPaceListener = new PaceNotifier.Listener() {
        public void paceChanged(int value) {
            mPace = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.paceChanged(mPace);
            }
        }
    };
    /**
     * Forwards distance values from DistanceNotifier to the activity. 
     */
    private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        public void valueChanged(float value) {
            mDistance = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.distanceChanged(mDistance);
            }
        }
    };
    /**
     * Forwards speed values from SpeedNotifier to the activity. 
     */
    private SpeedNotifier.Listener mSpeedListener = new SpeedNotifier.Listener() {
        public void valueChanged(float value) {
            mSpeed = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.speedChanged(mSpeed);
            }
        }
    };

    
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        CharSequence text = getText(R.string.app_name);
        Notification notification = new Notification(R.drawable.ic_notification, null,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        Intent pedometerIntent = new Intent();
        pedometerIntent.setComponent(new ComponentName(this, IndoorLocate.class));
        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                pedometerIntent, 0);
        notification.setLatestEventInfo(this, text,
                getText(R.string.notification_subtitle), contentIntent);

        mNM.notify(R.string.app_name, notification);
    }


    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Unregisters the listener and registers it again.
                StepService.this.unregisterDetector();
                StepService.this.registerDetector();
                if (mPedometerSettings.wakeAggressively()) {
                    wakeLock.release();
                    acquireWakeLock();
                }
            }
        }
    };

    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int wakeFlags;
        if (mPedometerSettings.wakeAggressively()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        }
        else if (mPedometerSettings.keepScreenOn()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK;
        }
        else {
            wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
        }
        wakeLock = pm.newWakeLock(wakeFlags, TAG);
        wakeLock.acquire();
    }

}

