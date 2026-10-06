package ma.rayan.librairie;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import ma.rayan.librairie.model.Livre;
import org.h2.tools.Server;

public class CatalogueApplication {
    public static void main(String[] args) throws Exception {
        boolean consoleActive = Arrays.asList(args).contains("--console");
        EntityManagerFactory fabrique = Persistence.createEntityManagerFactory("librairie-pu");
        Server console = null;
        try {
            insererLivres(fabrique);
            afficherCatalogue(fabrique);
            if (consoleActive) {
                console = Server.createWebServer("-webPort", "8082").start();
                System.out.println("\nConsole H2 : http://localhost:8082");
                System.out.println("URL JDBC : jdbc:h2:mem:librairie | Utilisateur : sa | Mot de passe vide");
                System.out.println("Appuyez sur Entrée pour arrêter la console.");
                System.in.read();
            }
        } finally {
            if (console != null) {
                console.stop();
            }
            fabrique.close();
        }
    }

    private static void insererLivres(EntityManagerFactory fabrique) {
        EntityManager gestionnaire = fabrique.createEntityManager();
        try {
            gestionnaire.getTransaction().begin();
            gestionnaire.persist(new Livre("La Boîte à merveilles", new BigDecimal("85.00")));
            gestionnaire.persist(new Livre("Le Dernier Jour d'un condamné", new BigDecimal("65.50")));
            gestionnaire.persist(new Livre("Antigone", new BigDecimal("72.00")));
            gestionnaire.getTransaction().commit();
            System.out.println("\nLes trois livres ont été enregistrés.");
        } catch (RuntimeException erreur) {
            if (gestionnaire.getTransaction().isActive()) {
                gestionnaire.getTransaction().rollback();
            }
            throw erreur;
        } finally {
            gestionnaire.close();
        }
    }

    private static void afficherCatalogue(EntityManagerFactory fabrique) {
        EntityManager gestionnaire = fabrique.createEntityManager();
        try {
            List<Livre> catalogue = gestionnaire.createQuery(
                    "SELECT l FROM Livre l ORDER BY l.id", Livre.class).getResultList();
            System.out.println("\nCatalogue de la librairie :");
            for (Livre livre : catalogue) {
                System.out.println(livre);
            }
            // Une nouvelle lecture force la recherche en base, hors du cache JPA.
            gestionnaire.clear();
            Livre livre = gestionnaire.find(Livre.class, 2L);
            System.out.println("\nRecherche du livre n° 2 :");
            System.out.println(livre != null ? livre : "Aucun livre trouvé.");
        } finally {
            gestionnaire.close();
        }
    }
}
