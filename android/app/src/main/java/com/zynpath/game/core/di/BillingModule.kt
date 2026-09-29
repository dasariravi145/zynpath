package com.zynpath.game.core.di

import com.zynpath.game.core.billing.BillingRepository
import com.zynpath.game.core.billing.BillingRepositoryImpl
import com.zynpath.game.core.premium.SubscriptionEntitlementRepository
import com.zynpath.game.core.premium.SubscriptionEntitlementRepositoryImpl
import com.zynpath.game.core.premium.network.OkHttpSubscriptionApiService
import com.zynpath.game.core.premium.network.SubscriptionApiService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BillingModule {

    @Binds
    @Singleton
    abstract fun bindBillingRepository(
        impl: BillingRepositoryImpl
    ): BillingRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionApiService(
        impl: OkHttpSubscriptionApiService
    ): SubscriptionApiService

    @Binds
    @Singleton
    abstract fun bindSubscriptionEntitlementRepository(
        impl: SubscriptionEntitlementRepositoryImpl
    ): SubscriptionEntitlementRepository
}
