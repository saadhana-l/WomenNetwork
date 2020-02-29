package com.example.womennetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;

public class Post extends AppCompatActivity {
    Button click,sub;
    private String mCurrentPhotoPath;
    ImageView mImageView;
    private Bitmap mImageBitmap;
    DatabaseReference db,db2;
    EditText etname,etph,etcont,etcaption,ettag;
    Blog blog;
    String email;
    ArrayList<Blog>arr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        click=findViewById(R.id.btnclick);
        mImageView=findViewById(R.id.img);
        sub=findViewById(R.id.btnsubmit);
        etcont=findViewById(R.id.etcontent);
        etph=findViewById(R.id.etphone);
        etname=findViewById(R.id.etname);
        etcaption=findViewById(R.id.etcaption);
        ettag=findViewById(R.id.ettag);
        arr=new ArrayList<>();
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

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Post.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        ||ActivityCompat.checkSelfPermission(Post.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("krishnaaa","entered");
                    ActivityCompat.requestPermissions(Post.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
                }
                else
                {    Log.d("krishnaaa","entered other one");
                    dispatchTakePictureIntent();
                }
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                blog=new Blog(etname.getText().toString(),etph.getText().toString(),mCurrentPhotoPath,etcont.getText().toString(),ettag.getText().toString(),etcaption.getText().toString());
                if(!arr.contains(blog))
                {
                    arr.add(blog);
                    Log.d("krishnaaa2","adding "+blog.name);
                }

                blog.pid = db.push().getKey();
                db.child(blog.pid).setValue(blog);
                db2 = FirebaseDatabase.getInstance().getReference("db2").child(blog.tag);
                db2.child(blog.pid).setValue(blog);
                Intent i=new Intent(Post.this,Feed.class);
                LinkedHashSet<Blog> hashSet = new LinkedHashSet<>(arr);

                arr = new ArrayList<>(hashSet);
                Collections.sort(arr);


                Bundle args = new Bundle();
                args.putSerializable("arr",(Serializable)arr);
                Log.d("krishnaa1",arr.get(0).name);
                i.putExtra("BUNDLE",args);
                startActivity(i);
                arr.remove(blog);


            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==101) {
            if(grantResults.length>1)
            {
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    Log.d("krishnaaa","camgr");
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d("krishnaaa","camnotgr");
                    Toast.makeText(this, "camera permission not granted", Toast.LENGTH_LONG).show();
                }
                if(grantResults[1]==PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "storage permission granted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, "storage permission not granted", Toast.LENGTH_LONG).show();
            }





        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI;
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("checkk1", ex.getStackTrace()+" ");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d("checkk2","not null");
                try {
                    photoURI = FileProvider.getUriForFile(Post.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    Log.d("checkk","going to take"+photoURI);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, 1);
                    Log.d("checkk","took");

                }
                catch(Exception e)
                {
                    Log.d("checkk",e+" ");
                }

            }
            else
                Log.d("checkk","null");

        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//       String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES);
//        //String imageFileName="pic1";
//        Log.d("checkk",imageFileName+"  "+storageDir);
//        File image = File.createTempFile(
//                imageFileName,  // prefix
//                ".jpg",      // suffix
//                storageDir     // directory
//        );


        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            email = user.getEmail();
            email = email.substring(0, email.indexOf('@'));
            int n=new Random().nextInt(1000);
            email+=n;
        }


            File image=new File(storageDir,email+".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Log.d("checkk",mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("checkk", "entered resbeg");
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Log.d("checkk", "entered res");
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                Log.d("krishnaa","mcurphoto="+mCurrentPhotoPath);
                Log.d("krishnaa","img first="+mImageBitmap.toString());
                mImageView.setImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                Intent i=new Intent(Post.this,Feed.class);
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
        return true;
    }

}

class Blog implements Serializable, Comparable{
    public String name;
    public String ph;
    public String pid;
    public String tag;
    public String caption;
    public int ct;

    public Blog()
    {
        ct=0;
    }

    public Blog(String name, String ph, String img, String content,String tag,String caption) {
        this.name = name;
        this.ph = ph;
        this.img = img;
        this.content = content;
        this.tag=tag;
        this.caption=caption;
        this.ct=0;
    }

    public String img;
    public String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCt() {
        return ct;
    }

    @Override
    public int compareTo(Object o) {
        Blog b=(Blog)o;
        int comparect=((Blog)b).getCt();
        /* For Ascending order*/
        return comparect-this.ct;
    }
}
