package net.ivpn.client.v2.protocol

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

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import net.ivpn.client.IVPNApplication
import net.ivpn.client.R
import net.ivpn.client.common.SnackbarUtil
import net.ivpn.client.common.utils.ToastUtil
import net.ivpn.client.databinding.FragmentProtocolBinding
import net.ivpn.client.ui.dialog.DialogBuilder
import net.ivpn.client.ui.dialog.Dialogs
import net.ivpn.client.ui.protocol.ProtocolNavigator
import net.ivpn.client.ui.protocol.ProtocolViewModel
import net.ivpn.client.ui.protocol.dialog.WireGuardDetailsDialogListener
import net.ivpn.client.ui.protocol.port.Port
import net.ivpn.client.ui.protocol.port.PortAdapter
import net.ivpn.client.v2.MainActivity
import net.ivpn.client.vpn.Protocol
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ProtocolFragment : Fragment(), ProtocolNavigator, WireGuardDetailsDialogListener {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ProtocolFragment::class.java)
    }

    lateinit var binding: FragmentProtocolBinding

    @Inject
    lateinit var viewModel: ProtocolViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_protocol, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        IVPNApplication.getApplication().appComponent.provideActivityComponent().create().inject(this)
        initViews()
        initToolbar()
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            if (it is MainActivity) {
                it.setContentSecure(false)
            }
        }
    }

    private fun initViews() {
        viewModel.setNavigator(this)
        binding.contentLayout.viewmodel = viewModel
        val openVpnPortAdapter = PortAdapter(context, R.layout.port_item, Port.valuesFor(Protocol.OPENVPN))
        binding.contentLayout.protocolSettings.openvpnSpinner.adapter = openVpnPortAdapter
        val wgVpnPortAdapter = PortAdapter(context, R.layout.port_item, Port.valuesFor(Protocol.WIREGUARD))
        binding.contentLayout.protocolSettings.wgSpinner.adapter = wgVpnPortAdapter

        binding.contentLayout.protocolSettings.wireguardDetails.setOnClickListener {
            openWireGuardDetails()
        }
    }

    private fun initToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    fun openWireGuardDetails() {
        DialogBuilder.createWireGuardDetailsDialog(context, viewModel.wireGuardInfo, this)
    }

    override fun notifyUser(msgId: Int, actionId: Int, listener: View.OnClickListener?) {
        SnackbarUtil.show(binding.coordinator, msgId, actionId, listener)
    }

    override fun openDialogueError(dialog: Dialogs?) {
        DialogBuilder.createNotificationDialog(context, dialog)
    }

    override fun openCustomDialogueError(title: String?, message: String?) {
        DialogBuilder.createFullCustomNotificationDialog(context, title, message)
    }

    override fun reGenerateKeys() {
        LOGGER.info("Regenerating WireGuard keys")
        viewModel.reGenerateKeys()
    }

    override fun copyPublicKeyToClipboard() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        viewModel.copyWgKeyToClipboard(clipboard)
        ToastUtil.toast(R.string.protocol_wg_public_key_copied)
    }

    override fun copyIpAddressToClipboard() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        viewModel.copyWgIpToClipboard(clipboard)
        ToastUtil.toast(R.string.protocol_wg_ip_address_copied)
    }
}