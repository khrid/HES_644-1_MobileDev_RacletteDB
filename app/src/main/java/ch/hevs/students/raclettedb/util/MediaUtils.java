package ch.hevs.students.raclettedb.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;


public class MediaUtils {
    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-MediaUtils";
    private static final int PICTURE_CAMERA = 1;
    private static final int PICTURE_GALLERY = 2;

    public static final String TARGET_CHEESES = "cheeses";
    public static final String TARGET_SHIELINGS = "shielings";

    private Activity activity;


    public MediaUtils(Activity activity) {
        this.activity = activity;
    }

    public void selectImage() {
        try {
            String[] options = {activity.getString(R.string.choose_from_gallery),
                    activity.getString(R.string.cancel)};
            if(hasCamera()) {
                Log.d(TAG, "Device has camera, adding option");
                options = new String[]{
                        activity.getString(R.string.take_picture),
                        activity.getString(R.string.choose_from_gallery),
                        activity.getString(R.string.cancel)};
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(activity.getString(R.string.selection_option));
            String[] finalOptions = options;
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (finalOptions[item].equals(activity.getString(R.string.take_picture))) {
                        dialog.dismiss();
                        takePicture();
                    } else if (finalOptions[item].equals(activity.getString(R.string.choose_from_gallery))) {
                        dialog.dismiss();
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(pickPhoto, PICTURE_GALLERY);
                    } else if (finalOptions[item].equals(activity.getString(R.string.cancel))) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            Toast.makeText(activity, activity.getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                //...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "ch.hevs.students.raclettedb.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, PICTURE_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public File getImageFile() {
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFiles[] = storageDir.listFiles();

        if (imageFiles == null || imageFiles.length == 0) {
            return null;
        }

        File lastModifiedFile = imageFiles[0];
        for (int i = 1; i < imageFiles.length; i++) {
            if (lastModifiedFile.lastModified() < imageFiles[i].lastModified()) {
                lastModifiedFile = imageFiles[i];
            }
        }
        return lastModifiedFile;
    }

    public File copyToLocalStorage(Bitmap fileToSave) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //File file = new File (storageDir, imageFileName);
        File file = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // direct
        );
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            fileToSave.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public String saveToFirebase(String target, Bitmap fileToSave) throws IOException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final String[] url = new String[1];

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "";

        StorageReference ref = storageRef.child("images/"+target+"/" + imageFileName + ".jpg");

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            fileToSave.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ref.getName();
    }

    public void getFromFirebase(String target, String ref, Context ctx, ImageView iv) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images").child(target).child(ref);
        Log.d(TAG, imageRef.getName());


        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, uri.toString());
                Glide.with(ctx )
                        .load(uri.toString())
                        .into(iv);
            }
        });

    }

    public boolean hasCamera() {
        PackageManager packageManager = activity.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}
