# Diagnostic du code historique

| Défaut | Emplacement | Principe SOLID violé |
|---|---|---|
| Le service principal valide les inscriptions, attribue les logements, gère les baux, envoie les notifications et construit le rapport. | `GestionResidence` | Responsabilité unique (S) |
| Ajouter un type de logement impose de modifier plusieurs conditions de création et de calcul. | `Residence.initialiser`, `GestionResidence.ajouterLogement`, `CalculateurLoyer.calculerLoyer` | Ouvert/fermé (O) |
| Le calculateur et la gestion dépendent de classes concrètes de logements et de services de notification. | `CalculateurLoyer`, `GestionResidence` | Inversion des dépendances (D) |
| Les logements partagent une interface imposant des opérations qu'ils ne prennent pas en charge, parfois via `UnsupportedOperationException`. | `ILogement`, `Studio`, `T1`, `T2`, `Colocation` | Substitution de Liskov (L) |
| L'interface de logement regroupe les opérations de colocation, de balcon et d'entretien, inutiles à la plupart des appelants. | `ILogement` | Ségrégation des interfaces (I) |

Au palier 1, les codes de profils, canaux, types de logement, statuts et options sont représentés par des enums dans le module refactoré. Les valeurs métier utilisées pour les calculs et les validations sont nommées dans les classes qui les appliquent. Au palier 3, les règles liées aux profils sont regroupées dans `StrategieProfil` et celles liées aux types de logement dans `StrategieLoyerType`. Au palier 4, les couches du loyer sont représentées par des décorateurs et assemblées dans `AssemblageLoyer`.

Au palier 5, `GestionResidence` délègue à des services spécialisés, les accès aux données passent par des interfaces de dépôt et le graphe de dépendances est construit dans `ContexteResidence`. Les états précédemment partagés sont désormais liés aux instances, et la génération des quittances est confiée à `GenerateurQuittance`.
