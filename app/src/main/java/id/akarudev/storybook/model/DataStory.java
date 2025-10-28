package id.akarudev.storybook.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

// Implementasi Serializable agar objek bisa dikirim via Intent
public class DataStory implements Serializable {
    @SerializedName("title")
    private String title;
    @SerializedName("story_text") // Field ini sekarang berisi HTML lengkap
    private String story_text;
    @SerializedName("tts_text") // Field baru untuk plain text (untuk TTS)
    private String tts_text;
    @SerializedName("audio")
    private String audio;
    @SerializedName("image")
    private String image;
    // Field css_style dihapus

    // Getter
    public String getTitle() {
        return title;
    }
    public String getStory_text() { // Mengembalikan HTML
        return story_text;
    }
    public String getTts_text() { // Getter baru untuk TTS
        return tts_text;
    }
    public String getAudio() {
        return audio;
    }
    public String getImage() {
        return image;
    }
    // Getter getCss_style() dihapus
}
