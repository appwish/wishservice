package io.appwish.wishservice;

import io.appwish.wishservice.eventbus.EventBusConfigurer;
import io.appwish.wishservice.repository.WishRepository;
import io.appwish.wishservice.repository.impl.PostgresWishRepository;
import io.appwish.wishservice.repository.impl.Query;
import io.appwish.wishservice.verticle.DatabaseVerticle;
import io.appwish.wishservice.verticle.GrpcVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

/**
 * Main verticle responsible for configuration and deploying all other verticles
 */
public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  private static final String DATABASE_HOST = "dbHost";
  private static final String DATABASE_PORT = "dbPort";
  private static final String DATABASE_NAME = "dbName";
  private static final String DATABASE_USER = "dbUser";
  private static final String DATABASE_PASSWORD = "dbPassword";

  @Override
  public void start(final Promise<Void> startPromise) {
    final ConfigRetriever retriever = ConfigRetriever.create(vertx);

    retriever.getConfig(event -> {
      final JsonObject config = event.result();
      final String databaseHost = config.getString(DATABASE_HOST);
      final Integer databasePort = config.getInteger(DATABASE_PORT);
      final String databaseName = config.getString(DATABASE_NAME);
      final String databaseUser = config.getString(DATABASE_USER);
      final String databasePassword = config.getString(DATABASE_PASSWORD);

      final PgConnectOptions connectOptions = new PgConnectOptions()
        .setPort(databasePort)
        .setHost(databaseHost)
        .setDatabase(databaseName)
        .setUser(databaseUser)
        .setPassword(databasePassword);

      final PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
      final PgPool client = PgPool.pool(vertx, connectOptions, poolOptions);
      final WishRepository repository = new PostgresWishRepository(client);
      final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());

      util.registerCodecs();

      // TODO It's here just for development purposes
      client.preparedQuery(Query.CREATE_WISH_TABLE.sql(), query -> { });

      CompositeFuture.all(
        deployDatabaseVerticle(repository),
        deployGrpcVerticle())
        .setHandler(ar -> {
          if (ar.succeeded()) {
            startPromise.complete();
          } else {
            LOG.error("Could not deploy required verticles", ar.cause());
            startPromise.fail(ar.cause());
          }
        });
    });
  }

  private Future<Void> deployGrpcVerticle() {
    final Promise<Void> promise = Promise.promise();

    vertx.deployVerticle(new GrpcVerticle(), new DeploymentOptions(), res -> {
      if (res.failed()) {
        promise.fail(res.cause());
      } else {
        promise.complete();
      }
    });

    return promise.future();
  }

  private Future<Void> deployDatabaseVerticle(final WishRepository repository) {
    final Promise<Void> promise = Promise.promise();

    vertx.deployVerticle(new DatabaseVerticle(repository), new DeploymentOptions(), res -> {
      if (res.failed()) {
        promise.fail(res.cause());
      } else {
        promise.complete();
      }
    });

    return promise.future();
  }
}
