package com.arcblaze.arctime.db.util;

import com.arcblaze.arctime.db.ArcTimeDaoFactory;

/**
 * Used to initialize a simple in-memory database for testing purposes.
 */
public class TestDatabase extends com.arcblaze.arccore.db.util.TestDatabase {
	/**
	 * @return a {@link ArcTimeDaoFactory} connected to this test database
	 */
	@Override
	public ArcTimeDaoFactory getDaoFactory() {
		return new ArcTimeDaoFactory(getConnectionManager());
	}
}
