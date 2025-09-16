package ncku.geomatics.lyp;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity2<imm> extends AppCompatActivity
        implements View.OnClickListener, LocationListener, OnMapReadyCallback, DialogInterface.OnClickListener//, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener
{
    //第二頁(地圖頁面)
    //播放音樂
    //顯示第幾關
    //尋找控制點
    //地圖(功能:可以顯示自己的位置&關卡相對位置，跟作業的概念一樣//一關結束後跳下一關的位置(輸入密碼後)，共有六關)
    //地圖上放指標(ex.1、4關指正確位置、2、5關指北針 、3、6關亂轉)
    //輸入通關密碼(成功通關密碼一組；失敗通關密碼一組(成功計分，失敗扣分)；其餘數字都跳出密碼錯誤)，輸入密碼後跳到下一關
    //關卡座標NE
    // 1(120.219829,22.999335)；2(120.219519,22.996494)；3(120.219011,22.998412)
    // 4(120.218915,22.998124)；5(120.218604,23.000829)；6(120.220522,23.000478)

    LocationManager lm;
    double lat = 0, lng = 0, lat2 = 0, lng2 = 0, d = 0;
    double[] latitude = {0, 22.998659, 22.999225, 22.998509, 22.998124, 23.000829, 23.000478};
    double[] longitude = {0, 120.219958, 120.220574, 120.218652, 120.218915, 120.218604, 120.220522};
    TextView tv;
    private GoogleMap mMap;
    SensorManager sm;
    private SensorListener listener = new SensorListener();
    ImageView iv1, iv2, iv3, iv4;
    double ag = 0;
    int level, done = 0,dire=0,finish = 0;
    int count = 1;//闖關數
    int point = 0;//分數
    int pass = 0;
    int picture = 0;
    AlertDialog AD, AD2, AD3;
    ArrayList<String> AL;
    ByteArrayOutputStream stream;
    ArrayList<byte[]> byteList = new ArrayList<>();
    //InputMethodManager imm;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            return true;
        else
            return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ImageView bt1 = findViewById(R.id.btn_change); //照相
        ImageView bt2 = findViewById(R.id.btn_find); //定位
        Button bt3=findViewById(R.id.btn_enter);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);

        Intent it = getIntent();
        level = it.getIntExtra("組別", 0)*2-1;
        tv = findViewById(R.id.tv_number);
        AL = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.關卡)));
        tv.setText(AL.get(level));

        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        iv3 = findViewById(R.id.iv3);
        iv4 = findViewById(R.id.iv4);

        if (level == 1 || level == 4)//相對位置
        {
            iv3.setVisibility(View.VISIBLE);
        }
        else if (level == 2 || level == 5)//指北針
        {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
        }
        else if (level == 3 || level == 6)//誤導
        {
            iv4.setVisibility(View.VISIBLE);
        }

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            return;
        }
        lm.requestLocationUpdates("gps", 1000, 0, this);
        sm = (SensorManager) getSystemService(Service.SENSOR_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_change) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);

            Intent it_cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(it_cam, 87878);
        }
        else if (v.getId() == R.id.btn_find && done == 1) {
            lat2 = latitude[level];
            lng2 = longitude[level];
            dire=1;
            if(level == 3 || level == 6)
            {
                iv4.setRotation((int) (Math.random() * 361));
                RotateAnimation animation = new RotateAnimation((int)(Math.random() * 360), (int)(Math.random() * 360),
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(1000);
                iv4.startAnimation(animation);
            }
        }
        else if (v.getId() == R.id.btn_enter) {
            EditText et = findViewById(R.id.edt);
            String code = et.getText().toString();
            if (code.length() > 0) {
                if (code.equals("success"))//成功
                {
                    pass = 1;
                    point += 1;
                    if (count != 6) {
                        AD = new AlertDialog.Builder(this)
                                .setTitle("成功")
                                .setMessage("恭喜你們「" + tv.getText() + "」闖關成功!!!\n請前往下一關~")
                                .setCancelable(false)
                                .setPositiveButton("OK", this)
                                .show();
                    }
                    else {
                        AD = new AlertDialog.Builder(this)
                                .setTitle("成功")
                                .setMessage("恭喜你們最後一關闖關成功!!!")
                                .setCancelable(false)
                                .setPositiveButton("OK", this)
                                .show();
                    }
                }
                else if (code.equals("failure"))//失敗
                {
                    pass = 2;
                    point -= 1;
                    if (count != 6) {
                        AD = new AlertDialog.Builder(this)
                                .setTitle("失敗")
                                .setMessage("你們「" + tv.getText() + "」闖關失敗QwQ\n請前往下一關~")
                                .setCancelable(false)
                                .setPositiveButton("OK", this)
                                .show();
                    }
                    else {
                        AD = new AlertDialog.Builder(this)
                                .setTitle("失敗")
                                .setMessage("你們最後一關闖關失敗QwQ")
                                .setCancelable(false)
                                .setPositiveButton("OK", this)
                                .show();
                    }
                }
                else {
                    AD2 = new AlertDialog.Builder(this)
                            .setTitle("密碼錯誤")
                            .setMessage("請重新輸入通關密碼!\n(提醒：請勿暴力破解^^)")
                            .setCancelable(false)
                            .setPositiveButton("OK", this)
                            .show();
                }
            }
            else {
                AD2 = new AlertDialog.Builder(this)
                        .setTitle("密碼錯誤")
                        .setMessage("請重新輸入通關密碼!\n(提醒：請勿暴力破解^^)")
                        .setCancelable(false)
                        .setPositiveButton("OK", this)
                        .show();
            }
            et.setText("");
            //imm.hideSoftInputFromWindow(MainActivity2.this.getCurrentFocus().getWindowToken(),0);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        if (Math.abs(lat - latitude[level]) < 0.00001 && Math.abs(lng - longitude[level]) < 0.00001) {
            Snackbar.make(findViewById(R.id.activity2_root),"找到關卡",Snackbar.LENGTH_SHORT).show();
        }

        done = 1;
        if(dire==1)
        {
            d = Distance(lng, lat, lng2, lat2);
            ag = angle(lat, lng, lat2, lng2);
        }
        int zoom = 18;
        if (d >= 150 && d < 320) {
            zoom = 17;
        }
        else if (d >= 320 && d < 800) {
            zoom = 16;
        }
        else if (d > 800) {
            zoom = 15;
        }
        if (mMap != null) {
            mMap.clear();
            LatLng loc = new LatLng(lat2, lng2);
            LatLng currPoint = new LatLng(lat, lng);
            LatLng camera = new LatLng((lat + lat2) / 2, (lng + lng2) / 2);
            mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            mMap.addMarker(new MarkerOptions().position(currPoint).title("目前位置")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            if (lat2 != 0 && lng2 != 0) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(camera));
                mMap.addMarker(new MarkerOptions().position(loc).title("人質可能位置")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            } else {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(currPoint));
            }
        }
    }

    public double Distance(double lng, double lat, double lng2, double lat2) {
        double distance;
        Location user = new Location("");
        user.setLatitude(lat);
        user.setLongitude(lng);
        Location loc = new Location("");
        loc.setLatitude(lat2);
        loc.setLongitude(lng2);
        distance = Math.round(user.distanceTo(loc) * 100.00) / 100.00;
        return distance;
    }

    public double angle(double lat, double lng, double lat2, double lng2)//控制點方位角
    {
        double a = 0;
        lat = lat * Math.PI / 180;
        lng = lng * Math.PI / 180;
        lat2 = lat2 * Math.PI / 180;
        lng2 = lng2 * Math.PI / 180;
        a = Math.sin(lat) * Math.sin(lat2) + Math.cos(lat) * Math.cos(lat2) * Math.cos(lng2 - lng);
        a = Math.sqrt(1 - a * a);
        a = Math.cos(lat2) * Math.sin(lng2 - lng) / a;
        a = Math.asin(a) * 180 / Math.PI;
        if (lat2 > lat) {
            if (lng2 < lng) {
                a = a + 360;
            }
        } else if (lat2 < lat) {
            if (lng2 > lng) {
                a = 180 - a;
            } else if (lng2 < lng) {
                a = -a + 180;
            }
        }
        return a;
    }

    private final class SensorListener implements SensorEventListener //指標
    {
        private float predegree = 0, predegree2 = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float degree = (float) ag - event.values[0];
            float degree2 = -event.values[0];
            RotateAnimation animation = new RotateAnimation(predegree, degree,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(1000);
            RotateAnimation animation2 = new RotateAnimation(predegree2, degree2,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation2.setDuration(1000);

            if ((level == 1 || level == 4) && ag != 0)//相對位置
            {
                iv3.startAnimation(animation);
            }
            else if (level == 2 || level == 5)//指北針
            {
                iv2.startAnimation(animation2);
            }
            predegree = degree;
            predegree2 = degree2;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng CENTER = new LatLng(22.998432, 120.219393);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CENTER));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            return;
        }
        //lm.requestLocationUpdates("gps", 1000, 0, this);
        Sensor sr = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sm.registerListener(listener, sr, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //lm.removeUpdates(this);
        sm.unregisterListener(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 87878) {
            picture ++;
            Bundle extras = data.getExtras();
            Bitmap bmp = (Bitmap)extras.get("data");
            stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteList.add(stream.toByteArray());
        }
        else
            Snackbar.make(findViewById(R.id.activity2_root), "error", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (dialog == AD) {
            lat2 = 0;
            lng2 = 0;
            d = 0;
            ag = 0;
            dire=0;

            if (count != 7) {
                count++;
                if (level != 6) {
                    level++;
                }
                else {
                    level = 1;
                }

                tv.setText(AL.get(level));

                iv1.setVisibility(View.INVISIBLE);
                iv2.setVisibility(View.INVISIBLE);
                iv3.setVisibility(View.INVISIBLE);
                iv4.setVisibility(View.INVISIBLE);

                if (level == 1 || level == 4)//相對位置
                {
                    iv3.setVisibility(View.VISIBLE);
                }
                else if (level == 2 || level == 5)//指北針
                {
                    iv1.setVisibility(View.VISIBLE);
                    iv2.setVisibility(View.VISIBLE);
                }
                else if (level == 3 || level == 6)//誤導
                {
                    iv4.setVisibility(View.VISIBLE);
                }
            }

            if(level == 1 || level == 3 || level == 5)
            {
                finish ++ ;

                if(pass == 1){
                    point += 1 ;
                    Intent clue = new Intent(MainActivity2.this,MainActivity6.class);
                    clue.putExtra("level",level);
                    clue.putExtra("finish",finish);
                    clue.putExtra("分數", point);
                    clue.putExtra("byteList",byteList);
                    clue.putExtra("picture",picture);
                    startActivity(clue);
                }
                else if(pass == 2 && finish == 3){
                    Snackbar.make(findViewById(R.id.activity2_root),"很抱歉...你們沒辦法救出人質",Snackbar.LENGTH_SHORT).show();
                    Intent it2 = new Intent(this, MainActivity3.class);
                    it2.putExtra("分數", point);
                    it2.putExtra("byteList",byteList);
                    it2.putExtra("picture",picture);
                    startActivity(it2);
                }
            }

            /*
            if(count == 7){
                Intent it2 = new Intent(this, MainActivity3.class);
                it2.putExtra("分數", point);
                it2.putExtra("byteList",byteList);
                it2.putExtra("picture",picture);
                startActivity(it2);
            }
            */
        }
    }
}