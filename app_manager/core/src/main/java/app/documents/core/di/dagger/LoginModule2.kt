package app.documents.core.di.dagger

import app.documents.core.account.AccountManager
import app.documents.core.account.AccountPreferences
import app.documents.core.database.datasource.CloudDataSource
import app.documents.core.login.LoginRepository
import app.documents.core.login.LoginRepositoryImpl
import app.documents.core.login.LoginScope
import app.documents.core.network.login.LoginDataSource
import app.documents.core.network.login.LoginModule
import dagger.Module
import dagger.Provides

@Module(includes = [LoginModule::class])
class LoginModule2 {

    @Provides
    @LoginScope
    fun provideLoginRepository(
        loginDataSource: LoginDataSource,
        cloudDataSource: CloudDataSource,
        accountManager: AccountManager,
        accountPreferences: AccountPreferences
    ): LoginRepository {
        return LoginRepositoryImpl(
            loginDataSource,
            cloudDataSource,
            accountManager,
            accountPreferences
        )
    }
}