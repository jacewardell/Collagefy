package com.dubes33.collagefy;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.Date;
import java.util.Random;

/**
 * Created by Jace Wardell on 11/9/13.
 */
public class ImageObject extends ImageView
{
    public float left, top, right, bottom, width, height;
    public String date, imageSize, imagePath;
    public Bitmap image;
    public boolean inCollage;
    public int id;

    public ImageObject(Context context, float left, float top, float right, float bottom, String date,
                       Bitmap image, boolean inCollage, String imagePath)
    {
        super(context);

        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        width = right - left;
        height = bottom - top;
        this.image = image;
        this.date = date;
        this.imageSize = Integer.toString((image.getWidth() * image.getHeight()) / 1000) + "k px";
        this.inCollage = inCollage;
        this.imagePath = imagePath;
        setImageBitmap(image);
    }

    public void setImagePosition(float left, float top, float right, float bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void setImageSize(float width, float height)
    {
        right = left + width;
        bottom = top + height;

        this.width = width;
        this.height = height;
    }
}
