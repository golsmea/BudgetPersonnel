package bgpersonnel.budget.objectif;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class ObjectifService {

    @Autowired
    private ObjectifRepository objectifRepository;

    @Autowired
    private JavaMailSender mailSender;
    // CRUD operations

    /**
     * Create a new objectif
     * @param objectif
     * @return
     */
    public Objectif createObjectif(Objectif objectif) {
        return objectifRepository.save(objectif);
    }

    /**
     * Find an objectif by its id
     * @param id de l'objectif
     * @return l'objectif correspondant à l'id
     */
    public Objectif findById(Long id) {
        return objectifRepository.findById(id).get();
    }

    /**
     * Find all objectifs
     * @return all objectifs
     */
    public Iterable<Objectif> findAll() {
        return objectifRepository.findAll();
    }

    /**
     * Update an objectif
     * @param objectif to update
     * @return the updated objectif
     */
    public Objectif update(Objectif objectif) {
        return objectifRepository.save(objectif);
    }

    /**
     * Delete an objectif by its id
     * @param id of the objectif to delete
     */
    public void deleteById(Long id) {
        objectifRepository.deleteById(id);
    }

    /**
     * Find all objectifs of a user
     * @param id of the user
     * @return all objectifs of the user
     */
    public List<Objectif> findByUser(Long id) {
        return objectifRepository.findByUser(id);
    }


    /**
     * Calculate the progress percentage of an objectif
     * @param idObjectif of the objectif
     * @return the progress percentage
     */
    public Double calculateProgressPercentage(Long idObjectif) {
        //récuperation de l'objectif
        Objectif objectif = objectifRepository.findById(idObjectif).orElse(null);
        //si l'objectif n'existe pas on renvoie une exception
        if (objectif == null) {
            throw new IllegalArgumentException("Objectif invalide");
        }
        Double progress = objectifRepository.calculateProgress(objectif);
        //On retourne la progression en pourcentage
        return progress / objectif.getAmount() * 100.0;
    }

    /**
     * Check if an objectif is reached
     * @param idObjectif of the objectif
     * @return true if the objectif is reached, false otherwise
     */
    public boolean isObjectifAtteint(Long idObjectif) {
        //récuperation de l'objectif
        Objectif objectif = objectifRepository.findById(idObjectif).orElse(null);
        //si l'objectif n'existe pas on renvoie une exception
        if (objectif == null) {
            throw new IllegalArgumentException("Objectif invalide");
        }
        double progressPercentage = calculateProgressPercentage(idObjectif);

        //si le pourcentage est supérieur ou égal à 100, l'objectif est atteint
        if(progressPercentage >= 100.0) {
            String subject = "Objectif atteint : " + objectif.getName();
            String text = "Félicitations, vous avez atteint votre objectif financier " + objectif.getName() + " !";

            // envoyer un email à l'utilisateur
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(objectif.getUser().getEmail());
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            return true;
        }else {
            return false;
        }
    }

}
