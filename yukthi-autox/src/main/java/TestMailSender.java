import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TestMailSender
{
	public static void main(String[] args)
	{
		sendEmail();
	}
	
    /**
     * Send the email via SMTP using StartTLS and SSL
     */
    private static void sendEmail() {
  
        // Create all the needed properties
        Properties connectionProperties = new Properties();
        // SMTP host
        connectionProperties.put("mail.smtp.host", "10.40.2.16");
        // Is authentication enabled
        connectionProperties.put("mail.smtp.auth", "false");
        // Is StartTLS enabled
        connectionProperties.put("mail.smtp.starttls.enable", "false");
        // SSL Port
        connectionProperties.put("mail.smtp.socketFactory.port", "25");
        // SSL Socket Factory class
        connectionProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // SMTP port, the same as SSL port :)
        connectionProperties.put("mail.smtp.port", "25");
         
        System.out.print("Creating the session...");
         
        // Create the session
        Session session = Session.getDefaultInstance(connectionProperties,
                new javax.mail.Authenticator() {    // Define the authenticator
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("akiran@yodlee.com","");
                    }
                });
         
        System.out.println("done!");
         
        // Create and send the message
        try {
            // Create the message 
            Message message = new MimeMessage(session);
            // Set sender
            message.setFrom(new InternetAddress("akiran@yodlee.com"));
            // Set the recipients
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("akiran@yodlee.com"));
            // Set message subject
            message.setSubject("Hello from Team ITCuties");
            // Set message text
            message.setText("Java is easy when you watch our tutorials ;)");
             
            System.out.print("Sending message...");
            // Send the message
            Transport.send(message);
             
            System.out.println("done!");
             
        } catch (Exception e) {
            e.printStackTrace();
        }
         
    }
}
