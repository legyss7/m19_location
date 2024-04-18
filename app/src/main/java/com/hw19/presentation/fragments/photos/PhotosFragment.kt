package com.hw19.presentation.fragments.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hw19.databinding.FragmentPhotosBinding
import com.hw19.presentation.adapter.ListPhotoAdapter
import com.hw19.presentation.fragments.photos.viewModel.PhotosViewModel
import com.hw19.presentation.fragments.photos.viewModel.PhotosViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PhotosFragment : Fragment() {

    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var mainViewModelFactory: PhotosViewModelFactory
    private val viewModel: PhotosViewModel by viewModels { mainViewModelFactory }

    private val photoAdapter: ListPhotoAdapter by lazy { ListPhotoAdapter(emptyList()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotosBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonTakePhoto.setOnClickListener {
            takePhoto()
        }

        binding.buttonDeletePhotos.setOnClickListener {
            viewModel.deleteAllPhotos()
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = photoAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allPhotos.collect { photos ->
                photoAdapter.photos = photos
                if (photos.isNotEmpty()) {
                    val newPosition = photos.size - 1
                    photoAdapter.notifyItemInserted(newPosition)
                } else {
                    photoAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun takePhoto() {
        val action = PhotosFragmentDirections
            .actionNavigationPhotosToMakingPhotosFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}