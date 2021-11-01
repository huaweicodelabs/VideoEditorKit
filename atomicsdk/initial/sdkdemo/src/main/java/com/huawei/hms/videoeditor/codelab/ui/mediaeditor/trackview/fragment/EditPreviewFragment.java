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

package com.huawei.hms.videoeditor.codelab.ui.mediaeditor.trackview.fragment;

import java.util.Objects;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.codelab.ui.common.utils.ThumbNailMemoryCache;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.codelab.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.codelab.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.codelab.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class EditPreviewFragment extends BaseFragment implements View.OnTouchListener {
    private static final String TAG = "EditPreviewFragment";

    private boolean isPlaying;

    private Point touchDown = new Point();

    private VideoClipsPlayViewModel playViewModel;

    public EditPreviewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
        playViewModel = new ViewModelProvider(mActivity, mFactory).get(VideoClipsPlayViewModel.class);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_edit_preview;
    }

    @Override
    protected void initView(View view) {
        initComponent(view);
        init(view);
    }

    @Override
    protected void initObject() {
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ThumbNailMemoryCache.getInstance().init();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setViewMargin();
    }

    @Override
    protected void initViewModelObserve() {
        playViewModel.getPlayState().observe(this, isPlay -> isPlaying = isPlay);
    }

    @Override
    public void onBackPressed() {
    }

    private void initComponent(View view) {
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(View view) {
    }

    private void setViewMargin() {
    }

    boolean interuptedScrolling = false;

    private void pauseTimeLine() {
        SmartLog.i(TAG, "pauseTimeLine:");
        if (mActivity == null) {
            return;
        }

        ((VideoClipsActivity) mActivity).pauseTimeLine();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mActivity == null) {
            return false;
        }

        if (interuptedScrolling) {
            return true;
        }
        try {
            switch (event.getAction() & event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    touchDown.x = (int) event.getX();
                    touchDown.y = (int) event.getY();
                    if (isPlaying) {
                        pauseTimeLine();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            SmartLog.i("onTouch ", Objects.requireNonNull(e.getMessage()));
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
