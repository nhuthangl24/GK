package com.example.gk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.gk.controller.AuthController
import com.example.gk.controller.ProductController
import com.example.gk.view.LoginScreen
import com.example.gk.view.ProductScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productController = ProductController()
        val authController = AuthController()

        setContent {
            var daDangNhap by remember { mutableStateOf(false) }
            var vaiTro by remember { mutableStateOf("admin") }

            if (!daDangNhap) {
                LoginScreen(
                    authController = authController,
                    onDangNhapThanhCong = { role ->
                        vaiTro = role
                        daDangNhap = true
                    }
                )
            } else {
                ProductScreen(
                    controller = productController,
                    vaiTro = vaiTro,
                    onDangXuat = {
                        authController.dangXuat()
                        daDangNhap = false
                    }
                )
            }
        }
    }
}
