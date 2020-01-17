package io.appwish.wishservice.eventbus;

/**
 * Represents addresses available on the event bus
 */
public enum Address {
  FIND_ALL_WISHES,
  FIND_ONE_WISH,
  CREATE_ONE_WISH,
  UPDATE_ONE_WISH,
  DELETE_ONE_WISH;

  public String get() {
    return name();
  }
}
