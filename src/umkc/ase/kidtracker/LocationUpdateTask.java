package umkc.ase.kidtracker;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class LocationUpdateTask extends AsyncTask{
	Context context;
	Location location;
	public final static String SOAP_ACTION_UPDATE = "http://tempuri.org/UpdateChildLocation";
	public final static String NAMESPACE = "http://tempuri.org/";
	public final static String UPDATE_METHOD = "UpdateChildLocation";
	public final static String URL = "http://170.224.165.253/aspnet_client/LoginService/LoginService.asmx";
	public static final String PREFS_NAME = "MyPrefsFile";
	
	public LocationUpdateTask(Context c, Location l){
		this.context = c;
		this.location = l;
	}
	@Override
	protected Object doInBackground(Object... args) {
		updateNewLocation();
		return null;   
	}
	
	public void updateNewLocation(){
		if(location != null && context != null){
		//service Call
		SoapObject soapObject = new SoapObject(NAMESPACE, UPDATE_METHOD);
		soapObject.addProperty("childId", "1000");
		soapObject.addProperty("latitude", location.getLatitude()+"");
		soapObject.addProperty("longitude", location.getLongitude()+"");
		System.out.println("location: " + location.getLatitude() + "," + location.getLongitude());
		String Response = serviceCall(soapObject,SOAP_ACTION_UPDATE);
		if (Response != null && Response.equals("true")){
			//Toast.makeText(context, "Location updated", Toast.LENGTH_SHORT).show();
		}else{
			//Toast.makeText(context, "Location update failed", Toast.LENGTH_SHORT).show();
		}
		
		//notification - alert
		if(!checkIfKidInBoundary(location))
		sendAlert(getAddress());
		}
	}
	
	private String getAddress(){
		
		 String addressString = null;

		 try {
		   Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
		   List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		   StringBuilder sb = new StringBuilder();
		   if (addresses.size() > 0) {
			   Address address = addresses.get(0);
			   for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
			   sb.append(address.getAddressLine(i)).append("\n");
			   }

		   addressString = sb.toString();
		   System.out.println("Address from latlong: "+ addressString);
		   Log.e("Address from lat,long ;", addressString);
		  } catch (Exception e) {
			  e.printStackTrace();
			  System.out.println(e.getMessage());
		  }
		 return addressString;
		 }
	
	private boolean checkIfKidInBoundary(Location loc){
		// Restore preferences
	       SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
	       String boundary = settings.getString("boundary", null);
	       String points[] = boundary.split(";");
	       int polySides = points.length;
	       double polyX[] = new double[polySides];
	       double polyY[] = new double[polySides];
	       int a =0;
	       for(String s : points){
	    	   String latlong[] = s.split(",");
	    	   System.out.println(a + ":" + latlong[0] + "," + latlong[1]);
	    	   polyX[a] = Double.valueOf(latlong[0]);
	    	   polyY[a] = Double.valueOf(latlong[1]);
	    	   a++;
	       }
	       double x = loc.getLatitude();
	       double y = loc.getLongitude();
	       int i, j = polySides - 1;
			boolean oddNodes = false;

			for (i = 0; i < polySides; i++) {
				if ((polyY[i] < y && polyY[j] >= y || polyY[j] < y && polyY[i] >= y)
						&& (polyX[i] <= x || polyX[j] <= x)) {
					if (polyX[i] + (y - polyY[i]) / (polyY[j] - polyY[i])
							* (polyX[j] - polyX[i]) < x) {
						oddNodes = !oddNodes;
					}
				}
				j = i;
			}

			return oddNodes;
	       
	}
	
	
	public static String serviceCall(SoapObject soapObject, String SOAP_ACTION) {
		HttpTransportSE transport = new HttpTransportSE(URL);
		transport.debug = true;

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(soapObject);
		String Response = null;

		try {
			transport.call(SOAP_ACTION, envelope);
			Response = envelope.getResponse().toString().trim();

		} catch (Exception e) {
			System.out.println("~~Call to web servie failed!"
					+ e.getMessage());
		}

		return Response;
	}
	
	public void sendAlert(String address){
		StringBuffer message = new StringBuffer();
		message.append("Alert: Child crossed the boundary.");
		message.append("\nChild current Address: "+ address);
		message.append("\nChild current location: "+ location.getLatitude()+","+location.getLongitude());
		if(Utils.isInternetAvailable(context)){
			sendData("Child", "kcsc95@mail.umkc.edu", "caretracker@umkc.edu", "Boundary Breach Alert", message.toString());
		}
	}
	
	
	public static void sendData(String name, String to, String from, String subject, String message)
    {
        String content = "";

        try
        {               
            /* Sends data through a HTTP POST request */
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://php.umkc.edu/intapps/android/android_feedback.php");
            List <NameValuePair> params = new ArrayList <NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("to", to));
            params.add(new BasicNameValuePair("from", from));
            params.add(new BasicNameValuePair("subject", subject));
            params.add(new BasicNameValuePair("message", message));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            /* Reads the server response */
            HttpResponse response = httpClient.execute(httpPost);
            InputStream in = response.getEntity().getContent();

            StringBuffer sb = new StringBuffer();
            int chr;
            while ((chr = in.read()) != -1)
            {
                sb.append((char) chr);
            }
            content = sb.toString();
            in.close();

            /* If there is a response, display it */
            if (!content.equals(""))
            {
                Log.i("HTTP Response", content);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
