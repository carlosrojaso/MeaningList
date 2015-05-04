package com.carlosrojasblog.meaninglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private String userid;
    ParseObject fooItem = new ParseObject("item");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_my_toolbar);


        // Enable Local Datastore.
        //Parse.enableLocalDatastore(this);

        Parse.initialize(this, "Y5NdFk1wqtgHi9qkc0lInFeEydyIFym3rZyPpVbL", "feLnPrBwBA8fxNpAbjcXa66OnxIF4v94zZojzGkw");

        userid = getIntent().getStringExtra(LoginActivity.EXTRA_MESSAGE);
        Log.i("User id",userid);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);

        setupListViewListener();
        toolbar.setTitle("MeaningList");
        setSupportActionBar(toolbar);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            LoginManager.getInstance().logOut();
            Intent g = new Intent(getApplicationContext(),LoginActivity.class);
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


}
