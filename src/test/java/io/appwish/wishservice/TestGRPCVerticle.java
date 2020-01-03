package io.appwish.wishservice;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class TestGRPCVerticle {
  @BeforeEach
  void deploy_verticle(final Vertx vertx, final VertxTestContext testContext) {
    vertx.deployVerticle(new GRPCVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(final Vertx vertx, final VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }
}
