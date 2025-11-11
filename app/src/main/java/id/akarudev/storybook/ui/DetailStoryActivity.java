package id.akarudev.storybook.ui;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import com.google.android.material.button.MaterialButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import id.akarudev.storybook.R;
import id.akarudev.storybook.model.DataStory;
import id.akarudev.storybook.util.Constans;

public class DetailStoryActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextView tvDetailJudul, tvDescription;
    ImageView imgCover;
    WebView webViewDetail;
    MaterialButton btnListen, btnRead;

    DataStory story;
    String storyText; // Teks untuk TTS

    MediaPlayer mediaPlayer;
    boolean isAudioFileReady = false;
    boolean isPlaying = false;
    TextToSpeech textToSpeech;
    boolean isTTSReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        tvDetailJudul = findViewById(R.id.tvDetailJudul);
        tvDescription = findViewById(R.id.tvDescription);
        imgCover = findViewById(R.id.imgCover);
        webViewDetail = findViewById(R.id.webViewDetail);
        btnListen = findViewById(R.id.btnListen);
        btnRead = findViewById(R.id.btnRead);

        story = (DataStory) getIntent().getSerializableExtra(Constans.KEY_STORY_DATA);

        if (story == null) {
            Toast.makeText(this, "Gagal memuat cerita", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        storyText = story.getTts_text();
        String storyHtml = story.getStory_text();

        tvDetailJudul.setText(story.getTitle());

        if (storyText != null && !storyText.isEmpty()) {
            if (storyText.length() > 200) {
                tvDescription.setText(storyText.substring(0, 200) + "...");
            } else {
                tvDescription.setText(storyText);
            }
        } else {
            tvDescription.setText("Tidak ada deskripsi.");
        }

        try {
            InputStream is = getAssets().open("image/" + story.getImage());
            Drawable d = Drawable.createFromStream(is, null);
            imgCover.setImageDrawable(d);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            imgCover.setImageResource(R.mipmap.ic_launcher);
        }

        // Memuat data ke WebView dengan tema yang sudah disesuaikan
        setupWebView(storyHtml);

        textToSpeech = new TextToSpeech(this, this);

        if (story.getAudio().equals("GT")) {
            // Siap untuk TTS
        } else {
            setupMediaPlayer(story.getAudio());
        }

        btnListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (story.getAudio().equals("GT")) {
                    playTextToSpeech();
                } else {
                    playAudioFile();
                }
            }
        });

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleReadView();
            }
        });
    }

    private void setupWebView(String storyHtml) {
        webViewDetail.getSettings().setJavaScriptEnabled(true);
        // Set latar belakang WebView menjadi transparan agar latar belakang layout terlihat
        webViewDetail.setBackgroundColor(Color.TRANSPARENT);

        String themedHtml;
        if (storyHtml == null || storyHtml.isEmpty()) {
            // Tampilkan pesan error dengan gaya tema gelap
            themedHtml = "<html><body style='color: #FFFFFF; background-color: transparent;'>Cerita tidak ditemukan.</body></html>";
        } else {
            // --- INI ADALAH PERUBAHAN PENTING ---
            // 1. Ganti CSS bawaan (teks hitam, latar putih) dengan CSS tema baru (teks putih, latar transparan)
            themedHtml = storyHtml.replace(
                    "color: #000000; background-color: #FFFFFF;",
                    "color: #FFFFFF; background-color: transparent;"
            );

            // 2. Tambahkan gaya untuk tag <em> (italic) agar warnanya kuning (accentYellow)
            // Kita menyisipkannya tepat sebelum tag </style> penutup
            themedHtml = themedHtml.replace(
                    "</style>",
                    " em { color: #FFD400; font-style: italic; } </style>"
            );
        }

        // Muat HTML yang sudah dimodifikasi
        webViewDetail.loadDataWithBaseURL(null, themedHtml, "text/html; charset=utf-8", "UTF-8", null);
    }

    private void toggleReadView() {
        if (webViewDetail.getVisibility() == View.GONE) {
            webViewDetail.setVisibility(View.VISIBLE);
            tvDescription.setVisibility(View.GONE);
            btnRead.setText("Hide Text");
            btnRead.setIconResource(R.drawable.ic_review); // (Contoh: ikon mata tertutup/hide)
        } else {
            webViewDetail.setVisibility(View.GONE);
            tvDescription.setVisibility(View.VISIBLE);
            btnRead.setText("Read");
            btnRead.setIconResource(R.drawable.ic_read);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("id", "ID"));
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

        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            btnListen.setText("Listen");
            btnListen.setIconResource(R.drawable.ic_listen);
            isPlaying = false;
        } else {
            textToSpeech.setPitch(1.0f);
            textToSpeech.setSpeechRate(1.0f);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(storyText, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                textToSpeech.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
            }
            btnListen.setText("Pause");
            btnListen.setIconResource(R.drawable.ic_pause);
            isPlaying = true;
        }
    }

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
                    btnListen.setText("Listen");
                    btnListen.setIconResource(R.drawable.ic_listen);
                    isPlaying = false;
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
                btnListen.setText("Listen");
                btnListen.setIconResource(R.drawable.ic_listen);
                isPlaying = false;
            } else {
                mediaPlayer.start();
                btnListen.setText("Pause");
                btnListen.setIconResource(R.drawable.ic_pause);
                isPlaying = true;
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
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}