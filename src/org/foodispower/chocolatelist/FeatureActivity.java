package org.foodispower.chocolatelist;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.foodispower.chocolatelist.fragment.FeatureCarouselItemFragment;
import org.foodispower.chocolatelist.model.Feature;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public class FeatureActivity extends FragmentActivity {
	
	public static final String KEY_FEATURE = "feature";

	private Feature feature;

	public FeatureActivity() {
		super();
	}

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_feature );

        setupBackButton();

        if ( null == savedInstanceState ) {

        	Feature feature = getIntent().getParcelableExtra( KEY_FEATURE );
        	this.feature = feature;

        	FeatureCarouselItemFragment fragment = ( FeatureCarouselItemFragment )getSupportFragmentManager().findFragmentById( R.id.header_fragment );
        	fragment.setInteractive( false );
        	fragment.setFeature( feature );
        }

        String mustacheTemplate = getContentsOfAsset( "carousel_item_body.mustache", "{{{content}}}" );
        String bootstrapCss     = getContentsOfAsset( "bootstrap.min.css", "" );

        Template tmpl = Mustache.compiler().compile( mustacheTemplate );

        Map<String, String> data = new HashMap<String, String>();
        data.put( "content",       feature.getBody() );
        data.put( "bootstrap_css", bootstrapCss );

        WebView webView = ( WebView )findViewById( R.id.body_webview );
        webView.loadDataWithBaseURL( null, tmpl.execute( data ), "text/html", "utf-8", null );
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    public boolean onCreateOptionsMenu( Menu menu ) {
    	if ( feature.hasLink() ) {
    		MenuItem mi = menu.add( feature.getLinkTitle() );

    		if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB ) {
    			mi.setShowAsAction( MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS );
    		}
    	}

    	return super.onCreateOptionsMenu( menu );
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
            case 0:
            	Intent i = new Intent( Intent.ACTION_VIEW );
            	i.setData( Uri.parse( feature.getLinkTarget() ) );
            	startActivity( i );
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
