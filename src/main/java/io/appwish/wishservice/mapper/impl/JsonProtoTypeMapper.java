package io.appwish.wishservice.mapper.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.appwish.grpc.Wish;
import io.appwish.wishservice.mapper.ProtoTypeMapper;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class JsonProtoTypeMapper implements ProtoTypeMapper<JsonObject> {

  private static final Logger LOG = LoggerFactory.getLogger(JsonProtoTypeMapper.class);
  private static final String MODEL_ID = "id";
  private static final String DOCUMENT_ID = "_id";

  @Override
  public Wish mapFrom(final JsonObject json) {
    try {
      final Wish.Builder builder = Wish.newBuilder();
      json.put(MODEL_ID, json.getString(DOCUMENT_ID)).remove(DOCUMENT_ID);
      JsonFormat.parser().merge(json.encode(), builder);
      return builder.build();
    } catch (final InvalidProtocolBufferException e) {
      LOG.error(e);
      return null;
    }
  }
}
