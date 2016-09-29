package org.foodispower.chocolatelist.spice.service;

import android.app.Application;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileInputStreamObjectPersister;

public class ImageSpiceService extends SpiceService {

	@Override
	public CacheManager createCacheManager( Application application ) {
        CacheManager cacheManager = new CacheManager();

        // init
        InFileInputStreamObjectPersister inFileInputStreamObjectPersister = new InFileInputStreamObjectPersister( application );

        inFileInputStreamObjectPersister.setAsyncSaveEnabled( true );

        cacheManager.addPersister( inFileInputStreamObjectPersister );

        return cacheManager;
	}
}
