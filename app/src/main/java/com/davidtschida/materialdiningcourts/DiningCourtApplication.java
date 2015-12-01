package com.davidtschida.materialdiningcourts;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.parse.Parse;
import com.parse.ParseUser;

import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;

/**
 * Created by david on 11/3/2015.
 */
public class DiningCourtApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeModule())
                .with(new MaterialModule());
        JodaTimeAndroid.init(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        // Parse //
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parseApplicationId), getString(R.string.parseClientKey));

        ParseUser.enableAutomaticUser();
    }
}
