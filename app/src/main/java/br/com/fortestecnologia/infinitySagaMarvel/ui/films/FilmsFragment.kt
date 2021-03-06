package br.com.fortestecnologia.infinitySagaMarvel.ui.films

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.fortestecnologia.infinitySagaMarvel.R
import br.com.fortestecnologia.infinitySagaMarvel.data.entities.FilmEntity
import br.com.fortestecnologia.infinitySagaMarvel.databinding.FilmsFragmentBinding
import br.com.fortestecnologia.infinitySagaMarvel.utils.Resource
import br.com.fortestecnologia.infinitySagaMarvel.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.films_fragment.*

@AndroidEntryPoint
class FilmsFragment : Fragment(), FilmsAdapter.FilmItemListener{


    private var binding: FilmsFragmentBinding by autoCleared()
    private val viewModel: FilmsViewModel by viewModels()
    private lateinit var adapter: FilmsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FilmsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    searchDatabase(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null){
                    searchDatabase(newText)
                }
                return true
            }
        })
    }

    fun searchDatabase(query: String) {
        val searchQuery = "%$query%"

        viewModel.searchDatabase(searchQuery).observe(this) { list ->
            list.let {
                adapter.setItems(it as ArrayList<FilmEntity>)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = FilmsAdapter(this)
        binding.filmsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.filmsRv.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.films.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    if (!it.data.isNullOrEmpty()) adapter.setItems(ArrayList(it.data))
                }
                Resource.Status.ERROR ->
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                Resource.Status.LOADING ->
                    binding.progressBar.visibility = View.VISIBLE
            }
        })
    }


    override fun onClickedFilm(filmId: Int) {
        findNavController().navigate(
            R.id.action_filmsFragment_to_filmDetailFragment,
            bundleOf("id" to filmId)
        )
    }









}