package com.smartcity.config;

import com.smartcity.service.CurrentResident;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

@Configuration
public class AtlasAccessConfig implements WebMvcConfigurer {

    private static final Set<String> PUBLIC = Set.of("cities","tourism_places","educational_institutions",
            "venues","venue_slots","sport_events","jobs","companies","business_news","places");
    private final CurrentResident resident;
    private final boolean enabled;

    public AtlasAccessConfig(CurrentResident resident, @Value("${app.access.enabled:true}") boolean enabled) {
        this.resident = resident;
        this.enabled = enabled;
    }

    private boolean sameOrigin(HttpServletRequest request) {
        if ("cross-site".equals(request.getHeader("Sec-Fetch-Site"))) return false;
        String header = request.getHeader("Origin");
        if (header == null) return true;
        try {
            URI origin = URI.create(header);
            String scheme = origin.getScheme();
            int port = origin.getPort() < 0 ? ("https".equals(scheme) ? 443 : 80) : origin.getPort();
            return request.getScheme().equalsIgnoreCase(scheme)
                    && request.getServerName().equalsIgnoreCase(origin.getHost())
                    && request.getServerPort() == port;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (!enabled) return;
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String path = UrlPathHelper.defaultInstance.getPathWithinApplication(request);
                boolean read = Set.of("GET","HEAD","OPTIONS").contains(request.getMethod());
                if (!read && !sameOrigin(request)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
                if (path.startsWith("/api/account") || path.equals("/api/atlas-visits")
                        || path.equals("/api/atlas-visits/collection")) return true;
                String resource = path.substring(5).split("/", 2)[0];
                if (read && PUBLIC.contains(resource) && (!resource.equals("business_news") || path.equals("/api/business_news"))) return true;
                var user = resident.require();
                if (path.matches("/api/(favourites(/.*)?|cvs/(mine(/\\d+)?|upload|\\d+/file)|bookings/(mine|reservations|\\d+/cancel)|job_applications/(mine|submissions))")) return true;
                if (user.getAdmin() == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator access required.");
                return true;
            }
        }).addPathPatterns("/api/**");
    }
}
