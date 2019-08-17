package com.example.atrue.bluelocking_final;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Random;

public class BlueLocking extends Activity {

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    private static String getRandomString(int length)
    {
        StringBuffer buffer = new StringBuffer();

        String chars[] = {"a", "b", "c", "d", "e", "f", " g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        for (int i=0 ; i<length ; i++)
        {
            Random RNG = new Random();
            int ran = RNG.nextInt(chars.length);
            buffer.append(chars[ran]);
        }
        return buffer.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("KeyInfo", 0);
        String key = pref.getString("s_key", null);


        if(key == null) {
            String str = getRandomString(8);
            Log.d("HoseLog", str);

            SharedPreferences.Editor edit = pref.edit();
            edit.putString("s_key", str);
            edit.commit();
        } else {
            Log.d("HoseLog", key);
        }

        setContentView(R.layout.activity_blue_locking);

        fingerprintManager =
                (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        /*
        ImageView img;
        img = (ImageView) findViewById(R.id.Logo);
        final Animation downAnim = AnimationUtils.loadAnimation(
                this,R.anim.down);


        Handler hd = new Handler();
        img.startAnimation(downAnim);

        hd. postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!keyguardManager.isKeyguardSecure()) {
                    Intent intent1 = new Intent (getApplicationContext(), BlueLogin.class);
                    startActivity(intent1);
                }

               else {
                    Intent intent2 = new Intent(getApplicationContext(), BlueLoginFP.class);
                    startActivity(intent2);
                }
                finish();
            }
        }, 400);

        */
    }
}
