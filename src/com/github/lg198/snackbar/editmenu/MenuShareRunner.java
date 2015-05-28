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

    public static boolean isCodeValid(String code) {
        for (Character c : code.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static void downloadMenu(EditMenuActivity a, String code) {
        String url = RAW_DOWNLOAD_URL + code;

        ConnectivityManager connMgr = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new MenuShareDownloadAsyncTask().doInBackground(url, a.getMenuFile());
        } else {
            //TODO: HANDLE ERROR
        }

    }

    private static class MenuShareDownloadAsyncTask extends AsyncTask<Object, Void, MenuShareDownloadResult> {


        @Override
        protected MenuShareDownloadResult doInBackground(Object... params) {
            MenuShareDownloadResult result;
            try {
                try {
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


    private static class MenuShareUpwnloadAsyncTask extends AsyncTask<Object, Void, MenuShareUploadResult> {


        @Override
        protected MenuShareUploadResult doInBackground(Object... params) {
            MenuShareUploadResult result;
            try {
                try {
                    upload((String) params[0], (File) params[1]);
                } catch (IOException e) {
                    throw new ReportableException(ReportableException.NETWORK_ERROR, e);
                }
            } catch (ReportableException e) {
                result = new MenuShareUploadResult(e);
            }
            return result;
        }

        private void upload(String url, File f) throws IOException, ReportableException {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            InputStream is = null;
            OutputStream os = null;
            try {
                connection.connect();
                is = connection.getInputStream();
                os = connection.getOutputStream();
                fillPost(os, f);
            } finally {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            }
        }

        private void fillPost(OutputStream os, File f) throws IOException, ReportableException {
            String req = "";
            req += "key=" + URLEncoder.encode(PASTE_KEY, "UTF-8");
            req += "&description=" + URLEncoder.encode("A SnackBar Menu", "UTF-8");
            req += "&expire=60";
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
    }
}
