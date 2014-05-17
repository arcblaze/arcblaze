package com.arcblaze.arccore.server.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.common.model.Password;
import com.arcblaze.arccore.common.model.Role;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.RoleDao;
import com.arcblaze.arccore.db.dao.UserDao;

/**
 * Provides an implementation of the security realm used to authenticate users in the system.
 */
public class SecurityRealm extends RealmBase {
    /** This will be used to log messages. */
    private final static Logger log = LoggerFactory.getLogger(SecurityRealm.class);

    /** The configured name of this realm. */
    private final String realmName;

    /** Used to retrieve user and role information from the database. */
    private final DaoFactory daoFactory;

    /** Used to perform password hashing. */
    private final Password password = new Password();

    /** The user object that has been retrieved from the database. */
    private User user;

    /**
     * @param realmName
     *            the name of the realm
     * @param daoFactory
     *            used to retrieve user and role information from the database
     */
    public SecurityRealm(final String realmName, final DaoFactory daoFactory) {
        this.realmName = realmName;
        this.daoFactory = daoFactory;
        setDigest(Password.HASH_ALGORITHM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.realmName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getPassword(final String username) {
        final UserDao dao = this.daoFactory.getUserDao();
        try {
            this.user = dao.getLogin(username);
            if (this.user == null)
                return null;

            return this.user.getHashedPass();
        } catch (final DatabaseException databaseException) {
            log.error("Failed to retrieve user password.", databaseException);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Principal getPrincipal(final String username) {
        if (this.user != null) {
            final RoleDao dao = this.daoFactory.getRoleDao();
            try {
                final Set<Role> roles = dao.get(this.user.getId());
                List<String> roleNames = new ArrayList<>(roles.size() + 1);
                roleNames.add("USER"); // Everyone gets the USER role.
                for (final Role role : roles)
                    roleNames.add(role.getName());
                this.user.setRoles(roles);

                // Note that we use the user's login in the principal in case
                // they logged in with their email.
                return new GenericPrincipal(this.user.getLogin(), this.user.getHashedPass(), roleNames, this.user);
            } catch (final DatabaseException databaseException) {
                log.error("Failed to retrieve user roles.", databaseException);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String digest(String credentials) {
        return this.password.hash(credentials, this.user.getSalt());
    }
}
