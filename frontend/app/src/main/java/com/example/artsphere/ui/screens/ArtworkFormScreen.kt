package com.example.artsphere.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.ArtworkRequest
import com.example.artsphere.api.CategoryResponse
import com.example.artsphere.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkFormScreen(
    userId: Long,
    artworkId: Long? = null, // null = dodawanie, wartość = edycja
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isPriceless by remember { mutableStateOf(false) }
    var artist by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var depth by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    var categories by remember { mutableStateOf<List<CategoryResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isEditMode = artworkId != null

    // Załaduj kategorie i dane dzieła (w trybie edycji)
    LaunchedEffect(artworkId) {
        try {
            // Załaduj kategorie
            val categoriesResponse = RetrofitClient.artworkApi.getAllCategories()
            if (categoriesResponse.isSuccessful && categoriesResponse.body() != null) {
                categories = categoriesResponse.body()!!
            }

            // Załaduj dane dzieła w trybie edycji
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
            errorMessage = "Nie udało się załadować danych"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (isEditMode) "Edytuj dzieło" else "Dodaj dzieło") 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Komunikat o błędzie
            if (errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Tytuł
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; errorMessage = "" },
                label = { Text("Tytuł *") },
                leadingIcon = { Icon(Icons.Default.Title, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            // Artysta
            OutlinedTextField(
                value = artist,
                onValueChange = { artist = it; errorMessage = "" },
                label = { Text("Artysta") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            // Checkbox "Bezcenne"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isPriceless,
                    onCheckedChange = { isPriceless = it; errorMessage = "" }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bezcenne")
            }

            // Cena (tylko jeśli nie bezcenne)
            if (!isPriceless) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it; errorMessage = "" },
                    label = { Text("Cena (zł) *") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
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
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Brak kategorii") },
                        onClick = {
                            selectedCategoryId = null
                            categoryExpanded = false
                        }
                    )
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategoryId = category.id
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Wymiary
            Text("Wymiary (cm)", fontWeight = FontWeight.Medium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = width,
                    onValueChange = { width = it; errorMessage = "" },
                    label = { Text("Szerokość") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it; errorMessage = "" },
                    label = { Text("Wysokość") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = depth,
                    onValueChange = { depth = it; errorMessage = "" },
                    label = { Text("Głębokość") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isLoading
                )
            }

            // Opis
            OutlinedTextField(
                value = description,
                onValueChange = { description = it; errorMessage = "" },
                label = { Text("Opis") },
                leadingIcon = { Icon(Icons.Default.Description, null) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 4,
                enabled = !isLoading
            )

            // Ścieżka do obrazu
            OutlinedTextField(
                value = imagePath,
                onValueChange = { imagePath = it; errorMessage = "" },
                label = { Text("Ścieżka do obrazu") },
                leadingIcon = { Icon(Icons.Default.Image, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                placeholder = { Text("np. /path/to/image.jpg") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Przycisk zapisu
            Button(
                onClick = {
                    // Walidacja
                    if (title.isBlank()) {
                        errorMessage = "Tytuł jest wymagany"
                        return@Button
                    }

                    if (!isPriceless && price.isBlank()) {
                        errorMessage = "Cena jest wymagana jeśli dzieło nie jest bezcenne"
                        return@Button
                    }

                    val priceValue = if (isPriceless) null else price.toDoubleOrNull()
                    if (!isPriceless && priceValue == null) {
                        errorMessage = "Nieprawidłowy format ceny"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = ""

                    coroutineScope.launch {
                        try {
                            val request = ArtworkRequest(
                                title = title,
                                description = description.ifBlank { null },
                                price = priceValue,
                                isPriceless = isPriceless,
                                artist = artist.ifBlank { null },
                                imagePath = imagePath.ifBlank { null },
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
                                errorMessage = response.errorBody()?.string() ?: "Błąd zapisu"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Błąd połączenia: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isEditMode) "Zaktualizuj" else "Dodaj dzieło")
                }
            }

            // Przycisk anuluj
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Anuluj")
            }
        }
    }
}