package com.github.oliveiradd.kineticDNS

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Properties
import java.util.logging.Logger

class ConfigHandler {

    var properties: Properties
    var configFile: String = ""
    var configFolder: String
    
    companion object {
        val DDNSProviders = arrayOf("No-IP","DynDNS","DuckDNS")
        val NoIPConfigurationParameters = arrayOf("username","password","hostname","domain")
        val DynDNSConfigurationParameters = arrayOf("username","updater client key","hostname","domain")
        val DuckDNSConfigurationParameters = arrayOf("hostname","token")
        val IPLookupServices = arrayOf("ifconfig.me","checkip.amazonaws.com","api.ipify.org","ipinfo.io/ip","ident.me","icanhazip.com")
        val defaultIPLookupService = IPLookupServices[1]
    }

    constructor(configFolder: String) {
        properties = Properties()
        this.configFolder = configFolder
    }

    fun getProperty(key: String): String {
        return properties.getProperty(key)
    }
    
    fun setProperty(key: String, keyValue: String) {
        properties.setProperty(key, keyValue)
    }

    fun readConfig(configFile: String) {
        try {
            val input: FileInputStream = FileInputStream(this.configFolder+"/"+configFile)
            properties.load(input)
        } catch (e: IOException) {
            logger.warning("Failed to open configuration file for reading!")
        }
    }

    fun writeConfig() {
        try {
            val output: FileOutputStream = FileOutputStream(this.configFolder+"/"+this.configFile)
            properties.store(output, "Configuration Properties")
            logger.info("Configuration file written successfully!")
        } catch (e: IOException) {
            logger.warning("Failed to write configuration file!")
        }
    }

    fun createConfig() {
        
        var keyValue: String
        // Define DNS service provider
        println("Enter your dynamic DNS service provider:")
        for (i in DDNSProviders.indices) {
            println("(${i+1}) ${DDNSProviders[i]}")
        }
        val providerOption = readLine()!!.toInt() 
        if (providerOption < 1 || providerOption > DDNSProviders.size) {
            System.err.println("Invalid option, aborting configuration.")
            return
        } else {
            this.setProperty("DDNS_provider",DDNSProviders[providerOption-1])
        }
        var configurationParameters: Array<String>
        when (providerOption) {
            1 -> configurationParameters = NoIPConfigurationParameters
            2 -> configurationParameters = DynDNSConfigurationParameters
            3 -> configurationParameters = DuckDNSConfigurationParameters
            else -> {
                configurationParameters = NoIPConfigurationParameters
                logger.info("Default provider set to "+DDNSProviders[0])
            }
        }

        for (i in configurationParameters.indices) {
            println("Enter "+configurationParameters[i]+":")
            keyValue = readLine() ?: ""
            this.setProperty(configurationParameters[i],keyValue)
        }
        // Define public IP address retrieval service
        println("Enter your public IP address lookup service provider:")
        for (i in IPLookupServices.indices) {
            println("(${i+1}) ${IPLookupServices[i]}")
        }
        val ipLookupProviderOption: Int = readLine()!!.toInt()
        if (ipLookupProviderOption < 1 || ipLookupProviderOption > IPLookupServices.size) {
            println("Invalid option, proceeding with default service.")
            this.setProperty("IPLookup_service",defaultIPLookupService)
        } else {
            this.setProperty("IPLookup_service",IPLookupServices[ipLookupProviderOption-1])
        }
        //determine configFile
        when (providerOption) {
            1,2 -> this.configFile = this.getProperty("hostname")+"."+this.getProperty("domain") // noip & dyndns
            3 -> this.configFile = this.getProperty("hostname")+".duckdns.org"
        }

        // Write to config file
        this.writeConfig()
        println("Configuration file created at "+this.configFolder+"/"+this.configFile)
    }
}
