package umkc.ase.kidtracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.widget.Toast;

public class CameraActivity extends Activity {

    protected boolean _taken = true;
    File sdImageMainDirectory;
    String recipient_address = "kcsc95@mail.umkc.edu";
    File path;

    protected static final String PHOTO_TAKEN = "photo_taken";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        try {

            super.onCreate(savedInstanceState);         
            path = new File(Environment
                            .getExternalStorageDirectory()
                            + File.separator + "myDir" + File.separator);
            path.mkdirs();
                    sdImageMainDirectory = new File(path, "myPic.jpg");
                    System.out.println(sdImageMainDirectory.getAbsolutePath());
                startCameraActivity();
                
        } catch (Exception e) {
            finish();
            Toast.makeText(this, "Error occured. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void sendEmail() {
    	System.out.println(sdImageMainDirectory.getAbsolutePath());
    	Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    	sharingIntent.setType("vnd.android.cursor.dir/email");
    	sharingIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient_address});
    	sharingIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(sdImageMainDirectory));
    	sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Picture of sourroundings");
    	startActivity(Intent.createChooser(sharingIntent, "Send email"));
		
	}

	protected void startCameraActivity() {

        Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
        case 0:
            finish();
            break;

        case -1:

            try {
                StoreImage(this, Uri.parse(data.toURI()),
                        sdImageMainDirectory);
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
            sendEmail();
            //startActivity(new Intent(CameraActivity.this, Home.class));

        }

    }
    
   

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getBoolean(CameraActivity.PHOTO_TAKEN)) {
            _taken = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CameraActivity.PHOTO_TAKEN, _taken);
    }

        public static void StoreImage(Context mContext, Uri imageLoc, File imageDir) {
        Bitmap bm = null;
        try {
            bm = Media.getBitmap(mContext.getContentResolver(), imageLoc);
            FileOutputStream out = new FileOutputStream(imageDir);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            bm.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}