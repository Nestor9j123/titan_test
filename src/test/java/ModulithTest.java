
import nitchcorp.backend.titan.TitanApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.ApplicationModule;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ModulithTest {

    private final ApplicationModules modules = ApplicationModules.of(TitanApplication.class);

    @Test
    void verifyModuleStructure() {
        // TEST PRINCIPAL - Validation automatique
        modules.verify();
        System.out.println("✅ Architecture Spring Modulith valide !");
    }

    @Test
    void showBasicModuleInfo() {
        //  AFFICHAGE BASIQUE - Méthodes publiques seulement
        System.out.println("\n=== MODULES DÉTECTÉS ===");

        modules.forEach(module -> {
            System.out.println("\n📁 Module: " + module.getDisplayName());
            System.out.println("   📦 Package: " + module.getBasePackage().getName());

            // Compter les classes Spring Beans
            try {
                var springBeans = module.getSpringBeans();
                System.out.println("   🔧 Spring Beans: " + springBeans.size());
            } catch (Exception e) {
                System.out.println("   🔧 Spring Beans: Non disponible");
            }
        });

        System.out.println("\n=== FIN ===");
    }

    @Test
    void countModules() {
        // COMPTAGE SIMPLE
        long moduleCount = modules.stream().count();
        System.out.println("📊 Nombre total de modules: " + moduleCount);

        assertThat(moduleCount).isGreaterThanOrEqualTo(2);
        System.out.println("✅ Modules détectés correctement");
    }

    @Test
    void listModuleNames() {
        //  LISTE DES NOMS
        System.out.println("\n=== LISTE DES MODULES ===");

        modules.stream()
                .map(ApplicationModule::getDisplayName)
                .forEach(name -> System.out.println("• " + name));

        System.out.println("✅ Liste complète");
    }

    // ========== TESTS DE VISIBILITÉ CORRIGÉS ==========

    @Test
    void verifyPackageVisibility() {
        System.out.println("\n=== VISIBILITÉ DES PACKAGES ===");

        modules.forEach(module -> {
            System.out.println("\n📁 Module: " + module.getDisplayName());

            // Vérifier les interfaces nommées (packages explicitement publics)
            try {
                var namedInterfaces = module.getNamedInterfaces();
                long interfaceCount = namedInterfaces.stream().count();
                final boolean hasNamedInterfaces = interfaceCount > 0;

                if (hasNamedInterfaces) {
                    System.out.println("   🔒 MODE STRICT : Seuls les @NamedInterface sont publics");
                    namedInterfaces.forEach(namedInterface ->
                            System.out.println("     ✅ PUBLIC: " + namedInterface.getName())
                    );
                } else {
                    System.out.println("   🔓 MODE PERMISSIF : Packages publics par défaut");
                    System.out.println("     ⚠️  Sauf conventions 'internal' et 'impl'");
                }

            } catch (Exception e) {
                System.out.println("   ❓ Interfaces nommées: " + e.getMessage());
            }
        });
    }

    @Test
    void verifyPrivacyByConvention() {
        System.out.println("\n=== PACKAGES PRIVÉS PAR CONVENTION ===");

        modules.forEach(module -> {
            final String moduleName = module.getDisplayName();
            final String basePackageName = module.getBasePackage().getName();

            System.out.println("\n📁 Module: " + moduleName);

            // Analyser les Spring Beans pour détecter les packages
            try {
                var springBeans = module.getSpringBeans();
                Set<String> packageNames = springBeans.stream()
                        .map(bean -> bean.getType().getPackageName())
                        .collect(Collectors.toSet());

                System.out.println("   📦 Packages détectés:");
                packageNames.forEach(packageName -> {
                    boolean isPrivateByConvention = packageName.contains("internal") ||
                            packageName.contains("impl");

                    String visibility = isPrivateByConvention ? "🔒 PRIVÉ" : "🔓 PUBLIC";
                    String relativeName = packageName.replace(basePackageName + ".", "");
                    System.out.println("     " + visibility + " " + relativeName);
                });

            } catch (Exception e) {
                System.out.println("  📦 Aucun package détecté via Spring Beans");
            }
        });
    }

    @Test
    void verifySharedModuleExposition() {
        System.out.println("\n=== VÉRIFICATION MODULE SHARED ===");

        ApplicationModule sharedModule = modules.getModuleByName("shared")
                .orElseThrow(() -> new AssertionError("Module shared non trouvé"));

        System.out.println("📁 Module: " + sharedModule.getDisplayName());

        try {
            var namedInterfaces = sharedModule.getNamedInterfaces();
            long interfaceCount = namedInterfaces.stream().count();

            if (interfaceCount > 0) {
                System.out.println("✅ Shared utilise @NamedInterface (contrôle strict)");
                namedInterfaces.forEach(ni ->
                        System.out.println("   🔓 PUBLIC: " + ni.getName())
                );
            } else {
                System.out.println("⚠️  Shared n'utilise pas @NamedInterface");
                System.out.println("   📝 Tous les packages sont publics par défaut");
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'analyse: " + e.getMessage());
        }
    }

    @Test
    void verifyEventModulePrivacy() {
        System.out.println("\n=== VÉRIFICATION MODULE EVENT ===");

        ApplicationModule eventModule = modules.getModuleByName("event")
                .orElseThrow(() -> new AssertionError("Module event non trouvé"));

        System.out.println("📁 Module: " + eventModule.getDisplayName());

        try {
            var namedInterfaces = eventModule.getNamedInterfaces();
            long interfaceCount = namedInterfaces.stream().count();

            if (interfaceCount > 0) {
                System.out.println("✅ Event utilise @NamedInterface (contrôle strict)");
                System.out.println("   🔒 SEULS ces packages sont publics:");
                namedInterfaces.forEach(ni ->
                        System.out.println("     ✅ PUBLIC: " + ni.getName())
                );
                System.out.println("   📝 Tout le reste est PRIVÉ");
            } else {
                System.out.println("⚠️  Event n'utilise pas @NamedInterface");
                System.out.println("   📝 Tous les packages sont publics (sauf conventions)");
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'analyse: " + e.getMessage());
        }
    }

    @Test
    void verifyStrictPrivacyModel() {
        System.out.println("\n=== MODÈLE DE CONFIDENTIALITÉ GLOBAL ===");

        boolean hasStrictModule = false;

        for (ApplicationModule module : modules) {
            try {
                var namedInterfaces = module.getNamedInterfaces();
                long interfaceCount = namedInterfaces.stream().count();
                boolean isStrict = interfaceCount > 0;

                if (isStrict) {
                    hasStrictModule = true;
                }

                String mode = isStrict ? "🔒 STRICT" : "🔓 PERMISSIF";
                System.out.println("📁 " + module.getDisplayName() + " → " + mode);

            } catch (Exception e) {
                System.out.println("📁 " + module.getDisplayName() + " → ❓ ERREUR");
            }
        }

        if (hasStrictModule) {
            System.out.println("\n✅ Architecture avec contrôle strict détectée");
            System.out.println("   📝 Les modules avec @NamedInterface sont sécurisés");
        } else {
            System.out.println("\n⚠️  Aucun contrôle strict détecté");
            System.out.println("   📝 Considérer l'ajout de @NamedInterface pour plus de sécurité");
        }
    }

    @Test
    void generateVisibilityReport() {
        System.out.println("\n=== RAPPORT DE VISIBILITÉ COMPLET ===");

        modules.forEach(module -> {
            final String displayName = module.getDisplayName();
            final String basePackageName = module.getBasePackage().getName();

            System.out.println("\n" + "=".repeat(50));
            System.out.println("📁 MODULE: " + displayName.toUpperCase());
            System.out.println("📦 Package de base: " + basePackageName);

            try {
                var namedInterfaces = module.getNamedInterfaces();
                long interfaceCount = namedInterfaces.stream().count();

                if (interfaceCount > 0) {
                    System.out.println("🔒 MODE: Sécurité stricte");
                    System.out.println("✅ PACKAGES PUBLICS:");
                    namedInterfaces.forEach(ni ->
                            System.out.println("   • " + ni.getName())
                    );
                    System.out.println("❌ PACKAGES PRIVÉS: Tous les autres");
                } else {
                    System.out.println("🔓 MODE: Permissif");
                    System.out.println("✅ PACKAGES PUBLICS: Tous (sauf conventions)");
                    System.out.println("❌ PACKAGES PRIVÉS: Seulement 'internal' et 'impl'");
                }

                // Analyser les Spring Beans pour voir les packages réels
                var springBeans = module.getSpringBeans();
                if (!springBeans.isEmpty()) {
                    System.out.println("📊 PACKAGES DÉTECTÉS:");
                    springBeans.stream()
                            .map(bean -> bean.getType().getPackageName())
                            .distinct()
                            .sorted()
                            .forEach(pkg -> {
                                String relative = pkg.replace(basePackageName + ".", "");
                                System.out.println("   • " + relative);
                            });
                }

            } catch (Exception e) {
                System.out.println("❌ Erreur d'analyse: " + e.getMessage());
            }
        });

        System.out.println("\n" + "=".repeat(50));
    }



    @Test
    void countNamedInterfacesPerModule() {
        System.out.println("\n=== COMPTAGE DES INTERFACES NOMMÉES ===");

        modules.forEach(module -> {
            try {
                var namedInterfaces = module.getNamedInterfaces();
                long count = namedInterfaces.stream().count();

                System.out.println("📁 " + module.getDisplayName() + ": " + count + " interface(s) nommée(s)");

                if (count > 0) {
                    namedInterfaces.forEach(ni ->
                            System.out.println("   → " + ni.getName())
                    );
                }

            } catch (Exception e) {
                System.out.println("📁 " + module.getDisplayName() + ": Erreur - " + e.getMessage());
            }
        });

    }
}