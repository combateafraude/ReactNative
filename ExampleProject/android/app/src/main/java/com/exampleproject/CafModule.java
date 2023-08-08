package com.exampleproject;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.jetbrains.annotations.NotNull;

import com.combateafraude.passivefaceliveness.input.PassiveFaceLiveness;
import com.combateafraude.passivefaceliveness.input.PreviewSettings;
import com.combateafraude.passivefaceliveness.input.CafStage;
import com.combateafraude.passivefaceliveness.PassiveFaceLivenessActivity;
import com.combateafraude.passivefaceliveness.output.PassiveFaceLivenessResult;

import com.combateafraude.documentdetector.output.Capture;
import com.combateafraude.documentdetector.input.DocumentDetector;
import com.combateafraude.documentdetector.output.DocumentDetectorResult;
import com.combateafraude.documentdetector.input.Document;
import com.combateafraude.documentdetector.input.DocumentDetectorStep;
import com.combateafraude.documentdetector.DocumentDetectorActivity;
import com.combateafraude.documentdetector.input.CaptureMode;
import com.combateafraude.documentdetector.input.CaptureStage;

import com.combateafraude.faceauthenticator.FaceAuthenticatorActivity;
import com.combateafraude.faceauthenticator.input.FaceAuthenticator;
import com.combateafraude.faceauthenticator.output.FaceAuthenticatorResult;

public class CafModule extends ReactContextBaseJavaModule {

    private final int REQUEST_CODE_PASSIVE_FACE_LIVENESS = 50005;
    private final int REQUEST_CODE_DOCUMENT_DETECTOR = 50006;
    private final int REQUEST_CODE_FACE_AUTHENTICATOR = 50007;

    CafModule(ReactApplicationContext reactContext) {
        super(reactContext);
        ActivityEventListener activityEventListener = new BaseActivityEventListener() {

            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
                try {
                    if (requestCode == REQUEST_CODE_PASSIVE_FACE_LIVENESS) {
                        if (intent == null) {
                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit("PassiveFaceLiveness_Cancel", null);
                            return;
                        }

                        PassiveFaceLivenessResult passiveFaceLivenessResult = (PassiveFaceLivenessResult) intent
                                .getSerializableExtra(PassiveFaceLivenessResult.PARAMETER_NAME);
                        if (passiveFaceLivenessResult.wasSuccessful()) {
                            WritableMap writableMap = new WritableNativeMap();

                            writableMap.putString("success", true);
                            writableMap.putString("imagePath", passiveFaceLivenessResult.getImagePath());
                            writableMap.putString("imageUrl", passiveFaceLivenessResult.getImageUrl());
                            writableMap.putString("signedResponse", passiveFaceLivenessResult.getSignedResponse());
                            writableMap.putString("trackingId", passiveFaceLivenessResult.getTrackingId());

                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit("PassiveFaceLiveness_Success", writableMap);
                        } else {
                            WritableMap writableMap = new WritableNativeMap();

                            writableMap.putString("message", passiveFaceLivenessResult.getSdkFailure().getMessage());
                            writableMap.putString("type",
                                    passiveFaceLivenessResult.getSdkFailure().getClass().getSimpleName());

                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit("PassiveFaceLiveness_Error", writableMap);
                        }

                    } else if (requestCode == REQUEST_CODE_DOCUMENT_DETECTOR) {
                        if (intent == null) {
                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit("DocumentDetector_Cancel", null);
                            return;
                        }

                        DocumentDetectorResult documentDetectorResult = (DocumentDetectorResult) intent
                                .getSerializableExtra(DocumentDetectorResult.PARAMETER_NAME);
                        if (documentDetectorResult.wasSuccessful()) {
                            WritableMap writableMap = new WritableNativeMap();

                            WritableArray capturesArray = new WritableNativeArray();
                            for (Capture capture : documentDetectorResult.getCaptures()) {
                                WritableMap captureMap = new WritableNativeMap();

                                captureMap.putString("imagePath", capture.getImagePath());
                                captureMap.putString("imageUrl", capture.getImageUrl());
                                captureMap.putString("label", capture.getLabel());
                                captureMap.putDouble("quality",
                                        capture.getQuality() != null ? capture.getQuality() : 0.0);

                                capturesArray.pushMap(captureMap);
                            }
                            writableMap.putArray("captures", capturesArray);

                            writableMap.putString("type", documentDetectorResult.getType());
                            writableMap.putString("trackingId", documentDetectorResult.getTrackingId());
                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit("DocumentDetector_Success", writableMap);
                        } else {
                            WritableMap writableMap = new WritableNativeMap();

                            writableMap.putString("message", documentDetectorResult.getSdkFailure().getMessage());
                            writableMap.putString("type",
                                    documentDetectorResult.getSdkFailure().getClass().getSimpleName());

                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit("DocumentDetector_Error", writableMap);
                        }
                    } else if (requestCode == REQUEST_CODE_FACE_AUTHENTICATOR) {
                        if (intent == null) {
                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit("FaceAuthenticator_Cancel", null);
                            return;
                        }

                        FaceAuthenticatorResult faceAuthenticatorResult = (FaceAuthenticatorResult) intent
                                .getSerializableExtra(FaceAuthenticatorResult.PARAMETER_NAME);
                        if (faceAuthenticatorResult.wasSuccessful()) {
                            WritableMap writableMap = new WritableNativeMap();

                            writableMap.putBoolean("authenticated", faceAuthenticatorResult.isAuthenticated());
                            writableMap.putString("signedResponse", faceAuthenticatorResult.getSignedResponse());
                            writableMap.putString("trackingId", faceAuthenticatorResult.getTrackingId());
                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit("FaceAuthenticator_Success", writableMap);
                        } else {
                            WritableMap writableMap = new WritableNativeMap();

                            writableMap.putString("message", faceAuthenticatorResult.getSdkFailure().getMessage());
                            writableMap.putString("type",
                                    faceAuthenticatorResult.getSdkFailure().getClass().getSimpleName());

                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit("FaceAuthenticator_Error", writableMap);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        reactContext.addActivityEventListener(activityEventListener);
    }

    @NotNull
    @Override
    public String getName() {
        return "CafModule";
    }

    @ReactMethod
    public void passiveFaceLiveness(String mobileToken) {
        try {
            PassiveFaceLiveness passiveFaceLiveness = new PassiveFaceLiveness.Builder(mobileToken)
                    .setPreviewSettings(new PreviewSettings(true))
                    .setUseDeveloperMode(true)
                    .setUseDebug(true)
                    .setUseAdb(true)
                    .setUseEmulator(true)
                    .setUseRoot(true)
                    // This method set the SDK's environment, you should have a specific mobile
                    // token fot each environment. Check the documentation here:
                    // https://docs.caf.io/sdks/ios/getting-started/faceauthenticator#:~:text=.setStage(stage%3A%20CAFStage)
                    .setStage(CafStage.BETA)
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

            documentDetectorBuilder
                    .setPreviewSettings(new com.combateafraude.documentdetector.input.PreviewSettings(true))
                    // This method set the SDK's environment, you should have a specific mobile
                    // token fot each environment. Check the documentation here:
                    // https://docs.caf.io/sdks/ios/getting-started/faceauthenticator#:~:text=.setStage(stage%3A%20CAFStage)
                    .setStage(com.combateafraude.documentdetector.input.CafStage.BETA)
                    .setUseDeveloperMode(true)
                    .setUseDebug(true)
                    .setUseAdb(true)
                    .setUseEmulator(true)
                    .setUseRoot(true);

            if (documentType.equals("CNH")) {
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[] {
                        new DocumentDetectorStep(Document.CNH_FRONT),
                        new DocumentDetectorStep(Document.CNH_BACK)
                });
            } else if (documentType.equals("RG")) {
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[] {
                        new DocumentDetectorStep(Document.RG_FRONT),
                        new DocumentDetectorStep(Document.RG_BACK)
                });
            } else if (documentType.equals("RNE")) {
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[] {
                        new DocumentDetectorStep(Document.RNE_FRONT),
                        new DocumentDetectorStep(Document.RNE_BACK)
                });
            } else if (documentType.equals("CRLV")) {
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[] {
                        new DocumentDetectorStep(Document.CRLV)
                });
            } else if (documentType.equals("PASSPORT")) {
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[] {
                        new DocumentDetectorStep(Document.PASSPORT)
                });
            } else if (documentType.equals("CTPS")) {
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[] {
                        new DocumentDetectorStep(Document.CTPS_FRONT),
                        new DocumentDetectorStep(Document.CTPS_BACK)
                });
            } else if (documentType.equals("OTHERS")) {
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[] {
                        new DocumentDetectorStep(Document.OTHERS)
                });
            } else if (documentType.equals("ANY")) {
                documentDetectorBuilder.setDocumentSteps(new DocumentDetectorStep[] {
                        new DocumentDetectorStep(Document.ANY)
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

    @ReactMethod
    public void faceAuthenticator(String mobileToken, String CPF) {
        try {
            FaceAuthenticator faceAuthenticator = new FaceAuthenticator.Builder(mobileToken)
                    .setPeopleId(CPF)
                    .setUseDeveloperMode(true)
                    .setUseDebug(true)
                    .setUseAdb(true)
                    .setUseEmulator(true)
                    .setUseRoot(true)
                    // This method set the SDK's environment, you should have a specific mobile
                    // token fot each environment. Check the documentation here:
                    // https://docs.caf.io/sdks/ios/getting-started/faceauthenticator#:~:text=.setStage(stage%3A%20CAFStage)
                    .setStage(com.combateafraude.faceauthenticator.input.CafStage.BETA)
                    .build();

            Activity activity = getCurrentActivity();
            Intent intent = new Intent(activity.getApplicationContext(), FaceAuthenticatorActivity.class);
            intent.putExtra(FaceAuthenticator.PARAMETER_NAME, faceAuthenticator);
            activity.startActivityForResult(intent, REQUEST_CODE_FACE_AUTHENTICATOR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
