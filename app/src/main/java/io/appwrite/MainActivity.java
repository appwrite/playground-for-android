package io.appwrite;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.maitretech.mydemo.R;

import java.io.IOException;
import java.util.ArrayList;

import io.appwrite.enums.OrderType;
import io.appwrite.services.Account;
import io.appwrite.services.Database;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //AppWrite Config
    private static final String project_id = "5eeb1d6e140e4";
    private static final String endpoint = "http://demo.appwrite.io/v1";

    private Button mLoginButton;
    private TextView mResponseDateTextView;
    private Client mClient = null;
    private boolean isLoggedIn = false;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button getDataBtn = findViewById(R.id.get_data_btn);
        mResponseDateTextView = findViewById(R.id.response_data_text);
        mLoginButton = findViewById(R.id.logIn_logOut_btn);
        mLoginButton.setOnClickListener(this);
        getDataBtn.setOnClickListener(this);
        mClient = new Client(getApplicationContext());
        mClient.setEndpoint(getResources().getString(R.string.endpoint));
        mClient.setProject(getResources().getString(R.string.project_id));
    }

    private void startLogin() {
        new LoginTask().execute();
    }

    private void startDataRequest() {
        new GetDataTask().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_data_btn:
                mResponseDateTextView.setText(getString(R.string.no_data_loaded));
                startDataRequest();
                break;
            case R.id.logIn_logOut_btn:
                mResponseDateTextView.setText(getString(R.string.no_data_loaded));
                startLogin();
                break;
            default:
                Log.d(this.getClass().getSimpleName(), "Unable to find view");
        }
    }

    private class LoginTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected void onPreExecute() { }

        @Override
        protected Response doInBackground(Void... voids) {
            Account account = new Account(mClient);
            Response response = null;
            try {
                response = account.getSessions().execute();
                if (response.code() == 401) {
                    //Enter Email an Password from which you want to login
                    response = account.createSession(getResources().getString(R.string.user_email),
                            getResources().getString(R.string.user_password))
                            .execute();
                    isLoggedIn = true;
                } else {
                    response = account.deleteSessions().execute();
                    isLoggedIn = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            String responseData = null;
            try {
                responseData = response.body().string();
            } catch (Exception e) {
                showErrorMessage(e.getMessage());
            }
            if (isLoggedIn) {
                mLoginButton.setText("LogOut");
            } else {
                mLoginButton.setText("LogIn");
            }
            mResponseDateTextView.setText(responseData);
            //progressDialog.dismiss();
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Response> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Response doInBackground(Void... params) {
            try {
                Database database = new Database(mClient);
                //Enter Collection ID and Document ID
                //Response response = database.getDocument("5eeb1d97ba0dd", "5eecee3bcaec9")
                //        .execute();
                return database.listDocuments(getResources().getString(R.string.collection_id),
                        new ArrayList(), 0, 50, "$id", OrderType.ASC,
                        "string", "", 0, 0)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response response) {
            //String responseData = Integer.toString(response.code());
            String responseData = null;
            try {
                responseData = response.body().string();
            } catch (IOException e) {
                showErrorMessage(e.getMessage());
            }
            mResponseDateTextView.setText(responseData);
        }
    }

    private void showErrorMessage(String msg) {
        Toast.makeText(this,
                msg, Toast.LENGTH_LONG).show();
    }

}