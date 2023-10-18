package de.ashminh.bikes.middleware.nameservice;

import de.ashminh.bikes.middleware.common.ApplicationStubCallee;

import java.util.List;

public class NameServiceCallee implements ApplicationStubCallee {

	@Override
	public Object call(String interfaceName, String method, List<Object> args) {
		if (INameService.class.getName().equals(interfaceName)) {
			switch (method) {
				case "lookup":
					String key = (String) args.get(0);
					return NameService.getInstance().lookup(key);
				case "register":
					String key1 = (String) args.get(0);
					String value = (String) args.get(1);
					return NameService.getInstance().register(key1, value);
			}
		}
		return null;
	}
}
