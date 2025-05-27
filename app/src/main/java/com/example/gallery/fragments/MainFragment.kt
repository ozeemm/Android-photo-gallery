package com.example.gallery.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.R
import com.example.gallery.adapters.PhotoAdapter
import com.example.gallery.databinding.FragmentMainBinding
import com.example.gallery.model.Photo
import com.example.gallery.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModel()
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var gestureDetector: GestureDetector

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gestureDetector = GestureDetector(context, SwipeListener())

        // toolbar
        val activity = (requireActivity() as AppCompatActivity)
        activity.setSupportActionBar(binding.mainToolbar)
        activity.addMenuProvider(MainMenuProvider(), viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Fill recycle view
        photoAdapter = PhotoAdapter(context!!, ArrayList(emptyList())) { photo: Photo ->
            val addImageFragment = AddImageFragment()
            val bundle = Bundle()
            bundle.putString("type", "Update")
            bundle.putLong("photo.id", photo.id)
            addImageFragment.arguments = bundle

            replaceFragment(addImageFragment)
        }
        binding.recyclerView.adapter = photoAdapter

        viewModel.photos.observe(this) { list ->
            photoAdapter.updateItems(list)
        }

        // Delete photos on swipe down
        val itemTouchHelper = ItemTouchHelper(
            object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.DOWN){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val index = viewHolder.adapterPosition
                        viewModel.deletePhoto(index)
                        makeText(getText(R.string.MainActivity_PhotoDeleted))
                    }
                }
            }
        )

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        binding.root.setOnTouchListener{ view, event ->
            gestureDetector.onTouchEvent(event)
        }
    }

    inner class MainMenuProvider: MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.main_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when(menuItem.itemId){
                R.id.menuItemAdd -> {
                    val addImageFragment = AddImageFragment()
                    val bundle = Bundle()
                    bundle.putString("type", "Create")
                    addImageFragment.arguments = bundle

                    replaceFragment(addImageFragment)
                }
                R.id.menuItemExport -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val message = viewModel.exportPhotos()
                        makeText(message)
                    }
                }
                R.id.menuItemBackup -> {
                    replaceFragment(BackupsFragment())
                }
                else -> return false
            }

            return true
        }
    }

    private fun onSwipeUp(){
        val addImageFragment = AddImageFragment()
        val bundle = Bundle()
        bundle.putString("type", "Create")
        addImageFragment.arguments = bundle

        replaceFragment(addImageFragment)
    }

    inner class SwipeListener: GestureDetector.SimpleOnGestureListener(){
        private val SWIPE_THRESHOLD: Int = 50
        private val SWIPE_VELOCITY_THRESHOLD: Int = 50

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val diffY = e2.y - e1!!.y
            val diffX = e2.x - e1.x

            if(abs(diffY) > abs(diffX)){
                if(diffY < 0 && abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD)
                    onSwipeUp()
            }

            return true
        }
    }

    private fun makeText(text: CharSequence){
        activity?.runOnUiThread {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceFragment(fragment: Fragment){
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrameLayout, fragment)
            .commit()
    }

}