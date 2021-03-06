package io.appwrite;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.maitretech.mydemo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import io.appwrite.enums.OrderType;
import io.appwrite.services.Account;
import io.appwrite.services.Database;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //AppWtire Config
    private static final String project_id = "5eeb1d6e140e4";
    private static final String endpoint =  "http://demo.appwrite.io/v1";

    private Button getDataBtn;
    private Button loginBtn;
    private TextView responseDataText;
    private Client client = null;
    private Account account = null;
    private Database database = null;
    private boolean isLogedIn = false;

    private void showErrorMessage(String msg) {
        Toast.makeText(MainActivity.this,
                msg, Toast.LENGTH_LONG).show();
    }

    private View.OnClickListener loginBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            responseDataText.setText(getString(R.string.no_data_loaded));
            if (!getResources().getString(R.string.project_id).equals("Enter Project Id ") && !getResources().getString(R.string.endpoint).equals("Enter Endpoint")) {
                startLogin();
            } else {
                Toast.makeText(MainActivity.this, "Please Update EndPoint and Project_ID!!", Toast.LENGTH_SHORT).show();
            }


        }
    };
    private View.OnClickListener getDataClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            responseDataText.setText(getString(R.string.no_data_loaded));
            if (!getResources().getString(R.string.project_id).equals("Enter Project Id ") && !getResources().getString(R.string.endpoint).equals("Enter Endpoint")) {
                startDataRequest();
            } else {
                Toast.makeText(MainActivity.this, "Please Update EndPoint and Project_ID!!", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private void startLogin() {
        new LoginTask().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDataBtn = findViewById(R.id.get_data_btn);
        responseDataText = findViewById(R.id.response_data_text);
        loginBtn = findViewById(R.id.logIn_logOut_btn);

        loginBtn.setOnClickListener(loginBtnListener);
        getDataBtn.setOnClickListener(getDataClickListener);
        client = new Client(getApplicationContext());
        client.setEndpoint(getResources().getString(R.string.endpoint));
        client.setProject(getResources().getString(R.string.project_id));


    }

    private void startDataRequest() {
        new GetDataTask().execute();
    }

    private class LoginTask extends AsyncTask<Void, Void, Response> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Response doInBackground(Void... voids) {
            account = new Account(client);
            Response response = null;
            try {
                    response = account.getSessions().execute();
                    if(response.code() == 401){

                          //Enter Email an Passowrd from which you want to login
                        response = account.createSession(getResources().getString(R.string.user_email),
                                   getResources().getString(R.string.user_password))
                                   .execute();
                          isLogedIn  = true;
                    }
                    else{
                        response = account.deleteSessions()
                                .execute();
                        isLogedIn  = false;
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
                responseData = Objects.requireNonNull(response.body()).string();

            } catch (Exception e) {
                showErrorMessage(e.getMessage());
            }
                if(isLogedIn){
                    loginBtn.setText(getResources().getString(R.string.LogOut));
                }
                else {
                    loginBtn.setText(getResources().getString(R.string.LogIn));
                }
                responseDataText.setText(responseData);
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
                database = new Database(client);

                //Enter Collection ID and Document ID
                //Response response = database.getDocument("5eeb1d97ba0dd", "5eecee3bcaec9")
                //        .execute();
                return database.listDocuments(getResources().getString(R.string.collection_id), new ArrayList<>(), 0, 50, "$id", OrderType.ASC, "string", "", 0, 0)
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
                responseDataText.setText(responseData);


        }
    }
}