package com.effectsar.labcv.core.license;
import com.effectsar.labcv.licenselibrary.HttpRequestProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class EffectHttpRequestProvider implements HttpRequestProvider {
    @Override
    public ResponseInfo getRequest(RequestInfo requestInfo) {
        return new ResponseInfo();
    }

    @Override
    public ResponseInfo postRequest(RequestInfo requestInfo) {
        ResponseInfo resultInfo = new ResponseInfo();
        OutputStream out = null;
        InputStream in = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(requestInfo.url).openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            for (Map.Entry<String, String> entry : requestInfo.requestHead.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            out = conn.getOutputStream();
            out.write(requestInfo.bodydata.getBytes());
            out.flush();
            resultInfo.status_code = conn.getResponseCode();
            if (resultInfo.status_code == HttpURLConnection.HTTP_OK) {
                in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    response.append(line);
                resultInfo.bodydata = response.toString();
                resultInfo.bodySize = resultInfo.bodydata.length();
                resultInfo.userdata = requestInfo.userdata;
                resultInfo.isSuc = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultInfo;
    }
}
