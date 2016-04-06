package com.samhith.book_your_show;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class List extends Activity {

    String myJSON;

    private static final String TAG_RESULTS="result";
    private static final String TAG_CNAME = "city_name";
    private static final String TAG_ENAME = "event";
    private static final String TAG_VEN ="venue";
    private static final String TAG_PRICE ="price";

    JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;

    ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        list = (ListView) findViewById(R.id.listviewer);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(List.this,"Booking..",Toast.LENGTH_LONG).show();
                booknow();
            }
        });
        personList = new ArrayList<HashMap<String,String>>();
        getData();

    }

    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("http://172.17.6.121/jsonlist.php");

                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);
            Toast.makeText(List.this,Integer.toString(peoples.length()),Toast.LENGTH_LONG).show();
            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String city = c.getString(TAG_CNAME);
                String event = c.getString(TAG_ENAME);
                String venue = c.getString(TAG_VEN);
                String price=c.getString(TAG_PRICE);
                Toast.makeText(List.this,city+event+venue+price,Toast.LENGTH_LONG).show();
                HashMap<String,String> persons = new HashMap<String,String>();

                persons.put(TAG_CNAME,city);
                persons.put(TAG_ENAME,event);
                persons.put(TAG_VEN,venue);
                persons.put(TAG_PRICE,price);
                personList.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    List.this, personList, R.layout.listview,
                    new String[]{TAG_CNAME,TAG_ENAME,TAG_VEN,TAG_PRICE},
                    new int[]{R.id.id, R.id.name, R.id.address,R.id.price}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void booknow(){
        Toast.makeText(List.this,"Booking..",Toast.LENGTH_LONG).show();
        Intent i = new Intent(List.this,BookNow.class);
        startActivity(i);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
