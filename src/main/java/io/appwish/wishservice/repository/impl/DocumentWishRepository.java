package io.appwish.wishservice.repository.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.appwish.grpc.Wish;
import io.appwish.wishservice.mapper.impl.JsonProtoTypeMapper;
import io.appwish.wishservice.repository.WishRepository;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class DocumentWishRepository implements WishRepository {

  public static final String COLLECTION = "wishes";

  private static final String DOCUMENT_ID = "_id";
  private static final String MODEL_ID = "id";
  private static final String SET = "$set";
  private static final int FIRST_INDEX = 0;

  private final MongoClient client;
  private final JsonProtoTypeMapper mapper;

  public DocumentWishRepository(final MongoClient client, final JsonProtoTypeMapper mapper) {
    this.client = client;
    this.mapper = mapper;
  }

  public Promise<List<Wish>> findAll() {
    final Promise<List<Wish>> promise = Promise.promise();
    final JsonObject query = new JsonObject();

    client.find(COLLECTION, query, res -> {
      if (res.succeeded()) {
        final List<Wish> wishes = res.result().stream().map(mapper::mapFrom).collect(Collectors.toList());
        promise.complete(wishes.stream().filter(Objects::nonNull).collect(Collectors.toList()));
      } else {
        promise.fail(res.cause());
      }
    });

    return promise;
  }

  public Promise<Optional<Wish>> findOne(final String id) {
    final Promise<Optional<Wish>> promise = Promise.promise();
    final JsonObject query = new JsonObject().put(DOCUMENT_ID, id);

    client.find(COLLECTION, query, res -> {
      if (res.succeeded()) {
        if (!res.result().isEmpty() && nonNull(res.result().get(FIRST_INDEX))) {
          promise.complete(Optional.of(mapper.mapFrom(res.result().get(FIRST_INDEX))));
        } else {
          promise.complete(Optional.empty());
        }
      } else {
        promise.fail(res.cause());
      }
    });

    return promise;
  }

  @Override
  public Promise<Optional<String>> addOne(final Wish wish) {
    final Promise<Optional<String>> promise = Promise.promise();

    try {
      client.insert(COLLECTION, new JsonObject(JsonFormat.printer().print(wish)), event -> {
        if (event.succeeded()) {
          if (isNull(event.result())) {
            promise.complete(Optional.empty());
          } else {
            promise.complete(Optional.of(event.result()));
          }
        } else {
          promise.fail(event.cause());
        }
      });
    } catch (final InvalidProtocolBufferException e) {
      promise.fail(e);
    }

    return promise;
  }

  @Override
  public Promise<Boolean> deleteOne(final String id) {
    final Promise<Boolean> promise = Promise.promise();
    final JsonObject query = new JsonObject().put(DOCUMENT_ID, id);

    client.removeDocument(COLLECTION, query, event -> {
      if (event.succeeded()) {
        if (isNull(event.result())) {
          promise.complete(false);
        } else {
          promise.complete(event.result().getRemovedCount() > 0);
        }
      } else {
        promise.fail(event.cause());
      }
    });

    return promise;
  }

  @Override
  public Promise<Boolean> updateOne(final Wish wish) {
    final Promise<Boolean> promise = Promise.promise();
    final JsonObject query = new JsonObject().put(DOCUMENT_ID, wish.getId());
    final JsonObject update = prepareUpdateFor(wish);

    client.updateCollection(COLLECTION, query, update, event -> {
      if (event.succeeded()) {
        if (isNull(event.result())) {
          promise.complete(false);
        } else {
          promise.complete(event.result().getDocModified() > 0);
        }
      } else {
        promise.fail(event.cause());
      }
    });

    return promise;
  }

  private JsonObject prepareUpdateFor(final Wish wish) {
    final JsonObject update = new JsonObject();
    final JsonObject fields = new JsonObject();

    wish.getAllFields().forEach((fieldDescriptor, o) -> {
      if (fieldDescriptor.getJsonName().equals(MODEL_ID)) {
        // do not include
      } else if (nonNull(o)) {
        fields.put(fieldDescriptor.getJsonName(), o);
      }
    });

    update.put(SET, fields);

    return update;
  }
}
