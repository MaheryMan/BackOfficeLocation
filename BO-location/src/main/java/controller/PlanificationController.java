package controller;

import annotation.Controller;
import annotation.Get;
import annotation.Param;
import annotation.RestAPI;
import model.Planification;
import model.Reservation;
import service.PlanificationService;
import util.ModelView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class PlanificationController {

    private final PlanificationService planificationService = new PlanificationService();

    /**
     * API REST pour obtenir la planification d'une date donnée
     * Exemple: /api/planification?date=2026-03-04
     */
    @Get("/api/planification")
    @RestAPI
    public List<Planification> getPlanificationAPI(@Param("date") String dateStr) throws SQLException {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return planificationService.getPlanification(date);
    }

    /**
     * API REST pour obtenir les réservations sans voiture assignée
     * Exemple: /api/reservations-sans-voiture?date=2026-03-04
     */
    @Get("/api/reservations-sans-voiture")
    @RestAPI
    public List<Reservation> getReservationsSansVoitureAPI(@Param("date") String dateStr) throws SQLException {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return planificationService.getReservationsSansVoiture(date);
    }

    /**
     * Interface pour afficher la planification d'une date
     * Exemple: /planification?date=2026-03-04
     */
    @Get("/planification")
    public ModelView getPlanificationView(@Param("date") String dateStr) throws SQLException {
        ModelView view = new ModelView("planification/index");
        
        if (dateStr == null || dateStr.isEmpty()) {
            // Date par défaut: aujourd'hui
            dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<Planification> planifications = planificationService.getPlanification(date);
        List<Reservation> reservationsSansVoiture = planificationService.getReservationsSansVoiture(date);
        
        view.addObject("planifications", planifications);
        view.addObject("reservationsSansVoiture", reservationsSansVoiture);
        view.addObject("date", dateStr);
        
        return view;
    }

    /**
     * Formulaire de sélection de date pour la planification
     */
    @Get("/planification/form")
    public ModelView planificationForm() {
        ModelView view = new ModelView("planification/form");
        String dateAujourdhui = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        view.addObject("dateAujourdhui", dateAujourdhui);
        return view;
    }
}
