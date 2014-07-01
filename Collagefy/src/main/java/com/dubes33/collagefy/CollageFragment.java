package com.dubes33.collagefy;

import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jace Wardell on 11/7/13.
 */
public class CollageFragment extends Fragment
{
    /*A subclass of Fragment that contains a view group that shows the
    pictures from the collage library that have been added to the collage. Pictures in the collage
    can be implemented as ImageView instances with the pictures assigned to them. The user
    should be able to move the picture around the collage by dragging. The picture should also be
    resizable in an aspect-ratio-preserving way by using a two-ï¬nger pinch gesture.*/
    public CollageViewGroup collageViewGroup = null;
    private float xPos;
    private float yPos;
    private float xLast1;
    private float xLast2;
    private float yLast1;
    private float yLast2;
    private boolean imagesSelected;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        collageViewGroup = new CollageViewGroup(getActivity());

        imagesSelected = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return collageViewGroup;
    }

    public void addToCollage(final ImageObject imageObject)
    {
        imageObject.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                int action = motionEvent.getAction();
                int pointerCount = motionEvent.getPointerCount();

//                xLast1 = imageObject.left + imageObject.getWidth() * 0.5f;
//                yLast1 = imageObject.top + imageObject.getHeight() * 0.5f;
//                xLast2 = imageObject.left + imageObject.getWidth() * 0.5f;
//                yLast2 = imageObject.top + imageObject.getHeight() * 0.5f;

                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                {
                    xPos = motionEvent.getRawX() - (getResources().getDisplayMetrics().widthPixels / 3);
                    yPos = motionEvent.getRawY() - (getResources().getDisplayMetrics().heightPixels / 8);
                }
                else
                {
                    xPos = motionEvent.getRawX();
                    yPos = motionEvent.getRawY() - (getResources().getDisplayMetrics().heightPixels / 15);
                }

                if (action == MotionEvent.ACTION_MOVE && pointerCount == 1)
                {
                    imageObject.setImagePosition(xPos - imageObject.width * 0.5f,yPos - imageObject.height * 0.5f,
                            xPos + imageObject.width * 0.5f, yPos + imageObject.height * 0.5f);

                    collageViewGroup.onLayout(true, 0, 0, 0, 0);
                }
                else if(action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP && pointerCount == 2)
                {
//                    float x1 = Math.abs(motionEvent.getX(0));
//                    float y1 = Math.abs(motionEvent.getY(0));
//                    float x2 = Math.abs(motionEvent.getX(1));
//                    float y2 = Math.abs(motionEvent.getY(1));

                    imageObject.setImageSize(Math.abs(motionEvent.getX(0) - motionEvent.getX(1)),
                            Math.abs(motionEvent.getY(0) - motionEvent.getY(1)));
                }

                return true;
            }
        });

        float minScreenDim = (Math.min(collageViewGroup.getWidth() * 0.25f,
                collageViewGroup.getHeight() * 0.25f));

        if(imageObject.width >= imageObject.height)
            imageObject.setImageSize(minScreenDim, imageObject.height *
                    (minScreenDim/imageObject.width));
        else
            imageObject.setImageSize(imageObject.width * (minScreenDim/imageObject.height),minScreenDim);

        imageObject.setImagePosition((collageViewGroup.getWidth() - imageObject.width) * 0.5f,
                (collageViewGroup.getHeight() - imageObject.height) * 0.5f,
                (collageViewGroup.getWidth() + imageObject.width) * 0.5f,
                (collageViewGroup.getHeight() + imageObject.height) * 0.5f);

        collageViewGroup.addView(imageObject);
    }

    public void removeFromCollage(ImageObject imageObject)
    {
        for(int i = 0; i < collageViewGroup.getChildCount(); i++)
        {
            ImageView child = (ImageView) collageViewGroup.getChildAt(i);
            Bitmap bitmap = ((BitmapDrawable)child.getDrawable()).getBitmap();

            if(bitmap == imageObject.image)
            {
                collageViewGroup.removeView(child);
            }
        }
    }

    public void highLightImage(int position)
    {
        ArrayList<ImageObject> children = PictureCollageDataModel.getInstance().libraryContent;
        int size = children.size();

        for(int i = 0; i < size; i++)
        {
            ImageObject child = (ImageObject) children.get(i);

            if(child.getAlpha() == 1.0f && i == position)
                child.setAlpha(0.5f);
            else if(children.get(i).getAlpha() == 0.5f && i == position)
                child.setAlpha(1.0f);
            else
                child.setAlpha(1.0f);
        }
    }
}