package id.akarudev.storybook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList; // DITAMBAHKAN
import java.util.List;
import id.akarudev.storybook.R;
import id.akarudev.storybook.model.DataStory;
import id.akarudev.storybook.ui.DetailStoryActivity;
import id.akarudev.storybook.util.Constans;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    Context context;
    List<DataStory> storyList; // Ini adalah daftar yang ditampilkan (bisa berubah)
    List<DataStory> storyListFull; // Ini adalah daftar penuh (backup)

    public StoryAdapter(Context context, List<DataStory> storyList) {
        this.context = context;
        this.storyList = storyList;
        // Buat salinan dari daftar asli untuk pencarian
        this.storyListFull = new ArrayList<>(storyList);
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        DataStory story = storyList.get(position); // Ambil dari storyList yang sudah difilter
        holder.tvJudul.setText(story.getTitle());

        try {
            InputStream is = context.getAssets().open("image/" + story.getImage());
            Drawable d = Drawable.createFromStream(is, null);
            holder.imgCerita.setImageDrawable(d);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            holder.imgCerita.setImageResource(R.mipmap.ic_launcher);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailStoryActivity.class);
                intent.putExtra(Constans.KEY_STORY_DATA, story);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size(); // Hanya hitung daftar yang ditampilkan
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCerita;
        TextView tvJudul;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCerita = itemView.findViewById(R.id.imgCerita);
            tvJudul = itemView.findViewById(R.id.tvJudul);
        }
    }

    // --- METODE FILTER BARU ---
    public void filter(String text) {
        storyList.clear(); // Hapus daftar yang ditampilkan saat ini

        if (text.isEmpty()) {
            // Jika teks pencarian kosong, tampilkan kembali semua cerita
            storyList.addAll(storyListFull);
        } else {
            text = text.toLowerCase().trim();
            // Loop melalui daftar penuh (backup)
            for (DataStory story : storyListFull) {
                // Jika judul cerita mengandung teks pencarian
                if (story.getTitle().toLowerCase().contains(text)) {
                    // Tambahkan ke daftar yang akan ditampilkan
                    storyList.add(story);
                }
            }
        }
        // Beri tahu adapter bahwa data telah berubah
        notifyDataSetChanged();
    }
    // --- AKHIR METODE FILTER ---
}