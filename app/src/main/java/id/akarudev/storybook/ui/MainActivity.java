package id.akarudev.storybook.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.material.button.MaterialButton; // DIUBAH
import androidx.appcompat.app.AppCompatActivity;
import id.akarudev.storybook.BuildConfig;
import id.akarudev.storybook.R;

public class MainActivity extends AppCompatActivity {

    MaterialButton btnStart, btnShare, btnReview, btnExit; // Tipe diubah & btnExit ditambah

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnShare = findViewById(R.id.btnShare);
        btnReview = findViewById(R.id.btnReview);
        btnExit = findViewById(R.id.btnExit); // BARU

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StoryListActivity.class));
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareLink = "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " - " + shareLink);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
            }
        });

        // --- LOGIKA TOMBOL EXIT BARU ---
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Menutup semua activity dan keluar dari aplikasi
                finishAffinity();
            }
        });
        // --- AKHIR LOGIKA TOMBOL EXIT ---
    }
}