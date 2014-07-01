package com.dubes33.collagefy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jace Wardell on 11/7/13.
 */
public class PictureCollageDataModel
{
    public ArrayList<ImageObject> libraryContent = null;
    private OnLibraryChangedListener _onLibraryChangedListener = null;
    private static PictureCollageDataModel _instance = null;

    public PictureCollageDataModel()
    {
        libraryContent = new ArrayList<ImageObject>();
    }

    public static synchronized PictureCollageDataModel getInstance()
    {
        if(_instance == null)
            _instance = new PictureCollageDataModel();
        return _instance;
    }

    public interface OnLibraryChangedListener
    {
        public void onLibraryChanged(ArrayList<ImageObject> libraryContent, boolean addImage);
    }

    public void setOnLibraryChangedListener(OnLibraryChangedListener listener)
    {
        _onLibraryChangedListener = listener;
    }

    public void addImage(MainActivity mainActivity, Bitmap image, String imagePath)
    {
        DateFormat df = new SimpleDateFormat("MM/d/yy");
        String date = df.format(new Date());

        ImageObject imageObject = new ImageObject(mainActivity, 0, 0, image.getWidth(),
                image.getHeight(), date, image, false, imagePath);

        libraryContent.add(imageObject);

        if(_onLibraryChangedListener != null)
            _onLibraryChangedListener.onLibraryChanged(libraryContent, true);
    }

    /**
     * Not implemented in this version
     * @param image
     */
    public void removeImage(Bitmap image)
    {
        libraryContent.remove(image);
    }
}