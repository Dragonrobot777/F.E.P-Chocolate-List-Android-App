package org.foodispower.chocolatelist.adapter;

import java.util.ArrayList;

import org.foodispower.chocolatelist.fragment.FeatureCarouselItemFragment;
import org.foodispower.chocolatelist.model.Feature;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class FeatureCarouselAdapter extends FragmentStatePagerAdapter {

	private ArrayList< Feature > features;

	public FeatureCarouselAdapter( FragmentManager fm ) {
		super( fm );
	}

	@Override
	public Fragment getItem( int index ) {
		if ( null == features ) {
			return null;
		}

		return FeatureCarouselItemFragment.newInstance( features.get( index ) );
	}

	@Override
	public int getCount() {
		if ( null == features ) {
			return 0;
		}

		return features.size();
	}

	public void setFeatures( ArrayList<Feature> features ) {
		this.features = features;

		notifyDataSetChanged();
	}

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE; // To make notifyDataSetChanged() do something
    }

    @Override
    public void notifyDataSetChanged() {
        try {
            super.notifyDataSetChanged();
        } catch (NullPointerException npe) {
            Log.w("", npe);
        }
    }
}
