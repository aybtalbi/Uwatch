package iao.project.uwatch;

import androidx.fragment.app.FragmentActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import iao.project.uwatch.bean.MyList;
import iao.project.uwatch.bean.User;
import iao.project.uwatch.database.DataBase;
import iao.project.uwatch.database.DataBaseStatement;

public class RegisterActivity extends FragmentActivity implements View.OnClickListener {

    private ImageButton back;
    private ImageView showPassword, showPassword2;

    private EditText nom;
    private EditText prenom;
    private EditText username;
    private EditText mail;
    private EditText password;
    private EditText confPassword;
    private Button register;
    private ArrayList<String> errors;
    private TextView toLogin;
    private User user ;
    private String Tag = "Cycle de vie";
    private String ETAT = "disconnected";
    private DataBaseStatement data;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Uwatch");
    DatabaseReference users = myRef.child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //Donner à chaque variable sa valeur relatif
        showPassword = findViewById(R.id.showpass1);
        showPassword2 = findViewById(R.id.showpass2);



        toLogin =(TextView) findViewById(R.id.tologin);
        back =(ImageButton) findViewById(R.id.btn_return);
        nom =(EditText) findViewById(R.id.reg_nom);
        prenom =(EditText) findViewById(R.id.reg_prenom);
        username =(EditText) findViewById(R.id.inputUsername);
        mail =(EditText) findViewById(R.id.inputEmail);
        password =(EditText) findViewById(R.id.inputPassword);
        confPassword =(EditText) findViewById(R.id.inputConformPassword);
        register =(Button) findViewById(R.id.btnRegister);

        user = new User();
        data = new DataBaseStatement(this);

        //Evenement apres clique sur retour
        back.setOnClickListener(v -> onBackPressed());

        //apres clique sur registre
        register.setOnClickListener(this);

        //Apres clique sur i have an account
        toLogin.setOnClickListener(this);

        showPassword.setOnClickListener(this);
        showPassword2.setOnClickListener(this);
    }
    public void createUser(User user)
    {
        if(!empty(nom.getText().toString()))
        {
            user.setNom(nom.getText().toString());
        }
        if(!empty(prenom.getText().toString()))
        {
            user.setPrenom(prenom.getText().toString());
        }
        if(!empty(username.getText().toString()))
        {
            user.setUserName(username.getText().toString());
        }
        if(!empty(mail.getText().toString()))
        {
            user.setMail(mail.getText().toString());
        }
        if(!empty(password.getText().toString()))
        {
            user.setPassword(password.getText().toString());
        }
        user.setEtat(ETAT);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        createUser(user);
        outState.putParcelable("parcelable",user);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        user = savedInstanceState.getParcelable("parcelable");
        nom.setText(user.getNom());
        prenom.setText(user.getPrenom());
        username.setText(user.getUserName());
        mail.setText(user.getMail());
        password.setText(user.getPassword());
    }

    public boolean empty(String s)
    {
        boolean val = false;
        if(s.trim().equals("") || s == null)
        {
            val = true;
        }
        return val;
    }
    public boolean isPasswordValid(String s1 , String s2)
    {
        boolean val = true;
        if(empty(s1) || empty(s2) || !(s1.equals(s2)))
        {
            val = false;
        }
        return val;
    }
    public static boolean isEmailValid(String email)
    {
        boolean val = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches())
        {
            val = true;
        }
        return val;
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.btnRegister)
        {
            errors = new ArrayList<String>();
            String er="";
            if(empty(nom.getText().toString()))
            {
                errors.add("Champ nom vide");
            }
            if(empty(prenom.getText().toString()))
            {
                errors.add("Champ prenom vide");
            }
            if(empty(username.getText().toString()))
            {
                errors.add("Champ username vide");
            }
            if(empty(mail.getText().toString()))
            {
                errors.add("Champ e-mail vide");
            }
            if(!isEmailValid(mail.getText().toString()))
            {
                errors.add("E-mail invalide");
            }
            if(empty(password.getText().toString()))
            {
                errors.add("Entrez un mots de passe");
            }
            if(empty(confPassword.getText().toString()))
            {
                errors.add("Champ confirmation vide");
            }
            else {
                if (!isPasswordValid(password.getText().toString(), confPassword.getText().toString())) {
                    errors.add("Confirmation failed");
                }
            }
            if(errors.size() == 0) {
                // Sauvegarder les valeurs dans la base de donnée local et distante
                user = new User(username.getText().toString(),nom.getText().toString(),prenom.getText().toString(),mail.getText().toString(),password.getText().toString(),ETAT);
                Query myQuery = users.orderByChild("userName").equalTo(user.getUserName());
                myQuery.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists())
                        {
                            user.setList(new MyList(0,0,0));
                            users.child(user.getUserName()).setValue(user);
                            data.insertUser(user);
                            Log.i(Tag,"Information added to dataBase");
                            //Envoyer le client pour se connecter
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this,"Information not added to dataBase",Toast.LENGTH_SHORT).show();
                            Log.i(Tag,"Information not added to dataBase");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.e("loadPost:onCancelled", String.valueOf(databaseError.toException()));
                    }
                });
            }
            else
            {
                Iterator<String> it = errors.iterator();
                while(it.hasNext())
                {
                    er += it.next()+" ";
                }
                Toast.makeText(this, er , Toast.LENGTH_SHORT).show();
            }
        }
        if(v.getId() == R.id.tologin)
        {
            //Envoyer le client pour se connecter
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        // for hide and show password
        if (v.getId() == R.id.showpass1 || v.getId() == R.id.showpass2)
            if (password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                confPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPassword.setImageResource(R.drawable.eye_active);
                showPassword2.setImageResource(R.drawable.eye_active);
            } else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPassword.setImageResource(R.drawable.eye_not_active);
                showPassword2.setImageResource(R.drawable.eye_not_active);
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
