package io.appwish.wishservice.verticle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.appwish.wishservice.TestData;
import io.appwish.wishservice.eventbus.Address;
import io.appwish.wishservice.eventbus.EventBusConfigurer;
import io.appwish.wishservice.repository.WishRepository;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class DatabaseVerticleTest {

  @Test
  void should_expose_database_service(final Vertx vertx, final VertxTestContext context) {
    // given
    final WishRepository repository = mock(WishRepository.class);
    final DatabaseVerticle verticle = new DatabaseVerticle(repository);
    final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());
    when(repository.findAll(TestData.ALL_WISH_QUERY))
      .thenReturn(Future.succeededFuture(TestData.WISHES));

    util.registerCodecs();

    // when
    vertx.deployVerticle(verticle, new DeploymentOptions(), context.succeeding());

    vertx.eventBus().request(Address.FIND_ALL_WISHES.get(), TestData.ALL_WISH_QUERY, event -> {
      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        assertEquals(TestData.WISHES, event.result().body());
        context.completeNow();
      });

    });
  }
}
