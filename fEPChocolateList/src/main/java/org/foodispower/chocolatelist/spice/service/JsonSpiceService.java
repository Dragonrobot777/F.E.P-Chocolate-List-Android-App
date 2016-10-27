package org.foodispower.chocolatelist.spice.service;

import android.app.Application;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileInputStreamObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.string.InFileStringObjectPersister;

public class JsonSpiceService extends SpiceService {

	@Override
	public CacheManager createCacheManager( Application application ) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();

        // init
        InFileStringObjectPersister inFileStringObjectPersister = new InFileStringObjectPersister( application );
        InFileInputStreamObjectPersister inFileInputStreamObjectPersister = new InFileInputStreamObjectPersister( application );

        inFileStringObjectPersister.setAsyncSaveEnabled( true );
        inFileInputStreamObjectPersister.setAsyncSaveEnabled( true );

        cacheManager.addPersister( inFileStringObjectPersister );
        cacheManager.addPersister( inFileInputStreamObjectPersister );

        return cacheManager;
	}

}
