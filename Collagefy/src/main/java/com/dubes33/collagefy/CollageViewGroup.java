package com.dubes33.collagefy;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by Jace Wardell on 11/10/13.
 */
public class CollageViewGroup extends ViewGroup
{
    public CollageViewGroup(Context context)
    {
        super(context);
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4)
    {
        int collageChildrenCount = getChildCount();
        for(int j = 0; j < collageChildrenCount; j++)
        {
            ImageObject child = (ImageObject) getChildAt(j);
            child.layout((int)child.left, (int)child.top, (int)child.right, (int)child.bottom);
        }
    }
}
