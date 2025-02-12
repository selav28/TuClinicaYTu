package com.example.tuclinicaytu

import android.util.Log
import androidx.compose.foundation.layout.add
import com.example.tuclinicaytu.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object UserSearchManager {
    private val TAG = "UserSearchManager"
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    fun findUsersByEmail(email: String, callback: (UserSearchResult) -> Unit) {
        findUserByEmailInFirestore(email, callback)
    }

    fun findUserByEmailInFirestore(email: String, callback: (UserSearchResult) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                val users = mutableListOf<UserInfo>()
                for (document in documents) {
                    val user = UserInfo(
                        document.getString("uid") ?: "",
                        document.getString("email"),
                        document.get("providers") as List<String>,
                        document.getLong("creationTimestamp"),
                        document.getLong("lastSignInTimestamp")
                    )
                    users.add(user)
                }
                callback(UserSearchResult.Success(users))
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                callback(UserSearchResult.Error("Error getting documents: ${exception.message}"))
            }
    }

    fun createUserInFirestore(user: FirebaseUser) {
        val userMap = hashMapOf(
            "uid" to user.uid,
            "email" to user.email,
            "providers" to user.providerData.map { it.providerId },
            "creationTimestamp" to user.metadata?.creationTimestamp,
            "lastSignInTimestamp" to user.metadata?.lastSignInTimestamp
        )
        db.collection("users").document(user.uid)
            .set(userMap)
            .addOnSuccessListener { Log.d(TAG, "User added to Firestore") }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding user to Firestore", e) }
    }
}