package org.foodispower.chocolatelist.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.commons.lang3.CharEncoding;
import org.foodispower.chocolatelist.R;
import org.foodispower.chocolatelist.model.Company;
import org.foodispower.chocolatelist.spice.request.listener.RemoteImageViewRequestListener;
import org.foodispower.chocolatelist.spice.service.ImageSpiceService;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.simple.SmallBinaryRequest;

public class CompanyAdapter extends ArrayAdapter< Company > {

	
	private SpiceManager spiceManager = new SpiceManager( ImageSpiceService.class );
	
	private ArrayList< Company > companies;

	public CompanyAdapter(Context context, int textViewResourceId,
			ArrayList<Company> companies) {
		super( context, textViewResourceId, companies) ;

		spiceManager.start( context );

		this.companies = companies;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
	    if ( null == v ) {
			LayoutInflater vi = ( LayoutInflater )this.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			v = vi.inflate( R.layout.company_list_cell, null );
			v.setTag( R.id.name_label,      v.findViewById( R.id.name_label ) );
			v.setTag( R.id.notes_label,     v.findViewById( R.id.notes_label ) );
			v.setTag( R.id.status_label,    v.findViewById( R.id.status_label ) );
			v.setTag( R.id.logo_image_view, v.findViewById( R.id.logo_image_view ) );
	    }

		TextView nameLabel      = ( TextView )  v.getTag( R.id.name_label );
		TextView notesLabel     = ( TextView )  v.getTag( R.id.notes_label );
		TextView statusLabel    = ( TextView )  v.getTag( R.id.status_label );
		ImageView logoImageView = ( ImageView ) v.getTag( R.id.logo_image_view );

    	if ( null != logoImageView.getTag() ) {
    		String cacheKey = generateCacheKey( ( String ) logoImageView.getTag() );

    		if ( null != cacheKey ) {
    			spiceManager.cancel( ImageSpiceService.class, cacheKey );
    		}
    	}

		// reset
		nameLabel.setText("");
		notesLabel.setText("");
		statusLabel.setText("");
		logoImageView.setImageResource( R.drawable.default_logo );
	           
	    final Company company = companies.get( position );

	    if ( null != company) {
	        if ( null != nameLabel ) {
	        	nameLabel.setText( company.getName() );
	        }
	
	        if ( null != notesLabel ) {
	        	notesLabel.setText( company.getNotes() );
	        }

	        if ( null != statusLabel ) {
	        	String status;
	        	Drawable backgroundDrawable;

	        	switch ( company.getStatus() ) {
					case RECOMMENDED:
						status = this.getContext().getString( R.string.status_recommended );
						backgroundDrawable = this.getContext().getResources().getDrawable( R.drawable.status_recommended );
						break;

					default:
					case CANNOT_RECOMMEND:
						status = this.getContext().getString( R.string.status_cannot_recommend );
						backgroundDrawable = this.getContext().getResources().getDrawable( R.drawable.status_cannot_recommend );
						break;
				}

	        	if ( null != status ) {
	        		statusLabel.setText( status );
	        		statusLabel.setBackgroundDrawable( backgroundDrawable );
	        	}
	        }

	        if ( null != logoImageView ) {
	        	if ( null != company.getLogoUrlRetina() ) {

	        		logoImageView.setTag( company.getLogoUrlRetina() );

	    	        spiceManager.execute(
	    	        		new SmallBinaryRequest( company.getLogoUrlRetina() ),
	    	        		generateCacheKey( company.getLogoUrlRetina() ),
	    	        		DurationInMillis.ONE_WEEK,
	    	        		new RemoteImageViewRequestListener( logoImageView, company.getLogoUrlRetina() )
	    	        );
	        	}
	        }
	    }

	    return v;
	}

	protected String generateCacheKey( String url ) {
		String cacheKey = null;
		try {
			cacheKey = "c--company_logo_image---" + URLEncoder.encode( url, CharEncoding.UTF_8 );
		} catch (UnsupportedEncodingException uee) {
		}
		return cacheKey;
	}

}
