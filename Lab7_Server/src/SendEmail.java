import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class SendEmail {



    public static Status send(String email, String registrationToken) {

        System.out.println("Sending the message to " + email);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        try {
            String host = "smtp.gmail.com";
            String user = "honey.kosareva@gmail.com";
            String pass = "180376as";
            final Properties properties = new Properties();

            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            Authenticator auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            };

            Session mailSession = Session.getInstance(properties, auth);


            MimeMessage message = new MimeMessage(mailSession);

            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Pass");
            message.setText("Hi, dear user!"
                    + "\nThis is your token: "
                    + registrationToken
                    + "\nCtrl+c and ctrl+v token in 'help me, i wanna die' program "
                    + "\nHave a nice day;)" );

            Transport.send(message);


            System.out.printf("Message to %s was sent successfully\n", email);
        } catch (MessagingException e) {
            System.err.println("Ooops! It might be a problem with e-mail");
            return Status.NO_MAIL;
        } catch (Exception e ){
            System.err.println();
            return Status.NO_MAIL;
            
        }return  Status.OK;
    }
}