package com.yukthitech.autox.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.config.SummaryNotificationConfig;
import com.yukthitech.autox.logmon.LogMonitorContext;
import com.yukthitech.autox.test.log.ExecutionLogData;
import com.yukthitech.utils.exceptions.InvalidStateException;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Responsible for generating output reports.
 * 
 * @author akiran
 */
public class ReportGenerator
{
	private static final String SUMMARY_REPORT_TEMPLATE = "/summary-report-template.html";

	private static Logger logger = LogManager.getLogger(ReportGenerator.class);

	/**
	 * Free marker configuration.
	 */
	private static Configuration freemarkerConfiguration = new Configuration(Configuration.getVersion());

	/**
	 * The log json file extension.
	 **/
	private static String LOG_JSON = "_log.json";
	
	/**
	 * log js file extension.
	 */
	private static String LOG_JS = ".js";

	/**
	 * Used to generate json files.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();

	public void generateReports(File reportFolder, FullExecutionDetails fullExecutionDetails, AutomationContext automationContext)
	{
		ApplicationConfiguration applicationConfiguration = automationContext.getAppConfiguration();
		
		List<String> summaryMessages = automationContext.getSummaryMessages();
		
		if(summaryMessages != null && !summaryMessages.isEmpty())
		{
			fullExecutionDetails.setSummaryMessages(automationContext.getSummaryMessages());
		}
		
		// copy the resource files into output folder
		ResourceManager.getInstance().copyReportResources(reportFolder);

		// create final report files
		try
		{
			String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fullExecutionDetails);
			String jsContent = "var reportData = " + jsonContent;

			FileUtils.write(new File(reportFolder, "test-results.json"), jsonContent);
			FileUtils.write(new File(reportFolder, "test-results.js"), jsContent);
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while generating test result report");
		}

		// generate summary report
		try
		{
			Map<String, Object> context = new HashMap<>();
			context.put("report", fullExecutionDetails);
			context.put("reportFolder", reportFolder.getPath());
			context.put("reportFolderName", reportFolder.getName());
			context.put("context", automationContext);
			
			SummaryNotificationConfig summaryNotificationConfig = applicationConfiguration.getSummaryNotificationConfig();
			summaryNotificationConfig = (summaryNotificationConfig != null && summaryNotificationConfig.isEnabled()) ? summaryNotificationConfig : null;
			
			//generate header and footer content and place in context which in turn will be used in summary report template
			if(summaryNotificationConfig != null)
			{
				generateHeaderAndFooter(context, summaryNotificationConfig);
			}

			String reportTemplate = IOUtils.toString(ReportGenerator.class.getResourceAsStream(SUMMARY_REPORT_TEMPLATE));
			String summaryResult = FreeMarkerMethodManager.replaceExpressions("reportTemplate", context, reportTemplate);

			File summaryHtml = new File(reportFolder, "summary-report.html");
			FileUtils.write(summaryHtml, summaryResult);

			//send notification mail
			if(summaryNotificationConfig != null)
			{
				sendSummaryMail(summaryNotificationConfig, summaryHtml, context);
			}
			else
			{
				logger.debug("Summary mail is disabled. Hence skipping sending summary mail..");
			}
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while generating summary report");
		}
		
	}
	
	private void generateHeaderAndFooter(Map<String, Object> context, SummaryNotificationConfig notificationConfig) throws IOException
	{
		File headerFile = notificationConfig.getHeaderTemplateFile() != null ? new File(notificationConfig.getHeaderTemplateFile()) : null;
		File footerFile = notificationConfig.getHeaderTemplateFile() != null ? new File(notificationConfig.getFooterTemplateFile()) : null;
		
		context.put("headerContent", processTemplateFile(context, headerFile, "header"));
		context.put("footerContent", processTemplateFile(context, footerFile, "footer"));
	}
	
	private String processTemplateFile(Map<String, Object> context, File file, String name) throws IOException
	{
		if(file == null)
		{
			return "";
		}
		
		if(!file.exists())
		{
			logger.warn("For summary-report specified {} template file does not exist: {}", name, file.getPath());
			return "";
		}
		
		String content = FileUtils.readFileToString(file);
		return FreeMarkerMethodManager.replaceExpressions("summary-" + name + "-template", context, content);
	}

	private void sendSummaryMail(final SummaryNotificationConfig notificationConfig, File summaryHtml, Object freeMarkerContext) throws MessagingException, IOException
	{
		Properties props = new Properties();
		props.put("mail.smtp.host", notificationConfig.getSmptpHost()); // SMTP
																		// Host
		props.put("mail.smtp.port", "" + notificationConfig.getSmptpPort()); // TLS
																				// Port
		props.put("mail.smtp.auth", "" + notificationConfig.isAuthEnabled()); // enable
																				// authentication
		props.put("mail.smtp.starttls.enable", "" + notificationConfig.isTtlsEnabled()); // enable
																							// STARTTLS

		Authenticator auth = null;

		// create Authenticator object to pass in Session.getInstance argument
		if(notificationConfig.isAuthEnabled())
		{
			auth = new Authenticator()
			{
				// override the getPasswordAuthentication method
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(notificationConfig.getUserName(), notificationConfig.getPassword());
				}
			};
		}

		Session session = Session.getInstance(props, auth);
		MimeMessage msg = new MimeMessage(session);
	
		// set message headers
		msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
		//msg.addHeader("format", "flowed");
		//msg.addHeader("Content-Transfer-Encoding", "8bit");

		msg.setFrom(new InternetAddress(notificationConfig.getFromAddress()));
		msg.setReplyTo(InternetAddress.parse(notificationConfig.getToAddressList(), false));
		msg.setSubject(FreeMarkerMethodManager.replaceExpressions("notificaton-subject", freeMarkerContext, notificationConfig.getSubjectTemplate()), "UTF-8");
		msg.setSentDate(new Date());

		// create multi part message
		Multipart multiPart = new MimeMultipart();

		// add body to multi part
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(FileUtils.readFileToString(summaryHtml), "text/html");
		multiPart.addBodyPart(messageBodyPart);
		
		// set the multi part message as content
		msg.setContent(multiPart);
		
		
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(notificationConfig.getToAddressList(), false));
		Transport.send(msg);
		
		logger.debug("Summary mail is sent successfully!");
	}

	/**
	 * Creates log files from specified test case result.
	 * @param testCaseResult result from which log data needs to be fetched
	 * @param logFilePrefix log file name prefix
	 * @param monitoringLogs Monitoring logs to be copied
	 * @param description Description about the test case.
	 */
	public void createLogFiles(AutomationContext context, TestCaseResult testCaseResult, String logFilePrefix, Map<String, File> monitoringLogs, String description)
	{
		// create logs folder
		File logsFolder = new File(context.getReportFolder(), "logs");
		
		if(!logsFolder.exists())
		{
			logsFolder.mkdirs();
		}

		ExecutionLogData executionLogData = testCaseResult.getExecutionLog();
		
		if( executionLogData == null)
		{
			return;
		}
		
		executionLogData.setStatus(testCaseResult.getStatus());
		
		executionLogData.copyResources(logsFolder);
		
		try
		{
			String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(executionLogData);
			String jsContent = "var logData = " + jsonContent;
			
			FileUtils.write(new File(logsFolder, logFilePrefix + LOG_JSON), jsonContent);
			FileUtils.write(new File(logsFolder, logFilePrefix + LOG_JS), jsContent);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating test log json file - {}", new File(logsFolder, logFilePrefix + LOG_JSON));
		}

		if(monitoringLogs != null)
		{
			for(Map.Entry<String, File> log : monitoringLogs.entrySet())
			{
				if(log.getValue() == null)
				{
					continue;
				}
				
				try
				{
					FileUtils.copyFile(log.getValue(), new File(logsFolder, logFilePrefix + "_" + log.getKey() + ".log"));
					
					Template freemarkerTemplate = new Template("monitor-log-template", 
							new InputStreamReader(AutomationLauncher.class.getResourceAsStream("/monitor-log-template.html")), freemarkerConfiguration);

					File logHtmlFile = new File(logsFolder, logFilePrefix + "_" + log.getKey() + ".log.html");
					FileWriter writer = new FileWriter(logHtmlFile);
					String logContent = FileUtils.readFileToString(log.getValue());
					
					freemarkerTemplate.process(new LogMonitorContext(testCaseResult.getTestCaseName(), log.getKey(), logContent, testCaseResult.getStatus(), description), writer);

					testCaseResult.setMonitorLog(log.getKey(), logHtmlFile.getName());
					
					writer.flush();
					writer.close();
					
					log.getValue().delete();
				}catch(Exception ex)
				{
					throw new InvalidStateException("An error occurred while creating monitoring log file - {}", log.getKey());
				}
			}
		}
		
		testCaseResult.setSystemLogName(logFilePrefix);
	}
}
