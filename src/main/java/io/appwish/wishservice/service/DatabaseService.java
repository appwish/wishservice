package io.appwish.wishservice.service;

import io.appwish.wishservice.eventbus.Address;
import io.appwish.wishservice.eventbus.Codec;
import io.appwish.wishservice.model.Wish;
import io.appwish.wishservice.model.input.UpdateWishInput;
import io.appwish.wishservice.model.input.WishInput;
import io.appwish.wishservice.model.query.AllWishQuery;
import io.appwish.wishservice.model.query.WishQuery;
import io.appwish.wishservice.repository.WishRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import java.util.List;
import java.util.Optional;

/**
 * Exposes the wish repository on the event bus. Takes data from the wish repository and replies to
 * requests on the event bus.
 */
public class DatabaseService {

  private final EventBus eventBus;
  private final WishRepository wishRepository;

  public DatabaseService(final EventBus eventBus, final WishRepository wishRepository) {
    this.eventBus = eventBus;
    this.wishRepository = wishRepository;
  }

  public void registerEventBusEventHandlers() {
    eventBus.<AllWishQuery>consumer(Address.FIND_ALL_WISHES.get())
      .handler(event -> wishRepository.findAll(event.body()).setHandler(findAllHandler(event)));

    eventBus.<WishQuery>consumer(Address.FIND_ONE_WISH.get())
      .handler(event -> wishRepository.findOne(event.body()).setHandler(findOneHandler(event)));

    eventBus.<WishInput>consumer(Address.CREATE_ONE_WISH.get())
      .handler(event -> wishRepository.addOne(event.body()).setHandler(addOneHandler(event)));

    eventBus.<UpdateWishInput>consumer(Address.UPDATE_ONE_WISH.get())
      .handler(event -> wishRepository.updateOne(event.body()).setHandler(updateOneHandler(event)));

    eventBus.<WishQuery>consumer(Address.DELETE_ONE_WISH.get())
      .handler(event -> wishRepository.deleteOne(event.body()).setHandler(deleteOneHandler(event)));
  }

  private Handler<AsyncResult<Boolean>> deleteOneHandler(final Message<WishQuery> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result());
      } else {
        event.fail(1, "Could not delete the wish from the database");
      }
    };
  }

  private Handler<AsyncResult<Optional<Wish>>> updateOneHandler(
    final Message<UpdateWishInput> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result(), new DeliveryOptions().setCodecName(Codec.WISH.getCodecName()));
      } else {
        event.fail(1, "Error updating the wish in the database");
      }
    };
  }

  private Handler<AsyncResult<Wish>> addOneHandler(final Message<WishInput> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result());
      } else {
        event.fail(1, "Error adding the wish to the database");
      }
    };
  }

  private Handler<AsyncResult<Optional<Wish>>> findOneHandler(final Message<WishQuery> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result(), new DeliveryOptions().setCodecName(Codec.WISH.getCodecName()));
      } else {
        event.fail(1, "Error fetching the wish from the database");
      }
    };
  }

  private Handler<AsyncResult<List<Wish>>> findAllHandler(final Message<AllWishQuery> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result(),
          new DeliveryOptions().setCodecName(Codec.WISH.getCodecName()));
      } else {
        event.fail(1, "Error fetching wishes from the database");
      }
    };
  }
}
