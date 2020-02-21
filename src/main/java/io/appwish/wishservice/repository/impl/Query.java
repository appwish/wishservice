package io.appwish.wishservice.repository.impl;

/**
 * Contains queries to execute on Postgres.
 *
 * I'm not sure what's the best practice for storing String SQLs, so for now it'll stay here.
 */
public enum Query {
  FIND_ALL_WISH("SELECT * FROM wishes"),
  FIND_ONE_WISH("SELECT * FROM wishes WHERE id=$1"),
  DELETE_WISH_QUERY("DELETE FROM wishes WHERE id=$1"),
  INSERT_WISH_QUERY(
    "INSERT INTO wishes ("
      + "title, "
      + "markdown, "
      + "cover_image_url, "
      + "author_id, "
      + "slug, "
      + "html, "
      + "created_at, "
      + "updated_at) "
      + "VALUES ($1, $2, $3, $4, $5, $6, $7, $8) "
      + "RETURNING *"),
  UPDATE_WISH_QUERY(
    "UPDATE wishes SET "
      + "title=$1, "
      + "markdown=$2, "
      + "cover_image_url=$3, "
      + "html=$4, "
      + "updated_at=$5 "
      + "WHERE id=$6 RETURNING *"),
  CREATE_WISH_TABLE(
    "CREATE TABLE IF NOT EXISTS wishes("
      + "id serial PRIMARY KEY, "
      + "title VARCHAR (50) NOT NULL, "
      + "markdown VARCHAR (255) NOT NULL, "
      + "html VARCHAR (255), "
      + "cover_image_url VARCHAR (255), "
      + "author_id serial, "
      + "created_at timestamp, "
      + "updated_at timestamp, "
      + "slug VARCHAR (255));");

  private final String sql;

  Query(final String sql) {
    this.sql = sql;
  }

  public String sql() {
    return sql;
  }
}
