package io.appwish.wishservice.model.query;

import io.appwish.grpc.AllWishQueryProto;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * This type should be used to query multiple objects from the database. Right now it's empty but in
 * the future it may contain information about pagination, sorting etc.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(AllWishQueryProto.class)
public class AllWishQuery {

}
