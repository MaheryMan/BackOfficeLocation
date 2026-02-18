package controller;

import annotation.Controller;
import annotation.Get;
import annotation.Param;
import annotation.PathVariable;
import annotation.Post;
import annotation.RestAPI;
import model.Reservation;
import model.Voiture;
import model.TypeEnergie;
import service.ClientService;
import service.HotelService;
import service.ReservationService;
import util.ModelView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReservationController {

    private final ReservationService reservationService = new ReservationService();
    private final ClientService clientService = new ClientService();
    private final HotelService hotelService = new HotelService();
    private final service.VoitureService voitureService = new service.VoitureService();

    @Get("/api/reservations")
    @RestAPI
    public List<Reservation> readAll() throws SQLException {
        return reservationService.readAll();
    }

    @Get("/api/reservations/{id}")
    @RestAPI
    public Reservation read(@PathVariable("id") int id) throws SQLException {
        return reservationService.read(id);
    }

    @Post("/api/reservations")
    @RestAPI
    public Reservation create(
            @Param("idClient") int idClient,
            @Param("idHotel") int idHotel,
            @Param("dateHeureArrivee") String dateHeureArrivee,
            @Param("nombrePassager") int nombrePassager
    ) throws SQLException {
        Reservation reservation = new Reservation(
                null,
                clientService.read(idClient),
                hotelService.read(idHotel),
                dateHeureArrivee,
                nombrePassager
        );
        // assigner automatiquement une voiture selon les regles metier
        Voiture v = reservationService.trouverVoiturePourPassengers(nombrePassager, dateHeureArrivee);
        if (v != null) {
            reservation.setVoiture(v);
        }
        reservationService.create(reservation);
        return reservation;
    }

    @Post("/api/reservations/{id}")
    @RestAPI
    public Reservation update(
            @PathVariable("id") int id,
            @Param("idClient") int idClient,
            @Param("idHotel") int idHotel,
            @Param("dateHeureArrivee") String dateHeureArrivee,
            @Param("nombrePassager") int nombrePassager
    ) throws SQLException {
        Reservation reservation = new Reservation(
                id,
                clientService.read(idClient),
                hotelService.read(idHotel),
                dateHeureArrivee,
                nombrePassager
        );
        reservationService.update(reservation);
        return reservation;
    }

    @Post("/api/reservations/{id}/delete")
    @RestAPI
    public Map<String, Object> delete(@PathVariable("id") int id) throws SQLException {
        reservationService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("deleted", true);
        result.put("id", id);
        return result;
    }

    @Get("/reservations/form")
    public ModelView reservationForm() throws SQLException {
        ModelView view = new ModelView("WEB-INF/reservation-form.jsp");
        view.addObject("clients", clientService.readAll());
        view.addObject("hotels", hotelService.readAll());
        return view;
    }

    @Post("/reservations/form")
    public ModelView submitReservationForm(
            @Param("idClient") int idClient,
            @Param("idHotel") int idHotel,
            @Param("dateHeureArrivee") String dateHeureArrivee,
            @Param("nombrePassager") int nombrePassager
    ) throws SQLException {
        Reservation reservation = new Reservation(
                null,
                clientService.read(idClient),
                hotelService.read(idHotel),
                dateHeureArrivee,
                nombrePassager
        );
        Voiture v = reservationService.trouverVoiturePourPassengers(nombrePassager, dateHeureArrivee);
        if (v != null) {
            reservation.setVoiture(v);
        }
        reservationService.create(reservation);

        ModelView view = new ModelView("WEB-INF/reservation-form.jsp");
        view.addObject("clients", clientService.readAll());
        view.addObject("hotels", hotelService.readAll());
        view.addObject("message", "Reservation ajoutee");
        return view;
    }

    @Get("/voitures/form")
    public ModelView voitureForm() throws SQLException {
        ModelView view = new ModelView("WEB-INF/voiture-form.jsp");
        view.addObject("types", voitureService.readAllTypes());
        return view;
    }

    @Post("/voitures/form")
    public ModelView submitVoitureForm(
            @Param("numero") String numero,
            @Param("idTypeEnergie") Integer idTypeEnergie,
            @Param("capacite") int capacite
    ) throws SQLException {
        model.TypeEnergie te = null;
        if (idTypeEnergie != null) te = new model.TypeEnergie(idTypeEnergie, null);
        model.Voiture v = new model.Voiture(null, numero, te, capacite);
        voitureService.create(v);

        ModelView view = new ModelView("WEB-INF/voiture-form.jsp");
        view.addObject("types", voitureService.readAllTypes());
        view.addObject("message", "Voiture creee (id=" + v.getId() + ")");
        return view;
    }
}