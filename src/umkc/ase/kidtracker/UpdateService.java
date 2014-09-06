package umkc.ase.kidtracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class UpdateService extends Service {
	
	LocationListener locationListener;
	LocationManager locationManager;
	
	
	@Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      Toast.makeText(this, "service starting...", Toast.LENGTH_SHORT).show();
	      locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

			// Define a listener that responds to location updates
			locationListener = new LocationListener() {
			    public void onLocationChanged(Location location) {
			    	Toast.makeText(getApplicationContext(), "Updating location", Toast.LENGTH_SHORT).show();
			      new LocationUpdateTask(getApplicationContext(), location).execute();
			    }

				@Override
				public void onProviderDisabled(String arg0) {
				}

				@Override
				public void onProviderEnabled(String arg0) {
				}

				@Override
				public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				}
			};
			//updateNewLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
			//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60*2, 0, locationListener);
			
	      // If we get killed, after returning from here, restart
	      return START_STICKY;
	  }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(locationListener != null && locationManager != null){
			locationManager.removeUpdates(locationListener);
			locationManager = null;
		}
	}
		
		
		@Override
		public IBinder onBind(Intent arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
}
