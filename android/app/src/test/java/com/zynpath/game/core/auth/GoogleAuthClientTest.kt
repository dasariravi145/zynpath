package com.zynpath.game.core.auth

import android.content.Context
import android.content.ContextWrapper
import com.zynpath.game.core.auth.provider.GoogleAuthClientImpl
import com.zynpath.game.core.auth.provider.GoogleCredentialResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Proxy

@OptIn(ExperimentalCoroutinesApi::class)
class GoogleAuthClientTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createDummyContext(): Context {
        return object : ContextWrapper(null) {
            override fun getPackageName(): String = "com.zynpath.game"
        }
    }

    @Test
    fun unconfiguredClient_isConfiguredReturnsFalse() {
        val dummyContext = createDummyContext()
        val client = GoogleAuthClientImpl(dummyContext, explicitClientId = null)

        assertFalse("Client should not be configured when explicitClientId is null", client.isConfigured())
        assertNull("getClientId() should be null when not configured", client.getClientId())
    }

    @Test
    fun configuredClient_isConfiguredReturnsTrue() {
        val dummyContext = createDummyContext()
        val expectedClientId = "123456789-abcdef.apps.googleusercontent.com"
        val client = GoogleAuthClientImpl(dummyContext, explicitClientId = expectedClientId)

        assertTrue("Client should be configured when client ID is provided", client.isConfigured())
        assertEquals(expectedClientId, client.getClientId())
    }

    @Test
    fun getCredential_whenNotConfigured_returnsNotConfiguredResult() = runTest {
        val dummyContext = createDummyContext()
        val client = GoogleAuthClientImpl(dummyContext, explicitClientId = null)

        val result = client.getCredential(dummyContext)
        assertTrue(
            "Expected GoogleCredentialResult.NotConfigured but got $result",
            result is GoogleCredentialResult.NotConfigured
        )
    }

    @Test
    fun getCredential_whenConfiguredButContextIsNotActivity_returnsFailure() = runTest {
        val dummyContext = createDummyContext()
        val client = GoogleAuthClientImpl(
            dummyContext,
            explicitClientId = "123456789-abcdef.apps.googleusercontent.com"
        )

        val result = client.getCredential(dummyContext)
        assertTrue(
            "Expected GoogleCredentialResult.Failure when non-activity context is passed but got $result",
            result is GoogleCredentialResult.Failure
        )
        val failure = result as GoogleCredentialResult.Failure
        assertTrue(failure.message.contains("Activity context"))
    }
}
