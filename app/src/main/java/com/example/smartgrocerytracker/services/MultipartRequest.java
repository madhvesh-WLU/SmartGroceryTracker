package com.example.smartgrocerytracker.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<JSONObject> {
    private final Response.Listener<JSONObject> mListener;
    private final Map<String, String> mHeaders;
    private final File mFile;
    private final String mFileKey;

    public MultipartRequest(
            String url,
            File file,
            String fileKey,
            Context context,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener
    ) {
        super(Method.POST, url, errorListener);
        this.mListener = listener;
        this.mFile = file;
        this.mFileKey = fileKey;
        this.mHeaders = new HashMap<>();
        // Add the Authorization Bearer Token using Context
        String token = SecurePreferences.getAuthToken(context);
        if (token != null) {
            this.mHeaders.put("Authorization", "Bearer " + token);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + BOUNDARY;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // Write the boundary
            bos.write(("--" + BOUNDARY + "\r\n").getBytes());

            // Add file field
            bos.write(("Content-Disposition: form-data; name=\"" + mFileKey + "\"; filename=\"" + mFile.getName() + "\"\r\n").getBytes());
            bos.write(("Content-Type: image/jpeg\r\n\r\n").getBytes());

            // Write file content
            FileInputStream fis = new FileInputStream(mFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            fis.close();

            // Write the closing boundary
            bos.write(("\r\n--" + BOUNDARY + "--\r\n").getBytes());
        } catch (Exception e) {
            VolleyLog.e("Error while creating request body: " + e.getMessage());
        }

        Log.d("MultipartRequest", "Request Body: " + new String(bos.toByteArray()));
        return bos.toByteArray();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            // Convert response data to string
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            // Parse the string to JSON object
            JSONObject jsonObject = new JSONObject(jsonString);
            return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException | JSONException e) {
            Log.e("MultipartRequest", "Error parsing response: " + e.getMessage());
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }

    private static final String BOUNDARY = "----WebKitFormBoundary";
}