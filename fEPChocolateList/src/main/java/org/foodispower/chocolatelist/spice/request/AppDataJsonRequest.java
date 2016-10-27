package org.foodispower.chocolatelist.spice.request;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.foodispower.chocolatelist.data.AppDataManager;
import org.foodispower.chocolatelist.model.AppData;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.octo.android.robospice.request.SpiceRequest;

public class AppDataJsonRequest extends SpiceRequest< AppData > {

	Context context;

	public AppDataJsonRequest( Context context ) {
		super( AppData.class );

		this.context = context;
	}

	@Override
	public AppData loadDataFromNetwork() throws Exception {

//      Uri.Builder uriBuilder = Uri.parse("http://192.168.0.100/~joshua/work/FoodEmpowermentProject/AppData.json").buildUpon();
      Uri.Builder uriBuilder = Uri.parse("http://fep.notjosh.com/data/AppData.json").buildUpon();

        String url = uriBuilder.build().toString();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
        
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestProperty("X-FEP-Platform", "android");
        urlConnection.setRequestProperty("X-FEP-Version",  context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        String result = IOUtils.toString(urlConnection.getInputStream());
        urlConnection.disconnect();

        // store JSON. we might be offline next app launch!
        AppDataManager appDataManager = new AppDataManager( context );
        AppData appData = appDataManager.instantiateAppData( result );

        if ( null != appData ) {
        	appDataManager.save( result );
        }

        return appData;

	}

}
