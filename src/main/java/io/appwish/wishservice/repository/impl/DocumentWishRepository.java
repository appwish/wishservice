package io.appwish.wishservice.repository.impl;

import io.appwish.grpc.Wish;
import io.appwish.wishservice.GRPCVerticle;
import io.appwish.wishservice.mapper.impl.JsonProtoTypeMapper;
import io.appwish.wishservice.repository.WishRepository;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class DocumentWishRepository implements WishRepository {

  private static final Logger LOG = LoggerFactory.getLogger(GRPCVerticle.class);
  private static final String COLLECTION = "wishes";

  private final MongoClient client;
  private final JsonProtoTypeMapper mapper;

  public DocumentWishRepository(final MongoClient client, final JsonProtoTypeMapper mapper) {
    this.client = client;
    this.mapper = mapper;
    initMockData();
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
    final JsonObject query = new JsonObject().put("_id", id);

    client.find(COLLECTION, query, res -> {
      if (res.succeeded()) {
        if (!res.result().isEmpty() && nonNull(res.result().get(0))) {
          promise.complete(Optional.of(mapper.mapFrom(res.result().get(0))));
        } else {
          promise.complete(Optional.empty());
        }
      } else {
        promise.fail(res.cause());
      }
    });

    return promise;
  }

  // TODO to implement
  @Override
  public Promise<Boolean> add(final Wish appWish) {
    return null;
  }

  // TODO to implement
  @Override
  public Promise<Boolean> delete(final String id) {
    return null;
  }

  // TODO remove sample data logic
  private void initMockData() {
    MOCK_DATA.forEach(appWish -> client.insert(COLLECTION, appWish, res -> {
      if (res.succeeded()) {
        LOG.info("Saved " + appWish);
      }
    }));
  }

  // TODO remove sample data logic
  private final List<JsonObject> MOCK_DATA = List.of(
    new JsonObject(Map.of("_id", "1", "title", "1st title", "description", "1st description", "coverImageUrl", "https://cdn.pixabay.com/photo/2017/07/06/14/44/help-2478193_1280.jpg")),
    new JsonObject(Map.of("_id", "2", "title", "2nd title", "description", "2nd description", "coverImageUrl", "https://cdn.pixabay.com/photo/2017/07/06/14/44/help-2478193_1280.jpg")),
    new JsonObject(Map.of("_id", "3", "title", "3rd title", "description", "3rd description", "coverImageUrl", "https://cdn.pixabay.com/photo/2017/07/06/14/44/help-2478193_1280.jpg")),
    new JsonObject(Map.of("_id", "4", "title", "4th title", "description", "4th description", "coverImageUrl", "https://cdn.pixabay.com/photo/2017/07/06/14/44/help-2478193_1280.jpg")),
    new JsonObject(Map.of("_id", "5", "title", "5th title", "description", "5th description", "coverImageUrl", "https://cdn.pixabay.com/photo/2017/07/06/14/44/help-2478193_1280.jpg")),
    new JsonObject(Map.of("_id", "6", "title", "6th title", "description", "6th description", "coverImageUrl", "https://cdn.pixabay.com/photo/2017/07/06/14/44/help-2478193_1280.jpg")),
    new JsonObject(Map.of("_id", "7", "title", "7th title", "description", "7th description", "coverImageUrl", "https://cdn.pixabay.com/photo/2017/07/06/14/44/help-2478193_1280.jpg")),
    new JsonObject(Map.of("_id", "8", "title", "8th title", "description", "8th description", "coverImageUrl", "https://cdn.pixabay.com/photo/2017/07/06/14/44/help-2478193_1280.jpg")),
    new JsonObject(Map.of("_id", "9", "title", "9th title", "description", "9th description", "coverImageUrl", "https://cdn.pixabay.com/photo/2017/07/06/14/44/help-2478193_1280.jpg")),
    new JsonObject(Map.of("_id", "10", "title", "10th title", "description", "10th description", "coverImageUrl", "https://cdn.pixabay.com/photo/2017/07/06/14/44/help-2478193_1280.jpg"))
  );
}
