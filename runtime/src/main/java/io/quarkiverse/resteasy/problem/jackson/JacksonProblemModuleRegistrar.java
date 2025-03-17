package io.quarkiverse.resteasy.problem.jackson;

import jakarta.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.quarkiverse.resteasy.problem.HttpProblem;
import io.quarkus.jackson.ObjectMapperCustomizer;

@Singleton
public final class JacksonProblemModuleRegistrar implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("RFC9457 problem");
        module.addSerializer(HttpProblem.class, new JacksonProblemSerializer());
        mapper.registerModule(module);
    }

}