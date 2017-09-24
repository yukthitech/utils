package com.yukthitech.autox.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.config.ApplicationConfiguration;

/**
 * Persistence storage used to store automation related data.
 * @author akiran
 */
public class PersistenceStorage
{
	private static Logger logger = LogManager.getLogger(PersistenceStorage.class);
	
	/**
	 * Derby driver class.
	 */
	public static final String DERBY_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	
	/**
	 * Query to create data table.
	 */
	public static final String CREATE_TABLE_QUERY = "CREATE TABLE DATA_TABLE (DATA_KEY VARCHAR(100) PRIMARY KEY, DATA_VALUE BLOB NOT NULL)";
	
	/**
	 * Query to check existence of a key.
	 */
	public static final String COUNT_QUERY = "SELECT COUNT(*) FROM DATA_TABLE WHERE DATA_KEY = ?";
	
	/**
	 * Query to insert key value pair.
	 */
	public static final String INSERT_QUERY = "INSERT INTO DATA_TABLE(DATA_VALUE, DATA_KEY) VALUES (?, ?)";

	/**
	 * Query to UPDATE key value pair.
	 */
	public static final String UPDATE_QUERY = "UPDATE DATA_TABLE SET DATA_VALUE = ? WHERE DATA_KEY = ?";

	/**
	 * Query to fetch value for specified key.
	 */
	public static final String FETCH_QUERY = "SELECT DATA_VALUE FROM DATA_TABLE WHERE DATA_KEY = ?";

	/**
	 * Connection to persistence storage.
	 */
	private Connection connection;
	
	public PersistenceStorage(ApplicationConfiguration config)
	{
		try
		{
			Class.forName(DERBY_DRIVER);
			connection = DriverManager.getConnection("jdbc:derby:" + config.getDataFolder() + ";create=true");
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating initializing persistence storage", ex);
		}

		try
		{
			logger.debug("Trying to create data table for persistence storage.");
			Statement statement = connection.createStatement();
			statement.execute(CREATE_TABLE_QUERY);
			
			statement.close();
		} catch(Exception ex)
		{
			logger.warn("An error occurred while creating data-table. Ignoring this and assuming the table already exist. Actual error: " + ex);
		}
	}
	
	/**
	 * Closes the specified db resources.
	 * @param rs
	 * @param stmt
	 */
	private void close(ResultSet rs, Statement stmt)
	{
		try
		{
			if(rs != null)
			{
				rs.close();
			}
			
			if(stmt != null)
			{
				stmt.close();
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Checks if specified key already existing in storage.
	 * @param key
	 * @return
	 */
	public boolean isExistingKey(String key)
	{
		PreparedStatement statement = null;
		ResultSet rs = null;

		try
		{
			statement = connection.prepareStatement(COUNT_QUERY);
			statement.setString(1, key);
			
			rs = statement.executeQuery();
			
			if(!rs.next())
			{
				return false;
			}
			
			int count = rs.getInt(1);
			return count > 0;
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while checking existence of the key: " + key, ex);
		} finally
		{
			close(rs, statement);
		}
	}
	
	/**
	 * Converts specified data into byte array.
	 * @param data
	 * @return
	 */
	private byte[] toBytes(Object data)
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(data);
			oos.flush();
			
			return bos.toByteArray();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while converting data object to byte array", ex);
		}
	}
	
	/**
	 * Sets the specified key-value into persistence storage.
	 * @param key Key to be used
	 * @param value value to set
	 */
	public void set(String key, Object value)
	{
		try
		{
			String query = isExistingKey(key) ? UPDATE_QUERY : INSERT_QUERY;
			
			logger.debug("Persisting specified key-value pair using query: {}", query);
			
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setBytes(1, toBytes(value));
			statement.setString(2, key);

			if(statement.executeUpdate() <= 0)
			{
				throw new IllegalStateException("Failed to persist specified value with key: " + key);
			}

			statement.close();
		} catch(SQLException ex)
		{
			throw new IllegalStateException("An error occurred while persisting value with specified key: " + key, ex);
		}
	}
	
	/**
	 * Used to fetch value for specified key.
	 * @param key
	 * @return
	 */
	public Object get(String key)
	{
		PreparedStatement statement = null;
		ResultSet rs = null;

		try
		{
			statement = connection.prepareStatement(FETCH_QUERY);
			statement.setString(1, key);
			
			rs = statement.executeQuery();
			
			if(!rs.next())
			{
				return null;
			}
			
			byte data[] = rs.getBytes(1);
			
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bis);
			
			return ois.readObject();
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while fetching value for key: " + key, ex);
		} finally
		{
			close(rs, statement);
		}
	}
}
