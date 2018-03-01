package com.example.inspiron.uroborosproto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.inspiron.uroborosproto.model.User;
import com.example.inspiron.uroborosproto.utils.Constants;
import com.example.inspiron.uroborosproto.utils.SharedPrefManager;
import com.example.inspiron.uroborosproto.utils.Utils;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.firebase.client.Firebase;

import java.util.HashMap;


/**
 * Created by INSPIRON on 28/02/2018.
 */

public class Login extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    public static final int RequestSignInCode = 7;
    public FirebaseAuth firebaseAuth;
    public GoogleApiClient googleApiClient;

    //Boton Google
    SignInButton mSignInButton = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mSignInButton = (SignInButton) findViewById(R.id.signIn);
        mSignInButton.setOnClickListener(this);


        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                }/* */)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();
        mSignInButton.setOnClickListener(this);
    }
    public void UserSignInMethod(){
        Intent AuthIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(AuthIntent, RequestSignInCode);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestSignInCode){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(googleSignInResult.isSuccess()){
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
                FirebaseUserAuth(googleSignInAccount);
                startActivity(new Intent(this,App.class));
                finish();
                //FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                //Toast.makeText(this," "+firebaseUser.getDisplayName(),Toast.LENGTH_SHORT).show();

            }
        }
    }
    public void FirebaseUserAuth(GoogleSignInAccount googleSignInAccount){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(),null);

        //FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //Toast.makeText(App.this,"Bienvenido!",Toast.LENGTH_SHORT).show();
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task AuthResultTask) {
                        if(AuthResultTask.isSuccessful()){
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            //SignOutButton.setVisibility(View.VISIBLE);
                            mSignInButton.setVisibility(View.GONE);

                        }else{
                            Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signIn){
            UserSignInMethod();
        }
    }

    /*GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    SignInButton mSignInButton = null;
    private static final String Tag = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private String idToken;
    public SharedPrefManager sharedPrefManager;
    private final Context mContext = this;
    private String name, email;
    private String photo;
    private Uri photoUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mSignInButton = (SignInButton) findViewById(R.id.signIn);
        mSignInButton.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(Login.this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        mGoogleApiClient.connect();

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get signedIn user
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to Firebase
                if (user != null) {
                    // User is signed in
                    createUserInFirebaseHelper();
                    Log.d(Tag, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(Tag, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null){
            FirebaseAuth.getInstance().signOut();
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {

        Utils utils = new Utils(this);
        int id = view.getId();

        if (id == R.id.signIn){
            if (utils.isNetworkAvailable()){
                SignIn();
            }else {
                Toast.makeText(Login.this, "Oops! no internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //This method creates a new user on our own Firebase database
    //after a successful Authentication on Firebase
    //It also saves the user info to SharedPreference
    private void createUserInFirebaseHelper(){

        //Since Firebase does not allow "." in the key name, we'll have to encode and change the "." to ","
        // using the encodeEmail method in class Utils
        final String encodedEmail = Utils.encodeEmail(email.toLowerCase());

        //create an object of Firebase database and pass the the Firebase URL
        final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail);

        //Add a Listerner to that above location
        userLocation.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
/*                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    // Insert into Firebase database
                    User newUser = new User(name, photo, encodedEmail, timestampJoined);
                    userLocation.setValue(newUser);

                    Toast.makeText(Login.this, "Account created!", Toast.LENGTH_SHORT).show();

                    // After saving data to Firebase, goto next activity
//                    Intent intent = new Intent(MainActivity.this, NavDrawerActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

                Log.d(Tag, getString(R.string.log_error_occurred) + firebaseError.getMessage());
                //hideProgressDialog();
                if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN){
                }
                else {
                    Toast.makeText(Login.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SignIn() {
        Intent SignInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(SignInIntent,RC_SIGN_IN);
    }

    // This IS the method where the result of clicking the signIn button will be handled
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, save Token and a state then authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                idToken = account.getIdToken();

                name = account.getDisplayName();
                email = account.getEmail();
                photoUri = account.getPhotoUrl();
                photo = photoUri.toString();

                System.out.println(idToken+" "+ name +" " + email+" " + photo);

                // Save Data to SharedPreference
                sharedPrefManager = new SharedPrefManager(mContext);
                sharedPrefManager.saveIsLoggedIn(mContext, true);

                sharedPrefManager.saveEmail(mContext, email);
                sharedPrefManager.saveName(mContext, name);
                sharedPrefManager.savePhoto(mContext, photo);

                sharedPrefManager.saveToken(mContext, idToken);
                //sharedPrefManager.saveIsLoggedIn(mContext, true);

                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(Tag, "Login Unsuccessful. ");
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    //After a successful sign into Google, this method now authenticates the user with Firebase
    private void firebaseAuthWithGoogle(AuthCredential credential){
        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Tag, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(Tag, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            createUserInFirebaseHelper();
                            Toast.makeText(Login.this, "Login successful",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, App.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                        hideProgressDialog();
                    }
                });
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(Tag, "Conexion failed" +connectionResult);
    }*/
}
