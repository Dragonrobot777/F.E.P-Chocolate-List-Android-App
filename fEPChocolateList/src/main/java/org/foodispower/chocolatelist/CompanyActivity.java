package org.foodispower.chocolatelist;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.CharEncoding;
import org.foodispower.chocolatelist.manager.FavouritesManager;
import org.foodispower.chocolatelist.model.Company;
import org.foodispower.chocolatelist.model.Company.CompanyStatus;
import org.foodispower.chocolatelist.spice.request.listener.RemoteImageViewRequestListener;
import org.foodispower.chocolatelist.spice.service.ImageSpiceService;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.simple.SmallBinaryRequest;

public class CompanyActivity extends Activity {
	
	public static final String KEY_COMPANY = "company";

	private SpiceManager spiceManager = new SpiceManager( ImageSpiceService.class );
	private Company company;
	
	private FavouritesManager favouritesManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_company );

		favouritesManager = FavouritesManager.getInstance( this );
		
		setupBackButton();

		spiceManager.start( this );

        if ( null == savedInstanceState ) {

        	Company company = getIntent().getParcelableExtra( KEY_COMPANY );
        	this.company = company;
        }

        populateView();
	}

	protected void populateView() {
		if ( null == company ) {
			return;
		}

		final TextView nameLabel           = ( TextView )  findViewById( R.id.name_label );
		final TextView notesLabel          = ( TextView )  findViewById( R.id.notes_label );
		final TextView statusLabel         = ( TextView )  findViewById( R.id.status_label );
		final ImageView logoImageView      = ( ImageView ) findViewById( R.id.logo_image_view );
		final TextView statusReasonLabel   = ( TextView )  findViewById( R.id.status_reason_label );
		final Button shareOnTwitterButton  = ( Button )    findViewById( R.id.button_share_on_twitter );
		final Button shareOnFacebookButton = ( Button )    findViewById( R.id.button_share_on_facebook );
		final ToggleButton toggleFavouriteButton = ( ToggleButton ) findViewById( R.id.button_toggle_favourite );

    	if ( null != logoImageView.getTag() ) {
    		String cacheKey = generateCacheKey( ( String ) logoImageView.getTag() );

    		if ( null != cacheKey ) {
    			// TODO: there are threading problems when cancelling: NPE to failure delegates :(
//    			spiceManager.cancel( ImageSpiceService.class, cacheKey );
    		}
    	}

		// reset
		nameLabel.setText( "" );
		notesLabel.setText( "" );
		statusLabel.setText( "" );
		logoImageView.setImageResource( R.drawable.default_logo );
		statusReasonLabel.setText( "" );

		if ( null != nameLabel ) {
			nameLabel.setText( company.getName() );
		}

		if ( null != notesLabel ) {
			notesLabel.setText( company.getNotes() );
		}

		if ( null != statusLabel ) {
			String status = null;
			Drawable backgroundDrawable;

			switch ( company.getStatus() ) {
			case RECOMMENDED:
				status = getString( R.string.status_recommended );
				backgroundDrawable = getResources().getDrawable( R.drawable.status_recommended );
				break;

			default:
			case CANNOT_RECOMMEND:
				status = getString( R.string.status_cannot_recommend );
				backgroundDrawable = getResources().getDrawable( R.drawable.status_cannot_recommend );
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

		if ( null != statusReasonLabel ) {
			String statusReason = null;

			switch ( company.getStatusReason() ) {
			case RECOMMENDED_BENEFIT_OF_THE_DOUBT:
				statusReason = getString( R.string.status_recommended_benefit_of_the_doubt );
				break;

			case CANNOT_RECOMMEND_DID_NOT_DISCLOSE:
				statusReason = getString( R.string.status_reason_cannot_recommend_did_not_disclose );
				break;

			case CANNOT_RECOMMEND_DID_NOT_RESPOND:
				statusReason = getString( R.string.status_reason_cannot_recommend_did_not_respond );
				break;

			case CANNOT_RECOMMEND_WORKING_ON_ISSUES:
				statusReason = getString( R.string.status_reason_cannot_recommend_working_on_issues );
				break;

			case CANNOT_RECOMMEND_RESPONDED:
				statusReason = getString( R.string.status_reason_cannot_recommend_responded );
				break;

			default:
			case RECOMMENDED:
			case CANNOT_RECOMMEND:
				break;
			}

			if ( null != statusReason ) {
				statusReasonLabel.setVisibility( View.VISIBLE );
				statusReasonLabel.setText( statusReason );
			} else {
				statusReasonLabel.setVisibility( View.GONE );
			}
		}

		if ( CompanyStatus.RECOMMENDED == company.getStatus() ) {
			shareOnTwitterButton.setVisibility( View.VISIBLE );
			shareOnFacebookButton.setVisibility( View.VISIBLE );
		} else {
			shareOnTwitterButton.setVisibility( View.GONE );
			shareOnFacebookButton.setVisibility( View.GONE );
		}

		toggleFavouriteButton.setChecked( favouritesManager.isCompanyFavourited( company ) );

		// events
		toggleFavouriteButton.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked ) {
					favouritesManager.addFavouriteCompany( company );
				} else {
					favouritesManager.removeFavouriteCompany( company );
				}
			}
		});

		shareOnTwitterButton.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				String message = company.getName() + " is among the companies selling vegan chocolate recommended by @FoodIsPower http://www.foodispower.org/chocolatelist.php";

				Intent share = new Intent( Intent.ACTION_SEND );

				if ( CompanyActivity.isIntentAvailable( getApplicationContext(),"application/twitter" ) ){
					share.setType( "application/twitter" );
				} else {
					share.setType( "plain/text" );
				}

				share.putExtra( Intent.EXTRA_TEXT, message );
				startActivity( Intent.createChooser(share, "Share using") );
			}
		});

		shareOnFacebookButton.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				String message = company.getName() + " is among the companies selling vegan chocolate not sourced from the worst forms of child labor, which is why they are recommended by Food Empowerment Project. http://www.foodispower.org/chocolatelist.php";

				Intent share = new Intent( Intent.ACTION_SEND );
				share.setType( "plain/text" );
				share.putExtra( Intent.EXTRA_TEXT, message );
				startActivity( Intent.createChooser(share, "Share using") );
			}
		});
	}

	public static boolean isIntentAvailable( Context context, String action ) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent( action );
	    List<ResolveInfo> list =
	            packageManager.queryIntentActivities( intent,
	                    PackageManager.MATCH_DEFAULT_ONLY );
	    return list.size() > 0;
	}

	protected String generateCacheKey( String url ) {
		String cacheKey = null;
		try {
			cacheKey = "c--company_logo_image---" + URLEncoder.encode( url, CharEncoding.UTF_8 );
		} catch (UnsupportedEncodingException uee) {
		}
		return cacheKey;
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
}
