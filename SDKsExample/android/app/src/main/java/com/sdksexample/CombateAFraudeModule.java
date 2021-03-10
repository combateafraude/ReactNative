package com.sdksexample;

import android.app.Activity;
import android.content.Intent;
import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import org.jetbrains.annotations.NotNull;

import com.combateafraude.passivefaceliveness.input.PassiveFaceLiveness;
import com.combateafraude.passivefaceliveness.PassiveFaceLivenessActivity;
import com.combateafraude.passivefaceliveness.output.PassiveFaceLivenessResult;

import com.combateafraude.documentdetector.output.Capture;
import com.combateafraude.documentdetector.input.DocumentDetector;
import com.combateafraude.documentdetector.output.DocumentDetectorResult;
import com.combateafraude.documentdetector.input.Document;
import com.combateafraude.documentdetector.input.DocumentDetectorStep;
import com.combateafraude.documentdetector.DocumentDetectorActivity;
import com.combateafraude.documentdetector.output.failure.SDKFailure;

import java.io.ByteArrayOutputStream;

public class CombateAFraudeModule extends ReactContextBaseJavaModule {

    private final int REQUEST_CODE_PASSIVE_FACE_LIVENESS = 50005;
    private final int REQUEST_CODE_DOCUMENT_DETECTOR = 50006;

    CombateAFraudeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        ActivityEventListener activityEventListener = new BaseActivityEventListener() {

            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
                if (intent == null) {
                    return;
                }

                if (requestCode == REQUEST_CODE_PASSIVE_FACE_LIVENESS){
                    try {
                        PassiveFaceLivenessResult passiveFaceLivenessResult = (PassiveFaceLivenessResult) intent.getSerializableExtra(PassiveFaceLivenessResult.PARAMETER_NAME);
                        if (intent == null) {
                            getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("PassiveFaceLiveness_Cancel", null);
                        } else if (passiveFaceLivenessResult.wasSuccessful()) {
                            WritableMap writableMap = new WritableNativeMap();

                            writableMap.putString("imagePath", passiveFaceLivenessResult.getImagePath());
                            writableMap.putString("imageUrl", passiveFaceLivenessResult.getImageUrl());
                            writableMap.putString("signedResponse", passiveFaceLivenessResult.getSignedResponse());
                            writableMap.putString("trackingId", passiveFaceLivenessResult.getTrackingId());

                            getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("PassiveFaceLiveness_Success", writableMap);
                        } else {
                            WritableMap writableMap = new WritableNativeMap();

                            writableMap.putString("message", passiveFaceLivenessResult.getSdkFailure().getMessage());
                            writableMap.putString("type", passiveFaceLivenessResult.getSdkFailure().getClass().getSimpleName());

                            getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("PassiveFaceLiveness_Error", writableMap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == REQUEST_CODE_DOCUMENT_DETECTOR){
                    try {
                        DocumentDetectorResult documentDetectorResult = (DocumentDetectorResult) intent.getSerializableExtra(DocumentDetectorResult.PARAMETER_NAME);
                        if (intent == null) {
                            getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DocumentDetector_Cancel", null);
                        } else if (documentDetectorResult.wasSuccessful()) {
                            WritableMap writableMap = new WritableNativeMap();

                            WritableArray capturesArray = new WritableNativeArray();
                            for (Capture capture: documentDetectorResult.getCaptures()) {
                                WritableMap captureMap = new WritableNativeMap();

                                captureMap.putString("imagePath", capture.getImagePath());
                                captureMap.putString("imageUrl", capture.getImageUrl());
                                captureMap.putString("label", capture.getLabel());
                                captureMap.putDouble("quality", capture.getQuality());

                                capturesArray.pushMap(captureMap);
                            }
                            writableMap.putArray("captures", capturesArray);

                            writableMap.putString("type", documentDetectorResult.getType());
                            writableMap.putString("trackingId", documentDetectorResult.getTrackingId());
                            getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DocumentDetector_Success", writableMap);
                        } else {
                            WritableMap writableMap = new WritableNativeMap();

                            writableMap.putString("message", documentDetectorResult.getSdkFailure().getMessage());
                            writableMap.putString("type", documentDetectorResult.getSdkFailure().getClass().getSimpleName());

                            getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DocumentDetector_Error", writableMap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        reactContext.addActivityEventListener(activityEventListener);
    }

    @NotNull
    @Override
    public String getName() {
        return "CombateAFraude";
    }

    @ReactMethod
    public void passiveFaceLiveness(String mobileToken) {
        try {
            PassiveFaceLiveness passiveFaceLiveness = new PassiveFaceLiveness.Builder(mobileToken)
                    .build();

            Activity activity = getCurrentActivity();
            Intent intent = new Intent(activity.getApplicationContext(), PassiveFaceLivenessActivity.class);
            intent.putExtra(PassiveFaceLiveness.PARAMETER_NAME, passiveFaceLiveness);
            activity.startActivityForResult(intent, REQUEST_CODE_PASSIVE_FACE_LIVENESS);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @ReactMethod
    public void documentDetector(String mobileToken, String documentType) {
        try {
            DocumentDetector.Builder documentDetectorBuilder = new DocumentDetector.Builder(mobileToken);
            documentDetectorBuilder.showPreview(true, null, null, null, null);

            if (documentType.equals("CNH")){
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[]{
                    new DocumentDetectorStep(Document.CNH_FRONT, null, null, null),
                    new DocumentDetectorStep(Document.CNH_BACK, null, null, null)
                });
            } else if (documentType.equals("RG")){
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[]{
                    new DocumentDetectorStep(Document.RG_FRONT, null, null, null),
                    new DocumentDetectorStep(Document.RG_BACK, null, null, null)
                });
            } else if (documentType.equals("RNE")){
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[]{
                    new DocumentDetectorStep(Document.RNE_FRONT, null, null, null),
                    new DocumentDetectorStep(Document.RNE_BACK, null, null, null)
                });
            } else if (documentType.equals("CRLV")){
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[]{
                    new DocumentDetectorStep(Document.CRLV, null, null, null)
                });
            }

            Activity activity = getCurrentActivity();
            Intent intent = new Intent(activity.getApplicationContext(), DocumentDetectorActivity.class);
            intent.putExtra(DocumentDetector.PARAMETER_NAME, documentDetectorBuilder.build());
            activity.startActivityForResult(intent, REQUEST_CODE_DOCUMENT_DETECTOR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
