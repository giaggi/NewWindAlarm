package wind.newwindalarm;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.text.format.Time;
import android.view.Display;
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
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import wind.newwindalarm.fragment.PanelFragment;
import wind.newwindalarm.fragment.ProfileFragment;
import wind.newwindalarm.fragment.ProgramFragment;
import wind.newwindalarm.fragment.ProgramListFragment;
import wind.newwindalarm.fragment.SearchSpotFragment;
import wind.newwindalarm.fragment.SpotDetailsFragment;
import wind.newwindalarm.fragment.SpotMeteoListFragment;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.OnSignInClickListener,
        PanelFragment.OnSpotClickListener,
        SpotDetailsFragment.OnClickListener,
        ProgramListFragment.OnProgramListListener, DownloadImageTask.AsyncDownloadImageResponse {

    private List<MeteoStationData> meteoDataList = new ArrayList<>();

    CountDownTimer countDownTimer;
    ProgressBar progressBar;
    private long spotId;

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private TextView mInformationTextView;
    private static final String TAG = "SignInActivity";
    //public List<Spot> spotList;
    private SpotList mSpotList = new SpotList();
    private Settings mSettings;

    PanelFragment panelFragment;
    ProgramFragment programFragment;
    ProgramListFragment programListFragment;
    SettingsFragment settingsFragment;
    ProfileFragment profileFragment;
    SpotMeteoListFragment spotMeteoListFragment;
    SpotDetailsFragment spotDetailsFragment;
    SearchSpotFragment searchSpotFragment;
    SpotMeteoDataList spotMeteoDataList = new SpotMeteoDataList();

    // google properties
    TextView mUserNameTextView;
    TextView memailTextView;
    ImageView mUserImageImageView;

    FloatingActionButton fabButton;
    FloatingActionButton refreshFab;

    private UserProfile mProfile = null;
    static boolean signedIn = false;
    static int nextFragment = -1;

    public List<Spot> getFavorites() {
        return mSpotList.getSpotFavorites();
    }
    public void addToFavorites(long id) {
        mSpotList.addToFavorites(this,id,mProfile.personId);
    }
    public void removeFromFavorites(long id) {
        mSpotList.removeFromFavorites(this,id,mProfile.personId);
    }
    public Spot getSpotFromId(long id) {
        return mSpotList.getSpotFromId(id);
    }
    public String getSpotName(long id) {
        return mSpotList.getSpotName(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mProfile = (UserProfile) intent.getSerializableExtra("userProfile");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabButton = (FloatingActionButton) findViewById(R.id.addFab);
        /*fabButton.setImageResource(R.drawable.refreshbutton);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLastMeteoData();
                //programListFragment.createProgram();
            }
        });*/

        /*refreshFab = (FloatingActionButton) findViewById(R.id.fabButton);
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastMeteoData();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //getSupportActionBar().setElevation(0);

        //drawer.setScrimColor(Color.TRANSPARENT);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mUserNameTextView = (TextView) header.findViewById(R.id.UserNameTextView);
        memailTextView = (TextView) header.findViewById(R.id.UserEmailTextView);
        mUserImageImageView = (ImageView) header.findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mSettings = new Settings(this);
        mSettings.setListener(new Settings.SettingsListener() {
            @Override
            public void onChangeOrder(List<Long> order) {
                //panelFragment.setSpotOrder(order);
            }

            @Override
            public void onChangeList(List<Long> list) {

            }
        });

        // carica la spot list per la combo dei programmmi
        getSpotListFromServer();

        programFragment = new ProgramFragment();
        programListFragment = new ProgramListFragment();
        programListFragment.setListener(this);
        settingsFragment = new SettingsFragment();
        settingsFragment.setSettings(mSettings);
        profileFragment = new ProfileFragment();
        spotMeteoListFragment = new SpotMeteoListFragment();
        //searchSpotFragment = new SearchSpotFragment();

        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        init();
    }

    private void init() {

        signedIn = true;
        mInformationTextView.setText("Loading profile");
        new LoadImagefromUrl().execute();

        mInformationTextView.setText("Loading meteodata");
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // TODO ma questo a cosa serve?????
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE/*EXTRA_MESSAGE*/);
            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
            String title = intent.getExtras().getString("title");
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
        }
    };

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
		 * options on options_menu.xml).*/

            case R.id.options_diagfile:
                startSendLogActivity();
                return true;
            case R.id.options_searchspot:

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

                ft.replace(R.id.content_frame, searchSpotFragment);
                ft.addToBackStack(null);
                ft.commit();
                return true;

            /*case R.id.action_settings:
                openSettings();
                return true;*/
            case R.id.action_add:

                //programListFragment.createProgram();
                ;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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

        showFragment(id, true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(int mPosition, boolean addToBackStack) {

        try {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

            if (!signedIn && mPosition != R.id.nav_settings) {
                nextFragment = mPosition;
                ft.replace(R.id.content_frame, profileFragment);
                profileFragment.setProfile(mProfile);
            } else {

                if (mPosition == R.id.nav_favorites && panelFragment != null) {
                    ft.replace(R.id.content_frame, panelFragment);
                    enableRefreshButton();
                } else if (mPosition == R.id.nav_program) {
                    ft.replace(R.id.content_frame, programListFragment);
                    enableAddProgramButton();
                } else if (mPosition == R.id.nav_meteostation) {
                    disableAllButton();
                    ft.replace(R.id.content_frame, spotMeteoListFragment);
                } else if (mPosition == R.id.nav_settings) {
                    disableAllButton();
                    ft.replace(R.id.content_frame, settingsFragment);
                } else if (mPosition == R.id.nav_profile) {
                    disableAllButton();
                    ft.replace(R.id.content_frame, profileFragment);
                    profileFragment.setProfile(mProfile);
                }
            }
            if (addToBackStack)
                ft.addToBackStack(null);
            ft.commit();
        } catch (IllegalStateException ignored) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
        }
    }

    private void getSpotListFromServer() {

        new requestMeteoDataTask(this, new AsyncRequestMeteoDataResponse() {

            @Override
            public void processFinish(List<MeteoStationData> list, boolean error, String errorMessage) {
            }

            @Override
            public void processFinishHistory(long spotId, List<MeteoStationData> list, boolean error, String errorMessage) {
            }

            @Override
            public void processFinishSpotList(List<Spot> list, List<Long> favorites, boolean error, String errorMessage) {

                if (error) {
                    showError(errorMessage);
                    return;
                }

                for (Spot spot : list) {
                    for (Long id : favorites) {
                        if (id == spot.id) {
                            spot.favorites = true;
                            break;
                        }
                    }
                    mSpotList.add(spot);
                }



                //programListFragment.setServerSpotList(mSpotList.getSpotList());

                if (panelFragment == null) {
                    panelFragment = new PanelFragment();
                    showFragment(R.id.nav_favorites, false);
                }

                searchSpotFragment = new SearchSpotFragment();
                searchSpotFragment.setSpotList(mSpotList);
                //panelFragment.refreshMeteoData();
                getLastMeteoData();
            }

            @Override
            public void processFinishAddFavorite(long spotId, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishRemoveFavorite(long spotId, boolean error, String errorMessage) {

            }


        }, requestMeteoDataTask.REQUEST_SPOTLIST).execute(mProfile.personId);
    }

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

    private void unregister() {
        AlarmPreferences.deleteRegId(this);
        AlarmPreferences.deletePersonId(this);
    }

    @Override
    public void onSpotClick(long spotId) {

        this.spotId = spotId;
        spotDetailsFragment = new SpotDetailsFragment();
        spotDetailsFragment.setListener(this);
        MeteoStationData md = spotMeteoDataList.getLastMeteoData(spotId);
        spotDetailsFragment.setMeteoData(spotMeteoDataList.getLastMeteoData(spotId));

        spotDetailsFragment.setSpotId(spotId);
        try {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(R.id.content_frame, spotDetailsFragment);
            ft.addToBackStack(null);
            ft.commit();
        } catch (IllegalStateException ignored) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
        }

        getHistoryMeteoData(spotId);

        getWebCamImages(spotId,md);

    }

    @Override
    public void onRefreshPanelRequest() {
        getLastMeteoData();
    }

    public void getLastMeteoData() {

        startProgressBar();

        long windId = spotMeteoDataList.getLastWindId();
        new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_FAVORITESLASTMETEODATA).execute(mProfile.personId, windId);
    }

    private void startProgressBar() {
        progressBar.setProgress(10);
        progressBar.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(50000, 1000) {

            private int progress = 10;

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                progress += 10;
                progressBar.setProgress(progress);
            }

            public void onFinish() {
                //mTextField.setText("done!");
            }
        }.start();
    }

    public void getHistoryMeteoData(long spotId) {

        long lastWindId = spotMeteoDataList.getLastHistoryId(spotId);

        Date end = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.HOUR_OF_DAY, -10); //minus number would decrement the hours
        Date start = cal.getTime();

        startProgressBar();

        // richiedi dati storici a partire da lastWindId e vai in append
        new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_LOGMETEODATA).execute(spotId, mProfile.personId, start, end, lastWindId);
    }

    public void getWebCamImages(long spotId, MeteoStationData md) {

        long lastWindId = spotMeteoDataList.getWebCamWindId(spotId,1);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        //int height_one = size.y;

        int count = 1;
        for (String webcamurl : md.webcamurlList) {
            if (lastWindId < md.id) {
                spotDetailsFragment.showWebCamProgressBar(count);
                new DownloadImageTask(this, count, md.id, width).execute(webcamurl);

            } else {
                spotDetailsFragment.setWebCamImage(count, spotMeteoDataList.getWebCamImage(spotId, count));
            }
            count++;
        }
    }

    @Override
    public void onSignInClick() {

    }

    @Override
    public void onSignOutClick() {
        unregister();
        setResult(SplashActivity.RESULT_SIGN_OUT);
        finish();
    }

    @Override
    public void onDisconnectClick() {
        unregister();
        setResult(SplashActivity.RESULT_DISCONNECT);
        finish();
    }

    @Override
    public void onRefreshDetailViewRequest(int position) {
        getLastMeteoData();
    }

    @Override
    public void onChangeDetailView(int page) {

        switch (page) {
            case SpotDetailsFragment.Pager_ChartPage:
                enableRefreshButton();
                break;
            case SpotDetailsFragment.Pager_MeteodataPage:
                enableRefreshButton();
                break;
            case SpotDetailsFragment.Pager_ProgramListPage:
                enableAddProgramButton();
                break;
            case SpotDetailsFragment.Pager_WebcamPage:
                enableRefreshButton();
                break;
            default:
                break;
        }
    }

    private void enableRefreshButton() {
        fabButton.setImageResource(R.drawable.refreshbutton);
        fabButton.setVisibility(View.VISIBLE);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLastMeteoData();
            }
        });
    }

    private void enableAddProgramButton() {

        fabButton.setImageResource(R.drawable.add);
        fabButton.setVisibility(View.VISIBLE);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //programListFragment.createProgram();
                WindAlarmProgram program = new WindAlarmProgram();
                program.spotId = spotId;
                /*if (alarmList.size() == 0)
                    program.id = 1L;
                else
                    program.id = alarmList.get(alarmList.size() - 1).alarm.id + 1;*/
                startProgramActivity(program, ProgramActivity.CREATEPROGRAM_REQUEST);
            }
        });
    }

    private void disableAllButton() {

        fabButton.setVisibility(View.GONE);
    }

    @Override
    public void onEditProgram(WindAlarmProgram program) {

        startProgramActivity(program, ProgramActivity.EDITPROGRAM_REQUEST);
    }

    private void startProgramActivity(WindAlarmProgram alarm, int request) {
        //Intent resultIntent = new Intent(getActivity(), ProgramActivity.class);
        Intent resultIntent = new Intent(SplashActivity.getContext(), ProgramActivity.class);
        resultIntent.putExtra("WindAlarmProgram", new Gson().toJson(alarm));

        //resultIntent.putExtra("spotid",spotId);

        /*Gson gson = new Gson();
        MainActivity a = (MainActivity) getActivity();
        SpotList sl = a.getServerSpotList();
        String myJson = gson.toJson(sl);
        resultIntent.putExtra("spotlist", myJson);*/
        //resultIntent.putExtra("serverurl", (AlarmPreferences.getServerUrl(getActivity())));


        startActivityForResult(resultIntent, request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == ProgramActivity.REQUESTRESULT_ERROR) {
            // non faccio niemnte
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle("Errore");
            alertDialogBuilder
                    .setMessage("Impossibile salvare")
                    .setCancelable(false);
            alertDialogBuilder
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog;
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return;
        }
        if (resultCode == ProgramActivity.REQUESTRESULT_ABORT) {
            // non faccio niemnte
            return;
        }

        String jsonMyObject;
        jsonMyObject = data.getStringExtra("WindAlarmProgram");
        WindAlarmProgram program = new Gson().fromJson(jsonMyObject, WindAlarmProgram.class);
        if (requestCode == ProgramActivity.CREATEPROGRAM_REQUEST) {
            if (resultCode == ProgramActivity.REQUESTRESULT_SAVED) {
                //programListFragment.addProgram(program);
                //programListFragment.
                //programListFragment = new ProgramListFragment();
            } else if (resultCode == ProgramActivity.REQUESTRESULT_DELETED) {
                // non faccio niemnte
            }
        } else if (requestCode == ProgramActivity.EDITPROGRAM_REQUEST) {
            if (resultCode == ProgramActivity.REQUESTRESULT_SAVED) {
                /*AlarmCardItem card = getCardFromId(program.id);
                card.update(program);*/
                //programListFragment.updateProgram(program);
            } else if (resultCode == ProgramActivity.REQUESTRESULT_DELETED) {
                /*AlarmCardItem card = getCardFromId(program.id);
                alarmList.remove(card);
                card.remove();*/
                //programListFragment.deleteProgram(program);
            }
        }

    }

    @Override
    public void processFinishDownloadImage(int index, Bitmap bmp, long windId) {
        if (bmp != null) {
            spotMeteoDataList.setWebCamImage(spotId,index,bmp,windId);
            //spotDetailsFragment.setWebCamImage(index, bmp);
            spotDetailsFragment.setWebCamImage(index, bmp); /// TODO qui c'è un errore. lo spot del fragment nel frattempo potrebbe cambiare
        }
    }


    private class requestDataResponse implements AsyncRequestMeteoDataResponse {

        @Override
        public void processFinish(List<MeteoStationData> list, boolean error, String errorMessage) {


            if (list == null)
                return;
            //meteoDataList = list;
            for (MeteoStationData md : list)
                spotMeteoDataList.setLastMeteoData(md.spotID,md);
            panelFragment.setMeteoDataList(list);
            panelFragment.refreshMeteoData();

            //countDownTimer.cancel();
            progressBar.setVisibility(View.GONE);
            mInformationTextView.setVisibility(View.GONE);
        }

        @Override
        public void processFinishHistory(long spotId, List<MeteoStationData> list, boolean error, String errorMessage) {

            if (spotDetailsFragment != null) {
                spotMeteoDataList.setHistory(spotId,list);
                spotDetailsFragment.setHistoryMeteoData(spotMeteoDataList.getHistory(spotId));
            }

            progressBar.setVisibility(View.GONE);
            mInformationTextView.setVisibility(View.GONE);
        }

        @Override
        public void processFinishSpotList(List<Spot> list, List<Long> favorites, boolean error, String errorMessage) {

            progressBar.setVisibility(View.GONE);
            mInformationTextView.setVisibility(View.GONE);

        }

        @Override
        public void processFinishAddFavorite(long spotId, boolean error, String errorMessage) {

        }

        @Override
        public void processFinishRemoveFavorite(long spotId, boolean error, String errorMessage) {

        }

    }

    private class LoadImagefromUrl extends AsyncTask<Object, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Object... params) {

            if (mProfile.photoUrl != null) {

                String url = mProfile.photoUrl.toString();
                return loadBitmapFromUrl(url);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap resultBitmap) {
            super.onPostExecute(resultBitmap);

            //mProfile = new UserProfile();
            /*mProfile.userName = mProfile.userName;
            mProfile.email = mProfile.email;
            mProfile.personId = acct.getId();*/
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

    private class SpotMeteoData {
        private MeteoStationData md;
        private List<MeteoStationData> history = new ArrayList<MeteoStationData>();
        private Bitmap[] webcamBitmap = new Bitmap[3];
        private long[] webcamWindId = new long[3];
    }
    private class SpotMeteoDataList {
        List<SpotMeteoData> lastMeteoData = new ArrayList<SpotMeteoData>();

        private SpotMeteoData getFromId(long spotId) {
            if (lastMeteoData == null) return null;
            for (SpotMeteoData smd : lastMeteoData) {
                if (smd.md.spotID == spotId)
                    return smd;
            }
            return null;
        }
        public MeteoStationData getLastMeteoData(long spotId) {
            return getFromId(spotId).md;
        }
        public void setLastMeteoData(long spotId, MeteoStationData md) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null) {
                smd = new SpotMeteoData();
                lastMeteoData.add(smd);
            }
            smd.md = md;
        }
        public List<MeteoStationData> getHistory(long spotId) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null || smd.history == null || smd.history.size() == 0)
                return null;
            return getFromId(spotId).history;
        }
        public long getLastHistoryId(long spotId) {
            List<MeteoStationData> list = getHistory(spotId);
            if (list == null) return -1;
            return list.get(list.size()-1).id;
        }
        public void setHistory(long spotId, List<MeteoStationData> history) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null) {
                smd = new SpotMeteoData();
            }

            for (MeteoStationData md : history) {
                smd.history.add(md);
            }
        }
        private long getLastWindId() {

            long windId = 0;
            for (SpotMeteoData smd : lastMeteoData) {
                if (smd.md.id > windId)
                    windId = smd.md.id;
            }
            return windId;
        }
        public Bitmap getWebCamImage(long spotId, int index) {

            SpotMeteoData smd = getFromId(spotId);
            if (index < 1 || index > smd.webcamBitmap.length)
                return null;
            return smd.webcamBitmap[index-1];
        }
        public long getWebCamWindId(long spotId, int index) {

            SpotMeteoData smd = getFromId(spotId);
            if (index < 1 || index > smd.webcamBitmap.length)
                return -1;
            return smd.webcamWindId[index-1];
        }
        public void setWebCamImage(long spotId, int index, Bitmap bmp, long windId) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null) {
                smd = new SpotMeteoData();
            }
            if (index < 1 || index > smd.webcamBitmap.length)
                return;
            smd.webcamBitmap[index-1] = bmp;
            smd.webcamWindId[index-1] = windId;
        }
    }
}
