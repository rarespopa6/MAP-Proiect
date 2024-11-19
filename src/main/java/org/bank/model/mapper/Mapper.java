package org.bank.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Mapper<T> {
    T map(ResultSet resultSet) throws SQLException;
}

