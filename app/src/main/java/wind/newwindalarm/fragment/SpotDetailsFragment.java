package wind.newwindalarm.fragment;


//import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import wind.newwindalarm.MainActivity;
import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;
import wind.newwindalarm.ScreenSlidePageFragment;

public class SpotDetailsFragment extends Fragment implements SpotDetailsMeteodataFragment.OnClickListener {

    public static final int MeteodataPage = 0;
    public static final int WebcamPage = 1;
    public static final int ChartPage = 2;
    public static final int ProgramListPage = 3;

    private MeteoStationData meteoData;
    private SpotDetailsMeteodataFragment meteodataFragment;
    private SpotDetailsWebcamFragment webcamFragment;
    private SpotDetailsChartFragment chartFragment;
    private ProgramListFragment programListFragment;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private long spotId;

    OnClickListener mCallback;
    // Container Activity must implement this interface
    public interface OnClickListener {
        void onRefreshDetailViewRequest(int position);
        void onChangeDetailView(int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_spotdetail, container, false);
        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tablayout);
        mPager = (ViewPager) v.findViewById(R.id.pager);

        mPagerAdapter = new SpotDetailPagerAdapter(((MainActivity)getActivity()).getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            // optional
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            // optional
            @Override
            public void onPageSelected(int position) {

            }

            // optional
            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        tabLayout.setupWithViewPager(mPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.logo);
        tabLayout.getTabAt(1).setIcon(R.drawable.webcamicon);
        tabLayout.getTabAt(2).setIcon(R.drawable.graphicon);
        tabLayout.getTabAt(3).setIcon(R.drawable.graphicon);

        // Updating the action bar title
        if (meteoData != null) {
            String txt = ((MainActivity)getActivity()).getSpotName(meteoData.spotID);
            if (txt != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(txt);
            }
        }
        return v;
    }

    public void setListener(OnClickListener listener) {
        mCallback = listener;
    }

    public SpotDetailsFragment() {
        meteodataFragment = new SpotDetailsMeteodataFragment();
        meteodataFragment.setListener(this);
        webcamFragment = new SpotDetailsWebcamFragment();
        chartFragment = new SpotDetailsChartFragment();
        programListFragment = new ProgramListFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setMeteoData(MeteoStationData data) {
        meteoData = data;

        /*chartFragment.setMeteoData(data);
        webcamFragment.setMeteoData(data);*/
        meteodataFragment.setMeteoData(data);
    }

    public void setSpotId(long spotId) {
        this.spotId = spotId;
    }

    public void setHistoryMeteoData(List<MeteoStationData> meteoDataList) {

        chartFragment.setHistoryMeteoData(meteoDataList);
    }

    public MeteoStationData getLastHistoryMeteoData() {
        return chartFragment.getLastHistoryMeteoData();
    }

    @Override
    public void onRefreshMeteoDataRequest() {

        int position = mPager.getCurrentItem();
        mCallback.onRefreshDetailViewRequest(position);
    }

    private class SpotDetailPagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 4;

        public SpotDetailPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            if (position == MeteodataPage) {

                meteodataFragment.setSpotId(spotId);
                meteodataFragment.setMeteoData(meteoData);
                meteodataFragment.refreshData();
                return  meteodataFragment;

            }  else if (position == WebcamPage) {

                webcamFragment.setSpotId(spotId);
                webcamFragment.setMeteoData(meteoData);
                webcamFragment.refreshData();
                return  webcamFragment;

            }  else if (position == ChartPage) {

                chartFragment.setSpotId(spotId);
                chartFragment.setMeteoData(meteoData);
                chartFragment.refreshData();
                return chartFragment;

            }  else if (position == ProgramListPage) {

                programListFragment.setSpotId(spotId);
                //programListFragment.setMeteoData(meteoData);
                //programListFragment.refreshData();
                return programListFragment;

            } else {
                return new ScreenSlidePageFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String titolo = "titolo " + position;
            if (position == 0 ) {
                return  "Dati meteo";
            }  else if (position == 1 ) {
                return  "Webcam";
            } else if (position == 2 ) {
                return  "Grafico";
            } else if (position == 3 ) {
                return  "Sveglie";
            }
            return titolo;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
