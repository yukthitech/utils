package com.yukthitech.autox.test;

import java.io.File;
import java.io.IOException;
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
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.config.SummaryNotificationConfig;
import com.yukthitech.utils.exceptions.InvalidStateException;

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

			String reportTemplate = IOUtils.toString(ReportGenerator.class.getResourceAsStream(SUMMARY_REPORT_TEMPLATE));
			String summaryResult = FreeMarkerMethodManager.replaceExpressions(context, reportTemplate);

			File summaryHtml = new File(reportFolder, "summary-report.html");
			FileUtils.write(summaryHtml, summaryResult);

			//send notification mail
			if(applicationConfiguration.getSummaryNotificationConfig() != null)
			{
				sendSummaryMail(applicationConfiguration.getSummaryNotificationConfig(), summaryHtml, context);
			}
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while generating summary report");
		}
		
	}

	private void sendSummaryMail(final SummaryNotificationConfig notificationConfig, File summaryHtml, Object freeMarkerContext) throws MessagingException, IOException
	{
		Properties props = new Properties();
		props.put("mail.smtp.host", notificationConfig.getSmptpHost()); // SMTP
																		// Host
		props.put("mail.smtp.port", "" + notificationConfig.getSmptpHost()); // TLS
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
		msg.setSubject(FreeMarkerMethodManager.replaceExpressions(freeMarkerContext, notificationConfig.getSubjectTemplate()), "UTF-8");
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
}
