package io.appwrite;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.maitretech.mydemo.R;

import java.io.IOException;

import io.appwrite.services.Account;
import io.appwrite.services.Database;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.getting_data));
        getDataBtn = findViewById(R.id.get_data_btn);
        responseDataText = findViewById(R.id.response_data_text);
        loginBtn = findViewById(R.id.logIn_logOut_btn);
        loginBtn.setOnClickListener(loginBtnListener);
        getDataBtn.setOnClickListener(getDataClickListener);
        client = new Client(getApplicationContext());
        client.setEndpoint("<Endpoint URL>"); //Enter Endpoint URL
        client.setProject("<ProjectID>"); //Enter ProjectID
    }

    private View.OnClickListener loginBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            responseDataText.setText(getString(R.string.no_data_loaded));
            startLogin();
        }
    };
    private void startLogin() {
        new LoginTask().execute();
    }

    private View.OnClickListener getDataClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            responseDataText.setText(getString(R.string.no_data_loaded));
            startDataRequest();
        }
    };

    private void startDataRequest() {
        new GetDataTask().execute();
    }

    private class LoginTask extends AsyncTask<Void, Void, Response> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Response doInBackground(Void... voids) {
            account = new Account(client);
            Response response = null;
            try {
                    response = account.getSessions().execute();
                    if(response.code() == 401){
                          response = account.createSession("abc@example.com","Hello@123")
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
                responseData = response.body().string();

            } catch (Exception e) {
                showErrorMessage(e.getMessage());
            }
                if(isLogedIn){
                    loginBtn.setText("LogOut");
                }
                else {
                    loginBtn.setText("LogIn");
                }
                responseDataText.setText(responseData);
                progressDialog.dismiss();
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Response> {


        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Response doInBackground(Void... params) {
           try {
                database = new Database(client);
                //Enter CollectionID and DocumentID
                Response response = database.getDocument("<CollectionID>","<DocumentID>").execute();
                return response;
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


            progressDialog.dismiss();
        }
    }
}