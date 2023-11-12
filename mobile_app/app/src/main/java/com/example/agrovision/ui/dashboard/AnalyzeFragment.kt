package com.example.agrovision.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.agrovision.ApiServes
import com.example.agrovision.MainActivity2
import com.example.agrovision.databinding.FragmentAnalyzeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream


class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null

    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1
    val storageRef = Firebase.storage.reference;
    private lateinit var dialog: AlertDialog
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var storage: FirebaseStorage

    lateinit var fusedLocationProviderClient:FusedLocationProviderClient



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        checkCameraPermission()
        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        storage = FirebaseStorage.getInstance()


        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestStoragePermission()

        }

        binding.ivImage.setOnClickListener {


            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }
        }




        binding.ivUpload.setOnClickListener {

            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)

            checkLocationPermission()


        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(requireActivity())



    }

    private fun checkLocationPermission() {

        val task:Task<Location> = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)

            return

        }

        task.addOnSuccessListener {

            if(it!=null){

                Toast.makeText(requireActivity(),"${it.latitude} ${it.longitude}",Toast.LENGTH_LONG).show()


            }

        }

    }


    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_EXTERNAL_STORAGE_PERMISSION_CODE
        )
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic
                // ...
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // This method will help to retrieve the image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && resultCode != RESULT_CANCELED) {

//            val url = data!!.extras!!.get("data")
//            upload(Uri.fromFile(url as File?))

            val imageBitmap = data?.extras?.get("data") as Bitmap
            //binding.image.setImageBitmap(imageBitmap)
            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val storageRef1 = storage.reference.child("images").child(fileName)

            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // Upload the image to Firebase Storage
            val uploadTask = storageRef1.putBytes(data)
            setProgressDialog(requireActivity())

            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef1.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        Log.d("MainActivity", "Download URL: $downloadUrl")

                        // Use the downloadUrl with Glide to load and display the image
                        dialog.cancel()
                        val intent = Intent(requireActivity(),MainActivity2::class.java)
                        intent.putExtra("Username",downloadUrl)
                        startActivity(intent)
                        // You can save the downloadUrl or use it to display the image later
                    }
                } else {
                    // Image upload failed
                    val exception = task.exception
                    // Handle the exception
                }
            }


        }

        if (resultCode == RESULT_OK && requestCode == SELECT_IMAGE_CODE) {
            imageUri = data?.data as Uri

            upload(imageUri!!)
        }
    }


    companion object {
        // Define the pic id
        private const val SELECT_IMAGE_CODE = 100
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }


    fun upload(url: Uri) {

        val sd = getFileName(requireActivity(), url!!)

        val uploadTask = storageRef.child("file/$sd").putFile(url)

        setProgressDialog(requireActivity())
        uploadTask.addOnSuccessListener {


            startActivity(Intent(requireActivity(),MainActivity2::class.java))
            val resultUrl = it.uploadSessionUri.toString()
            val intent = Intent(requireActivity(),MainActivity2::class.java)
            intent.putExtra("Username",resultUrl)
            intent.putExtra("name",sd)
            startActivity(intent)
            dialog.cancel()
//        uploadFile = true


        }.addOnFailureListener {
            Log.e("Firebase", "Image Upload fail")
        }
    }


    @SuppressLint("SetTextI18n")
    fun setProgressDialog(context: Context) {

        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        // Creating a ProgressBar inside the layout
        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam
        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER

        val tvText = TextView(context)
        tvText.text = "File uploading ..."
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 17f
        tvText.layoutParams = llParam
        ll.addView(progressBar)
        ll.addView(tvText)

        // Setting the AlertDialog Builder view
        // as the Linear layout created above
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(ll)

        // Displaying the dialog
        dialog = builder.create()
        dialog.show()

        val window: Window? = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams

            // Disabling screen touch to avoid exiting the Dialog
            window.setFlags(

                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

            )
        }
    }




    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }

        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }

    }



}





