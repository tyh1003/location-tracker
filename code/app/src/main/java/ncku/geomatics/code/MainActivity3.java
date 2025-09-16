package ncku.geomatics.lyp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;

public class MainActivity3 extends AppCompatActivity implements View.OnClickListener,DialogInterface.OnClickListener, View.OnLongClickListener {
    //遊戲結束頁面
    //播放音樂
    //計分板，顯示總分(成功通關次數(一關5分)+失敗次數(一關3分)+人質(一人10分)+控制點(一張3分)+bonus總得分)
    //bonus
    //結束按鍵
    int score,bonus=0,game=0,imv=0,picture=0,picture_finish=0;//;
    Vibrator vb;
    ArrayList<byte[]> byteList = new ArrayList<>();

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            return true;
        else
            return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Intent it2 = getIntent();
        bonus = it2.getIntExtra("bonus",0);
        score = it2.getIntExtra("分數",0);
        game = it2.getIntExtra("game",0);
        imv = it2.getIntExtra("imv",0);
        picture = it2.getIntExtra("picture",0);
        picture_finish=it2.getIntExtra("picture_finish",0);//
        byteList = (ArrayList<byte[]>)it2.getSerializableExtra("byteList");

        if(game == 2)
            Snackbar.make(findViewById(R.id.activity3_root),"恭喜獲得加分!",Snackbar.LENGTH_SHORT).show();
        else if(game == 3)
            Snackbar.make(findViewById(R.id.activity3_root),"喔喔...被扣分了!!",Snackbar.LENGTH_SHORT).show();

        ImageView btn=findViewById(R.id.btn_bonus);
        btn.setOnClickListener(this);

        TextView tv=findViewById(R.id.tv_2_4);
        tv.setText("\n\n你們的總分為:\n"+score);
        /*TextView tv1=findViewById(R.id.tv_2_2);
        tv1.setText("恭喜各位銀河護衛隊練習生\n接下來\n還會有甚麼挑戰等著你們呢...?");*/
        vb = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        ImageView btn2=findViewById(R.id.btn_end);
        btn2.setOnClickListener(this);
        btn2.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_bonus){
            if(game == 0&&picture_finish==1){
                AlertDialog.Builder game=new AlertDialog.Builder(this);
                game.setTitle("神祕通道");
                String g="恭喜你發現神秘通道!按下確定後，將進入BONUS加分遊戲，若遊戲成功，將會獲得遊戲大~加分；反之，失敗了則會分數分數減少...";
                game.setMessage(g);
                game.setPositiveButton((CharSequence)"確定", (DialogInterface.OnClickListener) this);
                game.setNegativeButton("取消",this);
                game.show();
            }
            else if(game==0&&picture==0){
                AlertDialog.Builder game=new AlertDialog.Builder(this);
                game.setTitle("神祕通道");
                String g="恭喜你發現神秘通道!按下確定後，將進入BONUS加分遊戲，若遊戲成功，將會獲得遊戲大~加分；反之，失敗了則會分數分數減少...";
                game.setMessage(g);
                game.setPositiveButton((CharSequence)"確定", (DialogInterface.OnClickListener) this);
                game.setNegativeButton("取消",this);
                game.show();
            }
            else{
                Snackbar.make(findViewById(R.id.activity3_root),"BONUS ERROR",Snackbar.LENGTH_LONG).show();
            }

        }
        else if(v.getId()==R.id.btn_end){
            if (picture != 0) {
                Intent it_imv = new Intent(MainActivity3.this,MainActivity5.class);
                it_imv.putExtra("byteList",byteList);
                it_imv.putExtra("imv",imv);
                it_imv.putExtra("分數",score);
                startActivity(it_imv);
            }
            else
                Snackbar.make(findViewById(R.id.activity3_root),"ERROR",Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        if(which==DialogInterface.BUTTON_POSITIVE){
                Intent it = new Intent(MainActivity3.this, MainActivity4.class);
                it.putExtra("分數", score);
                startActivity(it);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        finishAffinity();
        return true;
    }
}