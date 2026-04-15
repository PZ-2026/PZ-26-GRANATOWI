package com.example.artsphere.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.RetrofitClient
import com.example.artsphere.api.CategoryBackendResponse
import com.example.artsphere.api.UpdateCategoryRequest // Dodany import
import kotlinx.coroutines.launch
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoriesScreen(
    refreshTrigger: Int = 0,
    onBackClick: () -> Unit,
    onCategoryClick: (CategoryBackendResponse) -> Unit
) {
    var categories by remember { mutableStateOf<List<CategoryBackendResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun loadCategories() {
        isLoading = true
        errorMessage = null // Resetuj błąd przy każdej próbie ładowania
        scope.launch {
            try {
                val response = RetrofitClient.adminApi.getAllCategories()
                if (response.isSuccessful) {
                    categories = response.body() ?: emptyList()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = if (errorBody.isNullOrBlank()) {
                        "Błąd: ${response.code()}"
                    } else {
                        try {
                            val errorJson = Gson().fromJson(errorBody, Map::class.java)
                            errorJson["message"] as? String ?: errorBody
                        } catch (e: Exception) {
                            errorBody
                        }
                    }
                    errorMessage = "Błąd pobierania danych: $message"
                }
            } catch (e: Exception) {
                errorMessage = "Błąd połączenia: ${e.localizedMessage ?: "Sprawdź połączenie internetowe"}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(refreshTrigger) { loadCategories() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("ALL") }
    var selectedStatFilter by remember { mutableStateOf("ALL") }
    var showFilters by remember { mutableStateOf(false) }
    
    val filteredCategories = remember(categories, searchQuery, selectedTypeFilter, selectedStatFilter) {
        categories.filter {
            val matchesSearch = searchQuery.isEmpty() || 
                it.name.contains(searchQuery, ignoreCase = true) ||
                (it.description ?: "").contains(searchQuery, ignoreCase = true)
            
            val matchesType = when (selectedTypeFilter) {
                "ALL" -> true
                "PARENT" -> it.parentId == null
                "SUBCATEGORY" -> it.parentId != null
                else -> true
            }
            
            val matchesStat = when (selectedStatFilter) {
                "ALL" -> true
                "ACTIVE" -> it.isActive ?: true
                "INACTIVE" -> !(it.isActive ?: true)
                "PARENT" -> it.parentId == null
                "SUBCATEGORY" -> it.parentId != null
                else -> true
            }
            
            matchesSearch && matchesType && matchesStat
        }
    }
    
    val totalCategories = categories.size
    val activeCategories = categories.count { it.isActive ?: true }
    // val inactiveCategories = categories.count { !(it.isActive ?: true) } // Nie używane
    val parentCategories = categories.count { it.parentId == null }
    val subcategories = categories.count { it.parentId != null }
    // val totalArtworks = categories.sumOf { it.artworkCount ?: 0 } // Nie używane
    
    val adminGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE94057), Color(0xFF8A2387))
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zarządzanie kategoriami", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Wróć", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(adminGradient)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFE94057),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj kategorię")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE94057))
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(errorMessage!!, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { loadCategories() }) { Text("Ponów") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // ... Karty statystyk (zachowane bez zmian dla spójności)
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CategoryStatCard(Modifier.weight(1f), "Wszystkich", totalCategories.toString(), Icons.Default.Category, Color(0xFF2196F3), selectedStatFilter == "ALL", { selectedStatFilter = "ALL" })
                            CategoryStatCard(Modifier.weight(1f), "Aktywnych", activeCategories.toString(), Icons.Default.CheckCircle, Color(0xFF4CAF50), selectedStatFilter == "ACTIVE", { selectedStatFilter = "ACTIVE" })
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CategoryStatCard(Modifier.weight(1f), "Głównych", parentCategories.toString(), Icons.Default.AccountTree, Color(0xFF9C27B0), selectedStatFilter == "PARENT", { selectedStatFilter = "PARENT" })
                            CategoryStatCard(Modifier.weight(1f), "Podkategorii", subcategories.toString(), Icons.Default.SubdirectoryArrowRight, Color(0xFFFF9800), selectedStatFilter == "SUBCATEGORY", { selectedStatFilter = "SUBCATEGORY" })
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Szukaj kategorii...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                }

                if (showFilters) {
                    item {
                        Surface(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Typ kategorii:", fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilterChip(selected = selectedTypeFilter == "ALL", onClick = { selectedTypeFilter = "ALL" }, label = { Text("Wszystkie") })
                                    FilterChip(selected = selectedTypeFilter == "PARENT", onClick = { selectedTypeFilter = "PARENT" }, label = { Text("Główne") })
                                    FilterChip(selected = selectedTypeFilter == "SUBCATEGORY", onClick = { selectedTypeFilter = "SUBCATEGORY" }, label = { Text("Podkategorie") })
                                }
                            }
                        }
                    }
                }

                items(filteredCategories) { category ->
                    CategoryCard(category = category, onClick = { onCategoryClick(category) })
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var parentId by remember { mutableStateOf<Long?>(null) }
        var isSaving by remember { mutableStateOf(false) }
        
        val parentOptions = categories.filter { it.parentId == null }

        AlertDialog(
            onDismissRequest = { if (!isSaving) showAddDialog = false },
            title = { Text("Dodaj nową kategorię") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nazwa") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Opis") }, modifier = Modifier.fillMaxWidth())
                    
                    Text("Kategoria nadrzędna:", fontWeight = FontWeight.Bold)
                    Column {
                        RadioButtonRow(text = "Brak (Główna)", selected = parentId == null, onClick = { parentId = null })
                        parentOptions.forEach { cat ->
                            RadioButtonRow(text = cat.name, selected = parentId == cat.id, onClick = { parentId = cat.id })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !isSaving && name.isNotBlank(),
                    onClick = {
                        isSaving = true
                        scope.launch {
                            try {
                                val res = RetrofitClient.adminApi.createCategory(
                                    UpdateCategoryRequest(name = name, description = description, parentId = parentId)
                                )
                                if (res.isSuccessful) {
                                    loadCategories()
                                    showAddDialog = false
                                    Toast.makeText(context, "Kategoria dodana pomyślnie", Toast.LENGTH_SHORT).show()
                                } else {
                                    val errorBody = res.errorBody()?.string()
                                    val message = if (errorBody.isNullOrBlank()) {
                                        "Błąd: ${res.code()}"
                                    } else {
                                        try {
                                            val errorJson = Gson().fromJson(errorBody, Map::class.java)
                                            errorJson["message"] as? String ?: errorBody
                                        } catch (e: Exception) {
                                            errorBody
                                        }
                                    }
                                    Toast.makeText(context, "Nie udało się dodać kategorii: $message", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Błąd połączenia: ${e.localizedMessage ?: "Sprawdź połączenie internetowe"}", Toast.LENGTH_LONG).show()
                            } finally {
                                isSaving = false
                            }
                        }
                    }
                ) { 
                    if (isSaving) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White)
                    else Text("Dodaj")
                }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Anuluj") } }
        )
    }
}

@Composable
fun CategoryStatCard(modifier: Modifier, title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(85.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color.copy(alpha = 0.1f) else Color.White,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, Modifier.size(20.dp), color)
                Spacer(Modifier.width(8.dp))
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            }
            Text(title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CategoryCard(category: CategoryBackendResponse, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(8.dp), color = Color(0xFFE94057).copy(alpha = 0.1f)) {
                Icon(getCategoryIcon(category.iconName), null, Modifier.padding(8.dp), Color(0xFFE94057))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(category.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (category.parentName != null) Text("Kategoria główna: ${category.parentName}", fontSize = 12.sp, color = Color.Gray)
                Text("Liczba dzieł: ${category.artworkCount ?: 0}", fontSize = 12.sp, color = Color.Gray)
            }
            val isActive = category.isActive ?: true
            Surface(shape = RoundedCornerShape(16.dp), color = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)) {
                Text(if (isActive) "ON" else "OFF", Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, color = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828))
            }
        }
    }
}

private fun getCategoryIcon(iconName: String?): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "palette" -> Icons.Default.Palette
        "brush" -> Icons.Default.Brush
        "water_drop" -> Icons.Default.WaterDrop
        "sculpture" -> Icons.Default.Architecture
        "precision_manufacturing" -> Icons.Default.Handyman
        "camera_alt" -> Icons.Default.CameraAlt
        "portrait" -> Icons.Default.Portrait
        "auto_fix_high" -> Icons.Default.AutoFixHigh
        "computer" -> Icons.Default.Computer
        "draw" -> Icons.Default.Draw
        "museum" -> Icons.Default.Museum
        else -> Icons.Default.Category
    }
}
