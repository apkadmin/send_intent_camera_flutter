package com.movan.cameraplugin;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class CameraPlugin implements MethodCallHandler {
  Activity context;
  int code = 1;
  MethodChannel methodChannel;
  File tobeCapturedImageLocationFilePath;
  Uri toBeCapturedImageLocationURI;
  private Registrar registrar;
  private ActivityCompletedCallBack activityCompletedCallBack = null;

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "camera_plugin");
    channel.setMethodCallHandler(new CameraPlugin(registrar.activity(), channel, registrar));
  }

  public CameraPlugin(Activity activity, MethodChannel methodChannel, Registrar registrar) {
    this.context = activity;
    this.methodChannel = methodChannel;
    this.methodChannel.setMethodCallHandler(this);
    this.registrar = registrar;
  }

  @Override
  public void onMethodCall(MethodCall call, final Result result) {
    this.registrar.addActivityResultListener(new PluginRegistry.ActivityResultListener() {
                                               public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                                                 if(requestCode == code && resultCode == Activity.RESULT_OK){
                                                   List<String> temp = new ArrayList<String>();
                                                   temp.add(tobeCapturedImageLocationFilePath.getAbsolutePath());
                                                   activityCompletedCallBack.sendDocument(temp);
                                                   code += 1;
                                                   return true;
                                                 }
                                                 return false;
                                               };
                                             }
    );

    if(call.method.equalsIgnoreCase("takePhoto")) {
      Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      this.activityCompletedCallBack = new ActivityCompletedCallBack() {
        @Override
        public void sendDocument(List<String> data) {
          result.success(data);
        }
      };

      if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
        File file = getImageTempFile();
        tobeCapturedImageLocationFilePath = file;
        toBeCapturedImageLocationURI = FileProvider.getUriForFile(context.getApplicationContext(), "com.movan.cameraplugin.CameraPlugin", file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, toBeCapturedImageLocationURI);
        context.startActivityForResult(takePictureIntent, code);
      }

    }
    else {
      result.notImplemented();
    }
  }

  private File getImageTempFile(){
    try {
      UUID uuid =  UUID.randomUUID();
      File storageDir =  context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
      return  File.createTempFile(uuid.toString(), ".jpg", storageDir);
    } catch (Exception e) {
      Log.e("ErrorGetFile",e.getMessage());
    }
    return null;
  }
}

interface ActivityCompletedCallBack {
  void sendDocument(List<String> data);
}
