package iao.project.uwatch;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import iao.project.uwatch.bean.Image;
import iao.project.uwatch.bean.MyList;
import iao.project.uwatch.bean.User;
import iao.project.uwatch.database.DataBaseStatement;

public class ProfileActivity extends FragmentActivity implements View.OnClickListener {

    private ImageButton back ;
    private CircleImageView picture;
    private TextView username , mail , favorite , like , hate;
    private Button update;
    private EditText nom , prenom , user , e_mail ,password , conf_password;
    private User client;
    private DataBaseStatement data;
    private String ETAT = "connected";
    private Uri imageUri;
    private Drawable defaultImg;
    private List<String> errors , updates;
    private String error;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Uwatch");
    private DatabaseReference users = myRef.child("users");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Images");
    private FirebaseAuth publicClient;
    private FirebaseUser currentUser;
    private RelativeLayout privateLayout;
    private TextView publicDisplay;
    private int favoriteCount;
    private int likeCount;
    private int hateCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Instancier les objets
        back = findViewById(R.id.profile_back);
        picture = findViewById(R.id.profile_picture);
        username = findViewById(R.id.profile_username);
        mail = findViewById(R.id.profile_mail);
        nom = findViewById(R.id.profile_nom);
        prenom = findViewById(R.id.profile_prenom);
        user = findViewById(R.id.profile_username_field);
        e_mail = findViewById(R.id.profile_mail_field);
        password = findViewById(R.id.profile_password);
        conf_password = findViewById(R.id.profile_confirm_password);
        update = findViewById(R.id.update);
        favorite = findViewById(R.id.profile_favorite);
        hate = findViewById(R.id.profile_unwanted);
        like = findViewById(R.id.profile_wanted);
        privateLayout = findViewById(R.id.private_user);
        publicDisplay = findViewById(R.id.public_user);
        data = new DataBaseStatement(this);
        publicClient = FirebaseAuth.getInstance();
        currentUser = publicClient.getCurrentUser();

        if(currentUser!=null)
        {
            client = data.getOutsiderUser(currentUser.getUid());
            privateLayout.setVisibility(View.GONE);
            Picasso.get().load(currentUser.getPhotoUrl()).into(picture);
            username.setText(client.getNom());
        }
        else
        {
            publicDisplay.setVisibility(View.GONE);
            client = data.getUser(ETAT);
            username.setText(client.getUserName());
            nom.setText(client.getNom());
            prenom.setText(client.getPrenom());
            e_mail.setText(client.getMail());
            user.setText(client.getUserName());
            storageReference.child(username.getText().toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(picture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
            update.setOnClickListener(this);
            picture.setOnClickListener(this);
        }

        mail.setText(client.getMail());
        users.child(client.getUserName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteCount = (int)snapshot.child("favoriteList").getChildrenCount();
                data.updatefavorite(client.getUserName() , favoriteCount);
                likeCount = (int) snapshot.child("likeList").getChildrenCount();
                data.updateLike(client.getUserName() , likeCount);
                hateCount = (int) snapshot.child("hateList").getChildrenCount();
                data.updateHate(client.getUserName() , hateCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        like.setText(""+client.getList().getLike());
        hate.setText(""+client.getList().getHate());
        favorite.setText(""+client.getList().getFavorite());


        //Listener
        back.setOnClickListener(this);
    }
    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.profile_picture)
        {
            Intent gallery = new Intent();
            gallery.setType("image/*");
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(gallery,"Select your profile picture"), 1);
        }
        if(view.getId() == R.id.profile_back)
        {
            startActivity(new Intent(this,MainActivity.class));
        }
        if(view.getId() == R.id.update)
        {
            errors = new ArrayList<String>();
            updates = new ArrayList<String>();
            if(!nom.getText().toString().equals(client.getNom()))
            {
                data.updateName(client.getUserName() , nom.getText().toString());
                updates.add("Nom");
                users.child(client.getUserName()).child("nom").setValue(nom.getText().toString());
            }
            if(!prenom.getText().toString().equals(client.getPrenom()))
            {
                data.updateLastName(client.getUserName() , prenom.getText().toString());
                updates.add("Prenom");
                users.child(client.getUserName()).child("prenom").setValue(prenom.getText().toString());
            }
            if(password.getText().toString()!=null && !password.getText().toString().trim().equals(""))
            {
                if(conf_password.getText().toString()!=null && !conf_password.getText().toString().trim().equals("")) {
                    if(password.getText().toString().equals(conf_password.getText().toString())) {
                        data.updatePassword(client.getUserName(), password.getText().toString());
                        updates.add("Mot de passe");
                        users.child(client.getUserName()).child("password").setValue(password.getText().toString());
                    }
                    else
                    {
                        error = "Veuillez confirmer avec le même mot de passe";
                        errors.add(error);
                    }
                }
                else
                {
                    error = "Veuillez confirmer votre mot de passe";
                    errors.add(error);
                }
            }
            if(errors.isEmpty()) {
                Iterator<String> it = updates.iterator();
                String str = "";
                while(it.hasNext()) {
                    str += it.next()+" ";
                }
                Toast.makeText(this,str+" updated", Toast.LENGTH_LONG).show();
            }
            else
            {
                Iterator<String> it = errors.iterator();
                while(it.hasNext()) {
                    Toast.makeText(this,it.next(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode , int resultCode , Intent dt)
    {
        super.onActivityResult(requestCode , resultCode , dt);
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            imageUri = dt.getData();
            Picasso.get().load(imageUri).into(picture);
            uploadImage();
        }
    }
    @Override
    public void onBackPressed() {
        // Desactiver le retour physique
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void uploadImage()
    {

        if(imageUri != null)
        {
            StorageReference fileReference = storageReference.child(username.getText().toString());
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    /*Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    },5000);*/
                    Toast.makeText(ProfileActivity.this , "Upload successful", Toast.LENGTH_SHORT).show();
                    Image image = new Image(username.getText().toString(),taskSnapshot.getMetadata().getPath());
                    users.child(username.getText().toString()).child("image").setValue(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this , e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    Toast.makeText(ProfileActivity.this , "Upload en train", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(this , "No file selected",Toast.LENGTH_SHORT).show();
        }
    }
}