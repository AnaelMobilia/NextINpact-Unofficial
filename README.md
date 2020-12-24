[![Build Status](https://travis-ci.org/AnaelMobilia/NextINpact-Unofficial.svg?branch=master)](https://travis-ci.org/AnaelMobilia/NextINpact-Unofficial)

<a href="https://f-droid.org/packages/com.pcinpact/" target="_blank">
  <img src="https://fdroid.gitlab.io/artwork/badge/get-it-on-fr.png" alt="Disponible sur F-Droid" height="100">
</a>
<a href="https://play.google.com/store/apps/details?id=com.pcinpact" target="_blank">
  <img src="https://play.google.com/intl/en_us/badges/images/generic/fr_badge_web_generic.png" alt="Disponible sur Google Play" height="100"/>
</a>

## Bibliothèques utilisées par le projet :
  - [jsoup v1.13.1 - MIT](http://jsoup.org/) : Parsage du code HTML
  - [PhotoView v2.3.0 - Apache 2.0](https://github.com/chrisbanes/PhotoView) : zoom sur les images
  - [Glide v4.11.0 - BSD, part MIT and Apache 2.0](https://github.com/bumptech/glide) : chargement, cache et affichage des images
  - [OkHttp v4.8.0 - Apache 2.0](https://square.github.io/okhttp/) : Chargement des données et définition du user-agent utilisé
  par Glide

## Outils pratiques :
  - [Voir les icônes Android existantes](http://androiddrawables.com)
  - [Dépôt git des sources Android (permet de récupérer les images)](https://github.com/android/platform_frameworks_base/tree/master/core/res/res)

## Debug
  - Logcat sur une application tournant sur une device : `adb logcat *:E`
  - effacer le logcat : `adb logcat -c`
  - Install d'un apk en tant que mise à jour de l'application : `adb install -r path/to/file.apk`
  - Monkey test : `adb shell monkey -p mon.application.name -v 500`
