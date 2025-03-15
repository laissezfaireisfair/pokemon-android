package laiss.pokemon.android.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class RichViewModel : ViewModel() {
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