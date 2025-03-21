package io.quarkiverse.openapi.problem.jaxrs;

import static io.quarkiverse.openapi.problem.HttpProblem.APPLICATION_JSON_PROBLEM_TYPE;
import static jakarta.ws.rs.core.HttpHeaders.RETRY_AFTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.quarkiverse.openapi.problem.HttpProblem;
import jakarta.ws.rs.RedirectionException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;
import org.junit.jupiter.api.Test;

class WebApplicationExceptionMapperTest {

    static final MediaType MEDIA_TYPE_SHOULD_BE_IGNORED = MediaType.TEXT_PLAIN_TYPE;

    WebApplicationExceptionMapper mapper = new WebApplicationExceptionMapper();

    @Test
    void shouldMapAllBasicFields() {
        WebApplicationException exception = new WebApplicationException("Hello world", 400);

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_PROBLEM_TYPE);
        assertThat(response.getEntity())
                .isInstanceOf(HttpProblem.class)
                .hasFieldOrPropertyWithValue("title", "Bad Request")
                .hasFieldOrPropertyWithValue("detail", "Hello world");
    }

    @Test
    void shouldPassOnHeadersButIgnoreMediaType() {
        WebApplicationException exception = new WebApplicationException(
                Response.status(429)
                        .header(RETRY_AFTER, 120)
                        .type(MEDIA_TYPE_SHOULD_BE_IGNORED)
                        .build());

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_PROBLEM_TYPE);
        assertThat(response.getHeaderString(RETRY_AFTER)).isEqualTo("120");
        assertThat(response.getEntity())
                .isInstanceOf(HttpProblem.class)
                .hasFieldOrPropertyWithValue("detail", "HTTP 429 Too Many Requests");
    }

    @Test
    void shouldMapRedirectionException() {
        WebApplicationException exception = new RedirectionException(
                Response.Status.MOVED_PERMANENTLY,
                URI.create("/new-location"));

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(301);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_PROBLEM_TYPE);
        assertThat(response.getHeaderString("Location")).endsWith("/new-location");
        assertThat(response.getEntity())
                .isInstanceOf(HttpProblem.class)
                .hasFieldOrPropertyWithValue("detail", "HTTP 301 Moved Permanently");
    }

    @Test
    void shouldHandleNullHeaders() {
        Headers<Object> nullHeaders = null;
        WebApplicationException exception = new WebApplicationException(
                new ServerResponse(new Object(), 404, nullHeaders));

        assertThatCode(() -> mapper.toResponse(exception))
                .doesNotThrowAnyException();
    }
}
