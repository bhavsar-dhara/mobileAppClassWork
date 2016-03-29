package edu.neu.madcourse.dharabhavsar.ui.communication2player;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.madcourse.dharabhavsar.ui.main.R;
import edu.neu.madcourse.dharabhavsar.utils.gcmcomm.CommunicationMain;

public class MainFragmentScraggle2 extends Fragment {

    private AlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_main_scraggle2, container, false);
        // Handle buttons here...
        View newButton = rootView.findViewById(R.id.new_button);
//        View continueButton = rootView.findViewById(R.id.continue_button);
        View ackButton = rootView.findViewById(R.id.acknowledgementsBtn);
        View helpButton = rootView.findViewById(R.id.game_instructions_button);
        View scoreButton = rootView.findViewById(R.id.score_board_button);
        View combineButton = rootView.findViewById(R.id.combine_play_button);
        View testGCMButton = rootView.findViewById(R.id.test_gcm);

        testGCMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CommunicationMain.class);
                getActivity().startActivity(intent);
            }
        });
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScraggleGameActivity2.class);
                getActivity().startActivity(intent);
            }
        });
        combineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), ScraggleGameActivity2Combine.class);
                Intent intent = new Intent(getActivity(), ShowUserListDialogActivity.class);
                getActivity().startActivity(intent);
            }
        });
        /*continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScraggleGameActivity2.class);
                intent.putExtra(ScraggleGameActivity2.KEY_RESTORE, true);
                getActivity().startActivity(intent);
            }
        });*/
        ackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.ack_text_2);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // nothing
                            }
                        });
                mDialog = builder.show();
            }
        });
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.help_text_2);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // nothing
                            }
                        });
                mDialog = builder.show();
            }
        });
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TabFragmentActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Get rid of the about dialog if it's still up
        if (mDialog != null)
            mDialog.dismiss();
    }
}
