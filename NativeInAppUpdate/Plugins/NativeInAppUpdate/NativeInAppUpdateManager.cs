using System;
using UnityEngine;

namespace OneDevApp.InAppUpdate
{
    public class NativeInAppUpdateManager : MonoBehaviour
    {
        #region Events

#pragma warning disable 0067
        /// <summary>
        /// Event triggered when the update is available or not
        /// </summary>
        public static event Action<bool> OnUpdateAvailable;
        /// <summary>
        /// Event triggered with version code available
        /// </summary>
        public static event Action<int> OnUpdateVersionCode;
        /// <summary>
        /// Event triggered with staleness days available to download
        /// </summary>
        public static event Action<int> OnUpdateStalenessDays;
        /// <summary>
        /// Event triggered with install state during update
        /// </summary>
        public static event Action<InstallStatus> OnUpdateInstallState;
        /// <summary>
        /// Event triggered with downloading value
        /// </summary>
        public static event Action<long, long> OnUpdateDownloading;
        /// <summary>
        /// Event triggered with error during update
        /// </summary>
        public static event Action<int, string> OnUpdateError;

#pragma warning restore 0067
        #endregion


        public static NativeInAppUpdateManager Instance { get; private set; }

#pragma warning disable 0414
        /// <summary>
        /// UnityMainActivity current activity name or main activity name
        /// Modify only if this UnityPlayer.java class is extends or used any other default class
        /// </summary>
        [Tooltip("Android Launcher Activity")]
        [SerializeField]
        private string m_unityMainActivity = "com.unity3d.player.UnityPlayer";
        
        private bool writeLog = false;

#pragma warning restore 0414

#if UNITY_ANDROID && !UNITY_EDITOR
        private AndroidJavaObject mContext = null;
        private AndroidJavaObject mUpdateManager = null;
        

        class OnUpdateListener : AndroidJavaProxy
        {
            public OnUpdateListener() : base("com.onedevapp.nativeplugin.inappupdate.OnUpdateListener") { }

            // Invoked when Google Play Services returns a response 
            public void onUpdateAvailable(bool isUpdateAvailable, bool isUpdateTypeAllowed)
            {
                if (isUpdateAvailable && isUpdateTypeAllowed)
                {
                    UnityMainThreadDispatcher.Instance().Enqueue(() => {

                        if (OnUpdateAvailable != null)
                        {
                            OnUpdateAvailable.Invoke(true);
                        }
                    });
                }
                else
                {
                    UnityMainThreadDispatcher.Instance().Enqueue(() => {

                        if (OnUpdateAvailable != null)
                        {
                            OnUpdateAvailable.Invoke(false);
                        }
                    });
                }
            }

            // Invoked when the update is available with version code
            public void onUpdateVersionCode(int versionCode)
            {
                if (OnUpdateVersionCode != null)
                {

                    UnityMainThreadDispatcher.Instance().Enqueue(() => {
                        OnUpdateVersionCode.Invoke(versionCode);
                    });
                }
            }

            // Invoked when the update is available with staleness days
            public void onUpdateStalenessDays(int days)
            {
                if (OnUpdateStalenessDays != null)
                {
                    UnityMainThreadDispatcher.Instance().Enqueue(() => {
                        OnUpdateStalenessDays.Invoke(days);
                    });
                }
            }

            // Invoked when install status of the update
            public void onUpdateInstallState(int state)
            {
                if (OnUpdateInstallState != null)
                {
                    UnityMainThreadDispatcher.Instance().Enqueue(() => {
                        OnUpdateInstallState.Invoke((InstallStatus)state);
                    });
                }
            }

            // Invoked during downloading
            public void onUpdateDownloading(long bytesDownloaded, long bytesTotal)
            {
                if (OnUpdateDownloading != null)
                {
                    UnityMainThreadDispatcher.Instance().Enqueue(() => {
                        OnUpdateDownloading.Invoke(bytesDownloaded, bytesTotal);
                    });
                }
            }

            // Invoked when the update encounter error
            public void onUpdateError(int code, string error)
            {
                if (OnUpdateError != null)
                {
                    UnityMainThreadDispatcher.Instance().Enqueue(() => {
                        OnUpdateError.Invoke(code, error);
                    });
                }
            }
        }
        
#endif


        private void Awake()
        {
            if (Instance == null)
            {
                Instance = this;
            }
            else
            {
                DestroyImmediate(Instance.gameObject);
                Instance = this;
            }

#if UNITY_ANDROID && !UNITY_EDITOR
            if (Application.platform == RuntimePlatform.Android)
            {
                mContext = new AndroidJavaClass(m_unityMainActivity).GetStatic<AndroidJavaObject>("currentActivity");
            }
#elif UNITY_EDITOR
            Debug.Log("Platform not supported");
#endif
        }


        #region App Update
        /// <summary>
        /// Check for update and returns OnUpdateListener.onUpdateAvailable true or false
        /// </summary>
        /// <param name="updateMode">update mode</param>
        /// <param name="updatetype">update type</param>
        /// <param name="apkLink">new apk link</param>
        public void CheckForUpdate(UpdateMode updateMode = UpdateMode.PLAY_STORE, UpdateType updateType = UpdateType.FLEXIBLE, string thirdPartyLink = "")
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            // Initialize Update Manager

            var manager = new AndroidJavaClass("com.onedevapp.nativeplugin.inappupdate.UpdateManager");

            var mUpdateManager = manager.CallStatic<AndroidJavaObject>("Builder", mContext);

            mUpdateManager.Call<AndroidJavaObject>("updateMode", (int)updateMode)
                .Call<AndroidJavaObject>("handler", new OnUpdateListener())
                .Call<AndroidJavaObject>("updateType", (int)updateType);

            if (!string.IsNullOrEmpty(thirdPartyLink))
                mUpdateManager.Call<AndroidJavaObject>("updateLink", thirdPartyLink);

            mUpdateManager.Call("checkUpdate");
#elif UNITY_EDITOR
            if (writeLog)
                Debug.Log("Platform not supported");
#endif
        }

        /// <summary>
        /// To start the instalation of the update, you should call this function after OnUpdateListener.onUpdate(isUpdateAvailable, isUpdateTypeAllowed)
        /// and only if both isUpdateAvailable and isUpdateTypeAllowed are true 
        /// </summary>
        public void StartUpdate()
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            if (mUpdateManager != null)
                mUpdateManager.Call("startUpdate");
#elif UNITY_EDITOR
            if (writeLog)
                Debug.Log("Platform not supported");
#endif
        }

        /// <summary>
        /// To complete the instalation of the update, you should call this function after OnUpdateListener.onUpdateInstallState().
        /// This has no impact while using third party link update
        /// </summary>
        public void CompleteUpdate()
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            if (mUpdateManager != null)
                mUpdateManager.Call("completeUpdate");
#elif UNITY_EDITOR
            if (writeLog)
                Debug.Log("Platform not supported");
#endif
        }

        /// <summary>
        /// To continue update and must can called only on app onresume to complete the pending update of previous update
        /// </summary>
        public void ContinueUpdate()
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            if (mUpdateManager != null)
                mUpdateManager.Call("continueUpdate");
#elif UNITY_EDITOR
            if (writeLog)
                Debug.Log("Platform not supported");
#endif
        }

        #endregion


        #region Debug
        /// <summary>
        /// By default puglin console log will be diabled, but can be enabled
        /// </summary>
        /// <param name="showLog">If set true then log will be displayed else disabled</param>
        public void PluginDebug(bool showLog = true)
        {
#if UNITY_ANDROID && !UNITY_EDITOR

            AndroidJNIHelper.debug = showLog;
            var constantClass = new AndroidJavaClass("com.onedevapp.nativeinappupdate");
            constantClass.SetStatic("enableLog", showLog);

#elif UNITY_EDITOR
            writeLog = showLog;
#endif
        }
        #endregion

    }

}