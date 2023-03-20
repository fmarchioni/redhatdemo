package com.sample;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class MyBean {

    public void callDrools() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("com.sample", "basic-kjar", "1.0.0");
        KieContainer kContainer = ks.newKieContainer(releaseId);
        KieSession kSession = kContainer.newKieSession();

        try {
            Person john = new Person("john", 25);
            kSession.insert(john);
            kSession.fireAllRules();
        } finally {
            kSession.dispose();
        }
    }
    /*
     * public void callDrools(String server, int processors, int memory, int diskspace) { KieServices kieServices =
     * KieServices.Factory.get();
     *
     * KieContainer kContainer = kieServices.getKieClasspathContainer();
     *
     * System.out.println("Creating kieBase"); KieBase kieBase = kContainer.getKieBase();
     *
     * System.out.println("There should be rules: "); for (KiePackage kp : kieBase.getKiePackages()) { for (Rule rule :
     * kp.getRules()) { System.out.println("kp " + kp + " rule " + rule.getName()); } }
     *
     * System.out.println("Creating kieSession"); KieSession session = kieBase.newKieSession();
     *
     * System.out.println("Now running data");
     *
     * Server s1 = new Server(server, processors, memory, diskspace); session.insert(s1); session.fireAllRules();
     *
     * }
     */
}
