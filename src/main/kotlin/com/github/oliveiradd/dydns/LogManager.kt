package com.github.oliveiradd.dydns

import java.io.IOException

//user for checking wether configuration files and paths exist
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Log
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

//list 
// import java.util.List

class LogManager {
        //public static String logFolderName
        lateinit var logger: Logger
        lateinit var logFileFull: String
        // 
        constructor(logName: String, appendMode: Boolean) {
            configureLogManager(logName,appendMode)
        }
        // Check Log file
        fun configureLogManager(logName: String, appendMode: Boolean) {
            val logParentFolderNames = arrayOf("/var/log","/var/lib")
            val logFileName = logName + ".log"

            val logParentFoldersPath = Array<Path?>(logParentFolderNames.size) { null }
            for (i in logParentFolderNames.indices) {
                logParentFoldersPath[i] = Paths.get(logParentFolderNames[i])
            }

            var logParentFolderName = "."
            for (i in logParentFolderNames.indices){
                if (Files.exists(logParentFoldersPath[i])){
                    logParentFolderName = logParentFolderNames[i]
                    break
                }
            }

            val logFolderName = logParentFolderName + "/" + projectName //att
            val logFolderPath = Paths.get(logFolderName)
            if (!Files.exists(logFolderPath)) {
                if (hasWritePrivileges(logParentFolderName)){
                    try{
                        Files.createDirectories(logFolderPath)
                        //logFileName = logFolderName+"/"+logFileName
                    } catch (e: IOException) {
                        System.err.println("Failed to create log folder at "+logFolderName+". Proceeding with logging to file deactivated.")
                    }
                } else {
                    System.err.println("No write privileges at "+logParentFolderName)
                    System.err.println("Try running the program as root to write the log directory structure.")
                }
            }

            val logger: Logger = Logger.getLogger(logName)
            try {
                // Create a file handler that writes log messages to a file
                this.logFileFull = logFolderName+"/"+logFileName
                val fileHandler: FileHandler = FileHandler(logFolderName+"/"+logFileName,appendMode)
                fileHandler.setFormatter(SimpleFormatter())

                // Add the file handler to the logger
                logger.addHandler(fileHandler)
            } catch (e: IOException) {
                System.err.println("Could not open log file "+logFolderName+"/"+logFileName+" for writing. Proceeding with logging to file deactivated.")
            }
            this.logger = logger
        }

        fun info(message: String) {
            logger.info(message)
        }

        fun warning(message: String) {
            logger.warning(message)
        }

        fun getLastLine(): String {
            val logFilePath: Path = Path.of(this.logFileFull)
            var readLine: String = ""
            try {
                // Read all lines from the log file
                val logLines: List<String> = Files.readAllLines(logFilePath)
                // Check if the file has at least two lines
                if (logLines.size > 0) {
                    // Access the second line (index 1)
                    readLine = logLines.get(logLines.size-1)
                    // You can parse and analyze the second line as needed
                } else {
                    println("The log file is empty. No data to read from.")
                }
            } catch (e: IOException) {
                //e.printStackTrace()
                System.err.println("Could not read log file "+this.logFileFull)
            }
            return readLine
        }
}