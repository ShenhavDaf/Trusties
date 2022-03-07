package com.example.trusties;

import android.app.AlertDialog;
import android.content.Context;

public class CommonFunctions {

    public void myPopup(Context context , String msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setNegativeButton("OK", (dialog, which) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.setTitle("Error");
        alert.setMessage("\n" + msg + "\n");
        alert.show();
    }
}
