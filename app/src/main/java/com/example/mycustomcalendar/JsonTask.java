package com.example.mycustomcalendar;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonTask extends AsyncTask<String, String, String> {
    ProgressDialog pd;
    public String jsonStr;
    protected void onPreExecute() {
        super.onPreExecute();

     /*   pd = new ProgressDialog(CustomCalendarView);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();*/
    }

    protected String doInBackground(String... params) {


        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer buffer=null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            System.out.println("Before conn");
            connection.connect();
            System.out.println("Before conn1");

            InputStream stream = connection.getInputStream();
            System.out.println("Before connafagsgsr");
            reader = new BufferedReader(new InputStreamReader(stream));
            System.out.println("123");
            buffer = new StringBuffer();
            System.out.println("345");
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

            }
            jsonStr=buffer.toString();
            System.out.println("buffer" + jsonStr);
            //return buffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
/*        if (pd.isShowing()){
            pd.dismiss();
        }*/
        jsonStr=result;
        System.out.println("result"+ jsonStr);
    }
}

