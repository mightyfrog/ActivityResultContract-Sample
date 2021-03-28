package org.mightyfrog.android.activityresultcontractsample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import org.mightyfrog.android.activityresultcontractsample.databinding.ActivityMainBinding
import org.mightyfrog.android.activityresultcontractsample.databinding.ListItemBinding
import java.io.File

/**
 * https://developer.android.com/reference/kotlin/androidx/activity/result/contract/ActivityResultContracts
 *
 * implementation 'androidx.activity:activity-ktx:x.x.x' doesn't seem working, got
 * java.lang.IllegalArgumentException: Can only use lower 16 bits for requestCode
 * use
 * implementation 'androidx.fragment:fragment-ktx:x.x.x'
 */
class MainActivity : AppCompatActivity() {

    private val createDocumentActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.CreateDocument()
    ) { uri ->
        log("$uri")
    }

    private val getContentActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
    ) { uri ->
        log("$uri")
    }

    private val getMultipleContentsActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
    ) { list ->
        log("$list")
    }

    private val openDocumentActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
    ) { uri ->
        log("$uri")
    }

    private val openDocumentTreeActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        log("$uri")
    }

    private val openMultipleDocumentsActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        log("$list")
    }

    private val pickContactActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.PickContact()
    ) { uri ->
        log("$uri")
    }

    /**
     * must add all the permissions <uses-permission android:name="~"/> in manifest.mf; otherwise, this call does nothing
     *
     * does nothing if the permissions are already granted or denied (choose Ask every time to reset)
     */
    private val requestMultiplePermissionsActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        log("$map")
    }


    /**
     * must add the permission <uses-permission android:name="~"/> in manifest.mf; otherwise, this call does nothing
     *
     * does nothing if the permission is already granted or denied (choose Ask every time to reset)
     */
    private val requestPermissionActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
    ) { granted ->
        log("$granted")
    }

    private val startActivityForResultActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result ->
        log("$result")
    }

    private val startIntentSenderForResultActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        log("$result")
    }

    private val takePictureActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
    ) { success ->
        log("$success")
    }

    private val takePicturePreviewActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        log("$bitmap")
    }

    private val takeVideoActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.TakeVideo()
    ) { bitmap ->
        log("$bitmap")
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.recyclerView.adapter = ContractItemAdapter(createContractItems())
    }

    private fun createDocument() {
        createDocumentActivityResultLauncher.launch("test.txt")
    }

    private fun getContent() {
        try {
            getContentActivityResultLauncher.launch("text/*")
        } catch (e: Exception) {
            log("$e")
        }
    }

    private fun getMultipleContents() {
        getMultipleContentsActivityResultLauncher.launch("image/*")
    }

    private fun openDocument() {
        openDocumentActivityResultLauncher.launch(arrayOf("text/*"))
    }

    private fun openDocumentTree() {
        openDocumentTreeActivityResultLauncher.launch(Uri.parse("/"))
    }

    private fun openMultipleDocuments() {
        openMultipleDocumentsActivityResultLauncher.launch(arrayOf("text/*"))
    }

    private fun pickContact() {
        pickContactActivityResultLauncher.launch(null)
    }

    private fun requestMultiplePermissions() {
        requestMultiplePermissionsActivityResultLauncher.launch(arrayOf("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"))
    }

    private fun requestPermission() {
        requestPermissionActivityResultLauncher.launch("android.permission.ACCESS_FINE_LOCATION")
    }

    private fun startActivityForResult() {
        startActivityForResultActivityResultLauncher.launch(Intent(Intent.ACTION_VIEW).apply {
            type = "image/*"
        })
    }

    private fun startIntentSenderForResult() {
        LocationServices.getFusedLocationProviderClient(this).lastLocation
                .addOnSuccessListener {
                    log("Success. How do I make this fail to get a ResolvableApiException?")
                }
                .addOnFailureListener {
                    log("$it")
                    if (it is ResolvableApiException) {
                        startIntentSenderForResultActivityResultLauncher.launch(IntentSenderRequest.Builder(it.resolution).build())
                    }
                }
    }

    private fun takePicture() {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.png")
        val uri = FileProvider.getUriForFile(
                this,
                "$packageName.provider",
                file)
        takePictureActivityResultLauncher.launch(uri)
    }

    private fun takePicturePreview() {
        takePicturePreviewActivityResultLauncher.launch(null)
    }

    private fun takeVideo() {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.mp4")
        val uri = FileProvider.getUriForFile(
                this,
                "$packageName.provider",
                file)
        takeVideoActivityResultLauncher.launch(uri)
    }

    private fun createContractItems(): List<ContractItem> {
        val contracts = resources.getStringArray(R.array.contracts)
        val descs = resources.getStringArray(R.array.descs)
        val list = mutableListOf<ContractItem>()
        contracts.forEachIndexed { index, name ->
            list.add(ContractItem(name, descs[index]))
        }

        return list
    }

    private fun log(msg: String) {
        android.util.Log.e(this::class.java.simpleName, msg)
    }

    private inner class ContractItemAdapter(private val list: List<ContractItem>) : RecyclerView.Adapter<ContractItemViewHolder>() {

        override fun getItemCount(): Int = list.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContractItemViewHolder {
            val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val holder = ContractItemViewHolder(binding)

            binding.root.setOnClickListener {
                when (binding.root.tag) {
                    "CreateDocument" -> createDocument()
                    "GetContent" -> getContent()
                    "GetMultipleContents" -> getMultipleContents()
                    "OpenDocument" -> openDocument()
                    "OpenDocumentTree" -> openDocumentTree()
                    "OpenMultipleDocuments" -> openMultipleDocuments()
                    "PickContact" -> pickContact()
                    "RequestMultiplePermissions" -> requestMultiplePermissions()
                    "RequestPermission" -> requestPermission()
                    "StartActivityForResult" -> startActivityForResult()
                    "StartIntentSenderForResult" -> startIntentSenderForResult()
                    "TakePicture" -> takePicture()
                    "TakePicturePreview" -> takePicturePreview()
                    "TakeVideo" -> takeVideo()
                }
            }

            return holder
        }

        override fun onBindViewHolder(holder: ContractItemViewHolder, position: Int) {
            holder.bind(list[position])
        }
    }

    private inner class ContractItemViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contract: ContractItem) {
            binding.root.tag = contract.name
            binding.contract = contract
            binding.executePendingBindings()
        }
    }

    data class ContractItem(
            val name: String,
            val desc: String
    )
}