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

    public int CheckTitleAndDescription(String title, String description, Context context) {
        if (title.equals("") || description.equals("")) {
            String msg = "You need to add title/description first";
            new CommonFunctions().myPopup(context, "Error", msg);
            return 0;
        } else
            return 1;

    }

    public int CheckLocation(String address, Context context) {
        if (address == null) {
            String msg = "You need to add location";
            new CommonFunctions().myPopup(context, "Error", msg);
            return 0;
        } else
            return 1;

    }

}
