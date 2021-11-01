
/*
 *  Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.hms.videoeditor.codelab.ui.mediaeditor.menu;

import static com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor.TIMER_PLAY_PERIOD;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.huawei.hms.videoeditor.codelab.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.codelab.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.codelab.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.codelab.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.codelab.ui.common.EditorManager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class VideoClipsPlayFragment extends BaseFragment implements HuaweiVideoEditor.PlayCallback {
    private static final String TAG = "VideoClipsPlayFragment";

    private LinearLayout mSdkPreviewContainer;

    private DefaultPlayControlView mDefaultPlayControlView;

    private ConstraintLayout mLoadingLayout;

    private VideoClipsPlayViewModel mPlayViewModel;

    private EditPreviewViewModel mEditPreviewVieModel;

    private long mCurrentTime = 0;

    private long mVideoDuration = 0;

    private boolean isPlayState = false;

    private boolean isShowLoadingView = false;

    private ToastWrapper mToastState;

    private boolean hasInitCover;

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentTime = savedInstanceState.getLong("mCurrentTime");
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_clips_play_layout;
    }

    @Override
    protected void initView(View view) {
        mSdkPreviewContainer = view.findViewById(R.id.video_content_layout);
        mDefaultPlayControlView = view.findViewById(R.id.top_play_control_view);
        mLoadingLayout = view.findViewById(R.id.loading_layout);
    }

    @Override
    protected void initObject() {
        mPlayViewModel = new ViewModelProvider(mActivity, mFactory).get(VideoClipsPlayViewModel.class);
        mEditPreviewVieModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mDefaultPlayControlView.setVideoPlaying(false);
        mToastState = new ToastWrapper();
    }

    @Override
    protected void initData() {
        mPlayViewModel.getVideoDuration().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                mVideoDuration = time;
                mDefaultPlayControlView.setTotalTime(time);
            }
        });

        mPlayViewModel.getCurrentTime().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                if (time == -1) {
                    mCurrentTime = 0;
                    return;
                }
                mCurrentTime = time;
                mDefaultPlayControlView.updateRunningTime(time);
            }
        });

        mPlayViewModel.getPlayState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mDefaultPlayControlView.setVideoPlaying(aBoolean);
                isPlayState = aBoolean;
            }
        });
    }

    public void initEditor() {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return;
        }
        HVETimeLine timeLine = editor.getTimeLine();
        if (timeLine != null) {
            mDefaultPlayControlView.setVideoSeekBarLength((int) timeLine.getEndTime());
        }
    }

    public void setSeekBarProgress(Long progress) {
        mDefaultPlayControlView.setVideoSeekBar(progress);
    }

    @Override
    protected void initEvent() {
        mDefaultPlayControlView.setSeekListener(new DefaultPlayControlView.OnSeekListener() {
            @Override
            public void onSeek(int progress) {
                mEditPreviewVieModel.setCurrentTimeLine(progress);
            }
        });

        mDefaultPlayControlView.setOnPlayControlListener(new DefaultPlayControlView.OnPlayControlClickListener() {
            @Override
            public void onVoiceStateChange(boolean isMute) {
            }

            @Override
            public void onPlayStateChange(boolean isPlay) {
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null) {
                    return;
                }
                mCurrentTime = mDefaultPlayControlView.getCurrentTime();

                isPlayState = isPlay;
                if (isPlayState) {
                    if (mVideoDuration - mCurrentTime < TIMER_PLAY_PERIOD) {
                        mCurrentTime = 0;
                    }
                    // TODO step 6: Playback timeline, which specifies the start time of the playback.
                } else {
                    editor.pauseTimeLine();
                }
            }

            @Override
            public void onFullScreenClick() {
            }
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    public void setHideLockButton(DefaultPlayControlView.HideLockButton hideLockButton) {
        if (mDefaultPlayControlView != null) {
            mDefaultPlayControlView.setHideLockButton(hideLockButton);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setDisplay();
    }

    private void setDisplay() {
        EditorManager instance = EditorManager.getInstance();
        if (instance == null) {
            return;
        }
        HuaweiVideoEditor editor = instance.getEditor();
        if (editor == null) {
            return;
        }

        editor.setPlayCallback(this);
        editor.setSurfaceCallback(new HuaweiVideoEditor.SurfaceCallback() {
            @Override
            public void surfaceCreated() {

            }

            @Override
            public void surfaceDestroyed() {

            }

            @Override
            public void surfaceChanged(int width, int height) {
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null) {
                    return;
                }
                HVETimeLine timeLine = editor.getTimeLine();
                if (timeLine == null) {
                    return;
                }
                editor.refresh(mCurrentTime);
            }
        });
        editor.setDisplay(mSdkPreviewContainer);
    }

    @Override
    public void onPause() {
        super.onPause();
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor != null) {
            if (isPlayState) {
                editor.pauseTimeLine();
            }
            if (mActivity instanceof VideoClipsActivity) {
                VideoClipsActivity activity = (VideoClipsActivity) mActivity;
                if (activity.isFromSelfMode()) {
                    editor.saveProject();
                }
            }
        }
        if (mToastState != null) {
            mToastState.cancelToast();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("mCurrentTime", mCurrentTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mToastState != null) {
            mToastState.cancelToast();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mToastState != null) {
            mToastState.cancelToast();
        }
    }

    @Override
    public void onPlayProgress(long timeStamp) {
        if (mActivity != null && isAdded()) {
            mActivity.runOnUiThread(() -> mPlayViewModel.setPlayState(true));
        }
        mPlayViewModel.setCurrentTime(timeStamp);
        mDefaultPlayControlView.setVideoSeekBar((int) timeStamp);
    }

    @Override
    public void onPlayStopped() {
        if (mActivity == null) {
            return;
        }
        mActivity.runOnUiThread(() -> {
            if (mPlayViewModel == null) {
                return;
            }
            mPlayViewModel.setPlayState(false);
        });
    }

    @Override
    public void onPlayFinished() {
        mPlayViewModel.setCurrentTime(-1L);
    }

    @Override
    public void onPlayFailed() {
        mActivity.runOnUiThread(() -> mPlayViewModel.setPlayState(false));
    }
}
