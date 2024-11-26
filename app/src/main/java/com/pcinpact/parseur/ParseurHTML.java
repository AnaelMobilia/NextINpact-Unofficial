/*
 * Copyright 2013 - 2024 Anael Mobilia and contributors
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

import android.util.Log;

import com.pcinpact.R;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.Item;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyDateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parseur du code HTML
 *
 * @author Anael
 */
public class ParseurHTML {
    /**
     * Parse la liste des articles + le brief
     *
     * @param unContenu contenu HTML brut
     * @param currentTs Timestamp du téléchargement
     * @return liste d'articleItem
     */
    public static ArrayList<ArticleItem> getListeArticles(final String unContenu, final long currentTs) {
        ArrayList<ArticleItem> mesArticlesItem = new ArrayList<>();

        try {
            // Récupération du HTML
            Document maPage = Jsoup.parse(unContenu);
            Elements mesArticles = maPage.select("div[data-post-id]");

            ArticleItem monArticleItem;
            // Pour chaque article
            for (Element unArticle : mesArticles) {
                monArticleItem = new ArticleItem();
                Elements maSelection;
                String maValeur;

                // Timestamp de téléchargement
                monArticleItem.setTimestampDl(currentTs);

                // ID de l'article
                monArticleItem.setId(Integer.parseInt(unArticle.attr("data-post-id")));

                // Date de publication de l'article
                maSelection = unArticle.select("p[class=next-post-time] > abbr");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).attr("title");
                    monArticleItem.setTimestampPublication(MyDateUtils.convertToTimestamp(maValeur));
                }

                // URL Seo + type (brief / article)
                maSelection = unArticle.select("h1[class=next-post-title] > a");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).attr("href");
                    monArticleItem.setURLseo(maValeur);

                    if (maValeur.contains(Constantes.NEXT_TYPE_ARTICLES_BRIEF)) {
                        monArticleItem.setIsBrief(true);
                    } else {
                        monArticleItem.setIsBrief(false);
                    }
                }

                // URL de l'image d'illustration (seulement pour les articles)
                if (!monArticleItem.getIsBrief()) {
                    maSelection = unArticle.select("img");
                    if (!maSelection.isEmpty()) {
                        maValeur = maSelection.get(0).attr("src");
                        monArticleItem.setUrlIllustration(maValeur);
                    }
                }

                // Titre de l'article
                maSelection = unArticle.select("h1[class=next-post-title]");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).text();
                    monArticleItem.setTitre(maValeur);
                }

                // Sous-titre
                maSelection = unArticle.select("h2[class=next-post-subtitle]");
                if (!maSelection.isEmpty()) {
                    maValeur = maSelection.get(0).text();
                    monArticleItem.setSousTitre(maValeur);
                }

                mesArticlesItem.add(monArticleItem);
            }
        } catch (NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getListeArticles() - Crash", e);
            }
        }

        return mesArticlesItem;
    }

    /**
     * Parse les commentaires
     *
     * @param unContenu contenu JSON brut
     * @param headers   entêtes bruts (Nb total de commentaires)
     * @param idArticle ID de l'article
     * @return liste de CommentaireItem (10 premiers commentaires) et ArticleItem (Nb total de commentaires)
     */
    public static ArrayList<Item> getCommentaires(final String unContenu, final String headers, final int idArticle) {
        // mon retour
        ArrayList<Item> monRetour = new ArrayList<>();

        // Mon Article
        ArticleItem monArticle = new ArticleItem();
        monArticle.setId(idArticle);
        // Nombre total de commentaires
        Pattern p = Pattern.compile(Constantes.NEXT_URL_COMMENTAIRES_HEADER_NB_TOTAL + "(\\d+)\n");
        Matcher m = p.matcher(headers);
        while (m.find()) {
            monArticle.setNbCommentaires(Integer.parseInt(m.group(1)));
        }
        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("ParseurHTML", "getCommentaires() - " + Constantes.NEXT_URL_COMMENTAIRES_HEADER_NB_TOTAL + " " + monArticle.getNbCommentaires() + " - idArticle : " + idArticle);
        }

        monRetour.add(monArticle);

        try {
            // Récupération du JSON
            JSONArray lesCommentaires = new JSONArray(unContenu);

            CommentaireItem monCommentaireItem;
            // Pour chaque commentaire
            for (int i = 0; i < lesCommentaires.length(); i++) {
                JSONObject unCommentaire = lesCommentaires.getJSONObject(i);
                monCommentaireItem = new CommentaireItem();
                monCommentaireItem.setIdArticle(idArticle);

                // ID du commentaire
                monCommentaireItem.setId(unCommentaire.getInt("id"));

                // Auteur
                monCommentaireItem.setAuteur(Parser.unescapeEntities(unCommentaire.getString("next_author"), true));

                // Date
                monCommentaireItem.setTimestampPublication(MyDateUtils.convertToTimestamp(unCommentaire.getString("date")));

                // Contenu
                String contenuHtml = unCommentaire.getJSONObject("content").getString("rendered");
                // Enlever le retour à la ligne final
                contenuHtml = contenuHtml.trim();

                // TODO - https://github.com/AnaelMobilia/NextINpact-Unofficial/issues/309#issuecomment-1796525253
                //  Peut-être "statut : approved"
                // Commentaires modérés
                /*
                if (unCommentaire.optInt("moderationReasonId") != 0) {
                    DateFormat dfm = new SimpleDateFormat(Constantes.FORMAT_AFFICHAGE_COMMENTAIRE_DATE_HEURE, Constantes.LOCALE);

                    contenuHtml = "<em>Commentaire de " + monCommentaireItem.getAuteur() + " a été modéré " + dfm.format(new Date(TimeUnit.SECONDS.toMillis(monCommentaireItem.getTimestampPublication()))) + " : " + unCommentaire.getJSONObject("moderationReason").getString("content") + "</em>";
                }
                 */

                // Texte cité ex  > texte cité
                contenuHtml = contenuHtml.replaceAll("<p>&gt;(.*)</p>", Constantes.TAG_HTML_QUOTE_OPEN + "$1" + Constantes.TAG_HTML_QUOTE_CLOSE);

                // Citations - "En réponse à xxx"
                int parentId = unCommentaire.getInt("parent");
                if (parentId != 0) {
                    monCommentaireItem.setIdParent(parentId);
                }

                // Mis dans une div sinon #246 #151 (Cf ba64faeab9e5fe8f6d2f993777fea378830c323f)
                // Remplacement des citations "blockquote" par la custom
                contenuHtml = contenuHtml.replace("<blockquote>", Constantes.TAG_HTML_QUOTE_OPEN);
                contenuHtml = contenuHtml.replace("</blockquote>", Constantes.TAG_HTML_QUOTE_CLOSE);

                // Gras - ex : **texte**
                // .*? => .* en mode ungreedy (merci Java :-))
                contenuHtml = contenuHtml.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");

                // Italique - ex : *jekyll <jesaispluslenomdel'argument> ;*
                contenuHtml = contenuHtml.replaceAll("\\*(.*?)\\*", "<i>$1</i>");

                // Barré - ex : ~~texte~~
                contenuHtml = contenuHtml.replaceAll("~~(.*?)~~", "<s>$1</s>");

                // Lien - ex : [Texte](http://)
                // 1. Reformatage des données qui arrivent sont forme de lien + markdown
                // ex : des [droits](<a href="https://next.ink/131132/france-travail-pirate-les-donnees-de-43-millions-de-personnes-potentiellement-derobees/#comment-archor-2127552" rel="ugc">https://next.ink/131132/france-travail-pirate-les-donnees-de-43-millions-de-personnes-potentiellement-derobees/#comment-archor-2127552</a>)
                contenuHtml = contenuHtml.replaceAll("]\\(<a href=\"(.+)\".*>(\\1)</a>\\)", "]($1)");
                // 2. Parsage habituel du markdown
                contenuHtml = contenuHtml.replaceAll("\\[(.*?)]\\((.*?)\\)", "<a href=\"$2\">$1</a>");

                // Smileys - Fix certains smileys sont déjà rendus par WP
                // <img src="https://next.ink/wp-includes/images/smilies/mrgreen.png" alt=":mrgreen:" class="wp-smiley" style="height: 1em; max-height: 1em;" />
                contenuHtml = contenuHtml.replaceAll("<img src=\".+([a-zA-Z0-9-_]+)\\.[a-z]{3}\" alt=\":\\1:\" class=\"wp-smiley\" style=\".+\" />", ":$1:");

                // TODO - https://github.com/NextINpact/Next/issues/160
                // Smiley ex : :inpactitude: (via replace au lieu d'une regexp paramétrée pour aller plus vite)
                // Liste des smileys => https://api-v1.nextinpact.com/api/v1/Commentaire/smileys
                // regexp : .*tag":"(.*)".*,"image":"(.*)".* ==> contenuHtml = contenuHtml.replace("$1", "<img src=\\"" +
                // Constantes.X_CDN_SMILEY_URL + "$2\\" />");\n
                contenuHtml = contenuHtml.replace(":santa_flock:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "santa_flock.gif\" />");
                contenuHtml = contenuHtml.replace(":windu:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "windu.gif\" />");
                contenuHtml = contenuHtml.replace(":baffe:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "baffe.gif\" />");
                contenuHtml = contenuHtml.replace(":stress:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "stress.gif\" />");
                contenuHtml = contenuHtml.replace(":jesquate:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "jesquate.gif\" />");
                contenuHtml = contenuHtml.replace(":xzombi:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cerf.gif\" />");
                contenuHtml = contenuHtml.replace(":oui2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "oui2.gif\" />");
                contenuHtml = contenuHtml.replace(":duel1:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "lsvader.gif\" />");
                contenuHtml = contenuHtml.replace(":D", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_mrgreen.gif\" />");
                contenuHtml = contenuHtml.replace(":-D", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "biggerGrin.gif\" />");
                contenuHtml = contenuHtml.replace(":non:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ripeer.gif\" />");
                contenuHtml = contenuHtml.replace(":mdr:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "laugh.gif\" />");
                contenuHtml = contenuHtml.replace(":incline:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bowdown.gif\" />");
                contenuHtml = contenuHtml.replace(":yes:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "yaisse.gif\" />");
                contenuHtml = contenuHtml.replace(":chinois:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "chinese.gif\" />");
                contenuHtml = contenuHtml.replace(":fumer:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "hat.gif\" />");
                contenuHtml = contenuHtml.replace(":craint:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "frown.gif\" />");
                contenuHtml = contenuHtml.replace(":pleure:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "pleure.gif\" />");
                contenuHtml = contenuHtml.replace(":mad2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mad2.gif\" />");
                contenuHtml = contenuHtml.replace(":oops:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_redface.gif\" />");
                contenuHtml = contenuHtml.replace(":keskidit:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "keskidit2.gif\" />");
                contenuHtml = contenuHtml.replace(":byebye:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "byebye.gif\" />");
                contenuHtml = contenuHtml.replace(":fou:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "fou.gif\" />");
                contenuHtml = contenuHtml.replace(":prof:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "prof.gif\" />");
                contenuHtml = contenuHtml.replace(":8", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "lunettes1.gif\" />");
                contenuHtml = contenuHtml.replace(":love:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "love.gif\" />");
                contenuHtml = contenuHtml.replace(":roll:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_rolleyes.gif\" />");
                contenuHtml = contenuHtml.replace(":ooo:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ooo.gif\" />");
                contenuHtml = contenuHtml.replace(":francais:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "francais2.gif\" />");
                contenuHtml = contenuHtml.replace(":eeek2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "eeek2.gif\" />");
                contenuHtml = contenuHtml.replace(":bravo:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bravo.gif\" />");
                contenuHtml = contenuHtml.replace(":reflechis:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "reflechis.gif\" />");
                contenuHtml = contenuHtml.replace(":dors:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "dors2.gif\" />");
                contenuHtml = contenuHtml.replace(":cartonjaune:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cartonjaune.gif\" />");
                contenuHtml = contenuHtml.replace(":cartonrouge:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cartonrouge.gif\" />");
                contenuHtml = contenuHtml.replace(":mad:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mad.gif\" />");
                contenuHtml = contenuHtml.replace(":smack:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "smack.gif\" />");
                contenuHtml = contenuHtml.replace(":ouioui:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ouioui.gif\" />");
                contenuHtml = contenuHtml.replace(":censored:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "censored.gif\" />");
                contenuHtml = contenuHtml.replace(":transpi:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "transpi.gif\" />");
                contenuHtml = contenuHtml.replace(":langue:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "langue.gif\" />");
                contenuHtml = contenuHtml.replace(":mdr2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mdr2.gif\" />");
                contenuHtml = contenuHtml.replace(":bocul:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bocul.gif\" />");
                contenuHtml = contenuHtml.replace(":glasses:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "glasses.gif\" />");
                contenuHtml = contenuHtml.replace(":google:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "google.gif\" />");
                contenuHtml = contenuHtml.replace(":humour:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "humour.png\" />");
                contenuHtml = contenuHtml.replace(":heben:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "heben.png\" />");
                contenuHtml = contenuHtml.replace(":arrow:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_arrow.gif\" />");
                contenuHtml = contenuHtml.replace(":mrgreen:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_mrgreen.gif\" />");
                contenuHtml = contenuHtml.replace(":fume:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "fume.gif\" />");
                contenuHtml = contenuHtml.replace(":frown:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "frown.gif\" />");
                contenuHtml = contenuHtml.replace(":embarassed:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "embarassed.gif\" />");
                contenuHtml = contenuHtml.replace(":eeek:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "eek.gif\" />");
                contenuHtml = contenuHtml.replace(":duelsw:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "duelSW.gif\" />");
                contenuHtml = contenuHtml.replace(":devil:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "devil.gif\" />");
                contenuHtml = contenuHtml.replace(":copain:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "copain.png\" />");
                contenuHtml = contenuHtml.replace(":bouletdujour:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bouletdujour.gif\" />");
                contenuHtml = contenuHtml.replace(":boulet:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "boulet.gif\" />");
                contenuHtml = contenuHtml.replace(":birthday:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "birthday.gif\" />");
                contenuHtml = contenuHtml.replace(":ouimaistusors:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ouimaistusors.gif\" />");
                contenuHtml = contenuHtml.replace(":musique:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "music.gif\" />");
                contenuHtml = contenuHtml.replace(":merci:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "merci.gif\" />");
                contenuHtml = contenuHtml.replace(":best:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "meilleur.gif\" />");
                contenuHtml = contenuHtml.replace(":iloveyou:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "loveeyessmly.gif\" />");
                contenuHtml = contenuHtml.replace(":kimouss:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "kimouss.gif\" />");
                contenuHtml = contenuHtml.replace(":kill:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "kill.gif\" />");
                contenuHtml = contenuHtml.replace(":neutral:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icon_neutral.gif\" />");
                contenuHtml = contenuHtml.replace(":zzz:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "zzzzz.gif\" />");
                contenuHtml = contenuHtml.replace(":youhou:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "youhou.gif\" />");
                contenuHtml = contenuHtml.replace(":yoda:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "yoda.gif\" />");
                contenuHtml = contenuHtml.replace(":vomi2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "vomi2.gif\" />");
                contenuHtml = contenuHtml.replace(":vomi1:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "vomi1.gif\" />");
                contenuHtml = contenuHtml.replace(":inpactitude:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "inpactitude3.gif\" />");
                contenuHtml = contenuHtml.replace(":tchintchin:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "tchin.gif\" />");
                contenuHtml = contenuHtml.replace(":sm:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "sm.gif\" />");
                contenuHtml = contenuHtml.replace(":rhooo:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "rhooo.gif\" />");
                contenuHtml = contenuHtml.replace(":bigssourire:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "biggerGrin.gif\" />");
                contenuHtml = contenuHtml.replace(":nonnon:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ripeer.gif\" />");
                contenuHtml = contenuHtml.replace(":yaisse:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "yaisse.gif\" />");
                contenuHtml = contenuHtml.replace(":crever:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "crever.gif\" />");
                contenuHtml = contenuHtml.replace(":cap:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "maitrecapello.gif\" />");
                contenuHtml = contenuHtml.replace(":naz:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "naz.gif\" />");
                contenuHtml = contenuHtml.replace(":supervomi:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "supervomi.gif\" />");
                contenuHtml = contenuHtml.replace(":pet:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "pet.gif\" />");
                contenuHtml = contenuHtml.replace(":roule2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "roule2.gif\" />");
                contenuHtml = contenuHtml.replace(":dent:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "dent.gif\" />");
                contenuHtml = contenuHtml.replace(":singe:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "singe.gif\" />");
                contenuHtml = contenuHtml.replace(":mega:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mega.gif\" />");
                contenuHtml = contenuHtml.replace(":musicos:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "musicos.gif\" />");
                contenuHtml = contenuHtml.replace(":roule:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "roule.gif\" />");
                contenuHtml = contenuHtml.replace(":dd:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "dd.gif\" />");
                contenuHtml = contenuHtml.replace(":phibee:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "phibee.gif\" />");
                contenuHtml = contenuHtml.replace(":fete:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "fete.gif\" />");
                contenuHtml = contenuHtml.replace(":cul:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cul.gif\" />");
                contenuHtml = contenuHtml.replace(":lapin:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "lapin.gif\" />");
                contenuHtml = contenuHtml.replace(":ane:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ane.gif\" />");
                contenuHtml = contenuHtml.replace(":fou3:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "fou3.gif\" />");
                contenuHtml = contenuHtml.replace(":poke:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "poke.gif\" />");
                contenuHtml = contenuHtml.replace(":icq:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "icq.gif\" />");
                contenuHtml = contenuHtml.replace(":surenchere:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "surenchere.gif\" />");
                contenuHtml = contenuHtml.replace(":dix:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "dix.gif\" />");
                contenuHtml = contenuHtml.replace(":neuf:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "neuf.gif\" />");
                contenuHtml = contenuHtml.replace(":huit:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "huit.gif\" />");
                contenuHtml = contenuHtml.replace(":sept:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "sept.gif\" />");
                contenuHtml = contenuHtml.replace(":six:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "six.gif\" />");
                contenuHtml = contenuHtml.replace(":cinq:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "cinq.gif\" />");
                contenuHtml = contenuHtml.replace(":quatre:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "quatre.gif\" />");
                contenuHtml = contenuHtml.replace(":trois:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "trois.gif\" />");
                contenuHtml = contenuHtml.replace(":deux:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "deux.gif\" />");
                contenuHtml = contenuHtml.replace(":un:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "un.gif\" />");
                contenuHtml = contenuHtml.replace(":zero:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "zero.gif\" />");
                contenuHtml = contenuHtml.replace(":top:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "top.gif\" />");
                contenuHtml = contenuHtml.replace(":accident:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "accident.gif\" />");
                contenuHtml = contenuHtml.replace(":tristan:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bosse.gif\" />");
                contenuHtml = contenuHtml.replace(":baton:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "baton.gif\" />");
                contenuHtml = contenuHtml.replace(":prison:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "prison.gif\" />");
                contenuHtml = contenuHtml.replace(":faim:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "faim.gif\" />");
                contenuHtml = contenuHtml.replace(":photo:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "photo.gif\" />");
                contenuHtml = contenuHtml.replace(":nimp:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "nimp.gif\" />");
                contenuHtml = contenuHtml.replace(":ecrit:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "ecrit.gif\" />");
                contenuHtml = contenuHtml.replace(":chant:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "chant.gif\" />");
                contenuHtml = contenuHtml.replace(":brice:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "brice.gif\" />");
                contenuHtml = contenuHtml.replace(":kc:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "kc.gif\" />");
                contenuHtml = contenuHtml.replace(":mike:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mike.gif\" />");
                contenuHtml = contenuHtml.replace(":fr:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "france.gif\" />");
                contenuHtml = contenuHtml.replace(":bisous:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "bisous.gif\" />");
                contenuHtml = contenuHtml.replace(":win:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "win.gif\" />");
                contenuHtml = contenuHtml.replace(":chaud:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "chaud.gif\" />");
                contenuHtml = contenuHtml.replace(":pleure2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "pleure2.gif\" />");
                contenuHtml = contenuHtml.replace(":muscu:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "muscu.gif\" />");
                contenuHtml = contenuHtml.replace(":cbon:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "mangezen.gif\" />");
                contenuHtml = contenuHtml.replace(":pastaper:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "pastaper.gif\" />");
                contenuHtml = contenuHtml.replace(":inpactitude2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "inpactitude2.gif\" />");
                contenuHtml = contenuHtml.replace(":troll:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "troll.gif\" />");
                contenuHtml = contenuHtml.replace(":phiphi:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "phiphi.gif\" />");
                contenuHtml = contenuHtml.replace(":perv:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "perv.gif\" />");
                contenuHtml = contenuHtml.replace(":x:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "x.jpg\" />");
                contenuHtml = contenuHtml.replace(":rtfm:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "rtfm.gif\" />");
                contenuHtml = contenuHtml.replace(":marin:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "marin.gif\" />");
                contenuHtml = contenuHtml.replace(":breton:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "breton.gif\" />");
                contenuHtml = contenuHtml.replace(":google2:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "google2.gif\" />");
                contenuHtml = contenuHtml.replace(":zarb:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "zarb.gif\" />");
                contenuHtml = contenuHtml.replace(":sucre:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "sucre.gif\" />");
                contenuHtml = contenuHtml.replace(":rem:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "rem.gif\" />");
                contenuHtml = contenuHtml.replace(":plantage:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "plantage.gif\" />");
                contenuHtml = contenuHtml.replace(":auto:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "auto.gif\" />");
                contenuHtml = contenuHtml.replace(":pciwin:", "<img src=\"" + Constantes.X_CDN_SMILEY_URL + "champion.gif\" />");

                monCommentaireItem.setCommentaire(contenuHtml);
                // Et je le stocke
                monRetour.add(monCommentaireItem);
            }
        } catch (JSONException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ParseurHTML", "getCommentaires() - Crash JSON", e);
            }
        }

        return monRetour;
    }
}