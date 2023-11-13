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

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pcinpact.adapters.ItemsAdapter;
import com.pcinpact.datastorage.CacheManager;
import com.pcinpact.datastorage.DAO;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.Item;
import com.pcinpact.items.SectionItem;
import com.pcinpact.network.AccountCheckInterface;
import com.pcinpact.network.AsyncAccountCheck;
import com.pcinpact.network.AsyncHTMLDownloader;
import com.pcinpact.network.RefreshDisplayInterface;
import com.pcinpact.utils.Constantes;
import com.pcinpact.utils.MyDateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
     * [ 0, HTML_LISTE_ARTICLES, HTML_COMMENTAIRES ]
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
    private long timestampMinArticle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // DEBUG
        if (Constantes.DEBUG) {
            // Activation du debug Glide
            System.setProperty("log.tag.Glide", "VERBOSE");
            System.setProperty("log.tag.Engine", "VERBOSE");

            // Détection des violations des politiques Android
            StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setVmPolicy(policy);
        }

        super.onCreate(savedInstanceState);

        // Gestion du thème sombre (option utilisateur)
        Boolean isThemeSombre = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionThemeSombre, R.bool.defautOptionThemeSombre);
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
        dlInProgress = new int[3];

        // Mise en place de l'itemAdapter
        monItemsAdapter = new ItemsAdapter(getApplicationContext(), getLayoutInflater(), new ArrayList<>());
        monListView.setAdapter(monItemsAdapter);
        monListView.setOnItemClickListener(this);

        // onRefresh
        monSwipeRefreshLayout.setOnRefreshListener(this::prepareTelechargementListeArticles);

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
                    Log.d("ListeArticlesActivity", "onScroll() - SwipeRefreshLayout - topRowVerticalPosition : " + topRowVerticalPosition);
                }
                monSwipeRefreshLayout.setEnabled(topRowVerticalPosition <= 0);
            }
        });

        // J'active la BDD
        monDAO = DAO.getInstance(getApplicationContext());
        // Chargement des articles & MàJ de l'affichage
        monItemsAdapter.updateListeItems(prepareAffichage());

        // Gestion du changement d'options de l'application
        listenerOptions = (SharedPreferences sharedPreferences, String key) -> {

            // Taille des textes
            if (key.equals(getResources().getString(R.string.idOptionZoomTexte))) {
                // Rafraichissement de l'affichage
                monItemsAdapter.notifyDataSetChanged();

                // DEBUG
                if (Constantes.DEBUG) {
                    Log.w("ListeArticlesActivity", "onCreate() - changement taille des textes => " + Constantes.getOptionInt(getApplicationContext(), R.string.idOptionZoomTexte, R.string.defautOptionZoomTexte));
                }
            }
            // Menu debug
            else if (key.equals(getResources().getString(R.string.idOptionDebug))) {
                // invalidation du menu
                supportInvalidateOptionsMenu();

                // DEBUG
                if (Constantes.DEBUG) {
                    Log.w("ListeArticlesActivity", "onCreate() - changement option debug => " + Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebug, R.bool.defautOptionDebug));
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
        Boolean modeDebug = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebug, R.bool.defautOptionDebug);

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
        Boolean premiereUtilisation = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionInstallationApplication, R.bool.defautOptionInstallationApplication);
        // Si première utilisation : on affiche un disclaimer
        if (premiereUtilisation) {
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

        // Si on a jamais synchronisé, lancer un téléchargement des articles
        if (TimeUnit.SECONDS.toMillis(monDAO.chargerDateRefresh(Constantes.DB_REFRESH_ID_LISTE_ARTICLES)) == 0) {
            prepareTelechargementListeArticles();
        }

        return true;
    }


    // TODO : liste des appels à dl (voir meme des notifs qui remonteraient du downloader) avec les URL concernées

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
            prepareTelechargementListeArticles();
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
                Toast monToast = Toast.makeText(this, getString(R.string.erreurEnvoiMail), Toast.LENGTH_LONG);
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

            // Suppression des fichiers temporaires pour l'historique du ShareActionProvider
            CacheManager.effacerContenuRepertoire(getFilesDir().toString(), Constantes.PREFIXE_SHARE_HISTORY_FILE_NAME);
        } catch (Exception e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ListeArticlesActivity", "onDestroy()", e);
            }
        }

        super.onDestroy();
    }

    /**
     * Calculer la date minimum pour les articles
     */
    private void calculerTimeStampMinArticle() {
        // Nombre de jours demandés par l'utilisateur
        int nbJours = Constantes.getOptionInt(getApplicationContext(), R.string.idOptionNbJoursArticles, R.string.defautOptionNbJoursArticles);

        timestampMinArticle = MyDateUtils.timeStampDateActuelleMinus(nbJours);
    }

    /**
     * Lance le téléchargement de la liste des articles.
     */
    private void prepareTelechargementListeArticles() {
        // GUI : activité en cours...
        nouveauChargementGUI(Constantes.HTML_LISTE_ARTICLES);

        // DEBUG
        if (Constantes.DEBUG) {
            Log.i("ListeArticlesActivity", "prepareTelechargementListeArticles()");
        }

        // TimeStamp de la date depuis laquelle télécharger les articles
        calculerTimeStampMinArticle();

        // Récupération des identifiants de l'utilisateur
        String usernameOption = Constantes.getOptionString(getApplicationContext(), R.string.idOptionLogin, R.string.defautOptionLogin);
        String passwordOption = Constantes.getOptionString(getApplicationContext(), R.string.idOptionPassword, R.string.defautOptionPassword);
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
     * Télécharge la liste d'articles (y compris le brief)
     *
     * @param isDownloadBrief Boolean true => télécharger que le brief / false => télécharger que les articles / null => télécharger les deux
     */
    private void telechargeListeArticles(Boolean isDownloadBrief) {
        AsyncHTMLDownloader monAHD;
        // Les articles
        if (isDownloadBrief == null || !isDownloadBrief) {
            monAHD = new AsyncHTMLDownloader(this, Constantes.HTML_LISTE_ARTICLES, Constantes.NEXT_URL_LISTE_ARTICLE + MyDateUtils.convertToDateISO8601(timestampMinArticle), 0, token);
            // Lancement du téléchargement
            launchAHD(monAHD, Constantes.HTML_LISTE_ARTICLES);
        }

        // Le brief
        if (isDownloadBrief == null || isDownloadBrief) {
            monAHD = new AsyncHTMLDownloader(this, Constantes.HTML_LISTE_ARTICLES, Constantes.NEXT_URL_LISTE_ARTICLE_BRIEF + MyDateUtils.convertToDateISO8601(timestampMinArticle), 0, token);
            // Lancement du1 téléchargement
            launchAHD(monAHD, Constantes.HTML_LISTE_ARTICLES);
        }
    }

    /**
     * Télécharge le nombre de commentaires de chaque article.
     * Prend également les 10 premiers commentaires.
     */
    private void telechargeNbCommentaires() {
        // Récupération des ID d'articles
        for (ArticleItem unArticle : monDAO.chargerArticlesTriParDate()) {
            AsyncHTMLDownloader monAHD = new AsyncHTMLDownloader(this, Constantes.HTML_COMMENTAIRES, Constantes.NEXT_URL_COMMENTAIRES + unArticle.getIdNext(), unArticle.getPk(), token);
            // Lancement du téléchargement
            launchAHD(monAHD, Constantes.HTML_COMMENTAIRES);
        }
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
            Boolean debug = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionDebug, R.bool.defautOptionDebug);

            // Retour utilisateur ?
            if (debug) {
                Toast monToast = Toast.makeText(this, R.string.erreurAHDdl, Toast.LENGTH_SHORT);
                monToast.show();
            }

            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ListeArticlesActivity", "launchAHD() - erreur lancement AHD" + unAHD.toString());
            }
        }
    }

    @Override
    public void downloadHTMLFini(String uneURL, ArrayList<? extends Item> desItems) {
        // Téléchargement du nombre de commentaires et des 10 premiers commentaires
        if (uneURL.startsWith(Constantes.NEXT_URL_COMMENTAIRES)) {
            for (Item unItem : desItems) {
                // Nombre de commentaires
                if (unItem instanceof ArticleItem) {
                    // Récupération de l'article depuis la BDD
                    ArticleItem monArticle = monDAO.chargerArticle(((ArticleItem) unItem).getPk());
                    monArticle.setNbCommentaires(((ArticleItem) unItem).getNbCommentaires());
                    // L'enregistrer mais sans effacer les commentaires & date de refresh
                    monDAO.enregistrerArticle(monArticle, false);
                }
                // Commentaires
                else {
                    monDAO.enregistrerCommentaireSiNouveau((CommentaireItem) unItem);
                }
            }

            // gestion du téléchargement GUI
            finChargementGUI(Constantes.HTML_COMMENTAIRES);
        }
        // Téléchargement d'articles ou du brief
        else {
            // Si c'est un téléchargement de la liste d'articles
            if (desItems.size() > 0) {
                // Enregistrer en BDD les articles s'il est nouveau ou mis à jour (erreur de téléchargement, accès au contenu abonné, ...)
                for (ArticleItem unArticle : (ArrayList<ArticleItem>) desItems) {
                    monDAO.enregistrerArticleSiNouveau(unArticle);
                }
            }

            // gestion du téléchargement GUI
            finChargementGUI(Constantes.HTML_LISTE_ARTICLES);
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

        // Chargement des articles depuis la BDD (triés par date de publication)
        mesArticles = monDAO.chargerArticlesTriParDate();

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
            String monTexte = getString(R.string.lastUpdate) + new SimpleDateFormat(Constantes.FORMAT_DATE_DERNIER_REFRESH, Constantes.LOCALE).format(dernierRefresh);
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
        // TODO API 24 :: Arrays.stream(dlInProgress).sum() == 0;
        if (dlInProgress[Constantes.HTML_LISTE_ARTICLES] + dlInProgress[Constantes.HTML_COMMENTAIRES] == 0) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ListeArticlesActivity", "nouveauChargementGUI() - Lancement animation");
            }
            // Couleurs du RefreshLayout
            monSwipeRefreshLayout.setColorSchemeResources(R.color.refreshBleu, R.color.refreshOrange);
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

        // Si la liste d'articles est chargée
        if (
                typeDL == Constantes.HTML_LISTE_ARTICLES
                && dlInProgress[Constantes.HTML_LISTE_ARTICLES] == 0
        ) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.w("ListeArticlesActivity", "finChargementGUI() - Rafraichissement liste articles");
            }
            // MàJ de la date de rafraichissement de la liste des articles
            // Date du refresh
            long dateRefresh = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            monDAO.enregistrerDateRefresh(Constantes.DB_REFRESH_ID_LISTE_ARTICLES, dateRefresh);

            // Mise à jour du nombre de commentaires
            telechargeNbCommentaires();

            // Je met à jour les données
            monItemsAdapter.updateListeItems(prepareAffichage());
            // Je notifie le changement pour un rafraichissement du contenu
            monItemsAdapter.notifyDataSetChanged();
        }

        // Si toutes les données sont téléchargées...
        // TODO API 24 :: Arrays.stream(dlInProgress).sum() == 0;
        if (dlInProgress[Constantes.HTML_LISTE_ARTICLES] + dlInProgress[Constantes.HTML_COMMENTAIRES] == 0) {
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
        Toast monToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        monToast.show();

        /*
         * Téléchargement des pages de liste d'articles
         */
        telechargeListeArticles(null);

        // GUI : fin de l'activité en cours...
        finChargementGUI(Constantes.HTML_LISTE_ARTICLES);
    }
}
