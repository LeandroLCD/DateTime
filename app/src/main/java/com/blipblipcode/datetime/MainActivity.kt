package com.blipblipcode.datetime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blipblipcode.datetime.ui.theme.DateTimeTheme
import com.blipblipcode.library.DateTime
import com.blipblipcode.library.model.FormatType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        DateTime.init(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DateTimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
                        Greeting(
                            name = "Android ${DateTime.now().year}"
                        )

                        Spacer(Modifier.height(8.dp))
                        Greeting(
                            name = "Date format Large ${DateTime.now().format(FormatType.Large('-'))}"
                        )
                        Spacer(Modifier.height(8.dp))
                        Greeting(
                            name = "Date format Short ${DateTime.now().format(FormatType.Short('-'))}"
                        )
                        Spacer(Modifier.height(8.dp))
                        Greeting(
                            name = "Date format custom ${DateTime.now().format("yyyy-MM-dd HH:mm:ss")}"
                        )
                        Spacer(Modifier.height(8.dp))
                        Greeting(
                            name = "Add one year ${DateTime.now().addYears(1).year}"
                        )
                        Spacer(Modifier.height(8.dp))
                        Greeting(
                            name = "Add one month ${DateTime.now().addMonths(1).month}"
                        )
                        Spacer(Modifier.height(8.dp))
                        Greeting(
                            name = "Add one day ${DateTime.now().addDays(1).day}"
                        )
                    }


                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DateTimeTheme {
        Greeting("Android")
    }
}