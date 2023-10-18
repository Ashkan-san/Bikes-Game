package de.ashminh.bikes.middleware.nameservice;

import java.util.HashMap;
import java.util.Map;

public class NameService implements INameService {
	private static NameService instance = new NameService();

	private Map<String, String> lookupTable;

	private NameService() {
		lookupTable = new HashMap<>();
	}

	public static NameService getInstance() {
		return instance;
	}

	@Override
	public String lookup(String key) {
		System.out.println(lookupTable);
		return lookupTable.get(key);
	}

	@Override
	public String register(String key, String value) {
		if (lookupTable.containsKey(key)) {
			System.out.println("Already contains key: " + key);
			System.out.println("Gonna overwrite! " + key);

//			String newKey = key + "1";
//			lookupTable.put(newKey, value);
//			return newKey;
		}
		lookupTable.put(key, value);
		return key;
	}
}
