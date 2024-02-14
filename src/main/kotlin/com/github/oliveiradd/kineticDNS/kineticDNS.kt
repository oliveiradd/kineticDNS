package com.github.oliveiradd.kineticDNS;

//used by NoIP updater
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// used by isResponseAppropriate
import java.util.HashSet;

class kineticDNS {
    val userAgent: String = "kineticDNS/0.9";

    @Throws(IOException::class)
    fun getCurrentIP(IPLookup_service: String): String {
        val url: URL = URL("https://"+IPLookup_service)
        BufferedReader(InputStreamReader(url.openStream(), StandardCharsets.UTF_8)).use { reader ->
            val retrievedIP = reader.readLine()?.trim()
            return retrievedIP ?: throw IOException("Failed to retrieve IP")
        }
    }

    fun NoIpUpdate(username: String,password: String,hostname: String,domain: String): String {
        val provider: String = ConfigHandler.DDNSProviders[0];
        val updateUrl: String = String.format("https://dynupdate.no-ip.com/nic/update?hostname=%s.%s",hostname,domain);
        val url: URL = URL(updateUrl);

        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("User-Agent",userAgent);
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        val credentials: String  = username + ":" + password;
        val base64Credentials: String = java.util.Base64.getEncoder().encodeToString(credentials.toByteArray(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic " + base64Credentials);

        try {
            val reader: BufferedReader = BufferedReader(InputStreamReader(connection.getInputStream()))
            val response: String = reader.readLine();
            logger.info(provider + " Update Response: " + response);
            return response;
        } catch (e: IOException) {
            logger.warning("Failed to get proper response from " + provider);
            return "IOException";
        }
    }

    fun DynDNSUpdate(username: String, password: String, hostname: String, domain: String, ipAddress: String): String {
        val provider: String = ConfigHandler.DDNSProviders[1];
        val updateUrl: String = String.format("https://members.dyndns.org/v3/update?hostname=%s.%s&myip=%s",hostname,domain,ipAddress);
        val url: URL = URL(updateUrl);

        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("User-Agent",userAgent);
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        val credentials: String  = username + ":" + password;
        val base64Credentials: String = java.util.Base64.getEncoder().encodeToString(credentials.toByteArray(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic " + base64Credentials);

        try {
            val reader: BufferedReader = BufferedReader(InputStreamReader(connection.getInputStream()))
            val response: String = reader.readLine();
            logger.info(provider + " Update Response: " + response);
            return response;
        } catch (e: IOException) {
            logger.warning("Failed to get proper response from " + provider);
            return "IOException";
        }
    }

    fun DuckDNSUpdate(subdomain: String, token: String): String {
        val provider: String = ConfigHandler.DDNSProviders[2];
        val updateUrl: String = String.format("https://www.duckdns.org/update?domains=%s&token=%s",subdomain,token);
        val url: URL = URL(updateUrl);

        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("User-Agent",userAgent);
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        try {
            val reader: BufferedReader = BufferedReader(InputStreamReader(connection.getInputStream()))
            val response: String = reader.readLine();
            logger.info(provider + " Update Response: " + response);
            return response;
        } catch (e: IOException) {
            logger.warning("Failed to get proper response from " + provider);
            return "IOException";
        }
    }

    fun isResponseAppropriate(response: String): Boolean {
        val appropriateResponses = mutableSetOf<String>()
        appropriateResponses.add("ok");
        appropriateResponses.add("good");
        appropriateResponses.add("nochg");

        if (appropriateResponses.any { element -> response.lowercase().contains(element) }) {
            return true
        }        
        return false;
    }

}