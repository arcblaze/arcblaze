package com.arcblaze.arccore.db;

import java.sql.SQLException;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.db.dao.CompanyActivityDao;
import com.arcblaze.arccore.db.dao.CompanyDao;
import com.arcblaze.arccore.db.dao.RoleDao;
import com.arcblaze.arccore.db.dao.TransactionDao;
import com.arcblaze.arccore.db.dao.UserActivityDao;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcCompanyActivityDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcCompanyDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcRoleDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcTransactionDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcUserActivityDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcUserDao;

/**
 * Used to retrieve DAO instances to work with the configured back-end database.
 */
public class DaoFactory {
    /** Holds the type of back-end data store used in the cached DAOs. */
    private final DatabaseType databaseType;

    /** Used to generate connections for the JDBC database type. */
    private final ConnectionManager connectionManager;

    private CompanyDao cachedCompanyDao = null;
    private UserDao cachedUserDao = null;
    private RoleDao cachedRoleDao = null;
    private TransactionDao cachedTransactionDao = null;
    private CompanyActivityDao cachedCompanyActivityDao = null;
    private UserActivityDao cachedUserActivityDao = null;

    /**
     * @param config
     *            the system configuration information
     */
    public DaoFactory(final Config config) {
        this.databaseType = DatabaseType.parse(config.getString(DatabaseProperty.DB_TYPE));

        if (DatabaseType.JDBC.equals(this.databaseType))
            this.connectionManager = new ConnectionManager(config);
        else
            this.connectionManager = null;
    }

    /**
     * @param connectionManager
     *            a preconfigured connection manager
     */
    public DaoFactory(final ConnectionManager connectionManager) {
        this.databaseType = DatabaseType.JDBC;
        this.connectionManager = connectionManager;
    }

    /**
     * @return the type of database configured with this system
     */
    public DatabaseType getDatabaseType() {
        return this.databaseType;
    }

    /**
     * @return the internal connection manager used to access the database
     */
    public ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    /**
     * @return a {@link CompanyDao} based on the currently configured database
     */
    public CompanyDao getCompanyDao() {
        if (this.cachedCompanyDao == null) {
            if (DatabaseType.JDBC.equals(this.databaseType))
                this.cachedCompanyDao = new JdbcCompanyDao(this.connectionManager);
            else
                throw new RuntimeException("Invalid database type: " + this.databaseType);
        }

        return this.cachedCompanyDao;
    }

    /**
     * @return a {@link UserDao} based on the currently configured database
     */
    public UserDao getUserDao() {
        if (this.cachedUserDao == null) {
            if (DatabaseType.JDBC.equals(this.databaseType))
                this.cachedUserDao = new JdbcUserDao(this.connectionManager);
            else
                throw new RuntimeException("Invalid database type: " + this.databaseType);
        }

        return this.cachedUserDao;
    }

    /**
     * @return a {@link RoleDao} based on the currently configured database
     */
    public RoleDao getRoleDao() {
        if (this.cachedRoleDao == null) {
            if (DatabaseType.JDBC.equals(this.databaseType))
                this.cachedRoleDao = new JdbcRoleDao(this.connectionManager);
            else
                throw new RuntimeException("Invalid database type: " + this.databaseType);
        }

        return this.cachedRoleDao;
    }

    /**
     * @return a {@link TransactionDao} based on the currently configured database
     */
    public TransactionDao getTransactionDao() {
        if (this.cachedTransactionDao == null) {
            if (DatabaseType.JDBC.equals(getDatabaseType()))
                this.cachedTransactionDao = new JdbcTransactionDao(getConnectionManager());
            else
                throw new RuntimeException("Invalid database type: " + getDatabaseType());
        }

        return this.cachedTransactionDao;
    }

    /**
     * @return a {@link CompanyActivityDao} based on the currently configured database
     */
    public CompanyActivityDao getCompanyActivityDao() {
        if (this.cachedCompanyActivityDao == null) {
            if (DatabaseType.JDBC.equals(getDatabaseType()))
                this.cachedCompanyActivityDao = new JdbcCompanyActivityDao(getConnectionManager());
            else
                throw new RuntimeException("Invalid database type: " + getDatabaseType());
        }

        return this.cachedCompanyActivityDao;
    }

    /**
     * @return a {@link UserActivityDao} based on the currently configured database
     */
    public UserActivityDao getUserActivityDao() {
        if (this.cachedUserActivityDao == null) {
            if (DatabaseType.JDBC.equals(getDatabaseType()))
                this.cachedUserActivityDao = new JdbcUserActivityDao(getConnectionManager());
            else
                throw new RuntimeException("Invalid database type: " + getDatabaseType());
        }

        return this.cachedUserActivityDao;
    }

    /**
     * Close any resources associated with the internal DAOs.
     * 
     * @throws DatabaseException
     *             if there is a problem closing resources
     */
    public void close() throws DatabaseException {
        if (this.connectionManager != null) {
            try {
                this.connectionManager.close();
            } catch (final SQLException sqlException) {
                throw new DatabaseException(sqlException);
            }
        }
        this.cachedCompanyDao = null;
        this.cachedUserDao = null;
        this.cachedRoleDao = null;
        this.cachedTransactionDao = null;
        this.cachedCompanyActivityDao = null;
        this.cachedUserActivityDao = null;
    }
}
