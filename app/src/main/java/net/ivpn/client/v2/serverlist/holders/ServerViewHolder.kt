package net.ivpn.client.v2.serverlist.holders

/*
 IVPN Android app
 https://github.com/ivpn/android-app

 Created by Oleksandr Mykhailenko.
 Copyright (c) 2020 Privatus Limited.

 This file is part of the IVPN Android app.

 The IVPN Android app is free software: you can redistribute it and/or
 modify it under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any later version.

 The IVPN Android app is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details.

 You should have received a copy of the GNU General Public License
 along with the IVPN Android app. If not, see <https://www.gnu.org/licenses/>.
*/

import androidx.recyclerview.widget.RecyclerView
import net.ivpn.client.R
import net.ivpn.client.databinding.ServerItemBinding
import net.ivpn.client.rest.data.model.Server
import net.ivpn.client.ui.serverlist.AdapterListener

class ServerViewHolder(
        val binding: ServerItemBinding,
        val navigator: AdapterListener,
        val listener: HolderListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(server: Server, forbiddenServer: Server?) {
        binding.server = server
        binding.forbiddenServer = forbiddenServer
        binding.navigator = navigator
        binding.star.setImageResource(if (server.isFavourite) R.drawable.ic_star_on else R.drawable.ic_star_off)
        binding.starLayout.setOnClickListener {
            server.let {
                it.isFavourite = !it.isFavourite
                binding.star.setImageResource(if (it.isFavourite) R.drawable.ic_star_on else R.drawable.ic_star_off)
                navigator.changeFavouriteStateFor(it, it.isFavourite)
            }
        }
        binding.serverLayout.setOnClickListener {
            navigator.onServerSelected(server, forbiddenServer)
        }
        binding.executePendingBindings()
    }

}