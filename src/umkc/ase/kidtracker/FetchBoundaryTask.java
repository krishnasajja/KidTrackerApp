package umkc.ase.kidtracker;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class FetchBoundaryTask extends AsyncTask {
	
	Context context;
	public static final String PREFS_NAME = "MyPrefsFile";
	public final static String SOAP_ACTION_UPDATE = "http://tempuri.org/getCurrentBoundary";
	public final static String NAMESPACE = "http://tempuri.org/";
	public final static String UPDATE_METHOD = "getCurrentBoundary";
	public final static String URL = "http://170.224.165.253/aspnet_client/LoginService/LoginService.asmx";
	
	public FetchBoundaryTask(Context c){
		context = c;
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		SoapObject soapObject = new SoapObject(NAMESPACE, UPDATE_METHOD);
		soapObject.addProperty("childId", "1000");
		String Response = serviceCall(soapObject, SOAP_ACTION_UPDATE);
		if (Response != null && Response.length() > 0) {
			SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("boundary", Response);
			System.out.println("boundary: " + Response);
			// Commit the edits!
			editor.commit();
		}
		return null;
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
			System.out.println("~~Call to web servie failed!" + e.getMessage());
		}

		return Response;
	}

}
