package io.appwish.wishservice.repository.impl;

import io.appwish.grpc.Wish;
import io.appwish.wishservice.mapper.impl.JsonProtoTypeMapper;
import io.appwish.wishservice.testutil.MongoDbContainer;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
@Testcontainers
class DocumentWishRepositoryTest {

  @Container
  private static MongoDbContainer container = new MongoDbContainer();
  private DocumentWishRepository repository;
  private MongoClient client;

  @BeforeEach
  void set_up(final Vertx vertx, final VertxTestContext testContext) {
    assertThatPortIsAvailable(container);
    client = MongoClient.createShared(vertx, new JsonObject()
      .put("host", container.getContainerIpAddress())
      .put("port", container.getPort()));
    repository = new DocumentWishRepository(client, new JsonProtoTypeMapper());
    testContext.completeNow();
  }

  @AfterEach
  void tear_down(final Vertx vertx, final VertxTestContext testContext) {
    // for convenience we just drop entire collection after each test
    // instead of recreating whole mongodb container
    client.dropCollection(DocumentWishRepository.COLLECTION, testContext.succeeding());
    client.close();
    testContext.completeNow();
  }

  @Test
  void should_return_generated_id(final Vertx vertx, final VertxTestContext testContext) {
    // given
    final Wish given = Wish.newBuilder()
      .setTitle("Test Title")
      .setDescription("Test Desc")
      .setCoverImageUrl("Test Url")
      .build();

    // when
    repository.addOne(given).future().setHandler(insert -> {
      if (insert.succeeded()) {

        // when
        final String id = insert.result().get();
        repository.findOne(id).future().setHandler(findOne -> {
          if (findOne.succeeded()) {

            // then
            testContext.verify(() -> {
              assertFalse(id.isBlank());
              assertTrue(findOne.result().isPresent());
              assertEqualsIgnoreID(given, findOne.result().get());
              testContext.completeNow();
            });

          } else {
            testContext.failNow(findOne.cause());
          }
        });
      } else {
        testContext.failNow(insert.cause());
      }
    });
  }

  @Test
  void should_delete_wish(final Vertx vertx, final VertxTestContext testContext) {
    // given
    final Wish given = Wish.newBuilder()
      .setTitle("Test Title")
      .setDescription("Test Desc")
      .setCoverImageUrl("Test Url")
      .build();

    // when
    repository.addOne(given).future().setHandler(insert -> {
      if (insert.succeeded()) {

        // when
        final String id = insert.result().get();
        repository.findOne(id).future().setHandler(findOne -> {
          if (findOne.succeeded()) {

            // when
            repository.deleteOne(id).future().setHandler(delete -> {
              if (delete.succeeded()) {

                // then
                testContext.verify(() -> {
                  assertTrue(delete.result());
                  testContext.completeNow();
                });

              } else {
                testContext.failNow(delete.cause());
              }
            });
          } else {
            testContext.failNow(findOne.cause());
          }
        });
      } else {
        testContext.failNow(insert.cause());
      }
    });
  }

  @Test
  void should_update_wish(final Vertx vertx, final VertxTestContext testContext) {
    // given
    final String modifiedDescription = "MODIFIED";
    final String title = "Test Title";
    final String url = "Test Url";
    final Wish given = Wish.newBuilder()
      .setTitle(title)
      .setDescription("Test Desc")
      .setCoverImageUrl(url)
      .build();

    // when
    repository.addOne(given).future().setHandler(insert -> {
      if (insert.succeeded()) {

        // when
        final String id = insert.result().get();
        repository.findOne(id).future().setHandler(findOne -> {
          if (findOne.succeeded()) {

            final Wish modifiedWish = given.toBuilder()
              .setId(id)
              .setDescription(modifiedDescription)
              .build();

            // when
            repository.updateOne(modifiedWish).future().setHandler(update -> {
              if (update.succeeded()) {

                // then
                testContext.verify(() -> {
                  repository.findOne(id).future().setHandler(afterUpdate -> {
                    if (afterUpdate.succeeded()) {
                      assertTrue(update.result());
                      assertEquals(modifiedDescription, afterUpdate.result().get().getDescription());
                      assertEquals(title, afterUpdate.result().get().getTitle());
                      assertEquals(url, afterUpdate.result().get().getCoverImageUrl());
                      testContext.completeNow();
                    } else {
                      testContext.failNow(afterUpdate.cause());
                    }
                  });
                });
              } else {
                testContext.failNow(update.cause());
              }
            });
          } else {
            testContext.failNow(findOne.cause());
          }
        });
      } else {
        testContext.failNow(insert.cause());
      }
    });
  }

  @Test
  void should_notify_when_not_updated(final Vertx vertx, final VertxTestContext testContext) {
    // given
    final String title = "Test Title";
    final String url = "Test Url";
    final String description = "Test Desc";
    final Wish given = Wish.newBuilder()
      .setTitle(title)
      .setDescription(description)
      .setCoverImageUrl(url)
      .build();

    // when
    repository.addOne(given).future().setHandler(insert -> {
      if (insert.succeeded()) {

        // when
        final String id = insert.result().get();
        repository.findOne(id).future().setHandler(findOne -> {
          if (findOne.succeeded()) {

            final Wish modifiedWish = given.toBuilder()
              .setId(id)
              .setDescription(description)
              .build();

            // when
            repository.updateOne(modifiedWish).future().setHandler(update -> {
              if (update.succeeded()) {

                // then
                testContext.verify(() -> {
                  repository.findOne(id).future().setHandler(afterUpdate -> {
                    if (afterUpdate.succeeded()) {
                      assertFalse(update.result());
                      assertEquals(description, afterUpdate.result().get().getDescription());
                      assertEquals(title, afterUpdate.result().get().getTitle());
                      assertEquals(url, afterUpdate.result().get().getCoverImageUrl());
                      testContext.completeNow();
                    } else {
                      testContext.failNow(afterUpdate.cause());
                    }
                  });
                });
              } else {
                testContext.failNow(update.cause());
              }
            });
          } else {
            testContext.failNow(findOne.cause());
          }
        });
      } else {
        testContext.failNow(insert.cause());
      }
    });
  }

  @Test
  void should_notify_if_can_not_delete(final Vertx vertx, final VertxTestContext testContext) {
    // given
    final Wish given = Wish.newBuilder()
      .setTitle("Test Title")
      .setDescription("Test Desc")
      .setCoverImageUrl("Test Url")
      .build();

    // when
    repository.addOne(given).future().setHandler(insert -> {
      if (insert.succeeded()) {

        // when
        final String id = insert.result().get();
        repository.findOne(id).future().setHandler(findOne -> {
          if (findOne.succeeded()) {

            // when
            repository.deleteOne(id).future().setHandler(delete -> {
              if (delete.succeeded()) {

                // when
                repository.deleteOne(id).future().setHandler(secondDelete -> {
                  if (secondDelete.succeeded()) {

                    // then
                    testContext.verify(() -> {
                      assertFalse(secondDelete.result());
                      testContext.completeNow();
                    });

                  } else {
                    testContext.failNow(secondDelete.cause());
                  }
                });

              } else {
                testContext.failNow(delete.cause());
              }
            });
          } else {
            testContext.failNow(findOne.cause());
          }
        });
      } else {
        testContext.failNow(insert.cause());
      }
    });
  }

  @Test
  void should_insert_and_read_multiple_wishes(final Vertx vertx, final VertxTestContext testContext) {
    // given
    final Wish given1 = Wish.newBuilder()
      .setTitle("Test Title")
      .setDescription("Test Desc")
      .setCoverImageUrl("Test Url")
      .build();
    final Wish given2 = Wish.newBuilder()
      .setTitle("Test Another")
      .setDescription("Test Another")
      .setCoverImageUrl("Test Another")
      .build();

    // when
    CompositeFuture.all(repository.addOne(given1).future(), repository.addOne(given2).future()).setHandler(inserts -> {
      if (inserts.succeeded()) {

        // when
        repository.findAll().future().setHandler(findAll -> {
          if (findAll.succeeded()) {

            // then
            testContext.verify(() -> {
              findAll.result().forEach(wish -> assertTrue(
                equalsIgnoreId(given1, wish) ||
                  equalsIgnoreId(given2, wish))
              );
              testContext.completeNow();
            });

          } else {
            testContext.failNow(findAll.cause());
          }
        });
      } else {
        testContext.failNow(inserts.cause());
      }
    });
  }

  private void assertEqualsIgnoreID(final Wish a, final Wish b) {
    assertTrue(equalsIgnoreId(a, b));
  }

  private boolean equalsIgnoreId(final Wish a, final Wish b) {
    return a.getTitle().equals(b.getTitle()) &&
      a.getDescription().equals(b.getDescription()) &&
      a.getCoverImageUrl().equals(b.getCoverImageUrl());
  }

  private void assertThatPortIsAvailable(final MongoDbContainer container) {
    try {
      new Socket(container.getContainerIpAddress(), container.getPort());
    } catch (final IOException e) {
      throw new AssertionError("The expected port " + container.getPort() + " is not available!");
    }
  }
}
