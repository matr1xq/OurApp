package com.example.ourapp;

//import android.support.annotation.Nullable;
//import android.hardware.biometrics.BiometricManager;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;


import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat;


public class MainPage extends AppCompatActivity {


    private CancellationSignal cancellationSignal = null;
    private BiometricPrompt.AuthenticationCallback authCallback;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        authCallback = new BiometricPrompt.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(
                    int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Authentication Error : " + errString);
            }

            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication Succeeded");
                // or start a new Activity
            }
        };
        checkBiometricSupport();

        findViewById(R.id.fp_auth_button).setOnClickListener(
                new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.P)
                    @Override
                    public void onClick(View view) {
                        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(getApplicationContext())
                                .setTitle("Заголовок отпечатка")
                                .setSubtitle("Подзаголовок")
                                .setDescription("Описание")
                                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL).build();
//                                .setNegativeButton("Отмена", getMainExecutor(), new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        notifyUser("Аутентификация отменена");
//                                    }
//                                })


                        biometricPrompt.authenticate(
                                getCancellationSignal(),
                                getMainExecutor(),
                                authCallback);
                    }
                }
        );

    }

    // it will be called when
    // authentication is cancelled by
    // the user
    private CancellationSignal getCancellationSignal() {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(
                new CancellationSignal.OnCancelListener() {
                    @Override
                    public void onCancel() {
                        notifyUser("Authentication was Cancelled by the user");
                    }
                });
        return cancellationSignal;
    }


    // this is a toast method which is responsible for
    // showing toast it takes a string as parameter
    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private Boolean checkBiometricSupport() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.isDeviceSecure()) {
            notifyUser("Fingerprint authentication has not been enabled in settings");
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint Authentication Permission is not enabled");
            return false;
        }
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return true;
        } else
            return true;
    }

}