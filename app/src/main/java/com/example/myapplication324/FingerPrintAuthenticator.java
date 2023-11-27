package com.example.myapplication324;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricManager;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

import io.github.muddz.styleabletoast.StyleableToast;

public class FingerPrintAuthenticator extends BiometricPrompt.AuthenticationCallback


{
    private AuthenticationCallback authenticationCallback;

    public interface AuthenticationCallback {
        void onAuthenticationSuccess();
    }
    private Context context;
    public FingerPrintAuthenticator(Context context, AuthenticationCallback callback) {
        this.context = context;
        this.authenticationCallback = callback;
    }

    public void showSignInBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sign in")
                .setSubtitle("Touch the fingerprint sensor to continue")
                .setNegativeButtonText("Cancel")
                .build();

        showBiometricPrompt(promptInfo);
    }

    public void showSignUpBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sign up")
                .setSubtitle("Register your fingerprint to sign up")
                .setNegativeButtonText("Cancel")
                .build();

        showBiometricPrompt(promptInfo);
    }

    private void showBiometricPrompt(BiometricPrompt.PromptInfo promptInfo) {
        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                Log.e("MY_APP_TAG", "Please enroll your fingerprint in the settings.");
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(context);
        BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) context, executor, this);

        // Show the biometric authentication prompt
        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        StyleableToast.makeText(context, "Authentication error: you haven't set your fingerprint" , Toast.LENGTH_SHORT, R.style.mytoast).show();
    }


    @Override
    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);

        // Check if the context is Sign_up activity
        if (context instanceof Sign_up) {
            authenticationCallback.onAuthenticationSuccess();
        }
        // Check if the context is Login activity
        else if (context instanceof Login2) {
//            Intent intent = new Intent(context, Home.class);
//            context.startActivity(intent);
          context.startActivity(new Intent(context, Home.class));
        }
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        StyleableToast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT, R.style.mytoast).show();
    }
}