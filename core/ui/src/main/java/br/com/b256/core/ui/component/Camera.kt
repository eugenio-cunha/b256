package br.com.b256.core.ui.component

import java.util.UUID;
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.com.b256.core.designsystem.icon.B256Icons
import br.com.b256.core.designsystem.theme.B256Theme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest
import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import br.com.b256.core.ui.R
import java.io.File
import br.com.b256.core.ui.extension.checkPermission

class CameraXActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            B256Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Camera()
                }
            }
            fullscreen()
        }
    }

    /**
     * Oculta as barras de ação e status
     * */
    private fun fullscreen() {
        // Oculta a barra de ação na parte superior
        actionBar?.hide()

        // Ocultar as barras de status
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    private fun takePicture(
        context: Context,
        imageCapture: ImageCapture,
        onCaptureError: (String) -> Unit,
        onCaptureSuccess: (Bitmap) -> Unit,
    ) {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }
                    val bitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        matrix,
                        true,
                    )

                    onCaptureSuccess(bitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    onCaptureError(exception.message.orEmpty())
                }
            },
        )
    }

    @Composable
    private fun CameraPreview(
        modifier: Modifier = Modifier,
        context: Context,
        onCaptureError: (String) -> Unit,
        onCaptureSuccess: (Bitmap) -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val lifecycleOwner = LocalLifecycleOwner.current
        val imageCapture by remember {
            mutableStateOf(
                ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build(),
            )
        }

        Box(modifier = modifier) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    val previewView = PreviewView(context).apply {
                        this.scaleType = PreviewView.ScaleType.FILL_CENTER
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                    }

                    // CameraX Preview UseCase
                    val previewUseCase = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    coroutineScope.launch {
                        val cameraProvider: ProcessCameraProvider =
                            withContext(Dispatchers.IO) {
                                cameraProviderFuture.get()
                            }

                        try {
                            // Deve desvincular o useCase antes de vinculá-los novamente.
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner, cameraSelector, previewUseCase, imageCapture,
                            )
                        } catch (e: Exception) {
                            onCaptureError(e.message.orEmpty())
                        }
                    }

                    previewView
                },
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                FloatingActionButton(
                    shape = CircleShape,
                    onClick = {
                        takePicture(
                            context = context,
                            imageCapture = imageCapture,
                            onCaptureError = onCaptureError,
                            onCaptureSuccess = onCaptureSuccess,
                        )
                    },
                    content = {},
                )
            }
        }
    }

    @Composable
    private fun PicturePreview(
        modifier: Modifier = Modifier,
        bitmap: Bitmap,
        onDismiss: () -> Unit,
        onAccept: (Bitmap) -> Unit,
    ) {
        Box {
            Image(
                modifier = modifier
                    .fillMaxSize(),
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                IconButton(
                    onClick = onDismiss,
                ) {
                    Icon(
                        imageVector = B256Icons.Dismiss,
                        contentDescription = null,
                    )
                }

                IconButton(
                    onClick = { onAccept(bitmap) },
                ) {
                    Icon(
                        imageVector = B256Icons.Accept,
                        contentDescription = null,
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun Camera(
        modifier: Modifier = Modifier,
        context: Context = LocalContext.current,
//        onCaptureSuccess: (Bitmap) -> Unit,
//        onCaptureError: (String) -> Unit,
    ) {
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

        Box(
            modifier = modifier.fillMaxSize(),
        ) {
            if (bitmap == null) {
                CameraPreview(
                    context = context,
                    onCaptureError = {},
                    onCaptureSuccess = { bitmap = it },
                )
            } else {
                PicturePreview(
                    bitmap = bitmap!!,
                    onDismiss = { bitmap = null },
                    onAccept = { },
                )
            }
        }

//        Button(
//            onClick = {
//                // Fecha a Activity ao clicar no botão
//                val data = Intent().apply {
//                    putExtra("uri", "content://br.com.b256.bootstrap.develop.debug.fileProvider/cache/Android/data/br.com.b256.bootstrap.develop.debug/cache/images/2ffa0c9d-c320-49b4-829f-9aa567842419_6387504465194808185.jpg");
//                }
//                setResult(RESULT_OK, data);
//
//                (context as? ComponentActivity)?.finish()
//            },
//        ) {
//            Text(text = "Voltar")
//
//        }
//        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
//
//        Permission(
//            permissions = listOf(Manifest.permission.CAMERA),
//            denied = {
//                LaunchedEffect(key1 = Unit) {
//                    if (!it.allPermissionsGranted) {
//                        it.launchMultiplePermissionRequest()
//                    }
//                }
//
//                PermissionRequest(
//                    permission = it,
//                    bodyText = stringResource(id = R.string.core_ui_camera_permission_body),
//                    buttonText = stringResource(id = R.string.core_ui_camera_permission_buton),
//                )
//            },
//        ) {
//            Box(
//                modifier = modifier.fillMaxSize(),
//            ) {
//                if (bitmap == null) {
//                    CameraPreview(
//                        context = context,
//                        onCaptureError = onCaptureError,
//                        onCaptureSuccess = { bitmap = it },
//                    )
//                } else {
//                    PicturePreview(
//                        bitmap = bitmap!!,
//                        onDismiss = { bitmap = null },
//                        onAccept = { onCaptureSuccess(it) },
//                    )
//                }
//            }
//        }
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, CameraXActivity::class.java)
        }
    }
}

@Composable
fun rememberCamera(
    context: Context = LocalContext.current,
    onPicture: (Uri) -> Unit,
    onPermissionDenied: () -> Unit,
): UseCameraRequestFlow {
    val intent: Intent = CameraXActivity.intent(context)
    val cameraLauncher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                result.data?.extras?.getString("uri")?.let { uri ->
                    onPicture(uri.toUri())
                }
            }
        }

    val permissionLauncher: ManagedActivityResultLauncher<String, Boolean> =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(intent)
            } else {
                onPermissionDenied()
            }
        }

    return remember {
        UseCameraRequestFlow(
            context = context,
            cameraLauncher = cameraLauncher,
            permissionLauncher = permissionLauncher,
        )
    }
}

class UseCameraRequestFlow(
    private val context: Context,
    private val cameraLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    private val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
) {
    fun launch() {
        if (context.checkPermission(Manifest.permission.CAMERA)) {
            Picture.newUri(context = context) {
                val intent = CameraXActivity.intent(context = context).apply {
                    putExtra("uri", it)
                }

                cameraLauncher.launch(intent)
            }
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

internal class Picture : FileProvider(
    R.xml.core_ui_file_paths,
) {
    companion object {
        /**
         * Cria uma nova Uri para um novo arquivo de imagem
         *
         * @param context Contexto da aplicação
         *
         * @return Retorna uma nova Uri válida para criar um arquivo
         * */
        fun newUri(context: Context): Uri {
            val directory = File(context.externalCacheDir, "images")
            directory.mkdirs()

            val file = File.createTempFile("${UUID.randomUUID()}_", ".jpg", directory)
            val authority = "${context.packageName}.fileProvider"

            return getUriForFile(
                context,
                authority,
                file,
            )
        }

        /**
         * Cria uma nova Uri para um novo arquivo de imagem
         *
         * @param context Contexto da aplicação
         * @param callback Chamada de retorno
         *
         * @return Retorna uma nova Uri válida para criar um arquivo
         * */
        fun newUri(context: Context, callback: (Uri) -> Unit) {
            val directory = File(context.externalCacheDir, "images")
            directory.mkdirs()

            val file = File.createTempFile("${UUID.randomUUID()}_", ".jpg", directory)
            val authority = "${context.packageName}.fileProvider"

            callback(
                getUriForFile(
                    context,
                    authority,
                    file,
                ),
            )
        }
    }
}

//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult(),
//    ) { result ->
//        // Aqui você pode processar o resultado caso necessário
//        onPicture()
//        println(result)
//    }
//    LaunchedEffect(Unit) {
//        launcher.launch(intent)
//    }
