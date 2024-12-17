package laiss.pokemon.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import laiss.pokemon.android.ui.theme.Subtext0

@Composable
fun ErrorComposable(error: String?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = error, fontSize = 10.sp, color = Subtext0)
        }
    }
}