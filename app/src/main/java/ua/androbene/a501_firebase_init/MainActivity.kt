package ua.androbene.a501_firebase_init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.crashlytics.ktx.setCustomKeys
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import ua.androbene.a501_firebase_init.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var crashlytics: FirebaseCrashlytics
    private lateinit var customKeySamples: CustomKeySamples

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Accessory.logEventToFireBase("OnCreateMAIN")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

        customKeySamples = CustomKeySamples(applicationContext)
        customKeySamples.setSampleCustomKeys()
        customKeySamples.updateAndTrackNetworkState()


        // Получаем экземпляр Crashlytics
        crashlytics = Firebase.crashlytics
        // Log the onCreate event, this will also be printed in logcat
        crashlytics.log("onCreate")
        // Add some custom values and identifiers to be included in crash reports
        crashlytics.setCustomKeys {
            key("MeaningOfLife", 42)
            key("LastUIAction", "Test value")
        }
        crashlytics.setUserId("7777777772")
        // Report a non-fatal exception, for demonstration purposes
        crashlytics.recordException(Exception("Non-fatal exception: 77777 something went wrong !"))


        // Получаем экземпляр Remote Config.
        remoteConfig = Firebase.remoteConfig
        // Create a Remote Config Setting to enable developer mode
        val configSettings = remoteConfigSettings {
            // increase the number of fetches available per hour during development
            minimumFetchIntervalInSeconds = 2
        }
        remoteConfig.setConfigSettingsAsync(configSettings)  // use Remote Config Setting to set the minimum fetch interval.

        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

//        fetchRemoteData()
        displayRCData()


        binding.btnFetch.setOnClickListener {
            fetchRemoteData()
        }
        binding.btnReset.setOnClickListener {
            crashlytics.log("btnReset(crash) button clicked.")
            resetRC()
        }

        binding.btnResult.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }

        crashlytics.log("Activity created")
    }

    override fun onDestroy() {
        super.onDestroy()
        customKeySamples.stopTrackingNetworkState()
    }

    private fun resetRC() {
//        throw Exception("MY_EXEPTION_resetRC")
        remoteConfig.reset()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val configSettings = remoteConfigSettings {
                        minimumFetchIntervalInSeconds = 3
                    }
                    remoteConfig.setConfigSettingsAsync(configSettings)
                    remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
                        .addOnCompleteListener { displayRCData() }
                } else {
                    Log.d(TAG, "reset failed")
                }
            }
    }

    private fun fetchRemoteData() {
        binding.tvMainText.text = remoteConfig[MAIN_TEXT_RC_KEY].asString()
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    Toast.makeText(
                        this, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.d(TAG, "Fetch failed: ${task.exception?.message}")
                    Toast.makeText(this, "Fetch failed ", Toast.LENGTH_SHORT).show()
                }
                displayRCData()
            }
    }

    private fun displayRCData() {
        val mainText = remoteConfig[MAIN_TEXT_RC_KEY].asString() // 1й вариант написания
        val textColor = remoteConfig.getString(MAIN_TEXT_COLOR_RC_KEY) // 2й вариант написания
            .let {
                try {
                    Color.parseColor(it)
                } catch (e: Exception) {
                    crashlytics.recordException(e)
                    Color.YELLOW
                }
            } // 2й вариант написания
        binding.tvMainText.isAllCaps = remoteConfig[MAIN_TEXT_CAPS_RC_KEY].asBoolean()
        binding.tvMainText.text = mainText
        binding.tvMainText.setTextColor(textColor)
    }

    companion object {
        private const val TAG = "MainActivity"

        // Remote Config keys
        private const val MAIN_TEXT_RC_KEY = "MAIN_TEXT_RC_KEY"
        private const val MAIN_TEXT_CAPS_RC_KEY = "MAIN_TEXT_CAPS_RC_KEY"
        private const val MAIN_TEXT_COLOR_RC_KEY = "MAIN_TEXT_COLOR_RC_KEY"
    }
}