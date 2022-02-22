package com.example.flickerlayout;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flickerlayout.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickerJsonData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener {
    private static final String TAG = "MainActivity";
    private FlickerRecyclerViewAdapter mFlickerRecyclerViewAdapter;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setSupportActionBar(binding.toolbar);
        activateToolbar(false);


        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this,recyclerView,this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFlickerRecyclerViewAdapter=new FlickerRecyclerViewAdapter(new ArrayList<Photo>(), this);
        recyclerView.setAdapter(mFlickerRecyclerViewAdapter);

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetFlickerJsonData getFlickerJsonData= new GetFlickerJsonData("https://www.flickr.com/services/feeds/photos_public.gne",
                "en-us",true,this);
        //getFlickerJsonData.executeOnSameThread("Android,cake");
        getFlickerJsonData.execute("Android,cake");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: starts");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        Log.d(TAG, "onCreateOptionsMenu: ends");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: starts");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d(TAG, "onOptionsItemSelected: ends");
            return true;
        }
        Log.d(TAG, "onOptionsItemSelected: ends");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp: starts");

        Log.d(TAG, "onSupportNavigateUp: ends");

               super.onSupportNavigateUp();
               return true;
    }
@Override
public void OnDataAvailable(List<Photo> data, DownloadStatus Status){
    Log.d(TAG, "OnDataAvailable: starts");
        if (Status==DownloadStatus.OK)
            mFlickerRecyclerViewAdapter.loadNewData(data);
        else
        {
            Log.e(TAG, "onDownloadComlete failed with status: "+ Status);
        }
    Log.d(TAG, "OnDataAvailable: ends");
}

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: starts");
        Toast.makeText(this,"normal tap position: " + position,Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: starts");
        Intent intent=new Intent(this,PhotoDetailActivity.class);
        intent.putExtra(PHOTO_TRANSFER,mFlickerRecyclerViewAdapter.getPhoto(position));
        startActivity(intent);
    }
}