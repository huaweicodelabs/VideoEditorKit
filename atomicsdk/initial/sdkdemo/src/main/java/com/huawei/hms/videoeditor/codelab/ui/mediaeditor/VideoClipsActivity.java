
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

package com.huawei.hms.videoeditor.codelab.ui.mediaeditor;

import static com.huawei.hms.videoeditor.codelab.ui.common.bean.Constant.IntentFrom.INTENT_FROM_IMAGE_LIB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.codelab.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.codelab.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.codelab.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.codelab.ui.common.utils.SharedPreferencesUtils;
import com.huawei.hms.videoeditor.codelab.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.codelab.ui.common.utils.SoftKeyBoardUtils;
import com.huawei.hms.videoeditor.codelab.ui.common.utils.ThumbNailMemoryCache;
import com.huawei.hms.videoeditor.codelab.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.codelab.ui.common.utils.VolumeChangeObserver;
import com.huawei.hms.videoeditor.codelab.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.codelab.ui.common.view.dialog.ProgressDialog;
import com.huawei.hms.videoeditor.codelab.ui.mediaeditor.trackview.fragment.EditPreviewFragment;
import com.huawei.hms.videoeditor.codelab.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditor.codelab.ui.mediaexport.VideoExportActivity;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.LicenseException;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.codelab.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.codelab.ui.common.EditorManager;
import com.huawei.hms.videoeditor.codelab.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.codelab.ui.mediaeditor.menu.DefaultPlayControlView;
import com.huawei.hms.videoeditor.codelab.ui.mediaeditor.menu.VideoClipsPlayFragment;
import com.huawei.hms.videoeditor.codelab.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class VideoClipsActivity extends BaseActivity implements DefaultPlayControlView.HideLockButton {
    private static final String TAG = "VideoClipsActivity";

    private static final int TOAST_TIME = 700;

    public static final int VIEW_NORMAL = 1;

    public static final int VIEW_HISTORY = 3;

    public static final String CLIPS_VIEW_TYPE = "clipsViewType";

    public static final String PROJECT_ID = "projectId";

    public static final String CURRENT_TIME = "mCurrentTime";

    public static final String SOURCE = "source";

    public static final int ACTION_ADD_MEDIA_REQUEST_CODE = 1001;

    public static final int ACTION_ADD_AUDIO_REQUEST_CODE = 1002;

    public static final int ACTION_ADD_PICTURE_IN_REQUEST_CODE = 1003;

    public static final int ACTION_SPEECH_SYNTHESIS_REQUEST_CODE = 1004;

    public static final int ACTION_ADD_COVER_REQUEST_CODE = 1005;

    public static final int ACTION_ADD_CANVAS_REQUEST_CODE = 1006;

    public static final int ACTION_REPLACE_VIDEO_ASSET = 1007;

    public static final int ACTION_ADD_STICKER_REQUEST_CODE = 1009;

    public static final int ACTION_EXPORT_REQUEST_CODE = 1010;

    public static final int ACTION_PIP_VIDEO_ASSET = 1013;

    public static final int ACTION_ADD_BLOCKING_STICKER_REQUEST_CODE = 1015;

    public static final String MAIN_ACTIVITY_NAME = "com.huawei.hms.ml.mediacreative.MainActivity";

    public static final String EXTRA_FROM_SELF_MODE = "extra_from_self_mode";

    public static final String EDITOR_UUID = "editor_uuid";

    private static final long SEEK_INTERVAL = 10;

    private static final int MAX_TEXT = 50;

    private static final int VIEW_TYPE = 3;

    private EditorTextView mTvExport;

    private ImageView mIvExport;

    private ImageView mIvBack;

    private VideoClipsPlayViewModel mSdkPlayViewModel;

    private EditPreviewViewModel mEditPreviewViewModel;

    private ArrayList<MediaData> mMediaDataList;

    private Context mContext;

    private Handler seekHandler;

    private volatile long mCurrentTime = 0;

    public volatile boolean isVideoPlaying = false;

    private String mProjectId = "";

    private TranslateAnimation mHiddenAnim;

    private TranslateAnimation mShowAnim;

    SoftKeyBoardUtils mSoftKeyBoardUtils;

    private boolean isFromSelf = true;

    private VideoClipsPlayFragment mVideoClipsPlayFragment;

    private ProgressDialog progressDialog;

    private boolean isSoftKeyboardShow = false;

    private EditPreviewFragment mEditPreviewFragment;

    private int mViewType;

    private boolean isAbnormalExit;

    private HuaweiVideoEditor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        statusBarColor = R.color.home_color_FF181818;
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_clips);
        createTailSource();
        isAbnormalExit = false;
        if (savedInstanceState != null) {
            isAbnormalExit = true;
            mCurrentTime = savedInstanceState.getLong(CURRENT_TIME);
            mViewType = savedInstanceState.getInt(CLIPS_VIEW_TYPE);
            isFromSelf = savedInstanceState.getBoolean(EXTRA_FROM_SELF_MODE);
            mProjectId = savedInstanceState.getString(PROJECT_ID);
        }
        initView();
        initNavBarAnim();
        initObject();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mEditor != null) {
            EditorManager.getInstance().setEditor(mEditor);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mTvExport = findViewById(R.id.tv_save);
        mIvExport = findViewById(R.id.iv_save);

        mVideoClipsPlayFragment =
            (VideoClipsPlayFragment) getSupportFragmentManager().findFragmentById(R.id.id_edit_play_fragment);

        mEditPreviewFragment =
            (EditPreviewFragment) getSupportFragmentManager().findFragmentById(R.id.id_edit_preview_fragment);

        if (mVideoClipsPlayFragment != null) {
            mVideoClipsPlayFragment.setHideLockButton(this);
        }
    }

    private void initObject() {
        mContext = this;
        mSdkPlayViewModel = new ViewModelProvider(this, factory).get(VideoClipsPlayViewModel.class);
        mEditPreviewViewModel = new ViewModelProvider(this, factory).get(EditPreviewViewModel.class);

        seekHandler = new Handler();
        mMediaDataList = new ArrayList<>();
        mSoftKeyBoardUtils = new SoftKeyBoardUtils(this);

        SafeIntent safeIntent = new SafeIntent(getIntent());
        if (!isAbnormalExit) {
            mViewType = safeIntent.getIntExtra(CLIPS_VIEW_TYPE, 1);
            isFromSelf = safeIntent.getBooleanExtra(EXTRA_FROM_SELF_MODE, false);
            mProjectId = safeIntent.getStringExtra(PROJECT_ID);
        }
        ArrayList<MediaData> list = safeIntent.getParcelableArrayListExtra(Constant.EXTRA_SELECT_RESULT);

        Constant.IntentFrom.INTENT_WHERE_FROM =
            ("highlight".equals(safeIntent.getStringExtra(SOURCE)) ? INTENT_FROM_IMAGE_LIB : 0);

        // TODO step 3:Creating an Editor Object

        // TODO step 4:Initializing the Running Environment


        if (mEditor == null) {
            return;
        }
        EditorManager.getInstance().setEditor(mEditor);
        mEditPreviewViewModel.setFragment(mEditPreviewFragment);
        switch (mViewType) {
            case VIEW_NORMAL:
                mMediaDataList = new ArrayList<>();
                if (list != null) {
                    mMediaDataList.addAll(list);
                }
                if (Constant.IntentFrom.INTENT_WHERE_FROM != Constant.IntentFrom.INTENT_FROM_IMAGE_LIB) {
                    if (EditorManager.getInstance().getMainLane() == null) {
                        if (EditorManager.getInstance().getTimeLine() == null) {
                            return;
                        }
                        EditorManager.getInstance().getTimeLine().appendVideoLane();
                    }

                    for (MediaData data : mMediaDataList) {
                        if (data != null) {
                            if (data.getType() == MediaData.MEDIA_VIDEO) {
                                HVEVideoAsset hveVideoAsset = EditorManager.getInstance()
                                    .getMainLane()
                                    .appendVideoAsset(data.getPath(), data.getDuration(), data.getWidth(),
                                        data.getHeight());

                            } else {
                                HVEImageAsset imageAsset =
                                    EditorManager.getInstance().getMainLane().appendImageAsset(data.getPath());

                            }
                        }
                    }
                }
                break;
            case VIEW_HISTORY:
                break;
            default:
                break;

        }
        mVideoClipsPlayFragment.initEditor();

        mEditPreviewViewModel.updateDuration();
        mEditPreviewViewModel.refreshAssetList();
        VolumeChangeObserver instance = VolumeChangeObserver.getInstace(getApplicationContext());
        instance.registerVolumeReceiver();
        SmartLog.d(TAG, "VideoClipsActivity projectid:" + EditorManager.getInstance().getEditor().getProjectId());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mIvBack.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTextStyle();

                onBackPressed();
            }
        }, 100));

        mTvExport.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSdk = new Intent(mContext, VideoExportActivity.class);

                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor != null) {
                    intentSdk.putExtra(VideoExportActivity.EDITOR_UUID, editor.getUuid());
                } else {
                    SmartLog.e(TAG, "Export Clicked but editor is null");
                    return;
                }

                HVETimeLine timeLine = editor.getTimeLine();
                if (timeLine == null) {
                    return;
                }

                SafeIntent safeIntent = new SafeIntent(getIntent());
                intentSdk.putExtra(SOURCE, safeIntent.getStringExtra(SOURCE));

                if (timeLine.getCoverImage() != null) {
                    intentSdk.putExtra(VideoExportActivity.COVER_URL, timeLine.getCoverImage().getPath());
                }

                startActivityForResult(intentSdk, ACTION_EXPORT_REQUEST_CODE);
            }
        }));

        mEditPreviewViewModel.getVideoDuration().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                mSdkPlayViewModel.setVideoDuration(aLong);
            }
        });

        mSdkPlayViewModel.setCurrentTime(0L);
        mSdkPlayViewModel.getCurrentTime().observe(this, time -> {
            if (time == -1) {
                mCurrentTime = 0;
                return;
            }
            mCurrentTime = time;
            if (mEditPreviewViewModel == null) {
                return;
            }
            mEditPreviewViewModel.setCurrentTime(mCurrentTime);
            mEditPreviewViewModel.isAlarmClock(System.currentTimeMillis());
        });

        mEditPreviewViewModel.getCurrentTime().observe(this, time -> {
            if (mCurrentTime == time || mSdkPlayViewModel == null) {
                return;
            }
            mSdkPlayViewModel.setCurrentTime(time);
            mVideoClipsPlayFragment.setSeekBarProgress(time);
        });

        mSdkPlayViewModel.getPlayState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isPlaying) {
                if (isPlaying && seekHandler != null) {
                    seekHandler.removeCallbacksAndMessages(null);
                }
                isVideoPlaying = isPlaying;
            }
        });

        mEditPreviewViewModel.getFaceDetectError().observe(this, errorCode -> {
            if (TextUtils.equals(errorCode, "0")) {
                ToastWrapper.makeText(mContext, R.string.result_illegal, Toast.LENGTH_SHORT).show();
            } else {
                ToastWrapper.makeText(mContext, R.string.identify_failed, Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog = new ProgressDialog(this, getString(R.string.video_run_backward));
        progressDialog.setOnProgressClick(() -> {
            mEditPreviewViewModel.cancelVideoRevert();
        });

        mEditPreviewViewModel.getReverseCallback().observe(this, integer -> {
            if (integer == 1) {
                if (progressDialog != null && !progressDialog.isShowing()) {
                    progressDialog.show(getWindowManager());
                    progressDialog.setStopVisble(true);
                    progressDialog.setCancelable(true);
                    progressDialog.setProgress(0);
                }
            } else if (integer == 2) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.setProgress(mEditPreviewViewModel.getReverseProgress());
                }
            } else if (integer == 3) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void initNavBarAnim() {
        mShowAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAnim.setDuration(500);
        mHiddenAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mHiddenAnim.setDuration(500);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong(CURRENT_TIME, mCurrentTime);
        outState.putInt(CLIPS_VIEW_TYPE, VIEW_TYPE);
        outState.putBoolean(EXTRA_FROM_SELF_MODE, true);
        if (mEditor != null) {
            outState.putString(PROJECT_ID, mEditor.getProjectId());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backAction();
            }
        }, 50);

    }

    private void backAction() {
        finish();
    }

    private void stopEditor() {
        if (EditorManager.getInstance().getEditor() != null) {
            EditorManager.getInstance().recyclerEditor();
        }
    }

    private void saveToast() {
        int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
        Toast toast = Toast.makeText(this, getString(R.string.save_toast), Toast.LENGTH_SHORT);
        toast.getView().setBackgroundColor(Color.TRANSPARENT);
        TextView textView = toast.getView().findViewById(tvToastId);
        textView.setBackground(getDrawable(R.drawable.bg_toast_show));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.clip_color_E6FFFFFF));
        textView.setPadding(SizeUtils.dp2Px(this, 16), SizeUtils.dp2Px(this, 8), SizeUtils.dp2Px(this, 16),
            SizeUtils.dp2Px(this, 8));
        toast.setGravity(Gravity.CENTER, 0, -SizeUtils.dp2Px(this, 30));
        toast.show();
    }

    private String getSourceName(String path) {
        String sourceName = "";
        for (MediaData item : mMediaDataList) {
            if (item.getPath().equals(path)) {
                sourceName = item.getName();
                break;
            }
        }
        return sourceName;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            pauseTimeLine();
        }
        return super.onTouchEvent(event);
    }

    public void pauseTimeLine() {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor != null) {
            editor.pauseTimeLine();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (TimeOutOnTouchListener listener : onTouchListeners) {
            if (listener != null) {
                listener.onTouch(ev);
            }
        }
        try {
            if (getWindow().superDispatchTouchEvent(ev)) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            return true;
        }

        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolumeChangeObserver instance = VolumeChangeObserver.getInstace(getApplicationContext());
        instance.unregisterVolumeReceiver();

        if (EditorManager.getInstance().getEditor() != null) {
            EditorManager.getInstance().recyclerEditor();
        }

        ThumbNailMemoryCache.getInstance().recycler();
    }

    private void createTailSource() {
        String dirPath = getFilesDir().toString() + "/tail";
        File dir = new File(dirPath);
        if (!dir.mkdirs()) {
            SmartLog.e(TAG, "fail to make dir ");
        }
        String backPath = dirPath + "/background.png";
        FileOutputStream fOut = null;
        try {
            File tailFile = new File(backPath);
            if (!tailFile.exists()) {
                if (!tailFile.createNewFile()) {
                    SmartLog.e(TAG, "fail to create tail file");
                }
                Bitmap bitmap = Bitmap.createBitmap(1080, 1080, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.BLACK);
                fOut = new FileOutputStream(tailFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
            }
        } catch (IOException e) {
            SmartLog.e(TAG, e.getMessage());
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                SmartLog.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void isShowLockButton(boolean isShow) {
    }

    public boolean isFromSelfMode() {
        return isFromSelf;
    }

    public void initSetCoverData(String projectId, Bitmap bitmap, long time) {
        setBitmapCover(projectId, bitmap, time);
    }

    private void setBitmapCover(String projectId, Bitmap bitmap, long time) {
        if (TextUtils.isEmpty(projectId)) {
            SmartLog.e(TAG, "projectId is empty");
            return;
        }
        new Thread("CoverImageViewModel-Thread-1") {
            @Override
            public void run() {
                super.run();
                try {
                    String path = FileUtil.saveBitmap(getApplication(), projectId, bitmap,
                        System.currentTimeMillis() + "cover.png");
                    HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
                    if (timeLine != null) {
                        timeLine.addCoverImage(path);
                    }
                } catch (Exception e) {
                    SmartLog.e(TAG, e.getMessage());
                }
            }
        }.start();
    }

    private void clearTextStyle() {
        SharedPreferencesUtils.getInstance().putIntValue(mContext, SharedPreferencesUtils.TEXT_COLOR_INDEX, -1);
        SharedPreferencesUtils.getInstance().putIntValue(mContext, SharedPreferencesUtils.TEXT_STROKE_INDEX, -1);
        SharedPreferencesUtils.getInstance().putIntValue(mContext, SharedPreferencesUtils.TEXT_SHAWDOW_INDEX, -1);
        SharedPreferencesUtils.getInstance().putIntValue(mContext, SharedPreferencesUtils.TEXT_BACK_INDEX, -1);
    }

    private final ArrayList<TimeOutOnTouchListener> onTouchListeners = new ArrayList<TimeOutOnTouchListener>(10);

    public void registerMyOnTouchListener(TimeOutOnTouchListener onTouchListener) {
        onTouchListeners.add(onTouchListener);
    }

    public void unregisterMyOnTouchListener(TimeOutOnTouchListener onTouchListener) {
        onTouchListeners.remove(onTouchListener);
    }

    public boolean isSoftKeyboardShow() {
        return isSoftKeyboardShow;
    }

    public void setSoftKeyboardShow(boolean softKeyboardShow) {
        isSoftKeyboardShow = softKeyboardShow;
    }

    public interface TimeOutOnTouchListener {
        boolean onTouch(MotionEvent ev);
    }
}
