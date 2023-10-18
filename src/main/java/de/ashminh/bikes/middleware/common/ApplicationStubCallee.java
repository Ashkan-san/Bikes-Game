package de.ashminh.bikes.middleware.common;

import java.util.List;

public interface ApplicationStubCallee {
	Object call(String interfaceName, String method, List<Object> args);
}
