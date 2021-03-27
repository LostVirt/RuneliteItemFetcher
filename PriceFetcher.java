import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class PriceFetcher {
    private static final long HALF_HOUR = 900000;
    private static final String RUNELITE_URL = "https://prices.runescape.wiki/api/v1/osrs/latest";
    private static long cacheTime;
    private static String cache = null;

    public static ItemPrice getItemPrice(int id) {
        if (shouldUpdate())
            updateCache();
        return new Gson().fromJson(JsonParser.parseString(cache).getAsJsonObject().getAsJsonObject("data").getAsJsonObject(Integer.toString(id)), ItemPrice.class);
    }

    private static boolean shouldUpdate() {
        if (cache == null) return true;

        return System.currentTimeMillis() - cacheTime > HALF_HOUR;
    }

    private static void updateCache() {
        try {
            URLConnection urlConnection = new URL(RUNELITE_URL).openConnection();
            urlConnection.addRequestProperty("User-Agent", "Price tracker");
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder totalString = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                totalString.append(line);
            }

            cacheTime = System.currentTimeMillis();
            cache = totalString.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ItemPrice {
        int high, low;
        long highTime, lowTime;

        public int getHigh() {
            return high;
        }

        public int getLow() {
            return low;
        }

        public long getHighTime() {
            return highTime;
        }

        public long getLowTime() {
            return lowTime;
        }

    }

}
