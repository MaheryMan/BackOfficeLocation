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
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Syne:wght@600;700;800&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        :root {
            --navy-950:  #060d1a;
            --navy-700:  #163058;
            --accent:    #3b82f6;
            --gold:      #c9a84c;
            --gold-soft: rgba(201,168,76,0.1);
            --danger:    #ef4444;
            --danger-dim: rgba(239,68,68,0.12);
            --success:   #22c55e;
            --success-dim: rgba(34,197,94,0.12);
            --warning:   #f59e0b;
            --warning-dim: rgba(245,158,11,0.12);
            --text-primary:   #e8edf5;
            --text-secondary: #8fa3be;
            --text-muted:     #4a6080;
            --border:    rgba(59,130,246,0.13);
            --glass:     rgba(11,22,40,0.7);
            --row-hover: rgba(59,130,246,0.05);
        }

        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'DM Sans', sans-serif;
            background-color: var(--navy-950);
            color: var(--text-primary);
            min-height: 100vh;
            padding: 40px 24px 60px;
        }

        .bg-layer {
            position: fixed;
            inset: 0;
            z-index: 0;
            background:
                radial-gradient(ellipse 70% 50% at 5% 10%, rgba(30,68,120,0.35) 0%, transparent 60%),
                radial-gradient(ellipse 50% 40% at 90% 80%, rgba(38,93,156,0.2) 0%, transparent 55%),
                var(--navy-950);
        }
        .bg-grid {
            position: fixed;
            inset: 0;
            z-index: 0;
            background-image:
                linear-gradient(rgba(59,130,246,0.035) 1px, transparent 1px),
                linear-gradient(90deg, rgba(59,130,246,0.035) 1px, transparent 1px);
            background-size: 60px 60px;
            mask-image: radial-gradient(ellipse 90% 90% at center, black 40%, transparent 100%);
        }

        .page {
            position: relative;
            z-index: 1;
            max-width: 1100px;
            margin: 0 auto;
        }

        .nav-top {
            margin-bottom: 36px;
            animation: fadeDown 0.5s ease both;
        }
        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            color: var(--text-muted);
            text-decoration: none;
            font-size: 13px;
            font-weight: 500;
            transition: color 0.2s;
        }
        .back-link::before { content: '←'; }
        .back-link:hover { color: var(--accent); }

        .page-header {
            margin-bottom: 36px;
            animation: fadeDown 0.55s ease both;
        }
        .page-label {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            background: var(--gold-soft);
            border: 1px solid rgba(201,168,76,0.25);
            color: var(--gold);
            font-family: 'Syne', sans-serif;
            font-size: 10px;
            font-weight: 700;
            letter-spacing: 0.2em;
            text-transform: uppercase;
            padding: 5px 14px;
            border-radius: 100px;
            margin-bottom: 16px;
        }
        .page-label::before {
            content: '';
            width: 5px; height: 5px;
            background: var(--gold);
            border-radius: 50%;
        }
        h1 {
            font-family: 'Syne', sans-serif;
            font-size: clamp(24px, 4vw, 36px);
            font-weight: 800;
            letter-spacing: -0.03em;
            color: var(--text-primary);
            margin-bottom: 6px;
        }
        .page-date {
            font-size: 14px;
            font-weight: 300;
            color: var(--text-secondary);
        }
        .page-date strong {
            color: var(--accent);
            font-weight: 500;
        }

        .stats-row {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 14px;
            margin-bottom: 28px;
            animation: fadeUp 0.55s 0.1s ease both;
        }
        @media (max-width: 500px) { .stats-row { grid-template-columns: 1fr; } }

        .stat-card {
            background: var(--glass);
            border: 1px solid var(--border);
            border-radius: 14px;
            padding: 22px 20px;
            backdrop-filter: blur(12px);
        }
        .stat-number {
            font-family: 'Syne', sans-serif;
            font-size: 36px;
            font-weight: 800;
            color: var(--text-primary);
            line-height: 1;
            margin-bottom: 6px;
        }
        .stat-label {
            font-size: 12px;
            font-weight: 400;
            color: var(--text-muted);
            text-transform: uppercase;
            letter-spacing: 0.1em;
        }

        .alert {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            background: var(--warning-dim);
            border: 1px solid rgba(245,158,11,0.25);
            border-radius: 12px;
            padding: 14px 18px;
            margin-bottom: 24px;
            font-size: 13px;
            color: #fbbf24;
            animation: fadeUp 0.55s 0.15s ease both;
        }
        .alert strong { font-weight: 600; }

        .section {
            background: var(--glass);
            border: 1px solid var(--border);
            border-radius: 16px;
            overflow: hidden;
            margin-bottom: 20px;
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            animation: fadeUp 0.55s 0.2s ease both;
        }
        .section + .section { animation-delay: 0.3s; }

        .section-header {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 20px 24px;
            border-bottom: 1px solid var(--border);
        }
        .section-header h2 {
            font-family: 'Syne', sans-serif;
            font-size: 15px;
            font-weight: 700;
            color: var(--text-primary);
            letter-spacing: -0.01em;
        }
        .section-count {
            margin-left: auto;
            font-size: 11px;
            font-weight: 600;
            color: var(--text-muted);
            background: rgba(255,255,255,0.05);
            border: 1px solid var(--border);
            border-radius: 100px;
            padding: 3px 10px;
        }

        .table-wrap { overflow-x: auto; }

        table { width: 100%; border-collapse: collapse; }

        thead th {
            padding: 12px 20px;
            font-family: 'Syne', sans-serif;
            font-size: 10px;
            font-weight: 700;
            letter-spacing: 0.13em;
            text-transform: uppercase;
            color: var(--text-muted);
            text-align: left;
            border-bottom: 1px solid var(--border);
            white-space: nowrap;
        }
        tbody td {
            padding: 14px 20px;
            font-size: 14px;
            color: var(--text-secondary);
            border-bottom: 1px solid rgba(59,130,246,0.06);
            white-space: nowrap;
        }
        tbody tr:last-child td { border-bottom: none; }
        tbody tr:hover td { background: var(--row-hover); color: var(--text-primary); }
        td strong { font-weight: 600; color: var(--text-primary); }

        .badge {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 100px;
            font-size: 11px;
            font-weight: 600;
            font-family: 'Syne', sans-serif;
            letter-spacing: 0.04em;
        }
        .badge-diesel  { background: var(--success-dim); color: var(--success); border: 1px solid rgba(34,197,94,0.2); }
        .badge-essence { background: var(--warning-dim); color: var(--warning); border: 1px solid rgba(245,158,11,0.2); }
        .badge-warning { background: var(--danger-dim);  color: var(--danger);  border: 1px solid rgba(239,68,68,0.2); }

        .empty {
            padding: 50px 24px;
            text-align: center;
            color: var(--text-muted);
            font-size: 14px;
            font-weight: 300;
        }

        .actions {
            display: flex;
            gap: 12px;
            justify-content: center;
            margin-top: 36px;
            flex-wrap: wrap;
            animation: fadeUp 0.55s 0.35s ease both;
        }
        .btn {
            display: inline-block;
            padding: 12px 26px;
            border-radius: 10px;
            font-family: 'Syne', sans-serif;
            font-size: 13px;
            font-weight: 700;
            letter-spacing: 0.04em;
            text-decoration: none;
            transition: transform 0.2s, box-shadow 0.2s, opacity 0.2s;
        }
        .btn-primary {
            background: linear-gradient(135deg, var(--navy-700) 0%, var(--accent) 100%);
            color: white;
        }
        .btn-primary:hover {
            opacity: 0.9;
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(59,130,246,0.25);
        }
        .btn-ghost {
            background: transparent;
            color: var(--text-secondary);
            border: 1px solid var(--border);
        }
        .btn-ghost:hover {
            color: var(--text-primary);
            border-color: rgba(59,130,246,0.35);
            transform: translateY(-2px);
        }

        @keyframes fadeDown {
            from { opacity: 0; transform: translateY(-14px); }
            to   { opacity: 1; transform: translateY(0); }
        }
        @keyframes fadeUp {
            from { opacity: 0; transform: translateY(16px); }
            to   { opacity: 1; transform: translateY(0); }
        }
    </style>
</head>
<body>

    <div class="bg-layer"></div>
    <div class="bg-grid"></div>

    <main class="page">

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

        <div class="nav-top">
            <a href="<%= request.getContextPath() %>/planification/form" class="back-link">Changer de date</a>
        </div>

        <div class="page-header">
            <div class="page-label">Planification</div>
            <h1>Affectations véhicules</h1>
            <p class="page-date">Date : <strong><%= request.getAttribute("date") %></strong></p>
        </div>

        <div class="stats-row">
            <div class="stat-card">
                <div class="stat-number"><%= totalPlanifications %></div>
                <div class="stat-label">Affectations</div>
            </div>
            <div class="stat-card">
                <div class="stat-number"><%= totalPassagers %></div>
                <div class="stat-label">Passagers</div>
            </div>
            <div class="stat-card">
                <div class="stat-number"><%= totalSansVoiture %></div>
                <div class="stat-label">Sans véhicule</div>
            </div>
        </div>

        <% if (totalSansVoiture > 0) { %>
        <div class="alert">
            <strong>Attention —</strong>&nbsp;<%= totalSansVoiture %> réservation(s) n'ont pas pu être affectées à un véhicule.
        </div>
        <% } %>

        <div class="section">
            <div class="section-header">
                <h2>Affectations véhicule-réservation</h2>
                <span class="section-count"><%= totalPlanifications %> entrée<%= totalPlanifications > 1 ? "s" : "" %></span>
            </div>

            <% if (planifications != null && !planifications.isEmpty()) { %>
            <div class="table-wrap">
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
                            <th>HeureDepart</th>
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
                            <td><span class="badge <%= isDiesel ? "badge-diesel" : "badge-essence" %>"><%= typeEnergie %></span></td>
                            <td><%= p.getVoiture().getCapacite() %> places</td>
                            <td><%= p.getReservation().getClient() != null ? p.getReservation().getClient().getNom() : "N/A" %></td>
                            <td><%= p.getReservation().getHotel() != null ? p.getReservation().getHotel().getNom() : "N/A" %></td>
                            <td><%= String.format("%.1f km", p.getDistance()) %></td>
                            <td><strong><%= p.getReservation().getNombrePassager() %></strong></td>
                            <td><%= p.getDateHeure() != null ? p.getDateHeure().substring(11, 16) : "N/A" %></td>
                            <td><%= p.getDateHeureDepart() != null ? p.getDateHeureDepart().substring(11, 16) : "N/A" %></td>
                        </tr>
                        <%
                                    }
                                }
                            }
                        %>
                    </tbody>
                </table>
            </div>
            <% } else { %>
            <div class="empty">Aucune planification pour cette date.</div>
            <% } %>
        </div>

        <% if (reservationsSansVoiture != null && !reservationsSansVoiture.isEmpty()) { %>
        <div class="section">
            <div class="section-header">
                <h2>Réservations sans véhicule assigné</h2>
                <span class="section-count"><%= totalSansVoiture %> entrée<%= totalSansVoiture > 1 ? "s" : "" %></span>
            </div>
            <div class="table-wrap">
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
        </div>
        <% } %>

        <div class="actions">
            <a href="<%= request.getContextPath() %>/planification/form" class="btn btn-primary">Voir une autre date</a>
            <a href="<%= request.getContextPath() %>/" class="btn btn-ghost">Retour à l'accueil</a>
        </div>

    </main>
</body>
</html>
