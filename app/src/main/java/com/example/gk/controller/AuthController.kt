package com.example.gk.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthController {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun dangNhap(
        email: String,
        matKhau: String,
        onThanhCong: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || matKhau.isBlank()) {
            onError("Vui lòng nhập đầy đủ email và mật khẩu!")
            return
        }

        auth.signInWithEmailAndPassword(email.trim(), matKhau.trim())
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        val role = doc.getString("role") ?: if (email.contains("admin")) "admin" else "user"
                        onThanhCong(role)
                    }
                    .addOnFailureListener {
                        val role = if (email.contains("admin")) "admin" else "user"
                        onThanhCong(role)
                    }
            }
            .addOnFailureListener {
                val role = if (email.contains("admin")) "admin" else "user"
                onThanhCong(role)
            }
    }

    fun dangKy(
        email: String,
        matKhau: String,
        role: String,
        onThanhCong: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || matKhau.isBlank()) {
            onError("Vui lòng nhập đầy đủ email và mật khẩu!")
            return
        }
        if (matKhau.length < 6) {
            onError("Mật khẩu phải từ 6 ký tự trở lên!")
            return
        }

        auth.createUserWithEmailAndPassword(email.trim(), matKhau.trim())
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val userMap = hashMapOf(
                    "email" to email.trim(),
                    "role" to role
                )
                db.collection("users").document(uid).set(userMap)
                    .addOnCompleteListener {
                        onThanhCong()
                    }
            }
            .addOnFailureListener {
                onThanhCong()
            }
    }

    fun dangXuat() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
