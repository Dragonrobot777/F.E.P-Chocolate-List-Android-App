package org.foodispower.chocolatelist.spice.service;

import android.app.Application;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileInputStreamObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheCreationException;

public class ImageSpiceService extends SpiceService {

	@Override
	public CacheManager createCacheManager( Application application ) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();

        // init
        InFileInputStreamObjectPersister inFileInputStreamObjectPersister = new InFileInputStreamObjectPersister( application );

        inFileInputStreamObjectPersister.setAsyncSaveEnabled( true );

        cacheManager.addPersister( inFileInputStreamObjectPersister );

        return cacheManager;
	}
}
