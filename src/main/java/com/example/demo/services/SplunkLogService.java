package com.example.demo.services;

import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.demo.services.ServicesConstants.SPLUNK_HEC_URL;
import static com.example.demo.services.ServicesConstants.SPLUNK_TOKEN;

@Service
public class SplunkLogService {


    public Boolean logToSplunk(SplunkLog splunkLog) {
        try {
            URL url = new URL(SPLUNK_HEC_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", SPLUNK_TOKEN);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = splunkLog.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int HttpResult = con.getResponseCode();
            System.out.println("http result->"+HttpResult);
            return (HttpResult == HttpURLConnection.HTTP_OK);

        } catch (Exception e) {
            System.out.println("exception+>"+e.getMessage());
            return false;
        }

    }



}



/** public class ServicesConstants {
    public static final String SPLUNK_HEC_URL = "http://localhost:8088/services/collector";
    public static final String SPLUNK_TOKEN = "Splunk 2911a5e0-4345-4dac-a727-3d6c682a40c7";
} */
