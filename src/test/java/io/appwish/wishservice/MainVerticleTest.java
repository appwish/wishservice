package io.appwish.wishservice;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class MainVerticleTest {

  @Test
  void should_succesfuly_deploy_verticles(final Vertx vertx, final VertxTestContext testContext) {
    vertx.deployVerticle(
      new MainVerticle(),
      testContext.succeeding(id -> testContext.completeNow()));
  }
}
