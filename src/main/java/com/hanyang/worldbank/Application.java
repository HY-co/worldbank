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

    private static void insert() throws IOException{
        String code = "", name ="";
        Double internetUsers = new Double(0), adultLiteracyRate = new Double(0);

        boolean canAdd = false;
        do {
            System.out.println("Please enter country code:");
            code = reader.readLine();
            if (code.length() > 0 && code.length() < 4 && findCountryByCode(code) == null) {
                canAdd  = true;
            }else {
                System.out.println("Country code already exists!");
                continue;
            }
        } while (!canAdd);

        System.out.println("Please enter country name(Note: the maximum length of name is 32):");
        name = reader.readLine();
        name = name.substring(0, Math.min(name.length(), 32));

        canAdd = false;
        do {
            try {
                System.out.println("Please enter internet users rate");
                internetUsers = Double.valueOf(reader.readLine());
                if (internetUsers > 100 || internetUsers < 0) {
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
                System.out.println("Please enter adult literacy rate");
                adultLiteracyRate = Double.valueOf(reader.readLine());
                if (adultLiteracyRate > 100 || adultLiteracyRate < 0) {
                    System.out.println("Please use a number between 0 and 100");
                    continue;
                }
                canAdd = true;
            }catch (NumberFormatException nfe) {
                System.out.println(nfe.getMessage());
                nfe.printStackTrace();
            }
        }while (!canAdd);


        Country country = new CountryBuilder(code, name)
                .withInternetUsers(internetUsers)
                .withAdultLiteratyRate(adultLiteracyRate)
                .build();

        save(country);
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

    private static void edit() throws IOException{
        String code = reader.readLine();
        Country country = findCountryByCode(code);
        while (country == null) {
            System.out.println("Please enter a valid code:");
            code = reader.readLine();
            country = findCountryByCode(code);
        }

        String newCode = "";
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

    private static String promptForCode() throws IOException{
        System.out.println("Please enter the code of the country you want to delete");
        String code = reader.readLine();
        return code;
    }
}
