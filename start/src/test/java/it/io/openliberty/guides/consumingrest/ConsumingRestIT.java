package it.io.openliberty.guides.consumingrest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.openliberty.guides.consumingrest.model.Artist;

public class ConsumingRestIT {

    private static String port;
    private static String baseUrl;
    private static String targetUrl;

    private Client client;
    private Response response;

    @BeforeAll
    public static void oneTimeSetup() {
      port = System.getProperty("http.port");
      baseUrl = "http://localhost:" + port + "/artists/";
      targetUrl = baseUrl + "total/";
    }

    @BeforeEach
    public void setup() {
      client = ClientBuilder.newClient();
    }

    @AfterEach
    public void teardown() {
      client.close();
    }

    @Test
    public void testArtistDeserialization() {
      response = client.target(baseUrl + "jsonString").request().get();
      this.assertResponse(baseUrl + "jsonString", response);

      Jsonb jsonb = JsonbBuilder.create();

      String expectedString = "{\"name\":\"foo\",\"albums\":"
        + "[{\"title\":\"album_one\",\"artist\":\"foo\",\"ntracks\":12}]}";
      Artist expected = jsonb.fromJson(expectedString, Artist.class);

      String actualString = response.readEntity(String.class);
      Artist[] actual = jsonb.fromJson(actualString, Artist[].class);

      assertEquals(expected.name, actual[0].name,
        "Expected names of artists does not match");

      response.close();
    }

    @Test
    public void testJsonBAlbumCount() {
      String[] artists = {"dj", "bar", "foo"};
      for (int i = 0; i < artists.length; i++) {
        response = client.target(targetUrl + artists[i]).request().get();
        this.assertResponse(targetUrl + artists[i], response);

        int expected = i;
        int actual = response.readEntity(int.class);
        assertEquals(expected, actual, "Album count for "
                      + artists[i] + " does not match");

        response.close();
      }
    }

    @Test
    public void testJsonBAlbumCountForUnknownArtist() {
      response = client.target(targetUrl + "unknown-artist").request().get();

      int expected = -1;
      int actual = response.readEntity(int.class);
      assertEquals(expected, actual, "Unknown artist must have -1 albums");

      response.close();
    }

    @Test
    public void testJsonPArtistCount() {
      response = client.target(targetUrl).request().get();
      this.assertResponse(targetUrl, response);

      int expected = 3;
      int actual = response.readEntity(int.class);
      assertEquals(expected, actual, "Expected number of artists does not match");

      response.close();
    }

    /**
     * Asserts that the given URL has the correct (200) response code.
     */
    private void assertResponse(String url, Response response) {
      assertEquals(200, response.getStatus(), "Incorrect response code from " + url);
    }
}