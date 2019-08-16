package com.company;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        String get_url = "https://candidate.hubteam.com/candidateTest/v3/problem/dataset?userKey=1cae96d3904b260d06d0daa7387c";
        String post_url = "https://candidate.hubteam.com/candidateTest/v3/problem/result?userKey=1cae96d3904b260d06d0daa7387c";
        post_date(get_url, post_url);
        /*for(Partner p: partners) {
            System.out.println(p.getDates());
        }*/
        /*JSONObject jsonObject = new JSONObject();
        //粉丝是个数组,其实就是嵌套json
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("name","小王");
        jsonObject1.put("age",7);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("name","小尼玛");
        jsonObject2.put("age",10);

        //从此处可以看出其实list和json也是互相转换的
        List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        jsonObjects.add(jsonObject1);
        jsonObjects.add(jsonObject2);
        jsonObject.put("fans",jsonObjects);

        System.out.println(jsonObject);*/
    }
    private static void post_date(String get_url, String post_url) throws Exception {
        String result = readFromUrl(get_url);
        JSONObject jsonObject = JSONObject.fromObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("partners");
        List<Partner> partners = new ArrayList<>();
        for(int i = 0; i<jsonArray.size();i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            Partner partner = new Partner();
            partner.setFirstName(object.get("firstName").toString());
            partner.setLastName(object.get("lastName").toString());
            partner.setCountry(object.get("country").toString());
            partner.setEmail(object.get("email").toString());
            JSONArray array = object.getJSONArray("availableDates");
            List<String> list = new ArrayList<>();
            list.addAll(array);
            partner.setDates(list);
            partners.add(partner);
        }
        Map<String, List<Partner>> countries = new HashMap<>();
        for(Partner p: partners) {
            String country = p.getCountry();
            if(countries.containsKey(country)) {
                countries.get(country).add(p);
            } else {
                List<Partner> list = new ArrayList<>();
                list.add(p);
                countries.put(country, list);
            }
        }
        JSONObject res = new JSONObject();
        List<JSONObject> jsonObjects = new ArrayList<>();
        for(String country:countries.keySet()) {
            Map<String, Set<String>> dates = new HashMap<>();
            for(Partner partner: countries.get(country)) {
                List<String> p_date = new ArrayList<>();
                p_date.addAll(partner.getDates());
                //System.out.println("p_date" + p_date);
                for(int j = 1; j<p_date.size();j++) {
                    if(adjacent(p_date.get(j - 1), p_date.get(j))) {
                        if(dates.containsKey(p_date.get(j - 1))) {
                            dates.get(p_date.get(j - 1)).add(partner.getEmail());
                        } else {
                            Set<String> set = new HashSet<>();
                            set.add(partner.getEmail());
                            dates.put(p_date.get(j - 1), set);
                        }
                    }
                }
            }

            find_overlap(jsonObjects, dates, country);
        }
        res.put("countries", jsonObjects);
        //System.out.println(res);
        URL obj = new URL(post_url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent","application/x-java-serialized-object");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = res.toString();

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);
    }
    private static void find_overlap(List<JSONObject> jsonObjects, Map<String, Set<String>> dates, String country) throws Exception {
        JSONObject object = new JSONObject();
        if(dates == null  || dates.isEmpty()) {
            object.put("attendeeCount", 0);
            object.put("attendees", "[]");
            object.put("name", country);
            object.put("startDate", "");

        } else {
            int desiredLen = 0;
            String desiredDate = "";
            for(String date: dates.keySet()) {
                if(desiredDate == null) {
                    desiredLen = Math.max(desiredLen, dates.get(date).size());
                    desiredDate = date;
                } else if(dates.get(date).size() > desiredLen) {
                    desiredLen = dates.get(date).size();
                    desiredDate = date;
                } else if(dates.get(date).size() == desiredLen && previous(desiredDate, date)) {
                    desiredDate = date;
                }
            }
            object.put("attendeeCount", dates.get(desiredDate).size());
            object.put("attendees", dates.get(desiredDate));
            object.put("name", country);
            object.put("startDate", desiredDate);
        }
        jsonObjects.add(object);

    }
    private static boolean adjacent(String date1, String date2) throws Exception {
        SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdfo.parse(date1);
        Date d2 = sdfo.parse(date2);
        return d2.getDate()-  d1.getDate() == 1;
    }
    private static boolean previous(String date1, String date2) throws Exception {
        SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdfo.parse(date1);
        Date d2 = sdfo.parse(date2);
        return d1.getDate()-  d2.getDate() >= 1;
    }

    private static String readFromUrl(String url) throws Exception {
        URL oracle = new URL(url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        String inputLine;
        StringBuilder sb = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine);
        in.close();
        return sb.toString();
    }
    /*public static Object readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();

        Object jsonObject = null;
        JSONParser parser = new JSONParser();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            String jsonText = sb.toString();
            jsonObject = parser.parse(jsonText);


        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return jsonObject;
    }*/
}
