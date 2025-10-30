package com.example.myapp009aImagetoapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var btnSelectImage: Button
    private lateinit var btnClear: Button
    private lateinit var btnUndo: Button
    private lateinit var btnColorRed: View
    private lateinit var btnColorBlue: View
    private lateinit var btnColorGreen: View
    private lateinit var btnColorBlack: View
    private lateinit var seekBarBrushSize: SeekBar
    private lateinit var tvBrushSize: TextView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchGallery()
        } else {
            Toast.makeText(this, "Oprávnění zamítnuto", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                loadImageToDrawingView(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        drawingView = findViewById(R.id.drawingView)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnClear = findViewById(R.id.btnClear)
        btnUndo = findViewById(R.id.btnUndo)
        btnColorRed = findViewById(R.id.btnColorRed)
        btnColorBlue = findViewById(R.id.btnColorBlue)
        btnColorGreen = findViewById(R.id.btnColorGreen)
        btnColorBlack = findViewById(R.id.btnColorBlack)
        seekBarBrushSize = findViewById(R.id.seekBarBrushSize)
        tvBrushSize = findViewById(R.id.tvBrushSize)
    }

    private fun setupListeners() {
        btnSelectImage.setOnClickListener {
            openGallery()
        }

        btnClear.setOnClickListener {
            drawingView.clearDrawing()
            Toast.makeText(this, "Kreslení smazáno", Toast.LENGTH_SHORT).show()
        }

        btnUndo.setOnClickListener {
            drawingView.undo()
        }

        btnColorRed.setOnClickListener {
            drawingView.setBrushColor(Color.RED)
        }

        btnColorBlue.setOnClickListener {
            drawingView.setBrushColor(Color.BLUE)
        }

        btnColorGreen.setOnClickListener {
            drawingView.setBrushColor(Color.GREEN)
        }

        btnColorBlack.setOnClickListener {
            drawingView.setBrushColor(Color.BLACK)
        }

        seekBarBrushSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val size = progress.toFloat()
                drawingView.setBrushSize(size)
                tvBrushSize.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun openGallery() {
        when {
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU -> {
                if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    launchGallery()
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            else -> {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    launchGallery()
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun loadImageToDrawingView(uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            drawingView.setBackgroundImage(bitmap)
            Toast.makeText(this, "Obrázek načten! Můžete kreslit.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Chyba při načítání obrázku", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}