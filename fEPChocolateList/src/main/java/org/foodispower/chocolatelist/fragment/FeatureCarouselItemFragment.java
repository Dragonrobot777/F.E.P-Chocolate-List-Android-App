package org.foodispower.chocolatelist.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.CharEncoding;
import org.foodispower.chocolatelist.FeatureActivity;
import org.foodispower.chocolatelist.R;
import org.foodispower.chocolatelist.model.Feature;
import org.foodispower.chocolatelist.spice.request.listener.RemoteImageViewRequestListener;
import org.foodispower.chocolatelist.spice.service.ImageSpiceService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.simple.SmallBinaryRequest;

public class FeatureCarouselItemFragment extends Fragment {

	private SpiceManager spiceManager = new SpiceManager( ImageSpiceService.class );

	public static final String KEY_FEATURE = "feature";

	private Feature feature;
	private boolean interactive;

	private ViewGroup root;

	public static FeatureCarouselItemFragment newInstance( Feature feature ) {
		FeatureCarouselItemFragment f = new FeatureCarouselItemFragment( );

        Bundle args = new Bundle();
        if ( null != feature ) {
        	args.putParcelable( KEY_FEATURE, feature );
        }
        f.setArguments( args );

        return f;
    }

	public FeatureCarouselItemFragment( ) {
		super();

		interactive = true;
	}

	@Override
    public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		spiceManager.start( getActivity() );

		ViewGroup root = ( ViewGroup )inflater.inflate( R.layout.feature_carousel_item_fragment, container, false );

		this.root = root;

//		Button readMoreButton = ( Button )root.findViewById( R.id.read_more_button );

		Bundle bundle = this.getArguments();

		if ( null != bundle && bundle.containsKey( KEY_FEATURE ) ) {
			Feature feature = bundle.getParcelable( KEY_FEATURE );

			setFeature( feature );
		}

        return root;
	}
	
	protected String generateCacheKey( String url ) {
		String cacheKey = null;
		try {
			cacheKey = "a--feature_image---" + URLEncoder.encode( url, CharEncoding.UTF_8 );
		} catch ( UnsupportedEncodingException uee ) {
		}
		return cacheKey;
	}

	public void setFeature( Feature feature ) {
		this.feature = feature;

		configureView();
	}

	public void setInteractive( boolean interactive ) {
		this.interactive = interactive;

		configureView();
	}

	public void configureView() {
		if ( null == root || null == feature ) {
			return;
		}

		if ( null != feature.getTitle() ) {
			TextView titleLabel = ( TextView )root.findViewById( R.id.title_label );

			titleLabel.setText( feature.getTitle() );
		}

		Button readMoreButton = ( Button )root.findViewById( R.id.read_more_button );
		if ( interactive && null != feature.getBody() ) {
			readMoreButton.setVisibility( View.VISIBLE );

			readMoreButton.setOnClickListener( new OnClickListener() {
				
				public void onClick( View v ) {
	                Intent intent = new Intent();
	                intent.setClass( getActivity(), FeatureActivity.class );
	                intent.putExtra( FeatureActivity.KEY_FEATURE, feature ); 
	                startActivity( intent );
				}
			});
		} else {
			readMoreButton.setVisibility( View.GONE );
		}

    	if ( null != feature.getCoverImageUrlRetina() ) {

    		ImageView imageView = ( ImageView )root.findViewById( R.id.feature_image_view );

    		String url = ( interactive || ( null == feature.getArticleHeaderImageUrlRetina() ) ) ? feature.getCoverImageUrlRetina() : feature.getArticleHeaderImageUrlRetina();

	        spiceManager.execute(
	        		new SmallBinaryRequest( url ),
	        		generateCacheKey( url ),
	        		DurationInMillis.ONE_HOUR,
	        		new RemoteImageViewRequestListener( imageView, url )
	        );
    	}
	}

}
