package edu.neu.madcourse.dharabhavsar.ui.about;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.neu.madcourse.dharabhavsar.ui.main.R;

/**
 * Created by Dhara on 1/22/2016.
 */
public class MainFragmentAbout extends Fragment {

    public MainFragmentAbout() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_main_about, container, false);

//        used the below API to obtain the phone's IMEI number
//        android.telephony.TelephonyManager.getDeviceId();
        TelephonyManager mngr = (TelephonyManager) this.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String imei_code = mngr.getDeviceId();
        Log.d("MainFragmentAbout", "IMEI Code obtained is ::: " + imei_code);
        Log.d("MainFragmentAbout", "IMEI Code String obtained is ::: " + imei_code);

        MainFragmentAbout.this.getActivity().setContentView(R.layout.fragment_main_about);

        TextView textView;

        textView = (TextView) MainFragmentAbout.this.getActivity().findViewById(R.id.about_name);
        textView.setText("Dhara Bhavsar");

        textView = (TextView) MainFragmentAbout.this.getActivity().findViewById(R.id.about_email);
        textView.setText("bhavsar.d@husky.neu.edu");

        textView = (TextView) MainFragmentAbout.this.getActivity().findViewById(R.id.about_school_year);
        textView.setText("1st Year");

        textView = (TextView) MainFragmentAbout.this.getActivity().findViewById(R.id.about_degree);
        textView.setText("Master of Science in Computer Science");

        textView = (TextView) MainFragmentAbout.this.getActivity().findViewById(R.id.about_imei);
        textView.setText("IMEI : " + imei_code);

//        code to display Head Shot Image
        ImageView imageView1 = (ImageView) MainFragmentAbout.this.getActivity().findViewById(R.id.imageView);
        imageView1.setBackgroundResource(R.drawable.head_shot);

        return rootView;
    }
}
