package com.example.artsphere.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import coil.compose.rememberAsyncImagePainter
import com.example.artsphere.api.ArtworkRequest
import com.example.artsphere.api.CategoryResponse
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

/**
 * Ekran formularza służący do dodawania nowego dzieła sztuki lub edycji istniejącego.
 * Zawiera pola do wprowadzenia tytułu, opisu, ceny, wymiarów, kategorii oraz wizualny wybór zdjęcia z galerii.
 *
 * @param userId Identyfikator zalogowanego użytkownika (sprzedawcy).
 * @param artworkId Opcjonalny identyfikator dzieła do edycji. Jeśli null, formularz działa w trybie dodawania.
 * @param onNavigateBack Funkcja wywoływana przy powrocie do poprzedniego ekranu.
 * @param onSuccess Funkcja wywoływana po pomyślnym zapisaniu/zaktualizowaniu dzieła.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkFormScreen(
    userId: Long,
    artworkId: Long? = null,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isPriceless by remember { mutableStateOf(false) }
    var artist by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var depth by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    var categories by remember { mutableStateOf<List<CategoryResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isEditMode = artworkId != null

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedUri = it
            imagePath = it.toString()
        }
    }

    LaunchedEffect(artworkId) {
        try {
            val categoriesResponse = RetrofitClient.artworkApi.getAllCategories()
            if (categoriesResponse.isSuccessful) {
                categories = categoriesResponse.body() ?: emptyList()
            }

            if (artworkId != null) {
                isLoading = true
                val response = RetrofitClient.artworkApi.getArtworkById(artworkId)
                if (response.isSuccessful && response.body() != null) {
                    val artwork = response.body()!!
                    title = artwork.title
                    description = artwork.description ?: ""
                    price = artwork.price?.toString() ?: ""
                    isPriceless = artwork.isPriceless
                    artist = artwork.artist ?: ""
                    imagePath = artwork.imagePath ?: ""
                    width = artwork.width?.toString() ?: ""
                    height = artwork.height?.toString() ?: ""
                    depth = artwork.depth?.toString() ?: ""
                    selectedCategoryId = artwork.categoryId
                }
            }
        } catch (e: Exception) {
            errorMessage = "Błąd ładowania danych"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edytuj dzieło" else "Dodaj dzieło") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Wróć") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorMessage.isNotEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp))
                }
            }

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tytuł *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = artist, onValueChange = { artist = it }, label = { Text("Artysta") }, modifier = Modifier.fillMaxWidth())

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isPriceless, onCheckedChange = { isPriceless = it })
                Text("Bezcenne")
            }

            if (!isPriceless) {
                OutlinedTextField(
                    value = price, 
                    onValueChange = { price = it }, 
                    label = { Text("Cena (zł) *") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Kategoria
            var categoryExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = categories.find { it.id == selectedCategoryId }?.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Kategoria") },
                    leadingIcon = { Icon(Icons.Default.Category, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    DropdownMenuItem(text = { Text("Brak kategorii") }, onClick = { selectedCategoryId = null; categoryExpanded = false })
                    categories.forEach { category ->
                        DropdownMenuItem(text = { Text(category.name) }, onClick = { selectedCategoryId = category.id; categoryExpanded = false })
                    }
                }
            }

            // Wymiary
            Text("Wymiary (cm)", fontWeight = FontWeight.Medium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = width, onValueChange = { width = it }, label = { Text("Szerokość") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Wysokość") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = depth, onValueChange = { depth = it }, label = { Text("Głębokość") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            }

            // Opis
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Opis") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 4)

            // Sekcja zdjęcia
            Text("Zdjęcie dzieła", fontWeight = FontWeight.Medium)
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imagePath.isBlank()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Text("Dotknij, aby wybrać zdjęcie z galerii", color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    val displayImage = if (imagePath.startsWith("uploads/")) RetrofitClient.BASE_URL + imagePath else imagePath
                    Image(
                        painter = rememberAsyncImagePainter(displayImage),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { imagePath = ""; selectedUri = null },
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    ) { Icon(Icons.Default.Close, null, tint = Color.White) }
                }
            }

            OutlinedTextField(
                value = imagePath,
                onValueChange = { imagePath = it },
                label = { Text("Link URL do obrazu (opcjonalnie)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Zostaw puste jeśli wybrano z galerii") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isBlank()) { errorMessage = "Tytuł jest wymagany"; return@Button }
                    isLoading = true
                    errorMessage = ""
                    coroutineScope.launch {
                        try {
                            var finalImagePath = imagePath

                            // 1. Upload zdjęcia jeśli wybrano z galerii
                            if (selectedUri != null) {
                                val file = uriToFile(context, selectedUri!!)
                                if (file != null) {
                                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                                    val uploadRes = RetrofitClient.artworkApi.uploadImage(body)
                                    if (uploadRes.isSuccessful) {
                                        val bodyMap = uploadRes.body()
                                        finalImagePath = bodyMap?.get("imagePath") ?: imagePath
                                    } else {
                                        errorMessage = "Błąd przesyłania zdjęcia: ${uploadRes.code()}"
                                        isLoading = false
                                        return@launch
                                    }
                                }
                            }

                            // 2. Zapis dzieła
                            val request = ArtworkRequest(
                                title = title,
                                description = description.ifBlank { null },
                                price = if (isPriceless) null else price.toDoubleOrNull(),
                                isPriceless = isPriceless,
                                artist = artist.ifBlank { null },
                                imagePath = finalImagePath,
                                width = width.toDoubleOrNull(),
                                height = height.toDoubleOrNull(),
                                depth = depth.toDoubleOrNull(),
                                categoryId = selectedCategoryId
                            )

                            val response = if (isEditMode) {
                                RetrofitClient.artworkApi.updateArtwork(artworkId!!, userId, request)
                            } else {
                                RetrofitClient.artworkApi.createArtwork(userId, request)
                            }

                            if (response.isSuccessful) {
                                onSuccess()
                            } else {
                                errorMessage = "Błąd zapisu: ${response.code()} - ${response.errorBody()?.string()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Błąd: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text(if (isEditMode) "Zaktualizuj" else "Dodaj dzieło")
            }
        }
    }
}

/**
 * Konwertuje Uri z galerii na tymczasowy plik, który można wysłać na serwer.
 */
private fun uriToFile(context: android.content.Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        null
    }
}
