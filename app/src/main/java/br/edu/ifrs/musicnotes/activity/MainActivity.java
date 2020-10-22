package br.edu.ifrs.musicnotes.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import br.edu.ifrs.musicnotes.R;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView mainList = findViewById(R.id.mainList);
        mainList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent;
        switch (i) {
            case 0:
                intent = new Intent(this, AlbumCreateActivity.class);
                intent.putExtra("extraExample", "Novo item: ");
                startActivity(intent);
                break;
            case 1:
                String ifrsNumber = "555139306002";
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ifrsNumber));
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                break;
        }
    }
}