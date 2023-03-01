package com.example.getloginpassapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getloginpassapp.utils.PhotosUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sPref;
    public static String MY_PREF="GETLOGINASSAPP_PREFERENCES_FILE";
    public final static String FULLUSERNAME="fullusername";

    private final static int code=0;
    private final static int REQUEST_CONTACT = 1;
    private final static int REQUEST_PHOTO = 2;


    private Toast toast;
    private TextView tv;
    private Button buttonSendInfoToContact;
    private Button buttonGetContact;

    private ImageButton buttonSelfPhoto;
    private ImageView ivSelfPhoto;

    private Intent intentGetPhoto;

    private File selfPhotoFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv=findViewById(R.id.tvViewFullName);
        buttonSendInfoToContact = findViewById(R.id.buttonSendInfoToContact);
        buttonGetContact = findViewById(R.id.buttonGetContact);

        ivSelfPhoto = findViewById(R.id.ivSelfPhoto);
        buttonSelfPhoto = findViewById(R.id.buttonSelfPhoto);

        intentGetPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String photoFileName = getPhotoFileName();
        selfPhotoFile = getPhotoFile(photoFileName);

        boolean canTakePhoto = selfPhotoFile!= null && intentGetPhoto.resolveActivity(getPackageManager()) != null;

        buttonSelfPhoto.setEnabled(canTakePhoto);

        if (canTakePhoto){
            Uri uri = FileProvider.getUriForFile(this, "com.example.getloginpassapp.fileprovider", selfPhotoFile);
            intentGetPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        buttonSelfPhoto.setOnClickListener((view)->{
            startActivityForResult(intentGetPhoto, REQUEST_PHOTO);
        });

        String fullUserName;

        if((fullUserName=loadUserFullNameFromMyPref())==null){
            getUserFullNameFromLoginActivity();
        }else{
            tv.setText("Hi "+fullUserName);

            buttonSendInfoToContact.setOnClickListener((view)->{
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "I get up at 5:40");
                intent.putExtra(Intent.EXTRA_SUBJECT, "early report");

                Intent intentChooser = Intent.createChooser(intent, "SendReport");
                startActivity(intentChooser);
            });

            Intent intentGetContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            buttonGetContact.setOnClickListener((view)->{
                startActivityForResult(intentGetContact, REQUEST_CONTACT);
            });
        }


    }

    private String getPhotoFileName(){
        Date date = new Date();

        return "IMG_" + date.getTime() + ".jpg";
    }

    private File getPhotoFile(String fileName){
        File filesDir = getFilesDir();
        if (filesDir == null) {
            return null;
        }
        // TODO: insert check that file exists, in this case don't create new file
        return new File(filesDir, fileName);
    }

    private String loadUserFullNameFromMyPref(){
        sPref=getApplicationContext().getSharedPreferences(MY_PREF, MODE_PRIVATE);
        String fullUserName=sPref.getString(FULLUSERNAME, "");
        if(fullUserName.isEmpty()){
            return null;
        } else {
            return fullUserName;
        }
    }

    private void getUserFullNameFromLoginActivity(){
        Intent intent=new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==code){
            if(resultCode==RESULT_OK){
                String fullUserName=data.getStringExtra(FULLUSERNAME);
                saveFullUserNameImMyPref(fullUserName);
                tv.setText("Hi, "+fullUserName);
            } else {
                int duration= Toast.LENGTH_SHORT;
                if(toast!=null){
                    toast.cancel();
                }
                toast=Toast.makeText(this, "This User not found!", duration);
                toast.show();
                getUserFullNameFromLoginActivity();

            }
        }
        if (requestCode == REQUEST_CONTACT){
            if (resultCode == RESULT_OK){
                if (data != null){
                    Uri contactUri = data.getData();

                    String[] queryFields = new String[]{
                      ContactsContract.Contacts.DISPLAY_NAME
                    };

                    Cursor cursor = getContentResolver().query(contactUri, queryFields, null, null, null);
                    try {
                        if (cursor.getCount()==0){
                            return;
                        }
                        cursor.moveToFirst();
                        String name = cursor.getString(0);

                        tv.setText(name);
                    }
                    finally {
                        cursor.close();    
                    }
                }
            }
        }
        if (requestCode == REQUEST_PHOTO){
            if (requestCode == RESULT_OK || requestCode == DEFAULT_KEYS_SHORTCUT){
                 updateSelfPhotoImageView(selfPhotoFile);
            }
            else {
                Log.d("AAA", "onActivityResult: ERROR with photo");
            }
        }
    }

    private void updateSelfPhotoImageView(File photoFile){
        if (photoFile == null || !photoFile.exists()){
            ivSelfPhoto.setImageDrawable(null);
            Log.d("AAA", "updateSelfPhotoImageView: ERROR with file");
        }
        else {
            Point size = new Point();
            this.getWindowManager().getDefaultDisplay().getSize(size);

            Bitmap bitmap = PhotosUtils.getScaledBitmap(photoFile.getPath(), size.x, size.y);

            ivSelfPhoto.setImageBitmap(bitmap);
        }
    }

    private void saveFullUserNameImMyPref(String data){

        sPref=getApplicationContext().getSharedPreferences(MY_PREF, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(FULLUSERNAME, data);
        ed.commit();

    }
}