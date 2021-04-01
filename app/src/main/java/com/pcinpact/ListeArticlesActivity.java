/*
 * Copyright 2013 - 2021 Anael Mobilia and contributors
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
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.Item;
import com.pcinpact.items.SectionItem;
import com.pcinpact.network.AccountCheckInterface;
import com.pcinpact.network.AsyncAccountCheck;
import com.pcinpact.network.AsyncHTMLDownloader;
import com.pcinpact.network.RefreshDisplayInterface;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyDateUtils;
import com.pcinpact.utils.MyURLUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Liste des articles
 *
 * @author Anael
 */
public class ListeArticlesActivity extends AppCompatActivity implements RefreshDisplayInterface, OnItemClickListener,
        AccountCheckInterface {
    /**
     * Les articles
     */
    private ArrayList<ArticleItem> mesArticles = new ArrayList<>();
    /**
     * ItemAdapter
     */
    private ItemsAdapter monItemsAdapter;
    /**
     * BDD
     */
    private DAO monDAO;
    /**
     * Nombre de DL en cours
     */
    private int[] dlInProgress;
    /**
     * Menu
     */
    private Menu monMenu;
    /**
     * ListView
     */
    private ListView monListView;
    /**
     * SwipeRefreshLayout
     */
    private SwipeRefreshLayout monSwipeRefreshLayout;
    /**
     * TextView "Dernière synchro..."
     */
    private TextView headerTextView;
    /**
     * Listener pour le changement de taille des textes
     */
    private SharedPreferences.OnSharedPreferenceChangeListener listenerOptions;
    /**
     * Une mise à jour du thème est-elle à effectuer ?
     */
    private boolean updateTheme = false;
    /**
     * Dernière position affichée
     */
    private int dernierePosition;
    /**
     * Identifiant de l'utilisateur (null si non connecté)
     */
    private String token;
    /**
     * Timestamp de la date jusqu'à laquelle télécharger les articles
     */
    private long timeStampMinArticle;
    /**
     * Numéro de la page de la liste d'articles en cours de téléchargement pour chaque site
     */
    private int[] numPageListeArticle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // DEBUG
        if (Constantes.DEBUG) {
            // Activation du debug Glide
            System.setProperty("log.tag.Glide", "VERBOSE");
            System.setProperty("log.tag.Engine", "VERBOSE");
        }

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
        monListView = findViewById(R.id.listeArticles);
        monSwipeRefreshLayout = findViewById(R.id.swipe_container);
        headerTextView = findViewById(R.id.header_text);

        // Initialisation de l'array de supervision des téléchargements
        dlInProgress = new int[5];
        dlInProgress[Constantes.HTML_LISTE_ARTICLES] = 0;
        dlInProgress[Constantes.HTML_ARTICLE] = 0;
        dlInProgress[Constantes.HTML_NOMBRE_COMMENTAIRES] = 0;
        // Initialisation des numéros de page des listes d'articles
        numPageListeArticle = new int[Constantes.NOMBRE_SITES + 1];
        numPageListeArticle[Constantes.IS_NXI] = 1;
        numPageListeArticle[Constantes.IS_IH] = 1;

        // Mise en place de l'itemAdapter
        monItemsAdapter = new ItemsAdapter(getApplicationContext(), getLayoutInflater(), new ArrayList<>());
        monListView.setAdapter(monItemsAdapter);
        monListView.setOnItemClickListener(this);

        // onRefresh
        monSwipeRefreshLayout.setOnRefreshListener(this::telechargeListeArticles);

        // On active le SwipeRefreshLayout ssi on est en haut de la listview
        monListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Enregistrement de l'ID du premier élément affiché
                dernierePosition = firstVisibleItem;

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

        // Migration de préférences
        int valeurDefaut = Integer.parseInt(getString(R.string.defautOptionTelechargerImagesv2Test));
        if (Constantes.getOptionInt(getApplicationContext(), R.string.idOptionTelechargerImagesv2,
                                    R.string.defautOptionTelechargerImagesv2Test) == valeurDefaut) {
            // Si pas de valeur cohérente pour l'option de téléchargement des images
            // Ancienne valeur
            boolean valeurOld = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionTelechargerImages,
                                                            R.bool.defautOptionTelechargerImages);
            // Nouvelle valeur (actif tout le temps par défaut)
            String valeurNew = getString(R.string.defautOptionTelechargerImagesv2);
            if (!valeurOld) {
                // Pas de téléchargement automatique des images
                valeurNew = "0";
            }

            Constantes.setOptionInt(getApplicationContext(), R.string.idOptionTelechargerImagesv2, valeurNew);
        }


        // Gestion du changement d'options de l'application
        listenerOptions = (SharedPreferences sharedPreferences, String key) -> {

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
            // Nb de jours d'articles à télécharger
            else if (key.equals(getResources().getString(R.string.idOptionNbJoursArticles))) {
                calculerTimeStampMinArticle();
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
        super.onCreateOptionsMenu(monMenu);

        // Je charge mon menu dans l'actionBar
        MenuInflater inflater = getMenuInflater();

        // Chargement du fichier XML
        inflater.inflate(R.menu.activity_liste_articles_actions, monMenu);

        // Suis-je en mode DEBUG ?
        Boolean modeDebug = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebug,
                                                        R.bool.defautOptionDebug);

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ListeArticlesActivity", "onCreateOptionsMenu() - modeDebug => " + modeDebug);
        }
        // Si mode debug
        if (modeDebug) {
            // Invalidation du menu
            invalidateOptionsMenu();
            // Affichage du bouton de debug
            MenuItem boutonDebug = menu.findItem(R.id.action_debug);
            boutonDebug.setVisible(true);
        }

        // Est-ce la premiere utilisation de l'application ? [après création du menu]
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

        // Est-ce le premier lancement en v2.4.0 (changement de gestion du cache des images)
        Boolean version240 = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionVersion240,
                                                         R.bool.defautOptionVersion240);
        if (!version240) {
            // Effacement du cache de l'application v < 2.4.0
            CacheManager.effacerCacheV240(getApplicationContext());
            // Enregistrement de l'action
            Constantes.setOptionBoolean(getApplicationContext(), R.string.idOptionVersion240, true);
        }

        return true;
    }

    /**
     * Gestion du clic sur un article => l'ouvrir
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.d("ListeArticlesActivity", "onItemClick() -" + position);
        }

        // Récupère l'article en question
        ArticleItem monArticle = (ArticleItem) monItemsAdapter.getItem(position);
        // Le marquer comme lu en BDD
        monDAO.marquerArticleLu(monArticle.getPk());

        // Lance l'ouverture de l'article
        Intent monIntent = new Intent(getApplicationContext(), ArticleActivity.class);
        monIntent.putExtra("ARTICLE_PK", monArticle.getPk());
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

            // On relance l'application :-)
            recreate();
        }

        // Recréation d'un itemAdapter - #229
        monItemsAdapter = new ItemsAdapter(getApplicationContext(), getLayoutInflater(), mesArticles);
        monListView.setAdapter(monItemsAdapter);
        // On le remet à l'endroit où on était
        monListView.setSelection(dernierePosition);
        // Je pousse la mise à jour de l'affichage
        monItemsAdapter.updateListeItems(prepareAffichage());

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
        int id = pItem.getItemId();
        if (id == R.id.action_refresh) {
            // Rafraichir la liste des articles
            telechargeListeArticles();
        } else if (id == R.id.action_settings) {
            // Menu Options
            Intent intentOptions = new Intent(getApplicationContext(), OptionsActivity.class);
            startActivity(intentOptions);
        } else if (id == R.id.action_about) {
            // A propos
            Intent intentAbout = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intentAbout);
        } else if (id == R.id.action_debug) {
            // Debug
            Intent intentDebug = new Intent(getApplicationContext(), DebugActivity.class);
            startActivity(intentDebug);
        } else if (id == R.id.action_support) {
            // Support
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            // Sujet du mail
            intent.putExtra(Intent.EXTRA_SUBJECT, Constantes.getUserAgent());
            // Corps du mail
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.supportMessage));
            // A qui...
            intent.setDataAndType(Uri.parse("mailto:" + Constantes.MAIL_DEVELOPPEUR), "text/plain");
            // Si touche retour : revient a l'application et pas aux mails
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Affichage du numéro de version
                Toast monToast = Toast.makeText(getApplicationContext(), getString(R.string.erreurEnvoiMail), Toast.LENGTH_LONG);
                monToast.show();

                // DEBUG
                if (Constantes.DEBUG) {
                    Log.e("ListeArticlesActivity", "onOptionsItemSelected() - Support -> exception", e);
                }
            }
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
     * Calculer la date minimum pour les articles
     */
    private void calculerTimeStampMinArticle() {
        // Nombre de jours demandés par l'utilisateur
        int nbJours = Constantes.getOptionInt(getApplicationContext(), R.string.idOptionNbJoursArticles,
                                              R.string.defautOptionNbJoursArticles);

        timeStampMinArticle = MyDateUtils.timeStampDateActuelleMinus(nbJours);
    }

    /**
     * Lance le téléchargement de la liste des articles.
     */
    private void telechargeListeArticles() {
        // GUI : activité en cours...
        nouveauChargementGUI(Constantes.HTML_LISTE_ARTICLES);

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ListeArticlesActivity", "telechargeListeArticles()");
        }

        // TimeStamp de la date depuis laquelle télécharger les articles
        calculerTimeStampMinArticle();

        // Récupération des identifiants de l'utilisateur
        String usernameOption = Constantes.getOptionString(getApplicationContext(), R.string.idOptionLogin,
                                                           R.string.defautOptionLogin);
        String passwordOption = Constantes.getOptionString(getApplicationContext(), R.string.idOptionPassword,
                                                           R.string.defautOptionPassword);
        // Identifiants non définis...
        if ("".equals(usernameOption) && "".equals(passwordOption)) {
            // Lancement du téléchargement des articles
            retourVerifCompte(null);
        } else {
            // Lancement de la vérif des identifiants (flux réseau donc asynchrone)
            AsyncAccountCheck maVerif = new AsyncAccountCheck(this, usernameOption, passwordOption);
            maVerif.run();
            // Le téléchargement de la liste d'articles se fera une fois l'état du compte déterminé
        }

        /*
         * Nettoyage de la BDD
         */
        CacheManager.nettoyerCache(getApplicationContext());
    }

    /**
     * Télécharge la liste d'articles d'un site
     *
     * @param idSite ID du site (Cf Constantes.IS_xxx)
     */
    private void telechargeListeArticles(int idSite) {
        AsyncHTMLDownloader monAHD = new AsyncHTMLDownloader(this, Constantes.HTML_LISTE_ARTICLES, idSite,
                                                             Constantes.X_INPACT_URL_LISTE_ARTICLE + numPageListeArticle[idSite],
                                                             0, token);
        // Lancement du téléchargement
        launchAHD(monAHD, Constantes.HTML_LISTE_ARTICLES);
    }

    /**
     * Lance le téléchargement des articles.
     *
     * @param desItems liste d'articles à télécharger
     */
    private void telechargeArticles(final ArrayList<? extends Item> desItems) {
        for (Item unItem : desItems) {
            ArticleItem unArticle = (ArticleItem) unItem;

            // Téléchargement de la ressource
            AsyncHTMLDownloader monAHD = new AsyncHTMLDownloader(this, Constantes.HTML_ARTICLE, unArticle.getSite(),
                                                                 unArticle.getPathPourDl(), unArticle.getPk(), token);
            // DEBUG
            if (Constantes.DEBUG) {
                Log.i("ListeArticlesActivity",
                      "telechargeArticles() - DL de " + MyURLUtils.getSiteURL(unArticle.getSite(), unArticle.getPathPourDl(),
                                                                              false));
            }
            launchAHD(monAHD, Constantes.HTML_ARTICLE);
        }
    }

    /**
     * Télécharge le nombre de commentaires de chaque article
     *
     * @param idSite ID du site (Cf Constantes.IS_xxx)
     */
    private void telechargeNbCommentaires(int idSite) {
        StringBuilder param = new StringBuilder();
        // Récupération des ID d'articles
        for (ArticleItem unArticle : monDAO.chargerArticlesTriParDate(false)) {
            // Si c'est le bon site, je prends l'article
            if (unArticle.getSite() == idSite) {
                param.append(Constantes.X_INPACT_URL_NB_COMMENTAIRES_PARAM_ARTICLE).append(unArticle.getIdInpact());
            }
        }

        AsyncHTMLDownloader monAHD = new AsyncHTMLDownloader(this, Constantes.HTML_NOMBRE_COMMENTAIRES, idSite,
                                                             Constantes.X_INPACT_URL_NB_COMMENTAIRES + param, 0, token);
        // Lancement du téléchargement
        launchAHD(monAHD, Constantes.HTML_NOMBRE_COMMENTAIRES);
    }

    /**
     * Lance une tâche asynchrone de téléchargement et notifie l'user en cas d'erreur
     *
     * @param unAHD  object AsyncHTMLDownloader
     * @param typeDl int Type de téléchargement
     */
    private void launchAHD(AsyncHTMLDownloader unAHD, int typeDl) {
        // Lancement du téléchargement
        if (unAHD.run()) {
            // MàJ animation
            nouveauChargementGUI(typeDl);
        } else {
            // L'utilisateur demande-t-il un debug ?
            Boolean debug = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebug,
                                                        R.bool.defautOptionDebug);

            // Retour utilisateur ?
            if (debug) {
                Toast monToast = Toast.makeText(getApplicationContext(), R.string.erreurAHDdl, Toast.LENGTH_SHORT);
                monToast.show();
            }

            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ListeArticlesActivity", "launchAHD() - erreur lancement AHD" + unAHD.toString());
            }
        }
    }

    @Override
    public void downloadHTMLFini(int site, String pathURL, ArrayList<? extends Item> desItems) {
        // Si c'est un téléchargement de la liste d'articles
        if (pathURL.startsWith(Constantes.X_INPACT_URL_LISTE_ARTICLE) && desItems.size() > 0) {
            // Télécharger la prochaine page de la liste des articles
            boolean dlNextPage = true;

            ArticleItem lastArticle = (ArticleItem) desItems.get(desItems.size() - 1);
            // Le dernier article récupéré est plus vieux que ma limite
            if (lastArticle.getTimeStampPublication() < timeStampMinArticle) {
                // On réinitialise à la première page de la liste d'article pour le prochain refresh
                numPageListeArticle[site] = 1;
                // Plus de téléchargement de la liste des articles
                dlNextPage = false;
            }
            if (dlNextPage) {
                // Le dernier article n'est pas assez vieux => télécharger la page suivante
                numPageListeArticle[site]++;
                telechargeListeArticles(site);
            } else {
                // MàJ de la date de rafraichissement de la liste des articles
                // Date du refresh
                long dateRefresh = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                monDAO.enregistrerDateRefresh(Constantes.DB_REFRESH_ID_LISTE_ARTICLES, dateRefresh);

                // Mise à jour du nombre de commentaires
                telechargeNbCommentaires(Constantes.IS_NXI);
                telechargeNbCommentaires(Constantes.IS_IH);
            }

            // Je regarde pour chaque article si je dois le récupérer
            ArrayList<ArticleItem> articleADL = new ArrayList<>();
            for (ArticleItem unArticle : (ArrayList<ArticleItem>) desItems) {
                // Vérification de la date de l'article
                if (unArticle.getTimeStampPublication() > timeStampMinArticle) {
                    // Stockage en BDD ssi nouveau
                    if (monDAO.enregistrerArticleSiNouveau(unArticle)) {
                        // Je recharge l'article depuis la BDD pour récupérer sa PK
                        ArticleItem monArticle = monDAO.chargerArticle(unArticle.getIdInpact(), unArticle.getSite());
                        // Il faut maintenant télécharger le contenu de l'article !
                        articleADL.add(monArticle);
                    }
                }
            }
            // Téléchargement des articles dont la date est OK
            telechargeArticles(articleADL);

            // gestion du téléchargement GUI
            finChargementGUI(Constantes.HTML_LISTE_ARTICLES);
        }
        // Téléchargement du contenu d'un article
        else if (pathURL.startsWith(Constantes.X_INPACT_URL_ARTICLE)) {
            for (ArticleItem unArticle : (ArrayList<ArticleItem>) desItems) {
                // Récupération de l'article depuis la BDD
                ArticleItem monArticle = monDAO.chargerArticle(unArticle.getPk());
                monArticle.setContenu(unArticle.getContenu());
                monArticle.setLu(false);
                monArticle.setURLseo(unArticle.getURLseo());
                // Ais-je téléchargé la partie abonné ?
                if (monArticle.isAbonne() && !monArticle.isDlContenuAbonne()) {
                    // Suis-je connecté ?
                    if (token != null) {
                        monArticle.setDlContenuAbonne(true);
                    }
                }
                monDAO.enregistrerArticle(monArticle);
            }

            // gestion du téléchargement GUI
            finChargementGUI(Constantes.HTML_ARTICLE);
        }
        // Téléchargement du nombre de commentaires
        else {
            for (ArticleItem unArticle : (ArrayList<ArticleItem>) desItems) {
                // Récupération de l'article depuis la BDD
                ArticleItem monArticle = monDAO.chargerArticle(unArticle.getIdInpact(), unArticle.getSite());
                monArticle.setNbCommentaires(unArticle.getNbCommentaires());
                // L'enregistrer mais sans effacer les commentaires & date de refresh
                monDAO.enregistrerArticle(monArticle, false);
            }

            // gestion du téléchargement GUI
            finChargementGUI(Constantes.HTML_NOMBRE_COMMENTAIRES);
        }
    }

    /**
     * Fournit une liste d'articles triés par date + sections.
     *
     * @return Liste d'articles
     */
    private ArrayList<Item> prepareAffichage() {
        ArrayList<Item> monRetour = new ArrayList<>();
        String jourActuel = "";

        // Gestion des publicités rédactionnelles
        Boolean afficherPublicite = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionAfficherPublicite,
                                                                R.bool.defautOptionAfficherPublicite);

        // Chargement des articles depuis la BDD (triés par date de publication)
        mesArticles = monDAO.chargerArticlesTriParDate(afficherPublicite);

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
        long dernierRefresh = TimeUnit.SECONDS.toMillis(monDAO.chargerDateRefresh(Constantes.DB_REFRESH_ID_LISTE_ARTICLES));

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
            + dlInProgress[Constantes.HTML_NOMBRE_COMMENTAIRES] == 0) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ListeArticlesActivity", "nouveauChargementGUI() - Lancement animation");
            }
            // Couleurs du RefreshLayout
            monSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getApplicationContext(), R.color.refreshBleu),
                                                       ContextCompat.getColor(getApplicationContext(), R.color.refreshOrange));
            // Animation du RefreshLayout
            monSwipeRefreshLayout.setRefreshing(true);

            // Grisage de l'icône d'action
            MenuItem monItem = monMenu.findItem(R.id.action_refresh);
            monItem.getIcon().setAlpha(130);
            monItem.setEnabled(false);
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

        // Si la liste d'articles est chargée (et qu'on ne vient pas de finir de télécharger un article...)
        if (dlInProgress[Constantes.HTML_LISTE_ARTICLES] == 0 && typeDL != Constantes.HTML_ARTICLE) {
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
        if (dlInProgress[Constantes.HTML_LISTE_ARTICLES] + dlInProgress[Constantes.HTML_ARTICLE]
            + dlInProgress[Constantes.HTML_NOMBRE_COMMENTAIRES] == 0) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ListeArticlesActivity", "finChargementGUI() - Arrêt animation");
            }

            // On stoppe l'animation du SwipeRefreshLayout
            monSwipeRefreshLayout.setRefreshing(false);

            // Dégrisage de l'icône
            MenuItem monItem = monMenu.findItem(R.id.action_refresh);
            monItem.getIcon().setAlpha(255);
            monItem.setEnabled(true);
        }

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ListeArticlesActivity", "finChargementGUI() - " + Arrays.toString(dlInProgress));
        }
    }

    @Override
    public void retourVerifCompte(String unToken) {
        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ListeArticlesActivity", "retourVerifCompte() - Token : " + unToken);
        }

        String message;
        if (unToken == null) {
            // Pas d'identifiants
            message = getString(R.string.infoOptionAbonne);
            token = null;
        } else if ("".equals(unToken)) {
            // Erreur d'auth
            message = getString(R.string.erreurAuthentification);
            token = null;
        } else {
            // Compte abonné connecté avec succès
            message = getString(R.string.compteAbonne);
            token = unToken;
        }
        // Retour utilisateur
        Toast monToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        monToast.show();


        /*
         * Téléchargement des articles dont le contenu n'avait pas été téléchargé
         */
        telechargeArticles(monDAO.chargerArticlesATelecharger(token != null));

        /*
         * Téléchargement des pages de liste d'articles
         */
        telechargeListeArticles(Constantes.IS_NXI);
        telechargeListeArticles(Constantes.IS_IH);

        // GUI : fin de l'activité en cours...
        finChargementGUI(Constantes.HTML_LISTE_ARTICLES);
    }
}
