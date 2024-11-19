package org.bank.model.mapper;

import org.bank.model.Account;
import org.bank.model.CheckingAccount;
import org.bank.model.SavingsAccount;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AccountMapper implements Mapper<Account> {
    @Override
    public Account map(ResultSet resultSet) throws SQLException {
        String accountType = resultSet.getString("type");
        Account account;

        if ("CHECKING".equalsIgnoreCase(accountType)) {
            account = new CheckingAccount(
                    new ArrayList<>(), // Lista de Useri (va fi completată ulterior)
                    resultSet.getDouble("balance"),
                    resultSet.getDouble("transaction_fee")
            );
        } else if ("SAVINGS".equalsIgnoreCase(accountType)) {
            account = new SavingsAccount(
                    new ArrayList<>(), // Lista de Useri (va fi completată ulterior)
                    resultSet.getDouble("balance"),
                    resultSet.getDouble("interest_rate")
            );
        } else {
            throw new IllegalArgumentException("Unknown account type: " + accountType);
        }

        account.setId(resultSet.getInt("id"));
        account.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());
        return account;
    }
}

