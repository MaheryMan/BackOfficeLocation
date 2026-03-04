<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Planification" %>
<%@ page import="model.Reservation" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Planification du <%= request.getAttribute("date") %></title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f7fa;
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        .header h1 {
            margin: 0 0 10px 0;
            font-size: 32px;
        }
        .header p {
            margin: 0;
            opacity: 0.9;
            font-size: 16px;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
            transition: color 0.3s;
        }
        .back-link:hover {
            color: #5568d3;
        }
        .section {
            background-color: white;
            border-radius: 10px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .section h2 {
            margin-top: 0;
            color: #333;
            border-bottom: 3px solid #667eea;
            padding-bottom: 10px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px;
            text-align: left;
            font-weight: 600;
        }
        td {
            padding: 12px 15px;
            border-bottom: 1px solid #e0e0e0;
        }
        tr:hover {
            background-color: #f8f9fa;
        }
        .badge {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }
        .badge-diesel {
            background-color: #28a745;
            color: white;
        }
        .badge-essence {
            background-color: #ffc107;
            color: #333;
        }
        .badge-warning {
            background-color: #dc3545;
            color: white;
        }
        .empty-message {
            text-align: center;
            padding: 40px;
            color: #999;
            font-size: 16px;
        }
        .stats {
            display: flex;
            gap: 20px;
            margin-top: 20px;
        }
        .stat-card {
            flex: 1;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
        }
        .stat-card .number {
            font-size: 36px;
            font-weight: bold;
            margin-bottom: 5px;
        }
        .stat-card .label {
            font-size: 14px;
            opacity: 0.9;
        }
        .actions {
            margin-top: 30px;
            text-align: center;
        }
        .btn {
            display: inline-block;
            padding: 12px 30px;
            background-color: #667eea;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 600;
            transition: background-color 0.3s;
        }
        .btn:hover {
            background-color: #5568d3;
        }
        .alert {
            background-color: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
            color: #856404;
        }
    </style>
</head>
<body>
    <a href="<%= request.getContextPath() %>/planification/form" class="back-link">← Changer de date</a>
    
    <div class="header">
        <h1> Planification des Véhicules</h1>
        <p>Date : <%= request.getAttribute("date") %></p>
    </div>

    <%
        Object planificationsObj = request.getAttribute("planifications");
        List<?> planifications = null;
        if (planificationsObj instanceof List) {
            planifications = (List<?>) planificationsObj;
        }
        
        Object reservationsSansVoitureObj = request.getAttribute("reservationsSansVoiture");
        List<?> reservationsSansVoiture = null;
        if (reservationsSansVoitureObj instanceof List) {
            reservationsSansVoiture = (List<?>) reservationsSansVoitureObj;
        }
        
        int totalPlanifications = planifications != null ? planifications.size() : 0;
        int totalSansVoiture = reservationsSansVoiture != null ? reservationsSansVoiture.size() : 0;
        
        // Calculer le nombre total de passagers
        int totalPassagers = 0;
        if (planifications != null) {
            for (Object item : planifications) {
                if (item instanceof Planification) {
                    Planification p = (Planification) item;
                    if (p.getReservation() != null) {
                        totalPassagers += p.getReservation().getNombrePassager();
                    }
                }
            }
        }
    %>

    <div class="stats">
        <div class="stat-card">
            <div class="number"><%= totalPlanifications %></div>
            <div class="label">Affectations</div>
        </div>
        <div class="stat-card">
            <div class="number"><%= totalPassagers %></div>
            <div class="label">Passagers</div>
        </div>
        <div class="stat-card">
            <div class="number"><%= totalSansVoiture %></div>
            <div class="label">Sans véhicule</div>
        </div>
    </div>

    <%
        if (totalSansVoiture > 0) {
    %>
    <div class="alert">
         <strong>Attention :</strong> <%= totalSansVoiture %> réservation(s) n'ont pas pu être affectées à un véhicule.
    </div>
    <%
        }
    %>

    <!-- Section des planifications -->
    <div class="section">
        <h2>
             Affectations Véhicule-Réservation
        </h2>
        
        <%
            if (planifications != null && !planifications.isEmpty()) {
        %>
        <table>
            <thead>
                <tr>
                    <th>Véhicule</th>
                    <th>Type</th>
                    <th>Capacité</th>
                    <th>Client</th>
                    <th>Hôtel</th>
                    <th>Distance</th>
                    <th>Passagers</th>
                    <th>Heure</th>
                </tr>
            </thead>
            <tbody>
                <%
                    for (Object item : planifications) {
                        if (item instanceof Planification) {
                            Planification p = (Planification) item;
                            if (p.getVoiture() != null && p.getReservation() != null) {
                                String typeEnergie = p.getVoiture().getTypeEnergie() != null ? 
                                    p.getVoiture().getTypeEnergie().getLibelle() : "N/A";
                                boolean isDiesel = p.getVoiture().estDiesel();
                %>
                <tr>
                    <td><strong><%= p.getVoiture().getNumero() %></strong></td>
                    <td>
                        <span class="badge <%= isDiesel ? "badge-diesel" : "badge-essence" %>">
                            <%= typeEnergie %>
                        </span>
                    </td>
                    <td><%= p.getVoiture().getCapacite() %> places</td>
                    <td><%= p.getReservation().getClient() != null ? p.getReservation().getClient().getNom() : "N/A" %></td>
                    <td><%= p.getReservation().getHotel() != null ? p.getReservation().getHotel().getNom() : "N/A" %></td>
                    <td><%= String.format("%.1f km", p.getDistance()) %></td>
                    <td><strong><%= p.getReservation().getNombrePassager() %></strong></td>
                    <td><%= p.getDateHeure() != null ? p.getDateHeure().substring(11, 16) : "N/A" %></td>
                </tr>
                <%
                            }
                        }
                    }
                %>
            </tbody>
        </table>
        <%
            } else {
        %>
        <div class="empty-message">
             Aucune planification pour cette date.
        </div>
        <%
            }
        %>
    </div>

    <!-- Section des réservations sans véhicule -->
    <%
        if (reservationsSansVoiture != null && !reservationsSansVoiture.isEmpty()) {
    %>
    <div class="section">
        <h2>
             Réservations sans véhicule assigné
        </h2>
        
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Client</th>
                    <th>Hôtel</th>
                    <th>Distance</th>
                    <th>Passagers</th>
                    <th>Heure</th>
                </tr>
            </thead>
            <tbody>
                <%
                    for (Object item : reservationsSansVoiture) {
                        if (item instanceof Reservation) {
                            Reservation r = (Reservation) item;
                %>
                <tr>
                    <td><span class="badge badge-warning">#<%= r.getId() %></span></td>
                    <td><%= r.getClient() != null ? r.getClient().getNom() : "N/A" %></td>
                    <td><%= r.getHotel() != null ? r.getHotel().getNom() : "N/A" %></td>
                    <td><%= r.getHotel() != null ? String.format("%.1f km", r.getHotel().getDistanceAeroport()) : "N/A" %></td>
                    <td><strong><%= r.getNombrePassager() %></strong></td>
                    <td><%= r.getDateHeureArrivee() != null ? r.getDateHeureArrivee().substring(11, 16) : "N/A" %></td>
                </tr>
                <%
                        }
                    }
                %>
            </tbody>
        </table>
    </div>
    <%
        }
    %>

    <div class="actions">
        <a href="<%= request.getContextPath() %>/planification/form" class="btn"> Voir une autre date</a>
        <a href="<%= request.getContextPath() %>/" class="btn"> Retour à l'accueil</a>
    </div>
</body>
</html>
