package ua.androbene.a501_firebase_init

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import ua.androbene.a501_firebase_init.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем экземпляр Remote Config.
        remoteConfig = Firebase.remoteConfig

        // Create a Remote Config Setting to enable developer mode
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 2 // increase the number of fetches available per hour during development
        }
        remoteConfig.setConfigSettingsAsync(configSettings)  // use Remote Config Setting to set the minimum fetch interval.

        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        fetchWelcome()

        binding.btn.setOnClickListener {
            fetchWelcome()
        }
    }

    private fun fetchWelcome() {
        binding.tvHello.text = remoteConfig[LOADING_PHRASE_CONFIG_KEY].asString()
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
                    Toast.makeText(this, "Fetch failed", Toast.LENGTH_SHORT).show()
                }
                displayWelcomeMessage()
            }
    }

    private fun displayWelcomeMessage() {
        val welcomeMessage = remoteConfig[WELCOME_MESSAGE_KEY].asString() // 1й вариант написания через []
        val textColor = remoteConfig.getString(WELCOME_TEXT_COLOR_KEY).let { Color.parseColor(it) } // 2й вариант написания
        binding.tvHello.isAllCaps = remoteConfig[WELCOME_MESSAGE_CAPS_KEY].asBoolean()
        binding.tvHello.text = welcomeMessage
        binding.tvHello.setTextColor(textColor)
    }

    companion object {
        private const val TAG = "MainActivity"
        // Remote Config keys
        private const val LOADING_PHRASE_CONFIG_KEY = "loading_phrase"
        private const val WELCOME_MESSAGE_KEY = "welcome_message"
        private const val WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps"
        private const val WELCOME_TEXT_COLOR_KEY = "welcome_text_color"
    }
}