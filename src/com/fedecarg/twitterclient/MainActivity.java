package com.fedecarg.twitterclient;

import oauth.signpost.OAuth;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private SharedPreferences prefs;
	private final Handler mTwitterHandler = new Handler();
	
    final Runnable mUpdateTwitterNotification = new Runnable() {
        public void run() {
        	EditText txtEntry = (EditText) findViewById(R.id.entry);
        	txtEntry.setText("");
        	Toast.makeText(getBaseContext(), "Tweet sent", Toast.LENGTH_LONG).show();
        }
    };
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ImageView bgImageView = (ImageView) findViewById(R.id.bg_image);
        bgImageView.setImageResource(R.drawable.icon);
        
        if (TwitterUtils.isAuthenticated(prefs)) {
        	updateLoginStatus();
        	
            Button btnTweet = (Button) findViewById(R.id.btn_tweet);
            btnTweet.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
            		EditText txtEntry = (EditText) findViewById(R.id.entry);
            		sendTweet(txtEntry.getText().toString().trim());
                }
            });
            
            Button btnClearCredentials = (Button) findViewById(R.id.btn_clear_credentials);
            btnClearCredentials.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	clearCredentials();
                	updateLoginStatus();
                }
            });
        } else {
        	requestToken();
        }
	}
	
	public void updateLoginStatus() {
		Toast.makeText(MainActivity.this, "Logged into Twitter : " + TwitterUtils.isAuthenticated(prefs), Toast.LENGTH_SHORT).show();
	}	
	
	public void sendTweet(String text) {
		try {
			TwitterUtils.sendTweet(prefs, text);
			mTwitterHandler.post(mUpdateTwitterNotification);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString().trim();
	    		try {
					TwitterUtils.sendTweet(prefs,value);
					mTwitterHandler.post(mUpdateTwitterNotification);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
			
		alert.show();
		*/
	}

	private void clearCredentials() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
		requestToken();
	}
	
	private void requestToken() {
    	Intent i = new Intent(getApplicationContext(), PrepareRequestTokenActivity.class);
		startActivity(i);
	}
}