package laiss.pokemon.android.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

abstract class RichViewModel : ViewModel(), KoinComponent {
    protected fun launchFailable(task: suspend () -> Unit) = viewModelScope.launch {
        try {
            task.invoke()
        } catch (exception: Exception) {
            passErrorMessageToState(exception.toString())
        } catch (notImplementedError: NotImplementedError) {
            passErrorMessageToState(notImplementedError.toString())
        }
    }

    protected abstract fun passErrorMessageToState(message: String)
}