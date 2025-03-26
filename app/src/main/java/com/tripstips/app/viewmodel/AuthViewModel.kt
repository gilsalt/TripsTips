package com.tripstips.app.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tripstips.app.model.User
import com.tripstips.app.view.activities.BaseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    private val _currentUserStatus = MutableLiveData<String>()
    val currentUserStatus: LiveData<String> get() = _currentUserStatus

    private val _userDetailUpdateStatus = MutableLiveData<String>()
    val userDetailUpdateStatus: LiveData<String> get() = _userDetailUpdateStatus

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        auth.currentUser?.let { firebaseUser ->
            fetchUserDetails(firebaseUser.uid)
        } ?: _currentUser.postValue(null)
    }

    fun signUp(name: String, email: String, password: String, profileImageUri: Uri?) {
        viewModelScope.launch {
            try {

                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                firebaseUser?.let { user ->
                    val userId = user.uid
                    var imageUrl = ""

                    profileImageUri?.let { uri ->
                        imageUrl = uploadProfileImage(userId, uri)
                    }

                    val userData = mapOf(
                        "userId" to userId,
                        "name" to name,
                        "email" to email,
                        "image" to imageUrl
                    )

                    saveUserToFireStore(userId, userData)

                    _currentUser.postValue(User(userId,name, email, imageUrl))
                }
            } catch (e: Exception) {
                _currentUserStatus.postValue("Signup Failed: ${e.message}")
            }
        }
    }


    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                auth.currentUser?.let { fetchUserDetails(it.uid) }
            } catch (e: Exception) {
                _currentUserStatus.postValue("Sign-in Failed: ${e.message}")
            }
        }
    }

    fun fetchUserDetails(userId: String) {
        viewModelScope.launch {
            try {
                val document = firestore.collection("users").document(userId).get().await()
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null){
                        BaseActivity.loggedUser = user
                    }
                    _currentUser.postValue(user)
                } else {
                    _currentUserStatus.postValue("User not found")
                }
            } catch (e: Exception) {
                _currentUserStatus.postValue("Error fetching user details: ${e.message}")
            }
        }
    }

    fun updateProfile(name: String?, profileImageUri: Uri?) {
        viewModelScope.launch {
            try {
                auth.currentUser?.let { user ->
                    val updates = mutableMapOf<String, Any>()

                    name?.let {
                        updates["name"] = it
                    }

                    profileImageUri?.let { uri ->
                        val imageUrl = uploadProfileImage(user.uid, uri)
                        updates["image"] = imageUrl
                    }

                    if (updates.isNotEmpty()) {
                        firestore.collection("users").document(user.uid)
                            .update(updates).await()
                        fetchUserDetails(user.uid)
                        delay(3000)
                        _userDetailUpdateStatus.postValue("Profile updated successfully")
                    } else {
                        _userDetailUpdateStatus.postValue("No changes to update")
                    }
                }
            } catch (e: Exception) {
                _userDetailUpdateStatus.postValue("Failed to update profile: ${e.message}")
            }
        }
    }

    private suspend fun uploadProfileImage(uid: String, imageUri: Uri): String {
        return try {
            val storageRef = storage.reference.child("profile_images/$uid.jpg")
            storageRef.putFile(imageUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            _userDetailUpdateStatus.postValue("Profile image upload failed: ${e.message}")
            ""
        }
    }

    private fun saveUserToFireStore(userId: String, userData: Map<String, Any>) {
        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .set(userData)
            .addOnSuccessListener {
                _currentUserStatus.postValue("Signup successful!")
            }
            .addOnFailureListener { e ->
                _currentUserStatus.postValue("Failed to save user data: ${e.message}")
            }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.postValue(null)
    }
}
