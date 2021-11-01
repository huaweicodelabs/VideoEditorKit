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

package com.huawei.hms.videoeditor.codelab.fragment;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;

import com.huawei.hms.videoeditor.codelab.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.codelab.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.codelab.ui.mediapick.activity.MediaPickActivity;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;

public class ClipFragment extends BaseFragment {
    private LinearLayout mAddCardView;

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_main_clip;
    }

    @Override
    protected void initView(View view) {
        mAddCardView = view.findViewById(R.id.card_upload);
    }

    @Override
    protected void initObject() {
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
    }

    @Override
    protected void initData() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initEvent() {
        mAddCardView.setOnClickListener(new OnClickRepeatedListener(v -> {
            startActivity(new Intent(this.mActivity, MediaPickActivity.class));
            this.mActivity.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_silent);
        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }
}