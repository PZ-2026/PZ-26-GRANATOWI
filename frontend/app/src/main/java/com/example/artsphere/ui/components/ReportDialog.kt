package com.example.artsphere.ui.components

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDialog(
    title: String,
    onDismiss: () -> Unit,
    onGenerateReport: (dateFrom: LocalDate, dateTo: LocalDate) -> suspend () -> ResponseBody?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var dateFrom by remember { mutableStateOf(LocalDate.now().minusMonths(1)) }
    var dateTo by remember { mutableStateOf(LocalDate.now()) }
    var isGenerating by remember { mutableStateOf(false) }
    var dateFromText by remember { mutableStateOf(dateFrom.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))) }
    var dateToText by remember { mutableStateOf(dateTo.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))) }

    AlertDialog(
        onDismissRequest = { if (!isGenerating) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Wybierz zakres dat raportu:", fontSize = 14.sp)

                // Data od
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(20.dp))
                    OutlinedTextField(
                        value = dateFromText,
                        onValueChange = { value ->
                            dateFromText = value
                            try {
                                val parsed = LocalDate.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                dateFrom = parsed
                            } catch (_: Exception) { }
                        },
                        label = { Text("Data od (dd.MM.rrrr)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Data do
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(20.dp))
                    OutlinedTextField(
                        value = dateToText,
                        onValueChange = { value ->
                            dateToText = value
                            try {
                                val parsed = LocalDate.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                dateTo = parsed
                            } catch (_: Exception) { }
                        },
                        label = { Text("Data do (dd.MM.rrrr)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                if (isGenerating) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text("Generowanie raportu...", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isGenerating = true
                    scope.launch {
                        try {
                            val responseBody = withContext(Dispatchers.IO) {
                                onGenerateReport(dateFrom, dateTo).invoke()
                            }
                            if (responseBody != null) {
                                savePdfToDownloads(context, responseBody, title)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Raport zapisany w folderze Downloads", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Błąd generowania raportu", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Błąd: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        } finally {
                            isGenerating = false
                            onDismiss()
                        }
                    }
                },
                enabled = !isGenerating
            ) {
                Text("Generuj PDF")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isGenerating
            ) {
                Text("Anuluj")
            }
        }
    )
}

private fun savePdfToDownloads(context: Context, body: ResponseBody, reportTitle: String) {
    val timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    val filename = "ArtSphere_${reportTitle.replace(" ", "_")}_$timestamp.pdf"

    val inputStream = body.byteStream()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            val outputStream: OutputStream? = resolver.openOutputStream(it)
            outputStream?.use { out ->
                inputStream.copyTo(out)
            }
        }
    } else {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, filename)
        FileOutputStream(file).use { out ->
            inputStream.copyTo(out)
        }
    }
    inputStream.close()
}
