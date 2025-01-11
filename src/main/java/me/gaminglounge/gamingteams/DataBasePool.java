package me.gaminglounge.gamingteams;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.UUID;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataBasePool {
    HikariDataSource hikari;

    public void init() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("db-hikari");
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl(Gamingteams.CONFIG.getString("Database"));
        config.setUsername(Gamingteams.CONFIG.getString("Username"));
        config.setPassword(Gamingteams.CONFIG.getString("Password"));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(1);
        config.setMaxLifetime(0);
        config.setKeepaliveTime(120000);
        config.setConnectionTimeout(30000);

        this.hikari = new HikariDataSource(config);
        deregisterDriver("org.mariadb.jdbc.Driver");
    }

    public void shutdown() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    private static void deregisterDriver(String driverClassName) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals(driverClassName)) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.hikari == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }

        Connection connection = this.hikari.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }

    public void createTableTeams() throws SQLException {
        Connection con = getConnection();
        String sqlCreate = "CREATE TABLE IF NOT EXISTS teams (" +
            "id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL," +
            "name VARCHAR(128) NOT NULL," +
            "owner UUID NOT NULL," +
            "tag VARCHAR(16) NOT NULL)" +
            "ENGINE = InnoDB;";

        Statement stmt = con.createStatement();
        stmt.execute(sqlCreate);
    }

    public void createTablePlayer() throws SQLException {
        Connection con = getConnection();
        String sqlCreate = "CREATE TABLE IF NOT EXISTS player (" +
            "id INTEGER NOT NULL," +
            "player UUID NOT NULL)" +
            "ENGINE = InnoDB;";

        Statement stmt = con.createStatement();
        stmt.execute(sqlCreate);
    }

        public static void addTeam(DataBasePool pool, UUID owner, String name, String tag) {
        String querry = "INSERT INTO `teams` (`owner`, `name`, `tag`) VALUES (?, ?, ?);";
        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, owner);
            sel.setObject(2, name);
            sel.setObject(3, tag);
            sel.executeQuery();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean removeTeam(DataBasePool pool, int team, UUID owner) {
        String querry = "DELETE FROM `teams` WHERE teams.id = ? AND teams.owner = ?;";
        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, team);
            sel.setObject(2, owner);
            ResultSet res = sel.executeQuery();
            if (!res.first()) {
                return false;
            }
            sel.close();
            con.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setOwner(DataBasePool pool, UUID owner) {
        String querry = "INSERT INTO `teams` (`owner`) VALUES (?);";
        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, owner);
            sel.executeQuery();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setName(DataBasePool pool, String name) {
        String querry = "INSERT INTO `teams` (`name`) VALUES (?);";
        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, name);
            sel.executeQuery();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setTag(DataBasePool pool, String tag) {
        String querry = "INSERT INTO `teams` (`tag`) VALUES (?);";
        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, tag);
            sel.executeQuery();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPlayerToTeam(DataBasePool pool, int team, UUID playerUUID) {
        String querry = "INSERT INTO `player` (`id`, `player`) VALUES (?, ?);";
        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, team);
            sel.setObject(2, playerUUID);
            sel.executeQuery();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePlayerToTeam(DataBasePool pool, int team, UUID playerUUID) {
        String querry = "DELETE FROM `player` WHERE player.id = ? AND player.player = ?;";
        try {
            Connection con = pool.getConnection();
            PreparedStatement sel = con.prepareStatement(querry);
            sel.setObject(1, team);
            sel.setObject(2, playerUUID);
            sel.executeQuery();
            sel.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
