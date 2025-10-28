package id.akarudev.storybook.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StoryResponse {
    @SerializedName("Data")
    private List<DataStory> Data;

    public List<DataStory> getData() {
        return Data;
    }
}

