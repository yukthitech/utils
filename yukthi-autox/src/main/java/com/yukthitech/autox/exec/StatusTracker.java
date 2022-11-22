package com.yukthitech.autox.exec;

import com.yukthitech.autox.test.TestStatus;

public class StatusTracker
{
	int errorCount = 0;
	int failureCount = 0;
	int skipCount = 0;
	
	TestStatus status;
}
