package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Class App</h1>
 * The App class is used to ensure and demonstrate implemented functionalities.
 *
 * @author Daniela Todorova
 * @author Miguel Bacharov
 * @author Mihail Yonchev
 * @author Valeri Vladimirov
 * @since 2020-08-02
 */

public class App {

    /**
     * This is the main method
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        // Create new Application
        App a = new App();

        // Connect to database
        if (args.length < 1) {
            a.connect("localhost:3306");
        } else {
            a.connect(args[0]);
        }

        // _______________
        // Reports Display

        // Get Target City Record
        City city = a.getCity(1);
        a.displayCity(city);

        // Get all cities
        // [Commented Out Due Max Travis CI Lines] List<City> allCities = a.getAllCities();
        // [Commented Out Due Max Travis CI Lines] System.out.println("Total cities: " + allCities.size());
        // [Commented Out Due Max Travis CI Lines] System.out.println("First city in the list: " + allCities.get(0).toString());

        // ________________
        // Countries Report

        // Get all countries
        // [Commented Out Due Max Travis CI Lines] List<Country> allCountries = a.getAllCountries();
        // [Commented Out Due Max Travis CI Lines] System.out.println("Total countries: " + allCountries.size());
        // [Commented Out Due Max Travis CI Lines] Country firstCountryInList = allCountries.get(0);
        // [Commented Out Due Max Travis CI Lines] System.out.println("First country in the list: " + firstCountryInList.toString());
        // [Commented Out Due Max Travis CI Lines] System.out.println("City by largest population for: " + firstCountryInList.getName());

        // ________________
        // Languages Report
        // Get all Languages
        // [Commented Out Due Max Travis CI Lines] List<Language> allLanguages = a.getAllLanguages();
        // [Commented Out Due Max Travis CI Lines] System.out.println("First language " + allLanguages.get(0).toString());

        // ________________
        // City Report
        // [Commented Out Due Max Travis CI Lines] List<City> cityReport = a.getCitiesByLargestPopulationInCountry(firstCountryInList);

        // The population of city Varna
        // [Commented Out Due Max Travis CI Lines] System.out.println("The population of city: " + allCities.get(540).getName() + " is " + allCities.get(540).getPopulation());

        // [Commented Out Due Max Travis CI Lines]
        /*
        try{
            System.out.println("The population of district varna is: " + a.getPopulationOfDistrict("Varna"));
        }catch (Exception exp){
            System.out.println(exp);
        }

        try{
            System.out.println("The population of country Bulgaria is: " + a.getPopulationOfCountry("Bulgaria"));
        } catch(Exception exc){
            System.out.println(exc);
        }

        try{
            System.out.println("The population of Eastern Europe region is: " + a.getPopulationOfRegion("Eastern Europe"));
        } catch(Exception exc){
            System.out.println(exc);
        }

        try{
            System.out.println("The population of continent Asia is: " + a.getPopulationOfContinent("Asia"));
        } catch(Exception exc){
            System.out.println(exc);
        }

        try{
            System.out.println("The population of the world is: " + a.getWorldPopulation());
        }catch (Exception exc){
            System.out.println(exc);
        }
        try{
            System.out.println("People not living in cities for Country Bulgaria is: " + a.getNumberOfPeopleLivingInVillagesForCountry("Bulgaria") );
        } catch(Exception exc){
            System.out.println(exc);
        }
         */

        // Disconnect from database
        a.disconnect();
    }

    //Connection to MySQL database
    private Connection con = null;

    /**
     * Connects to the mysql jdbc driver
     */
    public void connect(String location) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {

            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location + "/world?allowPublicKeyRetrieval=true&useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                // Close connection
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    // ____________________
    // report methods:

    /**
     * Returns an integer for Population of a country
     *
     * @param countryName
     * @return
     * @throws IllegalArgumentException
     */
    public int getPopulationOfCountry(String countryName) throws IllegalArgumentException {
        if (countryName == null) {
            throw new IllegalArgumentException("Wrong parameter value type");
        }
        List<Country> allCountries = getAllCountries();
        int totalPopulation = 0;
        for (Country currCountry : allCountries) {
            if (currCountry.getName().equals(countryName)) {
                return currCountry.getPopulation();
            }
        }
        throw new IllegalArgumentException("Country not found");
    }

    /**
     * Returns an integer for the population of a district
     *
     * @param districtName
     * @return
     * @throws IllegalArgumentException
     */
    public int getPopulationOfDistrict(String districtName) throws IllegalArgumentException {
        if (districtName == null) {
            throw new IllegalArgumentException("Wrong parameter value type");
        }
        List<City> allCities = getAllCities();
        int totalPopulation = 0;
        for (City currCity : allCities) {
            if (currCity.getDistrict().equals(districtName)) {
                totalPopulation += currCity.getPopulation();

            }
        }
        return totalPopulation;
    }

    /**
     * Returns all of the Number of people living in villages for a specific country name:
     *
     * @param countryName
     * @return
     * @throws Exception
     */
    public long getNumberOfPeopleLivingInVillagesForCountry(String countryName) throws Exception {
        if (countryName == null) {
            throw new IllegalArgumentException("Wrong parameter value type");
        }
        List<City> allCities = getAllCities();
        List<Country> alLCountries = getAllCountries();
        Country country = null;
        for (Country currCountry : alLCountries) {
            if (currCountry.getName().equals(countryName)) {
                country = currCountry;
                break;
            }
        }

        if (country == null) {
            throw new Exception("Country not found");
        }

        long continentPopulation = 0;
        long peopleLivingInCities = 0;
        for (City currCity : allCities) {
            if (currCity.getCountry_code().equals(country.getISO3Code())) {
                peopleLivingInCities += currCity.getPopulation();
            }
        }
        return country.getPopulation() - peopleLivingInCities;
    }

    /**
     * Returns the world population;
     *
     * @return
     */
    public long getWorldPopulation() {
        List<Country> allCountries = getAllCountries();
        long worldPopulation = 0;
        for (Country currCountry : allCountries) {
            worldPopulation += currCountry.getPopulation();
        }
        return worldPopulation;
    }

    /**
     * Returns a long for the population for a continent;
     *
     * @param continentName
     * @return
     */
    public long getPopulationOfContinent(String continentName) {
        if (continentName == null) {
            throw new IllegalArgumentException("Wrong parameter value type");
        }
        List<Country> allCountries = getAllCountries();
        long continentPopulation = 0;
        for (Country currCountry : allCountries) {
            if (currCountry.getRegion().equals(continentName)) {
                continentPopulation += currCountry.getPopulation();
            }
        }
        throw new IllegalArgumentException("Country not found");
    }

    /**
     * Returns integer of the Population for a region;
     *
     * @param regionName
     * @return
     * @throws IllegalArgumentException
     */
    public int getPopulationOfRegion(String regionName) throws IllegalArgumentException {
        if (regionName == null) {
            throw new IllegalArgumentException("Wrong parameter value type");
        }
        List<Country> allCountries = getAllCountries();
        int regionPopulation = 0;
        for (Country currCountry : allCountries) {
            if (currCountry.getRegion().equals(regionName)) {
                regionPopulation += currCountry.getPopulation();
            }
        }
        throw new IllegalArgumentException("Country not found");
    }

    /**
     * All the cities from MySQL world database in the region of @param country organised by largest population
     *
     * @param country The region we aiming
     * @return ArrayList<City>
     */
    public List<City> getCitiesByLargestPopulationInCountry(Country country) {
        List<City> cities = new ArrayList<>();
        String countryCode = country.getISO3Code();
        try {

            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement
            String strSelect =
                    "SELECT * FROM city WHERE city.CountryCode = '" + countryCode + "' ORDER BY city.Population DESC;";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            while (rset.next()) {
                City curr_city = new City(rset.getInt("ID"), rset.getString("Name"), rset.getString("CountryCode"), rset.getString("District"), rset.getInt("Population"));
                cities.add(curr_city);
            }

        } catch (Exception e) {
            return null;
        }
        return cities;
    }

    /**
     * All countries from MySQL world database stored in ArrayList data structure.
     *
     * @return ArrayList<Country>
     */
    public List<Country> getAllCountries() {
        List<Country> countries = new ArrayList<>();
        try {

            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement
            String strSelect =
                    "SELECT * FROM country";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            while (rset.next()) {
                Country curr_country = new Country(rset.getString("Code"), rset.getString("Name"),
                        rset.getString("Continent"), rset.getString("Region"), rset.getDouble("SurfaceArea"),
                        rset.getInt("IndepYear"), rset.getInt("Population"), rset.getDouble("LifeExpectancy"),
                        rset.getDouble("GNP"), rset.getDouble("GNPOld"), rset.getString("LocalName"),
                        rset.getString("GovernmentForm"), rset.getString("HeadOfState"),
                        rset.getInt("Capital"), rset.getString("Code2"));
                countries.add(curr_country);
            }

        } catch (Exception e) {
            return null;
        }
        return countries;
    }

    /**
     * All cities from MySQL world database stored in ArrayList data structure.
     *
     * @return ArrayList<City>
     */
    public List<City> getAllCities() {
        List<City> cities = new ArrayList<>();
        try {

            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement
            String strSelect =
                    "SELECT * FROM city";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            while (rset.next()) {
                City curr_city = new City(rset.getInt("ID"), rset.getString("Name"), rset.getString("CountryCode"), rset.getString("District"), rset.getInt("Population"));
                cities.add(curr_city);
            }

        } catch (Exception e) {
            return null;
        }
        return cities;
    }

    /**
     * All languages from MySQL world database stored in ArrayList data structure.
     *
     * @return ArrayList<Language>
     */
    public List<Language> getAllLanguages() {
        List<Language> languages = new ArrayList<>();
        try {

            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement
            String strSelect =
                    "SELECT * FROM countrylanguage";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            while (rset.next()) {
                Language curr_language = new Language(rset.getString("CountryCode"), rset.getString("Language"), rset.getString("IsOfficial"), rset.getDouble("Percentage"));
                languages.add(curr_language);
                System.out.println(languages);
            }

        } catch (Exception e) {
            return null;
        }
        return languages;
    }

    // ____________________
    // app-integration tests:

    /**
     * Method GET Target City Data:
     *
     * @return City
     */
    public City getCity(int ID) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement
            String strSelect =
                    "SELECT ID, Name, CountryCode, District, Population "
                            + "FROM city "
                            + "WHERE ID = " + ID;

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            // Return new employee if valid.

            // Check one is returned
            if (rset.next()) {
                return new City(rset.getInt("ID"), rset.getString("Name"), rset.getString("CountryCode"), rset.getString("District"), rset.getInt("Population"));
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get target City");
            return null;
        }
    }

    /**
     * Method GET Target Country Data:
     * @param Code
     * @return
     */
    public Country getCountry(String Code) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement
            String strSelect =
                    "SELECT Code, Name, Continent, Region, SurfaceArea, IndepYear, Population, LifeExpectancy, GNP, GNPOld, LocalName, GovernmentForm, HeadOfState, Capital, Code2"
                            + " FROM country"
                            + " WHERE Code = " + "\"" + Code + "\"";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            // Return new employee if valid.

            // Check one is returned
            if (rset.next()) {
                return new Country(rset.getString("Code"),
                        rset.getString("Name"),
                        rset.getString("Continent"),
                        rset.getString("Region"),
                        rset.getDouble("SurfaceArea"),
                        rset.getInt("IndepYear"),
                        rset.getInt("Population"),
                        rset.getDouble("LifeExpectancy"),
                        rset.getDouble("GNP"),
                        rset.getDouble("GNPOld"),
                        rset.getString("LocalName"),
                        rset.getString("GovernmentForm"),
                        rset.getString("HeadOfState"),
                        rset.getInt("Capital"),
                        rset.getString("Code2"));
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get target Country");
            return null;
        }
    }

    /**
     * Method GET Target Country Language Data:
     *
     * @param countryCode  The ISO3 Code of the Country
     * @param languageName The target language
     * @return Language
     */
    public Language getCountryLanguage(String countryCode, String languageName) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement
            String strSelect =
                    "SELECT CountryCode, Language, IsOfficial, Percentage " +
                            "FROM countrylanguage " +
                            "WHERE CountryCode = " + "\"" + countryCode + "\"" +
                            " AND Language = " + "\"" + languageName + "\"";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            // Return new employee if valid.

            // Check one is returned
            if (rset.next()) {
                return new Language(rset.getString("CountryCode"), rset.getString("Language"), rset.getString("IsOfficial"), rset.getDouble("Percentage"));
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get target Country Language");
            return null;
        }
    }

    // ____________________
    // visual-display methods:

    /**
     * Method for Displaying Target Data
     *
     * @param city The city to be displayed
     */
    public void displayCity(City city) {
        if (city != null) {
            System.out.println(city.toString());
        }
    }
}