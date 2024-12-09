package org.bank.model.mapper;

import org.bank.model.Account;
import org.bank.model.AccountLogs;
import org.bank.model.CheckingAccount;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper class for mapping a {@link ResultSet} to an {@link AccountLogs} object.
 */
public class AccountLogsMapper implements Mapper<AccountLogs> {

    @Override
    public AccountLogs map(ResultSet resultSet) throws SQLException {
        Account account = new CheckingAccount(resultSet.getInt("account_id"));
        AccountLogs accountLogs = new AccountLogs(account);

        do {
            accountLogs.addLog(resultSet.getString("message"));
        } while (resultSet.next());

        return accountLogs;
    }
}
