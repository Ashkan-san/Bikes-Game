package de.ashminh.bikes.middleware.startup;

import de.ashminh.bikes.middleware.common.ApplicationStubCallee;
import de.ashminh.bikes.middleware.nameservice.INameService;
import de.ashminh.bikes.middleware.nameservice.NameServiceCallee;
import de.ashminh.bikes.middleware.stubs.ServerStub;

import java.util.Map;

public class A_NameserviceProvider {
    public static void main(String[] args) {
        ServerStub s = new ServerStub(1234, null, Map.of(), Map.of(), true, true);

        ApplicationStubCallee callee = new NameServiceCallee();

        s.register(INameService.class.getName(), callee);
    }
}
