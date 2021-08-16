package dev.krsk.monitor.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import dev.krsk.monitor.util.ObjectUtils;
import dev.krsk.monitor.vo.Camera;

public class MapViewModel extends ViewModel {
    private final MutableLiveData<Camera> mSelectedCamera = new MutableLiveData<>();
    private final MutableLiveData<List<Camera>> mCameras = new MutableLiveData<>();

    public MapViewModel() {
    }

    public void setSelectedCamera(Camera camera) {
        mSelectedCamera.setValue(camera);
    }

    public LiveData<Camera> getSelectedCamera() {
        return  mSelectedCamera;
    }

    public LiveData<List<Camera>> getCameras() {
        if (mCameras.getValue() == null) {
            loadCameras();
        }

        return mCameras;
    }

    private void loadCameras() {
        addCamera(new Camera(1,1,0,"Тест 1", "http://5.189.243.163:8082/orbita_V216_214/tracks-v1/index.m3u8", "http://5.189.243.163:8082/orbita_V216_214/preview.mp4"));
        addCamera(new Camera(4,3,90,"Тест 2", "http://krkvideo15.orionnet.online/cam2647/index.m3u8", "http://krkvideo15.orionnet.online/cam2647/preview.jpg"));
        addCamera(new Camera(2,1,180,"Тест 3", "http://krkvideo7.orionnet.online/cam2089/index.m3u8", "http://krkvideo15.orionnet.online/cam2089/preview.jpg"));
        addCamera(new Camera(5,1,270,"Тест 4", "http://krkvideo5.orionnet.online/cam2061/index.m3u8", "http://krkvideo15.orionnet.online/cam2061/preview.jpg"));
    }

    private void addCamera(Camera camera) {
        List<Camera> cameras = ObjectUtils.defaultWhenNull(mCameras.getValue(), new ArrayList<>());

        cameras.add(camera);

        mCameras.setValue(cameras);
    }
}
