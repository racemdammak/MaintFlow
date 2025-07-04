package gestion_maintenance;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class GmailSender {

    public static void envoyerEmail(String destinataire, String sujet, String messageTexte) {
        final String emailExpediteur = "racemdammak81@gmail.com";
        final String motDePasse = "ivsk kkgt zkox ksrs";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailExpediteur, motDePasse);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailExpediteur));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);
            message.setText(messageTexte);

            Transport.send(message);

            System.out.println("E-mail envoyé avec succès à " + destinataire);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Échec de l'envoi de l'e-mail.");
        }
    }
}
