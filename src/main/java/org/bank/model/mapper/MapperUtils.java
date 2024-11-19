package org.bank.model.mapper;

import org.bank.model.Account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class MapperUtils {
    public static void mapAccountAttributes(ResultSet resultSet, Account account) throws SQLException {
        account.setId(resultSet.getInt("id"));
        account.setBalance(resultSet.getDouble("balance"));
        account.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());
    }
}

