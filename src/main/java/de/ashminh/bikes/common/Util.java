package de.ashminh.bikes.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Util {
	public static String getNameServiceAddress() {

		String configFile = "bikes.properties";

		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(configFile));
			return (String) prop.getOrDefault("nameServerAddress", "172.22.0.1:1234");
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Iwas ist schief gegeangen... Buhu");
	}
}
