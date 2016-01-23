package edu.neu.madcourse.dharabhavsar.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.neu.madcourse.dharabhavsar.about.MainActivityAbout;
import edu.neu.madcourse.dharabhavsar.ut3.MainActivityUt3;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    MediaPlayer mMediaPlayer;

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

                mMediaPlayer = MediaPlayer.create(MainActivityFragment.this.getActivity(), R.raw.short_ping_freesound_org);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.start();

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

                mMediaPlayer = MediaPlayer.create(MainActivityFragment.this.getActivity(), R.raw.short_ping_freesound_org);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.start();

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

                mMediaPlayer = MediaPlayer.create(MainActivityFragment.this.getActivity(), R.raw.distillerystudio_error_03_freesound_org);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.start();

//          App generates an error due to proper
                PackageManager pm = getActivity().getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage("edu.neu.madcourse.dharabhavsar.ut3.MainActivity");
                startActivity(intent);


//                try {
//                    PackageManager pm = getActivity().getPackageManager();
//                    Intent intent = pm.getLaunchIntentForPackage("edu.neu.madcourse.dharabhavsar.ut3.MainActivity");
//                    startActivity(intent);
//                } catch (NullPointerException e) {
////                    Log.d("MainActivityFragment", e.getMessage());
//                    String err = (e.getMessage()==null) ? "Sorry something went wrong!" : e.getMessage();
//                    Log.e("generate-error-button:", err);
//
////                    Try - Catch 2
//                    /*AlertDialog alertDialog = new AlertDialog.Builder(
//                            MainActivityFragment.this.getContext()).create();
//                    alertDialog.setTitle("Sorry!");
//                    alertDialog.setMessage("The application NUMAD16S-DharaBhavsar has stopped unexpectedly. " +
//                            "Please try again.");
//                    alertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
//                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Force Quit",
//                            new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            getActivity().finish();
////                            Returning back to the Home Screen
//                            Intent startMain = new Intent(Intent.ACTION_MAIN);
//                            startMain.addCategory(Intent.CATEGORY_HOME);
//                            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(startMain);
//                            Toast.makeText(MainActivityFragment.this.getContext().getApplicationContext(),
//                                    "Quitting App NUMAD16S-DharaBhavsar", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Report",
//                            new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(MainActivityFragment.this.getContext().getApplicationContext(),
//                                    "Reporting App Error", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    alertDialog.show();*/
//
////                    Try - Catch 1
//                    /*MainActivityFragment.this.getActivity().runOnUiThread(new Runnable() {
//                        public void run() {
////                            ErrorDialog(e.getMessage());
//
//                            AlertDialog alertDialog = new AlertDialog.Builder(
//                                    MainActivityFragment.this.getContext()).create();
//                            alertDialog.setTitle("Sorry!");
//                            alertDialog.setMessage("The application NUMAD16S-DharaBhavsar has stopped unexpectedly. " +
//                                    "Please try again.");
//                            alertDialog.setIcon(R.drawable.restart);
//                            alertDialog.setButton("Force Quit", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(MainActivityFragment.this.getContext().getApplicationContext(),
//                                            "You clicked on OK", Toast.LENGTH_SHORT).show();
//                                }
//                            });
////                            alertDialog.setButton("Report",  );
//                            alertDialog.show();
//
//                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivityFragment.this.getContext());
//                            builder1.setMessage("Write your message here.");
//                            builder1.setCancelable(true);
//
//                            builder1.setPositiveButton(
//                                    "Yes",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//                                        }
//                                    });
//
//                            builder1.setNegativeButton(
//                                    "No",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//                                        }
//                                    });
//
//                            AlertDialog alert11 = builder1.create();
//                            alert11.show();
//                        }
//                    });*/
//                }
            }
        });

//        Method to view About Details
        Button btn4 = (Button) rootView.findViewById(R.id.about_button);
        btn4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMediaPlayer = MediaPlayer.create(MainActivityFragment.this.getActivity(), R.raw.short_ping_freesound_org);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.start();

                Intent intent = new Intent(MainActivityFragment.this.getActivity(), MainActivityAbout.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
