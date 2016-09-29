package org.foodispower.chocolatelist.spice.request.listener;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class RemoteImageViewRequestListener implements RequestListener< InputStream > {

	ImageView imageView;
	String originalUrl;

	public RemoteImageViewRequestListener( ImageView imageView ) {
		this( imageView, null );
	}
	
	public RemoteImageViewRequestListener( ImageView imageView, String originalUrl ) {
		this.imageView = imageView;
		this.originalUrl = originalUrl;

		imageView.setTag( originalUrl );
	}

    public void onRequestFailure( SpiceException spiceException ) {
    	// don't react. it's probably a 404, so the default image will remain..
    }

    public void onRequestSuccess( final InputStream result ) {

    	if ( null == imageView.getTag() ) {
    		return;
    	}

        Bitmap companyLogoImage = null;
        try {
        	companyLogoImage = BitmapFactory.decodeStream( result );
        } catch (Exception e) {
//            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        if ( null != companyLogoImage ) {
	        if ( null == originalUrl || imageView.getTag().equals( originalUrl ) ) {
	        	imageView.setImageBitmap( companyLogoImage );
	            imageView.setTag( null );
	    	}
        }
    }

}
