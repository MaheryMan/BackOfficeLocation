<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Reservation" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste des Réservations</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        h1 {
            color: #333;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        th {
            background-color: #007bff;
            color: white;
            padding: 12px;
            text-align: left;
        }
        td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #007bff;
            text-decoration: none;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <a href="/" class="back-link">← Retour à l'accueil</a>
    <h1>Liste des Réservations</h1>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nom Client</th>
                <th>Numéro Passport</th>
                <th>Email</th>
                <th>Contact</th>
                <th>Hôtel</th>
                <th>Date et Heure d'Arrivée</th>
                <th>Nombre de Passagers</th>
            </tr>
        </thead>
        <tbody>
            <%
                Object reservationsObj = request.getAttribute("reservations");
                if (reservationsObj instanceof List) {
                    for (Object item : (List<?>) reservationsObj) {
                        if (item instanceof Reservation) {
                            Reservation r = (Reservation) item;
            %>
                <tr>
                    <td><%= r.getId() %></td>
                    <td><%= r.getClient() != null ? r.getClient().getNom() : "N/A" %></td>
                    <td><%= r.getClient() != null ? r.getClient().getNumeroPassport() : "N/A" %></td>
                    <td><%= r.getClient() != null ? r.getClient().getEmail() : "N/A" %></td>
                    <td><%= r.getClient() != null ? r.getClient().getContact() : "N/A" %></td>
                    <td><%= r.getHotel() != null ? r.getHotel().getNom() : "N/A" %></td>
                    <td><%= r.getDateHeureArrivee() %></td>
                    <td><%= r.getNombrePassager() %></td>
                </tr>
            <%
                        }
                    }
                }
            %>
        </tbody>
    </table>
</body>
</html>
