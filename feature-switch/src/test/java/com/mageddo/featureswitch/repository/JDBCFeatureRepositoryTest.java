package com.mageddo.featureswitch.repository;

import com.mageddo.featureswitch.*;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JDBCFeatureRepositoryTest {

	private static JDBCDataSource dataSource;
	private static JDBCFeatureRepository jdbcFeatureRepository;

	@BeforeClass
	public static void beforeClass() {
		dataSource = new JDBCDataSource();
		dataSource.setUser("sa");
		dataSource.setPassword("");
		dataSource.setURL("jdbc:hsqldb:mem:testdb;set schema public");
				jdbcFeatureRepository = new JDBCFeatureRepository(dataSource);
	}

	@Before
	public void before() throws SQLException {
		try(final Connection conn = dataSource.getConnection()){

			conn.prepareStatement("DROP SCHEMA PUBLIC CASCADE").executeUpdate();

			StringBuilder sql = new StringBuilder()
				.append("CREATE TABLE USER_PARAMETER ( \n")
				.append("	IDT_USER_PARAMETER INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE) PRIMARY KEY, \n")
				.append("	IDT_PARAMETER INTEGER NOT NULL, \n")
				.append("	COD_USER VARCHAR(255) NOT NULL, \n")
				.append("	VAL_PARAMETER VARCHAR(1000), \n")
				.append("	DAT_CREATION TIMESTAMP NOT NULL, \n")
				.append("	DAT_UPDATE TIMESTAMP NOT NULL, \n")
				.append("	UNIQUE(IDT_PARAMETER, COD_USER) \n")
				.append("); \n");
			conn.prepareStatement(sql.toString()).executeUpdate();

			sql = new StringBuilder()
				.append(" \n")
				.append("CREATE TABLE PARAMETER( \n")
				.append("	IDT_PARAMETER INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 CYCLE) PRIMARY KEY, \n")
				.append("	NAM_PARAMETER VARCHAR(255) NOT NULL, \n")
				.append("	VAL_PARAMETER VARCHAR(1000), \n")
				.append("	DAT_CREATION TIMESTAMP NOT NULL, \n")
				.append("	DAT_UPDATE TIMESTAMP NOT NULL, \n")
				.append("	UNIQUE(NAM_PARAMETER) \n")
				.append("); \n");
			conn.prepareStatement(sql.toString()).executeUpdate();
		}
	}

	@Test
	public void shouldNotReturnFeatureCauseItDoesntExists() {
		final FeatureMetadata metadata = jdbcFeatureRepository.getMetadata(MyFeatures.FEATURE_ONE, null);
		assertNull(metadata);
	}

	@Test
	public void shouldInsertAndFindFeature() {

		final String expectedStatus = "1";
		final String expectedValue = "123";

		final Map<String, String> m = new HashMap<>();
		m.put(FeatureKeys.STATUS, expectedStatus);
		m.put(FeatureKeys.VALUE, expectedValue);

		jdbcFeatureRepository.updateMetadata(new DefaultFeatureMetadata(MyFeatures.FEATURE_ONE, m), null);
		jdbcFeatureRepository.updateMetadata(new DefaultFeatureMetadata(MyFeatures.FEATURE_ONE, m), null);

		final FeatureMetadata metadata = jdbcFeatureRepository.getMetadata(MyFeatures.FEATURE_ONE, null);
		assertEquals(expectedStatus, metadata.get(FeatureKeys.STATUS));
		assertEquals(expectedValue, metadata.get(FeatureKeys.VALUE));
		assertEquals(Status.fromCode(expectedStatus), metadata.status());

	}

	@Test
	public void shouldInsertUpdateAndFindUpdatedFeature() {

		final String expectedStatus = "1";
		final String expectedValue = "123";

		final Map<String, String> m = new HashMap<>();
		m.put(FeatureKeys.STATUS, expectedStatus);
		m.put(FeatureKeys.VALUE, expectedValue);

		jdbcFeatureRepository.updateMetadata(new DefaultFeatureMetadata(MyFeatures.FEATURE_ONE, m), null);

		m.put(FeatureKeys.VALUE, "321");
		jdbcFeatureRepository.updateMetadata(new DefaultFeatureMetadata(MyFeatures.FEATURE_ONE, m), null);

		final FeatureMetadata metadata = jdbcFeatureRepository.getMetadata(MyFeatures.FEATURE_ONE, null);
		assertEquals(expectedStatus, metadata.get(FeatureKeys.STATUS));
		assertEquals("321", metadata.get(FeatureKeys.VALUE));
		assertEquals(Status.fromCode(expectedStatus), metadata.status());

	}

	@Test
	public void shouldInsertUpdateAndFindUpdatedUserFeature() {

		final String expectedUser = "someone";
		final String expectedStatus = "1";
		final String expectedValue = "123";

		final Map<String, String> m = new HashMap<>();
		m.put(FeatureKeys.STATUS, expectedStatus);
		m.put(FeatureKeys.VALUE, expectedValue);

		jdbcFeatureRepository.updateMetadata(new DefaultFeatureMetadata(MyFeatures.FEATURE_ONE, m), expectedUser);

		m.put(FeatureKeys.VALUE, "321");
		jdbcFeatureRepository.updateMetadata(new DefaultFeatureMetadata(MyFeatures.FEATURE_ONE, m), expectedUser);

		final FeatureMetadata metadata = jdbcFeatureRepository.getMetadata(MyFeatures.FEATURE_ONE, expectedUser);
		assertEquals(expectedStatus, metadata.get(FeatureKeys.STATUS));
		assertEquals("321", metadata.get(FeatureKeys.VALUE));
		assertEquals(Status.fromCode(expectedStatus), metadata.status());
	}

	@Test
	public void shouldInsertAsRestrictedAndActivateForUser(){
		// arrange
		final String expectedValue = "999";
		final String expectedValue2 = "100";
		final String userId = "46546";
		final String user2Id = "46547";
		final MyFeatures feature = MyFeatures.RESTRICTED_FEATURE;
		final DefaultFeatureManager featureManager = new DefaultFeatureManager()
			.featureRepository(jdbcFeatureRepository)
			.featureMetadataProvider(new EnumFeatureMetadataProvider())
			;

		// act
		featureManager.updateMetadata(feature, userId, Map.of(FeatureKeys.VALUE, expectedValue));
		featureManager.updateMetadata(feature, user2Id, Map.of(FeatureKeys.VALUE, expectedValue2));

		// assert
		assertEquals(expectedValue, featureManager.metadata(feature, userId).value());
		assertEquals(expectedValue2, featureManager.metadata(feature, user2Id).value());
		assertEquals("", featureManager.metadata(feature, "notExistentUser").value());
		assertFalse(featureManager.isActive(feature));
	}

	enum MyFeatures implements InteractiveFeature {

		FEATURE_ONE,

		@FeatureDefaults(status = Status.RESTRICTED)
		RESTRICTED_FEATURE,
		;

		@Override
		public FeatureManager manager() {
			return new DefaultFeatureManager()
				.featureMetadataProvider(new EnumFeatureMetadataProvider())
				.featureRepository(jdbcFeatureRepository)
			;
		}
	}
}
