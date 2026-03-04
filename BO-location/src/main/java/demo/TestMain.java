package demo;

import java.sql.SQLException;
import java.util.*;
import model.TypeEnergie;
import model.Voiture;
import service.ReservationService;

public class TestMain {

    static class SimpleReservation {
        int voitureId;
        int nombrePassager;
        String date;

        SimpleReservation(int voitureId, int nombrePassager, String date) {
            this.voitureId = voitureId;
            this.nombrePassager = nombrePassager;
            this.date = date;
        }
    }

    public static Voiture chooseBestCar(List<Voiture> cars, List<SimpleReservation> resas, int passengers, String date) {
        Map<Integer, Integer> reservedByCar = new HashMap<>();
        for (SimpleReservation r : resas) {
            if (date == null || date.isBlank() || Objects.equals(r.date, date)) {
                reservedByCar.put(r.voitureId, reservedByCar.getOrDefault(r.voitureId, 0) + r.nombrePassager);
            }
        }

        List<Voiture> candidates = new ArrayList<>();
        for (Voiture v : cars) {
            int reserved = reservedByCar.getOrDefault(v.getId(), 0);
            int available = v.getCapacite() - reserved;
            if (v.getCapacite() > passengers && available >= passengers) {
                candidates.add(v);
            }
        }

        if (candidates.isEmpty()) return null;

        // choose minimal capacity
        int minCap = candidates.stream().mapToInt(Voiture::getCapacite).min().orElse(Integer.MAX_VALUE);
        List<Voiture> best = new ArrayList<>();
        for (Voiture v : candidates) if (v.getCapacite() == minCap) best.add(v);

        if (best.size() == 1) return best.get(0);

        // prefer diesel
        List<Voiture> diesel = new ArrayList<>();
        for (Voiture v : best) {
            if (v.getTypeEnergie() != null && "diesel".equalsIgnoreCase(v.getTypeEnergie().getLibelle())) diesel.add(v);
        }
        List<Voiture> pool = diesel.isEmpty() ? best : diesel;

        // random among tied
        Random rnd = new Random();
        return pool.get(rnd.nextInt(pool.size()));
    }

    public static void main(String[] args) {
        TypeEnergie diesel = new TypeEnergie(1, "diesel");
        TypeEnergie essence = new TypeEnergie(2, "essence");

        Voiture A = new Voiture(1, "A", diesel, 7);
        Voiture B = new Voiture(2, "B", essence, 12);
        Voiture C = new Voiture(3, "C", diesel, 19);

        List<Voiture> cars = Arrays.asList(A, B, C);

        List<SimpleReservation> resas = new ArrayList<>();
        // Exemple: la voiture A a déjà 1 passager de réservé à la même date
        String date = "2026-02-15 10:00:00";
        resas.add(new SimpleReservation(1, 1, date));

        int[] tests = {5, 6, 11, 12, 18};
        System.out.println("=== Test selection en memoire (logique metier) ===");
        for (int p : tests) {
            Voiture choix = chooseBestCar(cars, resas, p, date);
            System.out.printf("passagers=%d -> %s\n", p, choix != null ? choix.getNumero() + " (cap=" + choix.getCapacite() + ", energie=" + (choix.getTypeEnergie()!=null?choix.getTypeEnergie().getLibelle():"?") + ")" : "aucune voiture disponible");
        }

        System.out.println();
        System.out.println("=== Tentative d'appel DB via ReservationService.trouverVoiturePourPassengers(...) ===");
        try {
            ReservationService svc = new ReservationService();
            Voiture v = svc.trouverVoiturePourPassengers(6, date);
            System.out.println("Resultat DB (6 passagers): " + (v != null ? v.getNumero() + " cap=" + v.getCapacite() : "null"));
        } catch (SQLException e) {
            System.out.println("Impossible d'appeler la DB: " + e.getMessage());
        }
    }
}
