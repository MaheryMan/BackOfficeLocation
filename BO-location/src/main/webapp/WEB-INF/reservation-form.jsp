<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Client" %>
<%@ page import="model.Hotel" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Ajout reservation</title>
    <link rel="stylesheet" href="../styles.css">
</head>
<body>
    <h1>Ajouter une reservation</h1>

    <%
        Object messageObj = request.getAttribute("message");
        if (messageObj != null) {
    %>
    <p><%= messageObj %></p>
    <%
        }
    %>

    <form action="<%= request.getContextPath() %>/reservations/form" method="post">
        <label for="idClient">Client</label>
        <select id="idClient" name="idClient" required>
            <option value="" disabled selected>Choisir un client</option>
            <%
                Object clientsObj = request.getAttribute("clients");
                if (clientsObj instanceof List) {
                    for (Object item : (List<?>) clientsObj) {
                        if (item instanceof Client) {
                            Client client = (Client) item;
            %>
            <option value="<%= client.getId() %>"><%= client.getNom() %></option>
            <%
                        }
                    }
                }
            %>
        </select>

        <label for="idHotel">Hotel</label>
        <select id="idHotel" name="idHotel" required>
            <option value="" disabled selected>Choisir un hotel</option>
            <%
                Object hotelsObj = request.getAttribute("hotels");
                if (hotelsObj instanceof List) {
                    for (Object item : (List<?>) hotelsObj) {
                        if (item instanceof Hotel) {
                            Hotel hotel = (Hotel) item;
            %>
            <option value="<%= hotel.getId() %>"><%= hotel.getNom() %></option>
            <%
                        }
                    }
                }
            %>
        </select>

        <label for="dateHeureArrivee">Date et heure d'arrivee</label>
        <input type="datetime-local" id="dateHeureArrivee" name="dateHeureArrivee" required>

        <label for="nombrePassager">Nombre de passagers</label>
        <input type="number" id="nombrePassager" name="nombrePassager" min="1" required>

        <button type="submit">Ajouter</button>
    </form>
</body>
</html>
