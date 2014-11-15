/*
 * Copyright 2014 Anael Mobilia
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.pcinpact.adapters.ItemsAdapter;
import com.pcinpact.connection.HtmlConnector;
import com.pcinpact.connection.IConnectable;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.Item;
import com.pcinpact.managers.CommentManager;
import com.pcinpact.models.INPactComment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class CommentairesActivity extends ActionBarActivity implements IConnectable {
	List<INPactComment> comments;
	String articleID;
	ListView monListView;
	ItemsAdapter monItemsAdapter;
	Menu monMenu;
	Button buttonDl10Commentaires;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Partie graphique
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.commentaires);
		setSupportProgressBarIndeterminateVisibility(false);

		// Liste des commentaires
		monListView = (ListView) this.findViewById(R.id.listeCommentaires);
		// Footer : bouton "Charger plus de commentaires"
		buttonDl10Commentaires = new Button(this);
		buttonDl10Commentaires.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Téléchargement de 10 commentaires en plus
				refreshListeCommentaires();
			}
		});
		buttonDl10Commentaires.setText(getResources().getString(R.string.commentairesPlusDeCommentaires));
		monListView.addFooterView(buttonDl10Commentaires);

		// Adapter pour l'affichage des données
		monItemsAdapter = new ItemsAdapter(this, new ArrayList<Item>());
		monListView.setAdapter(monItemsAdapter);

		// Chargement des commentaires
		final String url = getIntent().getExtras().getString("URL");
		articleID = getIntent().getExtras().getString("ARTICLE_ID");
		comments = CommentManager.getCommentsFromFile(this, url);

		// Système de rafraichissement de la vue
		monListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// J'affiche le dernier des commentaires en cache ?
				if ((firstVisibleItem + visibleItemCount) == (totalItemCount - 1)) {
					// (# du 1er commentaire affiché + nb d'items affichés) == (nb total d'item dan la liste - [bouton footer])

					// Chargement des préférences de l'utilisateur
					final SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(view.getContext());
					// Téléchargement automatique en continu des commentaires ?
					Boolean telecharger = mesPrefs.getBoolean(getString(R.string.idOptionCommentairesTelechargementContinu),
							getResources().getBoolean(R.bool.defautOptionCommentairesTelechargementContinu));

					if (telecharger) {
						// Téléchargement de 10 commentaires en plus
						refreshListeCommentaires();
					}
				}

			}
		});
	}

	/**
	 * Convertit les anciens objets vers des objets actuels + gestion des doublons
	 * 
	 * @param comments
	 * @return
	 */
	public List<Item> convertOld(List<INPactComment> comments) {
		// Passage ancien système -> nouveau système
		// TreeSet : pas de doublons + tri sur l'ID
		TreeSet<Item> mesItemsSet = new TreeSet<Item>(new Comparator<Item>() {
			// Méthode de comparaison des objets pour leur ordonnancement
			public int compare(Item a, Item b) {
				CommentaireItem item1 = (CommentaireItem) a;
				CommentaireItem item2 = (CommentaireItem) b;
				return ((Integer) item1.getIDNumerique()).compareTo((Integer) item2.getIDNumerique());
			}
		});

		// Conversion
		for (INPactComment unOldItem : comments) {
			// On traite le commentaire
			CommentaireItem monCommentaire = new CommentaireItem();
			monCommentaire.convertOld(unOldItem);
			// Et on s'assure de ne pas avoir de doublon
			mesItemsSet.add(monCommentaire);
		}

		return new ArrayList<Item>(mesItemsSet);
	}

	/**
	 * Charge les commentaires suivants
	 */
	private void refreshListeCommentaires() {
		// Vérification de la connexion internet avant de lancer
		ConnectivityManager l_Connection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (l_Connection.getActiveNetworkInfo() == null || !l_Connection.getActiveNetworkInfo().isConnected()) {
			// Pas de connexion -> affichage d'un toast
			CharSequence text = getString(R.string.chargementPasInternet);
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(getApplicationContext(), text, duration);
			toast.show();
		} else {
			// On efface le bouton rafraîchir du header
			if (monMenu != null)
				monMenu.findItem(R.id.action_refresh).setVisible(false);
			// On fait tourner le bouton en cercle dans le header
			setSupportProgressBarIndeterminateVisibility(true);
			// On indique le chargement dans le bouton du footer
			buttonDl10Commentaires.setText(getString(R.string.commentairesChargement));

			// Appel à la méthode qui va faire le boulot...
			HtmlConnector connector = new HtmlConnector(this, this);

			// Le dernier commentaire enregistré TODO : reprendre ça lorsque le système fournissant les datas sera refait
			ArrayList<Item> mesItems = (ArrayList) convertOld(comments);

			int idDernierCommentaire = 0;
			// Si j'ai des commentaires, je récupère l'ID du dernier dans la liste
			if (mesItems.size() > 0) {
				CommentaireItem lastCommentaire = (CommentaireItem) mesItems.get(mesItems.size() - 1);
				idDernierCommentaire = lastCommentaire.getIDNumerique();
			}

			// Le cast en int supprime la partie après la virgule
			int maPage = (int) Math.floor((idDernierCommentaire / 10) + 1);

			String data = "page=" + maPage + "&newsId=" + articleID;
			connector.sendRequest(NextInpact.NEXT_INPACT_URL + "/comment/", "POST", data, null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Je garde le menu sous la main
		monMenu = menu;

		// Je charge mon menu dans l'actionBar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.commentaires_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem pItem) {
		switch (pItem.getItemId()) {
		// Retour
			case R.id.action_home:
				finish();
				Intent i = new Intent(this, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivity(i);
				return true;

				// Rafraichir la liste des commentaires
			case R.id.action_refresh:
				refreshListeCommentaires();
				return true;

			default:
				return super.onOptionsItemSelected(pItem);
		}
	}

	@Override
	public void didConnectionResult(final byte[] result, final int state, final String tag) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				safeDidConnectionResult(result, state, tag);
			}
		});

	}

	protected void safeDidConnectionResult(byte[] result, int state, String tag) {
		// On arrête la rotation du logo dans le header
		setSupportProgressBarIndeterminateVisibility(false);

		// Affiche à nouveau l'icône dans le header
		if (monMenu != null)
			monMenu.findItem(R.id.action_refresh).setVisible(true);

		// MàJ du bouton du footer
		buttonDl10Commentaires.setText(getString(R.string.commentairesPlusDeCommentaires));

		List<INPactComment> newComments = CommentManager.getCommentsFromBytes(this, result);

		// SSi nouveaux commentaires
		if (newComments.size() != 0) {
			// j'ajoute les commentaires juste téléchargés
			comments.addAll(newComments);

			// Passage ancien système -> nouveau système
			ArrayList<Item> mesItems = (ArrayList) convertOld(comments);

			// Je met à jour les données
			monItemsAdapter.updateListeItems(mesItems);
			// Je notifie le changement pour un rafraichissement du contenu
			monItemsAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void didFailWithError(final String error, final int state) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				safeDidFailWithError(error, state);
			}
		});
	}

	protected void safeDidFailWithError(String error, int state) {
		// On arrête la rotation du logo dans le header
		setSupportProgressBarIndeterminateVisibility(false);

		// Affiche à nouveau l'icône dans le header
		if (monMenu != null)
			monMenu.findItem(R.id.action_refresh).setVisible(true);

		// MàJ du bouton du footer
		buttonDl10Commentaires.setText(getString(R.string.commentairesPlusDeCommentaires));

		// Message d'erreur, si demandé !
		// Chargement des préférences de l'utilisateur
		final SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Est-ce la premiere utilisation de l'application ?
		Boolean debug = mesPrefs.getBoolean(getString(R.string.idOptionDebug), getResources()
				.getBoolean(R.bool.defautOptionDebug));

		if (debug) {
			// Affichage utilisateur du message d'erreur
			CharSequence text = "Message d'erreur détaillé : " + error;
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(getApplicationContext(), text, duration);
			toast.show();
		}
	}

}
