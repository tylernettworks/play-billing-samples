/*
 * Copyright 2018 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.subscriptions.data.network.firebase

import com.example.subscriptions.data.ContentResource
import com.example.subscriptions.data.SubscriptionStatus
import com.example.subscriptions.data.network.retrofit.ServerFunctionsImpl
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface to perform the Firebase Function calls and expose the results with [subscriptions].
 *
 * Use this class by observing the [subscriptions] StateFlow.
 * Any server updates will be communicated through this StateFlow.
 */
interface ServerFunctions {

    /**
     * True when there are pending network requests.
     */
    val loading: StateFlow<Boolean>

    /**
     * The latest subscription data from the server.
     *
     * Must be observed and active in order to receive updates from the server.
     */
    val subscriptions: StateFlow<List<SubscriptionStatus>>

    /**
     * The basic content URL.
     */
    val basicContent: StateFlow<ContentResource?>

    /**
     * The premium content URL.
     */
    val premiumContent: StateFlow<ContentResource?>

    /**
     * Fetch basic content and post results to [basicContent].
     * This will fail if the user does not have a basic subscription.
     */
    suspend fun updateBasicContent()

    /**
     * Fetch premium content and post results to [premiumContent].
     * This will fail if the user does not have a premium subscription.
     */
    suspend fun updatePremiumContent()

    /**
     * Fetches subscription data from the server and posts successful results to [subscriptions].
     */
    suspend fun updateSubscriptionStatus()

    /**
     * Register a subscription with the server and posts successful results to [subscriptions].
     */
    suspend fun registerSubscription(sku: String, purchaseToken: String)

    /**
     * Transfer subscription to this account posts successful results to [subscriptions].
     */
    suspend fun transferSubscription(sku: String, purchaseToken: String)

    /**
     * Register Instance ID when the user signs in or the token is refreshed.
     */
    suspend fun registerInstanceId(instanceId: String)

    /**
     * Unregister when the user signs out.
     */
    suspend fun unregisterInstanceId(instanceId: String)

    companion object {

        @Volatile
        private var INSTANCE: ServerFunctions? = null

        fun getInstance(): ServerFunctions =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: ServerFunctionsImpl().also { INSTANCE = it }
                }
    }

}
