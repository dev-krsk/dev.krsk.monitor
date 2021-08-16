package dev.krsk.monitor.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.UUID;

public class Camera implements ClusterItem {
    private final String id;
    private final LatLng position;
    private final float angle;

    private final String title;
    private final String snippet;

    private final String urlPreview;
    private final String urlSource;

    public Camera(LatLng position, float angle, String title, String snippet, String url, String preview) {
        this.id = UUID.randomUUID().toString();
        this.position = position;
        this.angle = angle;
        this.title = title;
        this.snippet = snippet;
        this.urlSource = url;
        this.urlPreview = preview;
    }

    public Camera(LatLng position, float angle, String url, String preview) {
        this(position, angle, "Без названия", "", url, preview);
    }

    public Camera(LatLng position, String url, String preview) {
        this(position, 0, url, preview);
    }

    public Camera(double latitude, double longitude, float angle, String title, String snippet, String url, String preview) {
        this(new LatLng(latitude, longitude), angle, title, snippet, url, preview);
    }

    public Camera(double latitude, double longitude, float angle, String title, String url, String preview) {
        this(new LatLng(latitude, longitude), angle, title, "", url, preview);
    }

    public Camera(double latitude, double longitude, float angle, String url, String preview) {
        this(new LatLng(latitude, longitude), angle, "Без названия", "", url, preview);
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    public String getId() {
        return id;
    }

    public float getAngle() {
        return angle;
    }

    @Nullable
    public String getUrlSource() {
        return urlSource;
    }

    @Nullable
    public String getUrlPreview() {
        return urlPreview;
    }
}
