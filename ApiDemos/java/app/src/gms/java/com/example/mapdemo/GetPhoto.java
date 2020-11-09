package com.example.mapdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.common.collect.ImmutableList;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient;
import com.google.photos.types.proto.MediaItem;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class GetPhoto {
    private static GetPhoto getPhotoInstance;
    private static int CREDENTIAL_PATH = R.raw.credentials;
    private static Context mContext;
    private static final List<String> REQUIRED_SCOPES =
            ImmutableList.of("https://www.googleapis.com/auth/photoslibrary.readonly");
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//    private static final java.io.File DATA_STORE_DIR =
//            new java.io.File(GetPhoto.class.getResource("/").getPath(), "credentials");
    private static final int LOCAL_RECEIVER_PORT = 61984;
    PhotosLibraryClient photosLibraryClient;
    private String TAG = "GetPhoto";

    public static GetPhoto getInstance() {
        if(getPhotoInstance == null) {
            getPhotoInstance = new GetPhoto();
        }
        return getPhotoInstance;
    }

    public void init() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    PhotosLibrarySettings photosLibrarySettings = createCredential();
                    photosLibraryClient =
                            PhotosLibraryClient.initialize(photosLibrarySettings);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void getPhoto() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // Make a request to list all media items in the user's library
                // Iterate over all the retrieved media items
                // Pagination is handled automatically
                InternalPhotosLibraryClient.ListMediaItemsPagedResponse response = photosLibraryClient.listMediaItems();
                for (MediaItem item : response.iterateAll()) {
                    // Get some properties of a media item
                    String id = item.getId();
                    String description = item.getDescription();
                    String mimeType = item.getMimeType();
                    String productUrl = item.getProductUrl();
                    String filename = item.getFilename();
                    Log.e(TAG,"[getPhoto] file name= "+filename+", id= "+id);
                }
                return null;
            }


        }.execute();
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    private PhotosLibrarySettings createCredential() throws IOException, GeneralSecurityException {
        // Set up the Photos Library Client that interacts with the API
        PhotosLibrarySettings settings =
                PhotosLibrarySettings.newBuilder()
                        .setCredentialsProvider(
//                                FixedCredentialsProvider.create(
//                                        getUserCredentials(REQUIRED_SCOPES))
                                (com.google.api.gax.core.CredentialsProvider) getUserCredentials(REQUIRED_SCOPES))
                        .build();
        return settings;
    }

    private CredentialsProvider getUserCredentials(List<String> selectedScopes)
            throws IOException, GeneralSecurityException {

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JSON_FACTORY, new InputStreamReader(/*new FileInputStream(credentialsPath)*/
                                mContext.getResources().openRawResource(CREDENTIAL_PATH)));
        String clientId = clientSecrets.getDetails().getClientId();
        String clientSecret = clientSecrets.getDetails().getClientSecret();

//        File DATA_STORE_DIR =
//                new java.io.File(mContext.getResource("/").getPath(), "credentials");
        File DATA_STORE_DIR =
                new java.io.File(String.valueOf(Uri.parse(String.valueOf(mContext.getResources().openRawResource(CREDENTIAL_PATH)))));


        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        /*GoogleNetHttpTransport.newTrustedTransport()*/
                        new com.google.api.client.http.javanet.NetHttpTransport(),
                        JSON_FACTORY,
                        clientSecrets,
                        selectedScopes)
                        .setDataStoreFactory(/*new FileDataStoreFactory(DATA_STORE_DIR)*/new MemoryDataStoreFactory())
                        .setAccessType("offline")
                        .build();
        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setPort(LOCAL_RECEIVER_PORT).build();
//        Credential credential = new AuthorizationCodeInstalledAppInternal(flow, receiver).authorize("user");
        FutureTask futureTask = getFutureTask(flow, receiver);
        while (!futureTask.isDone()) {}
        Credential credential = null;
        try {
            credential = (Credential)futureTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                (Credentials) MarkerCloseInfoWindowOnRetapDemoActivity.Credential);

        return credProvider;

        /*return UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(credential.getRefreshToken())
                .build();*/
    }

    private FutureTask<Credential> getFutureTask(GoogleAuthorizationCodeFlow flow, LocalServerReceiver receiver) {
        FutureTask<Credential> futureTask = new FutureTask<Credential>(new Callable<Credential>() {
            @Override
            public Credential call() throws Exception {
                return new AuthorizationCodeInstalledAppInternal(flow, receiver).authorize("user");
            }
        });
        Thread thread = new Thread(futureTask);
        thread.start();

        return futureTask;
    }

    static class AuthorizationCodeInstalledAppInternal extends AuthorizationCodeInstalledApp {
        AuthorizationCodeFlow flow;
        VerificationCodeReceiver receiver;

        public AuthorizationCodeInstalledAppInternal(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver) {
            super(flow, receiver);
            this.flow = flow;
            this.receiver = receiver;
        }

        @Override
        protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
            String url = authorizationUrl.build();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            ((Activity)mContext).startActivity(intent);
        }
    }


}
