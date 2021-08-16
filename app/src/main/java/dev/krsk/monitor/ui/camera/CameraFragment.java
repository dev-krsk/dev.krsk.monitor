package dev.krsk.monitor.ui.camera;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import dev.krsk.monitor.R;
import dev.krsk.monitor.util.Helper;
import dev.krsk.monitor.vm.MapViewModel;

public class CameraFragment extends Fragment {
    private Context mContext;
    private MapViewModel mMapViewModel;

    private PlayerView mPlayerView;
    private ProgressBar mProgressBar;
    private SimpleExoPlayer mExoPlayer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);

        mMapViewModel.getSelectedCamera().observe(getViewLifecycleOwner(), camera -> {
            if (camera == null) {
                mExoPlayer.stop();
                return;
            }

            HlsMediaSource hlsMediaSource = new HlsMediaSource
                    .Factory(new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "ExoPlayer")))
                    .setMinLoadableRetryCount(5)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(Uri.parse(camera.getUrlSource()));

            mExoPlayer.prepare(hlsMediaSource, true, true);
            mExoPlayer.setPlayWhenReady(true);
        });

        mPlayerView = view.findViewById(R.id.player_view);
        mProgressBar = view.findViewById(R.id.progress_bar);
        ((AspectRatioFrameLayout) view.findViewById(R.id.player_view_ratio)).setAspectRatio(16f / 9f);

        initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        mExoPlayer.setPlayWhenReady(true);
        mExoPlayer.getPlaybackState();
    }

    @Override
    public void onPause() {
        super.onPause();
        mExoPlayer.setPlayWhenReady(false);
        mExoPlayer.getPlaybackState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExoPlayer.release();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mExoPlayer.release();
    }

    private void initializePlayer() {
        LoadControl loadControl = new DefaultLoadControl();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

        mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()), trackSelector, loadControl);

        mPlayerView.setPlayer(mExoPlayer);
        mPlayerView.setKeepScreenOn(true);
        mPlayerView.setControllerShowTimeoutMs(-1);

        mExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case Player.STATE_READY:
                        mProgressBar.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (Helper.isBehindLiveWindow(error)) {
                    // Re-initialize player at the current live window default position.
                    mExoPlayer.seekToDefaultPosition();
                } else {
                    // TODO: Handle other errors.
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }
}
