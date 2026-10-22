package com.example.gk.controller

import com.example.gk.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ProductController {
    private val db = FirebaseFirestore.getInstance()

    fun dinhDangTien(gia: Long): String {
        return try {
            String.format(Locale.GERMAN, "%,d", gia)
        } catch (e: Exception) {
            gia.toString()
        }
    }

    fun layDanhSachRealtime(onCapNhat: (List<Product>) -> Unit) {
        try {
            db.collection("san_pham").addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && !snapshot.isEmpty) {
                    val danhSach = mutableListOf<Product>()
                    for (doc in snapshot.documents) {
                        val sp = Product(
                            id = doc.id,
                            tenSanPham = doc.getString("tenSanPham") ?: "",
                            loaiSanPham = doc.getString("loaiSanPham") ?: "",
                            gia = doc.getLong("gia") ?: 0L,
                            image = doc.getString("image") ?: ""
                        )
                        danhSach.add(sp)
                    }
                    onCapNhat(danhSach)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun themSanPham(ten: String, loai: String, gia: Long, image: String, onXong: (Product) -> Unit) {
        val anhLuu = if (image.isNotBlank()) image else "https://images.pexels.com/photos/56866/garden-rose-red-pink-56866.jpeg"
        val duLieu = hashMapOf(
            "tenSanPham" to ten,
            "loaiSanPham" to loai,
            "gia" to gia,
            "image" to anhLuu
        )

        val idMoi = "sp_${System.currentTimeMillis()}"
        val spMoi = Product(idMoi, ten, loai, gia, anhLuu)

        onXong(spMoi)

        try {
            db.collection("san_pham").add(duLieu)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun suaSanPham(id: String, ten: String, loai: String, gia: Long, image: String, onXong: () -> Unit) {
        val anhLuu = if (image.isNotBlank()) image else "https://images.pexels.com/photos/56866/garden-rose-red-pink-56866.jpeg"
        val duLieu = hashMapOf(
            "tenSanPham" to ten,
            "loaiSanPham" to loai,
            "gia" to gia,
            "image" to anhLuu
        )

        onXong()

        try {
            db.collection("san_pham").document(id).set(duLieu)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun xoaSanPham(id: String, onXong: () -> Unit) {
        onXong()

        try {
            if (id.isNotBlank()) {
                db.collection("san_pham").document(id).delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
