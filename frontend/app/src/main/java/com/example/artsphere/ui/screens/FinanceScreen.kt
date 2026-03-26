package com.example.artsphere.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    role: String, // "client" or "seller"
    currentBalance: Double,
    onNavigateBack: () -> Unit,
    onBalanceChange: (Double) -> Unit
) {
    val isSeller = role == "seller"
    val themeColor = if (isSeller) Color(0xFF2E8B57) else MaterialTheme.colorScheme.primary
    val gradient = Brush.verticalGradient(
        colors = listOf(themeColor, themeColor.copy(alpha = 0.7f))
    )

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje Finanse", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themeColor)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .background(gradient, shape = MaterialTheme.shapes.medium)
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                "Dostępne środki",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${String.format("%.2f", currentBalance)} zł",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FinanceActionButton(
                        title = "Wpłać",
                        icon = Icons.Default.AddCard,
                        color = themeColor,
                        modifier = Modifier.weight(1f),
                        onClick = { showDepositDialog = true }
                    )
                    FinanceActionButton(
                        title = "Wypłać",
                        icon = Icons.Default.Payments,
                        color = themeColor,
                        modifier = Modifier.weight(1f),
                        onClick = { showWithdrawDialog = true }
                    )
                }
            }

            // History Section Header
            item {
                Text(
                    "Ostatnie operacje",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Placeholder History Items
            items(getPlaceholderHistory(isSeller)) { item ->
                TransactionItem(item)
            }
        }
    }

    // Deposit Dialog
    if (showDepositDialog) {
        FinanceActionDialog(
            title = "Wpłać środki",
            onDismiss = { showDepositDialog = false; amountText = "" },
            onConfirm = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                onBalanceChange(currentBalance + amount)
                showDepositDialog = false
                amountText = ""
            },
            amount = amountText,
            onAmountChange = { amountText = it },
            confirmButtonColor = themeColor
        )
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        FinanceActionDialog(
            title = "Wypłać środki",
            onDismiss = { showWithdrawDialog = false; amountText = "" },
            onConfirm = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                if (amount <= currentBalance) {
                    onBalanceChange(currentBalance - amount)
                }
                showWithdrawDialog = false
                amountText = ""
            },
            amount = amountText,
            onAmountChange = { amountText = it },
            confirmButtonColor = themeColor,
            isWithdraw = true,
            maxBalance = currentBalance
        )
    }
}

@Composable
fun FinanceActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = color,
            contentColor = Color.White
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TransactionItem(transaction: TransactionData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (transaction.isIncome) Color(0xFF4CAF50).copy(alpha = 0.1f) 
                        else Color(0xFFF44336).copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.isIncome) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.title, fontWeight = FontWeight.SemiBold)
                Text(transaction.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Text(
                text = "${if (transaction.isIncome) "+" else "-"}${String.format("%.2f", transaction.amount)} zł",
                fontWeight = FontWeight.Bold,
                color = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
    }
}

@Composable
fun FinanceActionDialog(
    title: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmButtonColor: Color,
    isWithdraw: Boolean = false,
    maxBalance: Double = 0.0
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                if (isWithdraw) {
                    Text("Dostępne: ${String.format("%.2f", maxBalance)} zł", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) onAmountChange(it) },
                    label = { Text("Kwota (zł)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = confirmButtonColor)
            ) {
                Text("Potwierdź")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}

data class TransactionData(
    val title: String,
    val amount: Double,
    val date: String,
    val isIncome: Boolean
)

fun getPlaceholderHistory(isSeller: Boolean): List<TransactionData> {
    val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
    return if (isSeller) {
        listOf(
            TransactionData("Sprzedaż obrazu 'Zachód'", 450.00, date, true),
            TransactionData("Wypłata środków", 200.00, date, false),
            TransactionData("Sprzedaż rzeźby 'Abstrakcja'", 1200.00, date, true),
            TransactionData("Prowizja systemowa", 15.00, date, false)
        )
    } else {
        listOf(
            TransactionData("Zakup obrazu 'Noc'", 300.00, date, false),
            TransactionData("Doładowanie portfela", 500.00, date, true),
            TransactionData("Zakup grafiki 'Las'", 120.00, date, false),
            TransactionData("Doładowanie portfela", 100.00, date, true)
        )
    }
}
