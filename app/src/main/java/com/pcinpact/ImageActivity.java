/*
 * Copyright 2013 - 2019 Anael Mobilia and contributors
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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.pcinpact.datastorage.ImageProvider;
import com.pcinpact.utils.Constantes;

/**
 * Affiche ne image en grand
 *
 * @author Anael
 */
public class ImageActivity extends AppCompatActivity {

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
        ImageView monImageView = (ImageView) findViewById(R.id.zoom_image);

        // Récupération de l'URL de l'image
        String urlImage = getIntent().getExtras().getString("URL_IMAGE");

        // Chargement de l'image...
        ImageProvider monImageProviderArticle = new ImageProvider(getApplicationContext(), monImageView, 1);
        monImageView.setImageDrawable(monImageProviderArticle.getDrawable(urlImage));

        // Gestion de la fermeture de l'activité
        monImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}