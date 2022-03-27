package com.example.trusties;

import android.app.AlertDialog;
import android.content.Context;

public class CommonFunctions {

    public void myPopup(Context context, String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setNegativeButton("OK", (dialog, which) -> dialog.cancel());
//        builder.setView(R.layout.popup);  //popup.xml

        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.setMessage("\n" + msg + "\n");
        alert.show();
    }
}
