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
package com.pcinpact;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcinpact.adapters.ItemsAdapter;
import com.pcinpact.datastorage.CacheManager;
import com.pcinpact.datastorage.DAO;
import com.pcinpact.datastorage.ImageProvider;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.Item;
import com.pcinpact.items.SectionItem;
import com.pcinpact.network.AsyncHTMLDownloader;
import com.pcinpact.network.RefreshDisplayInterface;
import com.pcinpact.utils.Constantes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Liste des articles.
 *
 * @author Anael
 */
public class ListeArticlesActivity extends AppCompatActivity implements RefreshDisplayInterface, OnItemClickListener {
    /**
     * Les articles.
     */
    private ArrayList<ArticleItem> mesArticles = new ArrayList<>();
    /**
     * ItemAdapter.
     */
    private ItemsAdapter monItemsAdapter;
    /**
     * BDD.
     */
    private DAO monDAO;
    /**
     * Nombre de DL en cours.
     */
    private int[] dlInProgress;
    /**
     * Menu.
     */
    private Menu monMenu;
    /**
     * ListView.
     */
    private ListView monListView;
    /**
     * SwipeRefreshLayout.
     */
    private SwipeRefreshLayout monSwipeRefreshLayout;
    /**
     * TextView "Dernière synchro...".
     */
    private TextView headerTextView;
    /**
     * Listener pour le changement de taille des textes.
     */
    private SharedPreferences.OnSharedPreferenceChangeListener listenerOptions;
    /**
     * Une mise à jour du thème est-elle à effectuer ?
     */
    private boolean updateTheme = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gestion du thème sombre (option utilisateur)
        Boolean isThemeSombre = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionThemeSombre,
                                                            R.bool.defautOptionThemeSombre);
        if (isThemeSombre) {
            // Si actif, on applique le style
            setTheme(R.style.NextInpactThemeFonce);
        }

        // On définit la vue
        setContentView(R.layout.activity_liste_articles);
        // On récupère les éléments GUI
        monListView = (ListView) findViewById(R.id.listeArticles);
        monSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        headerTextView = (TextView) findViewById(R.id.header_text);

        // Initialisation de l'array de supervision des téléchargements
        dlInProgress = new int[5];
        dlInProgress[Constantes.IMAGE_MINIATURE_ARTICLE] = 0;
        dlInProgress[Constantes.HTML_LISTE_ARTICLES] = 0;
        dlInProgress[Constantes.HTML_ARTICLE] = 0;

        // Mise en place de l'itemAdapter
        monItemsAdapter = new ItemsAdapter(getApplicationContext(), getLayoutInflater(), mesArticles);
        monListView.setAdapter(monItemsAdapter);
        monListView.setOnItemClickListener(this);

        // onRefresh
        monSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                telechargeListeArticles();
            }
        });

        // On active le SwipeRefreshLayout ssi on est en haut de la listview
        monListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition;

                if (monListView == null || monListView.getChildCount() == 0) {
                    topRowVerticalPosition = 0;
                } else {
                    topRowVerticalPosition = monListView.getFirstVisiblePosition();
                }
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.d("ListeArticlesActivity",
                          "onScroll() - SwipeRefreshLayout - topRowVerticalPosition : " + topRowVerticalPosition);
                }
                monSwipeRefreshLayout.setEnabled(topRowVerticalPosition <= 0);
            }
        });

        // J'active la BDD
        monDAO = DAO.getInstance(getApplicationContext());
        // Chargement des articles & MàJ de l'affichage
        monItemsAdapter.updateListeItems(prepareAffichage());

        // Est-ce la premiere utilisation de l'application ?
        Boolean premiereUtilisation = Constantes.getOptionBoolean(getApplicationContext(),
                                                                  R.string.idOptionInstallationApplication,
                                                                  R.bool.defautOptionInstallationApplication);
        // Si première utilisation : on affiche un disclaimer
        if (premiereUtilisation) {
            // Effacement du cache de l'application v < 1.8.0
            CacheManager.effacerCacheV180(getApplicationContext());

            // Lancement d'un téléchargement des articles
            telechargeListeArticles();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Titre
            builder.setTitle(getResources().getString(R.string.app_name));
            // Contenu
            builder.setMessage(getResources().getString(R.string.disclaimerContent));
            // Bouton d'action
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", null);
            // On crée & affiche
            builder.create().show();

            // Enregistrement de l'affichage
            Constantes.setOptionBoolean(getApplicationContext(), R.string.idOptionInstallationApplication, false);
        }

        // Gestion du changement d'options de l'application
        listenerOptions = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                // Taille des textes
                if (key.equals(getResources().getString(R.string.idOptionZoomTexte))) {
                    // Rafraichissement de l'affichage
                    monItemsAdapter.notifyDataSetChanged();

                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.w("ListeArticlesActivity",
                              "onCreate() - changement taille des textes => " + Constantes.getOptionInt(getApplicationContext(),
                                                                                                        R.string.idOptionZoomTexte,
                                                                                                        R.string.defautOptionZoomTexte));
                    }
                }
                // Menu debug
                else if (key.equals(getResources().getString(R.string.idOptionDebug))) {
                    // invalidation du menu
                    supportInvalidateOptionsMenu();

                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.w("ListeArticlesActivity",
                              "onCreate() - changement option debug => " + Constantes.getOptionBoolean(getApplicationContext(),
                                                                                                       R.string.idOptionDebug,
                                                                                                       R.bool.defautOptionDebug));
                    }
                }
                // Debug - Effacement du cache
                else if (key.equals(getResources().getString(R.string.idOptionDebugEffacerCache))) {
                    // Je vide ma liste d'articles...
                    nouveauChargementGUI(Constantes.HTML_LISTE_ARTICLES);
                    mesArticles.clear();
                    // Lancement du refresh de l'affichage
                    finChargementGUI(Constantes.HTML_LISTE_ARTICLES);
                }
                // Changement de thème
                else if (key.equals(getResources().getString(R.string.idOptionThemeSombre))) {
                    // Note du changement de thème
                    updateTheme = true;
                }
            }
        };
        // Attachement du superviseur aux préférences
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(
                listenerOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Je garde le menu pour pouvoir l'animer après
        monMenu = menu;

        // Je charge mon menu dans l'actionBar
        MenuInflater inflater = getMenuInflater();

        // Suis-je en mode DEBUG ?
        Boolean modeDebug = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebug,
                                                        R.bool.defautOptionDebug);

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ListeArticlesActivity", "onCreateOptionsMenu() - modeDebug => " + modeDebug);
        }

        // Chargement du fichier XML
        if (modeDebug) {
            // Mode DEBUG
            inflater.inflate(R.menu.activity_liste_articles_debug_actions, monMenu);
        } else {
            // Mode standard
            inflater.inflate(R.menu.activity_liste_articles_actions, monMenu);
        }

        // Je lance l'animation si un DL est déjà en cours
        if (dlInProgress[Constantes.HTML_LISTE_ARTICLES] == 0) {
            // Hack : il n'y avait pas d'accès à la GUI sur onCreate
            dlInProgress[Constantes.HTML_LISTE_ARTICLES]--;
            nouveauChargementGUI(Constantes.HTML_LISTE_ARTICLES);
        }

        return super.onCreateOptionsMenu(monMenu);
    }

    /**
     * Gestion du clic sur un article => l'ouvrir + marquer comme lu.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // récupère l'article en question
        ArticleItem monArticle = (ArticleItem) monItemsAdapter.getItem(position);

        // Marquer l'article comme lu
        monArticle.setLu(true);
        monDAO.marquerArticleLu(monArticle);

        // Lance l'ouverture de l'article
        Intent monIntent = new Intent(getApplicationContext(), ArticleActivity.class);
        monIntent.putExtra("ARTICLE_ID", monArticle.getId());
        startActivity(monIntent);
    }

    @Override
    protected void onRestart() {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("ListeArticlesActivity", "onRestart()");
        }

        // Changement du thème
        if (updateTheme) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ListeArticlesActivity", "onRestart() - changement du thème");
            }

            // Mise à jour du thème utilisé
            Boolean isThemeSombre = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionThemeSombre,
                                                                R.bool.defautOptionThemeSombre);
            if (isThemeSombre) {
                setTheme(R.style.NextInpactThemeFonce);
            } else {
                setTheme(R.style.NextInpactTheme);
            }

            // invalidation du cache des view
            monItemsAdapter.setResetView();

            // C'est fini
            updateTheme = false;
        }

        // Je met à jour les données qui sont potentiellement fausses suite à slide
        monItemsAdapter.updateListeItems(prepareAffichage());
        // Je notifie le changement pour un rafraichissement du contenu
        monItemsAdapter.notifyDataSetChanged();
        super.onRestart();
    }

    /**
     * Ouverture du menu de l'actionbar à l'utilisation du bouton menu.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // Bouton menu
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (monMenu != null) {
                monMenu.performIdentifierAction(R.id.action_overflow, 0);
            } else {
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("ListeArticlesActivity", "onKeyUp() - monMenu est null !");
                }
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * Gestion des clic dans le menu d'options de l'activité.
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem pItem) {
        switch (pItem.getItemId()) {
            // Rafraichir la liste des articles
            case R.id.action_refresh:
                telechargeListeArticles();
                break;

            // Menu Options
            case R.id.action_settings:
                // Je lance l'activité options
                Intent intentOptions = new Intent(getApplicationContext(), OptionsActivity.class);
                startActivity(intentOptions);
                break;

            // A propos
            case R.id.action_about:
                Intent intentAbout = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intentAbout);
                break;

            // Debug
            case R.id.action_debug:
                Intent intentDebug = new Intent(getApplicationContext(), DebugActivity.class);
                startActivity(intentDebug);
                break;

            // Support
            case R.id.action_support:
                // Envoi...
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                // Mode texte
                intent.setType("text/plain");
                // Sujet du mail
                intent.putExtra(Intent.EXTRA_SUBJECT, Constantes.getUserAgent(getApplicationContext()));
                // Corps du mail
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.supportMessage));
                // A qui...
                intent.setData(Uri.parse("mailto:" + Constantes.MAIL_DEVELOPPEUR));
                // Si touche retour : revient a l'application et pas aux mails
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Affichage du numéro de version
                    Toast monToast = Toast.makeText(getApplicationContext(), getString(R.string.erreurEnvoiMail),
                                                    Toast.LENGTH_LONG);
                    monToast.show();

                    // DEBUG
                    if (Constantes.DEBUG) {
                        Log.e("ListeArticlesActivity", "onOptionsItemSelected() - Support -> exception", e);
                    }
                }
                break;

            default:
                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("ListeArticlesActivity", "onOptionsItemSelected() - cas default ! : " + pItem.getItemId());
                    // Peut-être clic sur menu hamburger
                }
                break;
        }
        return true;
    }

    /**
     * Arrêt de l'activité.
     */
    @Override
    protected void onDestroy() {
        try {
            // Détachement du listener pour la taille des textes
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(
                    listenerOptions);
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ListeArticlesActivity", "onDestroy()", e);
            }
        }

        // Nettoyage du cache de l'application
        CacheManager.nettoyerCache(getApplicationContext());

        super.onDestroy();
    }

    /**
     * Lance le téléchargement de la liste des articles.
     */
    private void telechargeListeArticles() {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ListeArticlesActivity", "telechargeListeArticles()");
        }

        // Uniquement si on est pas déjà en train de faire un refresh...
        if (dlInProgress[Constantes.HTML_LISTE_ARTICLES] == 0) {
            // GUI : activité en cours...
            nouveauChargementGUI(Constantes.HTML_LISTE_ARTICLES);

            /**
             * Nettoyage du cache
             */
            CacheManager.nettoyerCache(getApplicationContext());

            /**
             * Téléchargement des articles dont le contenu n'avait pas été téléchargé
             */
            telechargeArticles(monDAO.chargerArticlesATelecharger());

            /**
             * Téléchargement des pages de liste d'articles
             */
            int nbArticles = Constantes.getOptionInt(getApplicationContext(), R.string.idOptionNbArticles,
                                                     R.string.defautOptionNbArticles);
            int nbPages = nbArticles / Constantes.NB_ARTICLES_PAR_PAGE;
            // téléchargement de chaque page...
            for (int numPage = 1; numPage <= nbPages; numPage++) {
                // Ma tâche de DL
                AsyncHTMLDownloader monAHD = new AsyncHTMLDownloader(this, Constantes.HTML_LISTE_ARTICLES,
                                                                     Constantes.NEXT_INPACT_URL_NUM_PAGE + numPage, monDAO,
                                                                     getApplicationContext());
                // Lancement du téléchargement
                if (monAHD.run()) {
                    // MàJ animation
                    nouveauChargementGUI(Constantes.HTML_LISTE_ARTICLES);
                }
            }

            /**
             * Téléchargement des miniatures manquantes
             */
            // Miniatures manquantes
            ArrayList<String> miniaturesManquantes = CacheManager.getMiniaturesATelecharger(getApplicationContext());
            // Pour chacune...
            for (String imageURL : miniaturesManquantes) {
                // Je lance son DL (sans log en BDD imageCache)
                ImageProvider.telechargerImage(imageURL, Constantes.IMAGE_MINIATURE_ARTICLE, 0, getApplicationContext(), this);
                // Je note la dde de DL
                nouveauChargementGUI(Constantes.IMAGE_MINIATURE_ARTICLE);
            }
        }

        // GUI : fin de l'activité en cours...
        finChargementGUI(Constantes.HTML_LISTE_ARTICLES);
    }


    /**
     * Lance le téléchargement des articles.
     *
     * @param desItems liste d'articles à télécharger
     */
    private void telechargeArticles(final ArrayList<? extends Item> desItems) {
        for (Item unItem : desItems) {
            ArticleItem monItem = (ArticleItem) unItem;

            // Tâche de DL HTML
            AsyncHTMLDownloader monAHD;
            // DL de l'image d'illustration ?
            boolean dlIllustration = true;

            // Est-ce un article abonné ?
            if (((ArticleItem) unItem).isAbonne()) {
                boolean isConnecteRequis = false;

                // Ai-je déjà la version publique de l'article ?
                if (!((ArticleItem) unItem).getContenu().equals("")) {
                    // Je requiert d'être connecté (sinon le DL ne sert à rien)
                    isConnecteRequis = true;
                    // Je ne veux pas DL l'image de l'article
                    dlIllustration = false;
                }
                // téléchargement de la ressource
                monAHD = new AsyncHTMLDownloader(this, Constantes.HTML_ARTICLE, monItem.getUrl(), monDAO, getApplicationContext(),
                                                 isConnecteRequis);
            } else {
                // téléchargement de la ressource
                monAHD = new AsyncHTMLDownloader(this, Constantes.HTML_ARTICLE, monItem.getUrl(), monDAO,
                                                 getApplicationContext());
            }

            // Lancement du téléchargement
            if (monAHD.run()) {
                // MàJ animation
                nouveauChargementGUI(Constantes.HTML_ARTICLE);
            }

            // DL des miniatures des articles dont je récupère le contenu (sauf articles abonnés / contenu)
            if (dlIllustration) {
                // Je lance le téléchargement de sa miniature
                ImageProvider.telechargerImage(monItem.getUrlIllustration(), Constantes.IMAGE_MINIATURE_ARTICLE, monItem.getId(),
                                               getApplicationContext(), this);
                nouveauChargementGUI(Constantes.IMAGE_MINIATURE_ARTICLE);
            }
        }
    }

    @Override
    public void downloadHTMLFini(final String uneURL, final ArrayList<? extends Item> desItems) {
        // Si c'est un refresh général
        if (uneURL.startsWith(Constantes.NEXT_INPACT_URL_NUM_PAGE)) {
            // Le asyncDL ne me retourne que des articles non présents en BDD => à DL
            telechargeArticles(desItems);
            // gestion du téléchargement GUI
            finChargementGUI(Constantes.HTML_LISTE_ARTICLES);
        } else {
            // gestion du téléchargement GUI
            finChargementGUI(Constantes.HTML_ARTICLE);
        }
    }

    @Override
    public void downloadImageFini(final String uneURL) {
        // gestion du téléchargement GUI
        finChargementGUI(Constantes.IMAGE_MINIATURE_ARTICLE);
    }

    /**
     * Fournit une liste d'articles triés par date + sections.
     *
     * @return Liste d'articles
     */
    private ArrayList<Item> prepareAffichage() {
        ArrayList<Item> monRetour = new ArrayList<>();
        String jourActuel = "";

        // Nombre d'articles à afficher
        int maLimite = Constantes.getOptionInt(getApplicationContext(), R.string.idOptionNbArticles,
                                               R.string.defautOptionNbArticles);
        // Chargement des articles depuis la BDD (trié, limité)
        mesArticles = monDAO.chargerArticlesTriParDate(maLimite);

        for (ArticleItem article : mesArticles) {
            // Si ce n'est pas la même journée que l'article précédent
            if (!article.getDatePublication().equals(jourActuel)) {
                // Je met à jour ma date
                jourActuel = article.getDatePublication();
                // J'ajoute un sectionItem
                monRetour.add(new SectionItem(jourActuel));
            }

            // J'ajoute mon article
            monRetour.add(article);
        }

        // MàJ de la date de dernier refresh
        long dernierRefresh = monDAO.chargerDateRefresh(Constantes.DB_REFRESH_ID_LISTE_ARTICLES);

        if (dernierRefresh == 0) {
            // Jamais synchro...
            headerTextView.setText(getString(R.string.lastUpdateNever));
        } else {
            String monTexte = getString(R.string.lastUpdate) + new SimpleDateFormat(Constantes.FORMAT_DATE_DERNIER_REFRESH,
                                                                                    Constantes.LOCALE).format(dernierRefresh);
            // Une MàJ à déjà été faite
            headerTextView.setText(monTexte);
        }

        return monRetour;
    }

    /**
     * Gère les animations de téléchargement.
     */
    private void nouveauChargementGUI(int typeDL) {
        // Si c'est le premier => activation des gri-gri GUI
        if (dlInProgress[Constantes.HTML_LISTE_ARTICLES] + dlInProgress[Constantes.HTML_ARTICLE]
            + dlInProgress[Constantes.IMAGE_MINIATURE_ARTICLE] == 0) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ListeArticlesActivity", "nouveauChargementGUI() - Lancement animation");
            }
            // Couleurs du RefreshLayout
            monSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getApplicationContext(), R.color.refreshBleu),
                                                       ContextCompat.getColor(getApplicationContext(), R.color.refreshOrange));
            // Animation du RefreshLayout
            monSwipeRefreshLayout.setRefreshing(true);
        }

        // Je note le téléchargement en cours
        dlInProgress[typeDL]++;
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ListeArticlesActivity", "nouveauChargementGUI() - " + Arrays.toString(dlInProgress));
        }
    }

    /**
     * Gère les animations de téléchargement.
     */
    private void finChargementGUI(int typeDL) {
        // Je note la fin du téléchargement
        dlInProgress[typeDL]--;

        // Si la liste d'articles et de miniatures est chargée (et qu'on ne vient pas de finir de télécharger un article...)
        if (dlInProgress[Constantes.HTML_LISTE_ARTICLES] + dlInProgress[Constantes.IMAGE_MINIATURE_ARTICLE] == 0
            && typeDL != Constantes.HTML_ARTICLE) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ListeArticlesActivity", "finChargementGUI() - Rafraichissement liste articles");
            }
            // Je met à jour les données
            monItemsAdapter.updateListeItems(prepareAffichage());
            // Je notifie le changement pour un rafraichissement du contenu
            monItemsAdapter.notifyDataSetChanged();
        }

        // Si toutes les données sont téléchargées...
        if (dlInProgress[Constantes.HTML_LISTE_ARTICLES] + dlInProgress[Constantes.IMAGE_MINIATURE_ARTICLE] +
            dlInProgress[Constantes.HTML_ARTICLE] == 0) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ListeArticlesActivity", "finChargementGUI() - Arrêt animation");
            }

            // On stoppe l'animation du SwipeRefreshLayout
            monSwipeRefreshLayout.setRefreshing(false);
        }

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ListeArticlesActivity", "finChargementGUI() - " + Arrays.toString(dlInProgress));
        }
    }
}
