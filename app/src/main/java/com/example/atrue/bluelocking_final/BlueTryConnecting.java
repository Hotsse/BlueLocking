package com.example.atrue.bluelocking_final;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class BlueTryConnecting extends Activity {

    private DBHelper dbHelper;

    final int MAX_CNT = 20;

    private static final String baseURL = "http://210.117.188.54/bluelocking/log.php";


    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothadapter = null;

    static private BluetoothAdapter bluetoothAdapter;
    static private BluetoothDevice bluetoothDevice = null;
    static private BluetoothSocket bluetoothSocket;

    static private InputStream btIn;
    static private OutputStream btOut;

    public static AppCompatActivity activity;

    public static boolean mFlag = false;
    public static String mKey;

    private TimerTask mTask;
    private Timer mTimer;

    private TextView resultText, resultText2;

    private Button discbtn;
    private ImageView btnFavorite;

    String recentBtIn;

    private int cnt=1;
    private int test=0;
    private int myCNT = 20;

    private int testcnt=0;


    public void DisplayResult(int flag){
        resultText = (TextView)findViewById(R.id.result_text);
        resultText2 = (TextView)findViewById(R.id.result_text2);

        discbtn = (Button)findViewById(R.id.discbtn);

        if(flag == 0){
            resultText.setText("PC 통신 상태 불량");
            Toast.makeText(getApplicationContext(), "PC 통신 상태를 확인하세요", Toast.LENGTH_SHORT).show();
            System.exit(0);

            return;
        }
        else if(flag == 1){
            doClose(); mFlag=true;
            resultText.setText("인증 정보 불일치");
            Toast.makeText(getApplicationContext(), "인증화면으로 돌아갑니다", Toast.LENGTH_SHORT).show();

            Handler hd = new Handler();
            hd. postDelayed(new Runnable() {
                @Override
                public void run() {
                    Disc(1);
                }
            }, 3000);

            return;
        }
        else if(flag == 2){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
            Date date = new Date();
            String strDate = mKey + " try to connect to " + bluetoothDevice.toString() + ", when " + dateFormat.format(date);
            Log.d("HoseLog", strDate);

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("device",mKey);
            params.put("pc",bluetoothDevice.toString());
            params.put("time",dateFormat.format(date));
            params.put("act", "conn");
            client.post(baseURL, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                    String result = ""+new String(bytes);

                }

                @Override
                public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {

                }
            });

            //SQLITE
            if(dbHelper == null){
                dbHelper = new DBHelper(BlueTryConnecting.this, "DataBase", null, 1);
            }

            TableBowl tb = new TableBowl();
            tb.setDeviceKey(mKey);
            tb.setPcAddress(bluetoothDevice.toString());
            tb.setTime(dateFormat.format(date));
            tb.setAct("conn");

            dbHelper.InsertTable(tb);

            resultText.setText("인증 정보 확인 중...");

            Handler hd = new Handler();
            hd. postDelayed(new Runnable() {
                @Override
                public void run() {

                    //resultText.setText("연결 중...");
                    //discbtn.setVisibility(View.VISIBLE);
                    ConnectStart();
                }
            }, 1000);

        }
        else if(flag == 3){
            doClose(); mFlag=true;
            resultText.setText("등록 성공");
            Toast.makeText(getApplicationContext(), "재접속 해 주십시오", Toast.LENGTH_SHORT).show();

            Handler hd = new Handler();
            hd. postDelayed(new Runnable() {
                @Override
                public void run() {
                    Disc(1);
                }
            }, 3000);

            return;
        }

        return ;
    }

    public void ConnectStart(){


        mTask = new TimerTask() {
            @Override
            public void run() {
                BluetoothAdapter bluetoothDefaultAdapter = BluetoothAdapter.getDefaultAdapter();
                if ((bluetoothDefaultAdapter != null) && (bluetoothDefaultAdapter.isEnabled())) {
                    String str = mKey;
                    //String str = BluetoothAdapter.getDefaultAdapter().getAddress().toString();
                    doSend(str);
                    Log.d("HoseLog", str);
                }
                //doSend("Send");
            }
        };

        mTimer = new Timer();

        mTimer.schedule(mTask, 1000, 1000);
    }

    public void Disc(int flag){

        if(flag == 1) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
            Date date = new Date();
            String strDate = mKey + " disc to " + bluetoothDevice.toString() + ", when " + dateFormat.format(date);
            Log.d("HoseLog", strDate);

            //MainDB
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("device",mKey);
            params.put("pc",bluetoothDevice.toString());
            params.put("time",dateFormat.format(date));
            params.put("act", "disc");
            client.post(baseURL, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                    String result = ""+new String(bytes);

                }

                @Override
                public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {

                }
            });

            //SQLITE
            if(dbHelper == null){
                dbHelper = new DBHelper(BlueTryConnecting.this, "DataBase", null, 1);
            }

            TableBowl tb = new TableBowl();
            tb.setDeviceKey(mKey);
            tb.setPcAddress(bluetoothDevice.toString());
            tb.setTime(dateFormat.format(date));
            tb.setAct("disc");

            dbHelper.InsertTable(tb);

            Handler hd = new Handler();
            hd. postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    System.exit(0);
                }
            }, 200);
        }
        else {
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dbHelper = new DBHelper(BlueTryConnecting.this, "DataBase", null, 1);
        dbHelper.testDB();

        SharedPreferences pref = getSharedPreferences("KeyInfo", 0);
        String s_key = pref.getString("s_key", null);
        if(s_key == null){
            Disc(0);
        }else{
            mKey = s_key;
        }

        mFlag=false;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_connecting);

        resultText = (TextView)findViewById(R.id.result_text);

        discbtn = (Button)findViewById(R.id.discbtn);

        discbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mTimer.cancel();
                doClose();
                Disc(1);
            }
        });

        btnFavorite = (ImageView)findViewById(R.id.btnFavorite);

        btnFavorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String []item = new String[3];

                SharedPreferences pref = getSharedPreferences("FavoriteInfo", 0);
                String MAC = pref.getString("MAC", null);

                if(MAC == null){
                    item[0]="바로가기 설정 ON";
                }
                else{
                    item[0]="바로가기 설정 OFF";
                }
                item[1]="KEYCODE 확인";
                item[2]="Log 전송";

                new AlertDialog.Builder(BlueTryConnecting.this)
                        .setTitle("옵션")
                        .setItems(item, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                if(which == 0){

                                    if(bluetoothDevice.toString() != null) {

                                        SharedPreferences pref = getSharedPreferences("FavoriteInfo", 0);
                                        String MAC = pref.getString("MAC", null);
                                        if(MAC == null){
                                            AlertDialog dlg = new AlertDialog.Builder(BlueTryConnecting.this)
                                                    .setTitle("바로가기")
                                                    .setMessage("바로가기 설정을 하시겠습니까?\n\n기기명 : " + bluetoothDevice.getName())
                                                    .setPositiveButton("설정",new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            // OK 버튼 클릭시
                                                            SharedPreferences pref = getSharedPreferences("FavoriteInfo", 0);
                                                            SharedPreferences.Editor edit = pref.edit();
                                                            edit.putString("MAC", bluetoothDevice.toString());
                                                            edit.commit();

                                                            Toast.makeText(BlueTryConnecting.this, "MAC : " + bluetoothDevice.toString(), Toast.LENGTH_LONG).show();
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
                                        else{
                                            AlertDialog dlg = new AlertDialog.Builder(BlueTryConnecting.this)
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

                                                            Toast.makeText(BlueTryConnecting.this, "바로가기 설정이 해제되었습니다.", Toast.LENGTH_LONG).show();
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

                                    }
                                    else{
                                        Toast.makeText(BlueTryConnecting.this, "바로가기 설정은 통신 중에만 가능합니다", Toast.LENGTH_LONG).show();
                                    }
                                }
                                if(which == 1){
                                    SharedPreferences pref = getSharedPreferences("KeyInfo", 0);
                                    String key = pref.getString("s_key", null);

                                    AlertDialog dlg = new AlertDialog.Builder(BlueTryConnecting.this)
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

                                    // Person 데이터를 모두 가져온다
                                    if(dbHelper == null){
                                        dbHelper = new DBHelper(BlueTryConnecting.this, "DataBase", null, 1);
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


        mBluetoothadapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothadapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않습니다", Toast.LENGTH_LONG).show();
        }
        else {
            if(!mBluetoothadapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }
            else displayPaired();
        }

    }

    //뒤로가기 버튼 눌렀을 때
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("연결 종료")
                .setMessage("종료를 누르시면 통신이 종료됩니다.")
                .setPositiveButton("종료",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // OK 버튼 클릭시
                        Disc(1);
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

    private void displayPaired() {
        Set<BluetoothDevice> pairedDevices = mBluetoothadapter.getBondedDevices();

        if (pairedDevices.size() > 0) {

            // 페어링 된 장치가 있는 경우.
            final BluetoothDevice[] devices = pairedDevices.toArray(new BluetoothDevice[0]);

            String[] items = new String[devices.length];
            for (int i = 0; i < devices.length; i++) items[i] = devices[i].getName();


            SharedPreferences pref = getSharedPreferences("FavoriteInfo", 0);
            String MAC = pref.getString("MAC", null);

            //바로가기 설정 없음
            if(MAC == null){
                new AlertDialog.Builder(this)
                        .setTitle("페어링 연결 기기 목록")
                        .setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which){

                                doConnect(devices[which]);
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                //do whatever you want the back key to do
                                Disc(0);
                            }
                        })
                        .show();
            }
            else{
                for(int i=0; i<devices.length; i++){
                    if(devices[i].toString().equals(MAC)){
                        doConnect(devices[i]);
                    }
                }
            }


        }
        else {
            // 페어링 된 장치가 없는 경우.
            Toast.makeText(this, "연결 불가", Toast.LENGTH_SHORT).show();
            Disc(0);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    displayPaired();
                }
                else {
                    Toast.makeText(this, "블루투스 연결 후에 진행할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    Disc(0);
                }
                break;
        }
    }

    public void doConnect(BluetoothDevice address) {

        bluetoothDevice = address;
        //Standard SerialPortService ID
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try {
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            new ConnectTask().execute();

        } catch (IOException e) {
            Disc(0);
        }
    }

    public void doClose() {
        new CloseTask().execute();
    }

    public void doSend(String msg) {
        new SendTask().execute(msg);

    }

    public void signalFault() {

        if(!mFlag) {

            myCNT--;

            if (myCNT < MAX_CNT - 1) {
                resultText.setText("블루투스 신호가 미약합니다.");
                resultText2.setVisibility(View.VISIBLE);

                resultText2.setText(String.format("%02d:%02d", myCNT / 60, myCNT % 60));

                String str = String.format("Receive=onPost : %d", myCNT);
                Log.d("Ho", str);

                if (myCNT <= 0) {
                    doClose();
                    mTimer.cancel();
                    Disc(1);
                }
            }
        }
    }

    //Task------------------------------------------------------------------------------------------
    private class ConnectTask extends AsyncTask<Void, Void, Object> {
        private boolean connectFlag = false;
        @Override
        protected void onPreExecute() {
            resultText.setText("연결 시도 중...");
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                bluetoothSocket.connect();
                btIn = bluetoothSocket.getInputStream();
                btOut = bluetoothSocket.getOutputStream();
            } catch (Throwable t) {
                doClose();
                Disc(0);
                Log.d("Ho", "Error to ConnectTask");
                return t;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Throwable) {
                if(!connectFlag && cnt > 0){
                    cnt--;
                    if(cnt <= 0) {
                        connectFlag = true;
                        DisplayResult(0);
                    }
                }
            } else {
                if(!connectFlag){
                    connectFlag=true;
                    DisplayResult(2);
                }
            }
        }
    }

    private class SendTask extends AsyncTask<String, Void, Object> {

        private boolean flag;
        @Override
        protected Object doInBackground(String... params) {
            try {
                flag=false;
                if (btOut != null) {
                    btOut.write(params[0].getBytes());
                    btOut.flush();
                }

                byte[] buff = new byte[512];
                if (btIn.available() > 2) {
                    int len = 0;

                    if (btIn != null) {
                        len = btIn.read(buff);
                    }
                    String str;
                    str = new String(buff, 0, len);
                    recentBtIn = str;
                    Log.d("Ho", str);
                    /*
                    if(str.equals("NO_AUTH")){
                        DisplayResult(1);
                        return null;
                    }
                    */



                    if (myCNT < MAX_CNT) myCNT = -1;
                    else myCNT = MAX_CNT;

                    return new String(buff, 0, len);
                } else {
                    Log.d("Ho", "error:signal fault!");
                    flag=true;
                }

                return null;

            } catch (IOException t) {
                Log.d("Ho","Error to SendTask");
                return t;
            }
        }

        @Override
        protected void onPostExecute(Object result) {

            if(myCNT == -1){

                myCNT = MAX_CNT;
                if(recentBtIn.equals("NO_AUTH")){
                    DisplayResult(1);
                }
                else if(recentBtIn.equals("REGISTERED")){
                    DisplayResult(3);
                }
                else{
                    /*
                    testcnt++;
                    if(testcnt > 2) {

                    }
                    */
                    resultText.setText("통신 중...");
                    resultText2.setVisibility(View.INVISIBLE);
                }
            }
            if (result instanceof IOException || flag) {
                signalFault();
            }
        }
    }

    private class CloseTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... params) {
            try {
                try{btOut.close();}catch(Throwable t){/*ignore*/}
                try{btIn.close();}catch(Throwable t){/*ignore*/}

                bluetoothSocket.close();
            } catch (Throwable t) {
                return t;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Throwable) {
                Log.d("Ho", "Error to CloseTask");
            }
        }
    }

}