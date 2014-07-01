package com.dubes33.collagefy;

import android.app.ListFragment;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.math.MathContext;
import java.util.ArrayList;

/**
 * Created by Jace Wardell on 11/7/13.
 */

    /*A subclass of ListFragment that contains a list view that shows a
    list of all pictures associated with the current collage. This collection may include pictures that
    have not yet been added to the collage. Each row in the list represents a picture and must
    exhibit a thumbnail of the picture, the size in pixels of the picture, the date the picture was
    taken or otherwise created, and whether the picture has already been added to the collage. If
    the picture has not been added to the collage, show a â€œ+â€ icon. Otherwise, show a â€œ-â€œ icon.
    Tapping the â€œ+â€ icon should add the picture to the collage, initially centered in the collage, and
    the largest dimension of the picture taking up 25% of the shortest dimension of the collage
    area. Tapping the â€œ-â€œ icon should remove the picture from the collage. Tapping the row should
    highlight the picture in the collage in some way to allow the user to ï¬nd it (putting a border
    around the picture, initiating an animation on the picture that brightens it for a short period,
    etc). An add button should be contained in the applicationâ€™s action menu to allow the user to
    add a new picture to the library.*/
public class CollageLibraryFragment extends ListFragment implements ListAdapter
{
    private OnImageSelectedListener _onImageSelectedListener = null;
    private OnImageAddedOrRemovedListener _onImageAddedOrRemovedListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setListAdapter(this);

        PictureCollageDataModel.getInstance().setOnLibraryChangedListener(new PictureCollageDataModel.OnLibraryChangedListener()
        {
            @Override
            public void onLibraryChanged(ArrayList<ImageObject> libraryContent, boolean addImage)
            {
                if(addImage)
                    getListView().invalidateViews();
                else
                    getListView().invalidateViews();
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        getListView().invalidateViews();

        if(v.getAlpha() == 1.0f)
        {
            int size = getListView().getChildCount();
            for(int j = 0; j < size; j++)
                getListView().getChildAt(j).setAlpha(1.0f);

            v.setAlpha(0.5f);
        }
        else if(v.getAlpha() == 0.5f)
            v.setAlpha(1.0f);

        if(_onImageSelectedListener != null)
            _onImageSelectedListener.onImageSelected(position);
    }

    public interface OnImageSelectedListener
    {
        public void onImageSelected(int position);
    }

    public void setOnImageSelectedListener(OnImageSelectedListener listener)
    {
        _onImageSelectedListener = listener;
    }

    public interface OnImageAddedOrRemovedListener
    {
        public void onImageAddedOrRemoved(ImageObject imageObject, boolean addImage);
    }

    public void set_onImageAddedOrRemovedListener(OnImageAddedOrRemovedListener listener)
    {
        _onImageAddedOrRemovedListener = listener;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        final LinearLayout listItem = new LinearLayout(getActivity());
        listItem.setOrientation(LinearLayout.HORIZONTAL);
        listItem.setBackgroundColor(0xFF003263);

        listItem.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN && view.getAlpha() == 1.0f)
                {
                    int size = getListView().getChildCount();
                    for(int j = 0; j < size; j++)
                        getListView().getChildAt(j).setAlpha(1.0f);

                    view.setAlpha(0.5f);
                }
                else if(action == MotionEvent.ACTION_DOWN && view.getAlpha() == 0.5f)
                    view.setAlpha(1.0f);

                return false;
            }
        });

        final ImageObject imageObject = PictureCollageDataModel.getInstance().libraryContent.get(i);
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(
                PictureCollageDataModel.getInstance().libraryContent.get(i).image,
                (int)(getResources().getDisplayMetrics().densityDpi * 0.50),
                (int)(getResources().getDisplayMetrics().densityDpi * 0.50));

        final ImageView imageView = new ImageView(getActivity());
        imageView.setImageBitmap(thumbnail);

        TextView imageSize = new TextView(getActivity());
        imageSize.setText(imageObject.imageSize);
        imageSize.setTextColor(0xFF0077F0);
        imageSize.setGravity(Gravity.CENTER);

        TextView imageDate = new TextView(getActivity());
        imageDate.setText(imageObject.date);
        imageDate.setTextColor(0xFF0077F0);
        imageDate.setGravity(Gravity.CENTER);

        final Button button = new Button(getActivity());
        button.setGravity(Gravity.CENTER);
        button.setFocusable(false);

        if(imageObject.inCollage)
            button.setText("-");
        else
            button.setText("+");

        button.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if(button.getText() == "+")
                    {
                        Bitmap bitmap = ((BitmapDrawable)imageObject.getDrawable()).getBitmap();
                        button.setText("-");
                        imageObject.inCollage = true;
                        imageObject.setImageSize(bitmap.getWidth(), bitmap.getHeight());
                        if(_onImageAddedOrRemovedListener != null)
                            _onImageAddedOrRemovedListener.onImageAddedOrRemoved(imageObject, true);
                    }
                    else
                    {
                        button.setText("+");
                        imageObject.inCollage = false;
                        listItem.setAlpha(1.0f);
                        imageObject.setAlpha(1.0f);
                        if(_onImageAddedOrRemovedListener != null)
                            _onImageAddedOrRemovedListener.onImageAddedOrRemoved(imageObject, false);
                    }
                }
                return true;
            }
        });

        listItem.addView(imageView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 2));
        listItem.addView(imageSize, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1));
        listItem.addView(imageDate, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1));
        listItem.addView(button, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0));

        return listItem;
    }

    @Override
    public int getCount() {
        return PictureCollageDataModel.getInstance().libraryContent.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return PictureCollageDataModel.getInstance().libraryContent.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
}
