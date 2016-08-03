package wind.newwindalarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.OnSignInClickListener {

    private static MainActivity instance;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static int deviceId = -1;
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //startSendLogActivity();

        //SendLoagcatMail();
    }

    private void startSendLogActivity() {

        Intent resultIntent = new Intent(this, SendLogActivity.class);
        //startActivityForResult(resultIntent, 1);
        startActivity(resultIntent);
        finish();
    }



    public void SendLoagcatMail() {


        DateFormat df = new SimpleDateFormat("ddMMyyyyHHmm");
        String date = df.format(Calendar.getInstance().getTime());

        // save logcat in file
        File outputFile = new File(Environment.getExternalStorageDirectory(),
                "logcat" /*-+ date*/ + ".txt");
        try {
            Runtime.getRuntime().exec(
                    "logcat -f " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //send file using email
        /*Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // Set type to "email"
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"giaggi70@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, outputFile.getAbsolutePath());
        // the mail subject



        String str = outputFile.getAbsolutePath();

        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject " + str);
        startActivity(Intent.createChooser(emailIntent , "Send email..."));*/
    }

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private boolean isReceiverRegistered;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    GoogleSignInAccount acct;

    public static final String GO_DIRECTLY_TO_SPOT_DETAILS = "GoDirectlyToSpotDetails";


    public static List<Spot> spotList;
    private Settings mSettings;

    PanelFragment panelFragment;
    ProgramFragment programFragment;
    ProgramListFragment programListFragment;
    SettingsFragment settingsFragment;
    ProfileFragment profileFragment;
    SpotMeteoListFragment spotMeteoListFragment;
    SplashScreenFragment splashFragment;

    // google properties
    TextView mUserNameTextView;
    TextView memailTextView;
    ImageView mUserImageImageView;

    FloatingActionButton addFab;
    FloatingActionButton refreshFab;


    private UserProfile mProfile = null;
    static boolean signedIn = false;
    static int nextFragment = -1;

    public static AlarmPreferences preferences = new AlarmPreferences();

    public static MainActivity getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (savedInstanceState != null) {

        //  return;
        //}
        Log.i("PROVA", "MESSAGGIO");

        //sendLogToMail();
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));

        String hh = null;
        //hh.toString();

        instance = this;

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                wind.newwindalarm.CommonUtilities.DISPLAY_MESSAGE_ACTION));


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addFab = (FloatingActionButton) findViewById(R.id.addFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                programListFragment.createProgram();
            }
        });


        refreshFab = (FloatingActionButton) findViewById(R.id.refreshFab);
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                panelFragment.getMeteoData();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mUserNameTextView = (TextView) header.findViewById(R.id.UserNameTextView);
        memailTextView = (TextView) header.findViewById(R.id.UserEmailTextView);
        mUserImageImageView = (ImageView) header.findViewById(R.id.imageView);

        mSettings = new Settings(this);
        mSettings.setListener(new Settings.SettingsListener() {
            @Override
            public void onChangeOrder(List<Long> order) {
                panelFragment.setSpotOrder(order);
            }

            @Override
            public void onChangeList(List<Long> list) {

            }
        });

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                    mInformationTextView.setVisibility(ProgressBar.GONE);
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        // carica la spot list per la combo dei programmmi
        //getSpotListFromServer();

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();*/

        // Configure sign-in to request offline access to the user's ID, basic
        // profile, and Google Drive. The first time you request a code you will
        // be able to exchange it for an access token and refresh token, which
        // you should store. In subsequent calls, the code will only result in
        // an access token. By asking for profile access (through
        // DEFAULT_SIGN_IN) you will also get an ID Token as a result of the
        // code exchange.
        String serverClientId = "931700652688-vlqjc9s8klmjeti70p52ssnj4orgsdel.apps.googleusercontent.com";//getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(serverClientId, false)
                .build();

        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        //SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        //signInButton.setSize(SignInButton.SIZE_STANDARD);
        //signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]

        //findViewById(R.id.sign_in_button).setOnClickListener(this);
        //findViewById(R.id.sign_out_button).setOnClickListener(this);
        //findViewById(R.id.disconnect_button).setOnClickListener(this);

        //notificationFragment = new NotificationFragment();
        //notificationFragment.setContext(this);
        panelFragment = new PanelFragment();
        programFragment = new ProgramFragment();
        programListFragment = new ProgramListFragment();
        splashFragment = new SplashScreenFragment();
        settingsFragment = new SettingsFragment();
        settingsFragment.setSettings(mSettings);
        profileFragment = new ProfileFragment();
        spotMeteoListFragment = new SpotMeteoListFragment();

        //showSplashScreen();
        showFragment(R.id.nav_profile);
        int spotId = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) // se SpotID è valorizzato questa activity è chiamata dalla playalarm activity e deve visualizzare subito la spotdetailactivity
        {
            if (extras.getBoolean(GO_DIRECTLY_TO_SPOT_DETAILS) == true) {
                spotId = extras.getInt("spotId");
                //int alarmId = extras.getInt("alarmId");
                //panelFragment.showSpotDetail(spotId);
            }
        }

        silentSignIn(spotId);


    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE/*EXTRA_MESSAGE*/);

            // Waking up mobile if it is sleeping
            //WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            String title = intent.getExtras().getString("title");

            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            newMessage = today.format("%d.%m.%Y %H:%M:%S") + title + " - " + newMessage + "\n";


            //messageFragment.appendMessage(newMessage/* + "\n"*/);

            // Releasing wake lock
            //WakeLocker.release();
        }
    };

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

            isReceiverRegistered = true;
            String model = Build.MODEL;

            new registertask(this, new AsyncRegisterResponse() {

                @Override
                public void processFinish(String jsonStr, boolean error, String errorMessage) {

                    try {
                        JSONObject json = new JSONObject(jsonStr);
                        if (json.has("id")) {
                            int deviceId = json.getInt("id");
                            setDeviceId(deviceId);

                            spotList = new ArrayList<Spot>();
                            String str = json.getString("spotlist");
                            //JSONObject jObject = new JSONObject(str);
                            JSONArray jArray = new JSONArray(str);
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject jObject2 = jArray.getJSONObject(i);
                                Spot spt = new Spot(jObject2);


                                spotList.add(spt);
                            }
                            programListFragment.setServerSpotList(spotList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, registertask.POST_REGISTERDEVICE).execute(AlarmPreferences.getRegId(this), model);

        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Handling the touch event of app icon
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        // return super.onOptionsItemSelected(item);
        int id = item.getItemId();

        switch (item.getItemId()) {
        /*
         * Typically, an application registers automatically, so options below
		 * are disabled. Uncomment them if you want to manually register or
		 * unregister the device (you will also need to uncomment the equivalent
		 * options on options_menu.xml).
		 */
            case R.id.options_refresh:
                //requestWebduinoUpdate();
                return false;
            case R.id.options_diagfile:
                startSendLogActivity();
                return false;
            /*case R.id.action_settings:
                openSettings();
                return true;*/
            case R.id.action_add:

                programListFragment.createProgram();
                ;
                return true;
            case R.id.action_unregister:
                ServerUtilities.unregister(AlarmPreferences.getRegId(this), AlarmPreferences.getServerUrl(this));
                return true;
            /*case R.id.action_clear:
                deleteFile(messageFragment.messageFileName);
                //new File(messageFragment.messageFileName).delete();
                messageFragment.clearAll();
                return true;*/
            /*case R.id.options_refreshspotdetail:
                // D// Not implemented here, implemented in fragment
                return false;*/
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        /*menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_remove).setVisible(false);
        menu.findItem(R.id.action_clear).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.options_refresh).setVisible(false);*/

        /*if (mPosition == POSITION_PROGRAMLIST) {
            menu.findItem(R.id.action_add).setVisible(!drawerOpen);
        }
        if (mPosition == POSITION_LOG)
            menu.findItem(R.id.action_clear).setVisible(!drawerOpen);
*/
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        showFragment(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSplashScreen() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, splashFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void showFragment(int mPosition) {

        //Bundle data = new Bundle();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        refreshFab.setVisibility(View.GONE);
        addFab.setVisibility(View.GONE);


        if (!signedIn) {
            nextFragment = mPosition;
            ft.replace(R.id.content_frame, profileFragment);
            profileFragment.setProfile(mProfile);
        } else {

            if (mPosition == R.id.nav_favorites) {
                ft.replace(R.id.content_frame, panelFragment);
                refreshFab.setVisibility(View.VISIBLE);
            } else if (mPosition == R.id.nav_program) {
                ft.replace(R.id.content_frame, programListFragment);
                addFab.setVisibility(View.VISIBLE);
            } else if (mPosition == R.id.nav_meteostation) {
                ft.replace(R.id.content_frame, spotMeteoListFragment);
            } else if (mPosition == R.id.nav_settings) {
                ft.replace(R.id.content_frame, settingsFragment);
            } else if (mPosition == R.id.nav_profile) {
                ft.replace(R.id.content_frame, profileFragment);
                profileFragment.setProfile(mProfile);
            }
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    String getServerURL() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String syncConnPref = sharedPref.getString(QuickstartPreferences.KEY_PREF_SERVERURL, this.getResources().getString(R.string.pref_serverURL_default));
        return syncConnPref;
    }

    /*
    private void getSpotListFromServer() {

        new requestMeteoDataTask(this, new AsyncRequestMeteoDataResponse() {

            @Override
            public void processFinish(List<Object> list, boolean error, String errorMessage) {
            }

            @Override
            public void processFinishHistory(List<Object> list, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishSpotList(List<Object> list, boolean error, String errorMessage) {

                if (error) {
                    showError(errorMessage);
                    return;
                }

                spotList = new ArrayList<Spot>();
                for (int i = 0; i < list.size(); i++) {
                    spotList.add((Spot) list.get(i));
                }


                //settingsFragment.setServerSpotList(spotList);
                programListFragment.setServerSpotList(spotList);
                //spotMeteoListFragment.setSpotList(spotList);
            }
        },requestMeteoDataTask.REQUEST_SPOTLIST).execute();

    }
*/

    public void showError(String errorMessage) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Errore");
        alertDialogBuilder
                .setMessage(errorMessage)
                .

                        setCancelable(false);

        alertDialogBuilder
                .setNegativeButton("Ok", new DialogInterface.OnClickListener()

                        {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        }

                );
        AlertDialog alertDialog;
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static String getSpotName(long id) {

        if (spotList == null)
            return null;

        for (int i = 0; i < spotList.size(); i++) {
            if (spotList.get(i).id == id)
                return spotList.get(i).name;
        }
        return null;
    }

    public static Spot getSpotFromId(long id) {

        if (spotList == null)
            return null;

        for (int i = 0; i < spotList.size(); i++) {
            if (spotList.get(i).id == id)
                return spotList.get(i);
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;*/
            // ...
        }
    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        mProfile = null;
                        showNoUser();
                        signedIn = false;
                    }
                });
    }

    private void silentSignIn(final int spotId) {
        OptionalPendingResult<GoogleSignInResult> pendingResult =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            // There's immediate result available.
            //updateButtonsAndStatusFromSignInResult(pendingResult.get());
        } else {
            // There's no immediate result ready, displays some progress indicator and waits for the
            // async callback.
            //showProgressIndicator();
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    //updateButtonsAndStatusFromSignInResult(result);
                    //hideProgressIndicator();

                    handleSignInResult(result);

                    if (signedIn) {
                        showFragment(R.id.nav_favorites);
                        if (spotId != 0)
                            panelFragment.showSpotDetail(spotId);
                    } else {
                        showFragment(R.id.nav_profile);
                    }
                }
            });
        }
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        showNoUser();
                    }
                });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            signedIn = true;

            // Signed in successfully, show authenticated UI.
            /*GoogleSignInAccount*/
            acct = result.getSignInAccount();

            new LoadImagefromUrl().execute(/*mUserImageImageView, acct.getPhotoUrl().toString(),mProfile.userImage*/);
            //GoogleSignInAccount acct = result.getSignInAccount();
            String authCode = acct.getServerAuthCode();
            //mAuthCodeTextView.setText("Auth Code: " + authCode);
            // TODO(user): send code to server and exchange for access/refresh/ID tokens.

            //ServerUtilities.sendAuthCode(authCode,getServerURL());
            /*if (profileFragment != null) {
                profileFragment.setProfile(mProfile);
            }*/

            if (nextFragment != -1) {
                showFragment(nextFragment);
                nextFragment = -1;
            }

        } else {
            signedIn = false;
            mProfile = null;
            showNoUser();
            if (profileFragment != null) {
                profileFragment.setProfile(mProfile);
            }


        }
    }

    private void showNoUser() {
        mUserNameTextView.setText("no user");
        memailTextView.setText("no email");
        mUserImageImageView.setImageResource(R.drawable.user_white);

        if (profileFragment != null) {
            profileFragment.setProfile(null);

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSignInClick() {
        signIn();

    }

    @Override
    public void onSignOutClick() {
        signOut();
    }

    @Override
    public void onDisconnectClick() {
        revokeAccess();
    }

    private class LoadImagefromUrl extends AsyncTask<Object, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Object... params) {

            if(acct.getPhotoUrl() != null) {

                String url = acct.getPhotoUrl().toString();
                return loadBitmapFromUrl(url);
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap resultBitmap) {
            super.onPostExecute(resultBitmap);

            mProfile = new UserProfile();
            mProfile.userName = acct.getDisplayName();
            mProfile.email = acct.getEmail();
            mProfile.personId = acct.getId();
            mUserNameTextView.setText(mProfile.userName);
            memailTextView.setText(mProfile.email);

            if (resultBitmap != null) {
                int currentBitmapWidth = resultBitmap.getWidth();
                int currentBitmapHeight = resultBitmap.getHeight();
                int ivWidth = mUserImageImageView.getWidth();
                int ivHeight = mUserImageImageView.getHeight();
                int newWidth = ivWidth;
                int newHeight = (int) Math.floor((double) currentBitmapHeight * ((double) newWidth / (double) currentBitmapWidth));
                Bitmap newbitMap = Bitmap.createScaledBitmap(resultBitmap, newWidth, newHeight, true);

                mUserImageImageView.setImageBitmap(getCircleBitmap(newbitMap));
                mProfile.userImage = ((BitmapDrawable) mUserImageImageView.getDrawable()).getBitmap();
            }

            if (profileFragment != null)
                profileFragment.setProfile(mProfile);
        }
    }

    public Bitmap loadBitmapFromUrl(String url) {
        URL newurl = null;
        Bitmap bitmap = null;
        try {
            newurl = new URL(url);
            bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap circuleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(circuleBitmap);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return circuleBitmap;
    }

    /*public static String getIMEI() {
        TelephonyManager mngr = (TelephonyManager) MainActivity.getContext().getSystemService(MainActivity.getContext().TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }*/
    public static int getDeviceId() {

        return deviceId;
    }

    public static void setDeviceId(int id) {
        deviceId = id;
    }

}
