# Atlas of Morocco frontend

Start the existing Spring Boot project and open its root URL. Spring Boot serves the frontend from `src/main/resources/static`. For a frontend-only preview, run `python3 -m http.server 4173 --directory src/main/resources/static` from the project root and open `http://localhost:4173/`. Street tiles need internet access. Country geometry and landmark illustrations are bundled.

## Screens and preview flows

| Entry point | Screens |
| --- | --- |
| `index.html` and `city.html` | 25 destinations, regional and municipal borders, pixel-art landmark previews, arrival transition and flat street walk |
| `tourism.html` | Attractions, accommodation, food, ATMs, culture, transport, healthcare, emergency information, shopping, rental and air travel |
| `education.html` | Universities, colleges, libraries, coaching and training centres |
| `sports.html` | Event and venue details, ticket quantity, reservation review and booking history |
| `jobs.html` | Employer, requirements, deadline, CV selection, cover note and application history |
| `business.html` | Companies, centres, industries, opportunities, news, announcements and enquiry form |
| `account.html` | Sign-in, registration, reset, profile, CV add/edit/delete/file opening, favourites, bookings, applications and sign-out |
| `admin.html` | Preview entry, dashboard, users/roles, cities, listings, venues and time slots, news, enquiry forwarding, statuses and activity log |
| `search.html`, `terms.html`, `privacy.html` | City-wide search and map filters, Terms of Service and Privacy Policy |

Module pages have list, filters and map views. Map views add live named places from OpenStreetMap vector tiles via OpenFreeMap. The Places in view panel refreshes when the map stops moving, follows the active filters and includes only locations in the current map area. Live place records are held in memory for that view. Searches in Casablanca’s list view also look across the full Casablanca commune, including beyond the visible map. Named OpenStreetMap results are filtered by category or search phrase and held in memory while the search page is open. The map uses OpenStreetMap tiles and the opening borders use Morocco Communes data from OpenStreetMap. See the attribution on each map. Boundary source snapshot: https://github.com/zkousama/morocco-communes/tree/4ec363333fea43e712820e20f59532bac5df0b07/data/v1/geometry (ODbL).

The language switch covers the main navigation and common controls in English, French and Arabic, including RTL in Arabic. Sample content and some explanatory copy are in English.

## Frontend integration

The frontend uses browser preview state. Use **Start sample user session** to inspect personal flows and **Open administrator preview** to inspect admin flows. Forms demonstrate validation and state changes. They send no authentication requests, email, payment, bookings, applications, or admin writes. The reservation review leads to a preview booking. CV contents are kept in memory; only metadata is kept in tab storage. Use the Privacy Policy’s **Clear preview data** control to reset the demo.

The original Java, Spring configuration and tests are preserved. `data.js` defines record shapes and sample data. `app.js` contains the route, session, form and map handlers. Replace preview handlers with authenticated calls when the backend rules are ready.

Set the DB_PASSWORD environment variable to your PostgreSQL password before starting Spring Boot. The frontend-only preview does not need a database.
