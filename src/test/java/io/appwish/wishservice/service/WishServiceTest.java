package io.appwish.wishservice.service;

import io.appwish.grpc.*;
import io.appwish.wishservice.repository.impl.DocumentWishRepository;
import io.vertx.core.Promise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WishServiceTest {

  private static final String ID_1 = "ID1";
  private static final Wish WISH_1 = Wish.newBuilder().setTitle("Title1").setDescription("Desc1").setCoverImageUrl("Url1").setId(ID_1).build();
  private static final Wish WISH_2 = Wish.newBuilder().setTitle("Title2").setDescription("Desc2").setCoverImageUrl("Url2").setId("ID2").build();
  private static final Wish WISH_3 = Wish.newBuilder().setTitle("Title3").setDescription("Desc3").setCoverImageUrl("Url3").setId("ID3").build();
  private static final Wish WISH_4 = Wish.newBuilder().setTitle("Title4").setDescription("Desc4").setCoverImageUrl("Url4").setId("ID4").build();
  private static final List<Wish> WISHES = List.of(WISH_1, WISH_2, WISH_3, WISH_4);

  private DocumentWishRepository repository;
  private WishService service;

  @BeforeEach
  void setUp() {
    repository = mock(DocumentWishRepository.class);
    service = new WishService(repository);
  }

  @Test
  void should_return_all_wishes_from_repository() {
    // given
    final AllWishQuery query = AllWishQuery.newBuilder().build();
    final Promise<AllWishReply> promise = Promise.promise();
    final Promise<List<Wish>> reply = Promise.promise();
    when(repository.findAll()).thenReturn(reply);
    reply.complete(WISHES);

    // when
    service.getAllWish(query, promise);

    // then
    assertTrue(promise.future().succeeded());
    assertEquals(WISHES.size(), promise.future().result().getWishesList().size());
    assertEquals(WISHES, promise.future().result().getWishesList());
  }

  @Test
  void should_return_empty_list() {
    // given
    final AllWishQuery query = AllWishQuery.newBuilder().build();
    final Promise<AllWishReply> promise = Promise.promise();
    final Promise<List<Wish>> reply = Promise.promise();
    when(repository.findAll()).thenReturn(reply);
    reply.complete(List.of());

    // when
    service.getAllWish(query, promise);

    // then
    assertTrue(promise.future().succeeded());
    assertEquals(0, promise.future().result().getWishesList().size());
  }

  @Test
  void should_pass_exception_on_find_all_error() {
    // given
    final AllWishQuery query = AllWishQuery.newBuilder().build();
    final Promise<AllWishReply> promise = Promise.promise();
    final Promise<List<Wish>> reply = Promise.promise();
    when(repository.findAll()).thenReturn(reply);
    reply.fail(new RuntimeException());

    // when
    service.getAllWish(query, promise);

    // then
    assertTrue(promise.future().failed());
    assertTrue(promise.future().cause() instanceof RuntimeException);
  }

  @Test
  void should_return_found_wish() {
    // given
    final WishQuery query = WishQuery.newBuilder().setId(ID_1).build();
    final Promise<WishReply> promise = Promise.promise();
    final Promise<Optional<Wish>> reply = Promise.promise();
    when(repository.findOne(ID_1)).thenReturn(reply);
    reply.complete(Optional.of(WISH_1));

    // when
    service.getWish(query, promise);

    // then
    assertTrue(promise.future().succeeded());
    assertEquals(WISH_1, promise.future().result().getWish());
  }

  @Test
  void should_return_empty_when_wish_not_found() {
    // given
    final WishQuery query = WishQuery.newBuilder().setId(ID_1).build();
    final Promise<WishReply> promise = Promise.promise();
    final Promise<Optional<Wish>> reply = Promise.promise();
    when(repository.findOne(ID_1)).thenReturn(reply);
    reply.complete(Optional.empty());

    // when
    service.getWish(query, promise);

    // then
    assertTrue(promise.future().succeeded());
    assertNull(promise.future().result());
  }

  @Test
  void should_pass_exception_on_find_one_error() {
    // given
    final WishQuery query = WishQuery.newBuilder().setId(ID_1).build();
    final Promise<WishReply> promise = Promise.promise();
    final Promise<Optional<Wish>> reply = Promise.promise();
    when(repository.findOne(ID_1)).thenReturn(reply);
    reply.fail(new RuntimeException());

    // when
    service.getWish(query, promise);

    // then
    assertTrue(promise.future().failed());
    assertTrue(promise.future().cause() instanceof RuntimeException);
  }
}
