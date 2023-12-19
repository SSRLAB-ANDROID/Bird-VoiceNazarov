package by.ssrlab.birdvoice.main.fragments.recognition.recognition

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import by.ssrlab.birdvoice.R
import by.ssrlab.birdvoice.client.recognition.RecognitionClient
import by.ssrlab.birdvoice.databinding.FragmentRecognition1Binding
import by.ssrlab.birdvoice.helpers.utils.DialogCommonInitiator
import by.ssrlab.birdvoice.helpers.utils.ViewObject
import by.ssrlab.birdvoice.main.fragments.BaseMainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class Recognition1Fragment: BaseMainFragment() {

    private lateinit var binding: FragmentRecognition1Binding
    override lateinit var arrayOfViews: ArrayList<ViewObject>
    private var breakableMarker = false

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private var goNext = true
    private var isAudioPicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isAudioPicked = arguments?.getBoolean("picked_audio") ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRecognition1Binding.inflate(layoutInflater)
        binding.apply {
            arrayOfViews = arrayListOf(
                ViewObject(recognitionBird),
                ViewObject(recognitionText),
                ViewObject(recognitionLoaderHolder),
                ViewObject(recognitionBottomLeftCloud, "lc1"),
                ViewObject(recognitionTopLeftCloud, "lc2"),
                ViewObject(recognitionTopRightCloud, "rc1")
            )

            recognitionLoaderIc.startAnimation(AnimationUtils.loadAnimation(activityMain, R.anim.recognition_loader_animation))
        }

        animationUtils.commonDefineObjectsVisibility(arrayOfViews)
        animationUtils.commonObjectAppear(activityMain.getApp().getContext(), arrayOfViews, true)

        activityMain.setPopBackCallback { initBackDialog() }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        mainVM.setToolbarTitle(resources.getString(R.string.recognition_service))
        activityMain.setToolbarAction(R.drawable.ic_arrow_back){
            navigationBackAction {
                initBackDialog()
            }
        }

        recognizeAudio()

        mainVM.recognition2Value.value = false
    }

    override fun onDestroy() {
        super.onDestroy()

        breakableMarker = true
        goNext = false
    }

    private fun initBackDialog() {
        goNext = false
        val dialogResources = arrayListOf(
            ContextCompat.getString(activityMain, R.string.dialog_r1_title),
            ContextCompat.getString(activityMain, R.string.dialog_r1_body),
            ContextCompat.getString(activityMain, R.string.dialog_cancel),
            ContextCompat.getString(activityMain, R.string.dialog_stop)
        )
        DialogCommonInitiator().initCommonDialog(activityMain, dialogResources, {
            it.dismiss()
            popBackAction()
        }, {
            goNext = true
            navigateAction()
        })
    }

    private fun recognizeAudio() {
        if (isAudioPicked) {
            val inputStream = mainVM.getUri()?.let { activityMain.contentResolver.openInputStream(it) }
            val file = File(activityMain.getExternalFilesDir(Environment.DIRECTORY_DCIM), "audio.mp3")
            file.outputStream().use {
                it.write(inputStream?.readBytes())
                it.close()
            }

            RecognitionClient.post(mainVM.getToken(), file, activityMain.getApp().getLocaleInt(), { list ->
                mainVM.setList(list)
                navigateAction()
            }) { string ->
                activityMain.runOnUiThread { Toast.makeText(activityMain, string, Toast.LENGTH_SHORT).show() }
                navigateAction()
            }
        } else {
            mainVM.getAudioFile()?.let { RecognitionClient.post(mainVM.getToken(), it, activityMain.getApp().getLocaleInt(), { list ->
                mainVM.setList(list)
                navigateAction()
            }) { string ->
                activityMain.runOnUiThread { Toast.makeText(activityMain, string, Toast.LENGTH_SHORT).show() }
                navigateAction()
            }}
        }
    }

    private fun navigateAction() {
        scope.launch {
            delay(1000)
            if (goNext) {
                animationUtils.commonObjectAppear(activityMain.getApp().getContext(), arrayOfViews)
                binding.recognitionLoaderHolder.visibility = View.INVISIBLE
                mainVM.navigateToWithDelay(R.id.action_recognitionFragment1_to_recognitionFragment2)
            }
        }
    }

    private fun popBackAction() {
        animationUtils.commonObjectAppear(activityMain.getApp().getContext(), arrayOfViews)
        breakableMarker = true
        binding.recognitionLoaderHolder.visibility = View.INVISIBLE
    }
}