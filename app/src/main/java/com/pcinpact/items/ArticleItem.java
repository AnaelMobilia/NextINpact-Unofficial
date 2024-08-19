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
package com.pcinpact.items;

import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyDateUtils;

/**
 * Objet Article.
 *
 * @author Anael
 */
public class ArticleItem implements Item {
    /**
     * ID de l'article
     */
    private int id;
    /**
     * Titre de l'article
     */
    private String titre;
    /**
     * Sous-titre de l'article
     */
    private String sousTitre = "";
    /**
     * Est-ce un article abonné ?
     */
    private boolean isAbonne = false;
    /**
     * Nombre de commentaires de l'article
     */
    private int nbCommentaires = 0;
    /**
     * URL de la miniature de l'article
     */
    private String urlIllustration;
    /**
     * Contenu de l'article
     */
    private String contenu = "";
    /**
     * Timestamp de publication de l'article
     */
    private long timestampPublication;
    /**
     * L'article est-il déjà lu ?
     */
    private boolean isLu = false;
    /**
     * Le contenu abonné a-t-il été téléchargé ?
     */
    private boolean isDlContenuAbonne = false;
    /**
     * Indice du dernier commentaire lu
     */
    private int indiceDernierCommLu = 0;
    /**
     * URL SEO de l'article
     */
    private String URLseo;
    /**
     * Dernier ID de commentaire lors du parsage
     */
    private int parseurLastCommentId = 0;
    /**
     * Timestamp de téléchargement
     */
    private long timestampDl;
    /**
     * Timestamp de modification (non persistent)
     */
    private long timestampModification;

    @Override
    public int getType() {
        return Item.TYPE_ARTICLE;
    }

    /**
     * Heure et minute de la publication sous forme textuelle
     *
     * @return Heure & minute de la publication
     */
    public String getHeureMinutePublication() {
        return MyDateUtils.formatDate(Constantes.FORMAT_AFFICHAGE_ARTICLE_HEURE, this.getTimestampPublication());
    }

    /**
     * Date de la publication sous forme textuelle
     *
     * @return Date de la publication
     */
    public String getDatePublication() {
        String laDate = MyDateUtils.formatDate(Constantes.FORMAT_AFFICHAGE_SECTION_DATE, this.getTimestampPublication());
        // Première lettre en majuscule
        return String.valueOf(laDate.charAt(0)).toUpperCase(Constantes.LOCALE) + laDate.substring(1);
    }

    /**
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return titre
     */
    public String getTitre() {
        return titre;
    }

    /**
     * @param titre titre
     */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     * @return sousTitre
     */
    public String getSousTitre() {
        return sousTitre;
    }

    /**
     * @param sousTitre sousTitre
     */
    public void setSousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
    }

    /**
     * @return URL SEO
     */
    public String getURLseo() {
        return URLseo;
    }

    /**
     * @param URLseo URL SEO
     */
    public void setURLseo(String URLseo) {
        this.URLseo = URLseo;
    }

    /**
     * @return isAbonne
     */
    public boolean isAbonne() {
        return isAbonne;
    }

    /**
     * @param isAbonne isAbonne
     */
    public void setAbonne(boolean isAbonne) {
        this.isAbonne = isAbonne;
    }

    /**
     * @return nbCommentaires
     */
    public int getNbCommentaires() {
        return nbCommentaires;
    }

    /**
     * @param nbCommentaires nbCommentaires
     */
    public void setNbCommentaires(int nbCommentaires) {
        this.nbCommentaires = nbCommentaires;
    }

    /**
     * Nb de commentaires non lus
     *
     * @return int
     */
    public int getNbCommentairesNonLus() {
        int nbComms = this.getNbCommentaires() - this.getIndiceDernierCommLu();
        // Prévenir un cas négatif
        if (nbComms < 0) {
            nbComms = 0;
        }
        return nbComms;
    }

    /**
     * @return urlIllustration
     */
    public String getUrlIllustration() {
        return urlIllustration;
    }

    /**
     * @param urlIllustration urlIllustration
     */
    public void setUrlIllustration(String urlIllustration) {
        this.urlIllustration = urlIllustration;
    }

    /**
     * @return contenu
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * @param contenu contenu
     */
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    /**
     * @return timestampPublication
     */
    public long getTimestampPublication() {
        return timestampPublication;
    }

    /**
     * @param timestampPublication timestampPublication
     */
    public void setTimestampPublication(long timestampPublication) {
        this.timestampPublication = timestampPublication;
    }

    /**
     * @return isLu
     */
    public boolean isLu() {
        return isLu;
    }

    /**
     * @param isLu isLu
     */
    public void setLu(boolean isLu) {
        this.isLu = isLu;
    }

    /**
     * @return isDlContenuAbonne
     */
    public boolean isDlContenuAbonne() {
        return isDlContenuAbonne;
    }

    /**
     * @param isDlContenuAbonne isDlContenuAbonne
     */
    public void setDlContenuAbonne(boolean isDlContenuAbonne) {
        this.isDlContenuAbonne = isDlContenuAbonne;
    }

    /**
     * @return Indice du dernier commentaire lu
     */
    public int getIndiceDernierCommLu() {
        return indiceDernierCommLu;
    }

    /**
     * @param indiceDernierCommLu Indice du dernier commentaire lu
     */
    public void setIndiceDernierCommLu(int indiceDernierCommLu) {
        this.indiceDernierCommLu = indiceDernierCommLu;
    }

    /**
     * @return ID du dernier commentaire retourné par le parseur (-1 <=> pas de commentaires)
     */
    public int getParseurLastCommentId() {
        return parseurLastCommentId;
    }

    /**
     * @param parseurLastCommentId ID du dernier commentaire retourné par le parseur (-1 <=> pas de commentaires)
     */
    public void setParseurLastCommentId(int parseurLastCommentId) {
        this.parseurLastCommentId = parseurLastCommentId;
    }

    /**
     * @return Timestamp du téléchargement de l'article
     */
    public long getTimestampDl() {
        return timestampDl;
    }

    /**
     * @param timestampDl Timestamp du téléchargement de l'article
     */
    public void setTimestampDl(long timestampDl) {
        this.timestampDl = timestampDl;
    }

    /**
     * @return Timestamp de modification de l'article (non persistent)
     */
    public long getTimestampModification() {
        return timestampModification;
    }

    /**
     * @param timestampModification Timestamp de modification de l'article (non persistent)
     */
    public void setTimestampModification(long timestampModification) {
        this.timestampModification = timestampModification;
    }
}