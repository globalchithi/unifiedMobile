package com.vaxcare.unifiedhub.core.network.interceptor

import okhttp3.Dns
import okhttp3.Dns.Companion.SYSTEM
import java.net.Inet4Address
import java.net.InetAddress
import javax.inject.Inject

class DnsSelector @Inject constructor() : Dns {
    /**
     * DNS Lookup class that prioritizes IPv4 addresses first
     *
     * @param hostname
     * @return
     */
    override fun lookup(hostname: String): List<InetAddress> =
        SYSTEM
            .lookup(hostname)
            .sortedBy { !Inet4Address::class.java.isInstance(it) }
}
