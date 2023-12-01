/*
 * Copyright 2013 - 2023 Anael Mobilia and contributors
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
package com.pcinpact;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pcinpact.adapters.ItemsAdapter;
import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.Item;
import com.pcinpact.network.AsyncHTMLDownloader;
import com.pcinpact.network.RefreshDisplayInterface;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyDateUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Affichage des commentaires
 *
 * @author Anael
 */
public class CommentairesActivity extends AppCompatActivity implements RefreshDisplayInterface {
    /**
     * Les commentaires
     */
    private ArrayList<CommentaireItem> mesCommentaires = new ArrayList<>();
    /**
     * ID de l'article
     */
    private int idArticle;
    /**
     * Indice du dernier commentaire lu
     */
    private int indiceDernierCommentaireLu;
    /**
     * ID du dernier commentaire connu pour l'article
     */
    private int idDernierCommentaireArticle;
    /**
     * ItemAdapter
     */
    private ItemsAdapter monItemsAdapter;
    /**
     * accès à la BDD
     */
    private DAO monDAO;
    /**
     * téléchargement en cours ?
     */
    private int dlInProgress = 0;
    /**
     * Fin des commentaires ?
     */
    private Boolean isFinCommentaires = false;
    /**
     * téléchargement de TOUS les commentaires ?
     */
    private Boolean isChargementTotal = false;
    /**
     * Réouverture au dernier commentaire lu ?
     */
    private Boolean reouverture;
    /**
     * Bouton pour télécharger 10 commentaires en plus
     */
    private Button buttonDl10Commentaires;
    /**
     * TextView "Dernière synchro..."
     */
    private TextView headerTextView;
    /**
     * ListView
     */
    private ListView monListView;
    /**
     * SwipeRefreshLayout
     */
    private SwipeRefreshLayout monSwipeRefreshLayout;
    /**
     * Menu
     */
    private Menu monMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gestion du thème sombre (option utilisateur)
        Boolean isThemeSombre = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionThemeSombre, R.bool.defautOptionThemeSombre);
        if (isThemeSombre) {
            // Si actif, on applique le style
            setTheme(R.style.NextInpactThemeFonce);
        }

        // Partie graphique
        setContentView(R.layout.activity_liste_commentaires);

        // Gestion du swipe refresh
        monSwipeRefreshLayout = findViewById(R.id.swipe_container);
        // onRefresh
        monSwipeRefreshLayout.setOnRefreshListener(() -> {
            // Chargement de tous les commentaires
            isChargementTotal = true;
            refreshListeCommentaires();
        });

        headerTextView = findViewById(R.id.header_text);
        // Liste des commentaires
        monListView = this.findViewById(R.id.listeCommentaires);
        // Footer : bouton "Charger plus de commentaires"
        buttonDl10Commentaires = new Button(this);
        buttonDl10Commentaires.setOnClickListener((View arg0) -> {
            // Téléchargement de 10 commentaires en plus
            refreshListeCommentaires();
        });
        buttonDl10Commentaires.setText(getResources().getString(R.string.commentairesPlusDeCommentaires));
        // Forcer la couleur du texte (par défaut gris sur gris)
        buttonDl10Commentaires.setTextColor(Color.BLACK);
        monListView.addFooterView(buttonDl10Commentaires);

        // Adapter pour l'affichage des données
        monItemsAdapter = new ItemsAdapter(getApplicationContext(), getLayoutInflater(), new ArrayList<>());
        monListView.setAdapter(monItemsAdapter);

        // ID de l'article concerné
        try {
            idArticle = getIntent().getExtras().getInt("ARTICLE_ID");
        } catch (NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("CommentairesActivity", "onCreate() - Récupération ID article de l'intent", e);
            }
            // Arrêt de l'activité
            this.finish();
        }

        // J'active la BDD
        monDAO = DAO.getInstance(getApplicationContext());
        // Je charge mes commentaires
        mesCommentaires.addAll(monDAO.chargerCommentairesTriParID(idArticle));

        // Je récupère l'ID du dernier commentaire connu pour cet article
        ArticleItem monArticle = monDAO.chargerArticle(idArticle);
        idDernierCommentaireArticle = monArticle.getParseurLastCommentId();

        // MàJ de l'affichage
        monItemsAdapter.updateListeItems(mesCommentaires);
        // Je fait remarquer que le contenu à changé
        monItemsAdapter.notifyDataSetChanged();

        /*
         * Réouverture au dernier commentaire lu
         */
        reouverture = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionPositionCommentaire, R.bool.defautOptionPositionCommentaire);
        if (reouverture) {
            // Réaffichage du dernier commentaire (a-t-il été lu ?)
            indiceDernierCommentaireLu = monArticle.getIndiceDernierCommLu() - 1;
            monListView.setSelection(indiceDernierCommentaireLu);
        }

        // MàJ de la date de dernier refresh
        majDateRefresh();
    }

    /**
     * Charge les commentaires suivants
     */
    private void refreshListeCommentaires() {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("CommentairesActivity", "refreshListeCommentaires()");
        }

        int indiceDernierCommentaire = 0;
        isFinCommentaires = false;
        // Si j'ai des commentaires, je récupère le nombre de commentaires
        if (!mesCommentaires.isEmpty()) {
            indiceDernierCommentaire = mesCommentaires.size();
        }

        // Quelle est la page à charger (actuelle si pas 10 commentaires, sinon la prochaine)
        int maPage = (indiceDernierCommentaire / Constantes.NB_COMMENTAIRES_PAR_PAGE) + 1;

        // Création de l'URL
        String monPath = Constantes.NEXT_URL_COMMENTAIRES + idArticle + Constantes.NEXT_URL_COMMENTAIRES_PARAM_PAGE + maPage;

        // Ma tâche de DL
        AsyncHTMLDownloader monAHD = new AsyncHTMLDownloader(this, Constantes.HTML_COMMENTAIRES, monPath, idArticle, null);

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("CommentairesActivity", "refreshListeCommentaires() : lancement téléchargement");
        }

        // Lancement du téléchargement
        if (monAHD.run()) {
            // Lancement de l'animation de téléchargement
            debutTelechargement();
        } else {
            // L'utilisateur demande-t-il un debug ?
            Boolean debug = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebug, R.bool.defautOptionDebug);

            // Retour utilisateur ?
            if (debug) {
                Toast monToast = Toast.makeText(this, R.string.erreurAHDdl, Toast.LENGTH_SHORT);
                monToast.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Conservation du menu
        monMenu = menu;

        // Je charge mon menu dans l'actionBar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_commentaires_actions, menu);

        // Suis-je en mode DEBUG ?
        Boolean modeDebug = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebug, R.bool.defautOptionDebug);

        // Si mode debug
        if (modeDebug) {
            // Invalidation du menu
            invalidateOptionsMenu();
            // Affichage du bouton de debug
            MenuItem boutonDebug = menu.findItem(R.id.action_debug);
            boutonDebug.setVisible(true);
        }

        // Configuration du onScroll de la listview
        configOnScroll();

        return true;
    }

    /**
     * Configuration du scroll (listview) : Méthode séparée pour gestion propre des boutons du menu
     */
    private void configOnScroll() {
        // Système de rafraichissement de la vue
        monListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Dernier item affiché
                int lastVisibleItem = firstVisibleItem + visibleItemCount;

                /*
                 * Gestion du téléchargement
                 */
                if (lastVisibleItem >= (totalItemCount - 1)) {
                    // (# du 1er commentaire affiché + nb d'items affichés) == (nb total d'item dans la liste - [bouton footer])

                    // téléchargement automatique en continu des commentaires ?
                    Boolean telecharger = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionCommentairesTelechargementContinu, R.bool.defautOptionCommentairesTelechargementContinu);
                    // Si l'utilisateur le veut && je ne télécharge pas déjà && la fin des commentaires n'est pas atteinte
                    if (telecharger && dlInProgress == 0 && !isFinCommentaires) {
                        // téléchargement de 10 commentaires en plus
                        refreshListeCommentaires();

                        // DEBUG
                        if (Constantes.DEBUG) {
                            Log.i("CommentairesActivity", "onScroll() - Chargement continu -> lancement chargement commentaires - " + visibleItemCount);
                        }
                    }
                }

                /*
                 * Gestion de la réouverture au dernier commentaire lu
                 */
                // Et qu'on a lu plus de commentaires
                if (reouverture && lastVisibleItem > indiceDernierCommentaireLu) {
                    /*
                     * Enregistrement de l'id du dernier commentaire affiché
                     */
                    monDAO.setIndiceDernierCommentaireLu(idArticle, lastVisibleItem);
                    // Mise à jour de la copie locale
                    indiceDernierCommentaireLu = lastVisibleItem;
                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.d("CommentairesActivity", "onScroll() - setDernierCommentaireLu(" + idArticle + ", " + lastVisibleItem + ")");
                    }
                }

                /*
                 * Gestion du SwipeRefreshLayout
                 */
                int topRowVerticalPosition;

                if (monListView == null || monListView.getChildCount() == 0) {
                    topRowVerticalPosition = 0;
                } else {
                    topRowVerticalPosition = monListView.getFirstVisiblePosition();
                }
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("CommentairesActivity", "onScroll() - SwipeRefreshLayout - topRowVerticalPosition : " + topRowVerticalPosition);
                }
                monSwipeRefreshLayout.setEnabled(topRowVerticalPosition <= 0);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem pItem) {
        int id = pItem.getItemId();
        if (id == R.id.action_refresh) {
            // Rafraichir la liste des commentaires
            // téléchargement de TOUS les commentaires
            isChargementTotal = true;

            // Lancement du premier chargement
            refreshListeCommentaires();
        } else if (id == R.id.action_debug) {
            // Débug - Affichage du code source HTML
            Intent intentDebug = new Intent(getApplicationContext(), DebugActivity.class);
            intentDebug.putExtra("ARTICLE_ID_COMMENTAIRE", idArticle);
            startActivity(intentDebug);
        }
        return super.onOptionsItemSelected(pItem);
    }

    /**
     * Lance les animations indiquant un téléchargement
     */
    private void debutTelechargement() {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("CommentairesActivity", "debutTelechargement() : " + dlInProgress);
        }

        // Lancement de l'animation le cas échéant
        if (dlInProgress == 0) {
            // Couleurs du RefreshLayout
            monSwipeRefreshLayout.setColorSchemeResources(R.color.refreshBleu, R.color.refreshOrange);
            // Animation du RefreshLayout
            monSwipeRefreshLayout.setRefreshing(true);

            // MàJ du bouton du footer
            buttonDl10Commentaires.setText(getString(R.string.commentairesChargement));

            // Grisage de l'icône d'action
            MenuItem monItem = monMenu.findItem(R.id.action_refresh);
            monItem.getIcon().setAlpha(130);
            monItem.setEnabled(false);
        }

        // J'enregistre l'état
        dlInProgress++;
    }

    /**
     * Arrête les animations indiquant un téléchargement
     */
    private void finTelechargement() {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("CommentairesActivity", "finTelechargement() " + dlInProgress);
        }
        // J'enregistre l'état
        dlInProgress--;

        // Si téléchargement fini ou téléchargement de tous les commentaires
        if (dlInProgress == 0 || isChargementTotal) {
            // Chargement des commentaires triés
            mesCommentaires = monDAO.chargerCommentairesTriParID(idArticle);
        }

        // Mise à jour de l'ID du dernier commentaire connu de l'article si nécessaire
        int idDernierCommentaire = mesCommentaires.get(mesCommentaires.size() - 1).getId();
        if (idDernierCommentaire > idDernierCommentaireArticle) {
            monDAO.setIdDernierCommentaireParseur(idArticle, idDernierCommentaire);
            idDernierCommentaireArticle = idDernierCommentaire;
        }

        // Si plus de téléchargement en cours
        if (dlInProgress == 0) {
            // MàJ de la date de rafraichissement des commentaires de l'article
            // Date du refresh
            long dateRefresh = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            monDAO.enregistrerDateRefresh(idArticle, dateRefresh);

            // Mise à jour des données
            monItemsAdapter.updateListeItems(mesCommentaires);
            // Notification du changement pour un rafraichissement du contenu
            monItemsAdapter.notifyDataSetChanged();

            // Arrêt du RefreshLayout
            monSwipeRefreshLayout.setRefreshing(false);

            // MàJ du bouton du footer
            buttonDl10Commentaires.setText(getString(R.string.commentairesPlusDeCommentaires));

            // Dégrisage de l'icône
            MenuItem monItem = monMenu.findItem(R.id.action_refresh);
            monItem.getIcon().setAlpha(255);
            monItem.setEnabled(true);

            // Je MàJ la date du dernier refresh
            majDateRefresh();
        }
    }

    @Override
    public void downloadHTMLFini(String uneURL, ArrayList<? extends Item> desItems) {
        // Nombre de commentaires récupérés inférieur à ce qui était demandé => fin du fil de commentaires
        if (desItems.size() < Constantes.NB_COMMENTAIRES_PAR_PAGE) {
            // Je note qu'il n'y a plus de commentaires
            isFinCommentaires = true;

            // Chargement de TOUS les commentaires ?
            if (isChargementTotal) {
                // On enlève le marqueur
                isChargementTotal = false;
            }

            if (Constantes.DEBUG) {
                Log.i("CommentairesActivity", "downloadHTMLFini() - fin des commentaires");
            }
        }
        // Stockage en BDD des nouveaux commentaires
        for (Item unItem : desItems) {
            if (unItem instanceof CommentaireItem) {
                monDAO.enregistrerCommentaireSiNouveau((CommentaireItem) unItem);
            }
        }

        // Chargement de TOUS les commentaires ?
        if (isChargementTotal) {
            // Lancement du prochain téléchargement...
            refreshListeCommentaires();
        }
        // Arrêt des gris-gris en GUI
        finTelechargement();
    }

    /**
     * MàJ de la date de dernière MàJ
     */
    private void majDateRefresh() {
        // Date de dernier refresh
        long dernierRefresh = monDAO.chargerDateRefresh(idArticle);

        if (dernierRefresh == 0) {
            // Jamais synchro...
            headerTextView.setText(getString(R.string.lastUpdateNever));
        } else {
            String monTexte = getString(R.string.lastUpdate) + MyDateUtils.formatDate(Constantes.FORMAT_DATE_DERNIER_REFRESH, dernierRefresh);
            // Une MàJ à déjà été faite
            headerTextView.setText(monTexte);
        }
    }
}