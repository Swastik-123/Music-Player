package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items; //for storing song item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listViewSong);

        runtimePermission();

    }


    public void runtimePermission() // For Runtime Permission
    {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        dishplaySong();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();


                    }
                }).check();
    }

    public ArrayList<File> findSong (File file) //For find Song
    {
        ArrayList<File> arrayList = new ArrayList<File>();

        File[] files = file.listFiles();

        for(File singlefile: files)
        {
            if(singlefile.isDirectory() && !singlefile.isHidden()) //check file is Hidden or not and file is Under directory or not
            {
                arrayList.addAll(findSong(singlefile)); //add all file
            }
            else
            {
                if(singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")) // check file "mp3" ar not
                {
                    arrayList.add(singlefile);
                }
            }
        }

        return  arrayList;


    }


    void dishplaySong() //for Dishplay the song
    {
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];

        for(int i = 0;i<mySongs.size();i++) //for store all the song inside the item
        {
            items[i] = mySongs.get(i).getName().toString().replace(".mp3","").replace("wav","");
        }

        //ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items); //simple_list_item_1 default layout
        //listView.setAdapter(myAdapter);
        costomAdapter costomAdapter = new costomAdapter();
        listView.setAdapter(costomAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // After creating player activity but before working player activity,instead of UI design
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                String songName = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                        .putExtra("songs",mySongs)
                        .putExtra("songname",songName)
                         .putExtra("pos",i));



            }
        });




    }



    class costomAdapter extends BaseAdapter //all method are default with this class
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View myView = getLayoutInflater().inflate(R.layout.list_ltem,null);
            TextView textSong = myView.findViewById(R.id.txtsongnmae);
            textSong.setSelected(true);
            textSong.setText(items[i]);

            return myView;
        }
    }


}