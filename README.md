# Asteroids

Jeu experimental de destruction d'asteroids.

![picture](rsc/github/screenshot.png)

## Langage

Le jeu est programmé entièrement en Java et n'utilise aucune librairie autre que le jdk (version 17).
Pour le rendu graphique swing et awt sont utilisés.

## Fonctionnalités

- Génération aléatoire d'asteroid
- Séparation en plusieurs morceaux d'asteroid
- Différents types de munition.
- Sauvegarde du score
- Pause state
- Game over state

## Asteroid

Tout est programmé dans la class Asteroidv2 (La v1 est toujours dans le code).
Chaque entité possède un tableau en 2 dimensions de pixels.
Un pixel est un objet possédant une couleur, un nombre de vies...

### Génération d'asteroids

Pour générer un asteroid, je commence par une forme circulaire de pixels avec l'algorithme de "perlin noise".
Ensuite, en utilisant le "flood filling" je remplis la forme à partir de sont centre.
Pour finir, à chaque index marqué par un pixel, un pixel est créé avec une couleur générée par le perlin noise.

### Séparation des asteroids

Pour séparer en plusieurs morceaux un asteroid, j'utilise également le flood filling pour détecter
chaque petite partie. La fonction renvoi une liste de tableaux de pixel.
Pour chaque tableau, une nouvelle entitée de type asteroid est instantié.

## Moteur de jeu

Le moteur est un peu "cafouillis" car tout est fait par moi même. Je me suis inspiré de l'architecture "MonoBehaviour" 
comme on le retrouve dans le moteur Unity.

