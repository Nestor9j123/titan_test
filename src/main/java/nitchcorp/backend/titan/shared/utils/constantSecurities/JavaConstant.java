package nitchcorp.backend.titan.shared.utils.constantSecurities;

import org.springframework.beans.factory.annotation.Value;

public class JavaConstant {
    private static String frontendServerURL;

    @Value("${client.address}")
    public void setFrontendServerURL(String frontendServerURL) {
        JavaConstant.frontendServerURL = frontendServerURL;
    }

    public static String getFrontendServerURL() {
        return frontendServerURL;
    }

    public final static String API_BASE_URL = "/api";
    public final static String FRONTEND_URL = "*";

    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token can not be verified";
    public static final String GET_ARRAYS_LLC = "Get arrays, LLC";
    public static final String GET_ARRAYS_ADMINISTRATION = "User management portal";
    public static final String AUTHORITIES = "Authorities";
    public static final String FORBIDDEN_MESSAGE = "Vous devez vous connecter pour accéder à cette page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";

    // URLs publiques (sans authentification)
    public static final String[] PUBLIC_URLS = {
            API_BASE_URL + "/user/register",
            API_BASE_URL + "/user/login",
            API_BASE_URL + "/test/qrcode/**",
            API_BASE_URL + "/user/etat/**",
            API_BASE_URL + "/demandeur/valide/**",  // De URL_FOR_VALIDATE_ACCOUNTS
            API_BASE_URL + "/offreur/valide/**",    // De URL_FOR_VALIDATE_ACCOUNTS
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/swagger-ui.html"
    };

    // URLs pour utilisateurs authentifiés (ROLE_USER ou supérieur : consultation, achat, participation)
    public static final String[] AUTHENTICATED_URLS = {
            API_BASE_URL + "/events/view/**",  // Consultation des événements
            API_BASE_URL + "/votes/participate/**",  // Participation aux votes
            API_BASE_URL + "/purchased_ticket/purchase/**",  // Achat de tickets
            API_BASE_URL + "/purchased_ticket/own/**",  // Consultation des tickets personnels
            API_BASE_URL + "/purchased_ticket/cancel/**",  // Annulation des tickets personnels
            API_BASE_URL + "/visits/request/**",  // Demande de visites
            API_BASE_URL + "/properties/view/**",  // Consultation des propriétés
            API_BASE_URL + "/notifications/**",  // Notifications (assumées pour users)
            API_BASE_URL + "/restaurants/**",  // Restaurants (assumés pour users)
            API_BASE_URL + "/plats/**",  // Plats
            API_BASE_URL + "/platcommandes/**",  // Plat commandes
            API_BASE_URL + "/optionPersonaliser/**",  // Options personnalisées
            API_BASE_URL + "/commandes/**",  // Commandes
            API_BASE_URL + "/consumer/restaurants/**",
            API_BASE_URL + "/delivery-orders/**",
            API_BASE_URL + "/delivery-companies/**",
            API_BASE_URL + "/delivery-persons/**"
    };

    // URLs pour ADMIN ou supérieur (gestion : création, modification, validation)
    public static final String[] ADMIN_URLS = {
            API_BASE_URL + "/events/manage/**",  // Création/modification d'événements (ex. /events/manage/create, /events/manage/update/**)
            API_BASE_URL + "/votes/manage/**",  // Création/gestion de votes
            API_BASE_URL + "/ticket_template/**",  // Gestion des templates de tickets
            API_BASE_URL + "/purchased_ticket/manage/**",  // Gestion des tickets
            API_BASE_URL + "/purchased_ticket/validate/**",  // Validation des tickets
            API_BASE_URL + "/visits/manage/**",  // Gestion des visites
            API_BASE_URL + "/properties/manage/**",  // Création/modification de propriétés
            API_BASE_URL + "/paiements/**",  // Gestion des paiements
            API_BASE_URL + "/owners/**",  // Gestion des propriétaires
            API_BASE_URL + "/owner-agent-assignments/**",  // Assignations owner-agent
            API_BASE_URL + "/lease-contracts/**",  // Contrats de location
            API_BASE_URL + "/customers/**",  // Gestion des clients
            API_BASE_URL + "/agents/**",  // Gestion des agents

    };

    // URLs pour TITAN_ADMIN uniquement (gestion système, données sensibles)
    public static final String[] TITAN_ADMIN_URLS = {
            API_BASE_URL + "/system/**",  // Gestion système complète
            API_BASE_URL + "/sensitive/**"  // Accès aux données sensibles
    };
}