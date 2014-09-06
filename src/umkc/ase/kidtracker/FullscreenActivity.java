package umkc.ase.kidtracker;

import umkc.ase.kidtracker.util.SystemUiHider;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {

	private static boolean isServiceRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isServiceRunning = isServiceRunning();

		setContentView(R.layout.activity_fullscreen);
		if (isServiceRunning) {
			ImageButton image = (ImageButton) findViewById(R.id.imageButton);
			image.setImageResource(R.drawable.dark);
			TextView textView = (TextView) findViewById(R.id.status);
			textView.setText("Service Enabled");
		}
		if(!Utils.isInternetAvailable(getApplicationContext())){
			showNoConnectionDialog(this);
		}else{
			//fetch latest boundary
			new FetchBoundaryTask(this).execute();
		}
		
	}
	
	public static void showNoConnectionDialog(Context ctx1) {
        final Context ctx = ctx1;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(true);
        builder.setMessage("Internet not available");
        builder.setTitle("No internet");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //ctx.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
               ctx.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });

        builder.show();
    }

	public void onButtonClick(View view) {
		ImageButton image = (ImageButton) findViewById(R.id.imageButton);
		TextView textView = (TextView) findViewById(R.id.status);
		Intent intent = new Intent(this, UpdateService.class);
		isServiceRunning = isServiceRunning();
		if (isServiceRunning) {
			// stop service
			isServiceRunning = false;
			image.setImageResource(R.drawable.pale);
			textView.setText("Service Disabled");
			stopService(intent);

		} else {
			// start service
			LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				isServiceRunning = true;
				image.setImageResource(R.drawable.dark);
				textView.setText("Service Enabled");
				startService(intent);
			} else {
				buildAlertMessageNoGps();
			}
		}
	}

	private boolean isServiceRunning() {
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : activityManager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (service.service.getClassName().equals(
					UpdateService.class.getName())) {
				return true;
			}
		}
		return false;
	}
	
	 private void buildAlertMessageNoGps() {
		    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
		                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		               }
		           })
		           .setNegativeButton("No", new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
		                    dialog.cancel();
		               }
		           });
		    final AlertDialog alert = builder.create();
		    alert.show();
		}
	 
	 public void callHome(View view){
		 Intent intent = new Intent(Intent.ACTION_CALL);
		 intent.setData(Uri.parse("tel:7322088918"));
		 startActivity(intent);
	 }
	 
	 public void getDirections(View view){
		 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=39.03797,-94.584819"));
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 startActivity(intent);
	 }
	 
	 public void takePicnEmail(View v){
		 Intent intent = new Intent(getApplicationContext(),CameraActivity.class);
		 startActivity(intent);
	 }

}
