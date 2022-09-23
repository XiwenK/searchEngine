package Utils;

import Entity.Review;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class dataUtils {

    /**
     * Read JSON Object Array
     */
    public static List<Review> readJSON(String srcPath) {
        List<Review> reviews = new ArrayList<>();

        try {
            FileInputStream stream = new FileInputStream(srcPath);
            JsonReader reader = new JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            Gson gson = new GsonBuilder().create();

            reader.beginArray();

            while (reader.hasNext()) {
                Review review = gson.fromJson(reader, Review.class);
                // This is because there is null value in the field reviewerName.
                if (review.getReviewerName() == null) review.setReviewerName("");
                reviews.add(review);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    /**
     * Read JSON Object
     */
    public static void readJSON2() {
        try {
            String path = System.getProperty("user.dir") + "/static/samples_by_index.json";
            File file = new File(path);
            String content = FileUtils.readFileToString(file,"UTF-8");
            JSONObject jsonObject = new JSONObject(content);

            JSONObject js = jsonObject.getJSONObject("2");
            Gson gson = new GsonBuilder().create();
            Review review = gson.fromJson(String.valueOf(js), Review.class);
            System.out.println(review.getReviewText());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + "/static/samples_no_index.json";
        readJSON(path);
    }
}

