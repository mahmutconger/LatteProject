package com.anlarsinsoftware.latteproje.View

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.anlarsinsoftware.latteproje.databinding.ActivityTaramaBinding
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.YuvImage
import android.graphics.Rect
import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import com.anlarsinsoftware.latteproje.Databases.HataliUrunDatabase
import com.anlarsinsoftware.latteproje.Databases.UrunlerDatabase
import java.io.ByteArrayOutputStream


class TaramaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaramaBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageAnalyzerExecutor: ExecutorService
    private lateinit var tfliteInterpreter: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaramaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(REQUIRED_PERMISSIONS)
        }

        tfliteInterpreter = loadModel()


    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            Toast.makeText(this, "Kamera izni gerekli", Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            // ImageAnalysis - analiz için
            imageAnalyzerExecutor = Executors.newSingleThreadExecutor()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(128,128))// modele göre boyut
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(imageAnalyzerExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            // Kamera seçici (arka kamera)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("TaramaActivity", "Kamera başlatılamadı: ", e)
            }

        }, ContextCompat.getMainExecutor(this))
    }
    private fun processImageProxy(imageProxy: ImageProxy) {
        val rawBitmap = imageProxyToBitmap(imageProxy) ?: return
// Rotate
        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
        }
        val bitmap = Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)

        if (bitmap != null) {
            val result = runModel(bitmap)
            if (result < 0) {
                runOnUiThread {
                    binding.statusText.text = "Tahmin yapılamadı"
                }
            }

            val label = if (result == 0) "Hatalı Ürün" else "Sağlam Ürün"

            runOnUiThread {
                binding.statusText.text = label

                if (label == "Hatalı Ürün") {
                    val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                    val db = HataliUrunDatabase(this)
                    db.kaydet(currentTime)
                }
                else{
                    listeleSaglamlar()
                }
            }
        }

        imageProxy.close()
    }
    fun listeleSaglamlar() {
        val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val db = UrunlerDatabase(this)
        db.kaydet(currentTime)
    }

    @OptIn(ExperimentalGetImage::class)
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null
        val nv21 = yuv420ToNv21(image)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val imageBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun yuv420ToNv21(image: Image): ByteArray {
        val yBuffer = image.planes[0].buffer // Y
        val uBuffer = image.planes[1].buffer // U
        val vBuffer = image.planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)

        val uvPos = ySize
        val pixelStride = image.planes[1].pixelStride
        val rowStride = image.planes[1].rowStride

        // UV plane dönüşümü
        if (pixelStride == 1 && rowStride == uSize) {
            // Uyuyorsa hızlı kopyala
            uBuffer.get(nv21, uvPos, uSize)
            vBuffer.get(nv21, uvPos + uSize, vSize)
        } else {
            // Değilse manuel kopyalama gerekir
            var pos = uvPos
            for (row in 0 until image.height / 2) {
                for (col in 0 until image.width / 2) {
                    nv21[pos++] = vBuffer.get(row * rowStride + col * pixelStride)
                    nv21[pos++] = uBuffer.get(row * rowStride + col * pixelStride)
                }
            }
        }

        return nv21
    }



    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        imageAnalyzerExecutor.shutdown()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
    fun backImageClick(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun runModel(bitmap: Bitmap): Int {
        return try {
            val inputBuffer = preprocessBitmap(bitmap)
            val output = Array(1) { FloatArray(2) }
            tfliteInterpreter.run(inputBuffer, output)
            Log.d("ModelOutput", "Output: ${output[0].contentToString()}")
            output[0].indices.maxByOrNull { output[0][it] } ?: -1

        } catch (e: Exception) {
            Log.e("TaramaActivity", "Model çalıştırma hatası: ${e.message}")
            -1 // Hata durumunda varsayılan değer
        }
    }






    fun loadModel(): Interpreter {
        val fileDescriptor = assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        return Interpreter(modelBuffer)
    }

    fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
        val width = 128
        val height = 128

        // Bitmap'i modelin istediği boyuta getir
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

        // Float32 buffer: 1 * 128 * 128 * 3 * 4 = 196608 byte
        val inputBuffer = ByteBuffer.allocateDirect(1 * width * height * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(width * height)
        resizedBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            inputBuffer.putFloat(r)
            inputBuffer.putFloat(g)
            inputBuffer.putFloat(b)
        }

        return inputBuffer
    }










}
