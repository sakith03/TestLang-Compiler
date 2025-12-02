import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.net.http.*;
import java.net.*;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GeneratedTests {
  static String BASE = "http://localhost:8080";
  static Map<String,String> DEFAULT_HEADERS = new HashMap<>();
  static HttpClient client;

  @BeforeAll
  static void setup() {
    client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    DEFAULT_HEADERS.put("X-App","TestLangDemo");
    DEFAULT_HEADERS.put("Content-Type","application/json");
  }

  @Description("This explains a lot")
  @Test
  void test_Login() throws Exception {
    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + "/api/login"))
      .timeout(Duration.ofSeconds(10))
      .POST(HttpRequest.BodyPublishers.ofString("{ \"username\": \"sakith\", \"password\": \"2003\" }"));
    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());
    HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    assertEquals(200, resp.statusCode());
    assertTrue(resp.headers().firstValue("Content-Type").orElse("").contains("json"));
    assertTrue(resp.body().replace(" ", "").contains("\"token\":"));
  }

  @Disabled("Test marked as inactive")
  @Test
  void test_GetUser() throws Exception {
    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + "/api/users/"))
      .timeout(Duration.ofSeconds(10))
      .GET();
    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());
    HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    assertEquals(200, resp.statusCode());
    assertTrue(resp.body().replace(" ", "").contains("\"id\":3"));
  }

  @Disabled("Test marked as inactive")
  @Test
  void test_CreateUser() throws Exception {
    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + "/api/users"))
      .timeout(Duration.ofSeconds(10))
      .POST(HttpRequest.BodyPublishers.ofString("{ \"name\": \"bhagya\", \"email\": \"bhagya@gmail.com\", \"role\": \"USER\" }"));
    b.header("Content-Type", "application/json");
    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());
    HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    assertEquals(201, resp.statusCode());
    assertTrue(resp.headers().firstValue("Content-Type").orElse("").contains("json"));
    assertTrue(resp.body().replace(" ", "").contains("\"name\":\"bhagya\""));
    assertTrue(resp.body().replace(" ", "").contains("\"role\":\"USER\""));
  }

}
