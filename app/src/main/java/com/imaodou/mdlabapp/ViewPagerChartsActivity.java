package com.imaodou.mdlabapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.imaodou.mdlabapp.db.MdLabDBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.BubbleChartView;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

public class ViewPagerChartsActivity extends AppCompatActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory. If this becomes too
     * memory intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_charts);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ImageView mLegend;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            Log.d("ViewPagerCharts", "newInstance: sectionNumber" + sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_view_pager_charts, container, false);
            RelativeLayout layout = (RelativeLayout) rootView;
            mLegend = (ImageView) rootView.findViewById(R.id.legend);
            int sectionNum = getArguments().getInt(ARG_SECTION_NUMBER);
            switch (sectionNum) {
                case 1:
                    mLegend.setVisibility(View.VISIBLE);
                    mLegend.setImageResource(R.mipmap.tempandhumidity);
                    LineChartView lineChartView = new LineChartView(getActivity());
                    lineChartView.setLineChartData(generateLineTemperatureData(sectionNum));
                    lineChartView.setZoomType(ZoomType.HORIZONTAL);
                    lineChartView.setViewportCalculationEnabled(false);
                    Viewport v = new Viewport(0, 100, 60, -10);
                    lineChartView.setMaximumViewport(v);
                    lineChartView.setCurrentViewport(v);
                    /** Note: Chart is within ViewPager so enable container scroll mode. **/
                    lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

                    layout.addView(lineChartView);
                    Log.d("ViewPagerCharts", "onCreateView: line1");
                    break;
                case 2:
                    mLegend.setVisibility(View.INVISIBLE);
                    LineChartView lineChartView2 = new LineChartView(getActivity());
                    lineChartView2.setLineChartData(generateLinePressureData(sectionNum));
                    lineChartView2.setZoomType(ZoomType.HORIZONTAL);
                    lineChartView2.setViewportCalculationEnabled(false);
                    Viewport v2 = new Viewport(0, 200, 60, 0);
                    lineChartView2.setMaximumViewport(v2);
                    lineChartView2.setCurrentViewport(v2);
                    /** Note: Chart is within ViewPager so enable container scroll mode. **/
                    lineChartView2.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

                    layout.addView(lineChartView2);
                    Log.d("ViewPagerCharts", "onCreateView: line2");
                    break;
                case 3:
                    mLegend.setVisibility(View.INVISIBLE);
                    LineChartView lineChartView3 = new LineChartView(getActivity());
                    lineChartView3.setLineChartData(generateLinePressureData(sectionNum));
                    lineChartView3.setZoomType(ZoomType.HORIZONTAL);
                    lineChartView3.setViewportCalculationEnabled(false);
                    Viewport v3 = new Viewport(0, 700, 60, 0);
                    lineChartView3.setMaximumViewport(v3);
                    lineChartView3.setCurrentViewport(v3);
                    /** Note: Chart is within ViewPager so enable container scroll mode. **/
                    lineChartView3.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

                    layout.addView(lineChartView3);
                    Log.d("ViewPagerCharts", "onCreateView: line3");
                    break;
                case 4:
                    mLegend.setVisibility(View.VISIBLE);
                    mLegend.setImageResource(R.mipmap.fengsuandsun);
                    LineChartView lineChartView1 = new LineChartView(getActivity());
                    lineChartView1.setLineChartData(generateLineTemperatureData(sectionNum));
                    lineChartView1.setZoomType(ZoomType.HORIZONTAL);
                    lineChartView1.setViewportCalculationEnabled(false);
                    Viewport v1 = new Viewport(0, 200, 60, 0);
                    lineChartView1.setMaximumViewport(v1);
                    lineChartView1.setCurrentViewport(v1);
                    /** Note: Chart is within ViewPager so enable container scroll mode. **/
                    lineChartView1.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

                    layout.addView(lineChartView1);
                    Log.d("ViewPagerCharts", "onCreateView: line4");
                    break;
                case 5:
                    mLegend.setVisibility(View.INVISIBLE);
                    LineChartView lineChartView5 = new LineChartView(getActivity());
                    lineChartView5.setLineChartData(generateLinePressureData(sectionNum));
                    lineChartView5.setZoomType(ZoomType.HORIZONTAL);
                    lineChartView5.setViewportCalculationEnabled(false);
                    Viewport v5 = new Viewport(0, 200, 60, 0);
                    lineChartView5.setMaximumViewport(v5);
                    lineChartView5.setCurrentViewport(v5);
                    /** Note: Chart is within ViewPager so enable container scroll mode. **/
                    lineChartView5.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

                    layout.addView(lineChartView5);
                    Log.d("ViewPagerCharts", "onCreateView: line5");
                    break;
            }

            return rootView;
        }

        private LineChartData generateLineTemperatureData(int dataType) {

            List<PointValue> values = new ArrayList<PointValue>();
            List<PointValue> valuesH = new ArrayList<PointValue>();
            String startTime = "2017-02-10 15:00:00";
            String endTime = "2017-02-10 16:00:00";
            String column1, column2;
            switch (dataType) {
                case 1:
                    column1 = "temperature";
                    column2 = "humidity";
                    break;
                case 4:
                    column1 = "windSpeed";
                    column2 = "sunShine";
                    break;
                default:
                    column1 = "";
                    column2 = "";
                    break;
            }
            SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            long timeS = 0;
            long timeE = 0;
            try {
                Date date = bartDateFormat.parse(startTime);
                timeS = date.getTime();
                date = bartDateFormat.parse(endTime);
                timeE = date.getTime();

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MdLabDBHelper mdLabDBHelper = MdLabDBHelper.getInstance(this.getContext());
            for (int i = 0; i < 60; ++i) {
                int temperatureSum = 0;
                int temperatureAverage = 0;
                int humiditySum = 0;
                int humidityAverage = 0;
                Cursor cursor = mdLabDBHelper.selectSensorInfo("Weather", column1 + "," + column2, timeS + 60000 * i, timeS + 60000 * (i + 1));
                int j = cursor.getCount();
                if (cursor.moveToFirst()) {
                    do {
                        temperatureSum = cursor.getInt(cursor.getColumnIndex(column1)) + temperatureSum;
                        humiditySum = cursor.getInt(cursor.getColumnIndex(column2)) + humiditySum;

                    } while (cursor.moveToNext());
                }
                cursor.close();
                if (j != 0) {
                    temperatureAverage = temperatureSum / j;
                    humidityAverage = humiditySum / j;
                    values.add(new PointValue(i, temperatureAverage));
                    valuesH.add(new PointValue(i, humidityAverage));
                } else {
                    Toast.makeText(getActivity(), "无历史数据", Toast.LENGTH_SHORT);
                }

            }
            mdLabDBHelper.close();
            Line line = new Line(values);
            Line lineH = new Line(valuesH);
            line.setColor(ChartUtils.COLOR_GREEN);
            lineH.setColor(ChartUtils.COLOR_ORANGE).setCubic(true);
            line.setHasLabelsOnlyForSelected(true);

            List<Line> lines = new ArrayList<Line>();
            lines.add(line);
            lines.add(lineH);

            LineChartData data = new LineChartData(lines);
            data.setAxisXBottom(new Axis().setName("Axis X"));
            data.setAxisYLeft(new Axis().setName("Axis Y").setHasLines(true));
            return data;

        }

        //2: pressure 3: pm2.5 4:rain
        private LineChartData generateLinePressureData(int dataType) {

            List<PointValue> values = new ArrayList<PointValue>();

            String startTime = "2017-02-10 15:00:00";
            String endTime = "2017-02-10 16:00:00";
            String column;
            switch (dataType) {
                case 2:
                    column = "pressure";
                    Log.d("ViewPagerCharts", "generateLinePressureData: pressure");
                    break;
                case 3:
                    column = "pm25";
                    Log.d("ViewPagerCharts", "generateLinePressureData: pm25");
                    break;
                case 5:
                    column = "rainCollect";
                    Log.d("ViewPagerCharts", "generateLinePressureData: rain");
                    break;
                default:
                    column = "";
                    break;

            }
            SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            long timeS = 0;
            long timeE = 0;
            try {
                Date date = bartDateFormat.parse(startTime);
                timeS = date.getTime();
                date = bartDateFormat.parse(endTime);
                timeE = date.getTime();

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MdLabDBHelper mdLabDBHelper = MdLabDBHelper.getInstance(this.getContext());
            for (int i = 0; i < 60; ++i) {
                float pressureSum = 0;
                float pressureAverage = 0;

                Cursor cursor = mdLabDBHelper.selectSensorInfo("Weather", column, timeS + 60000 * i, timeS + 60000 * (i + 1));
                int j = cursor.getCount();
                if (cursor.moveToFirst()) {
                    do {
                        if (dataType == 5) {
                            pressureSum = cursor.getInt(cursor.getColumnIndex(column)) + pressureSum;
                        } else {
                            pressureSum = cursor.getFloat(cursor.getColumnIndex(column)) + pressureSum;
                        }

                    } while (cursor.moveToNext());
                }
                cursor.close();

                if (j != 0) {
                    pressureAverage = pressureSum/j;
                    if (dataType == 3) {
                        Log.d("ViewPagerCharts", "generateLinePressureData: average " + pressureAverage);
                    }
                    values.add(new PointValue(i, pressureAverage));
                } else {
                    Toast.makeText(getActivity(), "无历史数据", Toast.LENGTH_SHORT);
                }

            }
            mdLabDBHelper.close();
            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN);
            if (dataType == 5) {
                line.setFilled(true);
            }
            line.setHasLabelsOnlyForSelected(true);
            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            LineChartData data = new LineChartData(lines);
            data.setAxisXBottom(new Axis().setName("X 轴"));
            data.setAxisYLeft(new Axis().setName("Y 轴").setHasLines(true));
            return data;
        }

    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "温湿度";
                case 1:
                    return "气压";
                case 2:
                    return "PM2.5";
                case 3:
                    return "风速&光照";
                case 4:
                    return "降雨量";
            }
            return null;
        }
    }

}