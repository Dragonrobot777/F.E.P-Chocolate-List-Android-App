package org.foodispower.chocolatelist.data;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializationProblemHandler;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy;
import org.foodispower.chocolatelist.model.AppData;

import android.content.Context;
import android.util.Log;

public class AppDataManager {

	private Context context;
	
	public static final String INTERNET_CACHE_FILENAME = "feature";

	public AppDataManager( Context context ) {
		this.context = context;
	}

	public void save( String appDataJson ) {
		try {
			FileOutputStream outputStream = context.openFileOutput( INTERNET_CACHE_FILENAME, Context.MODE_PRIVATE );
			outputStream.write( appDataJson.getBytes() );
			outputStream.close();
		} catch ( Exception e ) {
		}
	}

	public String read() {
		String output = readFromInternetCache();

		if ( null == output ) {
			output = readFromAssets();
		}

		//Log.d("Chocolate List", "output " + output);
		return output;
	}

	public String readFromAssets() {
		return getContentsOfAsset( "AppData.json" );
	}

	public String readFromInternetCache() {
		String output = null;
		
		try {
		    InputStreamReader inputStreamReader = new InputStreamReader( context.openFileInput( INTERNET_CACHE_FILENAME ) );
		    BufferedReader bufferedReader = new BufferedReader( inputStreamReader );

		    StringBuilder sb = new StringBuilder();
		    String line;
		    while ( ( line = bufferedReader.readLine() ) != null ) {
		        sb.append( line );
		    }

		    output = sb.toString();
		} catch ( Exception e ) {
			output = null;
		}
		return output;
	}

	public AppData instantiateAppData() {
		return instantiateAppData( read() );
	}

	public AppData instantiateAppData( String appDataJson ) {
		AppData appData = null;

		try {
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.setPropertyNamingStrategy( new LowerCaseWithUnderscoresStrategy() );
	
	        appData = mapper.readValue( appDataJson, AppData.class );
		} catch ( Exception e ) {
			appData = null;
		}

		return appData;
	}

	private String getContentsOfAsset( String asset ) {
		return getContentsOfAsset( asset, null );
	}

    private String getContentsOfAsset( String asset, String defaultContents ) {
    	String string = "";

        try {
	        InputStream is = context.getAssets().open( asset );
        	BufferedReader in = new BufferedReader( new InputStreamReader( is ) );

            String line;
            StringBuilder buffer = new StringBuilder();

            while ( ( line = in.readLine() ) != null )
            {
                buffer.append( line ).append('\n');
            }

            string = buffer.toString();
        } catch ( Exception e ) {
        	string = defaultContents;
        }

        return string;
    }
}
