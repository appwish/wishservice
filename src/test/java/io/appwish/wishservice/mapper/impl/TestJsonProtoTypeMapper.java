package io.appwish.wishservice.mapper.impl;

import io.appwish.grpc.Wish;
import io.appwish.wishservice.mapper.ProtoTypeMapper;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestJsonProtoTypeMapper {

  private static final String SOME_ID = "someId";
  private static final String SOME_TITLE = "someTitle";
  private static final String SOME_DESCRIPTION = "someDescription";
  private static final String SOME_URL = "someUrl";

  @Test
  void should_map_properly() {
    // given
    final ProtoTypeMapper<JsonObject> mapper = new JsonProtoTypeMapper();

    final Wish expected = Wish.newBuilder()
      .setId(SOME_ID)
      .setTitle(SOME_TITLE)
      .setDescription(SOME_DESCRIPTION)
      .setCoverImageUrl(SOME_URL).build();

    final JsonObject json = new JsonObject()
      .put("_id", SOME_ID)
      .put("title", SOME_TITLE)
      .put("description", SOME_DESCRIPTION)
      .put("coverImageUrl", SOME_URL);

    // when
    final Wish result = mapper.mapFrom(json);

    // then
    assertEquals(expected, result);
  }
}
