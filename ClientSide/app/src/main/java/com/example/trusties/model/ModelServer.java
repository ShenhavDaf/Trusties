package com.example.trusties.model;

import android.content.Context;
import com.example.trusties.CommonFunctions;
import com.example.trusties.RetrofitInterface;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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


        Call<JsonObject> call = retrofitInterface.executeLogin(map);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject user = new JsonObject();
                String resMessage = "";

                if (response.code() == 200) {
                    user = response.body().get("user").getAsJsonObject();
                    resMessage = "user exists";
                } else if (response.code() == 400) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        resMessage = jObjError.get("error").toString();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

                user.addProperty("message", resMessage);
                listener.onComplete(response.code(), user);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
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
                } else if (response.code() == 400) {
                    String msg = "The email address is already in use by another user";
                    new CommonFunctions().myPopup(context, "Error", msg);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new CommonFunctions().myPopup(context, "Error", t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------------------- */

    public void resendEmail(HashMap<String, String> map, Model.resendEmailListener listener, Context context) {

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

    public void verifiedUser(HashMap<String, String> map, Model.verifiedUserListener listener, Context context) {
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

    public void forgotPassword(String email, Model.forgotPasswordListener listener) {

        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);

        Call<Void> forgotPassword = retrofitInterface.forgotPassword(email);
        //TODO: don't know if we need "verifyCall"
//        Call<Void> verifyCall = retrofitInterface.verifyEmail(map);

        forgotPassword.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                listener.onComplete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });


    }

    /* ------------------------------------------------------------------------- */

}
