package gwind.windalarm.fragment;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.squareup.picasso.Picasso;

import gwind.windalarm.AlarmPreferences;
import gwind.windalarm.BuildConfig;
import gwind.windalarm.MainActivity;
import gwind.windalarm.R;
import gwind.windalarm.SplashActivity;
import gwind.windalarm.UserProfile;

public class ProfileFragment extends Fragment {

    OnSignInClickListener mCallback;

    private TextView mUserNameTextView;
    private TextView mUserIdTextView;
    private TextView mEMailTextView;
    private ImageView mUserImageView;
    private TextView mRegIdTextView;
    private SignInButton mSignonButton;
    private Button mSignoutButton;
    private Button mDisconnectButton;

    private UserProfile mProfile;
    protected boolean initialized = false;

    // Container Activity must implement this interface
    public interface OnSignInClickListener {
        public void onSignInClick();
        public void onSignOutClick();
        public void onDisconnectClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSignInClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnSignInClickListener");
        }
    }

    @SuppressWarnings("deprecation")
    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            try {
                mCallback = (OnSignInClickListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnSignInClickListener");
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v;
        v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Updating the action bar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profilo");

        mSignonButton = (com.google.android.gms.common.SignInButton) v.findViewById(R.id.sign_in_button);
        mSignonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSignInClick();
            }
        });

        mSignoutButton = (Button) v.findViewById(R.id.sign_out_button);
        mSignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSignOutClick();
            }
        });

        mDisconnectButton = (Button) v.findViewById(R.id.disconnect_button);
        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onDisconnectClick();
            }
        });

        mRegIdTextView = (TextView) v.findViewById(R.id.regIdTextView);
        mRegIdTextView.setText(AlarmPreferences.getRegId(this.getActivity()));

        mUserNameTextView = (TextView) v.findViewById(R.id.userTextView);
        mEMailTextView = (TextView) v.findViewById(R.id.EmailTextView);
        mUserIdTextView = (TextView) v.findViewById(R.id.userIdTextView);
        mUserImageView = (ImageView) v.findViewById(R.id.userImageView);

        initialized = true;

        showUserProfile();

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        TextView tv =  (TextView) v.findViewById(R.id.versionCodeTextView);
        tv.setText("Build "+versionCode);
        tv =  (TextView) v.findViewById(R.id.versionNameTextView);
        tv.setText("Versione "+versionName);
        tv =  (TextView) v.findViewById(R.id.deviceTextView);
        tv.setText("Device "+AlarmPreferences.getDeviceId(SplashActivity.getContext()));


        return v;
    }
    public void setProfile(UserProfile profile) {
        this.mProfile = profile;
        showUserProfile();
    }

    private void showUserProfile() {

        if(!initialized) return;

        if (mProfile != null) {
            mUserNameTextView.setText(mProfile.userName);
            mUserNameTextView.setVisibility(View.VISIBLE);
            //mUserIdTextView.setText(mProfile.personId);
            mUserIdTextView.setVisibility(View.VISIBLE);
            mEMailTextView.setText(mProfile.email);
            mEMailTextView.setVisibility(View.VISIBLE);

            mUserImageView.setVisibility(View.VISIBLE);
            MainActivity ma = (MainActivity) getActivity();
            //Bitmap bitmap = ma.loadBitmapFromUrl(mProfile.photoUrl);
            //mUserImageView.setImageBitmap(bitmap/*mProfile.userImage*/);
            Picasso.with(getContext()).load(mProfile.photoUrl).into(mUserImageView);

            //mRegIdTextView.setVisibility(View.VISIBLE);
            mSignonButton.setVisibility(View.GONE);
            mSignoutButton.setVisibility(View.VISIBLE);
            mDisconnectButton.setVisibility(View.VISIBLE);
        } else {
            mUserNameTextView.setText("No user");
            mUserNameTextView.setVisibility(View.GONE);
            mUserIdTextView.setText("");
            mUserIdTextView.setVisibility(View.GONE);
            mEMailTextView.setText("");
            mEMailTextView.setVisibility(View.GONE);
            mUserImageView.setImageResource(R.drawable.user_white);
            //mUserImageView.setVisibility(View.GONE);
            mRegIdTextView.setVisibility(View.VISIBLE);
            mSignonButton.setVisibility(View.VISIBLE);
            mSignoutButton.setVisibility(View.GONE);
            mDisconnectButton.setVisibility(View.GONE);
        }
    }
}