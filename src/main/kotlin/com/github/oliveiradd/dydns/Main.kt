package com.github.oliveiradd.dydns

// work with files and paths
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

//check if file is writable
// import java.nio.file.Filesystems;

val projectName = "dydns"
val logger = LogManager(projectName,true)
val ipMonitor = LogManager("ipMonitor",true);

fun main() {
    
    val configFolderNames = arrayOf("/usr/local/etc","/etc",".")
    val configFileName = projectName + ".conf"
    var configFolderPaths = Array<Path?>(configFolderNames.size) { null }

    println(projectName)
    if(hasWritePrivileges("/usr")) {
        println("yes we can")
    } else {
        println("no can do")
    }
}

fun hasWritePrivileges(configFolder: String): Boolean {
    
    val path = Paths.get(configFolder)

    if (Files.isWritable(path)) {
        return true;
    } else {
        return false;
    }
}