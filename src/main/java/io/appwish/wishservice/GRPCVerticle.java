package io.appwish.wishservice;

import io.appwish.wishservice.mapper.impl.JsonProtoTypeMapper;
import io.appwish.wishservice.repository.WishRepository;
import io.appwish.wishservice.repository.impl.DocumentWishRepository;
import io.appwish.wishservice.service.WishService;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;

public class GRPCVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(GRPCVerticle.class);
  private static final String MONGO_HOST = "mongoHost";
  private static final String MONGO_PORT = "mongoPort";
  private static final String APP_PORT = "appPort";
  private static final String APP_HOST = "appHost";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    final ConfigRetriever retriever = ConfigRetriever.create(vertx);

    retriever.getConfig(json -> {
      final JsonObject config = json.result();
      final String mongoHost = config.getString(MONGO_HOST);
      final Integer mongoPort = config.getInteger(MONGO_PORT);
      final String appHost = config.getString(APP_HOST);
      final Integer appPort = config.getInteger(APP_PORT);
      final JsonObject mongoClientConfig = new JsonObject().put("host", mongoHost).put("port", mongoPort);

      final MongoClient client = MongoClient.createShared(vertx, mongoClientConfig);
      final WishRepository wishRepository = new DocumentWishRepository(client, new JsonProtoTypeMapper());

      final VertxServer server = VertxServerBuilder
        .forAddress(vertx, appHost, appPort)
        .addService(new WishService(wishRepository))
        .build();

      server.start(ar -> {
        if (ar.succeeded()) {
          LOG.info("WishService gRPC server started on port: " + appPort);
          startPromise.complete();
        } else {
          LOG.error("Could not start WishService gRPC server: " + ar.cause().getMessage());
          startPromise.fail(ar.cause());
        }
      });
    });
  }
}
