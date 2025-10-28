package id.akarudev.storybook.ui; // Paket diubah

import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable; // <-- DITAMBAHKAN
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.ImageView; // <-- DITAMBAHKAN
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream; // <-- DITAMBAHKAN
import java.util.Locale;
// Import diubah
import id.akarudev.storybook.R;
import id.akarudev.storybook.model.DataStory;
import id.akarudev.storybook.util.Constans;

public class DetailStoryActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextView tvDetailJudul;
    ImageView imgCover; // <-- DITAMBAHKAN
    WebView webViewDetail;
    FloatingActionButton btnPlay;

    DataStory story; // Menggunakan objek DataStory
    String storyText; // Tetap simpan story text untuk TTS

    MediaPlayer mediaPlayer;
    boolean isAudioFileReady = false;
    TextToSpeech textToSpeech;
    boolean isTTSReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout diubah
        setContentView(R.layout.activity_story_detail);

        tvDetailJudul = findViewById(R.id.tvDetailJudul);
        imgCover = findViewById(R.id.imgCover); // <-- DITAMBAHKAN
        webViewDetail = findViewById(R.id.webViewDetail);
        btnPlay = findViewById(R.id.btnPlay);

        // Mengambil objek DataStory dari Intent
        story = (DataStory) getIntent().getSerializableExtra(Constans.KEY_STORY_DATA);

        if (story == null) {
            Toast.makeText(this, "Gagal memuat cerita", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // (PERUBAHAN) Simpan text untuk TTS dari field 'tts_text'
        storyText = story.getTts_text();

        // (PERUBAHAN) Ambil HTML lengkap dari 'story_text'
        String storyHtml = story.getStory_text();

        // Tampilkan data
        tvDetailJudul.setText(story.getTitle());

        // --- PERUBAHAN: Memuat Gambar Cover dari Assets ---
        try {
            // Buka folder "image" di assets, lalu ambil file berdasarkan nama
            InputStream is = getAssets().open("image/" + story.getImage());
            // Buat Drawable dari input stream
            Drawable d = Drawable.createFromStream(is, null);
            // Set gambar ke ImageView
            imgCover.setImageDrawable(d);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Set placeholder jika gagal
            imgCover.setImageResource(R.mipmap.ic_launcher);
        }
        // --- Akhir Perubahan ---

        // --- PERUBAHAN: Memuat data ke WebView ---

        // (PERBAIKAN 1) Aktifkan JavaScript
        webViewDetail.getSettings().setJavaScriptEnabled(true);

        // (PERBAIKAN 2) Cek jika HTML null atau kosong
        if (storyHtml == null || storyHtml.isEmpty()) {
            storyHtml = "<html><body>Cerita tidak ditemukan.</body></html>";
        }

        // (PERBAIKAN 3) Gunakan loadDataWithBaseURL untuk memuat HTML lengkap dari JSON
        webViewDetail.loadDataWithBaseURL(null, storyHtml, "text/html; charset=utf-8", "UTF-8", null);

        // --- Akhir Perubahan ---

        textToSpeech = new TextToSpeech(this, this);

        // --- PERUBAHAN: Logika Setup Audio ---
        // Cek jenis audio untuk setup (logika setText dihapus)
        if (story.getAudio().equals("GT")) {
            // Jika "GT", tombol akan menggunakan TextToSpeech.
        } else {
            // Jika file audio, setup MediaPlayer.
            setupMediaPlayer(story.getAudio());
        }
        // --- Akhir Perubahan ---


        // --- PERUBAHAN: Listener Tombol Tunggal ---
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cek tipe audio di dalam listener
                if (story.getAudio().equals("GT")) {
                    playTextToSpeech();
                } else {
                    playAudioFile();
                }
            }
        });
        // --- Akhir Perubahan ---
    }

    // --- Logika Text-to-Speech (GT) ---
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("id", "ID")); // Bahasa Indonesia
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Bahasa tidak didukung!", Toast.LENGTH_SHORT).show();
            } else {
                isTTSReady = true;
            }
        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    private void playTextToSpeech() {
        if (!isTTSReady) {
            Toast.makeText(this, "Text-to-Speech belum siap.", Toast.LENGTH_SHORT).show();
            return;
        }
        textToSpeech.setPitch(1.0f);
        textToSpeech.setSpeechRate(1.0f);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(storyText, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    // --- Logika MediaPlayer (File Audio) ---
    private void setupMediaPlayer(String fileName) {
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = getAssets().openFd("audio/" + fileName);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            isAudioFileReady = true;

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.seekTo(0);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memuat file audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudioFile() {
        if (isAudioFileReady) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
