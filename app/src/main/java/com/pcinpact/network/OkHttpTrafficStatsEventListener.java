/*
 * Copyright 2013 - 2026 Anael Mobilia and contributors
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
package com.pcinpact.network;

import android.net.TrafficStats;

import androidx.annotation.NonNull;

import java.net.InetSocketAddress;
import java.net.Proxy;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Protocol;

public final class OkHttpTrafficStatsEventListener extends EventListener {
    public final int tagNxi = 1337;

    public EventListener getEventListener() {
        return new EventListener() {
            @Override
            public void connectStart(@NonNull Call call, @NonNull InetSocketAddress inetSocketAddress, @NonNull Proxy proxy) {
                // Injecter un tag custom
                if(TrafficStats.getThreadStatsTag() != tagNxi) {
                    // Fix UntaggedSocketViolation: Untagged socket detected; use TrafficStats.setThreadSocketTag() to track all network usage
                    TrafficStats.setThreadStatsTag(tagNxi);
                }
            }

            @Override
            public void connectEnd(@NonNull Call call, @NonNull InetSocketAddress inetSocketAddress, @NonNull Proxy proxy, Protocol protocol) {
                TrafficStats.clearThreadStatsTag();
            }
        };
    }
}
