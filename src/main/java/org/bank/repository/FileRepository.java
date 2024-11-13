package org.bank.repository;

import org.bank.model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FileRepository<T extends Identifiable> implements IRepository<T> {
    private final String filePath;

    public FileRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public int create(T obj) {
        try {
            List<T> allObjects = findAll();
            int newId = allObjects.isEmpty() ? 1 : Collections.max(allObjects, Comparator.comparingInt(Identifiable::getId)).getId() + 1;
            obj.setId(newId);
            allObjects.add(obj);
            writeAll(allObjects);
            return newId;
        } catch (IOException e) {
            throw new RuntimeException("Error creating object", e);
        }
    }

    @Override
    public T read(int id) {
        try {
            List<T> allObjects = findAll();
            return allObjects.stream()
                    .filter(obj -> obj.getId() == id)
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            throw new RuntimeException("Error reading object", e);
        }
    }

    @Override
    public void update(T obj) {
        try {
            List<T> allObjects = findAll();
            int index = -1;
            for (int i = 0; i < allObjects.size(); i++) {
                if (allObjects.get(i).getId() == obj.getId()) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                allObjects.set(index, obj);
                writeAll(allObjects);
            } else {
                throw new RuntimeException("Object not found for update");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating object", e);
        }
    }

    @Override
    public void delete(int id) {
        try {
            List<T> allObjects = findAll();
            allObjects = allObjects.stream()
                    .filter(obj -> obj.getId() != id)
                    .collect(Collectors.toList());
            writeAll(allObjects);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting object", e);
        }
    }

    public List<T> findAll() throws IOException {
        List<T> objects = new ArrayList<>();
        Map<Integer, User> userMap = new HashMap<>(); // Map pentru utilizatori
        Map<Integer, Account> accountMap = new HashMap<>(); // Map pentru conturi

        // Citirea obiectelor din fișierul principal
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }

        try (FileReader fileReader = new FileReader(file);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                T obj = deserialize(line, userMap, accountMap);
                if (obj != null) {
                    objects.add(obj);

                    if (obj instanceof User) {
                        userMap.put(((User) obj).getId(), (User) obj);
                    } else if (obj instanceof Account) {
                        accountMap.put(((Account) obj).getId(), (Account) obj);
                    }
                }
            }
        }

        // Citirea fișierului user_accounts.csv pentru a reconstrui relațiile dintre conturi și utilizatori
        File userAccountFile = new File("/data/user_accounts.csv");
        if (userAccountFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(userAccountFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int accountId = Integer.parseInt(parts[0]);
                    Account account = accountMap.get(accountId);

                    if (account != null) {
                        for (int i = 1; i < parts.length; i++) {
                            int userId = Integer.parseInt(parts[i]);
                            User user = userMap.get(userId);
                            if (user != null) {
                                account.getCustomers().add(user);
                            }
                        }
                    }
                }
            }
        }

        return objects;
    }


    private void writeAll(List<T> objects) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (T obj : objects) {
                writer.write(serialize(obj));
                writer.newLine();
            }
        }
    }

    private String serialize(T obj) {
        if (obj instanceof User) {
            return serializeUser((User) obj);
        } else if (obj instanceof Account) {
            return serializeAccount((Account) obj);
        }
        return obj.getId() + "," + obj; // Default case
    }

    private String serializeUser(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getId()).append(",");
        sb.append(user.getFirstName()).append(",");
        sb.append(user.getLastName()).append(",");
        sb.append(user.getEmail()).append(",");
        sb.append(user.getPhoneNumber()).append(",");
        sb.append(user.getPassword()).append(",");

        if (user instanceof Customer) {
            sb.append("Customer");
            Customer customer = (Customer) user;

            // Adăugăm lista de conturi doar dacă există
            if (!customer.getAccountList().isEmpty()) {
                sb.append(",");
                for (Account account : customer.getAccountList()) {
                    sb.append(account.getId()).append(";");
                }
            }
        } else if (user instanceof Employee) {
            Employee employee = (Employee) user;
            sb.append("Employee").append(",");
            sb.append(employee.getSalary()).append(",");
            sb.append(employee.getRole());
        }

        return sb.toString();
    }

    private String serializeAccount(Account account) {
        StringBuilder sb = new StringBuilder();
        sb.append(account.getId()).append(","); // ID-ul contului
        sb.append(account.getBalance()).append(","); // Balanța
        sb.append(account.getCreationTime()).append(","); // Timpul de creare

        // Serializare lista de utilizatori (doar ID-urile)
        if (!account.getCustomers().isEmpty()) {
            for (User user : account.getCustomers()) {
                sb.append(user.getId()).append(";"); // Adaugă ID-ul utilizatorului
            }
        }
        sb.append(","); // Separator

        // Adaugă tipul de cont
        if (account instanceof CheckingAccount) {
            sb.append("CheckingAccount");
        } else if (account instanceof SavingsAccount) {
            sb.append("SavingsAccount");
        }

        return sb.toString();
    }

    private T deserialize(String line, Map<Integer, User> userMap, Map<Integer, Account> accountMap) {
        String[] parts = line.split(",");
        try {
            int id = Integer.parseInt(parts[0]);

            if (parts[parts.length - 1].equals("Customer") || parts[parts.length - 1].equals("Employee")) {
                return (T) deserializeUser(parts, id, userMap);
            } else if (parts[parts.length - 1].equals("CheckingAccount") || parts[parts.length - 1].equals("SavingsAccount")) {
                return (T) deserializeAccount(parts, id, userMap, accountMap);
            }

            return null; // Unknown type
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing object", e);
        }
    }

    private User deserializeUser(String[] parts, int id, Map<Integer, User> userMap) {
        String firstName = parts[1];
        String lastName = parts[2];
        String email = parts[3];
        String phoneNumber = parts[4];
        String password = parts[5];
        String type = parts[6];

        if (type.equals("Customer")) {
            Customer customer = new Customer(id, firstName, lastName, email, phoneNumber, password);

            // Reconstruim lista de conturi asociate
            if (parts.length > 7 && !parts[7].isEmpty()) {
                String[] accountIds = parts[7].split(";");
                for (String accountId : accountIds) {
                    int accountIdInt = Integer.parseInt(accountId);
                    // Map-ul userMap nu conține conturi; aceasta este o problemă logică
                    // Deocamdată, ignorăm conturile care nu sunt deserializate corect
                }
            }
            return customer;
        } else if (type.equals("Employee")) {
            int salary = Integer.parseInt(parts[7]);
            String role = parts[8];
            return new Employee(id, firstName, lastName, email, phoneNumber, password, salary, role);
        }

        return null;
    }

    private Account deserializeAccount(String[] parts, int id, Map<Integer, User> userMap, Map<Integer, Account> accountMap) {
        try {
            double balance = Double.parseDouble(parts[1]);
            LocalDateTime creationTime = LocalDateTime.parse(parts[2]);
            String type = parts[4];
            List<User> customers = new ArrayList<>();

            // Reconstruim lista de clienți
            if (!parts[3].isEmpty()) {
                String[] customerIds = parts[3].split(";");
                for (String customerId : customerIds) {
                    int customerIdInt = Integer.parseInt(customerId);
                    User user = userMap.get(customerIdInt);
                    if (user != null) {
                        customers.add(user);
                    }
                }
            }

            // Instanțiem contul și setăm id-ul separat
            Account account;
            if (type.equals("CheckingAccount")) {
                account = new CheckingAccount(customers, balance, 0.5);
            } else if (type.equals("SavingsAccount")) {
                account = new SavingsAccount(customers, balance, 4.5);
            } else {
                throw new IllegalArgumentException("Unknown account type: " + type);
            }

            account.setId(id); // Setăm id-ul explicit
            account.setCreationTime(creationTime); // Setăm timpul de creare
            return account;

        } catch (Exception e) {
            throw new RuntimeException("Error deserializing Account", e);
        }
    }

    public void writeUserAccountRelation(Account account) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/data/user_accounts.csv", true))) {
            for (User customer : account.getCustomers()) {
                writer.write(account.getId() + "," + customer.getId());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing to user_accounts.csv", e);
        }
    }
}
