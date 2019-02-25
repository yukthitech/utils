package com.yukthitech.autox.storage;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;

/**
 * The Class ExecutionInfoEntity.
 */
@Table(name = "AUTOX_EXECUTION_INFO")
public class ExecutionInfoEntity
{
	/**
	 * Primary key.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Name of test suite.
	 */
	@Column(name = "TEST_SUITE", length = 1000, nullable = false)
	private String testSuite;

	/**
	 * Name of test suite.
	 */
	@Column(name = "TEST_CASE", length = 1000, nullable = false)
	private String testCase;

	/**
	 * Flag indicating if execution is successful.
	 */
	@Column(name = "IS_SUCCESSFUL")
	private boolean successful;
	
	/**
	 * Time taken for execution.
	 */
	@Column(name = "TIME_TAKEN")
	private long timeTaken;
	
	/**
	 * Time at which the execution is started.
	 */
	@Column(name = "STARTED_ON")
	private Date startedOn = new Date();
	
	/**
	 * In case of error, high level reason of error/failure.
	 */
	@Column(name = "ERROR_MESSAGE")
	@DataTypeMapping(type = DataType.CLOB)
	private String errorMessage;
	
	/**
	 * Instantiates a new execution info entity.
	 */
	public ExecutionInfoEntity()
	{}
	
	/**
	 * Instantiates a new execution info entity.
	 *
	 * @param testSuite the test suite
	 * @param testCase the test case
	 */
	public ExecutionInfoEntity(String testSuite, String testCase)
	{
		this.testSuite = testSuite;
		this.testCase = testCase;
	}

	/**
	 * Gets the primary key.
	 *
	 * @return the primary key
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the primary key.
	 *
	 * @param id the new primary key
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the name of test suite.
	 *
	 * @return the name of test suite
	 */
	public String getTestSuite()
	{
		return testSuite;
	}

	/**
	 * Sets the name of test suite.
	 *
	 * @param testSuite the new name of test suite
	 */
	public void setTestSuite(String testSuite)
	{
		this.testSuite = testSuite;
	}

	/**
	 * Gets the name of test suite.
	 *
	 * @return the name of test suite
	 */
	public String getTestCase()
	{
		return testCase;
	}

	/**
	 * Sets the name of test suite.
	 *
	 * @param testCase the new name of test suite
	 */
	public void setTestCase(String testCase)
	{
		this.testCase = testCase;
	}

	/**
	 * Gets the flag indicating if execution is successful.
	 *
	 * @return the flag indicating if execution is successful
	 */
	public boolean isSuccessful()
	{
		return successful;
	}

	/**
	 * Sets the flag indicating if execution is successful.
	 *
	 * @param successful the new flag indicating if execution is successful
	 */
	public void setSuccessful(boolean successful)
	{
		this.successful = successful;
	}

	/**
	 * Gets the time taken for execution.
	 *
	 * @return the time taken for execution
	 */
	public long getTimeTaken()
	{
		return timeTaken;
	}

	/**
	 * Sets the time taken for execution.
	 *
	 * @param timeTaken the new time taken for execution
	 */
	public void setTimeTaken(long timeTaken)
	{
		this.timeTaken = timeTaken;
	}

	/**
	 * Gets the time at which the execution is started.
	 *
	 * @return the time at which the execution is started
	 */
	public Date getStartedOn()
	{
		return startedOn;
	}

	/**
	 * Sets the time at which the execution is started.
	 *
	 * @param startedOn the new time at which the execution is started
	 */
	public void setStartedOn(Date startedOn)
	{
		this.startedOn = startedOn;
	}

	/**
	 * Gets the in case of error, high level reason of error/failure.
	 *
	 * @return the in case of error, high level reason of error/failure
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}

	/**
	 * Sets the in case of error, high level reason of error/failure.
	 *
	 * @param errorMessage the new in case of error, high level reason of error/failure
	 */
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
}
