package pkg.home.notepad;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditSelectedNoteActivity extends AppCompatActivity {
    private MaterialToolbar topMaterialToolBar;
    private TextView tvShowPreviousTimeOutput;
    private EditText etShowNoteOutput;
    private ImageView ivGallery,ivShowImageEditNoteActivity;
    private NotesDataTable table=NotesDataTable.createObject(this);
   private String noteText,noteTime,date,imagePath,newPath;
    private int noteSerialNumber,serialNumber;
    private static final int PICK_IMAGE=2;
    private  boolean isChecked;
    private Uri uri,imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_selected_note);
        topMaterialToolBar=findViewById(R.id.topMaterialTollBarEditSelectedNote);
        tvShowPreviousTimeOutput=findViewById(R.id.tvShowPreviousTimeOutput);
        etShowNoteOutput=findViewById(R.id.etNoteOutput);
        ivGallery=findViewById(R.id.ivGalleryEditSelectedNote);
        ivShowImageEditNoteActivity=findViewById(R.id.ivShowImageEditNoteActivity);
        serialNumber=table.latestSerialNumber();

        ActivityResultLauncher<String> pickImagePermissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

            }
        });
        ActivityResultLauncher<Void> takeImageLauncher=registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                //Show image in the image view and convert the image to uri
                ivShowImageEditNoteActivity.setImageBitmap(result);
                try {
                    File tempFile=File.createTempFile("ImageFile"+serialNumber,".png");
                    ByteArrayOutputStream bytes=new ByteArrayOutputStream();
                    result.compress(Bitmap.CompressFormat.PNG,100,bytes);
                    byte [] byteArray=bytes.toByteArray();
                    FileOutputStream outputStream=new FileOutputStream(tempFile);
                    outputStream.write(byteArray);
                    outputStream.flush();
                    outputStream.close();
                    Uri uri=Uri.fromFile(tempFile);
                    newPath=uri.toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
         //Unparse the intent and show it in the relevant Views
         noteText= getIntent().getStringExtra(NotesFragment.KEY_NOTE);
         noteTime= getIntent().getStringExtra(NotesFragment.KEY_TIME);
         noteSerialNumber=getIntent().getIntExtra(NotesFragment.KEY_SERIAL_NUMBER,0);
         isChecked=getIntent().getBooleanExtra(NotesFragment.KEY_IS_CHECKED,false);
         imagePath=getIntent().getStringExtra(NotesFragment.KEY_IMAGE);
         newPath=imagePath;
        etShowNoteOutput.setText(noteText);
        tvShowPreviousTimeOutput.setText(noteTime);
        if (imagePath==null){
         //Don't Show any image
        }else {
            //Convert String to Uri and display it
            uri = Uri.parse(Uri.decode(imagePath));
            ivShowImageEditNoteActivity.setImageURI(uri);
            }

        topMaterialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if the clicked note had an image or not and perform the relevant func
                if (ivShowImageEditNoteActivity.getDrawable()==null){
                if (etShowNoteOutput.getText().toString().equals(noteText)){
                    //go back without doing anything
                    finish();
                }else {
                    //update the note
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM hh:mm a");
                    date = dateformat.format(c.getTime());
                    noteText=etShowNoteOutput.getText().toString();
                    if (table.updateNote(new ListViewModelClass(noteSerialNumber,noteText,date,isChecked))&&table.updateNoteInFavourite(new Favourites(noteSerialNumber,noteText,date,isChecked))){
                        Toast.makeText(EditSelectedNoteActivity.this, "Note Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
                }else {
                    if (etShowNoteOutput.getText().toString().equals(noteText)&&newPath.equals(imagePath)) {
                        finish();
                    }else {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM hh:mm a");
                        date = dateformat.format(c.getTime());
                        noteText=etShowNoteOutput.getText().toString();
                        if (table.updateNote(new ListViewModelClass(noteSerialNumber,noteText,date,isChecked,newPath))&&table.updateNoteInFavourite(new Favourites(noteSerialNumber,noteText,date,isChecked,newPath))){
                            Toast.makeText(EditSelectedNoteActivity.this, "Note Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                }

            }
        });

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show the dialog to choose between camera or gallery
                Dialog customDialog=new Dialog(EditSelectedNoteActivity.this);
                customDialog.setContentView(R.layout.custom_dialog_image_option_layout);
                TextView tvCamera=customDialog.findViewById(R.id.tvCamera);
                TextView tvGallery=customDialog.findViewById(R.id.tvGallery);
                TextView btnCancel=customDialog.findViewById(R.id.btnCancel);
                tvCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //launch camera
                        takeImageLauncher.launch(null);
                        customDialog.dismiss();
                    }
                });
                tvGallery.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                    @Override
                    public void onClick(View view) {
                        //Check for permission
                        if (ContextCompat.checkSelfPermission(EditSelectedNoteActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                           //Open gallery
                            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            startActivityForResult(gallery,PICK_IMAGE);
                            customDialog.dismiss();
                        }else {
                            //Ask for permission again
                            pickImagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                        customDialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                    }
                });
                customDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            //Receive the image form gallery and show it in the image view and Convert it to String
            assert data != null;
            imageUri = data.getData();
            ivShowImageEditNoteActivity.setImageURI(imageUri);
           newPath=imageUri.toString();
        }

    }
}