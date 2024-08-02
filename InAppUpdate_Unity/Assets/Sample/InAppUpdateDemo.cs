using UnityEngine;
using UnityEngine.UI;

namespace OneDevApp.InAppUpdate.Demo
{
    public class InAppUpdateDemo : MonoBehaviour
    {

        public Text infoText;
        public Button checkUpdateBtn;
        public Button startUpdateBtn;
        public Button toggleUpdateBtn;
        public Button toggleTypeBtn;
        public Button toggleLogBtn;

        private UpdateMode updateMode = UpdateMode.PLAY_STORE;
        private UpdateType updateType = UpdateType.FLEXIBLE;
        [SerializeField]
        private string m_thirdPartyLink = "";
        private bool toggleLog = false;

        private void Start()
        {

            checkUpdateBtn.onClick.AddListener(() =>
            {
                NativeInAppUpdateManager.Instance.CheckForUpdate(updateMode, updateType, m_thirdPartyLink);
            });
            startUpdateBtn.onClick.AddListener(() =>
            {
                NativeInAppUpdateManager.Instance.StartUpdate();

                startUpdateBtn.interactable = false;
                checkUpdateBtn.interactable = false;
            });
            toggleUpdateBtn.onClick.AddListener(() =>
            {
                if (updateMode == UpdateMode.PLAY_STORE)
                    updateMode = UpdateMode.THIRD_PARTY;
                else
                    updateMode = UpdateMode.PLAY_STORE;

                toggleUpdateBtn.GetComponentInChildren<Text>().text = "Update Mode : " + (updateMode == UpdateMode.PLAY_STORE ? " PLAY STORE" : " THIRD PARTY");
            });
            toggleTypeBtn.onClick.AddListener(() =>
            {
                if (updateType == UpdateType.FLEXIBLE)
                    updateType = UpdateType.IMMEDIATE;
                else
                    updateType = UpdateType.FLEXIBLE;

                toggleTypeBtn.GetComponentInChildren<Text>().text = "Update Type : " + (updateType == UpdateType.FLEXIBLE ? " Flexiable" : " IMMEDIATE");
            });
            toggleLogBtn.onClick.AddListener(() =>
            {
                toggleLog = !toggleLog;
                NativeInAppUpdateManager.Instance.PluginDebug(toggleLog);
                toggleLogBtn.GetComponentInChildren<Text>().text = "Log : " + (toggleLog ? " enabled" : " diabled");
            });

            toggleUpdateBtn.onClick.Invoke();//Just to set up all values to toggles
            toggleTypeBtn.onClick.Invoke();//Just to set up all values to toggles
            toggleLogBtn.onClick.Invoke();//Just to set up all values to toggles
        }

        private void OnEnable()
        {
            // Subscribe for events from NativeManager
            NativeInAppUpdateManager.OnUpdateAvailable += OnUpdateAvailable;
            NativeInAppUpdateManager.OnUpdateVersionCode += OnUpdateVersionCode;
            NativeInAppUpdateManager.OnUpdateStalenessDays += OnUpdateStalenessDays;
            NativeInAppUpdateManager.OnUpdateInstallState += OnUpdateInstallState;
            NativeInAppUpdateManager.OnUpdateDownloading += OnUpdateDownloading;
            NativeInAppUpdateManager.OnUpdateError += OnUpdateError;
        }


        private void OnDisable()
        {
            // Unsubscribe for events from NativeManager
            NativeInAppUpdateManager.OnUpdateAvailable -= OnUpdateAvailable;
            NativeInAppUpdateManager.OnUpdateVersionCode -= OnUpdateVersionCode;
            NativeInAppUpdateManager.OnUpdateStalenessDays -= OnUpdateStalenessDays;
            NativeInAppUpdateManager.OnUpdateInstallState -= OnUpdateInstallState;
            NativeInAppUpdateManager.OnUpdateDownloading -= OnUpdateDownloading;
            NativeInAppUpdateManager.OnUpdateError -= OnUpdateError;
        }

        private void OnUpdateError(int code, string error)
        {
            if (code == (int)InstallErrorCode.ERROR_LIBRARY)
                Debug.Log("Error : " + error);
            else
                Debug.Log("Error Code : " + (InstallErrorCode)code + " :: " + error);
            startUpdateBtn.interactable = false;
            checkUpdateBtn.interactable = true;

        }

        private void OnUpdateDownloading(long bytesDownloaded, long bytesTotal)
        {
            int downloadProgress = (int)((bytesDownloaded * 100) / bytesTotal);
            infoText.text = "Downloading : " + bytesDownloaded.ToPrettySize(false, 1) + "/" + bytesTotal.ToPrettySize(true, 1) + " ( " + downloadProgress + "% )";
        }

        private void OnUpdateInstallState(InstallStatus state)
        {
            infoText.text = "Install State : " + state.ToString();

            if (state == InstallStatus.DOWNLOADED)
            {
                NativeInAppUpdateManager.Instance.CompleteUpdate();
                startUpdateBtn.interactable = false;
                checkUpdateBtn.interactable = true;
            }
        }

        private void OnUpdateStalenessDays(int days)
        {
            Debug.Log("Staleness Days : " + days);
        }

        private void OnUpdateVersionCode(int versionCode)
        {
            Debug.Log("Version code : " + versionCode);
        }

        private void OnUpdateAvailable(bool isUpdateAvailable)
        {
            infoText.text = "Is update Available : " + isUpdateAvailable;

            startUpdateBtn.interactable = isUpdateAvailable;
        }

    }

}