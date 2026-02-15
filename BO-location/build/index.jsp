<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>BO-location - Accueil</title>
    <link rel="stylesheet" href="styles.css">
    <style>
        .menu-container {
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .menu-section {
            background-color: #f9f9f9;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .menu-section h2 {
            color: #333;
            margin-top: 0;
            border-bottom: 2px solid #4CAF50;
            padding-bottom: 10px;
        }
        .menu-links {
            display: flex;
            flex-direction: column;
            gap: 10px;
            margin-top: 15px;
        }
        .menu-link {
            background-color: #4CAF50;
            color: white;
            padding: 12px 20px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            transition: background-color 0.3s;
        }
        .menu-link:hover {
            background-color: #45a049;
        }
        .menu-link.secondary {
            background-color: #2196F3;
        }
        .menu-link.secondary:hover {
            background-color: #0b7dda;
        }
    </style>
</head>
<body>
    <div class="menu-container">
        <h1>BackOffice - Gestion de Location</h1>
        
        <div class="menu-section">
            <h2>🚗 Gestion des Voitures</h2>
            <div class="menu-links">
                <a href="<%= request.getContextPath() %>/voitures" class="menu-link">Liste des voitures</a>
                <a href="<%= request.getContextPath() %>/voitures/form" class="menu-link secondary">Ajouter une voiture</a>
            </div>
        </div>

        <div class="menu-section">
            <h2>📋 Gestion des Réservations</h2>
            <div class="menu-links">
                <a href="<%= request.getContextPath() %>/reservations/form" class="menu-link">Ajouter une réservation</a>
            </div>
        </div>
    </div>
</body>
</html>
