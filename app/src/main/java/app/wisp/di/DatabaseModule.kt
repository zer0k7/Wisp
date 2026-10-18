package app.wisp.di

import android.content.Context
import androidx.room.Room
import app.wisp.core.database.WispDatabase
import app.wisp.core.database.dao.BoardDao
import app.wisp.core.database.dao.WishItemDao
import app.wisp.core.database.repository.RoomWishlistRepository
import app.wisp.core.database.repository.WishlistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "wisp.db"

    @Provides
    @Singleton
    fun provideWispDatabase(
        @ApplicationContext context: Context
    ): WispDatabase {
        return Room.databaseBuilder(
            context,
            WispDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBoardDao(database: WispDatabase): BoardDao = database.boardDao()

    @Provides
    @Singleton
    fun provideWishItemDao(database: WispDatabase): WishItemDao = database.wishItemDao()

    @Provides
    @Singleton
    fun provideWishlistRepository(
        boardDao: BoardDao,
        wishItemDao: WishItemDao
    ): WishlistRepository {
        return RoomWishlistRepository(boardDao, wishItemDao)
    }
}
