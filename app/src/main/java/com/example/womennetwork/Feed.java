package com.example.womennetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

public class Feed extends AppCompatActivity implements SearchView.OnQueryTextListener {
    int ct=0;
    ArrayList<Blog> arr;
    Bitmap mImageBitmap;
    TextView tvname,tvcontent,tvvisited,tvtag;
    CustomAdapter adapter;
    ImageView im;
    SearchView searchView;
    MenuItem searchmenuitem;
    private DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ListView lv=findViewById(R.id.listview);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        arr = (ArrayList<Blog>) args.getSerializable("arr");




        adapter=new CustomAdapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>=0 && position < arr.size()) {
                    Intent intent=new Intent(Feed.this,ExpandedBlog.class);
                    Bundle args = new Bundle();
                    args.putSerializable("post",(Serializable)arr.get(position));
                    Log.d("krishnaa1",arr.get(0).name);
                    intent.putExtra("BUNDLE",args);
                    startActivity(intent);
                }
            }
        });
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(this, Post.class);
                this.startActivity(intent);
                break;
            case R.id.item2:
                // another startActivity, this is for item with id "menu_item2"
                db = FirebaseDatabase.getInstance().getReference("db");
                db.orderByKey().addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Blog blog = dataSnapshot.getValue(Blog.class);
                        if(!arr.contains(blog))
                        {
                            arr.add(blog);

                            Log.d("krishnaaa1","adding "+blog.name);
                        }
                        Log.d("krishnaa","child"+blog.name);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Intent i=new Intent(Feed.this,Feed.class);
                LinkedHashSet<Blog> hashSet = new LinkedHashSet<>(arr);

                arr = new ArrayList<>(hashSet);
                Collections.sort(arr);


                Bundle args = new Bundle();
                args.putSerializable("arr",(Serializable)arr);
                Log.d("krishnaa1",arr.get(0).name);
                i.putExtra("BUNDLE",args);
                startActivity(i);

                break;
            case R.id.item3:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item=menu.findItem(R.id.search);
        item.setVisible(true);
        SearchManager searchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchmenuitem=menu.findItem(R.id.search);
        searchView=(SearchView)searchmenuitem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("krishnaaa",query);
        ArrayList<Blog>arr2=(ArrayList<Blog>) arr.clone();
        arr.clear();
        for(int i=0;i<arr2.size();i++)
            if(arr2.get(i).tag.equals(query)) {
                arr.add(arr2.get(i));
                Log.d("krishnaaa","new: "+arr2.get(i).name);
            }
            adapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    class CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            Log.d("krishnaa","size"+arr.size());
            return arr.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView=getLayoutInflater().inflate(R.layout.customlayout,null);

            try {
                im=convertView.findViewById(R.id.imgbtn);
                tvname=convertView.findViewById(R.id.txtvname);
                tvcontent=convertView.findViewById(R.id.txtvcontent);
                tvvisited=convertView.findViewById(R.id.txtvisited);
                tvtag=convertView.findViewById(R.id.txtvtag);
                mImageBitmap = MediaStore.Images.Media.getBitmap(Feed.this.getContentResolver(), Uri.parse(arr.get(position).img));
                Log.d("krishnaa","img-"+mImageBitmap.toString());
                im.setImageBitmap(mImageBitmap);



            } catch (IOException e) {
                e.printStackTrace();
            }
            tvname.setText("Name: "+arr.get(position).name);
            tvcontent.setText("Caption: "+arr.get(position).caption);
            tvtag.setText("Tag: "+arr.get(position).tag);
            tvvisited.setText("Liked By: "+arr.get(position).ct);
            return convertView;


        }
    }

}
