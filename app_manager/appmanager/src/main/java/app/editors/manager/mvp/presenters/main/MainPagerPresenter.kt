package app.editors.manager.mvp.presenters.main

import android.net.Uri
import app.documents.core.account.CloudAccount
import app.documents.core.network.ApiContract
import app.documents.core.settings.NetworkSettings
import app.editors.manager.R
import app.editors.manager.app.App
import app.editors.manager.mvp.models.models.OpenDataModel
import app.editors.manager.mvp.views.main.MainPagerView
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lib.toolkit.base.managers.utils.CryptUtils
import lib.toolkit.base.managers.utils.StringUtils
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

sealed class MainPagerState {
    class VisitorState(val account: String, val version: Int) : MainPagerState()
    class PersonalState(val account: String, val version: Int) : MainPagerState()
    class CloudState(val account: String, val version: Int) : MainPagerState()
}

@InjectViewState
class MainPagerPresenter : MvpPresenter<MainPagerView>() {

    @Inject
    lateinit var networkSetting: NetworkSettings

    init {
        App.getApp().appComponent.inject(this)
    }

    fun getState(account: String?, fileData: Uri? = null) {
        account?.let { jsonAccount ->
            Json.decodeFromString<CloudAccount>(jsonAccount).let { cloudAccount ->
                CoroutineScope(Dispatchers.Main).launch {
                    val render = async {
                        render(cloudAccount, jsonAccount)
                    }
                    render.await()
                    checkFileData(cloudAccount, fileData)
                }
            }
        } ?: run {
            throw Exception("Need account")
        }
    }

    private suspend fun render(cloudAccount: CloudAccount, jsonAccount: String) {
        when {
            networkSetting.getPortal().contains(ApiContract.PERSONAL_SUBDOMAIN) -> {
                withContext(Dispatchers.Main) {
                    viewState.onRender(
                        MainPagerState.PersonalState(
                            jsonAccount,
                            StringUtils.convertServerVersion(
                                networkSetting.serverVersion
                            )
                        )
                    )
                }

            }
            cloudAccount.isVisitor -> {
                withContext(Dispatchers.Main) {
                    viewState.onRender(
                        MainPagerState.VisitorState(
                            jsonAccount,
                            StringUtils.convertServerVersion(
                                networkSetting.serverVersion
                            )
                        )
                    )
                }

            }
            else -> {
                withContext(Dispatchers.Main) {
                    viewState.onRender(
                        MainPagerState.CloudState(
                            jsonAccount,
                            StringUtils.convertServerVersion(networkSetting.serverVersion)
                        )
                    )
                }
            }
        }
    }

    private suspend fun checkFileData(account: CloudAccount, fileData: Uri?) {
        fileData?.let { data ->
            if (data.scheme?.equals("oodocuments") == true && data.host.equals("openfile")) {
                val dataModel = Json.decodeFromString<OpenDataModel>(CryptUtils.decodeUri(data.query))
                if (dataModel.portal?.equals(account.portal) == true && dataModel.email?.equals(account.login) == true) {
                    withContext(Dispatchers.Main) {
                        viewState.setFileData(Json.encodeToString(dataModel))
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        viewState.onError(R.string.error_recent_enter_account)
                    }
                }
            }
        }
    }
}