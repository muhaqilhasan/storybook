package id.akarudev.storybook.ui;
import android.os.Bundle;
import android.view.Menu; // DITAMBAHKAN
import android.view.MenuItem; // DITAMBAHKAN
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView; // DITAMBAHKAN
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import id.akarudev.storybook.R;
import id.akarudev.storybook.adapter.StoryAdapter;
import id.akarudev.storybook.model.DataStory;
import id.akarudev.storybook.model.StoryResponse;

public class StoryListActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    StoryAdapter storyAdapter;
    List<DataStory> storyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitle("Pilih Cerita"); // Judul diubah

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadJsonData();

        storyAdapter = new StoryAdapter(this, storyList);
        recyclerView.setAdapter(storyAdapter);
    }

    private void loadJsonData() {
        String json = null;
        try {
            InputStream is = getAssets().open("Story.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<StoryResponse>() {}.getType();
            StoryResponse response = gson.fromJson(json, type);
            if (response != null && response.getData() != null) {
                storyList.addAll(response.getData());
            }
        }
    }

    // --- LOGIKA SEARCH BARU ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Panggil filter di adapter
                storyAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Panggil filter di adapter secara real-time
                storyAdapter.filter(newText);
                return true;
            }
        });

        // Saat ikon search ditutup, tampilkan kembali list penuh
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                storyAdapter.filter("");
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    // --- AKHIR LOGIKA SEARCH ---
}