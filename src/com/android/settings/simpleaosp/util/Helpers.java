package com.android.settings.simpleaosp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.android.settings.R;

public class Helpers {
    // avoids hardcoding the tag
    private static final String TAG = Thread.currentThread().getStackTrace()[1].getClassName();

    public Helpers() {
        // dummy constructor
    }

    public static void restartSystemUI() {
        CMDProcessor.startSuCommand("pkill -f com.android.systemui");
    }

    public static void showSystemUIrestartDialog(Activity a) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(R.string.systemui_restart_title);
        builder.setMessage(R.string.systemui_restart_message);
        builder.setPositiveButton(R.string.print_restart,
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        ProgressDialog dialog = new ProgressDialog(a);
                        dialog.setMessage(a.getResources().getString(R.string.restarting_ui));
                        dialog.setCancelable(false);
                        dialog.setIndeterminate(true);
                        dialog.show();
                    }
                    @Override
                    protected Void doInBackground(Void... params) {
                        // Give the user a second to see the dialog
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // Ignore
                        }

                        // Restart the UI
                        CMDProcessor.startSuCommand("pkill -f com.android.systemui");
                        a.finish();
                        return null;
                    }
                };
                task.execute();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }
}
