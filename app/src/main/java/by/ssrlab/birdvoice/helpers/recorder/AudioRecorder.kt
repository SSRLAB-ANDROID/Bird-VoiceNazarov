package by.ssrlab.birdvoice.helpers.recorder

import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import by.ssrlab.birdvoice.app.MainApp
import java.io.File
import java.io.FileOutputStream

class AudioRecorder : ViewModel(), AudioRecorderInterface {

    private var recorder: MediaRecorder? = null
    private lateinit var mainApp: MainApp

    fun setMainApp(app: MainApp) { mainApp = app }

    @Suppress("DEPRECATION")
    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(mainApp.getContext())
        } else MediaRecorder()
    }

    override fun start(outputFile: File) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setAudioSamplingRate(44100)
            setAudioChannels(1)
            setAudioEncodingBitRate(128000)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            recorder = this
        }
    }

    override fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder?.release()
        recorder = null
    }
}