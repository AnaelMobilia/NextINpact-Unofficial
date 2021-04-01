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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.pcinpact.utils.Constantes;

/**
 * Affiche une image en grand
 *
 * @author Anael
 */
public class ImageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Je lance l'activité
        super.onCreate(savedInstanceState);

        // Gestion du thème sombre (option utilisateur)
        Boolean isThemeSombre = Constantes.getOptionBoolean(getApplicationContext(), R.string.idOptionThemeSombre,
                                                            R.bool.defautOptionThemeSombre);
        if (isThemeSombre) {
            // Si actif, on applique le style
            setTheme(R.style.NextInpactThemeFonce);
        }

        setContentView(R.layout.activity_image);

        // Récupération de l'imageView
        PhotoView monImageView = findViewById(R.id.zoom_image);

        String urlImage;
        try {
            // Récupération de l'URL de l'image
            urlImage = getIntent().getExtras().getString("URL_IMAGE");

            // Chargement de l'image...
            Glide.with(getApplicationContext()).load(urlImage).error(R.drawable.logo_nextinpact_barre).into(monImageView);
        } catch (NullPointerException e) {
            // DEBUG
            if (Constantes.DEBUG) {
                Log.e("ImageActivity", "onCreate() - Récupération URL image de l'intent", e);
            }

            // Affichage d'un toast d'erreur
            Toast monToast = Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.erreurZoomImage),
                                            Toast.LENGTH_LONG);
            monToast.show();
            // fin
            finish();
        }

        // Fermeture de l'activité au clic sur l'image
        monImageView.setOnClickListener((View arg0) -> finish());
    }
}