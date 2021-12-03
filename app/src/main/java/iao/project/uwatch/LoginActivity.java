package iao.project.uwatch;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import iao.project.uwatch.bean.MyList;
import iao.project.uwatch.bean.User;
import iao.project.uwatch.database.DataBase;
import iao.project.uwatch.database.DataBaseStatement;
import iao.project.uwatch.mail.JavaMailApi;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class LoginActivity extends FragmentActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 123;
    private TextView toRegister , forgotPassword;
    private ImageButton back;
    private ImageView showPassword;
    private EditText username , pass;
    private Button login , googleButton , facebookButton ;
    private DataBaseStatement data;
    private List<User> users;
    private User user;
    private Iterator<User> it;
    private boolean test;
    private String ETAT = "connected";
    private String Tag = "Cycle de vie";
    private ArrayList<String> errors;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference client;
    private GoogleSignInOptions google;
    private GoogleSignInClient clientGoogle;
    private FirebaseAuth publicClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Instancier les objets
        pass = findViewById(R.id.inputPassword);
        username = findViewById(R.id.login_username);
        showPassword = findViewById(R.id.showpass);
        back = findViewById(R.id.login_back);
        login = findViewById(R.id.btnlogin);
        toRegister = findViewById(R.id.SignUp);
        data = new DataBaseStatement(this);
        googleButton = findViewById(R.id.btnGoogle);
        facebookButton = findViewById(R.id.btnFacebook);
        forgotPassword = findViewById(R.id.forgotPassword);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Uwatch");
        client = myRef.child("users");
        publicClient = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        google = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        clientGoogle = GoogleSignIn.getClient(this,google);


        //Mêtre les listener
        showPassword.setOnClickListener((v) -> {
            if (pass.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPassword.setImageResource(R.drawable.eye_active);
            } else {
                pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPassword.setImageResource(R.drawable.eye_not_active);
            }
        });
        facebookButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        login.setOnClickListener(this);
        back.setOnClickListener(v -> onBackPressed());
        toRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        forgotPassword.setOnClickListener(this);

    }


    public void onClick(View view)
    {
        if(view.getId() == R.id.btnlogin)
        {
            errors = new ArrayList<String>();
            if(username.getText().toString().trim().equals("") || username.getText().toString() == null)
            {
                errors.add("Remplir le champs username");
            }
            if(pass.getText().toString().trim().equals("") || pass.getText().toString() == null)
            {
                errors.add("Remplir le champs password");
            }
            if(errors.size()>0)
            {
                Iterator<String> it = errors.iterator();
                String er="";
                while(it.hasNext()) {
                    er += it.next()+" ";
                }
                Toast.makeText(this ,er,Toast.LENGTH_SHORT).show();
            }
            else {
                users = data.getAllUsers(this);
                it = users.iterator();
                while(it.hasNext())
                {
                    user = it.next();
                    if(user.getUserName().equals(username.getText().toString()) && user.getPassword().equals(pass.getText().toString()))
                    {

                        test = true;
                        break;
                    }
                    else
                    {
                        test = false;
                    }
                }
                if(!test)
                {
                    client.child(username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()) {
                                Log.i("Login state : " , "Recuperation failed");
                                Toast.makeText(LoginActivity.this , "Username not found",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                User user = snapshot.getValue(User.class);
                                if(!user.getPassword().equals(pass.getText().toString()))
                                {
                                    Log.i("Login state","Mot de pass incorrecte");
                                    Toast.makeText(LoginActivity.this , "Password incorrect",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    user.setEtat(ETAT);
                                    data.insertUser(user);
                                    Log.i("Login State" , "Recuperation reussite "+user.getUserName()+" "+user.getPassword());
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else
                {
                    data.updateEtat(username.getText().toString(),ETAT);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        }
        if(view.getId() == R.id.btnGoogle)
        {
            signIn();
        }
        if(view.getId() == R.id.btnFacebook)
        {
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
        }
        if(view.getId() == R.id.forgotPassword)
        {
            if(username.getText().toString().trim().equals("") || username.getText().toString() == null)
            {
                Toast.makeText(this , "Entrez votre username d'abord",Toast.LENGTH_SHORT).show();
            }
            else
            {
                client.child(username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists())
                        {
                            Toast.makeText(LoginActivity.this , "Il existe aucun compte avec se username",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            JavaMailApi javaMailApi = new JavaMailApi(LoginActivity.this,(String)snapshot.child("mail").getValue(),"Recuperation de password",(String)snapshot.child("password").getValue());
                            javaMailApi.execute();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this , "Connectez vous à internet",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken() , null);
        publicClient.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = publicClient.getCurrentUser();
                            Log.i("Connexion state", "signInWithCredential:success");
                            User user1 = new User(user.getUid(),user.getDisplayName(),user.getEmail());
                            client.child(user1.getUserName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.exists())
                                    {
                                        client.child(user1.getUserName()).setValue(user1);
                                        if(!data.isExist(user1.getUserName())) {
                                            data.insertUser(user1);
                                        }
                                    }
                                    else
                                    {
                                        if(!data.isExist(user1.getUserName())) {
                                            data.insertUser(snapshot.getValue(User.class));
                                        }

                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Connexion state", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this,"Authentication failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("Facebook auth", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        publicClient.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = publicClient.getCurrentUser();
                            Log.i("Tag", "signInWithCredential:success");
                            User user1 = new User(user.getUid(),user.getDisplayName(),user.getEmail());
                            client.child(user1.getUserName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.exists())
                                    {
                                        client.child(user1.getUserName()).setValue(user1);
                                        if(!data.isExist(user1.getUserName())) {
                                            data.insertUser(user1);
                                        }
                                    }
                                    else
                                    {
                                        if(!data.isExist(user1.getUserName())) {
                                            data.insertUser(snapshot.getValue(User.class));
                                        }

                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Tag", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = clientGoogle.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Authentification google", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Authentification state", "Google sign in failed", e);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart()
    {
        super.onStart();
        Log.i(Tag,"onStart()");
    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        Log.i(Tag,"onRestart()");
    }
    @Override
    public void onResume()
    {
        super.onResume();
        Log.i(Tag,"onResume()");
    }
    @Override
    public void onPause()
    {
        super.onPause();
        Log.i(Tag,"onPause()");
    }
    @Override
    public void onStop()
    {
        super.onStop();
        Log.i(Tag,"onStop()");
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.i(Tag,"onDestroy()");
    }


}