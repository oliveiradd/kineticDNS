package com.github.oliveiradd.kineticDNS

import java.io.IOException

// work with files and paths
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

//check if file is writable
// import java.nio.file.Filesystems
import java.util.logging.Logger

val projectName = "kineticDNS"
val logger = LogManager(projectName,true)
val ipMonitor = LogManager("ipMonitor",true)

fun main(args: Array<String>) {
    // Check configuration file
    val configFolderNames: Array<String> = arrayOf("/usr/local/etc","/etc",".")
    val configFileName: String = projectName + ".conf"

    val configFolderPaths = Array<Path?>(configFolderNames.size) { null }
    for (i in configFolderNames.indices) {
        configFolderPaths[i] = Paths.get(configFolderNames[i])
    }

    var configFolderName: String = ""
    for (i in configFolderNames.indices) {
        if (Files.exists(configFolderPaths[i]) && Files.isDirectory(configFolderPaths[i])) {
            configFolderName = configFolderNames[i]
            break
        }
    }

    val configFile: String = configFolderName + "/" + configFileName
    val configFilePath: Path = Paths.get(configFile)
    
    val configHandler: ConfigHandler = ConfigHandler(configFile)
    
    var operation: String = "check-update"

    for (i in args.indices) {
        when (args[i]) {
            "-f","--force" -> operation = "force-up"
            "-c","--configure" -> operation = "configure"
        }
    }
    
    if (!Files.exists(configFilePath) || operation.equals("configure")) {
        if (!Files.exists(configFilePath)) System.err.println("Configuration file not found in "+configFile)
        if (!hasWritePrivileges(configFolderName)){
            System.err.println("No write privileges to "+configFolderName)
            System.err.println("Try running the program as root to write the configuration file.")
            System.exit(0)
        } else {
            System.out.println("Configuring "+projectName+"...")
            configHandler.createConfig()
            return
        }
    }
    configHandler.readConfig()
    val DDNS_provider: String = configHandler.getProperty("DDNS_provider")
    val IPLookup_service: String = configHandler.getProperty("IPLookup_service")
    //String currentIP = KineticDNS.getCurrentIP()
    
    if (DDNS_provider.equals(ConfigHandler.DDNSProviders[0])) {
        val username: String = configHandler.getProperty(ConfigHandler.NoIPConfigurationParameters[0])
        val password: String = configHandler.getProperty(ConfigHandler.NoIPConfigurationParameters[1])
        val hostname: String = configHandler.getProperty(ConfigHandler.NoIPConfigurationParameters[2])
        val domain: String = configHandler.getProperty(ConfigHandler.NoIPConfigurationParameters[3])
        //procedes to update DDNS
        //System.out.println(username+" "+password+" "+hostname+" "+domain)
        try {
            val currentIP: String = KineticDNS.getCurrentIP(IPLookup_service)
            if (!(ipMonitor.getLastLine().contains(currentIP)) || operation.equals("force-update")) {
                val response: String = KineticDNS.NoIpUpdate(username, password, hostname, domain)
                if (KineticDNS.isResponseAppropriate(response)) ipMonitor.info("Retrieved IP: "+currentIP)
            }
        } catch (e: IOException) {
            logger.warning("Failed to get current IP address.")
        }
    }

    if (DDNS_provider.equals(ConfigHandler.DDNSProviders[1])) {
        val username: String = configHandler.getProperty(ConfigHandler.DynDNSConfigurationParameters[0])
        val password: String = configHandler.getProperty(ConfigHandler.DynDNSConfigurationParameters[1])
        val hostname: String = configHandler.getProperty(ConfigHandler.DynDNSConfigurationParameters[2])
        val domain: String = configHandler.getProperty(ConfigHandler.DynDNSConfigurationParameters[3])
        //procedes to update DDNS
        //System.out.println(username+" "+password+" "+hostname+" "+domain)
        try {
            val currentIP: String = KineticDNS.getCurrentIP(IPLookup_service)
            if (!(ipMonitor.getLastLine().contains(currentIP)) || operation.equals("--force")) {
                val response: String = KineticDNS.DynDNSUpdate(username, password, hostname, domain,currentIP)
                if (KineticDNS.isResponseAppropriate(response)) ipMonitor.info("Retrieved IP: "+currentIP)
            }
        } catch (e: IOException) {
            logger.warning("Failed to get current IP address.")
        }
    }

    if (DDNS_provider.equals(ConfigHandler.DDNSProviders[2])) {
        val token: String = configHandler.getProperty(ConfigHandler.DuckDNSConfigurationParameters[0])
        val hostname: String = configHandler.getProperty(ConfigHandler.DuckDNSConfigurationParameters[1])
        try {
            val currentIP: String = KineticDNS.getCurrentIP(IPLookup_service)
            if (!(ipMonitor.getLastLine().contains(currentIP)) || operation.equals("--force")) {
                val response: String = KineticDNS.DuckDNSUpdate(hostname, token)
                if (KineticDNS.isResponseAppropriate(response)) ipMonitor.info("Retrieved IP: "+currentIP)
            }
        } catch (e: IOException) {
            logger.warning("Failed to get current IP address.")
        }
    }        
}

fun hasWritePrivileges(configFolder: String): Boolean {
    
    val path = Paths.get(configFolder)

    if (Files.isWritable(path)) {
        return true
    } else {
        return false
    }
}
