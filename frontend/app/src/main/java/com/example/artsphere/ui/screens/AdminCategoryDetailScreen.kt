package com.example.artsphere.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.RetrofitClient
import com.example.artsphere.api.CategoryBackendResponse
import com.example.artsphere.api.UpdateCategoryRequest // Dodany import
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryDetailScreen(
    category: CategoryBackendResponse,
    onRefresh: () -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showSubcategoriesDialog by remember { mutableStateOf(false) }

    fun formatDateTime(dateStr: String?): String {
        if (dateStr == null || dateStr.isEmpty()) return "N/A"
        return try {
            // Zakładając format ISO z backendu, np. 2023-10-27T10:00:00
            val cleanDate = dateStr.substringBefore(".") // Usuwamy nanosekundy jeśli są
            val date = if (cleanDate.contains("T")) {
                java.time.LocalDateTime.parse(cleanDate)
            } else {
                java.time.LocalDateTime.parse(cleanDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }
            date.format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"))
        } catch (e: Exception) {
            dateStr.take(16) // Zwracamy początek stringa jako fallback
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły: ${category.name}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nagłówek i status
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    getCategoryIcon(category.iconName),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = Color(0xFFE94057)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = category.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            val isActive = category.isActive ?: true
                            Surface(
                                color = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = if (isActive) "Aktywna" else "Nieaktywna",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        if (category.description != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = category.description,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ID: ${category.id} • Slug: /${category.slug ?: "n/a"}",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }

            // Statystyki
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                            Icon(Icons.Default.BarChart, null, Modifier.size(24.dp), Color(0xFFE94057))
                            Spacer(Modifier.width(8.dp))
                            Text("STATYSTYKI", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF666666))
                        }
                        
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CategoryStatColumn(Modifier.weight(1f), Icons.Default.Brush, (category.artworkCount ?: 0).toString(), "Dzieła", Color(0xFF2196F3))
                            CategoryStatColumn(Modifier.weight(1f), Icons.Default.Visibility, "N/A", "Kliknięcia", Color(0xFF4CAF50))
                            CategoryStatColumn(Modifier.weight(1f), Icons.Default.ShoppingCart, (category.soldArtworkCount ?: 0).toString(), "Sprzedane", Color(0xFFFF9800))
                        }
                    }
                }
            }
            
            // Historia
            item {
                CategoryInfoSection(title = "HISTORIA", icon = Icons.Default.History) {
                    CategoryInfoRow("Utworzono", formatDateTime(category.createdDate))
                    CategoryInfoRow("Modyfikacja", formatDateTime(category.lastModified))
                }
            }
            
            // Akcje
            item {
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, null, Modifier.size(24.dp), Color(0xFFE94057))
                            Spacer(Modifier.width(8.dp))
                            Text("ZARZĄDZANIE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF666666))
                        }
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showEditDialog = true }, 
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp), 
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2196F3))
                            ) {
                                Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Edytuj")
                            }
                            
                            val isActive = category.isActive ?: true
                            OutlinedButton(
                                onClick = { showStatusDialog = true }, 
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp), 
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isActive) Color(0xFFF44336) else Color(0xFF4CAF50))
                            ) {
                                Icon(if (isActive) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(if (isActive) "Dezaktywuj" else "Aktywuj")
                            }
                        }
                        
                        if (category.parentId == null) {
                            Button(
                                onClick = { showSubcategoriesDialog = true }, 
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                            ) {
                                Icon(Icons.Default.AccountTree, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Podkategorie")
                            }
                        }
                        
                        TextButton(
                            onClick = { showDeleteDialog = true }, 
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                        ) {
                            Icon(Icons.Default.Delete, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Usuń trwale")
                        }
                    }
                }
            }
        }
    }
    
    // Dialogi
    if (showStatusDialog) {
        val isActive = category.isActive ?: true
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text(if (isActive) "Dezaktywuj" else "Aktywuj") },
            text = { Text("Czy na pewno chcesz zmienić status kategorii \"${category.name}\"?") },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        try {
                            val res = RetrofitClient.adminApi.updateCategoryStatus(category.id, mapOf("isActive" to !isActive))
                            if (res.isSuccessful) {
                                onRefresh()
                                Toast.makeText(context, "Zmieniono status", Toast.LENGTH_SHORT).show()
                            } else {
                                val errorBody = res.errorBody()?.string()
                                val errorMessage = if (errorBody.isNullOrBlank()) {
                                    "Błąd: ${res.code()}"
                                } else {
                                    try {
                                        val errorJson = Gson().fromJson(errorBody, Map::class.java)
                                        errorJson["message"] as? String ?: errorBody
                                    } catch (e: Exception) {
                                        errorBody
                                    }
                                }
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Błąd połączenia: ${e.localizedMessage ?: "Sprawdź połączenie internetowe"}", Toast.LENGTH_LONG).show()
                        }
                        showStatusDialog = false
                    }
                }) { Text("Potwierdź") }
            },
            dismissButton = { TextButton(onClick = { showStatusDialog = false }) { Text("Anuluj") } }
        )
    }

    if (showEditDialog) {
        var newName by remember { mutableStateOf(category.name) }
        var newDesc by remember { mutableStateOf(category.description ?: "") }
        var selectedParentId by remember { mutableStateOf(category.parentId) }
        var categoriesList by remember { mutableStateOf<List<CategoryBackendResponse>>(emptyList()) }
        var isSaving by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            val res = RetrofitClient.adminApi.getAllCategories()
            if (res.isSuccessful) {
                categoriesList = res.body()?.filter { it.id != category.id && it.parentId == null } ?: emptyList()
            }
        }

        AlertDialog(
            onDismissRequest = { if (!isSaving) showEditDialog = false },
            title = { Text("Edytuj kategorię") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newName, 
                        onValueChange = { newName = it }, 
                        label = { Text("Nazwa") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDesc, 
                        onValueChange = { newDesc = it }, 
                        label = { Text("Opis") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text("Kategoria nadrzędna:", fontWeight = FontWeight.Bold)
                    Column {
                        RadioButtonRow(text = "Brak (Główna)", selected = selectedParentId == null, onClick = { selectedParentId = null })
                        categoriesList.forEach { cat ->
                            RadioButtonRow(text = cat.name, selected = selectedParentId == cat.id, onClick = { selectedParentId = cat.id })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !isSaving && newName.isNotBlank(),
                    onClick = {
                        isSaving = true
                        scope.launch {
                            try {
                                val res = RetrofitClient.adminApi.updateCategory(
                                    category.id, 
                                    UpdateCategoryRequest(
                                        name = newName, 
                                        description = newDesc,
                                        parentId = selectedParentId
                                    )
                                )
                                if (res.isSuccessful) {
                                    onRefresh()
                                    Toast.makeText(context, "Kategoria zaktualizowana", Toast.LENGTH_SHORT).show()
                                    showEditDialog = false
                                } else {
                                    val errorBody = res.errorBody()?.string()
                                    val errorMessage = if (errorBody.isNullOrBlank()) {
                                        "Błąd zapisu: ${res.code()}"
                                    } else {
                                        try {
                                            val errorJson = Gson().fromJson(errorBody, Map::class.java)
                                            errorJson["message"] as? String ?: errorBody
                                        } catch (e: Exception) {
                                            errorBody
                                        }
                                    }
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
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
                    else Text("Zapisz") 
                }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Anuluj") } }
        )
    }

    if (showSubcategoriesDialog) {
        var subcategories by remember { mutableStateOf<List<CategoryBackendResponse>>(emptyList()) }
        var subToDelete by remember { mutableStateOf<CategoryBackendResponse?>(null) }
        var isLoadingSubs by remember { mutableStateOf(true) }
        
        fun refreshSubs() {
            isLoadingSubs = true
            scope.launch {
                try {
                    val res = RetrofitClient.adminApi.getSubcategories(category.id)
                    if (res.isSuccessful) subcategories = res.body() ?: emptyList()
                } catch (e: Exception) {
                    Toast.makeText(context, "Błąd ładowania podkategorii: ${e.localizedMessage ?: "Sprawdź połączenie internetowe"}", Toast.LENGTH_LONG).show()
                } finally {
                    isLoadingSubs = false
                }
            }
        }

        LaunchedEffect(Unit) { refreshSubs() }

        AlertDialog(
            onDismissRequest = { showSubcategoriesDialog = false },
            title = { Text("Podkategorie \"${category.name}\"") },
            text = {
                Box(Modifier.heightIn(max = 400.dp)) {
                    if (isLoadingSubs) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn {
                            items(subcategories) { sub ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(sub.name, fontWeight = FontWeight.Bold)
                                        Text("/${sub.slug ?: ""}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                    IconButton(onClick = { subToDelete = sub }) {
                                        Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                                    }
                                }
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            }
                            if (subcategories.isEmpty()) item { Text("Brak podkategorii", color = Color.Gray, modifier = Modifier.padding(16.dp)) }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showSubcategoriesDialog = false }) { Text("Zamknij") } }
        )

        if (subToDelete != null) {
            AlertDialog(
                onDismissRequest = { subToDelete = null },
                title = { Text("Odłącz podkategorię") },
                text = { Text("Czy na pewno chcesz odłączyć \"${subToDelete?.name}\" od kategorii \"${category.name}\"?") },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            try {
                                val targetId = subToDelete?.id ?: return@launch
                                val res = RetrofitClient.adminApi.detachCategory(targetId)
                                if (res.isSuccessful) {
                                    Toast.makeText(context, "Podkategoria odłączona", Toast.LENGTH_SHORT).show()
                                    refreshSubs()
                                    subToDelete = null
                                    onRefresh()
                                } else {
                                    val errorBody = res.errorBody()?.string()
                                    val errorMessage = if (errorBody.isNullOrBlank()) {
                                        "Błąd odłączania: ${res.code()}"
                                    } else {
                                        try {
                                            val errorJson = Gson().fromJson(errorBody, Map::class.java)
                                            errorJson["message"] as? String ?: errorBody
                                        } catch (e: Exception) {
                                            errorBody
                                        }
                                    }
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Błąd połączenia: ${e.localizedMessage ?: "Sprawdź połączenie internetowe"}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Odłącz") }
                },
                dismissButton = { TextButton(onClick = { subToDelete = null }) { Text("Anuluj") } }
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Usuń kategorię") },
            text = { Text("Czy na pewno chcesz trwale usunąć kategorię \"${category.name}\" wraz ze wszystkimi jej podkategoriami?") },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        try {
                            val res = RetrofitClient.adminApi.deleteCategory(categoryId = category.id)
                            if (res.isSuccessful) {
                                onBack() // Powrót bo kategoria już nie istnieje
                                onRefresh()
                                Toast.makeText(context, "Kategoria usunięta", Toast.LENGTH_SHORT).show()
                            } else {
                                val errorBody = res.errorBody()?.string()
                                val errorMessage = if (errorBody.isNullOrBlank()) {
                                    "Błąd usuwania: ${res.code()}"
                                } else {
                                    try {
                                        val errorJson = Gson().fromJson(errorBody, Map::class.java)
                                        errorJson["message"] as? String ?: errorBody
                                    } catch (e: Exception) {
                                        errorBody
                                    }
                                }
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Błąd połączenia: ${e.localizedMessage ?: "Sprawdź połączenie internetowe"}", Toast.LENGTH_LONG).show()
                        }
                        showDeleteDialog = false
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Usuń trwale") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Anuluj") } }
        )
    }
}

@Composable
fun CategoryInfoSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFE94057)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666)
                )
            }
            content()
        }
    }
}

@Composable
fun CategoryInfoRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlighted) Color(0xFFE94057) else Color.Black,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun RadioButtonRow(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text, fontSize = 14.sp)
    }
}

@Composable
fun CategoryStatColumn(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, Modifier.size(24.dp), color)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 11.sp, color = Color.Gray)
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