package com.onedevapp.nativeinappupdate.plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.google.android.play.core.install.model.InstallStatus;

/**
 * ThirdPartyUpdate is responsible for updating app from any third party URL provided
 * Note: This doesn't validate whether downloading and installing apk is actually belong to same package. Its actually downloads and install any apk URL.
 */
public class ThirdPartyUpdate extends BaseUpdateClass {

    // region Declarations
    private final String FILE_NAME = "app_release.apk";
    private final String MIME_TYPE = "application/vnd.android.package-archive";
    private final String APP_INSTALL_PATH = "\"application/vnd.android.package-archive\"";
    private Uri apk_file_uri;
    //endregion

    //region Constructor

    /**
     * Constructor
     * @param updateManager UpdateManger itself
     */
    public ThirdPartyUpdate(UpdateManager updateManager) {
        super(updateManager);
    }

    //endregion

    // region Public functions

    /**
     * Check update only checks for required permissions
     */
    @Override
    public void checkUpdate() {
        Activity context = mUpdateManager.getActivity();
        boolean isPermissionAvailable = true;
        String errorMsg = "";
        int errorCode = -1;

        if (!Constants.IsNetworkAvailable(context)) {
            isPermissionAvailable = false;
            errorMsg = "checkUpdate() : IsNetworkAvailable : false";
            errorCode = -102;
        } else if (!Constants.CheckPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            isPermissionAvailable = false;
            errorMsg = "checkUpdate() : IsStoragePermissionGranted : false";
            errorCode = -101;
        }

        if (mOnUpdateListener != null) {
            mOnUpdateListener.onUpdateAvailable(isPermissionAvailable, true);
            if (!isPermissionAvailable)
                mUpdateManager.reportUpdateError(errorCode, errorMsg);
        }
    }

    /**
     * Start update will download apk and prompt user for installation
     */
    @SuppressLint("Range")
    @Override
    public void startUpdate() {

        try {

            final Context context = mUpdateManager.getActivity();
            final String appName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();

            Constants.WriteLog("Downloading request on url :" + mUpdateLink);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mUpdateLink));
            request.setVisibleInDownloadsUi(false);
            request.allowScanningByMediaScanner();
            request.setMimeType(MIME_TYPE);
            //Set path for save download file
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, FILE_NAME);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDescription("Downloading..");
            request.setTitle(appName);

            // get download service and enqueue file
            final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);


            if (mOnUpdateListener != null)
                mOnUpdateListener.onUpdateInstallState(InstallStatus.DOWNLOADING);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean downloading = true;
                    while (downloading) {
                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(downloadId);
                        Cursor cursor = manager.query(q);

                        if (cursor.moveToFirst()) {
                            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                            if (status == DownloadManager.STATUS_FAILED) {
                                if (mOnUpdateListener != null)
                                    mUpdateManager.reportUpdateError(InstallStatus.FAILED, "Download failed, Try again later.");
                            } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                apk_file_uri = manager.getUriForDownloadedFile(downloadId);
                                if (mOnUpdateListener != null)
                                    mOnUpdateListener.onUpdateInstallState(InstallStatus.DOWNLOADED);
                            } else if (status == DownloadManager.STATUS_RUNNING) {
                                final int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                    downloading = false;
                                }
                                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                if (bytes_total != 0) {

                                    if (mOnUpdateListener != null)
                                        mOnUpdateListener.onUpdateDownloading(bytes_downloaded, bytes_total);
                                }
                            }
                            cursor.close();
                        }

                    }
                }
            }).start();

        } catch (Exception e) {
            mUpdateManager.reportUpdateError(-1, "startUpdate() : " + e.toString());
        }
    }

    /**
     * Complete update will delete the downloaded apk file
     */
    @Override
    public void completeUpdate() {
        installApk(mUpdateManager.getActivity());
    }

    /**
     * Continue update will try to install apk if already downloaded and user cancel the install previously
     */
    @Override
    public void continueUpdate() {

        Context context = mUpdateManager.getActivity();

        if (apk_file_uri != null && mOnUpdateListener != null) {
            mOnUpdateListener.onUpdateInstallState(InstallStatus.DOWNLOADED);

            installApk(context);
        }
    }

    /**
     * Intent to install apk
     */
    private void installApk(Context context) {

        //if (apk_file_path.exists()) {
        if (apk_file_uri != null) {

            if (mOnUpdateListener != null)
                mOnUpdateListener.onUpdateInstallState(InstallStatus.INSTALLING);

            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setData(apk_file_uri);

            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apk_file_uri, APP_INSTALL_PATH);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } else {
            if (mOnUpdateListener != null)
                mUpdateManager.reportUpdateError(-1, "installApk() : Downloaded file not exist");
        }
    }
    //endregion
}

