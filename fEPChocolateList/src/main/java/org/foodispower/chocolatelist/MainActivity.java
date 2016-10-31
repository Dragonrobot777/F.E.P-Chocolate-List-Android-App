package org.foodispower.chocolatelist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.viewpagerindicator.CirclePageIndicator;

import io.fabric.sdk.android.Fabric;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import org.foodispower.chocolatelist.adapter.CompanyAdapter;
import org.foodispower.chocolatelist.adapter.FeatureCarouselAdapter;
import org.foodispower.chocolatelist.data.AppDataManager;
import org.foodispower.chocolatelist.manager.FavouritesManager;
import org.foodispower.chocolatelist.model.AppData;
import org.foodispower.chocolatelist.model.Company;
import org.foodispower.chocolatelist.model.Feature;
import org.foodispower.chocolatelist.spice.request.AppDataJsonRequest;
import org.foodispower.chocolatelist.spice.service.JsonSpiceService;


public class MainActivity extends FragmentActivity {

	public enum CompanyListFilter {
		ALL,
		ONLY_RECOMMENDED,
		ONLY_NOT_RECOMMENDED,
		FAVOURITES,
	};

//	private static final String JSON_CACHE_KEY = "appdata_json";
	private SpiceManager spiceManager = new SpiceManager( JsonSpiceService.class );
	private FavouritesManager favouritesManager;

	private ArrayList< Company > companies = new ArrayList< Company >();
	private ListView contentListView;
	private CompanyAdapter companyAdapter;
	private ViewGroup carouselLayout;
	
	private FeatureCarouselAdapter featureCarouselAdapter;

	private List< Company > allCompanies;
	private String searchTerm;
	private CompanyListFilter filter;

	private AppData appData;

	public MainActivity() {
		super();

		searchTerm = "";
		filter = CompanyListFilter.ALL;
	}

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

		Fabric.with(this, new Answers());
        Fabric.with(this, new Crashlytics());

		setContentView( R.layout.activity_main );

        favouritesManager = FavouritesManager.getInstance( this );

        if ( null == savedInstanceState ) {
	        contentListView = ( ListView ) findViewById( R.id.content_list_view );
	        contentListView.setFastScrollEnabled(true);
	
	        LayoutInflater vi = getLayoutInflater();
	        carouselLayout = ( ViewGroup )vi.inflate( R.layout.feature_carousel, contentListView, false );
	
	        final ViewPager featureCarousel = ( ViewPager )carouselLayout.findViewById( R.id.carousel_view_pager );
	        featureCarousel.setOnTouchListener( new OnTouchListener() {
				
				public boolean onTouch( View v, MotionEvent event ) {
					switch ( event.getAction() ) {
						case MotionEvent.ACTION_DOWN:
							contentListView.requestDisallowInterceptTouchEvent(true);
							break;
	
						case MotionEvent.ACTION_UP:
						case MotionEvent.ACTION_CANCEL:
							contentListView.requestDisallowInterceptTouchEvent(false);
							break;
					}
	
					return false;
				}
			});

	        featureCarouselAdapter = new FeatureCarouselAdapter( getSupportFragmentManager() );
	        featureCarousel.setAdapter( featureCarouselAdapter );
	
	        CirclePageIndicator indicator = ( CirclePageIndicator )carouselLayout.findViewById( R.id.indicator );
	        float radius = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 2.5f, getResources().getDisplayMetrics() );
	        indicator.setRadius( radius );
	        indicator.setViewPager( featureCarousel );
	
	        companyAdapter = new CompanyAdapter( this, R.layout.company_list_cell, companies );
	
	        contentListView.addHeaderView( carouselLayout );
	        contentListView.setAdapter( companyAdapter );
	        contentListView.setClickable( true );
	
	        final MainActivity mainActivity = this;
	        contentListView.setOnItemClickListener( new OnItemClickListener() {
	
	        	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	
	        	    Company company = (Company)contentListView.getItemAtPosition(position);
	
	                Intent intent = new Intent();
	                intent.setClass( mainActivity, CompanyActivity.class );
	                intent.putExtra( CompanyActivity.KEY_COMPANY, company ); 
	                startActivity( intent );
	        	}
	        });

			contentListView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					int headerVisiblePart = contentListView.getChildAt(0).getHeight() + contentListView.getChildAt(0).getTop();
					//Log.d("Chocolate List", String.valueOf(headerVisiblePart));

					if (motionEvent.getY() <= headerVisiblePart) return true;

					return false;
				}
			});

            initialiseAppData();
        	updateAppData();
        }
    }

    @Override
    protected void onStart() {
    	spiceManager.start( this );
    	super.onStart();
    }
    
    @Override
    protected void onStop() {
    	spiceManager.shouldStop();
    	super.onStop();
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();

		System.exit(0);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.action_search:
            	openSearchDialog();
            	break;

            case R.id.action_filter_all:
            	filter = CompanyListFilter.ALL;
            	filterCompaniesAndUpdateList();
            	break;

            case R.id.action_filter_only_recommended:
            	filter = CompanyListFilter.ONLY_RECOMMENDED;
            	filterCompaniesAndUpdateList();
            	break;
            
            case R.id.action_filter_only_not_recommended:
            	filter = CompanyListFilter.ONLY_NOT_RECOMMENDED;
            	filterCompaniesAndUpdateList();
            	break;

            case R.id.action_filter_favourites:
            	filter = CompanyListFilter.FAVOURITES;
            	filterCompaniesAndUpdateList();
            	break;

            case R.id.action_contact:
            	openContactActivity();
            	break;

            case R.id.action_understanding:
            	openUnderstandingActivity();
            	break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSearchDialog() {
    	final EditText input = new EditText( this );

    	final AlertDialog ad = new AlertDialog.Builder( this )
    	.setTitle( R.string.action_search )
    	.setView( input )
        .setPositiveButton( android.R.string.search_go, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int whichButton ) {
            	setSearchTerm( input.getText().toString() );
            }
        }).setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int whichButton ) {
            }
        }).create();

    	input.setText( searchTerm );
    	input.setSelectAllOnFocus( true );
    	input.setSingleLine( true );
    	input.setOnEditorActionListener( new OnEditorActionListener() {
            public boolean onEditorAction( TextView v, int actionId, KeyEvent event ) {
                if ( ( null != event && ( KeyEvent.KEYCODE_ENTER == event.getKeyCode() ) ) || ( EditorInfo.IME_ACTION_DONE == actionId ) ) {
                	setSearchTerm( v.getText().toString() );
                	ad.dismiss();
                }    
                return false;
            }
        });

        ad.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE );
	    ad.show();
    }

    protected void openContactActivity() {
    	openMustacheActivity( "contact" );
	}
    
    protected void openUnderstandingActivity() {
		openMustacheActivity( "recommendations" );
	}
    
	protected void openMustacheActivity( String templateName ) {
        Intent intent = new Intent();
        intent.setClass( this, MustacheActivity.class );
        intent.putExtra( MustacheActivity.KEY_TEMPLATE_NAME, templateName ); 
        startActivity( intent );
	}

    public void setSearchTerm( String searchTerm ) {
    	this.searchTerm = searchTerm;
    	filterCompaniesAndUpdateList();
    }

    public void setAppData( AppData appData ) {
    	this.appData = appData;

    	if ( null == appData ) {
    		return;
    	}

    	// refresh stuffs
    	allCompanies = appData.getCompanies();
    	filterCompaniesAndUpdateList();

    	int carouselLayoutVisiblity;
    	if ( 0 == appData.getFeatures().size() ) {
    		carouselLayoutVisiblity = View.GONE;
    	} else {
    		carouselLayoutVisiblity = View.VISIBLE;
    	}
	    for (int i = 0; i < carouselLayout.getChildCount(); i++) {
	        carouselLayout.getChildAt( i ).setVisibility( carouselLayoutVisiblity );
	    }

    	featureCarouselAdapter.setFeatures( ( ArrayList< Feature > ) appData.getFeatures() );

        CirclePageIndicator indicator = ( CirclePageIndicator )carouselLayout.findViewById( R.id.indicator );
        indicator.notifyDataSetChanged();
    }
    
    private void initialiseAppData() {
    	setAppData(new AppDataManager( this ).instantiateAppData());
    }

    private void updateAppData() {
    	// TODO: should this fire on a timer every now and again?
    	spiceManager.execute( new AppDataJsonRequest( this ), null, DurationInMillis.NEVER, new AppDataRequestListener() );
    }

    private void filterCompaniesAndUpdateList() {
    	companyAdapter.clear();

    	for ( Iterator< Company > i = allCompanies.iterator(); i.hasNext(); ) {
    		Company company = i.next();

    		Boolean filterPassed = false;

    		switch (filter) {
			case ALL:
				filterPassed = true;
				break;
			case ONLY_RECOMMENDED:
				filterPassed = (Company.CompanyStatus.RECOMMENDED == company.getStatus());
				break;
			case ONLY_NOT_RECOMMENDED:
				filterPassed = (Company.CompanyStatus.RECOMMENDED != company.getStatus());
				break;
			case FAVOURITES:
				filterPassed = favouritesManager.isCompanyFavourited( company );
				break;
    		}

    		if ( !filterPassed ) {
    			continue;
    		}

			//Log.d("Chocolate List", "C name " + company.getName());
			//Log.d("Chocolate List", "C notes " + company.getNotes());
    		String companyNameNormalised = company.getName().toLowerCase( Locale.getDefault() );
			String companyNotesNormalised = "";
			if (company.getNotes() != null) {
				companyNotesNormalised = company.getNotes().toLowerCase(Locale.getDefault());
			}
    		String searchTermNormalised = searchTerm.toLowerCase( Locale.getDefault() );

    		if ( companyNameNormalised.contains( searchTermNormalised ) || companyNotesNormalised.contains(searchTermNormalised)) {
    			companyAdapter.add( company );
    		}
    	}
    }

    public final class AppDataRequestListener implements RequestListener< AppData > {

        public void onRequestFailure( SpiceException spiceException ) {
        	// quietly fail
        }

        public void onRequestSuccess( final AppData result ) {

        	if ( null == result ) {
        		return;
        	}

        	if ( result.getLastUpdated().after( appData.getLastUpdated() ) ) {
        		setAppData( result );
        	}
        }
    }
}
