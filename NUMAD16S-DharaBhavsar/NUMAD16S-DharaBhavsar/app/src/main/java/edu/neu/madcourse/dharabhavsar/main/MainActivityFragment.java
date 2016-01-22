package edu.neu.madcourse.dharabhavsar.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.neu.madcourse.dharabhavsar.ut3.MainActivityUt3;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Method to make Quit button exit the application.
        Button btn1 = (Button) rootView.findViewById(R.id.quit_button);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                One way of going back to the App List
                getActivity().finish();
//                System.exit(0);

//                Second way of going back to the App List
//                getActivity().moveTaskToBack(true);

//                Returning back to the Home Screen
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });

        // Method to pop-up UT3 app
        Button btn2 = (Button) rootView.findViewById(R.id.tictactoe_button);
        btn2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PackageManager pm = getActivity().getPackageManager();
//                Intent intent = pm.getLaunchIntentForPackage("edu.neu.madcourse.dharabhavsar.ut3.MainActivityUt3");
                Intent intent = new Intent(MainActivityFragment.this.getActivity(), MainActivityUt3.class);
                startActivity(intent);
            }});

        // Method to generate an error
        Button btn3 = (Button) rootView.findViewById(R.id.generate_error_button);
        btn3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PackageManager pm = getActivity().getPackageManager();
//                Intent intent = pm.getLaunchIntentForPackage("edu.neu.madcourse.dharabhavsar.ut3.MainActivity");
//                startActivity(intent);
                try {
                    PackageManager pm = getActivity().getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage("edu.neu.madcourse.dharabhavsar.ut3.MainActivity");
                    startActivity(intent);
                }catch (Exception e) {
                    Log.d("MainActivityFragment", e.getMessage());
                    MainActivityFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
//                            ErrorDialog(e.getMessage());
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivityFragment.this.getContext());
                            builder1.setMessage("Write your message here.");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            builder1.setNegativeButton(
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                    });
                }
                }});

        return rootView;
    }
}
