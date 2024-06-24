package de.sakros.civilization.utility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class MySQLUtility {
        private static final Pattern UUID_FIX = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

        public static ResultSet GetDataFromTable(String query){
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;

            try{
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            } catch (Exception exception){
                System.out.println(exception);
            }

            try{
                connection = DriverManager.getConnection("jdbc:mysql://45.81.234.118:3306/db_civilization?user=db_civilization&password=BG3A38xHD&autoReconnect=true&useSSL=false");
                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);

            } catch (SQLException error){
                System.out.println(error);
                System.out.println("SQLException: " + error.getMessage());
                System.out.println("SQLState: " + error.getSQLState());
            }

            return resultSet;
        }

        public static List<UUID> GetMinecraftUsers(){
            List<UUID> result = new ArrayList<>();

            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;

            try{
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            } catch (Exception exception){
                System.out.println(exception);
            }

            try{
                connection = DriverManager.getConnection("jdbc:mysql://45.81.234.118:3306/db_civilization?user=db_civilization&password=BG3A38xHD&autoReconnect=true&useSSL=false");
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT * FROM users");

                while (resultSet.next()) {
                    result.add(UUID.fromString(UUID_FIX.matcher(resultSet.getString(2).replace("-", "")).replaceAll("$1-$2-$3-$4-$5")));
                }

            } catch (SQLException error){
                System.out.println(error);
                System.out.println("SQLException: " + error.getMessage());
                System.out.println("SQLState: " + error.getSQLState());
            }

            return result;
        }
}
