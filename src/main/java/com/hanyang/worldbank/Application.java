package com.hanyang.worldbank;

import com.hanyang.worldbank.model.Country;
import com.hanyang.worldbank.model.Country.CountryBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


public class Application {
    // Hold a reusable reference to SessionFactory
    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static final List<String> menu = Arrays.asList(
            "Viewing data table(or 'viewd')",
            "Viewing statistics(or 'views')",
            "Adding a country(or 'add')",
            "Editing a country(or 'edit')",
            "Deleting a country(or 'delete')"
    );
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static SessionFactory buildSessionFactory() {
        // Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        String choice = "";
        do {
            try {
                choice = promptForAction();
                switch(choice) {
                    case "viewd":
                    case "viewing data table":
                        List<Country> countries = fetchAllCountries();
                        countries.stream().forEach(System.out::println);
                        break;
                    case "views":
                    case "Viewing statistics":
                        break;
                    case "add":
                    case "adding a country":
                        insert();
                        break;
                    case "edit":
                    case "editing a country":
                        edit();
                        break;
                    case "delete":
                    case "deleting a country":
                        delete();
                        break;
                    case "quit":
                        System.out.println("Exiting...");
                }
            }catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                ioe.printStackTrace();
            }
        }while (!choice.toLowerCase().equals("quit"));
    }

    //============ Viewing data table ================
    private static List<Country> fetchAllCountries() {
        // Open a session
        Session session = sessionFactory.openSession();

        // Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        //  Create CriteriaQuery
        CriteriaQuery<Country> criteria = builder.createQuery(Country.class);

        // Specify criteria root
        criteria.from(Country.class);

        // Execute query
        List<Country> countries = session.createQuery(criteria).getResultList();

        // Close the session
        session.close();

        return countries;
    }

    // ============ Viewing statistics ==================
    private static void getStatistics() {
        List<Country> countries = fetchAllCountries();
        double maxInternetUsers = 0, maxAdultLiteracyRate = 0;
        double minInternetUsers = 0, minAdultLiteracyRate = 0;

        double sumInternetAdult, sumInternetUsers, sumAdultLiteracyRate, sumInternetUsersSquare, sumAdultLiteracyRateSquare;
        sumInternetAdult = sumInternetUsers = sumAdultLiteracyRate = sumInternetUsersSquare = sumAdultLiteracyRateSquare = 0;

        countries.stream().forEach(c -> {
            // Any countries with missing data will not be included
            if (c.getInternetUsers() != null && c.getAdultLiteracyRate() != null) {

            }
        });
    }

    // ============ Adding a country =====================
    private static void insert() throws IOException{
        Country country = promptForInformation(false);
        save(country);
    }

    // ============ Editing a country ====================
    private static void edit() throws IOException{
        System.out.println("Please enter the code of the country you want to edit:");
        String code = reader.readLine();
        Country country = findCountryByCode(code);
        while (country == null) {
            System.out.println("Please enter a valid code:");
            code = reader.readLine();
            country = findCountryByCode(code);
        }

        // Assign new information
        Country temp = promptForInformation(true);
        country.setCode(temp.getCode().isEmpty() ? country.getCode() : temp.getCode());
        country.setName(temp.getName().isEmpty() ? country.getName() : temp.getName());
        country.setInternetUsers(temp.getInternetUsers() == null ? country.getInternetUsers() : temp.getInternetUsers());
        country.setAdultLiteracyRate(temp.getAdultLiteracyRate() == null ? country.getAdultLiteracyRate() : temp.getAdultLiteracyRate());

        update(country);
    }

    // ============= Delete a country =====================
    private static void delete() {
        // Open a session
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        boolean found = false;
        do {
            String code;
            try {
                code = promptForCode();
                if (code.length() < 0 || code.length() > 100) {
                    continue;
                }
                found = true;
                Country country = findCountryByCode(code);
                System.out.println("Deleting...");
                session.delete(country);
                session.getTransaction().commit();
                System.out.println("Delete complete!");
            }catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                ioe.printStackTrace();
            }
        }while (!found);

        session.close();
    }

    // =========================================================
    private static String promptForAction() throws IOException {
        System.out.println("menu:");
        for (String option : menu) {
            System.out.println(option);
        }

        System.out.println();
        System.out.println("Select an option:");
        String choice = reader.readLine();
        choice = choice.trim();
        return choice;
    }

    private static Country findCountryByCode(String code) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Retrieve the persistent object (or null if not found)
        Country country = session.get(Country.class, code);

        // Close the session
        session.close();

        // Return the object
        return country;
    }

    private static void save(Country country) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to save the contact
        session.save(country);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }

    private static Country promptForInformation(boolean isEdit) throws IOException {
        String code = null, name = null;
        Double internetUsers = null, adultLiteracyRate = null;

        boolean canAdd = false;
        do {
            System.out.println("Please enter country code" + (isEdit ? " (press 'enter' if you do not want to change)" : "") +":");
            code = reader.readLine();
            if (!isEdit && code.length() > 0 && code.length() < 4 && findCountryByCode(code) == null
                || isEdit && code.isEmpty()) {
                canAdd  = true;
            }else {
                System.out.println("Country code already exists!");
                continue;
            }
            //System.out.println(code);
        } while (!canAdd);

        System.out.println("Please enter country name(Note: the maximum length of name is 32)" +
                (isEdit ? " (press 'enter' if you do not want to change)" : "") +":");
        name = reader.readLine();
        name = name.substring(0, Math.min(name.length(), 32));

        canAdd = false;
        do {
            try {
                System.out.println("Please enter internet users rate" + (isEdit ? " (press 'enter' if you do not want to change)" : "") +":");
                String internetUsersStr = reader.readLine();
                internetUsers = internetUsersStr.isEmpty() ? null : Double.valueOf(internetUsersStr);
                if (!isEdit && (internetUsers == null || internetUsers > 100 || internetUsers < 0)
                    || isEdit && internetUsers != null && (internetUsers > 100 || internetUsers < 0)) {
                    System.out.println("Please use a number between 0 and 100");
                    continue;
                }
                canAdd = true;
            }catch (NumberFormatException nfe) {
                System.out.println(nfe.getMessage());
                nfe.printStackTrace();
            }
        }while (!canAdd);

        canAdd = false;
        do {
            try {
                System.out.println("Please enter adult literacy rate" + (isEdit ? " (press 'enter' if you do not want to change)" : "") +":");
                String adultLiteracyRateStr = reader.readLine();
                adultLiteracyRate = adultLiteracyRateStr.isEmpty() ? null : Double.valueOf(adultLiteracyRateStr);
                if (!isEdit && (adultLiteracyRate == null || adultLiteracyRate > 100 || adultLiteracyRate < 0)
                        || isEdit && adultLiteracyRate != null && (adultLiteracyRate > 100 || adultLiteracyRate < 0)) {
                    System.out.println("Please use a number between 0 and 100");
                    continue;
                }
                canAdd = true;
            }catch (NumberFormatException nfe) {
                System.out.println(nfe.getMessage());
                nfe.printStackTrace();
            }
        }while (!canAdd);

        return new CountryBuilder(code, name)
                .withInternetUsers(internetUsers)
                .withAdultLiteratyRate(adultLiteracyRate)
                .build();
    }

    private static void update(Country country) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to update
        session.update(country);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }

    private static String promptForCode() throws IOException{
        System.out.println("Please enter the code of the country you want to delete");
        String code = reader.readLine();
        return code;
    }
}
