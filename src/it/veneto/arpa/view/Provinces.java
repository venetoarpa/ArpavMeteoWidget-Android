package it.veneto.arpa.view;

import it.veneto.arpa.R;
import it.veneto.arpa.controller.Controller;

import android.app.Activity;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class that show the provinces list
 * @author Luca
 *
 */
public class Provinces extends Activity {
    private int res = 2;

    /**
     * @see Android activity.onCreate(Bundle savedInstanceState)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provinces);
        final ListView listview = (ListView) findViewById(R.id.listProvinces);
        String[] values = Controller.getInstance().getProvincesName(getApplicationContext());

        if (values.length == 0) {
            setResult(1);
            finish();
        }
        else {
            final ArrayList<String> list = new ArrayList<String>();

            for (int i = 0; i < values.length; ++i) {
                list.add(values[i]);
            }

            final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    Intent myIntent = new Intent(Provinces.this, Cities.class);
                    myIntent.putExtra("Province", parent.getItemAtPosition(position).toString());
                    startActivityForResult(myIntent, res);
                }
            });
        }
    }


    /**
     * Get the result of the activities launched
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 2) {
            setResult(0);
            finish();
            return;
        }
    }

    /**
     * Class used to create the array adapter for the cities list
     * @author Luca
     *
     */
    private class StableArrayAdapter extends ArrayAdapter<String> {
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);

            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(0);
    }

}
