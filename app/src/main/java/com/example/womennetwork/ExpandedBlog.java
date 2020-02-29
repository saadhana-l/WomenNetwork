package com.example.womennetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

public class ExpandedBlog extends AppCompatActivity {
    Blog b;
    private DatabaseReference db3;
    ArrayList<Blog>arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_blog);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        b= (Blog) args.getSerializable("post");
        Button button=findViewById(R.id.btninterested);
        TextView name,ph,desc;
        name=findViewById(R.id.nametv);
        desc=findViewById(R.id.contenttv);
        ph=findViewById(R.id.phtv);
        ImageView im=findViewById(R.id.imgpic);
        name.setText("Hi I Am "+b.name);
        desc.setText("Heres what Ive done! \n"+b.content);
        ph.setText("You can reach me at "+b.ph);
        arr=new ArrayList<>();
        try {
            Bitmap mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(b.img));
            Log.d("krishnaa","img:"+mImageBitmap.toString());
            im.setImageBitmap(mImageBitmap);



        } catch (IOException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db= FirebaseDatabase.getInstance().getReference("db").child(b.pid);
                b.ct++;
                Toast.makeText(getApplicationContext(),"Upvoted!",Toast.LENGTH_LONG).show();
                db.setValue(b);
                db=FirebaseDatabase.getInstance().getReference("db2").child(b.tag).child(b.pid);
                db.setValue(b);

            }
        });
        db3 = FirebaseDatabase.getInstance().getReference("db");
        db3.orderByKey().addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Blog blog = dataSnapshot.getValue(Blog.class);
                arr.add(blog);


                Log.d("krishnaaa1","adding here"+blog.name+blog.ct);

                Log.d("krishnaa","child"+blog.name);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Blog blog = dataSnapshot.getValue(Blog.class);
                Log.d("krishnaaa1",blog.pid);
                blog.ct=blog.ct-1;
                Log.d("krishnaaa1",blog.toString());
                for(int i=0;i<arr.size();i++)
                {
                    Log.d("krishnaaaa",arr.get(i).pid);
                    if(arr.get(i).pid.equals(blog.pid)) {
                        Blog b=arr.remove(i);
                        Log.d("krishnaaa1","rem"+b.pid);
                    }
                }


                Log.d("krishnaaa1",arr.indexOf(blog)+" ");
                Boolean t=arr.remove(blog);

                blog.ct++;
                arr.add(blog);


                Log.d("krishnaaa1","adding there"+blog.name+blog.ct);

                Log.d("krishnaa","child"+blog.name);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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



                Intent i=new Intent(ExpandedBlog.this,Feed.class);
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
}
