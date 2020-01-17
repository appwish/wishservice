package io.appwish.wishservice.verticle;

import io.appwish.wishservice.repository.WishRepository;
import io.appwish.wishservice.service.DatabaseService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * Verticle responsible for database access. Registers DatabaseService to expose the database on the
 * event bus.
 */
public class DatabaseVerticle extends AbstractVerticle {

  private final WishRepository wishRepository;

  public DatabaseVerticle(final WishRepository wishRepository) {
    this.wishRepository = wishRepository;
  }

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    final DatabaseService databaseService = new DatabaseService(vertx.eventBus(), wishRepository);
    databaseService.registerEventBusEventHandlers();
    startPromise.complete();
  }
}
