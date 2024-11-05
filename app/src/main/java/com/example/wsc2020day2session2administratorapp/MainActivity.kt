package com.example.wsc2020day2session2administratorapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wsc2020day2session2administratorapp.api.authAdmin
import com.example.wsc2020day2session2administratorapp.api.createUser
import com.example.wsc2020day2session2administratorapp.api.login
import com.example.wsc2020day2session2administratorapp.api.sendAnnouncement
import com.example.wsc2020day2session2administratorapp.models.Announcement
import com.example.wsc2020day2session2administratorapp.models.CreateUser
import com.example.wsc2020day2session2administratorapp.models.SessionManager
import com.example.wsc2020day2session2administratorapp.models.User
import com.example.wsc2020day2session2administratorapp.ui.theme.Wsc2020Day2Session2AdministratorAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.compose.foundation.layout.*
import com.google.mlkit.vision.common.InputImage
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var sessionManager: SessionManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "Permission granted")
        } else {
            Log.i("kilo", "Permission denied")
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()




        val colorScheme = lightColorScheme(
            primary = Color(0xFF005CB9),
            onPrimary = Color.White,
            // Add other color customizations if needed
        )
        sessionManager = SessionManager(this)




        setContent {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = androidx.compose.material3.Typography(),
                content = {
                    val navController = rememberNavController()
                    requestCameraPermission()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                context = this@MainActivity,
                            )
                        }
                        composable("checkInCompetitor") {
                            CheckInCompetitorScreen(
                                navController = navController,
                                context = this@MainActivity,
                            )
                        }
                        composable("qrCodeScanner") {
                            QRCodeScanner { scannedData ->
                                // Handle the scanned data here
                                println("Scanned QR code: $scannedData")
                            }
                        }
                        composable("home") {
                            HomeScreen(navController = navController,context = this@MainActivity)
                        }
                    }


                }
            )
        }

    }
}


@Composable
fun HomeScreen(navController: NavController, context: Context) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Check-In Competitor", "Add Competitor", "Announcements")
    val sessionManager = SessionManager(context)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row (
            modifier = Modifier.background(MaterialTheme.colorScheme.primary).fillMaxWidth().height(90.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ){
            Text(text = "Administrator App", style = MaterialTheme.typography.headlineMedium, color = Color.White, modifier = Modifier.padding(start = 16.dp))
            Button(
                onClick = {
                    sessionManager.clearSession()
                    navController.navigate("login")
                }
            ) {
                Text(text = "Logout", color = Color.White)
            }
        }


        when (selectedTabIndex) {
            0 -> CheckInCompetitorScreen(navController = navController, context = LocalContext.current)
            1 -> AddCompetitorScreen(navController = navController, context = LocalContext.current)
            2 -> AnnouncementsScreen(navController = navController, context = LocalContext.current)
        }
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
    }


}
@Composable
fun AddCompetitorScreen(navController: NavController, context: Context) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var alert by remember { mutableStateOf(false) }
    var postUser by remember { mutableStateOf(false) }
    
    if (alert)
    {
        AlertDialog(
            onDismissRequest = { alert = false },
            title = { Text("Success") },
            text = { Text("User $fullName created successfully") },
            confirmButton = {
                Button(
                    onClick = {
                        alert = false
                        fullName = ""
                        email = ""
                        password = ""
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "Create Competitor", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.width(280.dp),
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val authAdmin = authAdmin()
                    authAdmin.postFunction(context,
                        onSuccess = {

                           postUser = true
                                    },
                        onFailure = {
                            Toast.makeText(context, "Authentication Failed, Please Login Again", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        })

                    if (postUser)
                    {
                        val newUser = CreateUser(fullName ,email, password)
                        val loginService = createUser()
                        loginService.postFunction(newUser, context,
                            onSuccess = {
                                alert = true
                            },
                            onFailure = {
                                Toast.makeText(context, "Creation of Competitor Failed", Toast.LENGTH_SHORT).show()
                            })
                    }


                }
            }
        ) {
            Text(text = "Create")
        }
    }
}
@Composable
fun AnnouncementsScreen(navController: NavController, context: Context) {
    var fullName by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var alert by remember { mutableStateOf(false) }
    var postUser by remember { mutableStateOf(false) }

    if (alert)
    {
        AlertDialog(
            onDismissRequest = { alert = false },
            title = { Text("Success") },
            text = { Text("Announcement $description sented successfully") },
            confirmButton = {
                Button(
                    onClick = {
                        alert = false
                        title = ""
                        description = ""
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "Send Announcement", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.width(280.dp),
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val authAdmin = authAdmin()
                    authAdmin.postFunction(context,
                        onSuccess = {

                            postUser = true
                        },
                        onFailure = {
                            Toast.makeText(
                                context,
                                "Authentication Failed, Please Login Again",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("login")
                        })

                    if (postUser) {
                        val announcement = Announcement(title ,description)
                        val sendAnnouncement = sendAnnouncement()
                        sendAnnouncement.postFunction(announcement, context,
                            onSuccess = {
                                alert = true
                            },
                            onFailure = {
                                Toast.makeText(
                                    context,
                                    "Sending of Announcement Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                    }


                }
            }
        ) {
            Text(text = "Send")
        }
    }
}
@Composable
fun CheckInCompetitorScreen(navController: NavController, context: Context) {



    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Check-In Competitor", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        // QR code scanner placeholder
        Text(text = "QR Code Scanner Placeholder")

        Button(
            onClick = {
                navController.navigate("qrCodeScanner")
            }
        ) {
            Text(text = "Scan QR Code")
        }
        QRCodeScanner { scannedData ->
            // Handle the scanned data here
            println("Scanned QR code: $scannedData")
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Code entry field
        OutlinedTextField(
            value = "",
            onValueChange = { /* Handle code entry */ },
            label = { Text("Enter Code") }
        )
    }
}

@Composable
fun LoginScreen(navController: NavController, context: Context) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.width(280.dp),
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val user = User(username, password)
                    val loginService = login()
                    loginService.postFunction(user, context,
                        onSuccess = {
                            navController.navigate("home")
                        },
                        onFailure = {
                            Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                        })

                }
            }
        ) {
            Text(text = "Login")
        }
    }


}


@OptIn(ExperimentalGetImage::class)
@Composable
fun QRCodeScanner(onQRCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var cameraPermissionGranted by remember { mutableStateOf(false) }


    // Request camera permission
    LaunchedEffect(key1 = Unit) {
        val permission = Manifest.permission.CAMERA
        val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        cameraPermissionGranted = granted
    }

    // Scanning logic
    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also { analysis ->
            analysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { image ->
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
                val scanner = BarcodeScanning.getClient(options)
                val inputImage = InputImage.fromMediaImage(image.image, image.imageInfo.rotationDegrees)
                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            barcode.rawValue?.let { data ->
                                coroutineScope.launch {
                                    onQRCodeScanned(data)
                                }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.d("QRCodeScanner", "Barcode scanning failed: $e")
                    }
            }
        }

    val previewConfig = Preview.Builder().build()
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    val cameraProvider = remember(context) {
        val provider = ProcessCameraProvider.getInstance(context)
        provider.get()
    }

    if (cameraPermissionGranted) {
        cameraProvider.bindToLifecycle(
            context.applicationContext as MainActivity,
            cameraSelector,
            previewConfig,
            imageAnalysis
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Scanning for QR code...")
    }
}

