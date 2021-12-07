package com.example.k_health

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

object Repository {
    var userId = Firebase.auth.currentUser?.uid.orEmpty()

    suspend fun getTabRecyclerView() {

    }
}