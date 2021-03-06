package com.davidtschida.materialdiningcourts.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.davidtschida.materialdiningcourts.R;
import com.davidtschida.materialdiningcourts.adapters.DiningCourtPagerAdapter;
import com.davidtschida.materialdiningcourts.eventbus.DateChosenEvent;
import com.davidtschida.materialdiningcourts.eventbus.EventBus;
import com.davidtschida.materialdiningcourts.eventbus.MealChosenEvent;
import com.davidtschida.materialdiningcourts.eventbus.ShowSnackbarEvent;
import com.davidtschida.purduemenu.util.DefaultMealChooser;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import org.joda.time.LocalDate;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MealViewActivity extends NavDrawerActivity
{

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @Bind(R.id.container)
    protected ViewPager mViewPager;
    @Bind(R.id.tabs)
    protected TabLayout tabLayout;
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.coordinator)
    protected CoordinatorLayout mCoordinatorLayout;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private DiningCourtPagerAdapter mSectionsPagerAdapter;

    private DateChosenEvent mLastDateEvent;
    private MealChosenEvent mLastMealEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupToolbar();
        //Must be done AFTER initing the spinner. Relies on spinner having a value.
        EventBus.getBus().register(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new DiningCourtPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(mViewPager);

        if(mLastMealEvent == null) {
            new DefaultMealChooser().initiateDefaultMealSelection();
        }
    }

    @Subscribe @SuppressWarnings("unused")
    public void dateChosen(DateChosenEvent event) {
        Timber.i("Got the date chosen event %s", event);
        mLastDateEvent = event;
        if(mLastMealEvent != null) {
            setMealTitle(mLastMealEvent.getMeal(), mLastDateEvent.getLocalDate());
        }
    }

    @Subscribe @SuppressWarnings("unused")
    public void mealChosen(MealChosenEvent event) {
        Timber.i("Got the meal chosen event %s", event);
        mLastMealEvent = event;
        if(mLastDateEvent != null) {
            setMealTitle(mLastMealEvent.getMeal(), mLastDateEvent.getLocalDate());
        }
    }


    @Subscribe @SuppressWarnings("unused")
    public void onShowSnackbarEvent(ShowSnackbarEvent event) {
        Snackbar.make(mCoordinatorLayout, event.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    private void setMealTitle(String meal, LocalDate localDate) {
        if(getSupportActionBar() != null) {
            String dateString;
            if(localDate.equals(LocalDate.now())) {
                dateString = "Today";
            } else if(localDate.equals(LocalDate.now().plusDays(1))) {
                dateString = "Tomorrow";
            } else if(localDate.equals(LocalDate.now().plusDays(-1))) {
                dateString = "Yesterday";
            } else {
                dateString = localDate.toString("MM/dd/yyyy");
            }
            getSupportActionBar().setTitle(String.format("%s %s", meal, dateString));
        }
    }

    @Produce @SuppressWarnings("unused") public DateChosenEvent produceChosenDate() {
        if(mLastDateEvent == null) {
            mLastDateEvent = new DateChosenEvent(LocalDate.now());
        }
        return mLastDateEvent;
    }

    @Produce @SuppressWarnings("unused") public MealChosenEvent produceChosenMeal() {
        if(mLastMealEvent == null) {
            mLastMealEvent = new MealChosenEvent("Breakfast");
        }
        return mLastMealEvent;
    }


    private void setupToolbar() {
        //Set up the spinner in the toolbar with meals data.
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Menus");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(new IconDrawable(this, MaterialIcons.md_menu)
                    .colorRes(R.color.white)
                    .actionBarSize());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getBus().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meal_view, menu);
        return true;
    }
}
