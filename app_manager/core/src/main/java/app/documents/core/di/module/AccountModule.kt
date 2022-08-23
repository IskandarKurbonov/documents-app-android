package app.documents.core.di.module

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import app.documents.core.account.AccountDao
import app.documents.core.account.AccountsDataBase
import app.documents.core.account.CloudAccount
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
class AccountModule {

    @Provides
    @Singleton
    fun providesAccountDao(db: AccountsDataBase) = db.accountDao()

    @Provides
    @Singleton
    fun providesAccountDataBase(context: Context): AccountsDataBase {
        val builder = Room.databaseBuilder(context, AccountsDataBase::class.java, AccountsDataBase.TAG)
            .addMigrations(AccountsDataBase.MIGRATION_1_2)
            .addMigrations(AccountsDataBase.MIGRATION_2_3)
            .addMigrations(AccountsDataBase.MIGRATION_3_4)
            .addMigrations(AccountsDataBase.MIGRATION_4_5)
            .addMigrations(AccountsDataBase.MIGRATION_5_6)
            .addMigrations(AccountsDataBase.MIGRATION_6_7)
            .addMigrations(AccountsDataBase.MIGRATION_7_8)
        return builder.build()
    }

    @Provides
    fun provideAccount(accountDao: AccountDao): CloudAccount? = runBlocking {
        return@runBlocking accountDao.getAccountOnline()
    }

}