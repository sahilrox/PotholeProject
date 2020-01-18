package com.example.potholeproject.ui.notifications;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.potholeproject.GPSTracker;
import com.example.potholeproject.MainActivity;
import com.example.potholeproject.MapsActivity;
import com.example.potholeproject.R;
import com.example.potholeproject.Report;
import com.example.potholeproject.SubmitInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    Button btnShowLocation, reportBtn;
    private static final int REQUEST_CODE_PERMISSION = 2;
    int PICK_IMAGE_REQUEST = 111;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    // GPSTracker class
    GPSTracker gps;
    private Button btnCapture;
    private ImageView imgCapture;
    String lat = null, lon =null, imageURL;
    private final int REQUEST_TAKE_PHOTO = 1;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    ProgressDialog pd;
    private Uri filePath;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        mAuth = FirebaseAuth.getInstance();

        btnCapture = root.findViewById(R.id.button1);
        imgCapture =  root.findViewById(R.id.capturedImage);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });
        reportBtn = root.findViewById(R.id.reportBtn);
        reportBtn.setEnabled(false);

        pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading....");


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath != null) {
                    pd.show();

                    StorageReference childRef = storageReference.child("Images/"+System.currentTimeMillis());

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), "Select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            if (ActivityCompat.checkSelfPermission(getContext(), mPermission)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnShowLocation = root.findViewById(R.id.button);

        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(getContext());

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    lat = Double.toString(latitude);
                    lon = Double.toString(longitude);

                    // \n is for new line
                    //Toast.makeText(getContext(), "Your Location is - \nLat: "
                    //        + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra("lat",latitude);
                    intent.putExtra("long",longitude);
                    startActivity(intent);
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });

        return root;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting image to ImageView
                reportBtn.setEnabled(true);
                imgCapture.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference imageReference = storageReference.child("Images");

            if(filePath != null) {
                UploadTask uploadTask = imageReference.putFile(filePath);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error in uploading pic", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "pic uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else {
            Toast.makeText(getContext(),"File path is null",Toast.LENGTH_SHORT).show();
        }

    }
}