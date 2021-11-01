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

package com.huawei.hms.videoeditor.codelab.ui.mediaeditor.trackview.viewmodel;

import static android.content.Context.ALARM_SERVICE;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.Application;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.codelab.ui.mediaeditor.trackview.fragment.EditPreviewFragment;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEAudioLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEStickerLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.codelab.ui.common.EditorManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class EditPreviewViewModel extends AndroidViewModel {
    private static final String TAG = "EditPreviewViewModel";

    private MutableLiveData<Long> videoDuration = new MutableLiveData<>();

    private MutableLiveData<List<HVEAsset>> imageItemList = new MutableLiveData<>();

    private MutableLiveData<String> selectedUUID = new MutableLiveData<>();

    private MutableLiveData<Long> currentTime = new MutableLiveData<>();


    private MutableLiveData<Integer> reverseCallback = new MutableLiveData<>();


    private MutableLiveData<HVEAsset> mainLaneAsset = new MutableLiveData<>();

    private HVEAsset currentMainLaneAsset;


    private AlarmManager alarmManager = null;


    private MutableLiveData<String> faceDetectError = new MutableLiveData<>();

    private int reverseProgress;

    public MutableLiveData<Integer> getReverseCallback() {
        return reverseCallback;
    }

    public int getReverseProgress() {
        return reverseProgress;
    }

    public MutableLiveData<String> getFaceDetectError() {
        return faceDetectError;
    }

    public EditPreviewViewModel(@NonNull Application application) {
        super(application);
    }

    public void setVideoDuration(Long videoDuration) {
        this.videoDuration.postValue(videoDuration);
    }

    public MutableLiveData<Long> getVideoDuration() {
        return videoDuration;
    }

    public MutableLiveData<Long> getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        mainLaneAssetChange(currentTime);
        this.currentTime.postValue(currentTime);
    }

    public void mainLaneAssetChange(long currentTime) {
        HVEAsset asset = getMainLaneAsset(currentTime);
        if (asset == null) {
            return;
        }

        SmartLog.d(TAG, "mainLaneAssetChange:" + (currentMainLaneAsset == asset));
        if (currentMainLaneAsset == asset) {
            return;
        }
        SmartLog.d(TAG, "mainLaneAssetChange:postValue");
        mainLaneAsset.postValue(asset);
        currentMainLaneAsset = asset;
    }

    public HVETimeLine getTimeLine() {
        return EditorManager.getInstance().getTimeLine();
    }

    public void refreshAssetList() {
        if (EditorManager.getInstance().getMainLane() != null) {
            imageItemList.postValue(getItems());
        }
    }

    public List<HVEAsset> getItems() {
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        if (videoLane == null) {
            return new ArrayList<>();
        }
        return videoLane.getAssets();
    }

    public HVEAsset getMainLaneAsset(long currentTime) {
        long time = currentTime;
        HVEVideoLane lane = EditorManager.getInstance().getMainLane();
        if (lane != null && time == lane.getEndTime()) {
            return lane.getAssetByIndex(lane.getAssets().size() - 1);
        }

        for (int i = 0; i < getItems().size(); i++) {
            HVEAsset asset = getItems().get(i);
            if (time >= asset.getStartTime() && time < asset.getEndTime()) {
                return asset;
            }
        }
        return null;
    }

    public HVEAsset getMainLaneAsset() {
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null) {
            return null;
        }

        long time = timeLine.getCurrentTime();

        return getMainLaneAsset(time);
    }

    public void updateDuration() {
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null) {
            return;
        }
        setVideoDuration(timeLine.getDuration());
    }

    private EditPreviewFragment mEditPreviewFragment;

    public void setFragment(EditPreviewFragment mEditPreviewFragment) {
        this.mEditPreviewFragment = mEditPreviewFragment;
    }

    public EditPreviewFragment getFragment() {
        return mEditPreviewFragment;
    }

    public void setCurrentTimeLine(long time) {
        HuaweiVideoEditor huaweiVideoEditor = EditorManager.getInstance().getEditor();
        if (huaweiVideoEditor == null) {
            return;
        }
        SmartLog.d(TAG, "seek to " + time);
        setCurrentTime(time);
    }

    public HuaweiVideoEditor getEditor() {
        return EditorManager.getInstance().getEditor();
    }

    public HVEAsset getSelectedAsset() {
        if (selectedUUID == null || TextUtils.isEmpty(selectedUUID.getValue())) {
            return null;
        }
        return getSelectedAsset(selectedUUID.getValue());
    }

    public HVEAsset getSelectedAsset(String uuid) {
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null) {
            return null;
        }

        for (HVEVideoLane lane : timeLine.getAllVideoLane()) {
            for (HVEAsset asset : lane.getAssets()) {
                if (asset.getUuid().equals(uuid)) {
                    return asset;
                }
            }
        }
        for (HVEStickerLane lane : timeLine.getAllStickerLane()) {
            for (HVEAsset asset : lane.getAssets()) {
                if (asset.getUuid().equals(uuid)) {
                    return asset;
                }
            }
        }
        for (HVEAudioLane lane : timeLine.getAllAudioLane()) {
            for (HVEAsset asset : lane.getAssets()) {
                if (asset.getUuid().equals(uuid)) {
                    return asset;
                }
            }
        }
        return null;
    }

    public boolean isAlarmClock(long time) {
        boolean isClock = false;
        if (alarmManager == null) {
            alarmManager = (AlarmManager) getApplication().getSystemService(ALARM_SERVICE);
        }
        HuaweiVideoEditor editor = getEditor();
        AlarmManager.AlarmClockInfo nextAlarmClock = alarmManager.getNextAlarmClock();
        long triggerTime = 0;
        if (nextAlarmClock != null) {
            triggerTime = nextAlarmClock.getTriggerTime();
            if (triggerTime - time >= -500 && triggerTime - time <= 500) {
                isClock = true;
                if (editor != null) {
                    editor.pauseTimeLine();
                }
            }
        }
        return isClock;
    }

    public void cancelVideoRevert() {
        HVETimeLine hveTimeLine = getTimeLine();
        HVEAsset hveVideoAsset = getSelectedAsset();
        if (hveVideoAsset == null) {
            hveVideoAsset = getMainLaneAsset();
        }
        if (!(hveVideoAsset instanceof HVEVideoAsset)) {
            return;
        }

        if (getTimeLine() != null) {
            getTimeLine().getVideoLane(hveVideoAsset.getLaneIndex()).interruptReverseVideo(null);
        }
    }
}
