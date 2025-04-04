package io.quarkiverse.openapi.problem.postprocessing;

import static io.quarkiverse.openapi.problem.HttpProblemMother.badRequestProblem;
import static io.quarkiverse.openapi.problem.HttpProblemMother.badRequestProblemBuilder;
import static io.quarkiverse.openapi.problem.postprocessing.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkiverse.openapi.problem.HttpProblem;
import java.net.URI;
import org.junit.jupiter.api.Test;

class ProblemDefaultsProviderTest {

    ProblemDefaultsProvider processor = new ProblemDefaultsProvider();

    @Test
    void shouldProvideDefaults() {
        ProblemContext context = simpleContext();

        HttpProblem enhancedProblem = processor.apply(badRequestProblem(), context);

        assertThat(enhancedProblem.getInstance()).hasPath(context.path);
    }

    @Test
    void shouldNotOverrideExistingValues() {
        HttpProblem originalProblem = badRequestProblemBuilder()
                .withType(URI.create("/business-error"))
                .withInstance(URI.create("/non-default-endpoint"))
                .build();

        HttpProblem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem.getInstance()).hasPath("/non-default-endpoint");
    }

    @Test
    void shouldHandleUnwiseCharactersInPath() {
        HttpProblem problemWithUnwiseCharactersInPath = processor.apply(
                badRequestProblem(),
                ProblemContext.of(new RuntimeException(), "/non|existing{path /with{unwise\\characters>#"));

        assertThat(problemWithUnwiseCharactersInPath.getInstance())
                .hasPath("/non|existing{path+/with{unwise\\characters>#");
    }

}
