[![Build Status](https://github.com/AnaelMobilia/NextINpact-Unofficial/actions/workflows/build.yaml/badge.svg)](https://github.com/AnaelMobilia/NextINpact-Unofficial/actions/workflows/build.yaml)

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on-fr.png" alt="Disponible sur F-Droid" height="100">](https://f-droid.org/packages/com.pcinpact/)
[<img src="https://play.google.com/intl/en_us/badges/images/generic/fr_badge_web_generic.png" alt="Disponible sur Google Play" height="100"/>](https://play.google.com/store/apps/details?id=com.pcinpact)

## Bibliothèques utilisées par le projet :

- [jsoup v1.15.4 - MIT](http://jsoup.org/) : Parsage du code HTML
- [PhotoView v2.3.0 - Apache 2.0](https://github.com/Baseflow/PhotoView) : zoom sur les images
- [Glide v4.15.1 - BSD, part MIT and Apache 2.0](https://github.com/bumptech/glide) : chargement,
  cache et affichage des images
- [OkHttp v4.10.0 - Apache 2.0](https://github.com/square/okhttp) : Chargement des données et
  définition du user-agent utilisé
  par Glide
- [CounterFab v1.2.2 - Apache 2.0](https://github.com/andremion/CounterFab) : Bouton des
  commentaires avec badge

## Outils pratiques :

- [Voir les icônes Android existantes](http://androiddrawables.com)
- [Dépôt git des sources Android (permet de récupérer les images)](https://github.com/android/platform_frameworks_base/tree/master/core/res/res)

## Debug

- Logcat sur une application tournant sur une device : `adb logcat *:E`
- effacer le logcat : `adb logcat -c`
- Install d'un apk en tant que mise à jour de l'application : `adb install -r path/to/file.apk`
- Monkey test : `adb shell monkey -p mon.application.name -v 500`
