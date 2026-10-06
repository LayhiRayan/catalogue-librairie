package ma.rayan.librairie;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import ma.rayan.librairie.model.Livre;
import org.junit.Test;
import static org.junit.Assert.*;

public class LivrePersistenceTest {
    @Test
    public void enregistrerPuisRelireUnLivre() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("javax.persistence.jdbc.url", "jdbc:h2:mem:librairie_test");
        configuration.put("hibernate.hbm2ddl.auto", "create-drop");
        EntityManagerFactory fabrique = Persistence.createEntityManagerFactory("librairie-pu", configuration);
        EntityManager gestionnaire = fabrique.createEntityManager();
        try {
            gestionnaire.getTransaction().begin();
            Livre livre = new Livre("Antigone", new BigDecimal("72.00"));
            gestionnaire.persist(livre);
            gestionnaire.getTransaction().commit();
            assertNotNull(livre.getId());
            Long identifiant = livre.getId();
            gestionnaire.close();
            gestionnaire = fabrique.createEntityManager();
            Livre retrouve = gestionnaire.find(Livre.class, identifiant);
            assertNotNull(retrouve);
            assertEquals("Antigone", retrouve.getTitre());
            assertEquals(new BigDecimal("72.00"), retrouve.getPrix());
            assertEquals(1, gestionnaire.createQuery("SELECT l FROM Livre l", Livre.class).getResultList().size());
            assertNull(gestionnaire.find(Livre.class, -1L));
        } finally {
            if (gestionnaire.isOpen()) {
                if (gestionnaire.getTransaction().isActive()) {
                    gestionnaire.getTransaction().rollback();
                }
                gestionnaire.close();
            }
            fabrique.close();
        }
    }
}
