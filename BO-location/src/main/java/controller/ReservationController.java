package controller;

import annotation.Controller;
import annotation.Get;
import annotation.Param;
import annotation.PathVariable;
import annotation.Post;
import annotation.RestAPI;
import model.Reservation;
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
        reservationService.create(reservation);

        ModelView view = new ModelView("WEB-INF/reservation-form.jsp");
        view.addObject("clients", clientService.readAll());
        view.addObject("hotels", hotelService.readAll());
        view.addObject("message", "Reservation ajoutee");
        return view;
    }
}