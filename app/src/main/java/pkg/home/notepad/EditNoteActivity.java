package pkg.home.notepad;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditNoteActivity extends AppCompatActivity {
    MaterialToolbar editNoteTopMaterialTollBar;
    TextView tvShowCurrentTimeOutput;
    ImageView ivGallery,ivShowImage;
    EditText etNoteInput;
    private Uri imageUri;
    NotesDataTable table=NotesDataTable.createObject(this);
    private String date,imagePath;
    private int serialNumber=0;
    public static final int PICK_IMAGE=1;
    public static final String KEY_SENDING_HOUR="Key for sending hour";
    public static final String KEY_SENDING_MINUTE="Key for sending minute";
    public static final String KEY_SENDING_TEXT="Key for sending text";
    public static final String KEY_SENDING_IMAGE="Key for sending image";

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note_acitivity);
        editNoteTopMaterialTollBar = findViewById(R.id.editNoteTopMaterialTollBar);
        ivGallery = findViewById(R.id.ivGallery);
        tvShowCurrentTimeOutput = findViewById(R.id.tvShowCurrentTimeOutput);
        etNoteInput = findViewById(R.id.etNoteInput);
        ivShowImage = findViewById(R.id.ivShowImage);
        //Initialize to latest serial number
        serialNumber = table.latestSerialNumber();

        ActivityResultLauncher<String> pickImagePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

            }
        });
        ActivityResultLauncher<Void> takeImageLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                //Show image in the image view and convert the image in Uri
                ivShowImage.setImageBitmap(result);
                try {
                    File tempFile = File.createTempFile("ImageFile" + serialNumber, ".png");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    result.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    byte[] byteArray = bytes.toByteArray();
                    FileOutputStream outputStream = new FileOutputStream(tempFile);
                    outputStream.write(byteArray);
                    outputStream.flush();
                    outputStream.close();
                    Uri uri = Uri.fromFile(tempFile);
                    imagePath = uri.toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        //Show thw current date and time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd MMM hh:mm a");
        date = dateformat.format(c.getTime());
        tvShowCurrentTimeOutput.setText(date);


        editNoteTopMaterialTollBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog customDialog = new Dialog(EditNoteActivity.this);
                customDialog.setContentView(R.layout.custom_dialog_image_option_layout);
                TextView tvCamera = customDialog.findViewById(R.id.tvCamera);
                TextView tvGallery = customDialog.findViewById(R.id.tvGallery);
                TextView btnCancel = customDialog.findViewById(R.id.btnCancel);
                tvCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Launch Camera
                        takeImageLauncher.launch(null);
                        customDialog.dismiss();
                    }
                });
                tvGallery.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                    @Override
                    public void onClick(View view) {
                        //Check for permission
                        if (ContextCompat.checkSelfPermission(EditNoteActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            //If granted open the gallery
                            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            startActivityForResult(gallery, PICK_IMAGE);
                            customDialog.dismiss();
                        } else {
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
    public void finish() {
        //Send the note and image back to the main activity
        TimePicker getCurrentTime=new TimePicker(EditNoteActivity.this);
        getCurrentTime.setIs24HourView(false);
        Intent intent=new Intent();
        intent.putExtra(KEY_SENDING_HOUR,getCurrentTime.getHour());
        intent.putExtra(KEY_SENDING_MINUTE,getCurrentTime.getMinute());
        intent.putExtra(KEY_SENDING_TEXT,etNoteInput.getText().toString());
        intent.putExtra(KEY_SENDING_IMAGE,imagePath);
        setResult(RESULT_OK,intent);
        super.finish();
    }
    public static class EditNoteActivityContract extends ActivityResultContract<EditNoteParameters,EditNoteOutputParameters>{
        EditNoteOutputParameters editNoteOutputParameters=new EditNoteOutputParameters();
        public static final String KEY_HOUR="Hour";
        public static final String KEY_MINUTE="Minute";

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, EditNoteParameters editNoteParameters) {
            //Pack the intent for the edit note activity
              Intent editNoteActivityIntent=new Intent(context, EditNoteActivity.class);
              editNoteActivityIntent.putExtra(KEY_HOUR,editNoteParameters.getHour());
              editNoteActivityIntent.putExtra(KEY_MINUTE,editNoteParameters.getMinute());
            return editNoteActivityIntent;
        }

        @Override
        public EditNoteOutputParameters parseResult(int i, @Nullable Intent intent) {
            //Pack the result for main Activity
            if (i==EditNoteActivity.RESULT_OK){
                assert intent != null;
                editNoteOutputParameters.setSendingHour(intent.getIntExtra(EditNoteActivity.KEY_SENDING_HOUR,0));
                editNoteOutputParameters.setSendingMinute(intent.getIntExtra(EditNoteActivity.KEY_SENDING_MINUTE,0));
                editNoteOutputParameters.setEtNoteText(intent.getStringExtra(EditNoteActivity.KEY_SENDING_TEXT));
                editNoteOutputParameters.setImagePath(intent.getStringExtra(EditNoteActivity.KEY_SENDING_IMAGE));
                return editNoteOutputParameters;
            }
            return null;
        }
    }
    public static class EditNoteParameters{
         private  String hour;
         private  String minute;

        public EditNoteParameters() {
        }

        public EditNoteParameters(String hour, String minute) {
            this.hour = hour;
            this.minute = minute;
        }

        public String getHour() {
            return hour;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }

        public String getMinute() {
            return minute;
        }

        public void setMinute(String minute) {
            this.minute = minute;
        }
    }
    public static class EditNoteOutputParameters{
        private int sendingHour;
        private int sendingMinute;
        private String etNoteText;
        private  String imagePath;

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public EditNoteOutputParameters() {
        }

        public EditNoteOutputParameters(int sendingHour, int sendingMinute, String etNoteText) {
            this.sendingHour = sendingHour;
            this.sendingMinute = sendingMinute;
            this.etNoteText = etNoteText;
        }

        public int getSendingHour() {
            return sendingHour;
        }

        public void setSendingHour(int sendingHour) {
            this.sendingHour = sendingHour;
        }

        public int getSendingMinute() {
            return sendingMinute;
        }

        public void setSendingMinute(int sendingMinute) {
            this.sendingMinute = sendingMinute;
        }

        public String getEtNoteText() {
            return etNoteText;
        }

        public void setEtNoteText(String etNoteText) {
            this.etNoteText = etNoteText;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            //Receive the image picked form gallery and show it in the imageview and convert it to String
            assert data != null;
            imageUri = data.getData();
            ivShowImage.setImageURI(imageUri);
            imagePath=imageUri.toString();
        }
    }
}