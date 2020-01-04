package io.appwish.wishservice;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class TestGRPCVerticle {
  @Test
  void verticle_deployed_and_grpc_server_started(final Vertx vertx, final VertxTestContext testContext) {
    vertx.deployVerticle(new GRPCVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }
}
