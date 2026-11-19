# TP Android – Contacts V3 (Fragments, RecyclerView, filtrage, Google Maps)

## Lancer le projet
1. Ouvrir le dossier dans Android Studio (Gradle Sync ; `local.properties` avec `sdk.dir` est recréé automatiquement).
2. Ajouter dans `local.properties` la ligne `MAPS_API_KEY=VOTRE_CLE` (voir `local.properties.example`)
   (ce fichier est dans `.gitignore`, la clé n'est jamais publiée).
3. Google Cloud : activer « Maps SDK for Android » et créer une clé API.
4. Run sur un émulateur avec Google Play Services.

## Architecture
- `MainActivity` : héberge un `FragmentContainerView`; ajoute la liste seulement si `savedInstanceState == null`.
- `ListeContactsFragment` : RecyclerView + champ de recherche (filtre nom/prénom/téléphone, insensible à la casse, message si vide).
- `DetailContactFragment` : édition, validation, Appeler, E-mail, Galerie, Caméra, coordonnées GPS, « Voir sur la carte ».
- `CarteContactFragment` : SupportMapFragment, marqueur au nom du contact, messages si coordonnées absentes/invalides.
- `ContactsViewModel` (partagé, portée Activity) : liste en mémoire, mise à jour observée par la liste.

## Réponses aux questions de compréhension
1. **Activity vs Fragment** : une Activity est un écran complet déclaré dans le manifeste, avec son cycle de vie propre. Un Fragment est une partie d'interface réutilisable, hébergée par une Activity, avec son cycle de vie lié à celle-ci. Il permet une UI modulaire et une navigation sans multiplier les activités.
2. **FragmentContainerView et addToBackStack()** : FragmentContainerView est le conteneur recommandé pour les fragments (gestion correcte des transactions et de l'ordre d'affichage). `addToBackStack()` mémorise la transaction : le bouton Retour revient au fragment précédent au lieu de quitter l'application.
3. **Adapter et ViewHolder** : l'Adapter fournit les données à la RecyclerView et crée/remplit les lignes. Le ViewHolder garde les références aux vues d'une ligne pour les réutiliser (recyclage) sans refaire de `findViewById`, d'où de bonnes performances.
4. **Erreur d'indice après filtrage** : la liste affichée est une sous-liste, ses positions ne correspondent plus à celles de la liste complète. On travaille donc avec l'objet cliqué (ou son id), jamais avec la position.
5. **Transmettre l'identifiant** : l'id est stable, alors que la position change avec le filtre, le tri, l'ajout ou la suppression. L'id retrouve toujours le bon contact.
6. **Protéger la clé Maps** : la mettre dans `local.properties` (ignoré par Git), l'injecter via `manifestPlaceholders`, fournir seulement `local.properties.example`, et restreindre la clé dans Google Cloud (nom du package + empreinte SHA-1). Si une clé a déjà été publiée, la révoquer et en créer une nouvelle.

## Choix de communication
ViewModel partagé plutôt que Fragment Result API : la liste observe une source unique de données, donc elle se rafraîchit automatiquement, y compris après rotation. Le Bundle ne transporte que l'identifiant du contact.
