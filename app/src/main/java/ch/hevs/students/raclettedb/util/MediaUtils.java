package ch.hevs.students.raclettedb.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.ui.cheese.EditCheeseActivity;

public class MediaUtils {
    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-MediaUtils";
    private static final int PICTURE_CAMERA = 1;
    private static final int PICTURE_GALLERY = 2;

    private Activity activity;

    public MediaUtils(Activity activity) {
        this.activity = activity;
    }

    public void selectImage() {
        try {
            PackageManager pm = activity.getPackageManager();
            //int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            //if (hasPerm == PackageManager.PERMISSION_GRANTED) {
            final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Select Option");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Take Photo")) {
                        dialog.dismiss();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePicture();
                    } else if (options[item].equals("Choose From Gallery")) {
                        dialog.dismiss();
                        //requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },1);
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        Log.d(TAG, "Pick from gallery");
                        activity.startActivityForResult(pickPhoto, PICTURE_GALLERY);
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
            //} else
            //    Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(activity, "Camera Permission error", Toast.LENGTH_SHORT).show();
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
        String Path = Environment.getExternalStorageDirectory() + "/MyApp";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = new File(Path);
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
}
