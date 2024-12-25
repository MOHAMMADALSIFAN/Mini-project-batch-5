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


## Project Routes and Requirements

### Controller Routes

| HTTP Method | URL | Description |
|------------|-----|-------------|
| GET | `/` | Home page with user authentication |
| GET | `/login` | OAuth2 login with Google or GitHub |
| GET | `/search` | Recipe search page with random 10 recipes |
| GET | `/search/results` | Search results by category, area, or first letter |
| GET | `/meal/{id}` | Detailed meal page |
| GET | `/favorites` | User's favorite meals page |
| POST | `/favorites/add` | Add meal to user's favorites |
| POST | `/favorites/remove` | Remove meal from user's favorites |
| GET | `/random` | Random meal details page |
| GET | `/profile` | User profile page |
| POST | `/profile/update` | Update user profile |

### REST Controller Routes (API)

| HTTP Method | URL | Description |
|------------|-----|-------------|
| GET | `/api/meals/search` | Search meals by query parameter | ` example : /api/meals/search?query=chicken`
| GET | `/api/meals/{id}` | Get specific meal by ID | ` example : /api/meals/53065`
| GET | `/api/meals/random` | Get a random meal |

### Project Requirements Checklist

| Requirement | Status |
|------------|--------|
| OAuth2 Authentication | ✅ Completed |
| Multiple Search Methods | ✅ Completed |
| Random Recipe Generation | ✅ Completed |
| User Favorites Functionality | ✅ Completed |
| External Meal API Integration | ✅ Completed |
| User Profile Management | ✅ Completed |
| Responsive Design | ✅ Completed |
| Redis Database | ✅ Completed |
| Multiple Views | ✅ Completed |
| RESTful API Endpoints | ✅ Completed |

### Key Features
- Google/GitHub OAuth2 Authentication
- Comprehensive recipe search
- Favorites management
- User profile customization
- Random recipe discovery
- Responsive design
- Redis-backed data storage