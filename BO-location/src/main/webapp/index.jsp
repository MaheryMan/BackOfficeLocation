<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BO-location — Back Office</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        /* ── VARIABLES ─────────────────────────────────────────────── */
        :root {
            --navy-950:  #060d1a;
            --navy-900:  #0b1628;
            --navy-800:  #0f2040;
            --navy-700:  #163058;
            --navy-600:  #1e4478;
            --navy-500:  #265d9c;
            --accent:    #3b82f6;
            --accent-glow: rgba(59,130,246,0.25);
            --gold:      #c9a84c;
            --gold-soft: rgba(201,168,76,0.12);
            --text-primary:   #e8edf5;
            --text-secondary: #8fa3be;
            --text-muted:     #4a6080;
            --border:    rgba(59,130,246,0.15);
            --glass:     rgba(11,22,40,0.7);
        }

        /* ── RESET ─────────────────────────────────────────────────── */
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        /* ── BODY ──────────────────────────────────────────────────── */
        body {
            font-family: 'DM Sans', sans-serif;
            background-color: var(--navy-950);
            color: var(--text-primary);
            min-height: 100vh;
            overflow-x: hidden;
        }

        /* ── FOND ANIMÉ ────────────────────────────────────────────── */
        .bg-layer {
            position: fixed;
            inset: 0;
            z-index: 0;
            background:
                radial-gradient(ellipse 80% 60% at 10% 20%, rgba(30,68,120,0.45) 0%, transparent 60%),
                radial-gradient(ellipse 60% 50% at 85% 75%, rgba(38,93,156,0.3) 0%, transparent 55%),
                var(--navy-950);
        }
        .bg-grid {
            position: fixed;
            inset: 0;
            z-index: 0;
            background-image:
                linear-gradient(rgba(59,130,246,0.04) 1px, transparent 1px),
                linear-gradient(90deg, rgba(59,130,246,0.04) 1px, transparent 1px);
            background-size: 60px 60px;
            mask-image: radial-gradient(ellipse 90% 90% at center, black 40%, transparent 100%);
        }

        /* ── LAYOUT ────────────────────────────────────────────────── */
        .page {
            position: relative;
            z-index: 1;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 60px 24px;
        }

        /* ── HEADER ────────────────────────────────────────────────── */
        .header {
            text-align: center;
            margin-bottom: 64px;
            animation: fadeDown 0.7s ease both;
        }
        .header-badge {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            background: var(--gold-soft);
            border: 1px solid rgba(201,168,76,0.3);
            color: var(--gold);
            font-family: 'Syne', sans-serif;
            font-size: 11px;
            font-weight: 600;
            letter-spacing: 0.2em;
            text-transform: uppercase;
            padding: 6px 16px;
            border-radius: 100px;
            margin-bottom: 24px;
        }
        .header-badge::before {
            content: '';
            width: 6px; height: 6px;
            background: var(--gold);
            border-radius: 50%;
            animation: pulse 2s infinite;
        }
        h1 {
            font-family: 'Syne', sans-serif;
            font-size: clamp(36px, 6vw, 58px);
            font-weight: 800;
            letter-spacing: -0.03em;
            line-height: 1.1;
            color: var(--text-primary);
        }
        h1 .accent-word {
            color: var(--accent);
            position: relative;
        }
        .subtitle {
            margin-top: 14px;
            font-size: 15px;
            font-weight: 300;
            color: var(--text-secondary);
            letter-spacing: 0.02em;
        }

        /* ── GRID DES CARTES ──────────────────────────────────────── */
        .cards-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 16px;
            width: 100%;
            max-width: 780px;
        }
        @media (max-width: 580px) {
            .cards-grid { grid-template-columns: 1fr; }
        }

        /* ── CARTE ─────────────────────────────────────────────────── */
        .card {
            position: relative;
            display: flex;
            align-items: flex-start;
            gap: 18px;
            padding: 28px 26px;
            background: var(--glass);
            border: 1px solid var(--border);
            border-radius: 16px;
            text-decoration: none;
            color: inherit;
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            overflow: hidden;
            transition: transform 0.3s ease, border-color 0.3s ease, box-shadow 0.3s ease;
            animation: fadeUp 0.6s ease both;
        }
        /* stagger d'animation */
        .card:nth-child(1) { animation-delay: 0.15s; }
        .card:nth-child(2) { animation-delay: 0.25s; }
        .card:nth-child(3) { animation-delay: 0.35s; }
        .card:nth-child(4) { animation-delay: 0.45s; }

        .card::before {
            content: '';
            position: absolute;
            inset: 0;
            background: linear-gradient(135deg, rgba(59,130,246,0.07) 0%, transparent 60%);
            opacity: 0;
            transition: opacity 0.3s ease;
        }
        .card:hover {
            transform: translateY(-4px);
            border-color: rgba(59,130,246,0.4);
            box-shadow: 0 16px 40px rgba(0,0,0,0.35), 0 0 0 1px rgba(59,130,246,0.15);
        }
        .card:hover::before { opacity: 1; }

        /* Trait coloré gauche au hover */
        .card::after {
            content: '';
            position: absolute;
            left: 0; top: 20%; bottom: 20%;
            width: 3px;
            background: linear-gradient(180deg, var(--accent), transparent);
            border-radius: 0 4px 4px 0;
            opacity: 0;
            transition: opacity 0.3s ease, top 0.3s ease, bottom 0.3s ease;
        }
        .card:hover::after { opacity: 1; top: 15%; bottom: 15%; }

        /* ── ICÔNE ─────────────────────────────────────────────────── */
        .card-icon {
            flex-shrink: 0;
            width: 48px; height: 48px;
            background: rgba(59,130,246,0.1);
            border: 1px solid rgba(59,130,246,0.2);
            border-radius: 12px;
            display: grid;
            place-items: center;
            font-size: 22px;
            transition: background 0.3s ease, transform 0.3s ease;
        }
        .card:hover .card-icon {
            background: rgba(59,130,246,0.18);
            transform: scale(1.05) rotate(-2deg);
        }

        /* ── TEXTE CARTE ───────────────────────────────────────────── */
        .card-body { flex: 1; }
        .card-title {
            font-family: 'Syne', sans-serif;
            font-size: 16px;
            font-weight: 700;
            color: var(--text-primary);
            margin-bottom: 6px;
            letter-spacing: -0.01em;
        }
        .card-desc {
            font-size: 13px;
            font-weight: 300;
            color: var(--text-secondary);
            line-height: 1.5;
        }

        /* ── FLÈCHE ────────────────────────────────────────────────── */
        .card-arrow {
            align-self: center;
            flex-shrink: 0;
            color: var(--text-muted);
            font-size: 18px;
            transition: transform 0.3s ease, color 0.3s ease;
        }
        .card:hover .card-arrow {
            transform: translateX(4px);
            color: var(--accent);
        }

        /* ── FOOTER ────────────────────────────────────────────────── */
        .footer {
            margin-top: 56px;
            font-size: 12px;
            color: var(--text-muted);
            letter-spacing: 0.06em;
            text-transform: uppercase;
            animation: fadeUp 0.6s 0.6s ease both;
        }

        /* ── ANIMATIONS ────────────────────────────────────────────── */
        @keyframes fadeDown {
            from { opacity: 0; transform: translateY(-20px); }
            to   { opacity: 1; transform: translateY(0); }
        }
        @keyframes fadeUp {
            from { opacity: 0; transform: translateY(20px); }
            to   { opacity: 1; transform: translateY(0); }
        }
        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50%       { opacity: 0.4; }
        }
    </style>
</head>
<body>

    <!-- Fond décoratif -->
    <div class="bg-layer"></div>
    <div class="bg-grid"></div>

    <main class="page">

        <!-- En-tête -->
        <header class="header">
            <div class="header-badge">Back Office · Système de gestion</div>
            <h1>BO<span class="accent-word">location</span></h1>
            <p class="subtitle">Gestion des réservations &amp; véhicules</p>
        </header>

        <!-- Grille de navigation -->
        <nav class="cards-grid">

            <a href="<%= request.getContextPath() %>/reservations/form" class="card">
                <div class="card-icon"><i class="fas fa-pen-fancy"></i></div>
                <div class="card-body">
                    <div class="card-title">Ajouter une réservation</div>
                    <div class="card-desc">Créer une nouvelle réservation client</div>
                </div>
                <span class="card-arrow">→</span>
            </a>

            <a href="<%= request.getContextPath() %>/reservations/liste" class="card">
                <div class="card-icon"><i class="fas fa-list"></i></div>
                <div class="card-body">
                    <div class="card-title">Liste des réservations</div>
                    <div class="card-desc">Consulter et gérer toutes les réservations</div>
                </div>
                <span class="card-arrow">→</span>
            </a>

            <a href="<%= request.getContextPath() %>/planification/form" class="card">
                <div class="card-icon"><i class="fas fa-calendar-check"></i></div>
                <div class="card-body">
                    <div class="card-title">Planification des véhicules</div>
                    <div class="card-desc">Affectation automatique aux réservations</div>
                </div>
                <span class="card-arrow">→</span>
            </a>

            <a href="<%= request.getContextPath() %>/voitures/form" class="card">
                <div class="card-icon"><i class="fas fa-car"></i></div>
                <div class="card-body">
                    <div class="card-title">Gestion des véhicules</div>
                    <div class="card-desc">Ajouter ou gérer le parc automobile</div>
                </div>
                <span class="card-arrow">→</span>
            </a>

        </nav>

        <footer class="footer">BO-location &nbsp;·&nbsp; v2.0</footer>

    </main>

</body>
</html>
