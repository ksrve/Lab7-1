import java.net.UnknownHostException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class SendEmail {


    public static Status send(String email, String registrationToken) {

        System.out.println("Sending the message to " + email);

        final String SMTP_AUTH_EMAIL = "honey.kosareva@gmail.com"; // тот, кто отправляет
        final String SMTP_AUTH_PWD = "180376as";
        final String SMTP_SERVER = "smtp.gmail.com";
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        try{
            Properties properties = new Properties();
            properties.setProperty("mail.transport.protocol", "smtps");
            properties.setProperty("mail.smtps.host", SMTP_SERVER);
            properties.setProperty("mail.smtps.user", SMTP_AUTH_EMAIL);

            Session session = Session.getDefaultInstance(properties);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_AUTH_EMAIL));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(email));
            message.setSubject("Registration on baaaad server");
            message.setText("Hi, my dear friend \n" +
                    "\nThis is your password \n" + registrationToken);

            Transport tr = session.getTransport();
            tr.connect(SMTP_AUTH_EMAIL, SMTP_AUTH_PWD);
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();

            System.out.printf("Message to %s was sent successfully\n", email);
            return Status.OK;
        } catch (MessagingException e) {
            System.err.println("Ooops! It might be a problem with e-mail");
            return Status.NO_MAIL;
        } catch (Exception e ){
            System.err.println();
            return Status.NO_MAIL;
        }
    }
}