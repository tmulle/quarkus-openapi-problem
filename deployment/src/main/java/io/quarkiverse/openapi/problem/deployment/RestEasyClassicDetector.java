package io.quarkiverse.openapi.problem.deployment;

final class RestEasyClassicDetector extends ClasspathDetector {

    RestEasyClassicDetector() {
        super("io.quarkus.resteasy.common.runtime.ResteasyContextProvider");
    }

}