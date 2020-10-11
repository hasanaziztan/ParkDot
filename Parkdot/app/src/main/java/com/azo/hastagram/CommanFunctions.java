package com.azo.hastagram;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.azo.hastagram.Models.Park;
import com.google.gson.Gson;


public class CommanFunctions {

    public static void directionToLocation(Context context, double slat, double slon, double dlat, double dlon) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + slat + "," + slon + "&daddr=" + dlat + "," + dlon));
        context.startActivity(intent);

    }

    public static Park hashToModel(Object hashMap) {

        try {
            Gson gson = new Gson();
            String json = gson.toJson(hashMap);
            Park park = gson.fromJson(json, Park.class);
            return park;
        } catch (Exception e) {
            return null;
        }


    }
}
