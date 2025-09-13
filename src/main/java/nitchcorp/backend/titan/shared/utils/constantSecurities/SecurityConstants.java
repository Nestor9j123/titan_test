package nitchcorp.backend.titan.shared.utils.constantSecurities;

/**
 * Constantes pour la gestion de la sécurité et des rôles dans l'application Titan
 */
public final class SecurityConstants {

    private SecurityConstants() {
    }


    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_TITAN_ADMIN = "TITAN_ADMIN";

    // ==================== EXPRESSIONS PREAUTHORIZE ====================
    
    // Accès pour USER et rôles supérieurs
    public static final String HAS_ROLE_USER_OR_HIGHER = "hasAnyRole('USER', 'ADMIN', 'TITAN_ADMIN')";
    
    // Accès pour tous les utilisateurs authentifiés
    @Deprecated
    public static final String HAS_ANY_ROLE = HAS_ROLE_USER_OR_HIGHER; // Déprécié, utiliser HAS_ROLE_USER_OR_HIGHER
    
    // Accès pour ADMIN et TITAN_ADMIN uniquement
    public static final String HAS_ROLE_ADMIN_OR_HIGHER = "hasAnyRole('ADMIN', 'TITAN_ADMIN')";
    
    // Alias pour compatibilité
    public static final String HAS_ADMIN_ROLE = HAS_ROLE_ADMIN_OR_HIGHER;
    
    // Accès pour TITAN_ADMIN uniquement
    public static final String HAS_ROLE_TITAN_ADMIN = "hasRole('TITAN_ADMIN')";
    public static final String HAS_TITAN_ADMIN_ROLE = HAS_ROLE_TITAN_ADMIN;
    
    // Accès pour OWNER et rôles supérieurs
    public static final String HAS_ROLE_OWNER_OR_HIGHER = "hasAnyRole('OWNER', 'ADMIN', 'TITAN_ADMIN')";
    
    // ==================== EXPRESSIONS SPÉCIFIQUES EVENTS ====================
    
    // Consultation des événements - tous les utilisateurs
    public static final String CAN_VIEW_EVENTS = HAS_ROLE_USER_OR_HIGHER;
    
    // Création/modification d'événements - ADMIN+
    public static final String CAN_MANAGE_EVENTS = HAS_ADMIN_ROLE;
    
    // Achat de tickets - tous les utilisateurs
    public static final String CAN_PURCHASE_TICKETS = HAS_ROLE_USER_OR_HIGHER;
    
    // Gestion des tickets (admin) - ADMIN+
    public static final String CAN_MANAGE_TICKETS = HAS_ADMIN_ROLE;
    
    // Consultation de ses propres tickets - tous les utilisateurs
    public static final String CAN_VIEW_OWN_TICKETS = HAS_ROLE_USER_OR_HIGHER;
    
    // Annulation de ses propres tickets - tous les utilisateurs
    public static final String CAN_CANCEL_OWN_TICKETS = HAS_ROLE_USER_OR_HIGHER;
    
    // Validation des tickets (pour les organisateurs/staff) - ADMIN+
    public static final String CAN_VALIDATE_TICKETS = HAS_ADMIN_ROLE;
    
    // Création de votes - ADMIN+
    public static final String CAN_CREATE_VOTES = HAS_ADMIN_ROLE;
    
    // Participation aux votes - tous les utilisateurs
    public static final String CAN_PARTICIPATE_VOTES = HAS_ROLE_USER_OR_HIGHER;
    
    // Gestion des templates de tickets - ADMIN+
    public static final String CAN_MANAGE_TICKET_TEMPLATES = HAS_ADMIN_ROLE;
    
    // ==================== EXPRESSIONS SPÉCIFIQUES IMMO ====================
    
    // Consultation des propriétés - tous les utilisateurs
    public static final String CAN_VIEW_PROPERTIES = HAS_ROLE_USER_OR_HIGHER;
    
    // Création/modification de propriétés - ADMIN+
    public static final String CAN_MANAGE_PROPERTIES = HAS_ADMIN_ROLE;
    
    // Gestion des agents - ADMIN+
    public static final String CAN_MANAGE_AGENTS = HAS_ADMIN_ROLE;
    
    // Gestion des propriétaires - ADMIN+
    public static final String CAN_MANAGE_OWNERS = HAS_ADMIN_ROLE;
    
    // Gestion des clients - ADMIN+
    public static final String CAN_MANAGE_CUSTOMERS = HAS_ADMIN_ROLE;
    
    // Gestion des visites - tous les utilisateurs peuvent demander, ADMIN+ peuvent gérer
    public static final String CAN_REQUEST_VISITS = HAS_ROLE_USER_OR_HIGHER;
    public static final String CAN_MANAGE_VISITS = HAS_ADMIN_ROLE;
    
    // Gestion des contrats - ADMIN+
    public static final String CAN_MANAGE_CONTRACTS = HAS_ADMIN_ROLE;
    
    // Gestion des paiements - ADMIN+
    public static final String CAN_MANAGE_PAYMENTS = HAS_ADMIN_ROLE;
    
    // ==================== EXPRESSIONS SYSTÈME ====================
    
    // Gestion système complète - TITAN_ADMIN uniquement
    public static final String CAN_MANAGE_SYSTEM = HAS_TITAN_ADMIN_ROLE;
    
    // Accès aux données sensibles - ADMIN+
    public static final String CAN_ACCESS_SENSITIVE_DATA = HAS_ADMIN_ROLE;
}
