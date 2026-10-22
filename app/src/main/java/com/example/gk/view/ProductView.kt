package com.example.gk.view

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gk.controller.ProductController
import com.example.gk.model.Product

@Composable
fun ProductScreen(
    controller: ProductController,
    vaiTro: String = "admin",
    onDangXuat: () -> Unit = {}
) {
    val context = LocalContext.current

    var danhSachSP by remember {
        mutableStateOf(emptyList<Product>())
    }

    var tenSp by remember { mutableStateOf("") }
    var loaiSp by remember { mutableStateOf("") }
    var giaSp by remember { mutableStateOf("") }
    var linkAnh by remember { mutableStateOf("") }
    var tenFileAnh by remember { mutableStateOf("Thêm ảnh") }

    var idDangSua by remember { mutableStateOf("") }

    val chonAnhLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            linkAnh = uri.toString()
            tenFileAnh = uri.lastPathSegment?.substringAfterLast("/") ?: "pexels-blaque-x-264516-863963.jpg"
        }
    }

    LaunchedEffect(Unit) {
        controller.layDanhSachRealtime { danhSachMoi ->
            danhSachSP = danhSachMoi
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dữ liệu sản phẩm",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (vaiTro == "admin") Color(0xFFE3F2FD) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (vaiTro == "admin") "ADMIN" else "USER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (vaiTro == "admin") Color(0xFF1976D2) else Color(0xFF616161),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onDangXuat) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            }
        }

        if (vaiTro == "admin") {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tenSp,
                onValueChange = { tenSp = it },
                label = { Text("Tên sản phẩm") },
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = loaiSp,
                onValueChange = { loaiSp = it },
                label = { Text("Loại sản phẩm") },
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = giaSp,
                onValueChange = { input ->
                    val raw = input.filter { it.isDigit() }
                    giaSp = if (raw.isNotEmpty()) {
                        controller.dinhDangTien(raw.toLongOrNull() ?: 0L)
                    } else {
                        ""
                    }
                },
                label = { Text("Giá sản phẩm") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedCard(
                onClick = { chonAnhLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color(0xFF90CAF9))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = tenFileAnh,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val ten = tenSp.trim()
                    val loai = loaiSp.trim()
                    val giaNum = giaSp.replace(".", "").trim().toLongOrNull() ?: 0L

                    if (ten.isEmpty() || loai.isEmpty() || giaNum <= 0L) {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ tên, loại và giá hợp lệ!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (idDangSua.isEmpty()) {
                        controller.themSanPham(ten, loai, giaNum, linkAnh) { spMoi ->
                            danhSachSP = danhSachSP + spMoi
                            Toast.makeText(context, "Đã thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val idHienTai = idDangSua
                        controller.suaSanPham(idHienTai, ten, loai, giaNum, linkAnh) {
                            danhSachSP = danhSachSP.map {
                                if (it.id == idHienTai) Product(idHienTai, ten, loai, giaNum, linkAnh) else it
                            }
                            Toast.makeText(context, "Đã cập nhật sản phẩm!", Toast.LENGTH_SHORT).show()
                            idDangSua = ""
                        }
                    }

                    tenSp = ""
                    loaiSp = ""
                    giaSp = ""
                    linkAnh = ""
                    tenFileAnh = "Thêm ảnh"
                },
                modifier = Modifier.fillMaxWidth().height(45.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text(
                    text = if (idDangSua.isEmpty()) "THÊM SẢN PHẨM" else "CẬP NHẬT SẢN PHẨM",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Danh sách sản phẩm:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(danhSachSP, key = { it.id }) { sp ->
                OutlinedCard(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0xFF81D4FA)),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = sp.image.ifBlank { "https://images.pexels.com/photos/56866/garden-rose-red-pink-56866.jpeg" },
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(68.dp).clip(RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Tên sp: ${sp.tenSanPham}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Giá sp: ${controller.dinhDangTien(sp.gia)}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Loại sp: ${sp.loaiSanPham}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }

                        if (vaiTro == "admin") {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        idDangSua = sp.id
                                        tenSp = sp.tenSanPham
                                        loaiSp = sp.loaiSanPham
                                        giaSp = controller.dinhDangTien(sp.gia)
                                        linkAnh = sp.image
                                        tenFileAnh = if (sp.image.isNotEmpty()) "Đã có ảnh" else "Thêm ảnh"
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFFFFA000))
                                }

                                IconButton(
                                    onClick = {
                                        val idCanXoa = sp.id
                                        danhSachSP = danhSachSP.filter { it.id != idCanXoa }
                                        if (idDangSua == idCanXoa) {
                                            idDangSua = ""
                                            tenSp = ""
                                            loaiSp = ""
                                            giaSp = ""
                                            linkAnh = ""
                                            tenFileAnh = "Thêm ảnh"
                                        }
                                        controller.xoaSanPham(idCanXoa) {
                                            Toast.makeText(context, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
