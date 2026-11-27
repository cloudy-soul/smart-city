# Atlas of Morocco frontend

Follow [the PostgreSQL setup](README.md#run-locally) and open the Spring Boot URL. Directory entries come from PostgreSQL. OpenStreetMap places and tiles remain live and read-only. The landing map also uses a bundled geographic snapshot with attribution.

| Entry point | Connected flow |
| --- | --- |
| Tourism and Education | Search and filter real directory entries or explore live OpenStreetMap results |
| Sport events | View scheduled events, reserve available tickets, and cancel from **My bookings** |
| Jobs | View posted requirements and deadlines, and apply using an uploaded CV |
| Business | View companies and their published announcements |
| Account | Register or sign in, manage CVs, and see your favourites, reservations, and applications |
| Administration | Manage catalogue entries and venue slots, review applications, publish news, and view audit history and aggregate analytics |

Record lists offer 15 or 30 rows per page. Search and filters apply before pagination. The page-size selection is remembered for each list in the current tab.

A PostgreSQL-backed account is required for personal workflows. Select **Student account** during registration if you want to manage CVs and apply. An application keeps its original PDF. See the setup guide for granting Admin access to a registered account.

Open **Account → Profile** to edit your contact details, turn student status on or off, or change your password. Changing your email or password requires your current password and signs out other sessions. Turning off student status keeps your CVs and applications and pauses CV access and new applications until you turn it back on.

On a first visit, choose **Essential only**, **Accept all**, or **Customize**. The footer reopens **Cookie preferences**. Language can be set for the current tab. The optional language and personalization cookies remember your chosen language, city, and category between visits.

**Administration → Analytics** shows aggregate counts from opted-in visits when collection is enabled. The dashboard starts empty. Use its inclusive UTC date range to filter totals and breakdowns. The collector records no visitor identifiers or geolocation, so the location group is unknown for these visits.
