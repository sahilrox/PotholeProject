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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.potholeproject.GPSTracker;
import com.example.potholeproject.MapsActivity;
import com.example.potholeproject.R;
import com.example.potholeproject.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class NotificationsFragment extends Fragment {

    private static final int IMAGE_REQUEST = 1;
    private NotificationsViewModel notificationsViewModel;
    ImageButton btnShowLocation;
    Button reportBtn;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private TextView mTextView;
    private EditText descriptionView;
    FirebaseUser mUser;

    // GPSTracker class
    GPSTracker gps;
    private ImageButton btnCapture;
    private ImageView imgCapture;
    String lat = null, lon =null, imageURL = null;
    private static final int Image_Capture_Code = 1;
    private Bitmap bp;
    private Uri imageUri;
    private StorageTask uploadTask;
    String title,confidence;
    boolean detect = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();


        btnCapture = root.findViewById(R.id.button1);
        imgCapture =  root.findViewById(R.id.capturedImage);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                cInt.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(cInt,Image_Capture_Code);
//                openImage();

            }
        });

        descriptionView = root.findViewById(R.id.description);

        reportBtn = root.findViewById(R.id.reportBtn);
//        mTextView = root.findViewById(R.id.textView);

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bp == null) {

                    addReportToDatabase();
                    Toast.makeText(getContext(),"Report uploaded without image",Toast.LENGTH_SHORT).show();
                }
                else {

                    if (detect) {


                        //Send report to database
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(mAuth.getUid()).child("Reports");
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference storageReference = firebaseStorage.getReference();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte data[] = baos.toByteArray();

                        mUser = FirebaseAuth.getInstance().getCurrentUser();

                        final StorageReference reference = storageReference.child("images/" +
                                mUser.getUid() + "/" + System.currentTimeMillis() + ".jpg");
                        UploadTask task = reference.putBytes(data);

                        final ProgressDialog pd = new ProgressDialog(getContext());
//                pd.setTitle("Uploading");
                        pd.setMessage("Uploading");
                        pd.show();

                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                pd.dismiss();
                                if (task.isSuccessful())
                                    //  Toast.makeText(getContext(),"DownloadUrl = " + reference.getDownloadUrl(), Toast.LENGTH_SHORT);
//im
//                        final String mUri;
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
//                                mTextView.setText(uri.toString());
                                            setImageUrl(uri.toString());
                                            addReportToDatabase();
                                        }
                                    });
//                        mTextView.setText(mUri);
                            }

                        });


//                Report report = new Report(lat,lon,imageURL);
//                databaseReference.setValue(report);
                    } else {
                        Toast.makeText(getContext(), "Not a pothole image!! Please Try Again.", Toast.LENGTH_SHORT).show();
                    }
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

                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra("lat",latitude);
                    intent.putExtra("long",longitude);
                    startActivity(intent);

                    // \n is for new line
                    //Toast.makeText(getContext(), "Your Location is - \nLat: "
                           // + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
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

    private void getLocation() {
        gps = new GPSTracker(getContext());

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            lat = Double.toString(latitude);
            lon = Double.toString(longitude);

            // \n is for new line
            //Toast.makeText(getContext(), "Your Location is - \nLat: "
                   // + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    private void addReportToDatabase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Reports");
        String id = "Report" + System.currentTimeMillis();

        getLocation();

        String description = descriptionView.getText().toString();

        Report report = new Report(lat,lon,imageURL,mUser.getUid(),id,description);
        database.child(id).setValue(report);

    }

    private void setImageUrl(String url) {
        imageURL = url;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK){
                bp = (Bitmap) data.getExtras().get("data");

                FirebaseVisionLabelDetectorOptions options =
                        new FirebaseVisionLabelDetectorOptions.Builder()
                                .setConfidenceThreshold(0.8f)
                                .build();

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bp);

                // Or, to set the minimum confidence required:
                FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
                        .getVisionLabelDetector(options);

                Task<List<FirebaseVisionLabel>> result =
                        detector.detectInImage(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                            @Override
                                            public void onSuccess(List<FirebaseVisionLabel> labels) {
                                                // Task completed successfully
                                                // ...

                                                for (FirebaseVisionLabel label: labels) {
                                                    String text = label.getLabel();
                                                    String entityId = label.getEntityId();
                                                    float confidence = label.getConfidence();

                                                    if(text.equals("Asphalt") || text.equals("Road") ) {
                                                        if(confidence > 0.75) {
                                                            detect = true;
                                                        }
                                                    }

                                                    Toast.makeText(getContext(), text+" "+entityId+" "+confidence, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                                Toast.makeText(getContext(),"Failed ML", Toast.LENGTH_SHORT).show();
                                            }
                                        });






                imgCapture.setImageBitmap(bp);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
}