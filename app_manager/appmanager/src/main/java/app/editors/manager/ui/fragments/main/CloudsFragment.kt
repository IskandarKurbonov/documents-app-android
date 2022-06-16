package app.editors.manager.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.documents.core.network.ApiContract
import app.documents.core.webdav.WebDavApi
import app.editors.manager.BuildConfig
import app.editors.manager.R
import app.editors.manager.databinding.FragmentChooseCloudsBinding
import app.editors.manager.mvp.models.account.Storage
import app.editors.manager.storages.dropbox.ui.fragments.DropboxSignInFragment
import app.editors.manager.storages.googledrive.ui.fragments.GoogleDriveSignInFragment
import app.editors.manager.storages.onedrive.managers.utils.OneDriveUtils
import app.editors.manager.storages.onedrive.ui.fragments.OneDriveSignInFragment
import app.editors.manager.ui.activities.login.PortalsActivity
import app.editors.manager.ui.activities.login.WebDavLoginActivity
import app.editors.manager.ui.fragments.base.BaseAppFragment
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth

class CloudsFragment : BaseAppFragment() {

    companion object {
        val TAG: String = CloudsFragment::class.java.simpleName

        private const val KEY_IS_BACK = "KEY_IS_BACK"

        fun newInstance(isBack: Boolean): CloudsFragment {
            return CloudsFragment().apply {
                arguments = Bundle(1).apply {
                    putBoolean(KEY_IS_BACK, isBack)
                }
            }
        }
    }

    private var isBack = false
    private var viewBinding: FragmentChooseCloudsBinding? = null

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentChooseCloudsBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            isBack = it.getBoolean(KEY_IS_BACK)
        }
        init()
    }

    fun init() {
        setActionBarTitle(getString(R.string.fragment_clouds_title))

        viewBinding?.cloudsItemOnlyOffice?.setOnClickListener {
            PortalsActivity.show(requireActivity())
        }

        viewBinding?.cloudsItemNextCloud?.bind(
            R.drawable.ic_storage_nextcloud,
            R.string.storage_select_next_cloud
        ) {
            WebDavLoginActivity.show(requireActivity(), WebDavApi.Providers.NextCloud, null)
        }

        viewBinding?.cloudsItemOwnCloud?.bind(
            R.drawable.ic_storage_owncloud,
            R.string.storage_select_own_cloud
        ) {
            WebDavLoginActivity.show(requireActivity(), WebDavApi.Providers.OwnCloud, null)
        }

        viewBinding?.cloudsItemKDrive?.bind(
            R.drawable.ic_storage_kdrive,
            R.string.storage_select_kdrive
        ) {
            WebDavLoginActivity.show(requireActivity(), WebDavApi.Providers.KDrive, null)
        }

        viewBinding?.cloudsItemOneDrive?.bind(
            R.drawable.ic_storage_onedrive,
            R.string.storage_select_one_drive
        ) {
            val storage = Storage(
                OneDriveUtils.ONEDRIVE_STORAGE,
                BuildConfig.ONE_DRIVE_COM_CLIENT_ID,
                BuildConfig.ONE_DRIVE_COM_REDIRECT_URL
            )
            showFragment(OneDriveSignInFragment.newInstance(storage), OneDriveSignInFragment.TAG, false)
        }

        viewBinding?.cloudsItemDropbox?.bind(
            R.drawable.ic_storage_dropbox,
            R.string.storage_select_drop_box
        ) {
            val clientIdentifier = "OnlyOffice/${BuildConfig.VERSION_NAME}"
            val requestConfig = DbxRequestConfig(clientIdentifier)
            val scopes = listOf("account_info.read", "files.content.write")
            val appKey = BuildConfig.DROP_BOX_COM_CLIENT_ID
            Auth.startOAuth2PKCE(requireContext(), appKey, requestConfig, scopes)
        }

        viewBinding?.cloudsItemGoogleDrive?.bind(
            R.drawable.ic_storage_google,
            R.string.storage_select_google_drive
        ) {
            val storage = Storage(
                ApiContract.Storage.GOOGLEDRIVE,
                BuildConfig.GOOGLE_COM_CLIENT_ID,
                BuildConfig.GOOGLE_COM_REDIRECT_URL
            )
            showFragment(GoogleDriveSignInFragment.newInstance(storage), GoogleDriveSignInFragment.TAG, false)
        }


        viewBinding?.cloudsItemYandex?.isVisible = false
//        viewBinding?.cloudsItemYandex?.bind(
//            R.drawable.ic_storage_yandex,
//            R.string.storage_select_yandex
//        ) {
//            WebDavLoginActivity.show(requireActivity(), WebDavApi.Providers.Yandex, null)
//        }

        viewBinding?.cloudsItemWebDav?.bind(
            R.drawable.ic_storage_webdav,
            R.string.storage_select_web_dav
        ) {
            WebDavLoginActivity.show(requireActivity(), WebDavApi.Providers.WebDav, null)
        }
    }


}