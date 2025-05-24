package com.example.onpa.features.licenses

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onpa.R

@Composable
fun LicensesScreen(navigateBack: () -> Boolean) {
    val context = LocalContext.current

    // List license file names from assets/licenses folder
    val licenseFiles = remember { loadLicenseFileNames(context) }

    var selectedLicenseText by remember { mutableStateOf<String?>(null) }
    var selectedLicenseName by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { navigateBack() },
                modifier = Modifier
                    .padding(start = 5.dp, end = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                stringResource(R.string.open_source_licenses),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 25.sp)
            )
        }
        if (licenseFiles.isEmpty()) {
            Text(
                stringResource(R.string.error_read_lincenses_files),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 25.sp)
            )
        }
        licenseFiles.forEach { fileName ->
            Text(
                text = fileName,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Load license content on click
                        selectedLicenseName = fileName
                        selectedLicenseText =
                            loadLicenseText(
                                context,
                                "licenses/$fileName"
                            )
                    }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
            )
            HorizontalDivider()
        }

        // Show license content in dialog
        if (selectedLicenseText != null && selectedLicenseName != null) {
            AlertDialog(
                onDismissRequest = {
                    selectedLicenseText = null
                    selectedLicenseName = null
                },
                title = { Text(selectedLicenseName ?: "") },
                text = {
                    Text(
                        selectedLicenseText ?: "",
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .align(Alignment.Start)
                    )

                },
                confirmButton = {
                    TextButton(onClick = {
                        selectedLicenseText = null
                        selectedLicenseName = null
                    }) {
                        Text(stringResource(R.string.close))
                    }
                }
            )
        }
    }
}

// Helper to list filenames under assets/licenses
private fun loadLicenseFileNames(context: Context): List<String> {
    return try {
        context.assets.list("licenses")?.toList() ?: emptyList()
    } catch (_: Exception) {
        listOf(context.getString(R.string.error_read_lincenses_files))
    }
}

// Helper to read license text from assets
private fun loadLicenseText(context: Context, path: String): String {
    return try {
        context.assets.open(path).bufferedReader().use { it.readText() }
    } catch (_: Exception) {
        context.getString(R.string.error_read_lincenses)
    }
}