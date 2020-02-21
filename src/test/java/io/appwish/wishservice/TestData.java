package io.appwish.wishservice;

import io.appwish.wishservice.model.Wish;
import io.appwish.wishservice.model.input.UpdateWishInput;
import io.appwish.wishservice.model.input.WishInput;
import io.appwish.wishservice.model.query.AllWishQuery;
import io.appwish.wishservice.model.query.WishQuery;
import java.time.LocalDateTime;
import java.util.List;

import com.google.protobuf.Timestamp;


// TODO test if createdAt, updatedAt dates are properly handled
// TODO test if markdown -> html is handled as expected
/**
 * Class for constant test data/values to be used in test classes to avoid duplication /
 * boilerplate
 */
public final class TestData {

  /**
   * Represents app address that should be used during the tests
   */
  public static final String APP_HOST = "localhost";

  /**
   * Represents app port that should be used during the tests to avoid ports conflicts
   */
  public static final int APP_PORT = 8281;

  /**
   * Some random values to be used to fill Wish fields in tests
   * */
  public static final long SOME_ID = 1;
  public static final long SOME_AUTHOR_ID = 9999;
  public static final String SOME_TITLE = "Title1";
  public static final String SOME_MARKDOWN = "# Gimme the app!";
  public static final String SOME_COVER_IMAGE_URL = "https://appwish.org/static/hardcoded";
  public static final String SOME_WISH_SLUG = "hardcoded";
  public static final String SOME_HTML = "<p>blabla</p>";

  /**
   * Some random error message
   */
  public static final String ERROR_MESSAGE = "Something went wrong";

  /**
   * Use this in test for IDs that you assume do not exist in database
   */
  public static final long NON_EXISTING_ID = 1411223L;

  /**
   * Sample timestamp
   */
  public static final Timestamp SOME_TIMESTAMP = Timestamp.newBuilder().setNanos(LocalDateTime.of(2020, 2, 1, 9, 0).getNano()).build();

  /**
   * Wishes to be reused in tests
   */
  public static final Wish WISH_1 = new Wish(SOME_ID, SOME_TITLE, SOME_MARKDOWN, SOME_COVER_IMAGE_URL, SOME_AUTHOR_ID, SOME_WISH_SLUG, SOME_HTML, SOME_TIMESTAMP, SOME_TIMESTAMP);
  public static final Wish WISH_2 = new Wish(2, "title2", "desc2", "url2", 92, "posturl2", "<p>blabla</p>", SOME_TIMESTAMP, SOME_TIMESTAMP);
  public static final Wish WISH_3 = new Wish(3, "title3", "desc3", "url3", 93, "posturl3", "<p>blabla</p>", SOME_TIMESTAMP, SOME_TIMESTAMP);
  public static final Wish WISH_4 = new Wish(4, "title4", "desc4", "url4", 94, "posturl4", "<p>blabla</p>", SOME_TIMESTAMP, SOME_TIMESTAMP);

  /**
   * List of random wishes to be used in tests
   */
  public static final List<Wish> WISHES = List.of(WISH_1, WISH_2, WISH_3, WISH_4);

  /**
   * All wish query to be used in tests
   */
  public static final AllWishQuery ALL_WISH_QUERY = new AllWishQuery();

  /**
   * Some random inputs to be used in tests
   */
  public static final WishInput WISH_INPUT_1 = new WishInput(
    TestData.WISH_1.getTitle(),
    TestData.WISH_1.getMarkdown(),
    TestData.WISH_1.getCoverImageUrl());
  public static final WishInput WISH_INPUT_2 = new WishInput(
    TestData.WISH_2.getTitle(),
    TestData.WISH_2.getMarkdown(),
    TestData.WISH_2.getCoverImageUrl());
  public static final WishInput WISH_INPUT_3 = new WishInput(
    TestData.WISH_3.getTitle(),
    TestData.WISH_3.getMarkdown(),
    TestData.WISH_3.getCoverImageUrl());
  public static final WishInput WISH_INPUT_4 = new WishInput(
    TestData.WISH_4.getTitle(),
    TestData.WISH_4.getMarkdown(),
    TestData.WISH_4.getCoverImageUrl());

  /**
   * Some random data for update queries in tests
   */
  public static final UpdateWishInput UPDATE_WISH_INPUT = new UpdateWishInput(
    WISH_4.getId(),
    WISH_4.getTitle(),
    WISH_4.getMarkdown(),
    WISH_4.getCoverImageUrl());

  /**
   * Some data for wish queries in tests
   */
  public static final WishQuery WISH_QUERY = new WishQuery(TestData.SOME_ID);
}
