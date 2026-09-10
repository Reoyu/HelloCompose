package com.example.ch04starter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ch04starter.ui.theme.Ch04StarterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ch04StarterTheme { MainScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val title = when (selectedTab) {
        0 -> "Kalkulator BMI"
        1 -> "Konversi Suhu"
        else -> "Form Registrasi"
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    icon     = { Icon(Icons.Default.Calculate, contentDescription = null) },
                    label    = { Text("BMI") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    icon     = { Icon(Icons.Default.Thermostat, contentDescription = null) },
                    label    = { Text("Suhu") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick  = { selectedTab = 2 },
                    icon     = { Icon(Icons.Default.HowToReg, contentDescription = null) },
                    label    = { Text("Registrasi") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> BmiScreen()
                1 -> SuhuScreen()
                else -> RegistrasiScreen()
            }
        }
    }
}
