package ua.androbene.a501_firebase_init

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

object Accessory {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun logEventToFireBase(event: String) {
        firebaseAnalytics.logEvent(event) {}
    }

    fun logEventToFireBase(event: String, paramName: String, count: Long) {
        firebaseAnalytics.logEvent(event) {
            param(FirebaseAnalytics.Param.ITEM_NAME, paramName)
            param(FirebaseAnalytics.Param.VALUE, count)
        }
    }
}