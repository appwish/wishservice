package io.appwish.wishservice.mapper;

import io.appwish.grpc.Wish;

public interface ProtoTypeMapper<T> {
  Wish mapFrom(final T other);
}
