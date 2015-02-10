package com.tonyjhuang.patio;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;

public class Patio extends LinearLayout implements
        View.OnClickListener,
        PatioThumbnail.OnRemoveClickListener {

    /**
     * Constants
     */
    public final static String TAG = Patio.class.getSimpleName();

    /**
     * Variables
     */
    private final int WIDGET_LAYOUT_RES_ID = R.layout.patio_layout;
    private Context mContext;
    private PatioCallbacks mListener;
    private PatioThumbnail mPatioThumbnail;
    private String mTakePicturePath;

    //Resources
    private float mThumbnailContainerHeight;

    /**
     * Controls
     */
    //Actions
    public Button mTakePicture;
    public Button mAttachPicture;
    //Containers
    public FrameLayout mThumbnailsContainer;
    //Toolbars
    public LinearLayout mToolbarAddActions;

    //TODO: http://ryanharter.com/blog/2014/08/29/building-dynamic-custom-views/

    /**
     * Constructor
     */
    public Patio(Context context) {
        super(context);
        init(context, null);
    }

    public Patio(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Patio(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Lifecycle methods
     * https://speakerdeck.com/cyrilmottier/deep-dive-into-android-state-restoration
     * https://github.com/CharlesHarley/Example-Android-SavingInstanceState/blob/master/src/com/example/android/savinginstancestate/views/LockCombinationPicker.java
     * https://gist.github.com/granoeste/4037468
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState savedState = new SavedState(superState);
        savedState.setThumbnailsPath(getThumbnailUri());
        savedState.setTakePicturePath(mTakePicturePath);

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        Uri thumbnailPath = savedState.getThumbnailPath();
        String takePicturePath = savedState.getTakePicturePath();

        restoreState(thumbnailPath, takePicturePath);
    }

    /**
     * Custom methods
     */
    public void init(Context context, AttributeSet attributeSet) {
        //Setup defaults
        mContext = context;
        setOrientation(VERTICAL);

        //Local defaults
        float thumbnailsContainerPadding = mContext.getResources().getDimension(R.dimen.patio_default_thumbnails_container_padding);
        int actionsTextColor = mContext.getResources().getColor(R.color.patio_default_action_text_color);
        int thumbnailContainerBackground = mContext.getResources().getColor(R.color.patio_default_thumbnails_container_background);


        //Inflate view and setup controls
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(WIDGET_LAYOUT_RES_ID, this, true);
        //Buttons
        mTakePicture = (Button) findViewById(R.id.patio_action_take_picture);
        mAttachPicture = (Button) findViewById(R.id.patio_action_attach_picture);
        //Containers
        mThumbnailsContainer = (FrameLayout) findViewById(R.id.patio_thumbnail_container);
        //Toolbars
        mToolbarAddActions = (LinearLayout) findViewById(R.id.patio_add_actions_toolbar);

        //Set actions listeners
        if (!isInEditMode()) {
            mTakePicture.setOnClickListener(this);
            mAttachPicture.setOnClickListener(this);
        }

        //Get defined attributes
        if (attributeSet != null) {
            TypedArray a = mContext.getTheme().obtainStyledAttributes(attributeSet, R.styleable.Patio, 0, 0);
            try {
                //Local
                mThumbnailContainerHeight = a.getDimension(R.styleable.Patio_thumbnailContainerHeight, -1);
                thumbnailsContainerPadding = a.getDimension(R.styleable.Patio_thumbnailContainerPadding, thumbnailsContainerPadding);
                actionsTextColor = a.getColor(R.styleable.Patio_actionsTextColor, actionsTextColor);
                thumbnailContainerBackground = a.getColor(R.styleable.Patio_thumbnailContainerBackground, thumbnailContainerBackground);
            } finally {
                a.recycle();
            }
        }

        //Setup actions text color
        mTakePicture.setTextColor(actionsTextColor);
        mAttachPicture.setTextColor(actionsTextColor);

        //Setup thumbnails container background
        mThumbnailsContainer.setBackgroundColor(thumbnailContainerBackground);

        //Setup dimensions
        int paddingTop, paddingBottom, paddingLeft, paddingRight;
        //Thumbnail container height & width
        ViewGroup.LayoutParams layoutParams = mThumbnailsContainer.getLayoutParams();
        setThumbnailContainerLayoutParams(layoutParams.width, (int) mThumbnailContainerHeight);
        //Thumbnail container padding
        paddingTop = paddingBottom = paddingLeft = paddingRight = Float.valueOf(thumbnailsContainerPadding).intValue();
        mThumbnailsContainer.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mThumbnailContainerHeight == -1) {
            int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
            setThumbnailContainerLayoutParams(parentWidth, parentHeight);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setThumbnailContainerLayoutParams(int width, int height) {
        ViewGroup.LayoutParams layoutParams = mThumbnailsContainer.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        mThumbnailsContainer.setLayoutParams(layoutParams);
        mThumbnailContainerHeight = height;
    }

    public void restoreState(Uri thumbnailPath, String takePicturePath) {
        setThumbnail(thumbnailPath);
        mTakePicturePath = takePicturePath;
    }

    public void setThumbnail(Uri thumbnailUri) {
        showAddToolbar(false);
        removeCurrentThumbnail();

        mPatioThumbnail = new PatioThumbnail(mContext);
        mPatioThumbnail.setUri(thumbnailUri, mThumbnailsContainer.getWidth(), mThumbnailsContainer.getHeight());

        mThumbnailsContainer.addView(mPatioThumbnail);
        mPatioThumbnail.setOnRemoveClickListener(this);
    }

    public void setCallbacksListener(PatioCallbacks listener) {
        mListener = listener;
    }

    public Intent getTakePictureIntent() {
        mTakePicturePath = null;
        File pictureFile = null;
        try {
            pictureFile = PatioUtils.getNewImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (pictureFile == null)
            return null;

        mTakePicturePath = pictureFile.getAbsolutePath();
        Log.d(TAG, "Path: " + mTakePicturePath);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
        return takePictureIntent;
    }

    public Intent getAttachPictureIntent() {
        Intent attachPictureIntent = new Intent(Intent.ACTION_PICK);
        attachPictureIntent.setType("image/*");
        return attachPictureIntent;
    }

    public void handleTakePictureResult(Intent data) {
        Log.d(TAG, "File Path: " + mTakePicturePath);

        setThumbnail(Uri.fromFile(new File(mTakePicturePath)));
        PatioUtils.addNewImageToGallery(mContext, mTakePicturePath);
    }

    public void handleAttachPictureResult(Intent data) {
        Uri uri = data.getData();
        String filePath = PatioUtils.getRealPathFromURI(mContext, uri);
        Log.d(TAG, "File Path: " + filePath);

        setThumbnail(uri);
    }

    public void showAddToolbar(boolean show) {
       /* Animation animation;
        if(show) {
            animation = new SlideInAnimation(mToolbarAddActions).setDirection(Animation.DIRECTION_UP);
        } else {
            animation = new SlideOutAnimation(mToolbarAddActions).setDirection(Animation.DIRECTION_UP);
        }
        animation.animate();*/
    }

    public void removeCurrentThumbnail() {
        mThumbnailsContainer.removeView(mPatioThumbnail);
        mPatioThumbnail = null;
        showAddToolbar(true);
    }

    public Uri getThumbnailUri() {
        return mPatioThumbnail == null ? null : mPatioThumbnail.getThumbnailUri();
    }

    /**
     * OnClick buttons methods
     */
    @Override
    public void onClick(View view) {
        //Buttons
        if (view instanceof Button) {
            if (view.getId() == R.id.patio_action_take_picture) {
                mListener.onTakePictureClick();
            }
            if (view.getId() == R.id.patio_action_attach_picture) {
                mListener.onAddPictureClick();
            }
        }
    }

    @Override
    public void onRemoveClicked(PatioThumbnail thumbnail) {
        removeCurrentThumbnail();
    }

    /**
     * Interface
     */
    public interface PatioCallbacks {
        public void onTakePictureClick();

        public void onAddPictureClick();
    }

    /**
     * SavedState class for restoring view state
     */
    protected static class SavedState extends BaseSavedState {
        public Uri mThumbnailsPath;
        private String mTakePictureFilePath;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel in) {
            super(in);
            mThumbnailsPath = in.readParcelable(Uri.class.getClassLoader());
            mTakePictureFilePath = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(mThumbnailsPath, 0);
            out.writeString(mTakePictureFilePath);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        public void setThumbnailsPath(Uri thumbnailsPath) {
            mThumbnailsPath = thumbnailsPath;
        }

        public Uri getThumbnailPath() {
            return mThumbnailsPath;
        }

        public void setTakePicturePath(String takePictureFilePath) {
            mTakePictureFilePath = takePictureFilePath;
        }

        public String getTakePicturePath() {
            return mTakePictureFilePath;
        }

    }
}
