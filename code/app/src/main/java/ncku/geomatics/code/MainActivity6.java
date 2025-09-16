package ncku.geomatics.lyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity6 extends AppCompatActivity {

    ArrayList<String> lv = new ArrayList<>();
    ArrayAdapter<String> ap;
    ArrayList<byte[]> byteList = new ArrayList<>();
    int level,finish,point,picture;

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
        setContentView(R.layout.activity_main6);

        Intent it = getIntent();
        level = it.getIntExtra("level",0);
        finish = it.getIntExtra("finish",0);
        point = it.getIntExtra("分數",0);
        byteList = (ArrayList<byte[]>)it.getSerializableExtra("byteList");
        picture = it.getIntExtra("picture",0);

        if(level == 3){
            lv.add("衣服富翁");
            lv.add("長腿瘦子");
            ap = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,lv);
        }
        else if(level == 5) {
            lv.add("布丁小妹");
            lv.add("短髮社恐");
            ap = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lv);
        }
        else if(level == 1){
            lv.add("巧力成癮");
            lv.add("金毛加酒");
            ap = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,lv);
        }
        ((ListView)findViewById(R.id.lv_clue)).setAdapter(ap);

        ((ListView)findViewById(R.id.lv_clue)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(MainActivity6.this).setTitle("做好選擇了嗎...")
                        .setMessage("你確定要選擇"+((TextView)view).getText()+"嗎???")
                        .setPositiveButton("選擇好了!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(MainActivity6.this).setTitle("拯救成功")
                                        .setMessage("你將會獲得拯救人質分數，人質狀況請依關主說明~~")
                                        .setPositiveButton("繼續遊戲", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(finish == 3)
                                                {
                                                    Intent it_finish = new Intent(MainActivity6.this,MainActivity3.class);
                                                    it_finish.putExtra("分數",point);
                                                    it_finish.putExtra("byteList",byteList);
                                                    it_finish.putExtra("picture",picture);
                                                    startActivity(it_finish);
                                                }
                                                else
                                                    finish();
                                            }
                                        }).show();
                            }
                        }).setNegativeButton("ㄜ...再考慮一下好了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Snackbar.make(findViewById(R.id.activity6_root),"做好決定再選歐",Snackbar.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
    }
}