<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.TypeEnergie" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Ajout voiture</title>
    <link rel="stylesheet" href="../styles.css">
</head>
<body>
    <h1>Ajouter une voiture</h1>

    <%
        Object messageObj = request.getAttribute("message");
        if (messageObj != null) {
    %>
    <p><%= messageObj %></p>
    <%
        }
    %>

    <form action="<%= request.getContextPath() %>/voitures/form" method="post">
        <label for="numero">Numero</label>
        <input type="text" id="numero" name="numero" required>

        <label for="idTypeEnergie">Type d'energie</label>
        <select id="idTypeEnergie" name="idTypeEnergie">
            <option value="">-- Aucun --</option>
            <%
                Object typesObj = request.getAttribute("types");
                if (typesObj instanceof List) {
                    for (Object item : (List<?>) typesObj) {
                        if (item instanceof TypeEnergie) {
                            TypeEnergie t = (TypeEnergie) item;
            %>
            <option value="<%= t.getId() %>"><%= t.getLibelle() %></option>
            <%
                        }
                    }
                }
            %>
        </select>

        <label for="capacite">Capacite</label>
        <input type="number" id="capacite" name="capacite" min="1" required>

        <button type="submit">Ajouter</button>
    </form>
</body>
</html>
