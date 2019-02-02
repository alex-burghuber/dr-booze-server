package mail;

import entities.User;
import entities.VerificationToken;
import services.Main;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static javax.mail.Message.RecipientType;

/**
 * @author Alexander Burghuber
 */
public class MailService {

    private String emailPassword;
    private Session session;

    public MailService() {
        // load the email password from the config file
        try (InputStream input = new FileInputStream("src/main/resources/properties/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            emailPassword = prop.getProperty("email_password");

            // setup properties
            Properties properties = System.getProperties();
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            // setup mail session
            session = Session.getDefaultInstance(properties, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendConfirmation(User user, VerificationToken verificationToken) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.addRecipients(RecipientType.TO, String.valueOf(new InternetAddress(user.getEmail())));
            message.setSubject("Welcome to Dr. Booze");
            String mailBody =
                    "<h1>Welcome to Dr. Booze</h1><br>" +
                            "<a href='" + Main.BASE_URI + "/booze/verify/" + verificationToken.getToken()
                            + "'>Confirm your email</a>";
            transport(message, mailBody);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }

    private void transport(Message message, String mailBody) throws MessagingException {
        message.setContent(mailBody, "text/html");

        Transport transport = session.getTransport("smtp");
        transport.connect("smtp.gmail.com", "dr.boozeteam@gmail.com", emailPassword);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

}