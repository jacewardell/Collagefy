package com.dubes33.collagefy;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Picture;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity
{
    /*Picture Collage Data Model: Contains the pictures that are in the collage library, as well as
    the locations and sizes of those pictures within the collage.*/
    private FrameLayout libraryLayout = null;
    private CollageLibraryFragment libraryFragment = null;

    private FrameLayout collageLayout = null;
    private CollageFragment collageFragment = null;

    private ImageView imageView = null;
    private final int CAMERA_ID = 20;
    private final int ADD_ID = 21;
    private String imageDate = null;

    PictureCollageDataModel pictureCollageDataModel = null;
    ArrayList<Bitmap> library = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        library = new ArrayList<Bitmap>();

        LinearLayout mainLayout = new LinearLayout(this);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mainLayout.setOrientation(LinearLayout.VERTICAL);
        else
            mainLayout.setOrientation(LinearLayout.HORIZONTAL);

        setContentView(mainLayout);

        libraryLayout = new FrameLayout(this);
        libraryLayout.setBackgroundColor(0xFF4D4E4E);
        libraryLayout.setId(1);

        collageLayout = new FrameLayout(this);
        collageLayout.setBackgroundColor(Color.BLACK);
        collageLayout.setId(2);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {

            mainLayout.addView(collageLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 2));
            mainLayout.addView(libraryLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        }
        else
        {
            mainLayout.addView(libraryLayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            mainLayout.addView(collageLayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
        }

        libraryFragment = new CollageLibraryFragment();
        libraryFragment.setOnImageSelectedListener(new CollageLibraryFragment.OnImageSelectedListener()
        {
            @Override
            public void onImageSelected(int row)            {
                collageFragment.highLightImage(row);
            }
        });

        libraryFragment.set_onImageAddedOrRemovedListener(new CollageLibraryFragment.OnImageAddedOrRemovedListener()
        {
            @Override
            public void onImageAddedOrRemoved(ImageObject imageObject, boolean addImage)
            {
                if(addImage || imageObject.inCollage)
                    collageFragment.addToCollage(imageObject);
                else
                    collageFragment.removeFromCollage(imageObject);
            }
        });

        collageFragment = new CollageFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(libraryLayout.getId(), libraryFragment);
        transaction.add(collageLayout.getId(), collageFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem cameraItem = menu.add(Menu.NONE, CAMERA_ID, Menu.NONE, "");
        cameraItem.setIcon(android.R.drawable.ic_menu_camera);
        cameraItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

//        MenuItem addItem = menu.add(Menu.NONE, ADD_ID, Menu.NONE, "");
//        addItem.setIcon(android.R.drawable.ic_menu_add);
//        addItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == CAMERA_ID)
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmss");
            String date = df.format(new Date());
            imageDate = date;
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment
                    .DIRECTORY_PICTURES) + "/" + date + ".jpg");
            Intent captureImageIntent = new Intent();
            captureImageIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            startActivityForResult(captureImageIntent, CAMERA_ID);
            return true;
        }
//        else if(item.getItemId() == ADD_ID)
//        {
//            Intent addFromGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media
//                    .INTERNAL_CONTENT_URI);
//            startActivityForResult(addFromGalleryIntent, ADD_ID);
//            return true;
//        }
        else
            return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == CAMERA_ID && resultCode == RESULT_OK)
        {
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES) + "/" + imageDate + ".jpg");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            Bitmap image = BitmapFactory.decodeFile(imageFile.getPath(), options);

            if(image != null)
                PictureCollageDataModel.getInstance().addImage(this, image, imageFile.getPath());
        }
//        else if(requestCode == ADD_ID && resultCode == RESULT_OK)
//        {
//            Uri selectedImage = data.getData();
//
//            File imageFile = new File(Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_PICTURES) + selectedImage.getPath());
//
//            Bitmap image = BitmapFactory.decodeFile(imageFile.getPath());
//
//            PictureCollageDataModel.getInstance().addImage(this, image);
//        }

        ArrayList<String> imagePaths = new ArrayList<String>();
        for(ImageObject imageObject : PictureCollageDataModel.getInstance().libraryContent)
            imagePaths.add(imageObject.imagePath);

        try {
            FileOutputStream outputStream = openFileOutput("imagePaths.dubes33", MODE_PRIVATE);
            ObjectOutputStream writer = new ObjectOutputStream(outputStream);
            writer.writeObject(imagePaths);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        ArrayList<String> imagePaths = new ArrayList<String>();
        for(ImageObject imageObject : PictureCollageDataModel.getInstance().libraryContent)
            imagePaths.add(imageObject.imagePath);

        try {
            FileOutputStream outputStream = openFileOutput("imagePaths.dubes33", MODE_PRIVATE);
            ObjectOutputStream writer = new ObjectOutputStream(outputStream);
            writer.writeObject(imagePaths);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        ArrayList<String> imagePaths = new ArrayList<String>();
//        ArrayList<Pair<String, String>> imageObjects = new ArrayList<Pair<String, String>>();

        collageFragment.collageViewGroup.removeAllViews();

        PictureCollageDataModel.getInstance().libraryContent = new ArrayList<ImageObject>();

        try {
            FileInputStream inputStream = openFileInput("imagePaths.dubes33");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            imagePaths = (ArrayList<String>) reader.readObject();

            int size = imagePaths.size();
            for(String imagePath : imagePaths)
            {
                File imageFile = new File(imagePath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                Bitmap image = BitmapFactory.decodeFile(imageFile.getPath(), options);

                if(image != null)
                {
                    PictureCollageDataModel.getInstance().addImage(this, image, imagePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}