package com.nhanbt.demosnsfcm

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.CreatePlatformEndpointRequest
import aws.sdk.kotlin.services.sns.model.SetSubscriptionAttributesRequest
import aws.sdk.kotlin.services.sns.model.SubscribeRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val topicArn = "arn:aws:sns:us-east-1:814443606628:test-sns"
    private val appArn = "arn:aws:sns:us-east-1:814443606628:app/GCM/Test-SNS-1702"
    private val accessKey = "AKIA33IE7GJSJIWSYL44"
    private val secretAccess = "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"
    private lateinit var deviceToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        retrieveCurrentRegistrationToken()

        askNotificationPermission()
    }

    private fun askNotificationPermission() {
        // Request permission
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            // Permission is granted
            if (granted) {
                // TODO: Done get permission
            }
            // Permission is denied
            else {
                // Show error
                Log.w("ERROR!!", "Cannot get permission!")
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun retrieveCurrentRegistrationToken() {
        Log.d("FIREBASE", FirebaseMessaging.getInstance().toString())
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            deviceToken = task.result
            // Log
            Log.d("DEVICE_TOKEN", deviceToken)
            GlobalScope.launch {
                subApplicationNotification(topicArn, deviceToken)
            }
        }).addOnFailureListener(OnFailureListener { exception ->
            Log.w("FIREBASE_EXCEPTION", "${exception.message}")
        })
    }

    private suspend fun subApplicationNotification(topicArnVal: String?, deviceToken: String) {
        SnsClient {
            region = "us-east-1"
            credentialsProvider = getStaticCredential()
        }.use { snsClient ->
            // Create Arn endpoint
            val createPlatformEndpointRequest = CreatePlatformEndpointRequest {
                platformApplicationArn = appArn
                token = deviceToken
            }
            val endPointArn =
                snsClient.createPlatformEndpoint(createPlatformEndpointRequest).endpointArn

            // Subscribe to topic
            val request = SubscribeRequest {
                protocol = "application"
                returnSubscriptionArn = true
                endpoint = endPointArn
                topicArn = topicArnVal
            }
            Log.d("SNS_CLIENT", request.toString())
            val result = snsClient.subscribe(request)
            println("The subscription Arn is ${result.subscriptionArn}")
            val filterPolicy = "{\"userId\": [\"user-id-test\"]}"
            val setSubscriptionAttributesRequest = SetSubscriptionAttributesRequest {
                subscriptionArn = result.subscriptionArn
                attributeName = "FilterPolicy"
                attributeValue = filterPolicy
            }

            val filterResult = snsClient.setSubscriptionAttributes(setSubscriptionAttributesRequest)
            println(filterResult.toString())
        }
    }

    private fun getStaticCredential(): StaticCredentialsProvider {
        val staticCredentials = StaticCredentialsProvider {
            accessKeyId = accessKey
            secretAccessKey = secretAccess
        }
        return staticCredentials
    }
}
