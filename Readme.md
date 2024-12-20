# **Notes**

## Oauth2 security config.java class 
.authorizeHttpRequests: Configures which HTTP requests require authentication.
.requestMatchers(...): Specifies patterns for URLs that are publicly accessible (e.g., "/", "/login").
.permitAll(): Allows unrestricted access to these URLs.
.anyRequest().authenticated(): Requires authentication for all other requests.

### login 
.oauth2Login: Enables OAuth2 login.
.loginPage("/login"): Specifies the custom login page URL.
.defaultSuccessUrl("/", true): Redirects to "/" after successful login.
.failureUrl("/login?error=true"): Redirects to /login?error=true if login fails.
.permitAll(): Allows everyone to access the login page.

### logout
.logoutUrl("/logout"): Sets the URL to trigger logout.
.logoutSuccessUrl("/"): Redirects to "/" after logout.
.invalidateHttpSession(true): Invalidates the user’s session.
.deleteCookies("JSESSIONID"): Deletes the session cookie.
.permitAll(): Makes the logout functionality accessible to everyone.

### CSRF
.csrf(csrf -> csrf.disable()): Disables CSRF (Cross-Site Request Forgery) protection for simplicity (not recommended for production).
.cors(cors -> cors.disable()): Disables CORS (Cross-Origin Resource Sharing) checks.