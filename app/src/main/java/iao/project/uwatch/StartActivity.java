package iao.project.uwatch;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import iao.project.uwatch.database.DataBase;
import iao.project.uwatch.database.DataBaseStatement;

public class StartActivity extends FragmentActivity {

    private ImageView image;
    private final String ETAT="connected";
    private final String UNFOUND = "unfounded";
    private DataBaseStatement data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        data = new DataBaseStatement(this);

        //The Song of appearnance
        final MediaPlayer mediaPlayer = MediaPlayer.create(this , R.raw.appearnance);

        //Animation
        image = (ImageView) findViewById(R.id.start_image);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext() , R.anim.sample_animation);
        image.startAnimation(animation);
        mediaPlayer.start();

        //Redirection vers la page de la creation du compte ou login
        Runnable run = new Runnable(){
            @Override
            public void run()
            {
                //Demarrer l'activité d'acceuil
                Intent intent = new Intent(getApplicationContext(),Register_Or_Login_Activity.class);
                startActivity(intent);
                finish();
            }
        };
        //Redirection vers la page de la creation du compte ou login
        Runnable acceuil = new Runnable(){
            @Override
            public void run()
            {
                //Demarrer l'activité d'acceuil
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        if(data.getUserID(ETAT).equals(UNFOUND))
        {
            //Handler pour donner un temps d'attante avant la redirection
            new Handler().postDelayed(run , 3000);
        }
        else
        {
            //Handler pour donner un temps d'attante avant la redirection
            new Handler().postDelayed(acceuil , 3000);
        }


    }
}