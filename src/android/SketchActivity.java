package in.co.geekninja.plugin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SketchActivity extends Activity implements View.OnClickListener {


    private static final String TAG = "Draw";
    private FingerDrawView canvas;
    View clear, done;
    CircleImageView pick;
    private int paintColor = 0xFF660000;
    ColorPicker cp;
    private int selectedColorRGB;
    private int dest=-12;
    public static final int REQUEST_CODE_DRAW = 851;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        clear = findViewById(R.id.clear_drawing);
        done = findViewById(R.id.done_drawing);
        cp= new ColorPicker(SketchActivity.this, 255, 255, 255);
        pick = (CircleImageView) findViewById(R.id.pick_color);
        pick.setImageDrawable(new ColorDrawable(paintColor));
        dest=getIntent().getIntExtra("dest",-12);
        int type = getIntent().getIntExtra("requestCode", 0);
        clear.setOnClickListener(this);
        done.setOnClickListener(this);
        pick.setOnClickListener(this);
        canvas = (FingerDrawView) findViewById(R.id.drawing_canvas);
        Log.i("Canvas", canvas.getWidth() + "," + canvas.getHeight());
        canvas.setDrawingCacheEnabled(true);
       if (type == REQUEST_CODE_DRAW) {
           try {
               Uri u = getIntent().getData();
               if (getIntent().getBooleanExtra("isWebUrl",false))
               {
                   Target target=new Target() {
                       @Override
                       public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                           Drawable d = new BitmapDrawable(getResources(), bitmap);
                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                               canvas.setBackground(d);
                           }else{
                               canvas.setBackgroundDrawable(d);
                           }

                       }

                       @Override
                       public void onBitmapFailed(Drawable errorDrawable) {

                       }

                       @Override
                       public void onPrepareLoad(Drawable placeHolderDrawable) {

                       }
                   };
                   Picasso.with(SketchActivity.this).load(u).into(target);

               }
               else{
               File myFile = null;
               if (u != null) {

                   Bitmap bmp = decodeSampledBitmapFromFile(getPath(u, SketchActivity.this), 512, 512);
                   if (bmp == null) {
                       String path = getPath(u, SketchActivity.this);
                       if (path == null)
                           path = u.getPath();
                       myFile = new File(path);
                       bmp = decodeSampledBitmapFromFile(myFile.getPath(), 512, 512);

                   }
                   Drawable d = new BitmapDrawable(getResources(), bmp);
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                       canvas.setBackground(d);
                   }else {
                       canvas.setBackgroundDrawable(d);
                   }
                   if (myFile != null) {
                       if (myFile.delete()){
                           Log.i(TAG,"File deleted");
                       }
                   }
               }
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
        else
       {
           Drawable d = new ColorDrawable(Color.WHITE);
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
               canvas.setBackground(d);
           }else {
               canvas.setBackgroundDrawable(d);
           }
       }
        cp.setCallbackListener(new ColorPicker.Callback() {
            @Override
            public void onSet(int color) {
                selectedColorRGB = color;
                paintColor=color;
                canvas.setColor(selectedColorRGB);
                pick.setImageDrawable(new ColorDrawable(paintColor));
                cp.dismiss();
            }
        });

    }
    public   String getPath(Uri uri, Context context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        Log.v("ImagePath", s);
        return s;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_drawing:
                canvas.Clear();
                break;
            case R.id.done_drawing:
                canvas.buildDrawingCache();
                Bitmap bm = canvas.getDrawingCache();
                File file= WriteBmpToFile(bm);
                Intent result=new Intent();
                result.putExtra("path",file.getPath());
                result.putExtra("dest", dest);
                setResult(Activity.RESULT_OK, result);
                bm.recycle();
                finish();
                break;
            case R.id.pick_color:
                cp.show();
                break;
        }
    }
    public   File WriteBmpToFile(Bitmap bmp) {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/.kyc";

        File dir = new File(file_path);
        if (!dir.exists())
            if(dir.mkdirs())
                Log.i(TAG,"Directories are created");
        File file = new File(dir, "kyc_" + Calendar.getInstance().getTimeInMillis() + ".jpg");

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {

            try {
                if (fOut != null) {
                    fOut.flush();
                    fOut.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public   Bitmap decodeSampledBitmapFromFile(String path,
                                                int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }


    public   int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
