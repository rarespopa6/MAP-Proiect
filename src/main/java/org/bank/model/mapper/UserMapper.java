package org.bank.model.mapper;

import org.bank.model.Customer;
import org.bank.model.Employee;
import org.bank.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserMapper implements Mapper<User> {
    @Override
    public User map(ResultSet resultSet) throws SQLException {
        String userType = resultSet.getString("type");
        User user;

        if ("CUSTOMER".equalsIgnoreCase(userType)) {
            user = new Customer(
                    resultSet.getInt("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString("phone_number"),
                    resultSet.getString("password")
            );
            ((Customer) user).setAccountList(new ArrayList<>());
            ((Customer) user).setLoanList(new ArrayList<>());
        } else if ("EMPLOYEE".equalsIgnoreCase(userType)) {
            user = new Employee(
                    resultSet.getInt("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString("phone_number"),
                    resultSet.getString("password"),
                    resultSet.getInt("salary"),
                    resultSet.getString("role")
            );
        } else {
            throw new IllegalArgumentException("Unknown user type: " + userType);
        }

        return user;
    }
}


