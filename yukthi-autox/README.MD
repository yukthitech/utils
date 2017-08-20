## Launching the Automation Suite(s)

The automation can be launched using java class "com.yukthitech.automation.AutomationLauncher" using following command

**java com.yukthitech.automation.AutomationLauncher < app-config-file > < report-folder >**
	
**app-config-file** - Should point to a xml file which provides configuration about the application needed for automation. This file can have following information:

1. **basePackages** - Base java packages to be scanned for steps and validators. By default ***com.yukthitech*** package gets scanned.
2. **testSuiteFolder** - Test suite folder where test suite xmls (for automation) is located. Can be relative path from current folder.
3. **seleniumConfiguration [optional]** - Selenium configuration required by selenium based steps or validators.
4. **dbConfiguration [optional]** - DB configuration required by SQL based steps or validators.
5. **configuration [optional]** - Custom configurations required by custom steps or validators.

**report-folder** - Folder (new) output test reports will be created


## Test Suites

Test suite xml(s) are loaded from the test-suite folder specified in app-config-file recursivly. Non-xml files are ignored.

Only after loading all the files, test-suite dependencies are validated and gets ordered. So order of loading files does not matter.

A test-suite is composed of the following:

1. **name** - Name of the test suite. Displayed in test report and also to specify dependencies.
2. **description [optional]** - Description about the test suite. Included in output report
2. **dependencies [optional]** - Comma separated dependency list.
3. **dataBeans [optional]** - Common data bean definitions that can be used in test cases.
4. **testCases** - Test cases to be executed as part of this test suite.


## Test Cases

A test-suite is composed of test-cases. Test cases are executed in the order they are defined in the xml.

Test case is composed of following:

1. **name** - Name of the test cases. Used in the output report.
2. **description [optional]** - Description about the test case.
3. **steps [optional]** - List of steps to be executed as part of this test case. A step is an implementation of interface ***com.yukthitech.automation.IStep***.
4. **validations** - List of validations to be executed after all steps are executed successfully. Validation is implementation of interface ***com.yukthitech.automation.IValidation***. Validations are mandatory for a test-case.





