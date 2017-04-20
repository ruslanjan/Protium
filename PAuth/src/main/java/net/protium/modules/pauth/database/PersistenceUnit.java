/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.database;
/*
In net.protium.modules.pauth.database
From temporary-protium
*/

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class PersistenceUnit implements PersistenceUnitInfo {
	@Override
	public String getPersistenceUnitName( ) {
		return "PAuth_Default";
	}

	@Override
	public String getPersistenceProviderClassName( ) {
		return "org.hibernate.jpa.HibernatePersistenceProvider";
	}

	@Override
	public PersistenceUnitTransactionType getTransactionType( ) {
		return PersistenceUnitTransactionType.RESOURCE_LOCAL;
	}

	@Override
	public DataSource getJtaDataSource( ) {
		return null;
	}

	@Override
	public DataSource getNonJtaDataSource( ) {
		return null;
	}

	@Override
	public List < String > getMappingFileNames( ) {
		return Collections.emptyList();
	}

	@Override
	public List < URL > getJarFileUrls( ) {
		try {
			return Collections.list(this.getClass()
				.getClassLoader()
				.getResources(""));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public URL getPersistenceUnitRootUrl( ) {
		return null;
	}

	@Override
	public List < String > getManagedClassNames( ) {
		return Collections.emptyList();
	}

	@Override
	public boolean excludeUnlistedClasses( ) {
		return false;
	}

	@Override
	public SharedCacheMode getSharedCacheMode( ) {
		return null;
	}

	@Override
	public ValidationMode getValidationMode( ) {
		return null;
	}

	@Override
	public Properties getProperties( ) {
		Properties props = new Properties();
		props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
		props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
		props.put("hibernate.hbm2ddl.auto", "update");

		return props;
	}

	@Override
	public String getPersistenceXMLSchemaVersion( ) {
		return null;
	}

	@Override
	public ClassLoader getClassLoader( ) {
		return null;
	}

	@Override
	public void addTransformer(ClassTransformer transformer) {

	}

	@Override
	public ClassLoader getNewTempClassLoader( ) {
		return null;
	}
}
