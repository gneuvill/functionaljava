package fj.control.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A method of connecting to the database.
 */
public interface Connector {
  Connection connect() throws SQLException;
}
