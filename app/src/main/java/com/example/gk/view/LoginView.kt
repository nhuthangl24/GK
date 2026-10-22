package com.example.gk.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gk.controller.AuthController

@Composable
fun LoginScreen(
    authController: AuthController,
    onDangNhapThanhCong: (String) -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var matKhau by remember { mutableStateOf("") }
    var hienThiMatKhau by remember { mutableStateOf(false) }
    var dangOTrangDangKy by remember { mutableStateOf(false) }
    var quyenDangKy by remember { mutableStateOf("admin") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "APP ECOMER",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E88E5)
        )
        Text(
            text = if (dangOTrangDangKy) "Đăng ký tài khoản mới" else "Đăng nhập hệ thống",
            fontSize = 15.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = matKhau,
            onValueChange = { matKhau = it },
            label = { Text("Mật khẩu") },
            singleLine = true,
            visualTransformation = if (hienThiMatKhau) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { hienThiMatKhau = !hienThiMatKhau }) {
                    Icon(
                        imageVector = if (hienThiMatKhau) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (dangOTrangDangKy) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Vai trò: ", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(
                    selected = (quyenDangKy == "admin"),
                    onClick = { quyenDangKy = "admin" }
                )
                Text(
                    text = "Admin",
                    modifier = Modifier.clickable { quyenDangKy = "admin" }
                )
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = (quyenDangKy == "user"),
                    onClick = { quyenDangKy = "user" }
                )
                Text(
                    text = "User",
                    modifier = Modifier.clickable { quyenDangKy = "user" }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (dangOTrangDangKy) {
                    authController.dangKy(
                        email = email,
                        matKhau = matKhau,
                        role = quyenDangKy,
                        onThanhCong = {
                            Toast.makeText(context, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_SHORT).show()
                            dangOTrangDangKy = false
                        },
                        onError = { loi ->
                            Toast.makeText(context, loi, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    authController.dangNhap(
                        email = email,
                        matKhau = matKhau,
                        onThanhCong = { role ->
                            Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                            onDangNhapThanhCong(role)
                        },
                        onError = { loi ->
                            Toast.makeText(context, loi, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
        ) {
            Text(
                text = if (dangOTrangDangKy) "ĐĂNG KÝ" else "ĐĂNG NHẬP",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(
            onClick = { dangOTrangDangKy = !dangOTrangDangKy }
        ) {
            Text(
                text = if (dangOTrangDangKy) "Đã có tài khoản? Đăng nhập ngay" else "Chưa có tài khoản? Đăng ký ngay",
                color = Color(0xFF1E88E5)
            )
        }
    }
}
