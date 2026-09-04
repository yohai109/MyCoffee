package com.yohai.mycoffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yohai.mycoffee.database.AndroidDatabaseFactory
import com.yohai.mycoffee.database.initDatabase

class MainActivity : ComponentActivity() {
    companion object {
        const val EXTRA_DATABASE_NAME = "com.yohai.mycoffee.DATABASE_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initDatabase(AndroidDatabaseFactory(this, intent.getStringExtra(EXTRA_DATABASE_NAME) ?: "coffee.db"))
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
