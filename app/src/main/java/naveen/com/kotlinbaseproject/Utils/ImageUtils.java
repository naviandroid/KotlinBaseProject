package naveen.com.kotlinbaseproject.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import naveen.com.kotlinbaseproject.R;

/**
 * Created by naveen sharma on 17/10/16.
 */
public class ImageUtils {

    private static Uri outputUri;
    private static Activity activity = null;
    private static ImageUtils imageUtils;
    private static ImageSelectCallback imageSelectCallback;
    private static int reqCode;
    private boolean onlyCamera;
    private boolean onlyGallery;
    private boolean doCrop;
    private static int width;
    private static int height;
    private static Uri inputUri;

    private ImageUtils(ImageSelect.Builder builder) {
        activity = builder.activity;
        reqCode = builder.reqCode;
        imageSelectCallback = builder.imageSelectCallback;
        this.onlyCamera = builder.onlyCamera;
        this.onlyGallery = builder.onlyGallery;
        this.doCrop = builder.doCrop;
        width = builder.width;
        height = builder.height;
    }

    public static class ImageSelect {

        public static class Builder {
            private ImageSelectCallback imageSelectCallback;
            private Activity activity;
            private int reqCode;
            private boolean onlyCamera;
            private boolean onlyGallery;
            private boolean doCrop;
            private int width, height;

            public Builder(Activity activity, ImageSelectCallback imageSelectCallback, int reqCode) {
                this.activity = activity;
                this.reqCode = reqCode;
                this.imageSelectCallback = imageSelectCallback;
            }

            public Builder onlyCamera(boolean onlyCamera) {
                this.onlyCamera = onlyCamera;
                return this;
            }

            public Builder onlyGallery(boolean onlyGallery) {
                this.onlyGallery = onlyGallery;
                return this;
            }

            public Builder crop() {
                this.doCrop = true;
                return this;
            }

            public Builder aspectRatio(int width, int height) {
                this.width = width;
                this.height = height;
                this.doCrop = true;
                return this;
            }

            public void start() {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "Write External storage Permission not specified", Toast.LENGTH_LONG).show();
                    return;
                }
                imageUtils = new ImageUtils(this);
                if (imageUtils.onlyCamera) {
                    captureCameraImage();
                } else if (imageUtils.onlyGallery) {
                    selectGalleryImage();
                } else {
                    selectImageDialog();
                }
            }
        }
    }

    public interface ImageSelectCallback {
        void onImageSelected(String imagePath, int resultCode);
    }

    private static void selectImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.choose_image_source));
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        selectGalleryImage();
                        break;
                    case 1:
                        captureCameraImage();
                        break;
                }
            }
        });
        builder.show();
    }

    private static void captureCameraImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile;
        imageFile = new File(storageDir, String.valueOf(System.currentTimeMillis()) + ".jpg");
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            inputUri = Uri.fromFile(imageFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity, activity.getString(R.string.file_provider_authority_), imageFile));
            //  takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, inputUri);
            activity.startActivityForResult(takePictureIntent, Const.INSTANCE.getCAMERA_REQ());
        }
    }

    private static void selectGalleryImage() {
        Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent1.setType("image/*");
        activity.startActivityForResult(intent1, Const.INSTANCE.getGALLERY_REQ());
    }

    public static void activityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.INSTANCE.getGALLERY_REQ() && resultCode == Activity.RESULT_OK) {
            inputUri = data.getData();
            if (imageUtils.doCrop) {
                File file = new File("" + activity.getCacheDir() + System.currentTimeMillis() + ".jpg");
                outputUri = Uri.fromFile(file);
                Crop();
            } else {
                sendBackImagePath(inputUri, reqCode);
            }

        } else if (requestCode == Const.INSTANCE.getCAMERA_REQ() && resultCode == Activity.RESULT_OK) {
            if (imageUtils.doCrop) {
                File file = new File("" + activity.getCacheDir() + System.currentTimeMillis() + ".jpg");
                outputUri = Uri.fromFile(file);
                Crop();
            } else {
                sendBackImagePath(inputUri, reqCode);
            }

        } else if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            sendBackImagePath(outputUri, reqCode);
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(activity, "Write External storage Permission not specified", Toast.LENGTH_SHORT).show();
        }
    }

    private static void sendBackImagePath(Uri inputUri, int reqCode) {
        String path = getRealPath(activity, inputUri);
        imageSelectCallback.onImageSelected(path, reqCode);
    }

    private static void Crop() {
        if (width != 0) {
            Crop.of(inputUri, outputUri).withAspect(width, height).start(activity);
        } else
            Crop.of(inputUri, outputUri).asSquare().start(activity);
    }

    public static File bitmapToFile(Bitmap bitmap, Activity activity) {
        File f = new File(activity.getCacheDir(), System.currentTimeMillis() + ".jpg");
        try {
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        return f;
    }

    public static String getRealPath(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {

                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static Bitmap imageCompress(String picturePath) {
        return imageCompress(picturePath, 816.0f, 612.0f);
    }

    public static Bitmap imageCompress(String picturePath, float maxHeight, float maxWidth) {

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bmp = BitmapFactory.decodeFile(picturePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        } else {
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeFile(picturePath);
            bitmap = Bitmap.createScaledBitmap(bitmap, actualWidth, actualHeight, true);
            return bitmap;
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;

        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(picturePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        bmp.recycle();
        ExifInterface exif;
        try {
            exif = new ExifInterface(picturePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaledBitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        float totalPixels = width * height;
        float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static Bitmap blur(Activity context, Bitmap image) {

        float BITMAP_SCALE = 0.4f;
        float BLUR_RADIUS = 5f;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = null;
            theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);
            return outputBitmap;
        }
        return null;
    }

}
