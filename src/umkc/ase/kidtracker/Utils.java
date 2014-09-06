package umkc.ase.kidtracker;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	
	public static boolean isInternetAvailable(Context context) {
		boolean isNetworkEnabled = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting())
			isNetworkEnabled = true;
		return isNetworkEnabled;
	}

	public static boolean isGPSEnabled(Context context) {
		LocationManager m_LocationManager;
		m_LocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return m_LocationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}
