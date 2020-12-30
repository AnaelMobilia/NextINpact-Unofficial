/*
 * Copyright 2013 - 2020 Anael Mobilia and contributors
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
/* Originally source : https://gist.github.com/yrajabi/5776f4ade5695009f87ce7fcbc08078f
 * Originally Copyright (c) 2020. Yaser Rajabi https://github.com/yrajabi
 * Based on code by https://github.com/ddekanski
 * Issue : https://github.com/bumptech/glide/issues/3328
 */

package com.pcinpact.datastorage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GlideImageGetter implements Html.ImageGetter {
    private final WeakReference<TextView> container;
    private final boolean matchParentWidth;
    private float density = 1.0f;
    private final int placeholder;
    private final int error;
    private final boolean telecharger;

    /**
     * @param textView         Endroit où afficher l'image
     * @param matchParentWidth
     * @param densityAware
     * @param placeholderImage Image en attendant le téléchargement
     * @param errorImage       Image en cas d'erreur
     * @param telecharger      Faut-il effectuer un téléchargement ou utiliser uniquement le cache ?
     */
    public GlideImageGetter(TextView textView, boolean matchParentWidth, boolean densityAware, int placeholderImage,
                            int errorImage, boolean telecharger) {
        this.container = new WeakReference<>(textView);
        this.matchParentWidth = matchParentWidth;
        if (densityAware) {
            density = container.get().getResources().getDisplayMetrics().density;
        }
        this.placeholder = placeholderImage;
        this.error = errorImage;
        this.telecharger = telecharger;
    }

    @Override
    public Drawable getDrawable(String source) {
        BitmapDrawablePlaceholder drawable = new BitmapDrawablePlaceholder();

        if (telecharger) {
            // Téléchargement OK
            container.get().post(() -> Glide.with(container.get().getContext()).asBitmap().load(source).placeholder(
                    placeholder).error(error).into(drawable));
        } else {
            // Uniquement avec le cache
            container.get().post(() -> Glide.with(container.get().getContext()).asBitmap().load(source).placeholder(
                    placeholder).error(error).onlyRetrieveFromCache(true).into(drawable));
        }

        return drawable;
    }

    private class BitmapDrawablePlaceholder extends BitmapDrawable implements Target<Bitmap> {

        protected Drawable drawable;

        BitmapDrawablePlaceholder() {
            super(container.get().getResources(), Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        }

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        private void setDrawable(Drawable drawable) {
            this.drawable = drawable;
            int drawableWidth = (int) (drawable.getIntrinsicWidth() * density);
            int drawableHeight = (int) (drawable.getIntrinsicHeight() * density);
            int maxWidth = container.get().getMeasuredWidth();
            if ((drawableWidth > maxWidth) || matchParentWidth) {
                int calculatedHeight = maxWidth * drawableHeight / drawableWidth;
                drawable.setBounds(0, 0, maxWidth, calculatedHeight);
                setBounds(0, 0, maxWidth, calculatedHeight);
            } else {
                drawable.setBounds(0, 0, drawableWidth, drawableHeight);
                setBounds(0, 0, drawableWidth, drawableHeight);
            }

            container.get().setText(container.get().getText());
        }

        @Override
        public void onLoadStarted(@Nullable Drawable placeholderDrawable) {
            if (placeholderDrawable != null) {
                setDrawable(placeholderDrawable);
            }
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            if (errorDrawable != null) {
                setDrawable(errorDrawable);
            }
        }

        @Override
        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
            setDrawable(new BitmapDrawable(container.get().getResources(), bitmap));
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholderDrawable) {
            if (placeholderDrawable != null) {
                setDrawable(placeholderDrawable);
            }
        }

        @Override
        public void getSize(@NonNull SizeReadyCallback cb) {
            cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        @Override
        public void removeCallback(@NonNull SizeReadyCallback cb) {
        }

        @Override
        public void setRequest(@Nullable Request request) {
        }

        @Nullable
        @Override
        public Request getRequest() {
            return null;
        }

        @Override
        public void onStart() {
        }

        @Override
        public void onStop() {
        }

        @Override
        public void onDestroy() {
        }
    }
}
