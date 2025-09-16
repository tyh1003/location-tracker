package ncku.geomatics.lyp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity4 extends AppCompatActivity implements SensorEventListener,DialogInterface.OnClickListener{

    ImageView im1,im2,im3;
    ConstraintLayout.LayoutParams p1,p2,p3;
    Vibrator vb;
    SensorManager sm;
    Sensor c;
    Handler handler = new Handler();
    Runnable runnable;
    int count=0,seconds=30,score;

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
        setContentView(R.layout.activity_main4);

        Intent it_score = getIntent();
        score = it_score.getIntExtra("分數",0);

        im1 = findViewById(R.id.im_4_1); //三腳架
        im2 = findViewById(R.id.im_4_2); //全站儀
        im3 = findViewById(R.id.im_4_3); //皮捲尺

        p3=(ConstraintLayout.LayoutParams)im3.getLayoutParams();
        p3.width = 200;
        p3.height = 200;

        p1=(ConstraintLayout.LayoutParams)im1.getLayoutParams();
        p1.width = 325;
        p1.height = 325;

        vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        String g = "請晃動手機，靠自己的力量移動\"腳架\"接住\"儀器\"，**切記**碰到\"皮捲尺\"會扣分!!。限時30秒，目標為5分!!!!";

        AlertDialog.Builder talk = new AlertDialog.Builder(this);
        talk.setTitle("遊戲說明");
        talk.setMessage(g);
        talk.setPositiveButton((CharSequence) "確定", (DialogInterface.OnClickListener) this);
        talk.show();

        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        c=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //隨機移動
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moveIm3Randomly();
                    }
                });
            }
        }, 0, 700);


        //計時器
        runnable = new Runnable() {
            @Override
            public void run() {
                TextView tv = findViewById(R.id.tv_4_time);
                tv.setText("剩餘時間:" + seconds + "秒");
                if (seconds == 0) {
                    if(count == 5){
                        Snackbar.make(findViewById(R.id.root_4),"遊戲成功，加分!!",Snackbar.LENGTH_SHORT).show();
                        Intent it2 = new Intent();
                        it2.setClass(MainActivity4.this,MainActivity3.class);
                        it2.putExtra("分數",score+5);
                        it2.putExtra("game",2);
                        Snackbar.make(findViewById(R.id.root_4),"恭喜獲得加分!",Snackbar.LENGTH_SHORT).show();
                        startActivity(it2);
                    }
                    else{
                        Snackbar.make(findViewById(R.id.root_4),"遊戲失敗，扣分!!",Snackbar.LENGTH_SHORT).show();
                        Intent it2=new Intent();
                        it2.setClass(MainActivity4.this,MainActivity3.class);
                        it2.putExtra("分數",score-3);
                        it2.putExtra("game",3);
                        Snackbar.make(findViewById(R.id.root_4),"喔喔...被扣分了!!",Snackbar.LENGTH_SHORT).show();
                        startActivity(it2);
                    }
                }
                else {
                    seconds -= 1;
                    tv.setText("剩餘時間:" + seconds + "秒");
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.postDelayed(runnable,1000);
    }

    void moveIm3Randomly() {
        float randomHorizontalBias = p2.horizontalBias + new Random().nextFloat() * 0.3f - new Random().nextFloat() * 0.3f;
        float randomVerticalBias = p2.verticalBias + new Random().nextFloat() * 0.3f - new Random().nextFloat() * 0.3f;
        while(randomHorizontalBias - p2.horizontalBias < 0.03f)
            randomHorizontalBias = p2.horizontalBias + new Random().nextFloat() * 0.3f - new Random().nextFloat() * 0.3f;
        while(randomVerticalBias - p2.verticalBias < 0.03f)
            randomVerticalBias = p2.verticalBias + new Random().nextFloat() * 0.3f - new Random().nextFloat() * 0.3f;
        p3.horizontalBias = randomHorizontalBias;
        p3.verticalBias = randomVerticalBias;
        im3.setLayoutParams(p3);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //第一個圖片
        p1 = (ConstraintLayout.LayoutParams)im1.getLayoutParams();
        if(Math.abs(event.values[0])-Math.abs(event.values[1])>1.5 && event.values[0]>0){
            if(p1.horizontalBias-0.02f<0){
                p1.horizontalBias=0;
            }
            else{
                p1.horizontalBias -= 0.02f;
            }
            im1.setLayoutParams(p1);
        }
        else if(Math.abs(event.values[0])-Math.abs(event.values[1])>1.5 && event.values[0]<0){
            if(p1.horizontalBias+0.02f>1){
                p1.horizontalBias=1;
            }
            else{
                p1.horizontalBias+=0.02f;
            }
            im1.setLayoutParams(p1);
        }
        else if(Math.abs(event.values[1])-Math.abs(event.values[0])>1.5 && event.values[1]>0){
            if(p1.verticalBias+0.02f>1){
                p1.verticalBias=1;
            }
            else{
                p1.verticalBias+=0.02f;
            }
            im1.setLayoutParams(p1);
        }
        else if(Math.abs(event.values[1])-Math.abs(event.values[0])>1.5 && event.values[1]<0){
            if(p1.verticalBias-0.02f<0){
                p1.verticalBias=0;
            }
            else{
                p1.verticalBias-=0.02f;
            }
            im1.setLayoutParams(p1);
        }

        //第二個圖片(加分)
        p2 = (ConstraintLayout.LayoutParams)im2.getLayoutParams();
        if(Math.abs(p1.horizontalBias-p2.horizontalBias)<0.1 && Math.abs(p1.verticalBias-p2.verticalBias)<0.1){
            p2.horizontalBias = 0.1f + new Random().nextFloat()*0.7f;
            p2.verticalBias = 0.1f + new Random().nextFloat()*0.7f;
            im2.setLayoutParams(p2);
            count += 1;

            TextView tv = findViewById(R.id.tv_4_score);
            tv.setText(String.valueOf("分數:"+count));
            if(count == 5){
                Snackbar.make(findViewById(R.id.root_4),"遊戲成功，加分!!",Snackbar.LENGTH_SHORT).show();
                Intent it2=new Intent();
                it2.setClass(MainActivity4.this,MainActivity3.class);
                it2.putExtra("分數",score+5);
                it2.putExtra("game",2);
                Snackbar.make(findViewById(R.id.root_4),"恭喜獲得加分!",Snackbar.LENGTH_SHORT).show();
                startActivity(it2);
            }
        }

        //第三個圖片(扣分)
        if(Math.abs(p1.horizontalBias-p3.horizontalBias)<0.1 && Math.abs(p1.verticalBias-p3.verticalBias)<0.1){
            p2.horizontalBias = 0.1f + new Random().nextFloat()*0.7f;
            p2.verticalBias = 0.1f + new Random().nextFloat()*0.7f;
            im2.setLayoutParams(p2);
            moveIm3Randomly();
            vb.cancel();
            vb.vibrate(1500);
            count -= 1 ;
            TextView tv=findViewById(R.id.tv_4_score);
            tv.setText(String.valueOf("分數:"+count));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null)
            sm.registerListener(this,c,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }
}