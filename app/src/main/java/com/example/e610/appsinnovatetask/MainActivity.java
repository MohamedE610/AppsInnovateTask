package com.example.e610.appsinnovatetask;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

/***  this Button to do these tasks
  1.Create a Login screen using Facebook SDK with a "Remember Me" option
  2.Create a screen to show facebook friends *User fullname and UserPicture
 5.Image caching  ***/
    Button facebookBtn;

    /***  this Button to do these tasks
        3.Load phone contacts   ***/
    Button loadContactsBtn;

    /***  this Button to do these tasks
     4.Maps Activity shows random locations on Map GPS and shows and change the label ***/
    Button mapBtn;

    /***  this Button to do these tasks
         8.Share Photo to Facebook ***/
    Button shareBtn;

    /***  this Button to do these tasks
     6.Integrate with Phone calendar to add new event ***/
    Button calendarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /***   1.Create a Login screen using Facebook SDK with a "Remember Me" option
         2.Create a screen to show facebook friends *User fullname and UserPicture
         5.Image caching
         ***/
        facebookBtn=(Button)findViewById(R.id.fblogin_btn);
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,FacebookLoginActivity.class);
                startActivity(intent);
            }
        });


        /***  3.Load phone contacts  ***/
        loadContactsBtn=(Button)findViewById(R.id.load_contacts_btn);
        loadContactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        contactsList=loadMyContacts();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        Intent intent=new Intent(MainActivity.this,ContactsActivity.class);
                        intent.putStringArrayListExtra("contactsList",contactsList);
                        startActivity(intent);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });


/*** 4.Maps Activity shows random locations on Map GPS and shows and change the label  ***/
        mapBtn=(Button)findViewById(R.id.map_btn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MapsActivity.class));
            }
        });

        /***   8.Share Photo to Facebook   ***/
        shareBtn=(Button)findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken accessToken= AccessToken.getCurrentAccessToken();
                if(accessToken!=null && !accessToken.getPermissions().contains("publish_actions")) {
                    LoginManager.getInstance().logInWithPublishPermissions(MainActivity.this, Arrays.asList("publish_actions"));
                    AccessToken.refreshCurrentAccessTokenAsync();
                }
                selectImageToShareFacebook();
               /* ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://www.facebook.com"))
                        .build();
                ShareDialog.show(MainActivity.this,shareLinkContent);
                //LoginManager.getInstance().logOut;*/
            }
        });


        /***  6.Integrate with Phone calendar to add new event   ***/
        calendarBtn=(Button)findViewById(R.id.cal_btn);
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEventToCalendar();
            }
        });


    }


    /***  6.Integrate with Phone calendar to add new event   ***/
    private void addNewEventToCalendar() {

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2017,11, 11, 8, 30);
        Calendar endTime = Calendar.getInstance();
        endTime.set(2012,11, 11, 9, 0);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, "Meeting")
                .putExtra(CalendarContract.Events.DESCRIPTION, "About Work")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Company Location")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, "example1@example.com,example2@example.com");
        startActivity(intent);
    }


    ArrayList<String> contactsList;
    /***  3.Load phone contacts  ***/
    private ArrayList<String> loadMyContacts() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        ArrayList<String> List=new ArrayList<>();
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();
            String  str=name+"\n"+phoneNumber;
            List.add(str);
        }

        phones.close();

        return List;
    }


    /***   8.Share Photo to Facebook   ***/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /***   8.Share Photo to Facebook   ***/
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    /***   8.Share Photo to Facebook
     *  open dialog to make you choose between "Take Photo" or "Choose from Gallery" ***/
    int REQUEST_CAMERA = 101, SELECT_FILE = 102;
    private void selectImageToShareFacebook() {
        Log.w("Select Image", " TWO");
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select profile Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /***   8.Share Photo to Facebook
      Handle Gallery Result ***/
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);
        Bitmap thumbnail;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        thumbnail = BitmapFactory.decodeFile(selectedImagePath, options);
        ShareDialog(thumbnail);
    }
    /***   8.Share Photo to Facebook
     * handle Capture Image Result***/
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ShareDialog(thumbnail);
    }

    /***   8.Share Photo to Facebook
     * handle share operation .
     * This method is used to share Image on facebook timeline.
     * ***/
    public void ShareDialog(Bitmap imagePath){
        if(ShareDialog.canShow(SharePhotoContent.class)) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(imagePath)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            ShareDialog.show(this,content);
        }else
            Toast.makeText(this,"Please, Login from \n \"Facebook Login Task\" Button",Toast.LENGTH_LONG).show();
    }


}
