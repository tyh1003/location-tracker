package ncku.geomatics.lyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    //第一頁(開頭頁面)
    //播放音樂
    //選擇組別
    //遊戲開始
    int team=0;

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
        setContentView(R.layout.activity_main);

        ArrayAdapter ad=new ArrayAdapter(this,R.layout.listitem,getResources().getStringArray(R.array.組別));
        Spinner sp=findViewById(R.id.spinner);
        sp.setAdapter(ad);
        sp.setOnItemSelectedListener(this);

        ImageView im1=findViewById(R.id.imv_start);
        im1.setOnClickListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        team=position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v)
    {
        if(team!=0)
        {
            Intent it=new Intent(this,MainActivity2.class);
            it.putExtra("組別",team);
            startActivity(it);
        }
        else{
            Toast.makeText(this, "*請選擇組別*", Toast.LENGTH_SHORT).show();
        }
    }
}