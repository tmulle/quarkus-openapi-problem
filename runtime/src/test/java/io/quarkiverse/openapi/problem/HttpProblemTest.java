package io.quarkiverse.openapi.problem;

import static io.quarkiverse.openapi.problem.HttpProblemMother.complexProblem;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class HttpProblemTest {

    @Test
    void builderShouldPassAllFields() {
        HttpProblem problem = complexProblem().build();

        assertThat(problem.getType()).hasHost("tietoevry.com").hasPath("/problem");
        assertThat(problem.getInstance()).hasPath("/endpoint");
        assertThat(problem.getStatus()).isEqualTo(400);
        assertThat(problem.getDetail()).isEqualTo("Deep down wrongness, zażółć gęślą jaźń for Håkensth");
        assertThat(problem.getHeaders())
                .containsEntry("X-Numeric-Header", 123)
                .containsEntry("X-String-Header", "ABC");
    }

    @Test
    void copyBuilderShouldCopyAllFields() {
        HttpProblem original = complexProblem().build();

        HttpProblem copy = original.toBuilder().build();

        assertThat(copy).usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    void valueOfShouldSetTitle() {
        assertThat(HttpProblem.valueOf(Response.Status.BAD_REQUEST).getTitle())
                .isEqualTo(Response.Status.BAD_REQUEST.getReasonPhrase());

        assertThat(HttpProblem.valueOf(Response.Status.BAD_REQUEST, "Some detail").getTitle())
                .isEqualTo(Response.Status.BAD_REQUEST.getReasonPhrase());
    }

}
