package edu.neu.madcourse.dharabhavsar.dictionary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.neu.madcourse.dharabhavsar.main.R;

/**
 * Created by Dhara on 2/5/2016.
 */
public class AcknowledgementFragmentActivity extends Fragment {

    public AcknowledgementFragmentActivity() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =
                inflater.inflate(R.layout.fragment_main_ack, container, false);

        TextView textViewAck = (TextView) rootView.findViewById(R.id.acknowledgements);
        textViewAck.setMovementMethod(new ScrollingMovementMethod());

//        AcknowledgementFragmentActivity.this.getActivity().setContentView(R.layout.fragment_main_ack);

        return rootView;
    }
}
