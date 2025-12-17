Afin de lancer une partie d'une stratégie individuelle il faut ouvrir la clase MMMain et dans la ligne 
Strategie strat = new "" ; écrire la strategie souhaitée
(Soit StAleatoire() , StExpectimax(), StGreedy(), StMiniMax(), StMonotonicite(), StMonteCarlo()) 
i.e. Strategie strat = new StMonteCarlo() ;
Le reste du code se laisse tel qu'il est
Appuyer sur run et la partie se jouera automatiquement, le progrès sera affiché sur la console 

Afin de lancer un test simultané utiliser la classe benchmark et modifier sur la ligne 297 
le nombre de parties souhaitées 
final int NB_PARTIES = 1000;
P.S Chaque partie de MonteCarlo prend environ 1min (ca dépend de l'ordinateur) alors c'est important de rester sur des 
comparaisons raisonables 
