package com.example.artsphere.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artsphere.api.RetrofitClient
import com.example.artsphere.api.TransactionResponse
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    userId: Long,
    role: String,
    currentBalance: Double,
    onNavigateBack: () -> Unit,
    onBalanceChange: (Double) -> Unit
) {
    val isSeller = role == "seller" || role == "ARTIST"
    val themeColor = if (isSeller) Color(0xFF2E8B57) else MaterialTheme.colorScheme.primary
    val gradient = Brush.verticalGradient(colors = listOf(themeColor, themeColor.copy(alpha = 0.7f)))

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    // Lista transakcji z API
    var history by remember { mutableStateOf<List<TransactionResponse>>(emptyList()) }
    var isLoadingHistory by remember { mutableStateOf(true) }

    val fetchTransactions: () -> Unit = {
        coroutineScope.launch {
            try {
                val response = RetrofitClient.authApi.getTransactions(userId)
                if (response.isSuccessful && response.body() != null) {
                    history = response.body()!!
                }
            } catch (e: Exception) {
                // cicha obsługa błędu
            } finally {
                isLoadingHistory = false
            }
        }
    }

    // Załaduj na start ekranu!
    LaunchedEffect(userId) {
        fetchTransactions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje Finanse", color = Color.White) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themeColor)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
                    Box(modifier = Modifier.background(gradient, shape = MaterialTheme.shapes.medium).fillMaxWidth().padding(24.dp)) {
                        Column {
                            Text("Dostępne środki", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
                            Text(text = "${String.format("%.2f", currentBalance)} zł", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FinanceActionButton(title = "Wpłać", icon = Icons.Default.AddCard, color = themeColor, modifier = Modifier.weight(1f), onClick = { showDepositDialog = true })
                    FinanceActionButton(title = "Wypłać", icon = Icons.Default.Payments, color = themeColor, modifier = Modifier.weight(1f), onClick = { showWithdrawDialog = true })
                }
            }

            item { Text("Ostatnie operacje", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }

            if (isLoadingHistory) {
                item { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            } else if (history.isEmpty()) {
                item { Text("Brak historii operacji. Wpłać coś by zacząć!", color = Color.Gray, modifier = Modifier.padding(top = 8.dp)) }
            } else {
                items(history) { transaction -> TransactionItem(transaction) }
            }
        }
    }

    // --- DIALOG WPŁATY ---
    if (showDepositDialog) {
        FinanceActionDialog(
            title = "Wpłać środki na konto",
            isProcessing = isProcessing,
            onDismiss = { showDepositDialog = false; amountText = "" },
            onConfirm = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                if (amount > 0) {
                    isProcessing = true
                    coroutineScope.launch {
                        try {
                            val response = RetrofitClient.authApi.addBalance(userId, amount)
                            if (response.isSuccessful && response.body() != null) {
                                val newBal = response.body()!!["newBalance"] ?: 0.0
                                onBalanceChange(newBal) // Uaktualnia wielki nagłówek "Dostępne środki"

                                fetchTransactions() // <--- TU JEST MAGIA! Od razu pobiera nowy wpis z bazy!

                                showDepositDialog = false
                                amountText = ""
                                Toast.makeText(context, "Doładowano portfel!", Toast.LENGTH_SHORT).show()
                            } else { Toast.makeText(context, "Błąd z serwerem.", Toast.LENGTH_SHORT).show() }
                        } catch (e: Exception) { Toast.makeText(context, "Brak połączenia.", Toast.LENGTH_SHORT).show() }
                        finally { isProcessing = false }
                    }
                }
            },
            amount = amountText, onAmountChange = { amountText = it }, confirmButtonColor = themeColor
        )
    }

    // --- DIALOG WYPŁATY ---
    if (showWithdrawDialog) {
        FinanceActionDialog(
            title = "Wypłać środki na kartę",
            isProcessing = isProcessing,
            onDismiss = { showWithdrawDialog = false; amountText = "" },
            onConfirm = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                if (amount > 0 && amount <= currentBalance) {
                    isProcessing = true
                    coroutineScope.launch {
                        try {
                            val response = RetrofitClient.authApi.deductBalance(userId, amount)
                            if (response.isSuccessful && response.body() != null) {
                                val newBal = response.body()!!["newBalance"] ?: 0.0
                                onBalanceChange(newBal)

                                fetchTransactions()

                                showWithdrawDialog = false
                                amountText = ""
                                Toast.makeText(context, "Wypłacono na Twoje konto!", Toast.LENGTH_SHORT).show()
                            } else { Toast.makeText(context, "Błąd z serwerem.", Toast.LENGTH_SHORT).show() }
                        } catch (e: Exception) { Toast.makeText(context, "Brak połączenia.", Toast.LENGTH_SHORT).show() }
                        finally { isProcessing = false }
                    }
                } else if (amount > currentBalance) {
                    Toast.makeText(context, "Brak wystarczających środków!", Toast.LENGTH_SHORT).show()
                }
            },
            amount = amountText, onAmountChange = { amountText = it }, confirmButtonColor = themeColor, isWithdraw = true, maxBalance = currentBalance
        )
    }
}

@Composable
fun FinanceActionButton(title: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(onClick = onClick, modifier = modifier.height(60.dp), colors = ButtonDefaults.elevatedButtonColors(containerColor = color, contentColor = Color.White), shape = MaterialTheme.shapes.medium) {
        Icon(icon, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text(title, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TransactionItem(transaction: TransactionResponse) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(if (transaction.income) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFFF44336).copy(alpha = 0.1f), shape = MaterialTheme.shapes.small), contentAlignment = Alignment.Center) {
                Icon(imageVector = if (transaction.income) Icons.Default.TrendingUp else Icons.Default.TrendingDown, contentDescription = null, tint = if (transaction.income) Color(0xFF4CAF50) else Color(0xFFF44336))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.title, fontWeight = FontWeight.SemiBold)
                Text(transaction.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = "${if (transaction.income) "+" else "-"}${String.format("%.2f", transaction.amount)} zł", fontWeight = FontWeight.Bold, color = if (transaction.income) Color(0xFF4CAF50) else Color(0xFFF44336))
        }
    }
}

@Composable
fun FinanceActionDialog(title: String, amount: String, onAmountChange: (String) -> Unit, onDismiss: () -> Unit, onConfirm: () -> Unit, confirmButtonColor: Color, isWithdraw: Boolean = false, maxBalance: Double = 0.0, isProcessing: Boolean) {
    AlertDialog(
        onDismissRequest = onDismiss, title = { Text(title) },
        text = {
            Column {
                if (isWithdraw) { Text("Dostępne: ${String.format("%.2f", maxBalance)} zł", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(modifier = Modifier.height(8.dp)) }
                OutlinedTextField(value = amount, onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) onAmountChange(it) }, label = { Text("Kwota (zł)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !isProcessing)
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = confirmButtonColor), enabled = !isProcessing) {
                if (isProcessing) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White) else Text("Potwierdź")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !isProcessing) { Text("Anuluj") } }
    )
}