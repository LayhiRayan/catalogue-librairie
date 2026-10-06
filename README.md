# Catalogue de librairie

Application Java réalisée dans le cadre d'un TP Maven, JPA, Hibernate et H2. Elle enregistre trois livres, affiche le catalogue à l'aide d'une requête JPQL et recherche un livre par son identifiant.

## Prérequis

- JDK 8 ou supérieur
- Maven 3.6.3 ou supérieur

Les versions des dépendances suivent le support du TP : JPA 2.2, Hibernate 5.6.5.Final, H2 2.1.214 et SLF4J 1.7.36.

## Exécution

Depuis le dossier du projet :

```sh
mvn clean verify
mvn compile exec:java
```

Dans un IDE, importer le fichier `pom.xml` puis lancer `ma.rayan.librairie.CatalogueApplication`.

En plus des requêtes SQL, le programme affiche :

```text
Les trois livres ont été enregistrés.

Catalogue de la librairie :
Livre{id=1, titre='La Boîte à merveilles', prix=85.00 MAD}
Livre{id=2, titre='Le Dernier Jour d'un condamné', prix=65.50 MAD}
Livre{id=3, titre='Antigone', prix=72.00 MAD}

Recherche du livre n° 2 :
Livre{id=2, titre='Le Dernier Jour d'un condamné', prix=65.50 MAD}
```

## Console H2

```sh
mvn compile exec:java "-Dexec.args=--console"
```

Garder le terminal ouvert et accéder à [la console H2](http://localhost:8082). Utiliser les paramètres suivants :

| Paramètre | Valeur |
| --- | --- |
| Driver Class | `org.h2.Driver` |
| JDBC URL | `jdbc:h2:mem:librairie` |
| User Name | `sa` |
| Password | laisser vide |

```sql
SELECT * FROM LIVRES ORDER BY ID;
SELECT * FROM LIVRES WHERE ID = 2;
```

Appuyer sur Entrée dans le terminal pour fermer la console. Le serveur est prévu pour un usage local.

La configuration utilise `hibernate.hbm2ddl.auto=update`. La base reste en mémoire : son contenu est perdu à l'arrêt du processus Java, même avec `update`. Chaque lancement repart donc avec les trois livres d'exemple.

## Organisation

- `src/main/java/ma/rayan/librairie/CatalogueApplication.java` : insertion transactionnelle, lecture et console H2.
- `src/main/java/ma/rayan/librairie/model/Livre.java` : entité JPA, identifiant généré et prix décimal.
- `src/main/resources/META-INF/persistence.xml` : unité de persistance `librairie-pu`.
- `src/test/java/ma/rayan/librairie/LivrePersistenceTest.java` : vérification de l'enregistrement et de la lecture dans une base H2 isolée.

En cas d'échec d'insertion, la transaction est annulée et l'erreur remonte à l'appelant. Les gestionnaires et la fabrique JPA sont fermés en fin d'utilisation.
