[![Build Status](https://travis-ci.org/AnaelMobilia/NextINpact-Unofficial.svg?branch=master)](https://travis-ci.org/AnaelMobilia/NextINpact-Unofficial)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6128e8ab3cb24120857aa99637f250b2)](https://www.codacy.com/app/AnaelMobilia/NextINpact-Unofficial?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AnaelMobilia/NextINpact-Unofficial&amp;utm_campaign=Badge_Grade)

<a href="https://f-droid.org/packages/com.pcinpact/" target="_blank">
<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="100"/></a>
<a href="https://play.google.com/store/apps/details?id=com.pcinpact" target="_blank">
<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" height="100"/></a>

## Librairies utilisées par le projet :
  - [jsoup v1.10.3 - MIT] (http://jsoup.org/) : Parsage du code HTML

## Librairies forkées par le projet
  - [Commons IO 2.2 - Apache license, version 2.0] (https://commons.apache.org/proper/commons-io/) : InputStream .toString() &
  .toByteArray()

## Outils pratiques :
  - [Voir les icônes Android existantes](http://androiddrawables.com)
  - [Dépôt git des sources Android (permet de récupérer les images)](https://github.com/android/platform_frameworks_base/tree/master/core/res/res)

## Debug
  - Logcat sur une application tournant sur une device : `adb logcat *:E`
  - effacer le logcat : `adb logcat -c`
  - Install d'un apk en tant que mise à jour de l'application : `adb install -r path/to/file.apk`
  - Monkey test : `adb shell monkey -p mon.application.name -v 500`
