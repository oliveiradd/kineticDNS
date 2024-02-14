package com.github.oliveiradd.dydns;

//used by NoIP updater
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// used by isResponseAppropriate
import java.util.HashSet;
import java.util.Set;

public class DyDNS {
    private static String userAgent = "DyDNS/0.9";

    public static String getCurrentIP(String IPLookup_service) throws IOException {
        URL url = new URL("https://"+IPLookup_service);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String retrievedIP = reader.readLine().trim();
            //Main.ipMonitor.info("Retrieved IP: "+retrievedIP);
            return retrievedIP;
        }
    }
    
    public static String NoIpUpdate(String username, String password, String hostname, String domain) throws IOException {
        String provider = ConfigHandler.DDNSProviders[0];
        String updateUrl = String.format("https://dynupdate.no-ip.com/nic/update?hostname=%s.%s",hostname,domain);
        URL url = new URL(updateUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",userAgent);
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        String credentials = username + ":" + password;
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic " + base64Credentials);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            String response = reader.readLine();
            Main.logger.info(provider + " Update Response: " + response);
            return response;
        } catch (IOException e) {
            Main.logger.warning("Failed to get proper response from " + provider);
            return "IOException";
        }
    }

    public static String DynDNSUpdate(String username, String password, String hostname, String domain, String ipAddress) throws IOException {
        String provider = ConfigHandler.DDNSProviders[1];
        String updateUrl = String.format("https://members.dyndns.org/v3/update?hostname=%s.%s&myip=%s",hostname,domain,ipAddress);
        URL url = new URL(updateUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",userAgent);
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        String credentials = username + ":" + password;
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic " + base64Credentials);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            String response = reader.readLine();
            Main.logger.info(provider + " Update Response: " + response);
            return response;
        } catch (IOException e) {
            Main.logger.warning("Failed to get proper response from " + provider);
            return "IOException";
        }
    }

    public static String DuckDNSUpdate(String subdomain, String token) throws IOException {
        String provider = ConfigHandler.DDNSProviders[2];
        String updateUrl = String.format("https://www.duckdns.org/update?domains=%s&token=%s",subdomain,token);
        URL url = new URL(updateUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",userAgent);
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            String response = reader.readLine();
            Main.logger.info(provider + " Update Response: " + response);
            return response;
        } catch (IOException e) {
            Main.logger.warning("Failed to get proper response from " + provider);
            return "IOException";
        }
    }

    public static boolean isResponseAppropriate(String response) {
        Set<String> appropriateResponses = new HashSet<>();
        appropriateResponses.add("ok");
        appropriateResponses.add("good");
        appropriateResponses.add("nochg");

        for (String element:appropriateResponses) {
            if (response.toLowerCase().contains(element)) return true;
        }
        return false;

    }

}