/*
 * Copyright 2015, 2016 Anael Mobilia
 *
 * This file is part of NextINpact-Unofficial.
 *
 * NextINpact-Unofficial is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NextINpact-Unofficial is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NextINpact-Unofficial. If not, see <http://www.gnu.org/licenses/>
 */
package com.pcinpact.parseur;

import com.pcinpact.items.ArticleItem;
import com.pcinpact.utils.Constantes;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ParseurHTMLTest {
    /**
     * Impossible de mettre le code HTML dans un fichier resources jusqu'à Android Studio 1.3
     * https://code.google.com/p/android/issues/detail?id=136013#c28
     * https://code.google.com/p/android/issues/detail?id=64887
     *
     * Vérifie le bon fonctionnement du parseur entre un code HTML figé et le résultat correct attendu
     *
     * @throws Exception
     */
    @Test
    public void testGetListeArticles() throws Exception {
        /**
         * Contenu généré via DEBUG -> Développement - Log.e(ArrayList[ArticleItem])
         */

        ArrayList<ArticleItem> mesArticles = new ArrayList<>();
        ArticleItem unArticle;
        unArticle = new ArticleItem();
        unArticle.setId(93786);
        unArticle.setTimeStampPublication(1428841599000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/1983.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93786-le-recap-bons-plans-moment-semaine-15.htm");
        unArticle.setTitre("Le récap' des bons plans du moment, semaine 15");
        unArticle.setSousTitre("Cyberattaque sur les prix !");
        unArticle.setNbCommentaires(12);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93725);
        unArticle.setTimeStampPublication(1428789600000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6218.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93725-528eme-edition-lidd-liens-idiots-du-dimanche.htm");
        unArticle.setTitre("528ème édition des LIDD : Liens Idiots Du Dimanche");
        unArticle.setSousTitre("Il n'y a pas d'âge pour jouer aux billes");
        unArticle.setNbCommentaires(37);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93787);
        unArticle.setTimeStampPublication(1428747632000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6211.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93787-interview-le-droit-d-auteur-et-julia-reda-eurodeputeedu-parti-pirate.htm");
        unArticle.setTitre("[Interview] Le droit d’auteur et Julia Reda, eurodéputée du Parti Pirate");
        unArticle.setSousTitre("Quelle réforme du droit d'auteur en Europe ?");
        unArticle.setNbCommentaires(39);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93788);
        unArticle.setTimeStampPublication(1428739558000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/1596.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93788-la-faille-rootpipe-corrigee-dans-yosemite-10-10-3-mais-pas-dans-autres-os-x.htm");
        unArticle.setTitre("La faille Rootpipe corrigée dans Yosemite 10.10.3, mais pas dans les autres OS X");
        unArticle.setSousTitre("Trop de boulot !");
        unArticle.setNbCommentaires(42);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93775);
        unArticle.setTimeStampPublication(1428699466000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/5258.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93775-windows-10-pour-smartphone-nouvelle-build-10051-pour-dizaines-lumia.htm");
        unArticle.setTitre("Windows 10 pour smartphones : nouvelle build 10051 pour une trentaine de Lumia");
        unArticle.setSousTitre("Et paf, les serveurs Insider en rade");
        unArticle.setNbCommentaires(78);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93782);
        unArticle.setTimeStampPublication(1428681858000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6213.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93782-cyberattaque-tv5monde-on-est-dans-guerre-information.htm");
        unArticle.setTitre("Cyberattaque de TV5Monde : « On est dans une guerre de l'information »");
        unArticle.setSousTitre("ANSSI font les pirates");
        unArticle.setNbCommentaires(36);
        unArticle.setAbonne(true);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93785);
        unArticle.setTimeStampPublication(1428679800000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/2743.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93785-star-wars-sfr-numerciable-propose-films-en-telechargement-a-2299-unite.htm");
        unArticle.setTitre("[MàJ] Star Wars : 22,99 € le film chez Numericable-SFR, le prix de « la vraie expérience » sur TV");
        unArticle.setSousTitre("La « fameuse » règle des 30 % ?");
        unArticle.setNbCommentaires(187);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93776);
        unArticle.setTimeStampPublication(1428678744000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/1799.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93776-deux-plaintes-deposees-en-france-contre-programmes-surveillance.htm");
        unArticle.setTitre("Deux plaintes déposées en France contre les programmes de surveillance");
        unArticle.setSousTitre("Pochette sur Prism");
        unArticle.setNbCommentaires(14);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93779);
        unArticle.setTimeStampPublication(1428676200000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6209.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93779-renove-par-google-snapseed-2-0-fait-son-retour-sur-android-et-ios.htm");
        unArticle.setTitre("Rénové par Google, Snapseed 2.0 fait son retour sur Android et iOS");
        unArticle.setSousTitre("Votre smartphone ultra-moderne pourra vieillir toutes vos photos");
        unArticle.setNbCommentaires(5);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93777);
        unArticle.setTimeStampPublication(1428674400000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/3935.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93777-le-senat-favorable-a-l-open-data-sur-donnees-transport.htm");
        unArticle.setTitre("Le Sénat favorable à l’Open Data sur les données de transport");
        unArticle.setSousTitre("Métro, c'est trop");
        unArticle.setNbCommentaires(3);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93783);
        unArticle.setTimeStampPublication(1428672600000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6206.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93783-wordpress-fbi-met-en-garde-contre-vague-dattaques-par-soutiens-daesh.htm");
        unArticle.setTitre("Wordpress : le FBI met en garde contre une vague d'attaques par des soutiens de Daesh");
        unArticle.setSousTitre("Fox Mulder mène l'enquête");
        unArticle.setNbCommentaires(37);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93780);
        unArticle.setTimeStampPublication(1428669900000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/1774.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93780-les-editeurs-se-dressent-contre-restauration-fonctions-en-ligne-jeux-abandonnes.htm");
        unArticle.setTitre("Les éditeurs se dressent contre la restauration des fonctions en ligne de jeux abandonnés");
        unArticle.setSousTitre("Difficile de s'a-musée dans ces conditions");
        unArticle.setNbCommentaires(74);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93757);
        unArticle.setTimeStampPublication(1428667200000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/1553.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93757-arcep-defis-et-enjeux-qui-attendent-regulateur-telecoms.htm");
        unArticle.setTitre("ARCEP : les défis et les enjeux qui attendent le régulateur des télécoms");
        unArticle.setSousTitre("Une ouverture de velours dans une neutralité de fer");
        unArticle.setNbCommentaires(13);
        unArticle.setAbonne(true);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93784);
        unArticle.setTimeStampPublication(1428658200000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6205.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93784-asustor-ameliore-son-application-looksgood-avec-transcodage-video.htm");
        unArticle.setTitre("Asustor améliore son application LooksGood avec du transcodage vidéo");
        unArticle.setSousTitre("LooksInteresting");
        unArticle.setNbCommentaires(2);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93781);
        unArticle.setTimeStampPublication(1428656413000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6204.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93781-une-manifestation-contre-loi-sur-renseignement-organisee-lundi-a-paris.htm");
        unArticle.setTitre("[Brève] Une manifestation contre la loi sur le renseignement organisée lundi à Paris");
        unArticle.setSousTitre("Avis aux « exégètes amateurs » et opposants de « mauvaise foi »");
        unArticle.setNbCommentaires(58);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93770);
        unArticle.setTimeStampPublication(1428654000000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/4700.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93770-deus-ex-mankind-divided-prochain-volet-serie-se-montre-enfin.htm");
        unArticle.setTitre("[Brève] Deus Ex Mankind Divided : le prochain volet de la série se montre enfin");
        unArticle.setSousTitre("Vite, oublions The Fall");
        unArticle.setNbCommentaires(74);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93778);
        unArticle.setTimeStampPublication(1428652200000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6203.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93778-des-hebergeurs-menacent-quitter-france-en-cas-d-adoption-loi-renseignement.htm");
        unArticle.setTitre("Des hébergeurs menacent de quitter la France en cas d’adoption de la loi renseignement");
        unArticle.setSousTitre("Direction Sainte-Hélène");
        unArticle.setNbCommentaires(190);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93774);
        unArticle.setTimeStampPublication(1428650073000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/3936.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93774-dropbox-sintegre-desormais-a-office-online.htm");
        unArticle.setTitre("Dropbox s'intègre désormais à Office Online");
        unArticle.setSousTitre("Et réciproquement d'ailleurs");
        unArticle.setNbCommentaires(24);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93763);
        unArticle.setTimeStampPublication(1428648092000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6201.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93763-la-norme-hdmi-2-0a-est-officielle-et-prend-en-charge-hdr.htm");
        unArticle.setTitre("La norme HDMI 2.0a est officielle et prend en charge le HDR");
        unArticle.setSousTitre("Même pas 36 Gb/s ?");
        unArticle.setNbCommentaires(86);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93740);
        unArticle.setTimeStampPublication(1428594468000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6192.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93740-test-nxi-cities-skylines-simcity-like-presque-parfait.htm");
        unArticle.setTitre("Cities Skylines : un SimCity-like presque parfait");
        unArticle.setSousTitre("Pour l'ambiance ça va, mais le plat manque un peu de piment");
        unArticle.setNbCommentaires(68);
        unArticle.setAbonne(true);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93772);
        unArticle.setTimeStampPublication(1428591600000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/1560.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93772-skype-translator-integre-italien-chinois-et-le-text-to-speech.htm");
        unArticle.setTitre("Skype Translator intègre l'italien, le chinois et le « text-to-speech »");
        unArticle.setSousTitre("On attend toujours les langues de Molière et de Goethe");
        unArticle.setNbCommentaires(6);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93769);
        unArticle.setTimeStampPublication(1428588900000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/5258.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93769-microsoft-annonce-son-nano-server-tourne-vers-cloud.htm");
        unArticle.setTitre("Microsoft annonce son Windows Nano Server minimal pour 2016");
        unArticle.setSousTitre("Il en faut peu pour être heureux");
        unArticle.setNbCommentaires(92);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93771);
        unArticle.setTimeStampPublication(1428586200000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/2927.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93771-mise-a-jour-thecusos-5-0-nombreuses-failles-bouchees-et-kodi-pour-certains-nas.htm");
        unArticle.setTitre("Mise à jour de ThecusOS 5.0 : de nombreuses failles bouchées et Kodi pour certains NAS");
        unArticle.setSousTitre("Il était temps...");
        unArticle.setNbCommentaires(6);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93760);
        unArticle.setTimeStampPublication(1428583500000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6191.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93760-facebook-reconnaissance-faciale-serait-illegale-dans-illinois.htm");
        unArticle.setTitre("Facebook : la légalité de la reconnaissance faciale remise en cause dans l’Illinois");
        unArticle.setSousTitre("Ce vieux, très vieux combat entre l'opt-in et l'opt-out");
        unArticle.setNbCommentaires(43);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93768);
        unArticle.setTimeStampPublication(1428580800000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6189.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93768-microsoft-planche-sur-nouvelle-extension-pour-age-of-empires-ii.htm");
        unArticle.setTitre("Microsoft planche sur une nouvelle « extension » pour Age of Empires II");
        unArticle.setSousTitre("Les guillemets ont leur importance");
        unArticle.setNbCommentaires(155);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93765);
        unArticle.setTimeStampPublication(1428573600000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/1783.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93765-youtube-prepare-terrain-a-offre-dabonnement-sans-publicite.htm");
        unArticle.setTitre("YouTube prépare le terrain à une offre d'abonnement sans publicité");
        unArticle.setSousTitre("Le 15 juin de nouvelles conditions de monétisation");
        unArticle.setNbCommentaires(213);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93761);
        unArticle.setTimeStampPublication(1428571265000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/3660.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93761-yosemite-10-10-3-et-ios-8-3-photos-emojis-corrections-et-multiples-ameliorations.htm");
        unArticle.setTitre("Yosemite 10.10.3 et iOS 8.3 : Photos, emojis, corrections et multiples améliorations");
        unArticle.setSousTitre("Attention à votre espace de stockage iCloud");
        unArticle.setNbCommentaires(24);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93766);
        unArticle.setTimeStampPublication(1428568200000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/1777.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93766-zynga-don-mattrick-laisse-son-fauteuil-pdg-et-empoche-188-millions-dollars.htm");
        unArticle.setTitre("Zynga : Don Mattrick laisse son fauteuil de PDG et empoche 18,8 millions de dollars");
        unArticle.setSousTitre("Mattrick s'élève vers de nouveaux horizons");
        unArticle.setNbCommentaires(98);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93767);
        unArticle.setTimeStampPublication(1428566203000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/6185.jpg");
        unArticle.setUrl(
                "https://m.nextinpact.com/news/93767-tv5monde-pirate-ecran-noir-sur-11-chaines-et-propagande-sur-reseaux-sociaux.htm");
        unArticle.setTitre("TV5Monde piraté : écran noir sur les 11 chaines et propagande sur les réseaux sociaux");
        unArticle.setSousTitre("Éclipse totale des programmes");
        unArticle.setNbCommentaires(151);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);
        unArticle = new ArticleItem();
        unArticle.setId(93538);
        unArticle.setTimeStampPublication(1428563916000L);
        unArticle.setUrlIllustration("https://az664837.vo.msecnd.net/images/bd/square-linked-media/1547.jpg");
        unArticle.setUrl("https://m.nextinpact.com/news/93538-le-paiement-par-bitcoin-est-disponible-pour-nos-abonnements.htm");
        unArticle.setTitre("[Blog] Le paiement par Bitcoin est disponible pour nos abonnements");
        unArticle.setSousTitre("Minez, soutenez !");
        unArticle.setNbCommentaires(97);
        unArticle.setAbonne(false);
        mesArticles.add(unArticle);


        /**
         * Récupération du contenu du fichier HTML copié dans test\resources\ParseurHTMLTestListeArticles.html
         */
//        InputStream is = getClass().getResourceAsStream("ParseurHTMLTestListeArticles.html");
//        InputStream is = ClassLoader.getSystemResourceAsStream("resources/ParseurHTMLTestListeArticles.html");
//        String contenuIS = is.toString();

        String contenuIS = "\n" +
                           "\n" +
                           "\n" +
                           "<!DOCTYPE html>\n" +
                           "<html lang=\"fr-FR\" >\n" +
                           "<head>\n" +
                           "    <meta charset=\"utf-8\" />\n" +
                           "    <title>Next INpact, Actualit&#233;s informatique et high tech</title>\n" +
                           "    <link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"/images/favicon.ico\" />\n" +
                           "    <link rel=\"apple-touch-icon\" href=\"/images/common/touch-icons/apple-touch-icon-iphone.png\" />\n" +
                           "    <link rel=\"apple-touch-icon\" sizes=\"72x72\" href=\"/images/common/touch-icons/apple-touch-icon-ipad.png\" />\n" +
                           "    <link rel=\"apple-touch-icon\" sizes=\"114x114\" href=\"/images/common/touch-icons/apple-touch-icon-iphone4.png\" />\n" +
                           "    <script src=\"/js/jq?v=garalz9W_pCMb-67QPxLYnudsdYVN0--5CGlPQGEI2c1\"></script>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "    <link href=\"/css/jquery/cssclair?v=dh2tMW2Q965AkpDQX0p8wi9dRBUektCvyWIVDbw98F81\" rel=\"stylesheet\"/>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "    <!--[if IE]><script src=\"/js/ie/html5.js\"></script><![endif]-->\n" +
                           "\n" +
                           "\n" +
                           "    <script type=\"text/javascript\">\n" +
                           "        $(document).on(\"mobileinit\", function () {\n" +
                           "            $.mobile.defaultPageTransition = 'none';\n" +
                           "            $.mobile.pageLoadErrorMessage = \"Impossible de charger cette page. (Vérifiez votre connectivité réseau)\";\n" +
                           "        });\n" +
                           "    </script>\n" +
                           "\n" +
                           "    <script src=\"/js/all?v=bTDlPiYSGA3MbxcCD6ihW3t1cyoQRgHLeH-0OicklY01\"></script>\n" +
                           "\n" +
                           "\n" +
                           "    \n" +
                           "\n" +
                           "    \n" +
                           "\n" +
                           "\n" +
                           "    <meta name=\"viewport\" content=\"initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no, width=device-width\" />\n" +
                           "    <meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />\n" +
                           "    <meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black-translucent\" />\n" +
                           "</head>\n" +
                           "<body>\n" +
                           "    <div id=\"page\" data-role=\"page\" \n" +
                           "    data-savecache=\"true\" data-key=\"/\"\n" +
                           ">\n" +
                           "        \n" +
                           "<section id=\"menu\" data-role=\"panel\" data-display=\"overlay\" data-iscroll>\n" +
                           "    \n" +
                           "<div id=\"menu_content\">\n" +
                           "        <div id=\"closeMenu\">\n" +
                           "\n" +
                           "        <span class=\"menu_title\">\n" +
                           "            Menu\n" +
                           " <a href=\"#actu_list\" data-rel=\"close\" id=\"btn_menu_close\" class=\"sprite-options sprite-optionsico-close\"></a>        </span>\n" +
                           "\n" +
                           "\n" +
                           "        </div>\n" +
                           "        <div id=\"search\">\n" +
                           "            <div id=\"search_bloc\" class=\"search_bloc\">\n" +
                           "                <form method=\"GET\" action=\"/rechercher\" id=\"form_search\">\n" +
                           "                    <input type=\"text\" name=\"term\" placeholder=\"Rechercher une actualité...\" data-role=\"none\" id=\"input-search\" />\n" +
                           "                    <input type=\"submit\" class=\"sprite sprite-ico-rechercher\" value=\"\" data-role=\"none\" />\n" +
                           "                </form>\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "        <div id=\"sections\" class=\"sections\">\n" +
                           "            <nav>\n" +
                           "                <ul id=\"main-menu-pci\">\n" +
                           "                    <!-- Le commentaire sont !important et permettent une bonne interprétation de la propriété CSS inline-block -->\n" +
                           "                    <li class=\"btn_menu_focus\"><a rel=\"actus\" href=\"/\" data-ajax=\"true\" data-appentry=\"actus\"><span class=\"sprite sprite-menu-actu\"></span>Actus</a></li>\n" +
                           "                    <!--\n" +
                           "                        -->\n" +
                           "                    <li class=\"btn_menu\"><a rel=\"tests\" href=\"/tests\" data-ajax=\"true\" data-appentry=\"tests\"><span class=\"sprite sprite-menu-test\"></span>Test</a></li>\n" +
                           "                    <!--\n" +
                           "                        -->\n" +
                           "                    <li class=\"btn_menu\"><a rel=\"dossiers\" href=\"/dossiers\" data-ajax=\"true\" data-appentry=\"dossiers\"><span class=\"sprite sprite-menu-dossier\"></span>Dossiers</a></li>\n" +
                           "                    <!--\n" +
                           "                        -->\n" +
                           "                    <li class=\"btn_menu bp\"><a rel=\"bonsplans\" href=\"/bons-plans\" data-ajax=\"true\" data-appentry=\"bonplans\"><span class=\"sprite sprite-menu-bonplan\"></span>Bons plans</a></li>\n" +
                           "                    <!--\n" +
                           "                        -->\n" +
                           "                    <li class=\"btn_menu\"><a rel=\"forum\" target=\"_blank\" href=\"http://forum.pcinpact.com/\" data-appentry=\"forum\"><span class=\"sprite sprite-menu-forum\"></span>Forum</a></li>\n" +
                           "                </ul>\n" +
                           "            </nav>\n" +
                           "        </div>\n" +
                           "            <div id=\"categories\" class=\"categories\">\n" +
                           "                <nav>\n" +
                           "                    <ul id=\"cat-menu-pci\">\n" +
                           "                        <li><a href=\"/\">tout</a></li>\n" +
                           "                                <li><a href=\"/?cat=7\">Culture Num&#233;rique</a></li>\n" +
                           "                                <li><a href=\"/?cat=5\">Droit</a></li>\n" +
                           "                                <li><a href=\"/?cat=6\">Economie</a></li>\n" +
                           "                                <li><a href=\"/?cat=3\">Internet</a></li>\n" +
                           "                                <li><a href=\"/?cat=2\">Logiciel</a></li>\n" +
                           "                                <li><a href=\"/?cat=1\">Mat&#233;riel</a></li>\n" +
                           "                                <li><a href=\"/?cat=4\">Mobilit&#233;</a></li>\n" +
                           "                                <li><a href=\"/?cat=8\">Next INpact</a></li>\n" +
                           "\n" +
                           "                    </ul>\n" +
                           "                </nav>\n" +
                           "        </div>\n" +
                           "    </div>\n" +
                           "</section>\n" +
                           "        <section id=\"options\" class=\"options iscroll-wrapper\" data-role=\"panel\" data-display=\"overlay\" data-position=\"right\" data-iscroll>\n" +
                           "\n" +
                           "<section>\n" +
                           "    <div class=\"iscroll-wrapper\">\n" +
                           "        <span class=\"options_title\">\n" +
                           "            Options\n" +
                           "                <a href=\"#actu_list\" data-rel=\"close\" id=\"btn_options_close\" class=\"sprite-options sprite-optionsico-close\"></a>\n" +
                           "        </span>\n" +
                           "        <span class=\"options_separator noshadow\">Mon compte Next INpact</span>\n" +
                           "        <div class=\"bandeau_message_panel error_bandeau\">\n" +
                           "\n" +
                           "            \n" +
                           "        </div>\n" +
                           "\n" +
                           "        <div class=\"options_logon\">\n" +
                           "                <p>\n" +
                           "                    Bienvenue AnaelM\n" +
                           "                    <span class=\"options_logon_premium\">[PREMIUM]</span>\n" +
                           "\n" +
                           "                </p>\n" +
                           "                <button id=\"button_logOff\" type=\"button\" class=\"button_submit button_logon\" data-role=\"none\">Déconnexion</button>\n" +
                           "\n" +
                           "        </div>\n" +
                           "        <span class=\"options_separator\">Affichage</span>\n" +
                           "        <div class=\"options_aff\">\n" +
                           "            <div class=\"info_update\">Modifications sauvegardées</div>\n" +
                           "\n" +
                           "            <ul>\n" +
                           "                <li class=\"\">\n" +
                           "                    Smileys\n" +
                           "                    <div>\n" +
                           "                        <select name=\"option_smileys\" id=\"option_smileys\" data-role=\"slider\" style=\"width:20px!important;\">\n" +
                           "                            <option  value=\"off\">Masqués</option>\n" +
                           "                            <option selected=\"selected\" value=\"on\">Affichés</option>\n" +
                           "                        </select>\n" +
                           "                    </div>\n" +
                           "                </li>\n" +
                           "                <li class=\"\">\n" +
                           "                    Images\n" +
                           "                    <div>\n" +
                           "                        <select name=\"option_images\" id=\"option_images\" data-role=\"slider\">\n" +
                           "                            <option  value=\"off\">Masquées</option>\n" +
                           "                            <option selected=\"selected\" value=\"on\">Affichées</option>\n" +
                           "                        </select>\n" +
                           "                    </div>\n" +
                           "                </li>\n" +
                           "                <li class=\"\">\n" +
                           "                    Commentaires par actu\n" +
                           "                    <div>\n" +
                           "                        <ul class=\"option_nb_comm\">\n" +
                           "                            <li>\n" +
                           "                                <button data-role=\"none\" class=\"btn_comm_page btn_comm_page_active\">\n" +
                           "                                    10\n" +
                           "                                </button>\n" +
                           "                            </li>\n" +
                           "                            <li>\n" +
                           "                                <button data-role=\"none\" class=\"btn_comm_page \">\n" +
                           "                                    20\n" +
                           "                                </button>\n" +
                           "                            </li>\n" +
                           "                            <li>\n" +
                           "                                <button data-role=\"none\" class=\"btn_comm_page \"\n" +
                           "                                        >\n" +
                           "                                    50\n" +
                           "                                </button>\n" +
                           "                            </li>\n" +
                           "                        </ul>\n" +
                           "                    </div>\n" +
                           "                </li>\n" +
                           "                <li class=\"\">\n" +
                           "                    Commentaires sous les news\n" +
                           "                    <div>\n" +
                           "                        <select name=\"option_aff_comm\" id=\"option_aff_comm\" data-role=\"slider\">\n" +
                           "                            <option selected=\"selected\" value=\"off\">Masqués</option>\n" +
                           "                            <option  value=\"on\">Affichés</option>\n" +
                           "                        </select>\n" +
                           "                    </div>\n" +
                           "                    <div class=\"option_disabled_overlay\"></div>\n" +
                           "                </li>\n" +
                           "                    <li class=\"\">\n" +
                           "                        Toutes les publicités\n" +
                           "                        <div>\n" +
                           "                            <select name=\"option_pub\" id=\"option_pub\" data-role=\"slider\">\n" +
                           "                                <option  value=\"off\">Masquées</option>\n" +
                           "                                <option selected=\"selected\" value=\"on\">Affichées</option>\n" +
                           "                            </select>\n" +
                           "                        </div>\n" +
                           "                        <div class=\"option_disabled_overlay\"></div>\n" +
                           "                    </li>\n" +
                           "                    <li style=\"\" class=\"\">\n" +
                           "                        Publicités intrusives\n" +
                           "                        <div>\n" +
                           "                            <select name=\"option_pub_intru\" id=\"option_pub_intru\" data-role=\"slider\">\n" +
                           "                                <option selected=\"selected\" value=\"off\">Masquées</option>\n" +
                           "                                <option  value=\"on\">Affichées</option>\n" +
                           "                            </select>\n" +
                           "                        </div>\n" +
                           "                        <div class=\"option_disabled_overlay\"></div>\n" +
                           "                    </li>\n" +
                           "                    <li>\n" +
                           "                        Theme\n" +
                           "                        \n" +
                           "                        <div>\n" +
                           "                            <ul id=\"option_theme\" data-theme=\"c\">\n" +
                           "                                <li>\n" +
                           "                                    <button data-role=\"none\" data-value=\"0\" class=\"btn_choice_theme btn_theme_actif\">\n" +
                           "                                        Clair\n" +
                           "                                    </button>\n" +
                           "                                </li>\n" +
                           "                                <li>\n" +
                           "                                    <button data-role=\"none\" data-value=\"1\" class=\"btn_choice_theme  \">\n" +
                           "                                        Foncé\n" +
                           "                                    </button>\n" +
                           "                                </li>\n" +
                           "                            </ul>\n" +
                           "                        </div>\n" +
                           "                    </li>\n" +
                           "                                    <li style=\"\">\n" +
                           "\n" +
                           "                        \n" +
                           "\n" +
                           "                        Désactiver la version mobile\n" +
                           "                        <div>\n" +
                           "                            <select name=\"\" id=\"btn_disabled_vm\" data-role=\"slider\">\n" +
                           "                                <option  value=\"off\">Désactivée</option>\n" +
                           "                                <option selected=\"selected\" value=\"on\">Activée</option>\n" +
                           "                            </select>\n" +
                           "                        </div>\n" +
                           "                    </li>\n" +
                           "\n" +
                           "\n" +
                           "                <li>\n" +
                           "                    Taille de police\n" +
                           "                    <div>\n" +
                           "                        <fieldset data-role=\"controlgroup\" class=\"fieldset_policy\" data-type=\"horizontal\" data-mini=\"true\" style=\"margin: -5px -10px 0 0;\">\n" +
                           "                            <legend></legend>\n" +
                           "                            <input id=\"radio_size_option_14\" type=\"radio\" name=\"fontsize\" data-theme=\"c\" value=\"14\" />\n" +
                           "                            <label for=\"radio_size_option_14\">a<span style=\"text-transform: uppercase;\">A</span></label>\n" +
                           "                            <input id=\"radio_size_option_16\" type=\"radio\" name=\"fontsize\" data-theme=\"c\" value=\"16\" checked=\"checked\" />\n" +
                           "                            <label for=\"radio_size_option_16\">a<span style=\"text-transform: uppercase;\">A</span></label>\n" +
                           "                            <input id=\"radio_size_option_18\" type=\"radio\" name=\"fontsize\" data-theme=\"c\" value=\"18\" />\n" +
                           "                            <label for=\"radio_size_option_18\">a<span style=\"text-transform: uppercase;\">A</span></label>\n" +
                           "                        </fieldset>\n" +
                           "                    </div>\n" +
                           "                </li>\n" +
                           "            </ul>\n" +
                           "           \n" +
                           "\n" +
                           "            }\n" +
                           "\n" +
                           "        </div>\n" +
                           "    </div>\n" +
                           "</section>\n" +
                           "\n" +
                           "</section>\n" +
                           "        <section id=\"main\">\n" +
                           "            <header id=\"header\" data-role=\"header\">\n" +
                           "    <!-- Le commentaire sont !important et permettent une bonne interprétation de la propriété CSS inline-block -->\n" +
                           "    <div id=\"menu_button\">\n" +
                           "        \n" +
                           "        <a href=\"#menu\" data-shadow=\"false\" data-iconshadow=\"false\" class=\"ui-icon-nodisc button_submit button_menu\">Menu</a>\n" +
                           "    </div>\n" +
                           "    <!--\n" +
                           "              -->\n" +
                           "    <div id=\"logo\">\n" +
                           "        <a href=\"/\" class=\"logo-nxi\" data-ajax=\"true\" data-hometrim=\"true\"></a>\n" +
                           "    </div>\n" +
                           "    <!--\n" +
                           "                           -->\n" +
                           "    <div id=\"options_button\">\n" +
                           "        <a id=\"option_config\" href=\"#options\" class=\"sprite sprite-ico-options\" data-role=\"none\"></a>\n" +
                           "        <!--\n" +
                           "                               <span id=\"option_notifs\">0</span> -->\n" +
                           "    </div>\n" +
                           "\n" +
                           "\n" +
                           "</header>\n" +
                           "\n" +
                           "\n" +
                           "            <a href=\"#popupDialogBrowser\" id=\"linkToPopupDialogBrowser\" data-rel=\"popup\" data-position-to=\"window\"></a>\n" +
                           "            <div data-role=\"popup\" id=\"popupDialogBrowser\" data-overlay-theme=\"a\" data-theme=\"c\" data-dismissible=\"false\" data-corners=\"false\" style=\"max-width: 700px;\">\n" +
                           "                <a href=\"#\" data-rel=\"back\" data-role=\"button\" data-icon=\"delete\" data-mini=\"true\" data-inline=\"true\" data-iconpos=\"notext\" class=\"ui-btn-right\" id=\"btnClosePopupBrowser\">Close</a>\n" +
                           "                <div data-role=\"content\" data-theme=\"d\" class=\"ui-corner-bottom ui-content\" style=\"padding: 10px!important;\">\n" +
                           "                    <h3 class=\"ui-title\">Vous consultez la version mobile de ce contenu.</h3>\n" +
                           "                    <p><a href=\"#\" id=\"skipua\">Cliquez ici</a> pour être redirigé vers la version complète, ou attendez 5 secondes. Fermez ce pop-up pour continuer sur la version mobile.</p>\n" +
                           "                    <div id=\"defaultCountdown\">\n" +
                           "                        <span id=\"timeRemaining\">5</span><br />\n" +
                           "                        secondes\n" +
                           "                    </div>\n" +
                           "\n" +
                           "                </div>\n" +
                           "            </div>\n" +
                           "\n" +
                           "            \n" +
                           "\n" +
                           "\n" +
                           "            \n" +
                           "\n" +
                           "\n" +
                           "<section id=\"actu_list\" data-role=\"content\" class=\"\">\n" +
                           "  \n" +
                           "    <div id=\"bann_pub\">\n" +
                           "        <script src=\"http://engine.widespace.com/map/engine/dynamic?sid=5d06f2cc-5266-49ce-a30b-8f79b55422dc\" type=\"text/javascript\"></script>\n" +
                           "    </div>\n" +
                           "    \n" +
                           "</section>\n" +
                           "<section id=\"actu_list\" data-role=\"content\" class=\"\">\n" +
                           "\n" +
                           "    \n" +
                           "            <span class=\"actu_separator_date\">Dimanche 12 avril 2015</span>\n" +
                           "        <article data-datepubli=\"12/04/2015 14:26:39\" data-acturowid=\"93786\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93786-le-recap-bons-plans-moment-semaine-15.htm\" data-article-id=\"93786\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/1983.jpg\" alt=\"Le récap' des bons plans du moment, semaine 15\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93786-le-recap-bons-plans-moment-semaine-15.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93786-le-recap-bons-plans-moment-semaine-15.htm\"> Le récap' des bons plans du moment, semaine 15</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">14:26</span><span class=\"soustitre\"> - Cyberattaque sur les prix !</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93786-le-recap-bons-plans-moment-semaine-15.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">12</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"12/04/2015 00:00:00\" data-acturowid=\"93725\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93725-528eme-edition-lidd-liens-idiots-du-dimanche.htm\" data-article-id=\"93725\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6218.jpg\" alt=\"528ème édition des LIDD : Liens Idiots Du Dimanche\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93725-528eme-edition-lidd-liens-idiots-du-dimanche.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93725-528eme-edition-lidd-liens-idiots-du-dimanche.htm\"> 528ème édition des LIDD : Liens Idiots Du Dimanche</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">00:00</span><span class=\"soustitre\"> - Il n&#39;y a pas d&#39;&#226;ge pour jouer aux billes</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93725-528eme-edition-lidd-liens-idiots-du-dimanche.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">37</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "            <span class=\"actu_separator_date\">Samedi 11 avril 2015</span>\n" +
                           "        <article data-datepubli=\"11/04/2015 12:20:32\" data-acturowid=\"93787\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93787-interview-le-droit-d-auteur-et-julia-reda-eurodeputeedu-parti-pirate.htm\" data-article-id=\"93787\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6211.jpg\" alt=\"[Interview] Le droit d’auteur et Julia Reda, eurodéputée du Parti Pirate\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93787-interview-le-droit-d-auteur-et-julia-reda-eurodeputeedu-parti-pirate.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93787-interview-le-droit-d-auteur-et-julia-reda-eurodeputeedu-parti-pirate.htm\"> [Interview] Le droit d’auteur et Julia Reda, eurodéputée du Parti Pirate</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">12:20</span><span class=\"soustitre\"> - Quelle r&#233;forme du droit d&#39;auteur en Europe ?</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93787-interview-le-droit-d-auteur-et-julia-reda-eurodeputeedu-parti-pirate.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">39</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"11/04/2015 10:05:58\" data-acturowid=\"93788\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93788-la-faille-rootpipe-corrigee-dans-yosemite-10-10-3-mais-pas-dans-autres-os-x.htm\" data-article-id=\"93788\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/1596.jpg\" alt=\"La faille Rootpipe corrigée dans Yosemite 10.10.3, mais pas dans les autres OS X\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93788-la-faille-rootpipe-corrigee-dans-yosemite-10-10-3-mais-pas-dans-autres-os-x.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93788-la-faille-rootpipe-corrigee-dans-yosemite-10-10-3-mais-pas-dans-autres-os-x.htm\"> La faille Rootpipe corrigée dans Yosemite 10.10.3, mais pas dans les autres OS X</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">10:05</span><span class=\"soustitre\"> - Trop de boulot !</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93788-la-faille-rootpipe-corrigee-dans-yosemite-10-10-3-mais-pas-dans-autres-os-x.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "       "
                           + "                         <span class=\"nb_comments\">42</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "            <span class=\"actu_separator_date\">Vendredi 10 avril 2015</span>\n" +
                           "        <article data-datepubli=\"10/04/2015 22:57:46\" data-acturowid=\"93775\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93775-windows-10-pour-smartphone-nouvelle-build-10051-pour-dizaines-lumia.htm\" data-article-id=\"93775\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/5258.jpg\" alt=\"Windows 10 pour smartphones : nouvelle build 10051 pour une trentaine de Lumia\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93775-windows-10-pour-smartphone-nouvelle-build-10051-pour-dizaines-lumia.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93775-windows-10-pour-smartphone-nouvelle-build-10051-pour-dizaines-lumia.htm\"> Windows 10 pour smartphones : nouvelle build 10051 pour une trentaine de Lumia</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">22:57</span><span class=\"soustitre\"> - Et paf, les serveurs Insider en rade</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93775-windows-10-pour-smartphone-nouvelle-build-10051-pour-dizaines-lumia.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">78</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 18:04:18\" data-acturowid=\"93782\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93782-cyberattaque-tv5monde-on-est-dans-guerre-information.htm\" data-article-id=\"93782\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6213.jpg\" alt=\"Cyberattaque de TV5Monde : « On est dans une guerre de l'information »\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93782-cyberattaque-tv5monde-on-est-dans-guerre-information.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93782-cyberattaque-tv5monde-on-est-dans-guerre-information.htm\"> Cyberattaque de TV5Monde : « On est dans une guerre de l'information »</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                            <img src=\"/images/common/badge-listing-abo@2x.png\" style=\"border: none;top: 5px;display: inline-block; width: 40px; height: 12px;position: relative;\" alt=\"badge_abonne\" />\n" +
                           "                    <span class=\"date_pub\">18:04</span><span class=\"soustitre\"> - ANSSI font les pirates</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93782-cyberattaque-tv5monde-on-est-dans-guerre-information.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">36</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 17:30:00\" data-acturowid=\"93785\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93785-star-wars-sfr-numerciable-propose-films-en-telechargement-a-2299-unite.htm\" data-article-id=\"93785\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/2743.jpg\" alt=\"[MàJ] Star Wars : 22,99 € le film chez Numericable-SFR, le prix de « la vraie expérience » sur TV\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93785-star-wars-sfr-numerciable-propose-films-en-telechargement-a-2299-unite.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93785-star-wars-sfr-numerciable-propose-films-en-telechargement-a-2299-unite.htm\"> [MàJ] Star Wars : 22,99 € le film chez Numericable-SFR, le prix de « la vraie expérience » sur TV</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">17:30</span><span class=\"soustitre\"> - La &#171; fameuse &#187; r&#232;gle des 30 % ?</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93785-star-wars-sfr-numerciable-propose-films-en-telechargement-a-2299-unite.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">187</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 17:12:24\" data-acturowid=\"93776\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93776-deux-plaintes-deposees-en-france-contre-programmes-surveillance.htm\" data-article-id=\"93776\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/1799.jpg\" alt=\"Deux plaintes déposées en France contre les programmes de surveillance\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93776-deux-plaintes-deposees-en-france-contre-programmes-surveillance.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93776-deux-plaintes-deposees-en-france-contre-programmes-surveillance.htm\"> Deux plaintes déposées en France contre les programmes de surveillance</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">17:12</span><span class=\"soustitre\"> - Pochette sur Prism</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93776-deux-plaintes-deposees-en-france-contre-programmes-surveillance.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">14</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 16:30:00\" data-acturowid=\"93779\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93779-renove-par-google-snapseed-2-0-fait-son-retour-sur-android-et-ios.htm\" data-article-id=\"93779\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6209.jpg\" alt=\"Rénové par Google, Snapseed 2.0 fait son retour sur Android et iOS\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93779-renove-par-google-snapseed-2-0-fait-son-retour-sur-android-et-ios.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93779-renove-par-google-snapseed-2-0-fait-son-retour-sur-android-et-ios.htm\"> Rénové par Google, Snapseed 2.0 fait son retour sur Android et iOS</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">16:30</span><span class=\"soustitre\"> - Votre smartphone ultra-moderne pourra vieillir toutes vos photos</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93779-renove-par-google-snapseed-2-0-fait-son-retour-sur-android-et-ios.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">5</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 16:00:00\" data-acturowid=\"93777\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93777-le-senat-favorable-a-l-open-data-sur-donnees-transport.htm\" data-article-id=\"93777\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/3935.jpg\" alt=\"Le Sénat favorable à l’Open Data sur les données de transport\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93777-le-senat-favorable-a-l-open-data-sur-donnees-transport.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93777-le-senat-favorable-a-l-open-data-sur-donnees-transport.htm\"> Le Sénat favorable à l’Open Data sur les données de transport</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">16:00</span><span class=\"soustitre\"> - M&#233;tro, c&#39;est trop</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93777-le-senat-favorable-a-l-open-data-sur-donnees-transport.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">3</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 15:30:00\" data-acturowid=\"93783\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93783-wordpress-fbi-met-en-garde-contre-vague-dattaques-par-soutiens-daesh.htm\" data-article-id=\"93783\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6206.jpg\" alt=\"Wordpress : le FBI met en garde contre une vague d'attaques par des soutiens de Daesh\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93783-wordpress-fbi-met-en-garde-contre-vague-dattaques-par-soutiens-daesh.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93783-wordpress-fbi-met-en-garde-contre-vague-dattaques-par-soutiens-daesh.htm\"> Wordpress : le FBI met en garde contre une vague d'attaques par des soutiens de Daesh</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">15:30</span><span class=\"soustitre\"> - Fox Mulder m&#232;ne l&#39;enqu&#234;te</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93783-wordpress-fbi-met-en-garde-contre-vague-dattaques-par-soutiens-daesh.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "       "
                           + "                         <span class=\"nb_comments\">37</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 14:45:00\" data-acturowid=\"93780\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93780-les-editeurs-se-dressent-contre-restauration-fonctions-en-ligne-jeux-abandonnes.htm\" data-article-id=\"93780\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/1774.jpg\" alt=\"Les éditeurs se dressent contre la restauration des fonctions en ligne de jeux abandonnés\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93780-les-editeurs-se-dressent-contre-restauration-fonctions-en-ligne-jeux-abandonnes.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93780-les-editeurs-se-dressent-contre-restauration-fonctions-en-ligne-jeux-abandonnes.htm\"> Les éditeurs se dressent contre la restauration des fonctions en ligne de jeux abandonnés</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">14:45</span><span class=\"soustitre\"> - Difficile de s&#39;a-mus&#233;e dans ces conditions</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93780-les-editeurs-se-dressent-contre-restauration-fonctions-en-ligne-jeux-abandonnes.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">74</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 14:00:00\" data-acturowid=\"93757\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93757-arcep-defis-et-enjeux-qui-attendent-regulateur-telecoms.htm\" data-article-id=\"93757\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/1553.jpg\" alt=\"ARCEP : les défis et les enjeux qui attendent le régulateur des télécoms\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93757-arcep-defis-et-enjeux-qui-attendent-regulateur-telecoms.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93757-arcep-defis-et-enjeux-qui-attendent-regulateur-telecoms.htm\"> ARCEP : les défis et les enjeux qui attendent le régulateur des télécoms</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                            <img src=\"/images/common/badge-listing-abo@2x.png\" style=\"border: none;top: 5px;display: inline-block; width: 40px; height: 12px;position: relative;\" alt=\"badge_abonne\" />\n" +
                           "                    <span class=\"date_pub\">14:00</span><span class=\"soustitre\"> - Une ouverture de velours dans une neutralit&#233; de fer</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93757-arcep-defis-et-enjeux-qui-attendent-regulateur-telecoms.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">13</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 11:30:00\" data-acturowid=\"93784\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93784-asustor-ameliore-son-application-looksgood-avec-transcodage-video.htm\" data-article-id=\"93784\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6205.jpg\" alt=\"Asustor améliore son application LooksGood avec du transcodage vidéo\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93784-asustor-ameliore-son-application-looksgood-avec-transcodage-video.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93784-asustor-ameliore-son-application-looksgood-avec-transcodage-video.htm\"> Asustor améliore son application LooksGood avec du transcodage vidéo</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">11:30</span><span class=\"soustitre\"> - LooksInteresting</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93784-asustor-ameliore-son-application-looksgood-avec-transcodage-video.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">2</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 11:00:13\" data-acturowid=\"93781\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93781-une-manifestation-contre-loi-sur-renseignement-organisee-lundi-a-paris.htm\" data-article-id=\"93781\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6204.jpg\" alt=\"Une manifestation contre la loi sur le renseignement organisée lundi à Paris\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93781-une-manifestation-contre-loi-sur-renseignement-organisee-lundi-a-paris.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93781-une-manifestation-contre-loi-sur-renseignement-organisee-lundi-a-paris.htm\">[Br&#232;ve]  Une manifestation contre la loi sur le renseignement organisée lundi à Paris</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">11:00</span><span class=\"soustitre\"> - Avis aux &#171; ex&#233;g&#232;tes amateurs &#187; et opposants de &#171; mauvaise foi &#187;</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93781-une-manifestation-contre-loi-sur-renseignement-organisee-lundi-a-paris.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">58</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 10:20:00\" data-acturowid=\"93770\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93770-deus-ex-mankind-divided-prochain-volet-serie-se-montre-enfin.htm\" data-article-id=\"93770\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/4700.jpg\" alt=\"Deus Ex Mankind Divided : le prochain volet de la série se montre enfin\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93770-deus-ex-mankind-divided-prochain-volet-serie-se-montre-enfin.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93770-deus-ex-mankind-divided-prochain-volet-serie-se-montre-enfin.htm\">[Br&#232;ve]  Deus Ex Mankind Divided : le prochain volet de la série se montre enfin</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">10:20</span><span class=\"soustitre\"> - Vite, oublions The Fall</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93770-deus-ex-mankind-divided-prochain-volet-serie-se-montre-enfin.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">74</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 09:50:00\" data-acturowid=\"93778\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93778-des-hebergeurs-menacent-quitter-france-en-cas-d-adoption-loi-renseignement.htm\" data-article-id=\"93778\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6203.jpg\" alt=\"Des hébergeurs menacent de quitter la France en cas d’adoption de la loi renseignement\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93778-des-hebergeurs-menacent-quitter-france-en-cas-d-adoption-loi-renseignement.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93778-des-hebergeurs-menacent-quitter-france-en-cas-d-adoption-loi-renseignement.htm\"> Des hébergeurs menacent de quitter la France en cas d’adoption de la loi renseignement</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">09:50</span><span class=\"soustitre\"> - Direction Sainte-H&#233;l&#232;ne</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93778-des-hebergeurs-menacent-quitter-france-en-cas-d-adoption-loi-renseignement.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">190</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 09:14:33\" data-acturowid=\"93774\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93774-dropbox-sintegre-desormais-a-office-online.htm\" data-article-id=\"93774\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/3936.jpg\" alt=\"Dropbox s'intègre désormais à Office Online\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93774-dropbox-sintegre-desormais-a-office-online.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93774-dropbox-sintegre-desormais-a-office-online.htm\"> Dropbox s'intègre désormais à Office Online</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">09:14</span><span class=\"soustitre\"> - Et r&#233;ciproquement d&#39;ailleurs</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93774-dropbox-sintegre-desormais-a-office-online.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">24</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"10/04/2015 08:41:32\" data-acturowid=\"93763\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93763-la-norme-hdmi-2-0a-est-officielle-et-prend-en-charge-hdr.htm\" data-article-id=\"93763\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6201.jpg\" alt=\"La norme HDMI 2.0a est officielle et prend en charge le HDR\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93763-la-norme-hdmi-2-0a-est-officielle-et-prend-en-charge-hdr.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93763-la-norme-hdmi-2-0a-est-officielle-et-prend-en-charge-hdr.htm\"> La norme HDMI 2.0a est officielle et prend en charge le HDR</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">08:41</span><span class=\"soustitre\"> - M&#234;me pas 36 Gb/s ?</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93763-la-norme-hdmi-2-0a-est-officielle-et-prend-en-charge-hdr.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">86</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "            <span class=\"actu_separator_date\">Jeudi 09 avril 2015</span>\n" +
                           "        <article data-datepubli=\"09/04/2015 17:47:48\" data-acturowid=\"93740\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93740-test-nxi-cities-skylines-simcity-like-presque-parfait.htm\" data-article-id=\"93740\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6192.jpg\" alt=\"Cities Skylines : un SimCity-like presque parfait\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93740-test-nxi-cities-skylines-simcity-like-presque-parfait.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93740-test-nxi-cities-skylines-simcity-like-presque-parfait.htm\"> Cities Skylines : un SimCity-like presque parfait</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                            <img src=\"/images/common/badge-listing-abo@2x.png\" style=\"border: none;top: 5px;display: inline-block; width: 40px; height: 12px;position: relative;\" alt=\"badge_abonne\" />\n" +
                           "                    <span class=\"date_pub\">17:47</span><span class=\"soustitre\"> - Pour l&#39;ambiance &#231;a va, mais le plat manque un peu de piment</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93740-test-nxi-cities-skylines-simcity-like-presque-parfait.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">68</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"09/04/2015 17:00:00\" data-acturowid=\"93772\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93772-skype-translator-integre-italien-chinois-et-le-text-to-speech.htm\" data-article-id=\"93772\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/1560.jpg\" alt=\"Skype Translator intègre l'italien, le chinois et le « text-to-speech »\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93772-skype-translator-integre-italien-chinois-et-le-text-to-speech.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93772-skype-translator-integre-italien-chinois-et-le-text-to-speech.htm\"> Skype Translator intègre l'italien, le chinois et le « text-to-speech »</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">17:00</span><span class=\"soustitre\"> - On attend toujours les langues de Moli&#232;re et de Goethe</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93772-skype-translator-integre-italien-chinois-et-le-text-to-speech.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">6</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"09/04/2015 16:15:00\" data-acturowid=\"93769\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93769-microsoft-annonce-son-nano-server-tourne-vers-cloud.htm\" data-article-id=\"93769\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/5258.jpg\" alt=\"Microsoft annonce son Windows Nano Server minimal pour 2016\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93769-microsoft-annonce-son-nano-server-tourne-vers-cloud.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93769-microsoft-annonce-son-nano-server-tourne-vers-cloud.htm\"> Microsoft annonce son Windows Nano Server minimal pour 2016</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">16:15</span><span class=\"soustitre\"> - Il en faut peu pour &#234;tre heureux</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93769-microsoft-annonce-son-nano-server-tourne-vers-cloud.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">92</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"09/04/2015 15:30:00\" data-acturowid=\"93771\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93771-mise-a-jour-thecusos-5-0-nombreuses-failles-bouchees-et-kodi-pour-certains-nas.htm\" data-article-id=\"93771\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/2927.jpg\" alt=\"Mise à jour de ThecusOS 5.0 : de nombreuses failles bouchées et Kodi pour certains NAS\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93771-mise-a-jour-thecusos-5-0-nombreuses-failles-bouchees-et-kodi-pour-certains-nas.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93771-mise-a-jour-thecusos-5-0-nombreuses-failles-bouchees-et-kodi-pour-certains-nas.htm\"> Mise à jour de ThecusOS 5.0 : de nombreuses failles bouchées et Kodi pour certains NAS</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">15:30</span><span class=\"soustitre\"> - Il &#233;tait temps...</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93771-mise-a-jour-thecusos-5-0-nombreuses-failles-bouchees-et-kodi-pour-certains-nas.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">6</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"09/04/2015 14:45:00\" data-acturowid=\"93760\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93760-facebook-reconnaissance-faciale-serait-illegale-dans-illinois.htm\" data-article-id=\"93760\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6191.jpg\" alt=\"Facebook : la légalité de la reconnaissance faciale remise en cause dans l’Illinois\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93760-facebook-reconnaissance-faciale-serait-illegale-dans-illinois.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93760-facebook-reconnaissance-faciale-serait-illegale-dans-illinois.htm\"> Facebook : la légalité de la reconnaissance faciale remise en cause dans l’Illinois</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">14:45</span><span class=\"soustitre\"> - Ce vieux, tr&#232;s vieux combat entre l&#39;opt-in et l&#39;opt-out</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93760-facebook-reconnaissance-faciale-serait-illegale-dans-illinois.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">43</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"09/04/2015 14:00:00\" data-acturowid=\"93768\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93768-microsoft-planche-sur-nouvelle-extension-pour-age-of-empires-ii.htm\" data-article-id=\"93768\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6189.jpg\" alt=\"Microsoft planche sur une nouvelle « extension » pour Age of Empires II\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93768-microsoft-planche-sur-nouvelle-extension-pour-age-of-empires-ii.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93768-microsoft-planche-sur-nouvelle-extension-pour-age-of-empires-ii.htm\"> Microsoft planche sur une nouvelle « extension » pour Age of Empires II</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">14:00</span><span class=\"soustitre\"> - Les guillemets ont leur importance</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93768-microsoft-planche-sur-nouvelle-extension-pour-age-of-empires-ii.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">155</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"09/04/2015 12:00:00\" data-acturowid=\"93765\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93765-youtube-prepare-terrain-a-offre-dabonnement-sans-publicite.htm\" data-article-id=\"93765\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/1783.jpg\" alt=\"YouTube prépare le terrain à une offre d'abonnement sans publicité\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93765-youtube-prepare-terrain-a-offre-dabonnement-sans-publicite.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93765-youtube-prepare-terrain-a-offre-dabonnement-sans-publicite.htm\"> YouTube prépare le terrain à une offre d'abonnement sans publicité</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">12:00</span><span class=\"soustitre\"> - Le 15 juin de nouvelles conditions de mon&#233;tisation</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93765-youtube-prepare-terrain-a-offre-dabonnement-sans-publicite.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">213</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"09/04/2015 11:21:05\" data-acturowid=\"93761\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93761-yosemite-10-10-3-et-ios-8-3-photos-emojis-corrections-et-multiples-ameliorations.htm\" data-article-id=\"93761\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/3660.jpg\" alt=\"Yosemite 10.10.3 et iOS 8.3 : Photos, emojis, corrections et multiples améliorations\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93761-yosemite-10-10-3-et-ios-8-3-photos-emojis-corrections-et-multiples-ameliorations.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93761-yosemite-10-10-3-et-ios-8-3-photos-emojis-corrections-et-multiples-ameliorations.htm\"> Yosemite 10.10.3 et iOS 8.3 : Photos, emojis, corrections et multiples améliorations</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">11:21</span><span class=\"soustitre\"> - Attention &#224; votre espace de stockage iCloud</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93761-yosemite-10-10-3-et-ios-8-3-photos-emojis-corrections-et-multiples-ameliorations.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">24</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"09/04/2015 10:30:00\" data-acturowid=\"93766\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93766-zynga-don-mattrick-laisse-son-fauteuil-pdg-et-empoche-188-millions-dollars.htm\" data-article-id=\"93766\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/1777.jpg\" alt=\"Zynga : Don Mattrick laisse son fauteuil de PDG et empoche 18,8 millions de dollars\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93766-zynga-don-mattrick-laisse-son-fauteuil-pdg-et-empoche-188-millions-dollars.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93766-zynga-don-mattrick-laisse-son-fauteuil-pdg-et-empoche-188-millions-dollars.htm\"> Zynga : Don Mattrick laisse son fauteuil de PDG et empoche 18,8 millions de dollars</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">10:30</span><span class=\"soustitre\"> - Mattrick s&#39;&#233;l&#232;ve vers de nouveaux horizons</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93766-zynga-don-mattrick-laisse-son-fauteuil-pdg-et-empoche-188-millions-dollars.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">98</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"09/04/2015 09:56:43\" data-acturowid=\"93767\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93767-tv5monde-pirate-ecran-noir-sur-11-chaines-et-propagande-sur-reseaux-sociaux.htm\" data-article-id=\"93767\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/6185.jpg\" alt=\"TV5Monde piraté : écran noir sur les 11 chaines et propagande sur les réseaux sociaux\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93767-tv5monde-pirate-ecran-noir-sur-11-chaines-et-propagande-sur-reseaux-sociaux.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93767-tv5monde-pirate-ecran-noir-sur-11-chaines-et-propagande-sur-reseaux-sociaux.htm\"> TV5Monde piraté : écran noir sur les 11 chaines et propagande sur les réseaux sociaux</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">09:56</span><span class=\"soustitre\"> - &#201;clipse totale des programmes</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93767-tv5monde-pirate-ecran-noir-sur-11-chaines-et-propagande-sur-reseaux-sociaux.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">151</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "        <article data-datepubli=\"09/04/2015 09:18:36\" data-acturowid=\"93538\">\n" +
                           "            <div>\n" +
                           "            <div>\n" +
                           "\n" +
                           "                <a href=\"/news/93538-le-paiement-par-bitcoin-est-disponible-pour-nos-abonnements.htm\" data-article-id=\"93538\"><img data-frz-src=\"//az664837.vo.msecnd.net/images/bd/square-linked-media/1547.jpg\" alt=\"Le paiement par Bitcoin est disponible pour nos abonnements\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" onload=\"lzld(this)\" onerror=\"lzld(this)\" class=\"ded-image\" /></a>\n" +
                           "\n" +
                           "            </div>\n" +
                           "\n" +
                           "                <div>\n" +
                           "\n" +
                           "                    <h1><a href=\"/news/93538-le-paiement-par-bitcoin-est-disponible-pour-nos-abonnements.htm\" class=\"ui-link\" data-customfetch=\"true\" data-key=\"93538-le-paiement-par-bitcoin-est-disponible-pour-nos-abonnements.htm\">[Blog]  Le paiement par Bitcoin est disponible pour nos abonnements</a></h1>\n" +
                           "\n" +
                           "\n" +
                           "                    <p>\n" +
                           "                    <span class=\"date_pub\">09:18</span><span class=\"soustitre\"> - Minez, soutenez !</span>\n" +
                           "                </p>\n" +
                           "\n" +
                           "                            <a class=\"notif_link ui-link\" href=\"/news/93538-le-paiement-par-bitcoin-est-disponible-pour-nos-abonnements.htm?vc=1\">\n" +
                           "                                <span class=\"sprite sprite-ico-commentaire\"></span>\n" + "                                <span class=\"nb_comments\">97</span>\n"
                           +
                           "                            </a>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "            </div>\n" +
                           "        </div>\n" +
                           "    </article>\n" +
                           "\n" +
                           "    <div id=\"loader_actu_list\">\n" +
                           "        <img src=\"/images/common/ajax-loader.gif\" /><br />\n" +
                           "        Chargement des actualités précédentes\n" +
                           "    </div>\n" +
                           "        <nav class=\"pager\" id=\"pagerListActu\">\n" +
                           "        <ul>\n" +
                           "                <li><a class=\"btn_pager_page_active\">1</a></li>\n" +
                           "                <li><a class=\"btn_pager_page\" href=\"?page=2\">2</a></li>\n" +
                           "                <li><a class=\"btn_pager_page\" href=\"?page=3\">3</a></li>\n" +
                           "                <li><a class=\"btn_pager_page\" href=\"?page=4\">4</a></li>\n" +
                           "                <li><a class=\"spriteactu spriteactu-ico-fleche-droite\" href=\"?page=2\"></a></li>\n" +
                           "\n" +
                           "        </ul>\n" +
                           "    </nav>\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "</section>\n" +
                           "            <footer id=\"footer\" data-role=\"footer\">\n" +
                           "    \n" +
                           "    <button id=\"btn_to_top\" class=\"button_submit btn_footer_up\" data-role=\"none\"><span class=\"sprite sprite-ico-up\"></span></button>\n" +
                           "\n" +
                           "    <p>Tous droits réservés &copy; 2003 - 2015 </p>\n" +
                           "    <p>PC INpact SARL de presse</p>\n" +
                           "    <br/>\n" +
                           "    <a id=\"skipua\" href=\"#\" data-role=\"none\">Voir ce contenu avec la version classique</a><br/>\n" +
                           "\n" +
                           "  \n" +
                           "</footer>\n" +
                           "\n" +
                           "\n" +
                           "        </section>\n" +
                           "    </div>\n" +
                           "\n" +
                           "    <script type=\"text/javascript\">\n" +
                           "        var _gaq = _gaq || [];\n" +
                           "\n" +
                           "        (function () {\n" +
                           "            var ga = document.createElement('script');\n" +
                           "            ga.type = 'text/javascript';\n" +
                           "            ga.async = true;\n" +
                           "            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" +
                           "            var s = document.getElementsByTagName('script')[0];\n" +
                           "            s.parentNode.insertBefore(ga, s);\n" +
                           "        })();\n" +
                           "\n" +
                           "        $(document).on('pagebeforeshow', '[data-role=page]', function (event, ui) {\n" +
                           "            try {\n" +
                           "                _gaq.push(['_setAccount', 'UA-10957917-5']);\n" +
                           "                hash = location.hash;\n" +
                           "                if (hash) {\n" +
                           "                    _gaq.push(['_trackPageview', hash.substr(1)]);\n" +
                           "                } else {\n" +
                           "                    _gaq.push(['_trackPageview']);\n" +
                           "                }\n" +
                           "            } catch (err) { }\n" +
                           "        });\n" +
                           "\n" +
                           "    </script>\n" +
                           "\n" +
                           "    \n" +
                           " \n" +
                           "\n" +
                           "</body>\n" +
                           "</html>\n";

        /**
         * Traitement du fichier...
         */
        ArrayList<ArticleItem> articlesCalcules;
        articlesCalcules = ParseurHTML.getListeArticles(contenuIS, Constantes.NEXT_INPACT_URL);

        /**
         * Vérification...
         */
        for (int i = 0; i < Constantes.NB_ARTICLES_PAR_PAGE; i++){
            assertEquals(mesArticles.get(i).getId(), articlesCalcules.get(i).getId());
            assertEquals(mesArticles.get(i).getTimeStampPublication(), articlesCalcules.get(i).getTimeStampPublication());
            assertEquals(mesArticles.get(i).getUrlIllustration(), articlesCalcules.get(i).getUrlIllustration());
            assertEquals(mesArticles.get(i).getUrl(), articlesCalcules.get(i).getUrl());
            assertEquals(mesArticles.get(i).getTitre(), articlesCalcules.get(i).getTitre());
            assertEquals(mesArticles.get(i).getSousTitre(), articlesCalcules.get(i).getSousTitre());
            assertEquals(mesArticles.get(i).getNbCommentaires(), articlesCalcules.get(i).getNbCommentaires());
            assertEquals(mesArticles.get(i).isAbonne(), articlesCalcules.get(i).isAbonne());
        }
    }
}