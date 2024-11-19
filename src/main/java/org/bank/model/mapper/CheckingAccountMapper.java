package org.bank.model.mapper;

import org.bank.model.CheckingAccount;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CheckingAccountMapper implements Mapper<CheckingAccount> {
    @Override
    public CheckingAccount map(ResultSet resultSet) throws SQLException {
        CheckingAccount account = new CheckingAccount(
                new ArrayList<>(), // Lista de customers poate fi populată ulterior.
                resultSet.getDouble("balance"), // Setăm balance.
                resultSet.getDouble("transactionFee") // Setăm transactionFee.
        );

        MapperUtils.mapAccountAttributes(resultSet, account);

        return account;
    }
}

