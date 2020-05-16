package com.cufe.searchengine.util;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import com.maxmind.geoip2.*;
import com.maxmind.geoip2.model.*;
import com.maxmind.geoip2.record.*;

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

    public static String countryFromIP(String ip) throws Exception {
        File dbfile = new File(codesDatabasePath);
        DatabaseReader reader = new DatabaseReader.Builder(dbfile).build();
        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = reader.city(ipAddress);
        Country country = response.getCountry();
        return country.getIsoCode();
    }

    public static String countryCodeFromName(String countryName) throws Exception {
        return countryName;
    }
}
