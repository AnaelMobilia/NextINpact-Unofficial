[![Build Status](https://travis-ci.org/AnaelMobilia/NextINpact-Unofficial.svg?branch=master)](https://travis-ci.org/AnaelMobilia/NextINpact-Unofficial)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6128e8ab3cb24120857aa99637f250b2)](https://www.codacy.com/app/AnaelMobilia/NextINpact-Unofficial?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AnaelMobilia/NextINpact-Unofficial&amp;utm_campaign=Badge_Grade)

## Librairies utilisées par le projet :
  - [jsoup v1.10.1] (http://jsoup.org/) : Parsage du code HTML

## Outils pratiques :
  - [Voir les icônes Android existantes] (http://androiddrawables.com)
  - [Dépôt git des sources Android (permet de récupérer les images)] (https://github.com/android/platform_frameworks_base/tree/master/core/res/res)

## Debug
  - Logcat sur une application tournant sur une device : `adb logcat *:E`
  - effacer le logcat : `adb logcat -c`
  - Install d'un apk en tant que mise à jour de l'application : `adb install -r path/to/file.apk`
  - Monkey test : `adb shell monkey -p mon.application.name -v 500`