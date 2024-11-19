package org.bank.model.mapper;

import org.bank.model.SavingsAccount;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SavingsAccountMapper implements Mapper<SavingsAccount> {
    @Override
    public SavingsAccount map(ResultSet resultSet) throws SQLException {
        SavingsAccount account = new SavingsAccount(
                new ArrayList<>(), // Lista de customers poate fi populată ulterior.
                resultSet.getDouble("balance"), // Setăm balance.
                resultSet.getDouble("interestRate") // Setăm interestRate.
        );

        MapperUtils.mapAccountAttributes(resultSet, account);

        return account;
    }
}

