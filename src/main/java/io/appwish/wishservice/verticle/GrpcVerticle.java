package io.appwish.wishservice.verticle;

import io.appwish.wishservice.service.GrpcServiceImpl;
import io.grpc.BindableService;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;

/**
 * Verticle responsible for spinning up the gRPC server.
 */
public class GrpcVerticle extends AbstractVerticle {

  private static final String USER_ID = "userId";
  public static final Context.Key<String> USER_CONTEXT = Context.key(USER_ID);

  private static final Logger LOG = LoggerFactory.getLogger(GrpcVerticle.class.getName());
  private static final String APP_PORT = "appPort";
  private static final String APP_HOST = "appHost";

  private final JsonObject config;

  public GrpcVerticle(final JsonObject config) {
    this.config = config;
  }

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    final BindableService grpcWishService = new GrpcServiceImpl(vertx.eventBus());
    final String appHost = config.getString(APP_HOST);
    final Integer appPort = config.getInteger(APP_PORT);

    final VertxServer server = VertxServerBuilder
      .forAddress(vertx, appHost, appPort)
      .intercept(userContextInterceptor())
      .addService(grpcWishService)
      .build();

    server.start(asyncResult -> {
      if (asyncResult.succeeded()) {
        LOG.info("GrpcServiceImpl gRPC server started on host=" + appHost + " and port=" + appPort);
        startPromise.complete();
      } else {
        LOG.error(
          "Could not start GrpcServiceImpl gRPC server: " + asyncResult.cause().getMessage());
        startPromise.fail(asyncResult.cause());
      }
    });
  }

  private ServerInterceptor userContextInterceptor() {
    return new ServerInterceptor() {
      @Override
      public <ReqT, RespT> Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        final String userId = headers.get(Key.of(USER_ID, Metadata.ASCII_STRING_MARSHALLER));
        final Context context = Context.current().withValue(USER_CONTEXT, userId);
        return Contexts.interceptCall(context, call, headers, next);
      }
    };
  }
}
