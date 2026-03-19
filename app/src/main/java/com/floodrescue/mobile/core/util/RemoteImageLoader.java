package com.floodrescue.mobile.core.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class RemoteImageLoader {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(3);
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private RemoteImageLoader() {
    }

    public static void load(ImageView imageView, String imageUrl) {
        if (imageView == null) {
            return;
        }
        imageView.setTag(imageUrl);
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageView.setImageDrawable(null);
            return;
        }

        EXECUTOR.execute(() -> {
            Bitmap bitmap = download(imageUrl.trim());
            MAIN_HANDLER.post(() -> {
                Object currentTag = imageView.getTag();
                if (currentTag == null || !imageUrl.equals(currentTag.toString())) {
                    return;
                }
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            });
        });
    }

    private static Bitmap download(String imageUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setDoInput(true);
            connection.connect();
            try (InputStream inputStream = connection.getInputStream()) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
