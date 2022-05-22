package com.example.sea_battle.presentation.choose_game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.sea_battle.R
import com.example.sea_battle.databinding.ItemLoadingServersRecyclerAdapterBinding
import com.example.sea_battle.databinding.ItemServerServersRecyclerAdapterBinding
import com.example.sea_battle.entities.Host

class ServersRecyclerAdapter : RecyclerView.Adapter<ServersRecyclerAdapter.RecyclerViewHolder>() {

    private val hosts = mutableListOf<Host>()

    fun addItem(host: Host){
        hosts.add(host)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = if (position < hosts.size) R.layout.item_server_servers_recycler_adapter else R.layout.item_loading_servers_recycler_adapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding: ViewBinding = when(viewType){
            R.layout.item_server_servers_recycler_adapter -> {
                ItemServerServersRecyclerAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            }
            else -> {
                ItemLoadingServersRecyclerAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            }
        }
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        if (position < hosts.size){
            holder.fillServerItem(hosts[position])
        }
    }

    override fun getItemCount(): Int {
        return hosts.size + 1
    }

    class RecyclerViewHolder(private val binding: ViewBinding): RecyclerView.ViewHolder(binding.root){
        fun fillServerItem(host: Host){
            if (binding is ItemServerServersRecyclerAdapterBinding){
                binding.apply {
                    hostName.text = host.name
                    timeBound.text = host.timeBound.toString()
                }
            }
        }
    }
}