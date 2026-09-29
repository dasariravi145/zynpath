package com.zynpath.game.core.di

import com.zynpath.game.core.puzzle.catalog.AndroidAssetLoader
import com.zynpath.game.core.puzzle.catalog.LevelCatalogRepository
import com.zynpath.game.core.puzzle.catalog.LevelCatalogRepositoryImpl
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetLoader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogModule {

    @Binds
    @Singleton
    abstract fun bindPuzzleAssetLoader(
        impl: AndroidAssetLoader
    ): PuzzleAssetLoader

    @Binds
    @Singleton
    abstract fun bindLevelCatalogRepository(
        impl: LevelCatalogRepositoryImpl
    ): LevelCatalogRepository
}
