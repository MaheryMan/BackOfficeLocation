<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Voiture" %>
<%@ page import="model.TypeEnergie" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title><% if (request.getAttribute("voiture") != null) { %>Modifier<% } else { %>Ajouter<% } %> une voiture</title>
    <link rel="stylesheet" href="../styles.css">
</head>
<body>
    <h1><% if (request.getAttribute("voiture") != null) { %>Modifier<% } else { %>Ajouter<% } %> une voiture</h1>

    <%
        Object messageObj = request.getAttribute("message");
        if (messageObj != null) {
    %>
    <p class="message"><%= messageObj %></p>
    <%
        }
    %>

    <%
        Voiture voiture = (Voiture) request.getAttribute("voiture");
        String numero = voiture != null ? voiture.getNumero() : "";
        Integer idTypeEnergie = voiture != null ? voiture.getIdTypeEnergie() : null;
        Integer capacite = voiture != null ? voiture.getCapacite() : null;
        Integer id = voiture != null ? voiture.getId() : null;
    %>

    <form action="<%= request.getContextPath() %>/voitures/form" method="post">
        <% if (id != null) { %>
        <input type="hidden" name="id" value="<%= id %>">
        <% } %>

        <label for="numero">Numero de la voiture</label>
        <input type="text" id="numero" name="numero" value="<%= numero %>" required>

        <label for="idTypeEnergie">Type d'energie</label>
        <select id="idTypeEnergie" name="idTypeEnergie" required>
            <option value="" disabled <%= idTypeEnergie == null ? "selected" : "" %>>Choisir un type d'energie</option>
            <%
                Object typesEnergieObj = request.getAttribute("typesEnergie");
                if (typesEnergieObj instanceof List) {
                    for (Object item : (List<?>) typesEnergieObj) {
                        if (item instanceof TypeEnergie) {
                            TypeEnergie typeEnergie = (TypeEnergie) item;
                            boolean isSelected = idTypeEnergie != null && idTypeEnergie.equals(typeEnergie.getId());
            %>
            <option value="<%= typeEnergie.getId() %>" <%= isSelected ? "selected" : "" %>><%= typeEnergie.getLibelle() %></option>
            <%
                        }
                    }
                }
            %>
        </select>

        <label for="capacite">Capacite (nombre de places)</label>
        <input type="number" id="capacite" name="capacite" min="1" value="<%= capacite != null ? capacite : "" %>" required>

        <button type="submit"><% if (id != null) { %>Modifier<% } else { %>Ajouter<% } %></button>
        <a href="<%= request.getContextPath() %>/voitures">Retour a la liste</a>
    </form>
</body>
</html>
