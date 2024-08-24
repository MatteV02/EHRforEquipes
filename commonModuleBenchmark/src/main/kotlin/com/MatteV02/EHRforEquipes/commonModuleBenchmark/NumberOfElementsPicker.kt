package com.MatteV02.EHRforEquipes.commonModuleBenchmark

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberOfElementsPicker(value: Int, onValueChange: (newNumberOfElements: Int) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier
    ) {
        OutlinedTextField(
            value = value.toString(),
            onValueChange = {  },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf(100, 1_000, 10_000, 100_000).forEach { preset ->
                DropdownMenuItem(
                    text = { Text(preset.toString()) },
                    onClick = {
                        onValueChange.invoke(preset)
                        expanded = false
                    }
                )
            }
        }
    }
}