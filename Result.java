import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.JSONListAdapter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

class Result {

    /*
     * Complete the 'getTopRatedFoodOutlets' function below.
     *
     * URL for cut and paste
     * https://jsonmock.hackerrank.com/api/food_outlets?city=<city>&page=<pageNumber>
     *
     * The function is expected to return an array of strings.
     *
     * The function accepts only city argument (String).
     */


    private static int getPageSize(StringBuilder sb) {
        StringBuilder pageSizeApi = new StringBuilder(sb);
        pageSizeApi.append("&page=1");

        int size = 0;


        try {
            URL url = new URL(String.valueOf(pageSizeApi));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("Content-Type", "application/json");
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);

            }
            inputStream.close();
            bufferedReader.close();

            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine engine = scriptEngineManager.getEngineByName("javascript");

            String script = "Java.asJSONCompatible(" + String.valueOf(response) + ")";

            Object result = engine.eval(script);
            Map<String, Object> data = (Map<String, Object>) result;
            String total = String.valueOf(data.getOrDefault("total_pages", "1"));
            size = Integer.valueOf(total);

            //System.out.println(size + " ...........");
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return size;

    }


    static class Pair implements Comparable<Pair> {

        public String name;
        public double ratings;

        public double getRatings() {
            return ratings;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setRatings(double ratings) {
            this.ratings = ratings;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "name='" + name + '\'' +
                    ", ratings=" + ratings +
                    '}';
        }

        @Override
        public int compareTo(Pair o) {
            return Double.valueOf(o.ratings).compareTo(Double.valueOf(this.ratings));
        }
    }


    public static List<String> getTopRatedFoodOutlets(String city) {
        StringBuilder sb = new StringBuilder("https://jsonmock.hackerrank.com/api/food_outlets?city=" + city);
        int pageSize = getPageSize(sb);
        List<String> res = new LinkedList<>();
        List<Pair> pairs = new ArrayList<Pair>();
        for (int idx = 1; idx <= pageSize; idx++) {
            try {
                StringBuilder nextRequest = new StringBuilder(sb);
                nextRequest.append("&page=" + idx);
                URL url = new URL(String.valueOf(nextRequest));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.addRequestProperty("Content-Type", "application/json");
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                inputStream.close();
                bufferedReader.close();
                //System.out.println(response);
                ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
                ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
                String script = "Java.asJSONCompatible(" + response + ")";
                Object result = engine.eval(script);
                //Map<String, Object> contents = (Map<String, Object>)result;
                Map<String, JSONListAdapter> other = (Map<String, JSONListAdapter>) result;
                JSONListAdapter data = other.get("data");

                for (int i = 0; i < data.size(); i++) {
                    Pair pair = new Pair();
                    pair.ratings = Double.valueOf(String.valueOf(((ScriptObjectMirror) (((ScriptObjectMirror) data.get(i)).get("user_rating"))).get("average_rating")));
                    pair.name = String.valueOf(((ScriptObjectMirror) data.get(0)).get("name"));
                    pairs.add(pair);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

         Collections.sort(pairs);

        for(int idx =0; idx < pairs.size(); idx++)
            if(res.contains(pairs.get(idx).getName()) == false)
                res.add(pairs.get(idx).getName());


        return res;
    }

    public static void main(String[] ars) {
        List<String> ansList = getTopRatedFoodOutlets("Seattle");
        ansList.forEach(System.out::println);
    }

}
