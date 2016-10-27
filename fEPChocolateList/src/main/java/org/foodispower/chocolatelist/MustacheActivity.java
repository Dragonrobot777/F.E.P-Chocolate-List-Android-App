package org.foodispower.chocolatelist;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public class MustacheActivity extends Activity {
	
	public static final String KEY_TEMPLATE_NAME = "template_name";

	protected String templateName;

	public MustacheActivity() {
		super();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_mustache );
		
		setupBackButton();

        if ( null == savedInstanceState ) {
        	String templateName = getIntent().getStringExtra( KEY_TEMPLATE_NAME );
        	this.templateName = templateName;
        }

        populateView();
	}
	
	protected void populateView() {
        String mustacheTemplate = getContentsOfAsset( templateName + ".mustache", "" );
        String bootstrapCss     = getContentsOfAsset( "bootstrap.min.css", "" );

        Template tmpl = Mustache.compiler().compile( mustacheTemplate );

        Map<String, String> data = new HashMap<String, String>();
        data.put( "bootstrap_css", bootstrapCss );

        WebView webView = ( WebView )findViewById( R.id.webView );
        webView.loadDataWithBaseURL( null, tmpl.execute( data ), "text/html", "utf-8", null );
	}

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch ( item.getItemId() ) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed
                // in the Action Bar.
                Intent parentActivityIntent = new Intent( this, MainActivity.class );
                parentActivityIntent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK
                );
                startActivity( parentActivityIntent );
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupBackButton() {
		if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB ) {
			getActionBar().setDisplayHomeAsUpEnabled( true );
		}
    }

    private String getContentsOfAsset( String asset, String defaultContents ) {
    	String string = "";

        try {
	        InputStream is = getAssets().open( asset );
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
