package com.example.ch10

import dagger.hilt.android.AndroidEntryPoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ch10.ui.theme.Ch10Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ch10Theme { KabarKiniApp() }
        }
    }
}
