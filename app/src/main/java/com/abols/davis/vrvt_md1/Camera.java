package com.abols.davis.vrvt_md1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class Camera extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView thumbnailView;
    String mCurrentPhotoPath;
    private ImageAdapter adapter;
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){

                mView =inflater.inflate(R.layout.fragment_camera, viewGroup, false);
                return mView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        thumbnailView = mView.findViewById(R.id.thumbView);
        adapter = new ImageAdapter(mView.getContext());

        Bundle bundle = new Bundle();

        GridView gridview = mView.findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

            }
        });

        Button btn = getActivity().findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchImage(v);
            }
        });
    }


    @Override
     public void onDestroy() {
        super.onDestroy();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        this.traverse(storageDir);
    }

    public void traverse (File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory()) {
                    traverse(file);
                } else {
                    // Delete files
                    file.delete();
                }
            }
        }
    }

    public void catchImage(View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mView.getContext(),
                        "com.abols.davis.vrvt_md1.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("data", photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            thumbnailView.setImageBitmap(myBitmap);
            adapter.addThisBitmap(myBitmap);
            adapter.notifyDataSetChanged();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        ArrayList<Bitmap> mThumbs = new ArrayList<Bitmap>();

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public void addThisBitmap(Bitmap newmap) {
            mThumbs.add(newmap);
        }

        public int getCount() {
            return mThumbs.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(500, 500));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap(mThumbs.get(position));
            return imageView;
        }
    }
}

