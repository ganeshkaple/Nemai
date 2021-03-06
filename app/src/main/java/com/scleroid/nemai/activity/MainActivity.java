package com.scleroid.nemai.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scleroid.nemai.GarlandApp;
import com.scleroid.nemai.R;
import com.scleroid.nemai.activity.registration.LoginActivity;
import com.scleroid.nemai.activity.registration.SocialRegisterActivity;
import com.scleroid.nemai.data.AppDatabase;
import com.scleroid.nemai.data.SessionManager;
import com.scleroid.nemai.data.controller.AddressLab;
import com.scleroid.nemai.data.controller.ParcelLab;
import com.scleroid.nemai.data.localdb.PinCode;
import com.scleroid.nemai.data.models.Address;
import com.scleroid.nemai.data.models.Parcel;
import com.scleroid.nemai.fragment.AddressListFragment;
import com.scleroid.nemai.fragment.HomeFragment;
import com.scleroid.nemai.fragment.OrdersFragment;
import com.scleroid.nemai.fragment.PartnersFragment;
import com.scleroid.nemai.fragment.ProfileFragment;
import com.scleroid.nemai.fragment.SettingsFragment;
import com.scleroid.nemai.utils.ProfileUtils;

import java.util.ArrayList;
import java.util.List;

import io.bloco.faker.Faker;

public class MainActivity extends AppCompatActivity implements GarlandApp.FakerReadyListener {

    // urls to load navigation header background image
    // and profile image
    private static final String TAG = MainActivity.class.getSimpleName();
    //TODO add header for cover image of social login
    // tags used to attach the fragments
    private static final String TAG_DASHBOARD = "dashboard";
    private static final String TAG_PARTNERS = "partners";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_PROFILE = "profile";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_ORDERS = "orders";
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_DASHBOARD;
    public static SessionManager session;
    private final ProfileUtils profileUtils = new ProfileUtils(this);
    public Context mContext;
    ImageButton btn_search;
    int[] totalParcels;
    private List<PinCode> mResultPinList;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;

    private ParcelLab parcelLab;
    private ViewPager mViewPager;
    private List<Parcel> parcels;
    private Handler handler;


    @NonNull
    public static Intent newIntent(Context activity) {
        return new Intent(activity, MainActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
*/
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

// TODO add after development complet
       /*// Rollbar.init(this, "fe4fb1ae0576446eb3b4b7b082aa25bf", "development");
        Rollbar.reportMessage("Test message", "debug");
        Rollbar.reportException(new Exception("Test exception"));*/
        session = new SessionManager(getApplicationContext());


        session.setLogin(false);
        session.setVerified(false);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else if (!session.isVerified()) {
            startActivity(new Intent(MainActivity.this, SocialRegisterActivity.class));
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();*/
        handler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        //     drawer.setScrimColor(Color.parseColor("#33000000"));

        //  mViewPager = findViewById(R.id.frame_pager);


        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.name);

        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        imgProfile = navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, TrackingActivity.class);
            startActivity(i);
        });


        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();
        //findPins(getApplicationContext());
        //backgroundTasks.newParcel(getApplicationContext());

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_DASHBOARD;
            loadHomeFragment();
        }


    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {

        profileUtils.setProfileName(txtName);

        /*// loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);*/
        profileUtils.setHeaderBackgroundImage(imgNavHeaderBg);


        profileUtils.setUserProfilePicture(imgProfile);


    }



    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        // This method will trigger on item Click of navigation menu
        navigationView.setNavigationItemSelectedListener(menuItem -> {

            //Check to see which item was being clicked and perform appropriate action
            switch (menuItem.getItemId()) {
                //Replacing the main content with ContentFragment Which is our Inbox View;
                case R.id.nav_dashboard:
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_DASHBOARD;
                    break;
                case R.id.nav_orders:
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_ORDERS;
                    break;
                case R.id.nav_address:
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_ADDRESS;
                    break;
                case R.id.nav_partners:
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_PARTNERS;
                    break;

                case R.id.nav_profile:
                    navItemIndex = 4;
                    CURRENT_TAG = TAG_PROFILE;
                    break;
                case R.id.nav_settings:
                    navItemIndex = 5;
                    CURRENT_TAG = TAG_SETTINGS;
                    break;
                case R.id.nav_about_us:
                    // launch new intent instead of loading fragment
                    startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                    drawer.closeDrawers();
                    return true;
                case R.id.nav_privacy_policy:
                    // launch new intent instead of loading fragment
                    startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                    drawer.closeDrawers();
                    return true;
                default:
                    navItemIndex = 0;
            }

            //Checking if the item is in checked state or not, if not make it in checked state
            if (menuItem.isChecked()) {
                menuItem.setChecked(false);
            } else {
                menuItem.setChecked(true);
            }
            menuItem.setChecked(true);

            loadHomeFragment();

            return true;
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_DASHBOARD;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
       /* mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return getCurrentFragment();
            }

            @Override
            public int getCount() {
                return totalParcels[0];
            }
        });*/
        Runnable mPendingRunnable = () -> {
            // update the main content by replacing fragments

            Fragment fragment = getCurrentFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        };

        // If mPendingRunnable is not null, then add to the message queue
        boolean post = handler.post(mPendingRunnable);

        // show or hide the fab button


        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private Fragment getCurrentFragment() {
        switch (navItemIndex) {
            case 0:
                // dashboard
                return new HomeFragment();
            case 1:
                // orders fragment
                return new OrdersFragment();
            case 2:
                // address fragment
                return new AddressListFragment();
            case 3:
                // partners fragment
                return new PartnersFragment();

            case 4:
                // profile fragment
                return new ProfileFragment();

            case 5:
                //setting fragment
                return new SettingsFragment();

            default:
                return new HomeFragment();// HomeFragment.newInstance(HomeFragment.parcelCount);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected

        // when fragment is notifications, load the menu created for notifications
        /*if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            Intent i = new Intent(MainActivity.this, TrackingActivity.class);
            startActivity(i);
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        ParcelLab.boomTable(mContext);
        AppDatabase.destroyInstance();
        super.onDestroy();
    }


    @Override
    public void onFakerReady(Faker faker) {
        //populateData(faker);
    }

    private void populateData(Faker faker) {
        List<Address> innerData = new ArrayList<>();
        List<Address> tempList = new ArrayList<>();
        ParcelLab.addParcel(createParcelData(faker), AppDatabase.getAppDatabase(getApplicationContext()));
        for (int i = 0; i < 5; i++) {
            // ParcelLab.addParcel(createParcelData(faker), AppDatabase.getAppDatabase(context));
            AddressLab.addAddress(createInnerData(faker), AppDatabase.getAppDatabase(getApplicationContext()));
        }

    }


    private Address createInnerData(Faker faker) {
        return new Address(
                faker.name.name(),
                faker.address.streetAddress(),
                faker.address.streetName(),
                faker.address.state(),
                faker.address.city(),
                (faker.number.between(111111, 999999)) + "",
                faker.phoneNumber.phoneNumber()
        );
    }

	private com.scleroid.nemai.data.models.Parcel createParcelData(Faker faker) {
        String source = faker.address.city();
        String dest = faker.address.city();
		return new com.scleroid.nemai.data.models.Parcel(
                source,
                dest,
                "Domestic",
                "Parcel",
                faker.number.positive(0, 10),
                faker.number.positive(0, 1000),
                faker.number.positive(),
                faker.number.positive(),
                faker.number.positive(),
                faker.company.catchPhrase(),
                faker.date.forward(),
                new PinCode(source, (faker.number.between(111111, 999999)) + "", faker.address.state(), faker.address.streetName()),
                new PinCode(dest, (faker.number.between(111111, 999999)) + "", faker.address.state(), faker.address.streetName())
        );
    }

}
