package com.sample.mysamples.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sample.mysamples.R;
import com.sample.mysamples.model.app.AppController;
import com.sample.mysamples.model.db.entity.User;
import com.sample.mysamples.model.db.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by truong.tam on 15/06/19.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText userId;
    private EditText pass;
    private Button btnLogin;

    private UserModel mUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_use_support_library);
        getSupportActionBar().hide();
        mUserModel = new UserModel(getApplicationContext());
        findViews(); //viewの読み込み

        List<User> users = mUserModel.findAll();
        for(User user : users) {
            Log.d(TAG, user.toString());
        }
    }

    /**
     * Viewの読み込み
     */
    private void findViews() {
        userId = (EditText) findViewById(R.id.user_id);  //user ID
        pass = (EditText) findViewById(R.id.password);   //password
        btnLogin = (Button) findViewById(R.id.btnLogin); //Login button
        // ボタンのイベントリスナーを設定する
        btnLogin.setOnClickListener(this);
    }

    /**
     * ボタンクリックのイベントリスナー。
     * ボタンが押された時のアクションを実行する。
     *
     * @param view 押されたボタンのViwe
     */
    public void onClick(View view) {
        if (view == btnLogin) {
            //call login api
            requestLoginApi();
        }
    }

    /**
     * リクエスト処理
     */
    private void requestLoginApi() {
        String tagJsonObj = getResources().getString(R.string.json_obj_req);
        String url = getResources().getString(R.string.api_login_url);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.loading_message));
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    String uid = null;
                    boolean result = false;
                    pDialog.dismiss();
                    try {
                        result = response.getBoolean("response");
                        uid = response.getString("uid");
                        User user = new User(uid, userId.getText().toString(), pass.getText().toString(), true);
                        mUserModel.save(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (result) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    pDialog.dismiss();
                    if (error instanceof NetworkError) {
                    } else if (error instanceof ServerError) {
                    } else if (error instanceof AuthFailureError) {
                    } else if (error instanceof ParseError) {
                    } else if (error instanceof NoConnectionError) {
                    } else if (error instanceof TimeoutError) {
                    }
                }
            }
        );
        AppController.getInstance().addToRequestQueue(jsonObjReq, tagJsonObj);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // 戻るボタンの処理
            String tagJsonObj = getResources().getString(R.string.json_obj_req);
            AppController.getInstance().cancelPendingRequests(tagJsonObj);
        }
        return super.onKeyDown(keyCode, event);
    }
}