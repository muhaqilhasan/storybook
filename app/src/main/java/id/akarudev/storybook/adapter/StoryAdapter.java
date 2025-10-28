package id.akarudev.storybook.adapter; // Paket diubah

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable; // <-- DITAMBAHKAN
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException; // <-- DITAMBAHKAN
import java.io.InputStream; // <-- DITAMBAHKAN
import java.util.List;
// Import diubah
import id.akarudev.storybook.R;
import id.akarudev.storybook.model.DataStory;
import id.akarudev.storybook.ui.DetailStoryActivity;
import id.akarudev.storybook.util.Constans;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    Context context;
    List<DataStory> storyList; // Diubah ke DataStory

    public StoryAdapter(Context context, List<DataStory> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Layout item diubah
        View view = LayoutInflater.from(context).inflate(R.layout.list_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        DataStory story = storyList.get(position);
        holder.tvJudul.setText(story.getTitle());

        // --- PERUBAHAN: Memuat Gambar dari Assets ---
        try {
            // Buka folder "image" di assets, lalu ambil file berdasarkan nama
            InputStream is = context.getAssets().open("image/" + story.getImage());
            // Buat Drawable dari input stream
            Drawable d = Drawable.createFromStream(is, null);
            // Set gambar ke ImageView
            holder.imgCerita.setImageDrawable(d);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            holder.imgCerita.setImageResource(R.mipmap.ic_launcher); // Fallback jika gagal
        }
        // --- Akhir Perubahan ---

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengirim seluruh objek DataStory
                Intent intent = new Intent(context, DetailStoryActivity.class);
                intent.putExtra(Constans.KEY_STORY_DATA, story); // Menggunakan kunci dari Constans
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCerita;
        TextView tvJudul;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // ID dari list_story.xml
            imgCerita = itemView.findViewById(R.id.imgCerita);
            tvJudul = itemView.findViewById(R.id.tvJudul);
        }
    }
}