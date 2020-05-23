package com.cufe.searchengine.util;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import com.maxmind.geoip2.*;
import com.maxmind.geoip2.model.*;
import com.maxmind.geoip2.record.*;
import com.neovisionaries.i18n.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class GeoUtils {
	private static String codesDatabasePath = "src/main/resources/GeoLite2-City.mmdb";

	public static String ipFromURL(String url) throws Exception {
		InetAddress address = InetAddress.getByName(new URL(url).getHost());
		String ip = address.getHostAddress();
		return ip;
	}

    public static String countryAlpha2FromIP(String ip) throws Exception {
        File dbfile = new File(codesDatabasePath);
        DatabaseReader reader = new DatabaseReader.Builder(dbfile).build();
        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = reader.city(ipAddress);
        Country country = response.getCountry();
        return country.getIsoCode();
    }

    public static String countryAlpha3FromAlpha2(String countryAlpha2) throws Exception {
        CountryCode cc = CountryCode.getByCode(countryAlpha2);
        return cc.getAlpha3();
    }

    public static String getPublicIPAddr(String ipAddr) throws Exception {
        String[] ipAddrCodes = ipAddr.split("\\.");
        String publicIPAddr = ipAddr;

		if (ipAddrCodes[0].equals("127") || ipAddrCodes[0].equals("192") || ipAddrCodes[0].equals("0")) {
			// get external IP address in case of localhost
            URL whatismyip = new URL("http://checkip.amazonaws.com/");
            URLConnection connection = whatismyip.openConnection();

            connection.addRequestProperty("Protocol", "Http/1.1");
            connection.addRequestProperty("Connection", "keep-alive");
            connection.addRequestProperty("Keep-Alive", "1000");
            connection.addRequestProperty("User-Agent", "Web-Agent");
            
            BufferedReader in = 
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
            publicIPAddr = in.readLine();
		}

        return publicIPAddr;
    }
}
