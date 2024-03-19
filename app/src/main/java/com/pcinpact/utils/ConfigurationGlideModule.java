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
package com.pcinpact.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;


/**
 * Configuration de Glide
 * @see <a href="https://bumptech.github.io/glide/doc/configuration.html">Doc</a>
 */
@GlideModule
public class ConfigurationGlideModule extends AppGlideModule {
    /**
     * 50 Mo de cache
     *
     * @param context contexte
     * @param builder GlideBuilder
     */
    @Override
    public void applyOptions(@NonNull Context context, GlideBuilder builder) {
        int diskCacheSizeBytes = 1024 * 1024 * Constantes.TAILLE_CACHE; // 50 Mo de cache
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));
    }

    /**
     * Glide v4
     * @see <a href="https://bumptech.github.io/glide/doc/configuration.html#manifest-parsing">Doc</a>
     *
     * @return boolean
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
