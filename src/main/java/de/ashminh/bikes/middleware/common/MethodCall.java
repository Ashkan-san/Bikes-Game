package de.ashminh.bikes.middleware.common;

import java.util.List;

public class MethodCall {
	public String interfaceName;
	public String method;
	public List<Object> args;

	public MethodCall(String interfaceName, String method, List<Object> args) {
		this.interfaceName = interfaceName;
		this.method = method;
		this.args = args;
	}

	@Override
	public String toString() {
		return String.format("Form{schnittstelle='%s', methode='%s', args=%s}", interfaceName, method, args);
	}
}
