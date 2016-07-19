package wind.newwindalarm;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;

import wind.newwindalarm.controls.DatePickerDialogFragment;
import wind.newwindalarm.controls.SpeedDialogFragment;
import wind.newwindalarm.controls.TemperatureDialogFragment;
import wind.newwindalarm.controls.TimePickerDialogFragment;

public class ProgramActivity extends AppCompatActivity {

    protected ProgramFragment programFragment;
    protected String mServerUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        programFragment = new ProgramFragment();

        Bundle data2 = new Bundle();
        data2.putInt("position", 1);
        programFragment.setArguments(data2);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, programFragment)
                .commit();

        String jsonMyObject;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("WindAlarmProgram");
            WindAlarmProgram program = new Gson().fromJson(jsonMyObject, WindAlarmProgram.class);

            Gson gson = new Gson();
            SpotList list = gson.fromJson(getIntent().getStringExtra("spotlist"), SpotList.class);
            programFragment.setServerSpotList(list.list);

            programFragment.setWebduinoPrograms(program);
            //getActionBar().setTitle("Programma " + program.id);
            getSupportActionBar().setTitle("Programma " + program.id);


            mServerUrl = getIntent().getStringExtra("serverurl");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.program, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save:
                postProgram(false);
                return true;
            case R.id.action_delete:
                postProgram(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void postProgram(final boolean deletekey) {

        final int postType;
        if (deletekey)
            postType = postprogramtask.POST_DELETEALARM;
        else
            postType = postprogramtask.POST_ALARM;

        WindAlarmProgram program = programFragment.saveProgram();
        if (program == null) // c'e stato un errore nel salvataggio
            return;
        postprogramtask task = (postprogramtask) new postprogramtask(this, new AsyncPostProgramResponse() {

            @Override
            public void processFinish(Object obj, boolean error, String errorMessage) {

                WindAlarmProgram program = (WindAlarmProgram) obj;
                if (error) {

                    setResult(ProgramListFragment.REQUESTRESULT_ERROR, null);


                } else {

                    Intent output = new Intent();
                    output.putExtra("WindAlarmProgram", new Gson().toJson(program));

                    if (postType == postprogramtask.POST_DELETEALARM)
                        setResult(ProgramListFragment.REQUESTRESULT_DELETED, output);
                    else if  (postType == postprogramtask.POST_ALARM)
                        setResult(ProgramListFragment.REQUESTRESULT_SAVED, output);

                }
                finish();
            }
        }, postType).execute(program,mServerUrl);
    }

    public void onBackPressed() {

        setResult(ProgramListFragment.REQUESTRESULT_ABORT, null);
        finish();
        // super.onBackPressed();
        // myFragment.onBackPressed();
    }

    public void showDatePickerDialog(int mDay, int mMonth, int mYear, String message, Handler mHandler) {

        Bundle b = new Bundle();
        b.putInt("set_day", mDay);
        b.putInt("set_month", mMonth);
        b.putInt("set_year", mYear);
        b.putString("set_message", message);

        DatePickerDialogFragment datePicker = new DatePickerDialogFragment(
                mHandler);

        datePicker.setArguments(b);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(datePicker, message);
        ft.commit();
    }

    public void showTimePickerDialog(int mHour, int mMinute, int mBand, String message, Handler mHandler) {

        Bundle b = new Bundle();
        b.putInt("set_hour", mHour);
        b.putInt("set_minute", mMinute);
        b.putInt("set_band", mBand);
        b.putString("set_message", message);

        TimePickerDialogFragment timePicker = new TimePickerDialogFragment(
                mHandler);
        timePicker.setArguments(b);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(timePicker, message/*"time_picker"*/);
        ft.commit();
    }

    public void showSpeedPickerDialog(double speed, String message, Handler mHandler) {

        Bundle b = new Bundle();
        b.putDouble("set_speed", speed);
        b.putString("set_message", message);
        SpeedDialogFragment numberPicker = new SpeedDialogFragment(mHandler);
        numberPicker.setArguments(b);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(numberPicker, message);
        ft.commit();
    }
}