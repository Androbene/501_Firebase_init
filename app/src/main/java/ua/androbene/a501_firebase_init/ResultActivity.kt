package ua.androbene.a501_firebase_init

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import ua.androbene.a501_firebase_init.databinding.ActivityResultBinding
import kotlin.random.Random

class ResultActivity : AppCompatActivity() {
    //    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var crashlytics: FirebaseCrashlytics
    private lateinit var bind: ActivityResultBinding

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var userWasResultScreenTimesPS = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityResultBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.apply {
            btnOptimize.setOnClickListener {
                if (userWasResultScreenTimesPS < 10) userWasResultScreenTimesPS++

                Accessory.logEventToFireBase("BTN_${userWasResultScreenTimesPS}")

                tvResultTimes.text = userWasResultScreenTimesPS.toString()
                coroutineScope.launch {
                    for (i in 1..10) {
                        tvResultTimes.setTextColor(Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE))
                        delay(200)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()

        // ЛОГИ ЧЕРЕЗ АНАЛИТИКУ
//        val firebaseAnalytics = Firebase.analytics
//        firebaseAnalytics.logEvent("USER_WAS_ON_RESULT_SCREEN") {
//            param(FirebaseAnalytics.Param.ITEM_NAME, "TIMES_PER_SESSION")
//            param(FirebaseAnalytics.Param.VALUE, userWasResultScreenTimesPS)
//        }
        Accessory.logEventToFireBase(
            "USER_WAS_ON_RESULT_SCREEN",
            "TIMES_PER_SESSION",
            userWasResultScreenTimesPS
        )

        // ЛОГИ ЧЕРЕЗ КРАШЛИТИКУ
//        crashlytics = Firebase.crashlytics
//        try {
//            //работает, НО без веских причин на второй день прила НАЧАЛА крашиться
//            //и длилось это до переустановки из студии
//            throw ResultException("USER_WAS_ON_RESULT_SCREEN_${USER_WAS_ON_RESULT_SCREEN}_TIMES")
//        } catch (e: Exception) {
//            crashlytics.recordException(e)
//        }
        Log.d("lol", "onDestroy -> $userWasResultScreenTimesPS")
    }

    class ResultException(mess: String) : Exception(mess)
}