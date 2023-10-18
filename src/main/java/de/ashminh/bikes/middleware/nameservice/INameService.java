package de.ashminh.bikes.middleware.nameservice;

public interface INameService {
	String lookup(String key);

	String register(String key, String value);
}
