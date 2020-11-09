package com.example.mapdemo.oauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mapdemo.MarkerCloseInfoWindowOnRetapDemoActivity;
import com.example.mapdemo.R;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import static com.example.mapdemo.MarkerCloseInfoWindowOnRetapDemoActivity.*;
import static com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent;

public final class GoogleLogin extends Activity implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 0;
    private static String PHOTO_READ_SCOPE = "https://www.googleapis.com/auth/photoslibrary.readonly";
    private String TAG = "GoogleLogin";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_sign_in);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(PHOTO_READ_SCOPE))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.e(TAG,"[onStart] account null? "+(account==null));
//        updateUI(account);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                singIn();
                break;
        }
    }

    private void singIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task, data);
//            finish();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task, Intent data) {
        try {
            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
            Log.e(TAG,"[handleSignInResult] token= "+googleSignInAccount.getIdToken());
            Credential = data.getParcelableExtra(Credential.EXTRA_KEY);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }


}