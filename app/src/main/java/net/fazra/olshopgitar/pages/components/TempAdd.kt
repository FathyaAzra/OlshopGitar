package net.fazra.olshopgitar.pages.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import net.fazra.olshopgitar.api.ImgurService
import net.fazra.olshopgitar.data.Item
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun TempAdd(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Produk") })
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") })
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Harga") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") })
        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stok") })

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Pilih Gambar")
        }

        Button(
            onClick = {
                if (imageUri != null && name.isNotEmpty()) {
                    isUploading = true

                    scope.launch {
                        try {
                            val inputStream = context.contentResolver.openInputStream(imageUri!!)
                            val bytes = inputStream!!.readBytes()
                            val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                            val body = MultipartBody.Part.createFormData("image", "upload.jpg", requestFile)

                            val response = ImgurService.api.uploadImage(
                                authHeader = ImgurService.getAuthHeader(),
                                image = body
                            )

                            if (response.success) {
                                val downloadUrl = response.data.link
                                val itemId = System.currentTimeMillis().toInt()
                                val item = Item(
                                    id = itemId,
                                    name = name,
                                    category = category,
                                    price = price.toIntOrNull() ?: 0,
                                    description = description,
                                    stock = stock.toIntOrNull() ?: 0,
                                    photoUrl = downloadUrl
                                )
                                val dbRef = FirebaseDatabase.getInstance().getReference("items")
                                dbRef.child(itemId.toString()).setValue(item)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                            } else {
                                Toast.makeText(context, "Upload ke Imgur gagal", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isUploading = false
                        }
                    }
                } else {
                    Toast.makeText(context, "Isi semua field & pilih gambar", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isUploading
        ) {
            Text(if (isUploading) "Menyimpan..." else "Simpan Produk")
        }
    }
}
