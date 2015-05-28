package com.carlosrojasblog.meaninglist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    int notificaciones =0;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private GridView gridView;
    private String userid;
    ParseObject fooItem = new ParseObject("item");

    // Async

    //URL to get JSON Array
    private static String url = "http://www.iheartquotes.com/api/v1/random?format=json";

    //JSON Node Names
    //private static final String TAG_CONTENTS = "contents";
    //private static final String TAG_ID = "id";
    private static final String TAG_QUOTE = "quote";
    private static final String TAG_AUTHOR = "source";

    JSONArray user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_my_toolbar);

        MiTarea tarea = new MiTarea();
        tarea.execute();


        // Enable Local Datastore.
        //Parse.enableLocalDatastore(this);

        Parse.initialize(this, "Y5NdFk1wqtgHi9qkc0lInFeEydyIFym3rZyPpVbL", "feLnPrBwBA8fxNpAbjcXa66OnxIF4v94zZojzGkw");

        userid = getIntent().getStringExtra(LoginActivity.EXTRA_MESSAGE);
        Log.i("User id", userid);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();



        //tablet
        if (lvItems == null) {
            itemsAdapter = new MyAdapter(this,R.layout.gridview_item, items);
            gridView = (GridView) findViewById(R.id.gridview);
            gridView.setAdapter(itemsAdapter);
            setupGridViewListener();
        } else {
            itemsAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, items);
            lvItems.setAdapter(itemsAdapter);
            setupListViewListener();

        }

        readItems();


        toolbar.setTitle("MeaningList");
        setSupportActionBar(toolbar);

    }

    private void setupGridViewListener() {

        gridView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        //Log.i("tmpValue=",items.get(pos));

                        //removing
                        ParseQuery query = new ParseQuery("item");
                        query.whereEqualTo("value", items.get(pos));
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> object, ParseException e) {
                                if (e == null) {
                                    //Log.d("score", "Retrieved " + testList.size() + " test objects");

                                    ParseObject tempTest = object.get(0);
                                    tempTest.deleteInBackground();

                                } else {
                                    Log.d("object", "Error: " + e.getMessage());
                                }
                            }
                        });

                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        return true;
                    }

                });
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        //Log.i("tmpValue=",items.get(pos));

                        //removing
                        ParseQuery query = new ParseQuery("item");
                        query.whereEqualTo("value", items.get(pos));
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> object, ParseException e) {
                                if (e == null) {
                                    //Log.d("score", "Retrieved " + testList.size() + " test objects");

                                    ParseObject tempTest = object.get(0);
                                    tempTest.deleteInBackground();

                                } else {
                                    Log.d("object", "Error: " + e.getMessage());
                                }
                            }
                        });

                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        return true;
                    }

                });
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        //itemsAdapter.notifyDataSetChanged();
        etNewItem.setText("");

        //ParseObject fooItem = new ParseObject("item");
        fooItem.put("userId", userid);
        fooItem.put("value", itemText);
        fooItem.saveInBackground();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            LoginManager.getInstance().logOut();
            Intent g = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(g);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void readItems() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("item");
        query.whereEqualTo("userId", userid);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + objects.size() + " items");

                    for (int i = 0; i < objects.size(); i++) {
                        Object object = objects.get(i);
                        String name = ((ParseObject) object).getString("value").toString();
                        itemsAdapter.add(name);
                    }
                    itemsAdapter.notifyDataSetChanged();
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }



    private class MyAdapter extends ArrayAdapter {
        private List<String> items;
        private LayoutInflater inflater;

        public MyAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
            inflater = LayoutInflater.from(context);

        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            TextView name;

            if (v == null) {
                v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            name = (TextView) v.getTag(R.id.text);

            String item = (String) getItem(i);

            name.setText(item);

            return v;
        }



    }


    class MiTarea extends AsyncTask<JSONObject, Void, JSONObject> {
        private ProgressDialog pDialog;

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override

        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array
                //JSONObject userjson = (JSONObject) json;
                //JSONObject c = json.getJSONObject(TAG_CONTENTS);

                //JSONObject c = user.getJSONObject(0);

                // Storing  JSON item in a Variable
                //String responsejson = json.toString();

                //JSONObject etiq = c.getJSONObject("tags");
                //String id = json.getString(TAG_ID);
                String nametxt = json.getString(TAG_QUOTE);
                String emailtxt = json.getString(TAG_AUTHOR);

                //Set JSON Data in TextView
                Log.v("respuesta JSON", "ID: " + "Name: " + nametxt + " Email: " + emailtxt);
                //Log.v("respuesta JSON", "JSON: " +responsejson);

                Toast toast = Toast.makeText(getApplicationContext(), nametxt, Toast.LENGTH_SHORT);
                toast.show();


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static class JSONParser {

        static InputStream is = null;
        static JSONObject jObj = null;
        static String json = "";

        // constructor
        public JSONParser() {

        }

        public JSONObject getJSONFromUrl(String url) {

            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            // return JSON String
            return jObj;

        }
    }


}
