[![Build Status](https://travis-ci.org/AnaelMobilia/NextINpact-Unofficial.svg?branch=master)](https://travis-ci.org/AnaelMobilia/NextINpact-Unofficial)

## Librairies utilisées par le projet :
  - [jsoup v1.8.1a] (http://jsoup.org/) : Parsage du code HTML

  - [Commons-io v2.2] (http://commons.apache.org/proper/commons-io/) : Gestion de flux de fichiers (entre autres)

## Outils pratiques :
  - [Voir les icônes Android existantes] (http://androiddrawables.com)
  - [Dépôt git des sources Android (permet de récupérer les images)] (https://github.com/android/platform_frameworks_base/tree/master/core/res/res)

## Debug
  - Logcat sur une application tournant sur une device : `adb logcat *:E`
  - effacer le logcat : `adb logcat -c`
  - Install d'un apk en tant que mise à jour de l'application : `adb install -r path/to/file.apk`
  - Monkey test : `adb shell monkey -p mon.application.name -v 500`