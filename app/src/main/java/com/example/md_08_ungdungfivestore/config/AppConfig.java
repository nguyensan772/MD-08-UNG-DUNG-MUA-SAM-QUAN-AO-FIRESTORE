package com.example.md_08_ungdungfivestore.config;

/**
 * Centralized configuration for the application.
 * All BASE_URL constants are defined here for easy maintenance.
 */
public class AppConfig {

    // Base URL without trailing slash (for image URLs and path concatenation)
    public static final String BASE_URL = "https://tinisha-nonwashable-castiel.ngrok-free.dev";

    // Base URL with trailing slash (for Retrofit API clients)
    public static final String API_BASE_URL = BASE_URL + "/";

    // Auth API base URL (for login/register endpoints)
    public static final String AUTH_BASE_URL = BASE_URL + "/api/auth/";

    // Private constructor to prevent instantiation
    private AppConfig() {}
}

