package com.example.atrue.bluelocking_final;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by true on 2016-11-05.
 */
public class BlueLoginFP extends Activity {

    private static final String KEY_NAME = "example_key";
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    private ImageView btnFavorite;

    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginfp);

        dbHelper = new DBHelper(BlueLoginFP.this, "DataBase", null, 1);
        dbHelper.testDB();

        TextView mText = (TextView)findViewById(R.id.getKey);
        mText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("KeyInfo", 0);
                String key = pref.getString("s_key", null);
                Toast.makeText(BlueLoginFP.this,key,Toast.LENGTH_LONG);
            }
        });

        keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager =
                (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (!keyguardManager.isKeyguardSecure()) {

            Toast.makeText(this,
                    "Lock screen security not enabled in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    "Fingerprint authentication permission not enabled",
                    Toast.LENGTH_LONG).show();

            return;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {

            // 지문이 하나도 등록된 게 없을 때.
            Toast.makeText(this,
                    "등록된 지문이 없습니다",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        generateKey();

        if (cipherInit()) {
            FingerprintHandler helper = new FingerprintHandler(this);
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            helper.startAuth(fingerprintManager, cryptoObject);
        }

        btnFavorite = (ImageView)findViewById(R.id.btnFavorite);

        btnFavorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String []item = new String[3];

                SharedPreferences pref = getSharedPreferences("FavoriteInfo", 0);
                String MAC = pref.getString("MAC", null);


                item[0]="바로가기 설정 OFF";
                item[1]="KEYCODE 확인";
                item[2]="Log 전송";

                new AlertDialog.Builder(BlueLoginFP.this)
                        .setTitle("옵션")
                        .setItems(item, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                if(which == 0){
                                    AlertDialog dlg = new AlertDialog.Builder(BlueLoginFP.this)
                                            .setTitle("바로가기")
                                            .setMessage("바로가기 설정을 해제하시겠습니까?")
                                            .setPositiveButton("해제",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    // OK 버튼 클릭시
                                                    SharedPreferences pref = getSharedPreferences("FavoriteInfo", 0);
                                                    SharedPreferences.Editor edit = pref.edit();
                                                    edit.putString("MAC", null);
                                                    edit.clear();
                                                    edit.commit();

                                                    Toast.makeText(BlueLoginFP.this, "바로가기 설정이 해제되었습니다.", Toast.LENGTH_LONG).show();
                                                }
                                            })
                                            .setNegativeButton("취소",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    // Cancel 버튼 클릭시

                                                }
                                            })
                                            .show();

                                    Button nbtn = dlg.getButton(DialogInterface.BUTTON_NEGATIVE);
                                    nbtn.setTextColor(Color.BLACK);
                                    Button pbtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
                                    pbtn.setTextColor(Color.BLACK);
                                }
                                if(which == 1){
                                    SharedPreferences pref = getSharedPreferences("KeyInfo", 0);
                                    String key = pref.getString("s_key", null);

                                    AlertDialog dlg = new AlertDialog.Builder(BlueLoginFP.this)
                                            .setTitle("KEYCODE")
                                            .setMessage(key)
                                            .setPositiveButton("확인",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    // OK 버튼 클릭시

                                                }
                                            })
                                            .show();

                                    Button nbtn = dlg.getButton(DialogInterface.BUTTON_NEGATIVE);
                                    nbtn.setTextColor(Color.BLACK);
                                    Button pbtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
                                    pbtn.setTextColor(Color.BLACK);
                                }
                                if(which == 2){
                                    AlertDialog dlg = new AlertDialog.Builder(BlueLoginFP.this)
                                            .setTitle("Log 전송")
                                            .setMessage("지문 인식 후에 가능합니다")
                                            .setNegativeButton("종료",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    // OK 버튼 클릭시
                                                }
                                            })
                                            .show();

                                    Button nbtn = dlg.getButton(DialogInterface.BUTTON_NEGATIVE);
                                    nbtn.setTextColor(Color.BLACK);
                                    Button pbtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
                                    pbtn.setTextColor(Color.BLACK);

                                    /*
                                    // Person 데이터를 모두 가져온다
                                    if(dbHelper == null){
                                        dbHelper = new DBHelper(BlueLoginFP.this, "DataBase", null, 1);
                                    }

                                    String tbs = dbHelper.SelectTable();

                                    Intent email = new Intent(Intent.ACTION_SEND);
                                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{"dkdlrja@naver.com"});
                                    email.putExtra(Intent.EXTRA_SUBJECT, "Log");
                                    email.putExtra(Intent.EXTRA_TEXT, tbs);
                                    email.setType("message/rfc822");
                                    startActivity(Intent.createChooser(email, "전송 수단을 선택하세요 :)"));
                                    */
                                }
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                //do whatever you want the back key to do
                            }
                        })
                        .show();
            }
        });
    }

    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            throw new RuntimeException(
                    "Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
    

    public void SendLog(){
        // Person 데이터를 모두 가져온다
        if(dbHelper == null){
            dbHelper = new DBHelper(BlueLoginFP.this, "DataBase", null, 1);
        }

        String tbs = dbHelper.SelectTable();

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"dkdlrja@naver.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Log");
        email.putExtra(Intent.EXTRA_TEXT, tbs);
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "전송 수단을 선택하세요 :)"));
    }

}

class FingerprintHandler extends
        FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context appContext;
    public int next = 0;
    public FingerprintHandler(Context context) {
        appContext = context;
    }



    public void startAuth(FingerprintManager manager,
                          FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();

        if (ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        if (next == 1) {
            return;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
        Toast.makeText(appContext,
                errString,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        Toast.makeText(appContext,
                helpString,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(appContext,
                "일치하지 않는 지문입니다",
                Toast.LENGTH_SHORT).show();
        next = 0;
    }

    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {

        Intent intent = new Intent(appContext, BlueTryConnecting.class);
        appContext.startActivity(intent);

    }
}