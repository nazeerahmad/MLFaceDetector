package com.example.mlfacedetector.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mlfacedetector.Helper.GraphicOverlay;
import com.example.mlfacedetector.Helper.RectOverlay;
import com.example.mlfacedetector.MainActivity;
import com.example.mlfacedetector.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class FragmentDetectFace extends Fragment implements View.OnClickListener{

    private CameraView cameraView;
    private GraphicOverlay graphicOverlay;
    private Button btnDetect;
    private AlertDialog waitingDialog;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.face_detect_layout,container,false);
        cameraView =(CameraView) view.findViewById(R.id.camera_view);
        graphicOverlay = view.findViewById(R.id.graphic_overlay);
        btnDetect = view.findViewById(R.id.btn_detect);

        return view;

    }

    private void initView(View view){
        cameraView =(CameraView) view.findViewById(R.id.camera_view);
        graphicOverlay = view.findViewById(R.id.graphic_overlay);
        btnDetect = view.findViewById(R.id.btn_detect);
        waitingDialog = new SpotsDialog.Builder().setContext(context)
                                .setMessage("please wait")
                                .setCancelable(false).build();

        btnDetect.setOnClickListener(this);

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialog.show();
                Bitmap bitmap =cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();
                runFaceDetecter(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

    }

    private void runFaceDetecter(Bitmap bitmap) {
        FirebaseVisionImage  image =  FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions options =  new  FirebaseVisionFaceDetectorOptions.Builder().build();

        FirebaseVisionFaceDetector detector =  FirebaseVision.getInstance().getVisionFaceDetector(options);

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        processFaceResult(firebaseVisionFaces);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void processFaceResult(List<FirebaseVisionFace> firebaseVisionFaces) {
        int count=0;
        for(FirebaseVisionFace face : firebaseVisionFaces){
            Rect bounds = face.getBoundingBox();
            RectOverlay rect= new RectOverlay(graphicOverlay,bounds);
            graphicOverlay.add(rect);
            count++;
        }
        waitingDialog.dismiss();
        Toast.makeText(context, String.format("Detected %d faces in image",count), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context   instanceof MainActivity){
            this.context= context;
        }
    }

    @Override
    public void onClick(View view) {
        cameraView.start();
        cameraView.captureImage();
        graphicOverlay.clear();
    }
}
