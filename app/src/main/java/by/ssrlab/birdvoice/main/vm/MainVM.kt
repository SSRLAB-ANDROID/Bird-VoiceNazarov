package by.ssrlab.birdvoice.main.vm

import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import by.ssrlab.birdvoice.databinding.ActivityMainBinding
import by.ssrlab.birdvoice.main.MainActivity
import kotlinx.coroutines.*

class MainVM: ViewModel() {

    //Activity elements
    var activityBinding: ActivityMainBinding? = null
    private val toolbarTitle = MutableLiveData<String>()
    fun setToolbarTitle(title: String){
        toolbarTitle.value = title
    }
    fun setToolbarTitleObserver(toolbar: androidx.appcompat.widget.Toolbar, activity: MainActivity){
        toolbarTitle.observe(activity) {
            toolbar.title = it
        }
    }

    //NavController set
    private lateinit var navController: NavController
    fun setNavController(controller: NavController){
        navController = controller
    }
    fun navigateToWithDelay(address: Int){
        scope.launch {
            delay(1500)
            navController.navigate(address)
        }
    }
    fun navigateUpWithDelay(){
        scope.launch {
            delay(1200)
            navController.popBackStack()
        }
    }

    //Scope
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    fun getScope() = scope

    //OnBackPressedCallback
    val onMapBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            navUpAnimLambda()
            navigateUpWithDelay()
        }
    }
    private var navUpAnimLambda = {}
    fun setNavUpAnimLambda(anim: () -> Unit){
        navUpAnimLambda = anim
    }
}