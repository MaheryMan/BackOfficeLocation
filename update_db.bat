@echo off
set PGPASSWORD=BfsmL3HGAhKbLHcGOzrdD7z4VNbCizds

echo Mise a jour de la base de donnees...
psql -h dpg-d691dmsr85hc73d4f1ag-a.frankfurt-postgres.render.com -U location -d location_s5 -f "sql/initialisation 6-02-2026/update.sql"

echo Insertion des donnees...
psql -h dpg-d691dmsr85hc73d4f1ag-a.frankfurt-postgres.render.com -U location -d location_s5 -f "sql/initialisation 6-02-2026/data.sql"

echo Termine!
pause