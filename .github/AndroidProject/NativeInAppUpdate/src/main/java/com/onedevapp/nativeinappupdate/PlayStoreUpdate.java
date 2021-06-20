package com.onedevapp.nativeinappupdate;

import android.content.IntentSender;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

/**
 * PlayStoreUpdate is responsible for updating app playstore.
 */
public class PlayStoreUpdate extends BaseUpdateClass {

    // region Declarations
    private AppUpdateManager mAppUpdateManager;
    private AppUpdateInfo mAppUpdateInfo;

    private InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState installState) {

            // Show module progress, log state, or install the update.
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {

                if (mOnUpdateListener != null)
                    mOnUpdateListener.onUpdateInstallState(installState.installStatus());

            } else if (installState.installStatus() == InstallStatus.DOWNLOADING) {

                long bytesDownloaded = installState.bytesDownloaded();
                long totalBytesToDownload = installState.totalBytesToDownload();

                if (mOnUpdateListener != null)
                    mOnUpdateListener.onUpdateDownloading(bytesDownloaded, totalBytesToDownload);

            } else if (installState.installStatus() == InstallStatus.FAILED) {

                if (mOnUpdateListener != null) {
                    mOnUpdateListener.onUpdateInstallState(installState.installStatus());
                    mUpdateManager.reportUpdateError(installState.installErrorCode(), "");
                }

            } else if (installState.installStatus() == InstallStatus.INSTALLED) {
                if (mOnUpdateListener != null)
                    mOnUpdateListener.onUpdateInstallState(installState.installStatus());

                unRegisterListener();
            } else {

                if (mOnUpdateListener != null)
                    mOnUpdateListener.onUpdateInstallState(installState.installStatus());
            }
        }
    };
    //endregion

    //region Constructor

    /**
     * Constructor
     * @param updateManager UpdateManger itself
     */
    public PlayStoreUpdate(UpdateManager updateManager) {
        super(updateManager);
    }

    //endregion

    // region Public functions

    /**
     * Check update
     */
    @Override
    public void checkUpdate() {

        this.mAppUpdateManager = AppUpdateManagerFactory.create(mUpdateManager.getActivity());
        Task<AppUpdateInfo> appUpdateInfoTask = this.mAppUpdateManager.getAppUpdateInfo();

        registerListener();

        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {

                mAppUpdateInfo = appUpdateInfo;

                if (mOnUpdateListener != null) {
                    mOnUpdateListener.onUpdateAvailable(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE, appUpdateInfo
                            .isUpdateTypeAllowed(mUpdateType));
                }

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    if (mOnUpdateListener != null) {
                        int availableVersionCode = appUpdateInfo.availableVersionCode();
                        mOnUpdateListener.onUpdateVersionCode(availableVersionCode);
                        try {
                            int stalenessDays = appUpdateInfo.clientVersionStalenessDays() == null ? -1 : appUpdateInfo
                                    .clientVersionStalenessDays();
                            mOnUpdateListener.onUpdateStalenessDays(stalenessDays);
                        } catch (Exception e) {
                            mUpdateManager.reportUpdateError(-1, "checkUpdate:" + e.getMessage());
                        }
                    }
                }
            }
        });
    }

    /**
     * Start update
     */
    @Override
    public void startUpdate() throws Exception {
        this.mAppUpdateManager.startUpdateFlowForResult(
                this.mAppUpdateInfo,
                this.mUpdateType,
                // The current activity making the update request.
                mUpdateManager.getActivity(),
                // Include a request code to later monitor this update request.
                mUpdateManager.getRequestCode());
    }

    /**
     * complete update
     */
    @Override
    public void completeUpdate() {
        this.mAppUpdateManager.completeUpdate();
    }

    /**
     * Continue update should be called from onresume to check unfinished updates
     */
    @Override
    public void continueUpdate() {
        mAppUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {

                        //FLEXIBLE:
                        // If the update is downloaded but not installed,
                        // notify the user to complete the update.
                        if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {

                            if (mOnUpdateListener != null)
                                mOnUpdateListener.onUpdateInstallState(appUpdateInfo.installStatus());
                        }

                        //IMMEDIATE:
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                            // If an in-app update is already running, resume the update.
                            try {
                                mAppUpdateManager.startUpdateFlowForResult(
                                        appUpdateInfo,
                                        mUpdateType,
                                        mUpdateManager.getActivity(),
                                        mUpdateManager.getRequestCode());
                            } catch (IntentSender.SendIntentException e) {
                                mUpdateManager.reportUpdateError(-1, "continueUpdate:" + e.getMessage());
                            }
                        }
                    }
                });
    }
    //endregion

    // region private functions

    /**
     * Register listener for install status
     */
    private void registerListener() {
        if (mUpdateType == AppUpdateType.FLEXIBLE)
            mAppUpdateManager.registerListener(this.listener);
    }

    /**
     * unregister listener from install status
     */
    private void unRegisterListener() {
        if (mUpdateType == AppUpdateType.FLEXIBLE)
            mAppUpdateManager.unregisterListener(this.listener);
    }
    //endregion
}
