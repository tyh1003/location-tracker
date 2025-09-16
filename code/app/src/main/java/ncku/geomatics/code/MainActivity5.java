package ncku.geomatics.lyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity5 extends AppCompatActivity implements View.OnClickListener {

    int imv=0,score,count=0,picture_finish=0;
    ArrayList<byte[]> byteList = new ArrayList<>();

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
        setContentView(R.layout.activity_main5);

        Intent it = getIntent();
        imv = it.getIntExtra("imv", 0);
        score = it.getIntExtra("分數", 0);
        byteList = (ArrayList<byte[]>) it.getSerializableExtra("byteList");
        picture_finish++;
        /*ImageView btn=findViewById(R.id.button);
        btn.setOnClickListener(this);*/

        //Snackbar.make(findViewById(R.id.activity5_root),Integer.toString(score),Snackbar.LENGTH_SHORT).show();

        // 取得 LinearLayout
        LinearLayout linearLayout = findViewById(R.id.activity5_root);

        // 取得螢幕寬度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // 定義每一排要顯示的 ImageView 數量
        int imagesPerRow = 5;
        int imageViewWidth = (screenWidth - 110) / imagesPerRow;

        // 計算總共需要多少排
        int numRows = (byteList.size() + imagesPerRow - 1) / imagesPerRow;

        // 迴圈生成 ImageView
        for (int row = 0; row < numRows; row++) {
            // 創建一個新的 LinearLayout 作為每一排
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            // 迴圈在每一排生成 ImageView
            for (int i = row * imagesPerRow; i < Math.min((row + 1) * imagesPerRow, byteList.size()); i++) {
                // 創建一個新的 ImageView
                ImageView imageView = new ImageView(this);

                // 解碼 byte 數據並設置到 ImageView
                Bitmap bmp = BitmapFactory.decodeByteArray(byteList.get(i), 0, byteList.get(i).length);
                imageView.setImageBitmap(bmp);

                // 設定 ImageView 之間的間距
                int margin = 10;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        imageViewWidth,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(margin, margin, margin, margin);
                imageView.setLayoutParams(params);

                // 將 ImageView 添加到排的 LinearLayout
                rowLayout.addView(imageView);

                // 設定 OnClickListener
                imageView.setOnClickListener(this);

            }
            // 將排的 LinearLayout 添加到主 LinearLayout
            linearLayout.addView(rowLayout);
        }
    }


    @Override
    public void onClick(View v) {

            new AlertDialog.Builder(MainActivity5.this).setTitle("這是控制點嗎...").setMessage("這到底是不是控制點呢...")
                    .setPositiveButton("這是歐!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            v.getId();
                            v.setVisibility(View.INVISIBLE);
                            score++;
                            count++;
                            if (count == byteList.size()) {
                                Intent it_back = new Intent(MainActivity5.this, MainActivity3.class);
                                it_back.putExtra("分數", score);
                                it_back.putExtra("picture_finish",picture_finish);
                                startActivity(it_back);
                            }
                        }
                    }).setNegativeButton("騙人...再亂拍阿", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Snackbar.make(findViewById(R.id.activity5_root), "騙人不好歐", Snackbar.LENGTH_LONG).show();
                            v.getId();
                            v.setVisibility(View.INVISIBLE);
                            count++;
                            if (count == byteList.size()) {
                                Intent it_back = new Intent(MainActivity5.this, MainActivity3.class);
                                it_back.putExtra("分數", score);
                                it_back.putExtra("picture_finish",picture_finish);
                                startActivity(it_back);
                            }
                        }
                    }).show();

        }

}