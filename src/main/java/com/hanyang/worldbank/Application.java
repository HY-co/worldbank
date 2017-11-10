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
            "Viewing data table(use 'viewd' for option)",
            "Viewing statistics(use 'views' for option)",
            "Adding a country(use 'add' for option)",
            "Editing a country(use 'edit' for option)",
            "Deleting a country(use 'delete' for option)"
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
                        List<Country> countries = fetchAllCountries();
                        countries.stream().forEach(System.out::println);
                        break;
                    case "views":
                        break;
                    case "add":
                        break;
                    case "edit":
                        break;
                    case "delete":
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

    private static Country findCoutryByCode(String code) {
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
        Long internetUsers = new Long(0), adultLiteracyRate = new Long(0);
        boolean canAdd = false;
        do {
            System.out.println("Please enter country code:");
            code = reader.readLine();
            if (code.length() > 0 && code.length() < 4 && findCoutryByCode(code) == null) {
                canAdd  = true;
            }else {
                System.out.println("Country code already exists!");
                continue;
            }

            System.out.println("Please enter country name:");
            name = reader.readLine();

            System.out.println("Please enter internet users rate (e.g. 19.5 represents 19.5%");
            internetUsers = Long.valueOf(reader.readLine());

            System.out.println("Please enter adult literacy rate (e.g. 19.5 represents 19.5%");
            adultLiteracyRate = Long.valueOf(reader.readLine());
        } while (!canAdd);
    }

    private static void edit() throws IOException{
        String code = reader.readLine();
        Country country = findCoutryByCode(code);
        while (country == null) {
            System.out.println("Please enter a valid code:");
            code = reader.readLine();
            country = findCoutryByCode(code);
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
}
