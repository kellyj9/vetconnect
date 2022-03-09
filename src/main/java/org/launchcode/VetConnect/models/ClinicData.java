package org.launchcode.VetConnect.models;

import java.util.ArrayList;

public class ClinicData {

    public static ArrayList<Clinic> findClinic(String value, Iterable<Clinic> allClinics) {
        ArrayList<Clinic> results = new ArrayList<>();

        for(Clinic clinic : allClinics) {
            if(clinic.getCity().toLowerCase().contains(value.toLowerCase())) {
                results.add(clinic);
            } else if (clinic.getState().toLowerCase().contains(value.toLowerCase())) {
                results.add(clinic);
            } else if (clinic.getZip().toLowerCase().contains(value.toLowerCase())) {
            results.add(clinic);
        }
        }
        return results;
    }
}
