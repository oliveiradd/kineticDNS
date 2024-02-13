package com.github.oliveiradd.dydns

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Properties

// getting input to write configuration file
import java.util.Scanner

public class ConfigHandler {

    private Properties properties;
    private String filePath;
    public static String[] DDNSProviders = {"No-IP","DynDNS","DuckDNS"};
    public static String[] NoIPConfigurationParameters = {"username","password","hostname","domain"};
    public static String[] DynDNSConfigurationParameters = {"username","updater client key","hostname","domain"};
    public static String[] DuckDNSConfigurationParameters = {"hostname","token"};
    public static String[] IPLookupServices = {"ifconfig.me","checkip.amazonaws.com","api.ipify.org","ipinfo.io/ip","ident.me","icanhazip.com"};
    public static String defaultIPLookupService = IPLookupServices[1];

    public ConfigHandler(String filePath) {
        properties = new Properties();
        this.filePath = filePath;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public void readConfig() {
        try (FileInputStream input = new FileInputStream(this.filePath)) {
            properties.load(input);
        } catch (IOException e) {
            Main.logger.warning("Failed to open configuration file for reading!");
        }
    }

    public void writeConfig() {
        try (FileOutputStream output = new FileOutputStream(this.filePath)) {
            properties.store(output, "Configuration Properties");
            Main.logger.info("Configuration file written successfully!");
        } catch (IOException e) {
            Main.logger.warning("Failed to write configuration file!");
        }
    }

    public void createConfig() {
        
        Scanner scanner = new Scanner(System.in);
        String value;
        // Define DNS service provider
        System.out.println("Enter your dynamic DNS service provider:");
        for (int i=0; i<DDNSProviders.length; i++) {
            System.out.println((i+1)+") "+DDNSProviders[i]);
        }
        int providerOption = scanner.nextInt();
        scanner.nextLine(); // needed to avoid newLine char to get read int nextLine() below
        if (providerOption < 1 || providerOption > DDNSProviders.length) {
            System.out.println("Invalid option, aborting configuration.");
            return;
        } else {
            this.setProperty("DDNS_provider",DDNSProviders[providerOption-1]);
        }
        String[] configurationParameters = {};
        switch (providerOption) {
            case 1:
                configurationParameters = NoIPConfigurationParameters;
                break;
            case 2:
                configurationParameters = DynDNSConfigurationParameters;
                break;
            case 3:
                configurationParameters = DuckDNSConfigurationParameters;
                break;
            default:
                configurationParameters = NoIPConfigurationParameters;
                Main.logger.info("Default provider set to "+DDNSProviders[0]);
        }
        for (int i=0; i<configurationParameters.length; i++){
            System.out.println("Enter "+configurationParameters[i]+":");
            value = scanner.nextLine();
            this.setProperty(configurationParameters[i],value);
        }
        // Define public IP address retrieval service
        System.out.println("Enter your public IP address lookup service provider:");
        for (int i=0; i<IPLookupServices.length; i++) {
            System.out.println((i+1)+") "+IPLookupServices[i]);
        }
        int ipLookupProviderOption = scanner.nextInt();
        if (ipLookupProviderOption < 1 || ipLookupProviderOption > IPLookupServices.length) {
            System.out.println("Invalid option, proceeding with default service.");
            this.setProperty("IPLookup_service",defaultIPLookupService);
        } else {
            this.setProperty("IPLookup_service",IPLookupServices[ipLookupProviderOption-1]);
        }
        // Write to config file
        this.writeConfig();
        System.out.println("Configuration file created at "+filePath);
    }
}