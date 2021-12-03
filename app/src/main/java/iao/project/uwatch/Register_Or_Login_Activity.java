package iao.project.uwatch;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static iao.project.uwatch.MainActivity.Tag;

public class Register_Or_Login_Activity extends FragmentActivity implements View.OnClickListener{

    private Button login;
    private Button register;
    float x1,x2,y1,y2;
    private FirebaseAuth publicClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_or_login);

        //Affecter chaque variable à sa valeur
        login = (Button) findViewById(R.id.regOrLog_login);
        register = (Button) findViewById(R.id.regOrLog_register);
        //Ajouter le listener
        login.setOnClickListener(this);
        register.setOnClickListener(this);

        publicClient = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.regOrLog_register)
        {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            Animatoo.animateSwipeLeft(this);
        }
        if(v.getId() == R.id.regOrLog_login)
        {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            Animatoo.animateSwipeRight(this);
        }
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = publicClient.getCurrentUser();
        if(currentUser != null)
        {
            Toast.makeText(this,"U Signed In successfully",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }
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