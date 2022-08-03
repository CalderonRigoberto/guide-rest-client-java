package io.openliberty.guides.consumingrest.service;

import jakarta.json.JsonArray;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import io.openliberty.guides.consumingrest.model.Artist;
import io.openliberty.guides.consumingrest.Consumer;


@Path("artists")
public class ArtistResource {

    @Context
    UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getArtists() {
      return Reader.getArtists();
    }

    @GET
    @Path("jsonString")
    @Produces(MediaType.TEXT_PLAIN)
    public String getJsonString() {
      Jsonb jsonb = JsonbBuilder.create();
     
      Artist[] artists = Consumer.consumeWithJsonb(uriInfo.getBaseUri().toString()
                                                   + "artists");
      String result = jsonb.toJson(artists);

      return result;
    }

    @GET
    @Path("total/{artist}")
    @Produces(MediaType.TEXT_PLAIN)
    public int getTotalAlbums(@PathParam("artist") String artist) {
      Artist[] artists = Consumer.consumeWithJsonb(uriInfo.getBaseUri().toString()
        + "artists");

      for (int i = 0; i < artists.length; i++) {
        if (artists[i].name.equals(artist)) {
          return artists[i].albums.length;
        }
      }
      return -1;
    }

    @GET
    @Path("total")
    @Produces(MediaType.TEXT_PLAIN)
    public int getTotalArtists() {
      return Consumer.consumeWithJsonp(uriInfo.getBaseUri().toString()
                                       + "artists").length;
    }
}