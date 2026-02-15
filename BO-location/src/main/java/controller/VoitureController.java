package controller;

import annotation.Controller;
import annotation.Get;
import annotation.Param;
import annotation.PathVariable;
import annotation.Post;
import annotation.RestAPI;
import model.Voiture;
import service.VoitureService;
import service.TypeEnergieService;
import util.ModelView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class VoitureController {

    private final VoitureService voitureService = new VoitureService();
    private final TypeEnergieService typeEnergieService = new TypeEnergieService();

    @Get("/api/voitures")
    @RestAPI
    public List<Voiture> readAll() throws SQLException {
        return voitureService.readAll();
    }

    @Get("/api/voitures/{id}")
    @RestAPI
    public Voiture read(@PathVariable("id") int id) throws SQLException {
        return voitureService.read(id);
    }

    @Get("/api/voitures/type/{idTypeEnergie}")
    @RestAPI
    public List<Voiture> readByTypeEnergie(@PathVariable("idTypeEnergie") int idTypeEnergie) throws SQLException {
        return voitureService.readByTypeEnergie(idTypeEnergie);
    }

    @Post("/api/voitures")
    @RestAPI
    public Voiture create(
            @Param("numero") String numero,
            @Param("idTypeEnergie") int idTypeEnergie,
            @Param("capacite") int capacite
    ) throws SQLException {
        Voiture voiture = new Voiture(
                null,
                numero,
                idTypeEnergie,
                capacite
        );
        voitureService.create(voiture);
        return voiture;
    }

    @Post("/api/voitures/{id}")
    @RestAPI
    public Voiture update(
            @PathVariable("id") int id,
            @Param("numero") String numero,
            @Param("idTypeEnergie") int idTypeEnergie,
            @Param("capacite") int capacite
    ) throws SQLException {
        Voiture voiture = new Voiture(
                id,
                numero,
                idTypeEnergie,
                capacite
        );
        voitureService.update(voiture);
        return voiture;
    }

    @Post("/api/voitures/{id}/delete")
    @RestAPI
    public Map<String, Object> delete(@PathVariable("id") int id) throws SQLException {
        voitureService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("deleted", true);
        result.put("id", id);
        return result;
    }

    @Get("/voitures")
    public ModelView voituresList() throws SQLException {
        ModelView view = new ModelView("voitures/list");
        view.addObject("voitures", voitureService.readAll());
        view.addObject("typesEnergie", typeEnergieService.readAll());
        return view;
    }

    @Get("/voitures/form")
    public ModelView voitureForm(@Param("id") Integer id) throws SQLException {
        ModelView view = new ModelView("voitures/form");
        view.addObject("typesEnergie", typeEnergieService.readAll());
        
        if (id != null) {
            view.addObject("voiture", voitureService.read(id));
        }
        
        return view;
    }

    @Post("/voitures/form")
    public ModelView submitVoitureForm(
            @Param("id") Integer id,
            @Param("numero") String numero,
            @Param("idTypeEnergie") int idTypeEnergie,
            @Param("capacite") int capacite
    ) throws SQLException {
        Voiture voiture = new Voiture(id, numero, idTypeEnergie, capacite);
        
        if (id == null) {
            voitureService.create(voiture);
        } else {
            voitureService.update(voiture);
        }

        ModelView view = new ModelView("voitures/form");
        view.addObject("typesEnergie", typeEnergieService.readAll());
        view.addObject("message", id == null ? "Voiture ajoutee avec succes" : "Voiture modifiee avec succes");
        return view;
    }

    @Post("/voitures/{id}/delete")
    public ModelView deleteVoiture(@PathVariable("id") int id) throws SQLException {
        voitureService.delete(id);
        
        ModelView view = new ModelView("voitures/list");
        view.addObject("voitures", voitureService.readAll());
        view.addObject("typesEnergie", typeEnergieService.readAll());
        view.addObject("message", "Voiture supprimee avec succes");
        return view;
    }
}
