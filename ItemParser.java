import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ItemParser {

    private static final String WIKI_URL = "https://prices.runescape.wiki/api/v1/osrs/mapping";
    private static final long HALF_HOUR = 900000;
    private static long cacheTime;
    private static List<ParsedItem> data = new ArrayList<>();


    public static ParsedItem get(int id){
        if (shouldUpdate())
            getData();

        return data.stream().filter(i -> i.id == id).findFirst().orElse(null);
    }

    private static boolean shouldUpdate() {
        if (data.isEmpty()) return true;

        return System.currentTimeMillis() - cacheTime > HALF_HOUR;
    }

    private static void getData() {
        try {
            URLConnection urlConnection = new URL(WIKI_URL).openConnection();
            urlConnection.addRequestProperty("User-Agent", "Item Tracker");
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder totalString = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                totalString.append(line);
            }

            data = new Gson().fromJson(JsonParser.parseString(totalString.toString()).getAsJsonArray(), new TypeToken<List<ParsedItem>>(){}.getType());
            cacheTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ParsedItem {
        int id, limit, lowalch, highalch, value;
        boolean members;
        String examine, icon, name;

        public int getId() {
            return id;
        }

        public int getLimit() {
            return limit;
        }

        public int getLowalch() {
            return lowalch;
        }

        public int getHighalch() {
            return highalch;
        }

        public int getValue() {
            return value;
        }

        public boolean isMembers() {
            return members;
        }

        public String getExamine() {
            return examine;
        }

        public String getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }
    }
}
