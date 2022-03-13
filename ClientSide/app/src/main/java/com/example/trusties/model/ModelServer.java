package com.example.trusties.model;

import android.content.Context;
import android.util.Log;

import com.example.trusties.CommonFunctions;
import com.example.trusties.MyApplication;
import com.example.trusties.RetrofitInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;

public class ModelServer {

    private final RetrofitInterface retrofitInterface;
    final private static String BASE_URL = "http://10.0.2.2:4000";

    public ModelServer() {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    /* ------------------------------------------------------------------------- */

    public void handleLoginDialog(String email, String password, Model.loginListener listener, Context context) {

        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);

        Call<Void> call = retrofitInterface.executeLogin(map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete(response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                new CommonFunctions().myPopup(context, "Error", t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void handleSignupDialog(HashMap<String, String> map, Model.signupListener listener, Context context) {

        Call<JsonObject> signUpCall = retrofitInterface.executeSignup(map);
        Call<Void> verifyCall = retrofitInterface.verifyEmail(map);

        signUpCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    String randomCodeFromServer = response.body().get("randomCode").toString();
//                    JsonObject json = response.body().get("newUser").getAsJsonObject();
//                    JsonElement isVerified = json.get("verified");
//                    Log.d("TAG",isVerified.toString());

                    verifyCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            listener.onComplete(randomCodeFromServer);
//                            isVerified.equal("true");
//                            json.remove("verified");

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            new CommonFunctions().myPopup(context ,"oops..", "didn't send email!");
                        }
                    });
                } else if (response.code() == 400) {
                    String msg = "The email address is already in use by another user";
                    new CommonFunctions().myPopup(context, "Error", msg);
                    System.out.println("---------------------" +msg);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new CommonFunctions().myPopup(context, "Error", t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void resendEmail(HashMap<String, String> map, Model.resendEmailListener listener,Context context) {

        Call<String> resendCall = retrofitInterface.resendEmail();
        Call<Void> verifyCall = retrofitInterface.verifyEmail(map);

        resendCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String randomCodeFromServer = response.body();
                if (response.code() == 200) {
                    verifyCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            listener.onComplete(randomCodeFromServer);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            new CommonFunctions().myPopup(context, "oops..", "didn't send email!");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new CommonFunctions().myPopup(context, "Error", t.getMessage());
            }
        });
    }

    public void verifiedUser( HashMap<String, String> map,Model.verifiedUserListener listener, Context context) {
        Call<Void> afterVerify = retrofitInterface.verifiedUser(map);
        afterVerify.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete("true");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    /* ------------------------------------------------------------------------- */

}
