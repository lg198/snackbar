package com.github.lg198.snackbar.editmenu;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import com.github.lg198.snackbar.ReportableException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MenuShareRunner {

    private static final String PASTE_KEY = "eef081f7a6d25de667894071b339ee84";

    private static final String RAW_DOWNLOAD_URL = "http://paste.ee/r/";
    private static final String API_URL = "http://paste.ee/api";

    public static boolean isCodeValid(String code) {
        for (Character c : code.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static void downloadMenu(EditMenuActivity a, String code, MenuShareDownloadCallback callback) throws ReportableException {
        String url = RAW_DOWNLOAD_URL + code;

        ConnectivityManager connMgr = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new MenuShareDownloadAsyncTask().execute(url, a.getMenuFile(), callback);
        } else {
            throw new ReportableException(ReportableException.NETWORK_NOT_CONNECTED);
        }
    }

    public static void uploadMenu(EditMenuActivity a, MenuShareUploadCallback callback) throws ReportableException {
        String url = API_URL;

        ConnectivityManager connMgr = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new MenuShareUploadAsyncTask().execute(url, a.getMenuFile(), callback);
        } else {
            throw new ReportableException(ReportableException.NETWORK_NOT_CONNECTED);
        }
    }

    private static class MenuShareDownloadAsyncTask extends AsyncTask<Object, Void, MenuShareDownloadResult> {

        private MenuShareDownloadCallback callback;


        @Override
        protected MenuShareDownloadResult doInBackground(Object... params) {
            MenuShareDownloadResult result;
            try {
                try {
                    callback = (MenuShareDownloadCallback) params[2];
                    InputStream is = getInputStream((String) params[0]);
                    readAndWriteFiles(is, (File) params[1]);
                    result = new MenuShareDownloadResult();
                } catch (IOException e) {
                    throw new ReportableException(ReportableException.NETWORK_ERROR, e);
                }
            } catch (ReportableException e) {
                result = new MenuShareDownloadResult(e);
            }
            return result;
        }

        @Override
        public void onPostExecute(MenuShareDownloadResult res) {
            callback.finished(res);
        }

        private InputStream getInputStream(String url) throws IOException, ReportableException {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            if (connection.getResponseCode() != 200) {
                throw new ReportableException(ReportableException.MENU_SHARE_DOWNLOAD_NOT_FOUND);
            }
            InputStream is = connection.getInputStream();

            return is;
        }

        public void readAndWriteFiles(InputStream is, File result) throws IOException, ReportableException {
            try (FileOutputStream fos = new FileOutputStream(result);) {
                byte[] buff = new byte[32 * 1024];
                for (int bread; (bread = is.read(buff)) > 0; ) {
                    fos.write(buff, 0, bread);
                }
            } finally {
                is.close();
            }
        }

    }


    private static class MenuShareUploadAsyncTask extends AsyncTask<Object, Void, MenuShareUploadResult> {

        private MenuShareUploadCallback callback;


        @Override
        protected MenuShareUploadResult doInBackground(Object... params) {
            MenuShareUploadResult result;
            try {
                try {
                    callback = (MenuShareUploadCallback) params[2];
                    String code = upload((String) params[0], (File) params[1]);
                    result = new MenuShareUploadResult(code);
                } catch (IOException e) {
                    throw new ReportableException(ReportableException.NETWORK_ERROR, e);
                }
            } catch (ReportableException e) {
                result = new MenuShareUploadResult(e);
            }
            return result;
        }

        @Override
        public void onPostExecute(MenuShareUploadResult res) {
            callback.finished(res);
        }

        private String upload(String url, File f) throws IOException, ReportableException {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            InputStream is = null;
            OutputStream os = null;
            String code = null;
            try {
                os = connection.getOutputStream();
                fillPost(os, f);
                connection.connect();
                is = connection.getInputStream();
                code = readCode(is);
            } finally {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            }
            return code;
        }

        private void fillPost(OutputStream os, File f) throws IOException, ReportableException {
            String req = "";
            req += "key=" + URLEncoder.encode(PASTE_KEY, "UTF-8");
            req += "&description=" + URLEncoder.encode("A SnackBar Menu", "UTF-8");
            req += "&expire=1800";
            req += "&format=simple";
            req += "&return=id";

            String contents = "";
            BufferedReader br = new BufferedReader(new FileReader(f));
            for (String line; (line = br.readLine()) != null; ) {
                contents += line;
            }
            br.close();

            req += "&paste=" + URLEncoder.encode(contents, "UTF-8");

            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
            pw.append(req);
            pw.flush();
            pw.close();
        }

        private String readCode(InputStream is) throws IOException, ReportableException {
            String contents = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (String line; (line = br.readLine()) != null; ) {
                contents += line;
            }
            br.close();
            if (contents.trim().startsWith("error")) {
                throw new ReportableException(ReportableException.MENU_SHARE_UPLOAD_SERVER_ERROR, new RuntimeException(contents));
            }
            return contents.trim();
        }
    }
}
