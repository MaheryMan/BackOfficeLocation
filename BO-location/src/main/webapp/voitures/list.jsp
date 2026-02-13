<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="model.Voiture" %>
<%@ page import="model.TypeEnergie" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des voitures</title>
    <link rel="stylesheet" href="../styles.css">
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #4CAF50;
            color: white;
        }
        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
        .actions {
            display: flex;
            gap: 10px;
        }
        .btn {
            padding: 5px 10px;
            text-decoration: none;
            border-radius: 3px;
            display: inline-block;
        }
        .btn-edit {
            background-color: #2196F3;
            color: white;
        }
        .btn-delete {
            background-color: #f44336;
            color: white;
        }
        .btn-add {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 3px;
            display: inline-block;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <h1>Liste des voitures</h1>

    <%
        Object messageObj = request.getAttribute("message");
        if (messageObj != null) {
    %>
    <p class="message"><%= messageObj %></p>
    <%
        }
    %>

    <a href="<%= request.getContextPath() %>/voitures/form" class="btn-add">+ Ajouter une voiture</a>

    <%
        // Creer un map des types d'energie pour un acces rapide
        Map<Integer, String> typesEnergieMap = new HashMap<>();
        Object typesEnergieObj = request.getAttribute("typesEnergie");
        if (typesEnergieObj instanceof List) {
            for (Object item : (List<?>) typesEnergieObj) {
                if (item instanceof TypeEnergie) {
                    TypeEnergie typeEnergie = (TypeEnergie) item;
                    typesEnergieMap.put(typeEnergie.getId(), typeEnergie.getLibelle());
                }
            }
        }
    %>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Numero</th>
                <th>Type d'energie</th>
                <th>Capacite</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
                Object voituresObj = request.getAttribute("voitures");
                if (voituresObj instanceof List) {
                    List<?> voituresList = (List<?>) voituresObj;
                    if (voituresList.isEmpty()) {
            %>
            <tr>
                <td colspan="5" style="text-align: center;">Aucune voiture disponible</td>
            </tr>
            <%
                    } else {
                        for (Object item : voituresList) {
                            if (item instanceof Voiture) {
                                Voiture voiture = (Voiture) item;
                                String typeEnergieLibelle = typesEnergieMap.getOrDefault(
                                    voiture.getIdTypeEnergie(), 
                                    "Inconnu"
                                );
            %>
            <tr>
                <td><%= voiture.getId() %></td>
                <td><%= voiture.getNumero() %></td>
                <td><%= typeEnergieLibelle %></td>
                <td><%= voiture.getCapacite() %> places</td>
                <td>
                    <div class="actions">
                        <a href="<%= request.getContextPath() %>/voitures/form?id=<%= voiture.getId() %>" class="btn btn-edit">Modifier</a>
                        <form action="<%= request.getContextPath() %>/voitures/<%= voiture.getId() %>/delete" method="post" style="margin: 0;">
                            <button type="submit" class="btn btn-delete" onclick="return confirm('Etes-vous sur de vouloir supprimer cette voiture ?');">Supprimer</button>
                        </form>
                    </div>
                </td>
            </tr>
            <%
                            }
                        }
                    }
                }
            %>
        </tbody>
    </table>

    <br>
    <a href="<%= request.getContextPath() %>/">Retour a l'accueil</a>
</body>
</html>
