package com.google.identitytoolkit.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.IdToken;

import java.io.IOException;

public class GitkitDemo extends Activity implements OnClickListener {

  private GitkitClient client;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    client = GitkitClient.newBuilder(this, new GitkitClient.SignInCallbacks() {
      @Override
      public void onSignIn(IdToken idToken, GitkitUser user) {
        showProfilePage(idToken, user);
      }
    @Override
    public void onSignInFailed() {
      Toast.makeText(GitkitDemo.this, "Sign in failed", Toast.LENGTH_LONG).show();
    }
    }).build();

    showSignInPage();
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (!client.handleActivityResult(requestCode, resultCode, intent)) {
      super.onActivityResult(requestCode, resultCode, intent);
    }
  }


  @Override
  protected void onNewIntent(Intent intent) {
    if (!client.handleIntent(intent)) {
      super.onNewIntent(intent);
    }
  }



  private void showSignInPage() {
    setContentView(R.layout.welcome);
    Button button = (Button) findViewById(R.id.sign_in);
    button.setOnClickListener(this);
  }


  private void showProfilePage(IdToken idToken, GitkitUser user) {
    setContentView(R.layout.profile);
    showAccount(user);
    findViewById(R.id.sign_out).setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {

    if (v.getId() == R.id.sign_in) {
      client.startSignIn();
    } else if (v.getId() == R.id.sign_out) {
      showSignInPage();
    }
  }

  private void showAccount(GitkitUser user) {
    ((TextView) findViewById(R.id.account_email)).setText(user.getEmail());

    if (user.getDisplayName() != null) {
      ((TextView) findViewById(R.id.account_name)).setText(user.getDisplayName());
    }

    if (user.getPhotoUrl() != null) {
      final ImageView pictureView = (ImageView) findViewById(R.id.account_picture);
      new AsyncTask<String, Void, Bitmap>() {

        @Override
        protected Bitmap doInBackground(String... arg) {
          try {
            byte[] result = HttpUtils.get(arg[0]);
            return BitmapFactory.decodeByteArray(result, 0, result.length);
          } catch (IOException e) {
            return null;
          }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
          if (bitmap != null) {
            pictureView.setImageBitmap(bitmap);
          }
        }
      }.execute(user.getPhotoUrl());
    }
  }

}
